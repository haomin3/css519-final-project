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
public class FileOperationsTest {

    private HttpServer server;
    private String sessionToken;

    @BeforeEach
    void setUp() throws Exception {
        server = MockCloudServerMain.startServer();
        sessionToken = authenticateAndGetSessionToken();
    }

    @AfterEach
    void tearDown() {
        if(server != null) server.stop(0);
    }

    @Test
    void filesEndpointWithoutSessionReturns401() throws Exception {
        HttpURLConnection connection = createConnection("/files", "GET", null, null);

        int statusCode = connection.getResponseCode();
        String responseBody = readErrorBody(connection);

        assertEquals(401, statusCode);
        assertTrue(responseBody.contains("\"success\":false"));
        assertTrue(responseBody.contains("\"error\":\"invalid_or_missing_session\""));

        connection.disconnect();
    }

    @Test
    void filesEndpointWithValidSessionReturns200() throws Exception {
        HttpURLConnection connection = createConnection("/files", "GET", sessionToken, null);

        int statusCode = connection.getResponseCode();
        String responseBody = readResponseBody(connection);

        assertEquals(200, statusCode);
        assertTrue(responseBody.contains("\"success\":true"));
        assertTrue(responseBody.contains("\"files\""));

        connection.disconnect();
    }

    @Test
    void uploadWithValidSessionReturns200() throws Exception {
        HttpURLConnection connection = createConnection(
                "/upload",
                "POST",
                sessionToken,
                "{\"name\":\"test.txt\",\"content\":\"hello from junit\"}"
        );

        int statusCode = connection.getResponseCode();
        String responseBody = readResponseBody(connection);

        assertEquals(200, statusCode);
        assertTrue(responseBody.contains("\"success\":true"));
        assertTrue(responseBody.contains("\"name\":\"test.txt\""));

        connection.disconnect();
    }

    @Test
    void downloadWithValidSessionReturns200() throws Exception {
        HttpURLConnection uploadConnection = createConnection(
                "/upload",
                "POST",
                sessionToken,
                "{\"name\":\"download-test.txt\",\"content\":\"download me\"}"
        );
        assertEquals(200, uploadConnection.getResponseCode());
        uploadConnection.disconnect();

        HttpURLConnection downloadConnection = createConnection(
                "/download",
                "POST",
                sessionToken,
                "{\"name\":\"download-test.txt\"}"
        );

        int statusCode = downloadConnection.getResponseCode();
        String responseBody = readResponseBody(downloadConnection);

        assertEquals(200, statusCode);
        assertTrue(responseBody.contains("\"success\":true"));
        assertTrue(responseBody.contains("\"name\":\"download-test.txt\""));
        assertTrue(responseBody.contains("\"content\":\"download me\""));

        downloadConnection.disconnect();
    }

    @Test
    void invalidMethodReturns405() throws Exception {
        HttpURLConnection connection = createConnection("/files", "POST", sessionToken, null);

        int statusCode = connection.getResponseCode();
        String responseBody = readErrorBody(connection);

        assertEquals(405, statusCode);
        assertTrue(responseBody.contains("\"success\":false"));
        assertTrue(responseBody.contains("\"error\":\"method_not_allowed\""));

        connection.disconnect();
    }

    private String authenticateAndGetSessionToken() throws Exception {
        HttpURLConnection connection = createConnection(
                "/auth",
                "POST",
                null,
                "{\"username\":\"admin\",\"password\":\"password123\"}"
        );

        int statusCode = connection.getResponseCode();
        String responseBody = readResponseBody(connection);

        assertEquals(200, statusCode);

        String marker = "\"sessionToken\":\"";
        int start = responseBody.indexOf(marker);
        int end = responseBody.indexOf("\"", start + marker.length());

        connection.disconnect();

        if(start < 0 || end < 0) {
            throw new IllegalStateException("Session token not found in auth response: " + responseBody);
        }

        return responseBody.substring(start + marker.length(), end);
    }

    private HttpURLConnection createConnection(String path, String method, String token, String jsonBody) throws Exception {
        HttpURLConnection connection = (HttpURLConnection) URI.create("http://localhost:8080" + path)
                .toURL()
                .openConnection();

        connection.setRequestMethod(method);

        if(token != null) {
            connection.setRequestProperty("Session-Token", token);
        }

        if(jsonBody != null) {
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);

            try (OutputStream out = connection.getOutputStream()) {
                out.write(jsonBody.getBytes(StandardCharsets.UTF_8));
            }
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