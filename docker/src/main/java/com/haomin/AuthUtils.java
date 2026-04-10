package com.haomin;

import com.sun.net.httpserver.HttpExchange;

public class AuthUtils {
    private static final String SESSION_HEADER = "Session-Token";

    public static String getSessionToken(HttpExchange exchange) {
        return exchange.getRequestHeaders().getFirst(SESSION_HEADER);
    }

    public static boolean hasValidSession(HttpExchange exchange) {
        String token = getSessionToken(exchange);
        return AuthManager.isValidToken(token);
    }
}
