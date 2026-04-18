package com.haomin.handler;

import com.haomin.HttpUtils;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.util.Map;

public class HealthHandler implements HttpHandler {
    @Override
    // Use the command below:
    // curl -i http://localhost:8080/health
    public void handle(HttpExchange exchange) throws IOException {
        // Only allow GET
        if(!exchange.getRequestMethod().equalsIgnoreCase("GET")) {
            HttpUtils.sendJson(exchange, 405, Map.of(
                    "success", false,
                    "error", "method_not_allowed"
            ));
            return;
        }

        // If server is able to respond, is it assumed to be healthy
        HttpUtils.sendJson(exchange, 200, Map.of(
                "success", true,
                "status", "ok"
        ));
    }
}
