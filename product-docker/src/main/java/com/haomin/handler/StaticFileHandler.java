package com.haomin.handler;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class StaticFileHandler implements HttpHandler {

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        // Only allow GET for the static files
        if (!exchange.getRequestMethod().equalsIgnoreCase("GET")) {
            exchange.sendResponseHeaders(405, -1);
            return;
        }

        // Get the path from the browser request:
        // "/" -> main page
        // "/styles.css" -> CSS file
        // "/app.js" -> JavaScript file
        String path = exchange.getRequestURI().getPath();

        // If visited "/" directly, serve index.html
        // Otherwise, look for the requested file under /static
        if (path.equals("/") || path.isBlank()) {
            path = "/static/index.html";
        } else {
            path = "/static" + path;
        }

        // Load the file from src/main/resources/static inside the JAR
        InputStream resourceStream = getClass().getResourceAsStream(path);

        // If the file does not exist, return 404.
        if (resourceStream == null) {
            exchange.sendResponseHeaders(404, -1);
            return;
        }

        // Read the whole file into memory so it can be sent in the response
        byte[] content;
        try (InputStream in = resourceStream) {
            content = in.readAllBytes();
        }

        // Tell the browser what kind of file it is
        exchange.getResponseHeaders().set("Content-Type", getContentType(path));

        // Send the file contents back to the browser
        exchange.sendResponseHeaders(200, content.length);
        try (OutputStream out = exchange.getResponseBody()) {
            out.write(content);
        }
    }

    private String getContentType(String path) {
        if (path.endsWith(".html")) {
            return "text/html; charset=UTF-8";
        }
        if (path.endsWith(".css")) {
            return "text/css; charset=UTF-8";
        }
        if (path.endsWith(".js")) {
            return "application/javascript; charset=UTF-8";
        }

        // Fallback for anything else
        return "application/octet-stream";
    }
}