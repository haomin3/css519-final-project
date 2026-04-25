// Session state used for authenticated requests
let sessionToken = "";
let currentUser = "";

// Main display elements
const serverStatusEl = document.getElementById("serverStatus");
const currentUserEl = document.getElementById("currentUser");
const tokenStateEl = document.getElementById("tokenState");
const filesTableBody = document.getElementById("filesTableBody");
const messageBox = document.getElementById("messageBox");
const downloadResultEl = document.getElementById("downloadResult");
const loginToggleBtn = document.getElementById("loginToggleBtn");
const uploadToggleBtn = document.getElementById("uploadToggleBtn");
const downloadToggleBtn = document.getElementById("downloadToggleBtn");
const refreshBtn = document.getElementById("refreshBtn");

// Form inputs
const usernameInput = document.getElementById("usernameInput");
const passwordInput = document.getElementById("passwordInput");
const uploadFileInput = document.getElementById("uploadFileInput");
const uploadNameInput = document.getElementById("uploadNameInput");
const uploadContentInput = document.getElementById("uploadContentInput");
const downloadNameInput = document.getElementById("downloadNameInput");

// Collapse sections
const loginSectionEl = document.getElementById("loginSection");
const uploadSectionEl = document.getElementById("uploadSection");
const downloadSectionEl = document.getElementById("downloadSection");

const loginCollapse = new bootstrap.Collapse(loginSectionEl, { toggle: false });
const uploadCollapse = new bootstrap.Collapse(uploadSectionEl, { toggle: false });
const downloadCollapse = new bootstrap.Collapse(downloadSectionEl, { toggle: false });

// Button bindings
document.getElementById("loginBtn").addEventListener("click", login);
document.getElementById("refreshBtn").addEventListener("click", refreshFiles);
document.getElementById("uploadBtn").addEventListener("click", uploadFile);
document.getElementById("downloadBtn").addEventListener("click", downloadFile);
uploadFileInput.addEventListener("change", handleSelectedFile);

// Initial page setup
window.addEventListener("load", async () => {
    await checkHealth();
    setAuthenticatedUi(false);
    showMessage("Login is required first", "warning");
});

// Keep only one sidebar section open at a time
uploadToggleBtn.addEventListener("click", () => {
    loginCollapse.hide();
    downloadCollapse.hide();
});

downloadToggleBtn.addEventListener("click", () => {
    loginCollapse.hide();
    uploadCollapse.hide();
});

loginToggleBtn.addEventListener("click", () => {
    uploadCollapse.hide();
    downloadCollapse.hide();
});

// Checks whether the backend is reachable
async function checkHealth() {
    try {
        const response = await fetch("/health");
        const data = await response.json();

        if (response.ok && data.success) {
            setServerStatus("Online", "good");
        } else {
            setServerStatus("Error", "bad");
        }
    } catch (error) {
        setServerStatus("Offline", "bad");
        showMessage("Could not reach server", "danger");
    }
}

// Sends login request and collapses the login form on success
async function login() {
    const username = usernameInput.value.trim();
    const password = passwordInput.value;

    if (!username || !password) {
        showMessage("Username and password are required", "warning");
        return;
    }

    try {
        const response = await fetch("/auth", {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify({ username, password })
        });

        const data = await response.json();

        if (response.ok && data.success) {
            sessionToken = data.sessionToken;
            currentUser = username;

            currentUserEl.textContent = currentUser;
            setTokenStatus("Present", "good");
            loginToggleBtn.textContent = "Logged In";
            loginToggleBtn.classList.remove("btn-primary");
            loginToggleBtn.classList.add("btn-success");
            loginToggleBtn.disabled = true;

            passwordInput.value = "";

            showMessage("Login succeeded", "success");
            loginCollapse.hide();
            setAuthenticatedUi(true);
            await refreshFiles();
        } else {
            showMessage(`Login failed: ${formatError(data.error)}`, "danger");
        }
    } catch (error) {
        showMessage("Login request failed", "danger");
    }
}

// Requests the file list from the backend
async function refreshFiles() {
    if (!sessionToken) {
        showMessage("Login is required first", "warning");
        return;
    }

    try {
        const response = await fetch("/files", {
            headers: {
                "Session-Token": sessionToken
            }
        });

        const data = await response.json();

        if (response.ok && data.success) {
            renderFiles(data.files || []);
            showMessage("File list refreshed", "success");
        } else {
            showMessage(`Could not load files: ${formatError(data.error)}`, "danger");
        }
    } catch (error) {
        showMessage("File refresh failed", "danger");
    }
}

