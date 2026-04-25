package com.haomin.handler;

import com.google.gson.JsonSyntaxException;
import com.haomin.AuthManager;
import com.haomin.AuthUtils;
import com.haomin.FileStorage;
import com.haomin.HttpUtils;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

public class UploadHandler implements HttpHandler {
    private final AuthManager authManager;
    private final FileStorage fileStorage;

    public UploadHandler(AuthManager authManager, FileStorage fileStorage) {
        this.authManager = authManager;
        this.fileStorage = fileStorage;
    }

    @Override
    /* Use the command below to check:
    curl -i -X POST http://localhost:8080/upload \
        -H "Content-Type: application/json" \
        -H "Session-Token: <token>" \
        -d '{"name":"test.txt","content":"123 this is content for a test file 456"}'
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
        UploadRequest request;
        try {
            request = HttpUtils.fromJson(body, UploadRequest.class);
        } catch (JsonSyntaxException e) {
            HttpUtils.sendJson(exchange, 400, Map.of(
                    "success", false,
                    "error", "invalid_json"
            ));
            return;
        }

        // Checking missing file name and content
        if(request == null || request.name == null || request.content == null) {
            HttpUtils.sendJson(exchange, 400, Map.of(
                    "success", false,
                    "error", "missing_name_or_content"
            ));
            return;
        }

        // Checking blank file name and content
        if(request.name.isBlank() || request.content.isBlank()) {
            HttpUtils.sendJson(exchange, 400, Map.of(
                    "success", false,
                    "error", "empty_name_or_content"
            ));
            return;
        }

        // Everything ok. Uploading file...
        String uploadedBy = authManager.getUsernameForToken(token);
        String uploadedAt = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        fileStorage.saveFile(request.name, request.content, uploadedAt, uploadedBy);
        HttpUtils.sendJson(exchange, 200, Map.of(
                "success", true,
                "name", request.name
        ));
    }

    private static class UploadRequest {
        String name;
        String content;
    }
}