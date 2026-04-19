package com.haomin.handler;

import com.google.gson.JsonSyntaxException;
import com.haomin.AuthManager;
import com.haomin.HttpUtils;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.util.Map;

public class AuthHandler implements HttpHandler {
    private static final String VALID_USERNAME = "admin";
    private static final String VALID_PASSWORD = "password123";

    @Override
    /* Use the command below for correct auth:
    curl -i -X POST http://localhost:8080/auth \
        -H "Content-Type: application/json" \
        -d '{"username":"admin","password":"password123"}'
    */
    public void handle(HttpExchange exchange) throws IOException {
        // Only allow POST
        if(!exchange.getRequestMethod().equalsIgnoreCase("POST")) {
            HttpUtils.sendJson(exchange, 405, Map.of(
                    "success", false,
                    "error", "method_not_allowed"
            ));
            return;
        }

        String body = HttpUtils.readBody(exchange);
        AuthRequest request;
        try {
            // Check JSON format
            request = HttpUtils.fromJson(body, AuthRequest.class);
        } catch (JsonSyntaxException e) {
            HttpUtils.sendJson(exchange, 400, Map.of(
                    "success", false,
                    "error", "invalid_json"
            ));
            return;
        }

        // Check username and password exists
        if(request == null || request.username == null || request.password == null) {
            HttpUtils.sendJson(exchange, 400, Map.of(
                    "success", false,
                    "error", "missing_username_or_password"
            ));
            return;
        }

        // Too many failed attempts for this username
        if(AuthManager.isBlocked(request.username)) {
            HttpUtils.sendJson(exchange, 403, Map.of(
                    "success", false,
                    "error", "temporarily_blocked"
            ));
            return;
        }

        // Issues session token if username and password is valid
        if(VALID_USERNAME.equals(request.username) && VALID_PASSWORD.equals(request.password)) {
            AuthManager.clearFailures(request.username);
            String sessionToken = AuthManager.createSessionToken();
            HttpUtils.sendJson(exchange, 200, Map.of(
                    "success", true,
                    "sessionToken", sessionToken
            ));
            return;
        }
        AuthManager.recordFailure(request.username);

        // Invalid username or passwordA
        HttpUtils.sendJson(exchange, 401, Map.of(
                "success", false,
                "error", "invalid_credentials"
        ));
    }

    private static class AuthRequest {
        String username;
        String password;
    }
}