package com.haomin.handler;

import com.haomin.AuthManager;
import com.haomin.FileStorage;
import com.haomin.HttpUtils;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

public class OeHandler implements HttpHandler {
    private static final int SIMULATED_STORAGE_LIMIT_BYTES = 100 * 1024;

    private final AuthManager authManager;
    private final FileStorage fileStorage;

    public OeHandler(AuthManager authManager, FileStorage fileStorage) {
        this.authManager = authManager;
        this.fileStorage = fileStorage;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        addCorsHeaders(exchange);

        if(exchange.getRequestMethod().equalsIgnoreCase("OPTIONS")) {
            exchange.sendResponseHeaders(204, -1);
            return;
        }

        if(!exchange.getRequestMethod().equalsIgnoreCase("GET")) {
            HttpUtils.sendJson(exchange, 405, Map.of(
                    "success", false,
                    "error", "method_not_allowed"
            ));
            return;
        }

        int storageUsedBytes = fileStorage.getTotalStorageUsed();
        int storagePercent = Math.min(100, (int)Math.round(
                (storageUsedBytes * 100.0) / SIMULATED_STORAGE_LIMIT_BYTES
        ));

        HttpUtils.sendJson(exchange, 200, Map.of(
                "summary", buildSummary(storageUsedBytes, storagePercent),
                "authChartData", buildAuthChartData(),
                "errorChartData", buildErrorChartData(),
                "customerRiskMetrics", buildCustomerRiskMetrics(),
                "events", buildEvents()
        ));
    }

    private Map<String, Object> buildSummary(int storageUsedBytes, int storagePercent) {
        return Map.of(
                "lastUpdated", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                "healthStatus", "Healthy",
                "healthSubtext", "GET /oe returned 200 OK",
                "avgResponseTime", "118 ms",
                "storageUsed", storagePercent + "%",
                "storageSubtext", "Used: " + formatBytes(storageUsedBytes) + " of " + formatBytes(SIMULATED_STORAGE_LIMIT_BYTES),
                "activeSessions", authManager.getActiveSessionCount(),
                "blockedUsers", authManager.getBlockedUserCount()
        );
    }

    private Map<String, Object> buildAuthChartData() {
        return Map.of(
                "hourly", Map.of(
                        "labels", List.of("10:00", "10:05", "10:10", "10:15", "10:20", "10:25"),
                        "success", List.of(4, 5, 6, 4, 5, 3),
                        "failure", List.of(1, 2, 2, 4, 3, 5)
                ),
                "daily", Map.of(
                        "labels", List.of("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"),
                        "success", List.of(32, 28, 35, 30, 40, 22, 18),
                        "failure", List.of(6, 5, 7, 8, 10, 4, 3)
                ),
                "weekly", Map.of(
                        "labels", List.of("Week 1", "Week 2", "Week 3", "Week 4"),
                        "success", List.of(180, 210, 195, 225),
                        "failure", List.of(28, 35, 31, 40)
                )
        );
    }

    private Map<String, Object> buildErrorChartData() {
        return Map.of(
                "hourly", Map.of(
                        "labels", List.of("10:00", "10:05", "10:10", "10:15", "10:20", "10:25"),
                        "invalidJson", List.of(0, 1, 0, 1, 0, 0),
                        "unauthorized", List.of(1, 1, 2, 1, 1, 2),
                        "blocked", List.of(0, 0, 1, 1, 1, 1),
                        "fileNotFound", List.of(0, 0, 1, 0, 1, 0),
                        "methodNotAllowed", List.of(0, 1, 0, 0, 0, 1)
                ),
                "daily", Map.of(
                        "labels", List.of("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"),
                        "invalidJson", List.of(2, 1, 3, 2, 2, 1, 1),
                        "unauthorized", List.of(5, 6, 7, 5, 8, 4, 3),
                        "blocked", List.of(1, 1, 2, 2, 3, 1, 1),
                        "fileNotFound", List.of(1, 2, 1, 2, 2, 1, 1),
                        "methodNotAllowed", List.of(1, 1, 1, 0, 2, 1, 0)
                ),
                "weekly", Map.of(
                        "labels", List.of("Week 1", "Week 2", "Week 3", "Week 4"),
                        "invalidJson", List.of(8, 10, 7, 9),
                        "unauthorized", List.of(24, 27, 22, 30),
                        "blocked", List.of(6, 8, 7, 9),
                        "fileNotFound", List.of(5, 6, 4, 7),
                        "methodNotAllowed", List.of(3, 4, 2, 5)
                )
        );
    }

    private List<Map<String, Object>> buildCustomerRiskMetrics() {
        return List.of(
                Map.of(
                        "riskArea", "Login disruption",
                        "metric", "Valid Login Success Rate",
                        "currentValue", "92%",
                        "status", "OK",
                        "alertRule", "OK >= 90%, Warn 75-89%, Critical < 75%"
                ),
                Map.of(
                        "riskArea", "Session access disruption",
                        "metric", "Protected Endpoint Access Success Rate",
                        "currentValue", "85%",
                        "status", "WARN",
                        "alertRule", "OK >= 90%, Warn 75-89%, Critical < 75%"
                ),
                Map.of(
                        "riskArea", "File operation disruption",
                        "metric", "File Operation Success Rate",
                        "currentValue", "81%",
                        "status", "WARN",
                        "alertRule", "OK >= 90%, Warn 75-89%, Critical < 75%"
                )
        );
    }

    private List<Map<String, Object>> buildEvents() {
        return List.of(
                Map.of(
                        "time", "10:42:15",
                        "severity", "INFO",
                        "source", "health",
                        "event", "Health check succeeded",
                        "details", "GET /health returned status ok"
                ),
                Map.of(
                        "time", "10:41:57",
                        "severity", "WARN",
                        "source", "auth",
                        "event", "Login failed",
                        "details", "Invalid credentials for username=admin"
                ),
                Map.of(
                        "time", "10:41:45",
                        "severity", "WARN",
                        "source", "auth",
                        "event", "User temporarily blocked",
                        "details", "username=admin exceeded failed login threshold"
                ),
                Map.of(
                        "time", "10:40:32",
                        "severity", "INFO",
                        "source", "upload",
                        "event", "File uploaded",
                        "details", "name=test.txt uploaded successfully"
                ),
                Map.of(
                        "time", "10:39:48",
                        "severity", "ERROR",
                        "source", "download",
                        "event", "File not found",
                        "details", "name=does-not-exist.txt"
                ),
                Map.of(
                        "time", "10:38:20",
                        "severity", "ERROR",
                        "source", "api",
                        "event", "Invalid JSON request",
                        "details", "POST /auth rejected malformed JSON body"
                ),
                Map.of(
                        "time", "10:37:11",
                        "severity", "INFO",
                        "source", "files",
                        "event", "File list returned",
                        "details", "Authenticated session retrieved file list"
                )
        );
    }

    private void addCorsHeaders(HttpExchange exchange) {
        exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "http://localhost:8081");
        exchange.getResponseHeaders().set("Access-Control-Allow-Methods", "GET, OPTIONS");
        exchange.getResponseHeaders().set("Access-Control-Allow-Headers", "Content-Type");
    }

    private String formatBytes(int bytes) {
        if(bytes < 1024) return bytes + " B";
        if(bytes < 1024 * 1024) return String.format("%.1f KB", bytes / 1024.0);

        return String.format("%.1f MB", bytes / (1024.0 * 1024.0));
    }
}