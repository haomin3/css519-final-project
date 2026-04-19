package com.haomin.handler;

import com.haomin.AuthUtils;
import com.haomin.FileStorage;
import com.haomin.HttpUtils;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.util.Map;

public class FilesHandler implements HttpHandler {
    @Override
    // curl -i http://localhost:8080/files -H "Session-Token: <token>"
    public void handle(HttpExchange exchange) throws IOException {
        // GET requests only
        if(!exchange.getRequestMethod().equalsIgnoreCase("GET")) {
            HttpUtils.sendJson(exchange, 405, Map.of(
                    "success", false,
                    "error", "method_not_allowed"
            ));
            return;
        }

        // Verifies session token
        if(!AuthUtils.hasValidSession(exchange)) {
            HttpUtils.sendJson(exchange, 401, Map.of(
                    "success", false,
                    "error", "invalid_or_missing_session"
            ));
            return;
        }

        // Everything ok. Sending list of files...
        HttpUtils.sendJson(exchange, 200, Map.of(
                "success", true,
                "files", FileStorage.listFiles()
        ));
    }
}