// Renders file metadata into the table
function renderFiles(files) {
    if (!files.length) {
        filesTableBody.innerHTML = `
            <tr>
                <td colspan="4">No files found</td>
            </tr>
        `;
        return;
    }

    filesTableBody.innerHTML = files
        .map(file => `
            <tr>
                <td>${escapeHtml(file.name)}</td>
                <td>${escapeHtml(formatSize(file.size))}</td>
                <td>${escapeHtml(file.uploadedAt ?? "")}</td>
                <td>${escapeHtml(file.uploadedBy ?? "")}</td>
            </tr>
        `)
        .join("");
}

// Loads a selected local text file into the upload fields
function handleSelectedFile(event) {
    const file = event.target.files[0];
    if (!file) {
        return;
    }

    uploadNameInput.value = file.name;
    showMessage(`Selected local file: ${file.name}`, "info");

    const reader = new FileReader();

    reader.onload = function () {
        uploadContentInput.value = typeof reader.result === "string" ? reader.result : "";
    };

    reader.onerror = function () {
        uploadContentInput.value = "";
        showMessage("Could not read selected file", "danger");
    };

    reader.readAsText(file);
}

// Uploads either typed content or selected-file content
async function uploadFile() {
    if (!sessionToken) {
        showMessage("Login is required first", "warning");
        return;
    }

    const name = uploadNameInput.value.trim();
    const content = uploadContentInput.value;
    const pickedFile = uploadFileInput.files[0];

    if (!name) {
        showMessage("File name is required", "warning");
        return;
    }

    if (!content.trim()) {
        if (pickedFile) {
            showMessage("Selected file is empty or could not be read", "warning");
        } else {
            showMessage("Type content or choose a local file first", "warning");
        }
        return;
    }

    try {
        const response = await fetch("/upload", {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
                "Session-Token": sessionToken
            },
            body: JSON.stringify({ name, content })
        });

        const data = await response.json();

        if (response.ok && data.success) {
            showMessage(`Uploaded ${data.name}`, "success");

            uploadNameInput.value = "";
            uploadContentInput.value = "";
            uploadFileInput.value = "";
            uploadCollapse.hide();

            await refreshFiles();
        } else {
            showMessage(`Upload failed: ${formatError(data.error)}`, "danger");
        }
    } catch (error) {
        showMessage("Upload request failed", "danger");
    }
}

// Downloads a file and shows its contents in the main panel
async function downloadFile() {
    if (!sessionToken) {
        showMessage("Login is required first", "warning");
        return;
    }

    const name = downloadNameInput.value.trim();

    if (!name) {
        showMessage("A file name is required for download", "warning");
        return;
    }

    try {
        const response = await fetch("/download", {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
                "Session-Token": sessionToken
            },
            body: JSON.stringify({ name })
        });

        const data = await response.json();

        if (response.ok && data.success) {
            downloadResultEl.textContent = data.content;
            showMessage(`Downloaded ${data.name}`, "success");

            downloadCollapse.hide();
        } else {
            downloadResultEl.textContent = "";
            showMessage(`Download failed: ${formatError(data.error)}`, "danger");
        }
    } catch (error) {
        showMessage("Download request failed", "danger");
    }
}

// Shows a Bootstrap alert message
function showMessage(message, type) {
    messageBox.innerHTML = `
        <div class="alert alert-${type}" role="alert">
            ${escapeHtml(message)}
        </div>
    `;
}

// Converts backend error codes into friendlier text
function formatError(errorCode) {
    if (!errorCode) {
        return "unknown_error";
    }

    return errorCode.replaceAll("_", " ");
}

function setServerStatus(text, type) {
    serverStatusEl.textContent = text;
    serverStatusEl.className = getStatusClass(type);
}

function setTokenStatus(text, type) {
    tokenStateEl.textContent = text;
    tokenStateEl.className = getStatusClass(type);
}

function getStatusClass(type) {
    if (type === "good") return "status-good";
    if (type === "bad") return "status-bad";
    return "status-neutral";
}

// Escapes text before inserting into HTML
function escapeHtml(value) {
    return String(value)
        .replaceAll("&", "&amp;")
        .replaceAll("<", "&lt;")
        .replaceAll(">", "&gt;")
        .replaceAll('"', "&quot;")
        .replaceAll("'", "&#039;");
}

// Formats file size
function formatSize(size) {
    const bytes = Number(size) || 0;

    if (bytes < 1024) return `${bytes} B`;
    if (bytes < 1024 * 1024) return `${(bytes / 1024).toFixed(1)} KB`;

    return `${(bytes / (1024 * 1024)).toFixed(1)} MB`;
}

// Enables or disables authenticated sidebar actions
function setAuthenticatedUi(isAuthenticated) {
    refreshBtn.disabled = !isAuthenticated;
    uploadToggleBtn.disabled = !isAuthenticated;
    downloadToggleBtn.disabled = !isAuthenticated;
}