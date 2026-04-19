const AUTH_CHART_DATA = {
    hourly: {
        labels: ["10:00", "10:05", "10:10", "10:15", "10:20", "10:25"],
        success: [4, 5, 6, 4, 5, 3],
        failure: [1, 2, 2, 4, 3, 5]
    },
    daily: {
        labels: ["Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"],
        success: [32, 28, 35, 30, 40, 22, 18],
        failure: [6, 5, 7, 8, 10, 4, 3]
    },
    weekly: {
        labels: ["Week 1", "Week 2", "Week 3", "Week 4"],
        success: [180, 210, 195, 225],
        failure: [28, 35, 31, 40]
    }
};

const ERROR_CHART_DATA = {
    hourly: {
        labels: ["10:00", "10:05", "10:10", "10:15", "10:20", "10:25"],
        invalidJson: [0, 1, 0, 1, 0, 0],
        unauthorized: [1, 1, 2, 1, 1, 2],
        blocked: [0, 0, 1, 1, 1, 1],
        fileNotFound: [0, 0, 1, 0, 1, 0],
        methodNotAllowed: [0, 1, 0, 0, 0, 1]
    },
    daily: {
        labels: ["Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"],
        invalidJson: [2, 1, 3, 2, 2, 1, 1],
        unauthorized: [5, 6, 7, 5, 8, 4, 3],
        blocked: [1, 1, 2, 2, 3, 1, 1],
        fileNotFound: [1, 2, 1, 2, 2, 1, 1],
        methodNotAllowed: [1, 1, 1, 0, 2, 1, 0]
    },
    weekly: {
        labels: ["Week 1", "Week 2", "Week 3", "Week 4"],
        invalidJson: [8, 10, 7, 9],
        unauthorized: [24, 27, 22, 30],
        blocked: [6, 8, 7, 9],
        fileNotFound: [5, 6, 4, 7],
        methodNotAllowed: [3, 4, 2, 5]
    }
};

window.DashboardData = {
    async getSummaryData() {
        return Promise.resolve({
            lastUpdated: "2026-04-18 10:42:15",
            healthStatus: "Healthy",
            healthSubtext: "/health returned 200 OK",
            avgResponseTime: "118 ms",
            storageUsed: "71%",
            storageSubtext: "Used: 7.1 GB of 10 GB",
            activeSessions: 1,
            blockedUsers: 3
        });
    },

    async getAuthChartData(range = "hourly") {
        return Promise.resolve(AUTH_CHART_DATA[range]);
    },

    async getErrorChartData(range = "hourly") {
        return Promise.resolve(ERROR_CHART_DATA[range]);
    },

    async getEventData() {
        return Promise.resolve([
            {
                time: "10:42:15",
                severity: "INFO",
                source: "health",
                event: "Health check succeeded",
                details: "GET /health returned status ok"
            },
            {
                time: "10:41:57",
                severity: "WARN",
                source: "auth",
                event: "Login failed",
                details: "Invalid credentials for username=admin"
            },
            {
                time: "10:41:45",
                severity: "WARN",
                source: "auth",
                event: "User temporarily blocked",
                details: "username=admin exceeded failed login threshold"
            },
            {
                time: "10:40:32",
                severity: "INFO",
                source: "upload",
                event: "File uploaded",
                details: "name=test.txt uploaded successfully"
            },
            {
                time: "10:39:48",
                severity: "ERROR",
                source: "download",
                event: "File not found",
                details: "name=does-not-exist.txt"
            },
            {
                time: "10:38:20",
                severity: "ERROR",
                source: "api",
                event: "Invalid JSON request",
                details: "POST /auth rejected malformed JSON body"
            },
            {
                time: "10:37:11",
                severity: "INFO",
                source: "files",
                event: "File list returned",
                details: "Authenticated session retrieved file list"
            }
        ]);
    }
};