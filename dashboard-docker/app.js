let authChartInstance = null;
let errorChartInstance = null;
let cachedEvents = [];

async function loadDashboard() {
    const summary = await window.DashboardData.getSummaryData();
    const customerRiskMetrics = await window.DashboardData.getCustomerRiskMetrics();
    cachedEvents = await window.DashboardData.getEventData();
    
    renderSummary(summary);
    renderCustomerRiskTable(customerRiskMetrics);
    await refreshAuthChart();
    await refreshErrorChart();
    renderEventTable(cachedEvents);
    setupSearch(cachedEvents);
    setupChartRangeSelectors();
}

function renderSummary(summary) {
    document.getElementById("lastUpdated").textContent = summary.lastUpdated;

    const healthEl = document.getElementById("healthStatus");
    healthEl.textContent = summary.healthStatus;
    healthEl.classList.remove("status-healthy", "status-warning", "status-down");

    const healthText = summary.healthStatus.toLowerCase();
    if (healthText === "healthy" || healthText === "ok") {
        healthEl.classList.add("status-healthy");
    } else if (healthText === "unstable" || healthText === "warning") {
        healthEl.classList.add("status-warning");
    } else {
        healthEl.classList.add("status-down");
    }

    document.getElementById("healthSubtext").textContent = summary.healthSubtext;
    document.getElementById("avgResponseTime").textContent = summary.avgResponseTime;
    document.getElementById("storageUsed").textContent = summary.storageUsed;
    document.getElementById("storageSubtext").textContent = summary.storageSubtext;
    document.getElementById("activeSessions").textContent = summary.activeSessions;
    document.getElementById("blockedUsers").textContent = summary.blockedUsers;
}

async function refreshAuthChart() {
    const range = document.getElementById("authRange").value;
    const data = await window.DashboardData.getAuthChartData(range);
    renderAuthChart(data);
}

async function refreshErrorChart() {
    const range = document.getElementById("errorRange").value;
    const data = await window.DashboardData.getErrorChartData(range);
    renderErrorChart(data);
}

function renderAuthChart(data) {
    const ctx = document.getElementById("authChart");

    if (authChartInstance) {
        authChartInstance.destroy();
    }

    authChartInstance = new Chart(ctx, {
        type: "line",
        data: {
            labels: data.labels,
            datasets: [
                {
                    label: "Success",
                    data: data.success,
                    borderWidth: 2,
                    tension: 0.3
                },
                {
                    label: "Failure",
                    data: data.failure,
                    borderWidth: 2,
                    tension: 0.3
                }
            ]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            scales: {
                y: {
                    beginAtZero: true,
                    ticks: {
                        precision: 0
                    }
                }
            }
        }
    });
}

function renderErrorChart(data) {
    const ctx = document.getElementById("errorChart");

    if (errorChartInstance) {
        errorChartInstance.destroy();
    }

    errorChartInstance = new Chart(ctx, {
        type: "bar",
        data: {
            labels: data.labels,
            datasets: [
                {
                    label: "Invalid JSON",
                    data: data.invalidJson,
                    borderWidth: 1
                },
                {
                    label: "Unauthorized",
                    data: data.unauthorized,
                    borderWidth: 1
                },
                {
                    label: "Blocked",
                    data: data.blocked,
                    borderWidth: 1
                },
                {
                    label: "File Not Found",
                    data: data.fileNotFound,
                    borderWidth: 1
                },
                {
                    label: "Method Not Allowed",
                    data: data.methodNotAllowed,
                    borderWidth: 1
                }
            ]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            scales: {
                x: {
                    stacked: true
                },
                y: {
                    stacked: true,
                    beginAtZero: true,
                    ticks: {
                        precision: 0
                    }
                }
            }
        }
    });
}

function setupChartRangeSelectors() {
    document.getElementById("authRange").addEventListener("change", refreshAuthChart);
    document.getElementById("errorRange").addEventListener("change", refreshErrorChart);
}

function renderEventTable(events) {
    const tbody = document.getElementById("eventTableBody");
    tbody.innerHTML = "";

    events.forEach(event => {
        const row = document.createElement("tr");

        row.innerHTML = `
            <td>${event.time}</td>
            <td>${buildSeverityBadge(event.severity)}</td>
            <td>${event.source}</td>
            <td>${event.event}</td>
            <td>${event.details}</td>
        `;

        tbody.appendChild(row);
    });
}

function buildSeverityBadge(severity) {
    const normalized = severity.toUpperCase();

    if (normalized === "INFO") {
        return `<span class="severity-badge severity-info">INFO</span>`;
    }
    if (normalized === "WARN") {
        return `<span class="severity-badge severity-warn">WARN</span>`;
    }
    return `<span class="severity-badge severity-error">ERROR</span>`;
}

function renderCustomerRiskTable(metrics) {
    const tbody = document.getElementById("customerRiskTableBody");
    tbody.innerHTML = "";

    metrics.forEach(metric => {
        const row = document.createElement("tr");

        row.innerHTML = `
            <td>${metric.riskArea}</td>
            <td>${buildAlertStatusBadge(metric.status)}</td>
            <td>${metric.metric}</td>
            <td>${metric.currentValue}</td>
            <td>${metric.alertRule}</td>
        `;

        tbody.appendChild(row);
    });
}

function buildAlertStatusBadge(status) {
    const normalized = status.toUpperCase();

    if (normalized === "OK") {
        return `<span class="alert-badge alert-ok">OK</span>`;
    }
    if (normalized === "WARN") {
        return `<span class="alert-badge alert-warn">WARN</span>`;
    }
    return `<span class="alert-badge alert-critical">CRITICAL</span>`;
}

function setupSearch(events) {
    const searchInput = document.getElementById("eventSearch");

    searchInput.addEventListener("input", function () {
        const query = this.value.trim().toLowerCase();

        if (!query) {
            renderEventTable(events);
            return;
        }

        const filtered = events.filter(event =>
            event.time.toLowerCase().includes(query) ||
            event.severity.toLowerCase().includes(query) ||
            event.source.toLowerCase().includes(query) ||
            event.event.toLowerCase().includes(query) ||
            event.details.toLowerCase().includes(query)
        );

        renderEventTable(filtered);
    });
}

loadDashboard();