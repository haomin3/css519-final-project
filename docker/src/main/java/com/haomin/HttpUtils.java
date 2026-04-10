package com.haomin;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class HttpUtils {
    private static final Gson GSON = new Gson();

    public static String readBody(HttpExchange exchange) throws IOException {
        try(InputStream in = exchange.getRequestBody()) {
            return new String(in.readAllBytes(), StandardCharsets.UTF_8);
        }
    }

    public static <T> T fromJson(String json, Class<T> clazz) {
        return GSON.fromJson(json, clazz);
    }

    public static void sendJson(HttpExchange exchange, int statusCode, Object responseObject) throws IOException {
        String json = GSON.toJson(responseObject);
        byte[] responseBytes = json.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
        exchange.sendResponseHeaders(statusCode, responseBytes.length);
        try(OutputStream out = exchange.getResponseBody()) {
            out.write(responseBytes);
        }
    }
}
