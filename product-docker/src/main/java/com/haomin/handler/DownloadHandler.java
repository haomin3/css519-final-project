package com.haomin.handler;

import com.google.gson.JsonSyntaxException;
import com.haomin.AuthManager;
import com.haomin.AuthUtils;
import com.haomin.FileStorage;
import com.haomin.HttpUtils;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.util.Map;

public class DownloadHandler implements HttpHandler {
    private final AuthManager authManager;
    private final FileStorage fileStorage;

    public DownloadHandler(AuthManager authManager, FileStorage fileStorage) {
        this.authManager = authManager;
        this.fileStorage = fileStorage;
    }

    @Override
    /* Use the following command to check:
    curl -i -X POST http://localhost:8080/download \
        -H "Content-Type: application/json" \
        -H "Session-Token: <token>" \
        -d '{"name":"readme.txt"}'
    */
    public void handle(HttpExchange exchange) throws IOException {
        // POST request only
        if(!exchange.getRequestMethod().equalsIgnoreCase("POST")) {
            HttpUtils.sendJson(exchange, 405, Map.of(
                    "success", false,
                    "error", "method_not_allowed"
            ));
            return;
        }

        // Checking session token
        String token = AuthUtils.getSessionToken(exchange);
        if(!authManager.isValidToken(token)) {
            HttpUtils.sendJson(exchange, 401, Map.of(
                    "success", false,
                    "error", "invalid_or_missing_session"
            ));
            return;
        }

        // Checking JSON format
        String body = HttpUtils.readBody(exchange);
        DownloadRequest request;
        try {
            request = HttpUtils.fromJson(body, DownloadRequest.class);
        } catch (JsonSyntaxException e) {
            HttpUtils.sendJson(exchange, 400, Map.of(
                    "success", false,
                    "error", "invalid_json"
            ));
            return;
        }

        // Checking requested file name
        if(request == null || request.name == null || request.name.isBlank()) {
            HttpUtils.sendJson(exchange, 400, Map.of(
                    "success", false,
                    "error", "missing_name"
            ));
            return;
        }

        // Checking that the requested file name exists
        if(!fileStorage.fileExists(request.name)) {
            HttpUtils.sendJson(exchange, 404, Map.of(
                    "success", false,
                    "error", "file_not_found"
            ));
            return;
        }

        // Everything ok. Sending file...
        HttpUtils.sendJson(exchange, 200, Map.of(
                "success", true,
                "name", request.name,
                "content", fileStorage.getFileContent(request.name)
        ));
    }

    private static class DownloadRequest {
        String name;
    }
}
