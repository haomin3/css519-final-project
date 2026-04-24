package com.haomin;

import com.sun.net.httpserver.HttpServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Execution(ExecutionMode.SAME_THREAD)
public class AuthHandlerTest {

    private HttpServer server;

    @BeforeEach
    void setUp() throws Exception {
        server = MockCloudServerMain.startServer();
    }

    @AfterEach
    void tearDown() {
        if(server != null) server.stop(0);
    }

    @Test
    void validCredentialsReturnSessionToken() throws Exception {
        HttpURLConnection connection = createPostConnection("/auth",
                "{\"username\":\"admin\",\"password\":\"password123\"}");

        int statusCode = connection.getResponseCode();
        String responseBody = readResponseBody(connection);

        assertEquals(200, statusCode);
        assertTrue(responseBody.contains("\"success\":true"));
        assertTrue(responseBody.contains("\"sessionToken\""));

        connection.disconnect();
    }

    @Test
    void invalidCredentialsReturn401() throws Exception {
        HttpURLConnection connection = createPostConnection("/auth",
                "{\"username\":\"admin\",\"password\":\"wrongpassword\"}");

        int statusCode = connection.getResponseCode();
        String responseBody = readErrorBody(connection);

        assertEquals(401, statusCode);
        assertTrue(responseBody.contains("\"success\":false"));
        assertTrue(responseBody.contains("\"error\":\"invalid_credentials\""));

        connection.disconnect();
    }

    @Test
    void malformedAuthJsonReturns400() throws Exception {
        HttpURLConnection connection = createPostConnection("/auth",
                "{\"username\":\"admin\",\"password\":\"password123\"");

        int statusCode = connection.getResponseCode();
        String responseBody = readErrorBody(connection);

        assertEquals(400, statusCode);
        assertTrue(responseBody.contains("\"success\":false"));
        assertTrue(responseBody.contains("\"error\":\"invalid_json\""));

        connection.disconnect();
    }

    @Test
    void repeatedFailedLoginsTriggerTemporaryBlock() throws Exception {
        for (int i = 0; i < 3; i++) {
            HttpURLConnection connection = createPostConnection("/auth",
                    "{\"username\":\"admin\",\"password\":\"wrongpassword\"}");

            int statusCode = connection.getResponseCode();
            String responseBody = readErrorBody(connection);

            assertEquals(401, statusCode);
            assertTrue(responseBody.contains("\"error\":\"invalid_credentials\""));

            connection.disconnect();
        }

        HttpURLConnection blockedConnection = createPostConnection("/auth",
                "{\"username\":\"admin\",\"password\":\"wrongpassword\"}");

        int blockedStatus = blockedConnection.getResponseCode();
        String blockedBody = readErrorBody(blockedConnection);

        assertEquals(403, blockedStatus);
        assertTrue(blockedBody.contains("\"success\":false"));
        assertTrue(blockedBody.contains("\"error\":\"temporarily_blocked\""));

        blockedConnection.disconnect();
    }

    private HttpURLConnection createPostConnection(String path, String jsonBody) throws Exception {
        HttpURLConnection connection = (HttpURLConnection) URI.create("http://localhost:8080" + path)
                .toURL()
                .openConnection();

        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setDoOutput(true);

        try (OutputStream out = connection.getOutputStream()) {
            out.write(jsonBody.getBytes(StandardCharsets.UTF_8));
        }

        return connection;
    }

    private String readResponseBody(HttpURLConnection connection) throws Exception {
        try (InputStream in = connection.getInputStream();
             BufferedReader reader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8))) {
            return reader.lines().collect(Collectors.joining());
        }
    }

    private String readErrorBody(HttpURLConnection connection) throws Exception {
        try (InputStream in = connection.getErrorStream();
             BufferedReader reader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8))) {
            return reader.lines().collect(Collectors.joining());
        }
    }
}