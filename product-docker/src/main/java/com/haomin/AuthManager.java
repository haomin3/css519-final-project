package com.haomin;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class AuthManager {
    // Session token -> expiration time in milliseconds
    private final Map<String, Long> VALID_TOKENS = new ConcurrentHashMap<>();
    private static final int SESSION_EXPIRATION_MINUTES = 30;
    private static final long SESSION_EXPIRATION_MILLIS = SESSION_EXPIRATION_MINUTES * 60L * 1000L;

    // Username -> failed attempt count
    private final Map<String, Integer> FAILED_ATTEMPTS = new ConcurrentHashMap<>();
    // username -> blocked until timestamp
    private final Map<String, Long> BLOCKED_UNTIL = new ConcurrentHashMap<>();
    private static final int MAX_FAILED_ATTEMPTS = 3;
    private static final int BLOCK_TIME_MINUTES  = 5;
    private static final long BLOCK_TIME_MILLIS = BLOCK_TIME_MINUTES * 60L * 1000L;

    public String createSessionToken() {
        String token = UUID.randomUUID().toString();
        long expiresAt = System.currentTimeMillis() + SESSION_EXPIRATION_MILLIS;
        VALID_TOKENS.put(token, expiresAt);
        return token;
    }

    public boolean isValidToken(String token) {
        if(token == null) return false;
        Long expiresAt = VALID_TOKENS.get(token);

        if(expiresAt == null) return false;
        if(System.currentTimeMillis() > expiresAt) {
            VALID_TOKENS.remove(token);
            return false;
        }
        return true;
    }

    public boolean isBlocked(String username) {
        if(username == null) return false;
        Long blockedUntil = BLOCKED_UNTIL.get(username);

        if(blockedUntil == null) return false;
        if(System.currentTimeMillis() >= blockedUntil) {
            BLOCKED_UNTIL.remove(username);
            FAILED_ATTEMPTS.remove(username);
            return false;
        }
        return true;
    }

    public void recordFailure(String username) {
        if(username == null) return;
        int failures = FAILED_ATTEMPTS.getOrDefault(username, 0) + 1;
        
        FAILED_ATTEMPTS.put(username, failures);
        if(failures >= MAX_FAILED_ATTEMPTS) {
            BLOCKED_UNTIL.put(username, System.currentTimeMillis() + BLOCK_TIME_MILLIS);
        }
    }

    public void clearFailures(String username) {
        if(username == null) return;
        FAILED_ATTEMPTS.remove(username);
        BLOCKED_UNTIL.remove(username);
    }
}
