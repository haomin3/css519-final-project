package com.haomin;

import com.sun.net.httpserver.HttpServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class HealthHandlerTest {

    private HttpServer server;

    @BeforeEach
    void setUp() throws Exception {
        server = MockCloudServerMain.startServer();
    }

    @AfterEach
    void tearDown() {
        if (server != null) server.stop(0);
    }

    @Test
    void healthEndpointReturns200() throws Exception {
        HttpURLConnection connection = (HttpURLConnection) URI.create("http://localhost:8080/health")
                .toURL()
                .openConnection();
        connection.setRequestMethod("GET");

        int statusCode = connection.getResponseCode();
        String responseBody;
        try(BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8))) {
            responseBody = reader.lines().collect(Collectors.joining());
        }

        assertEquals(200, statusCode);
        assertTrue(responseBody.contains("\"success\":true"));
        assertTrue(responseBody.contains("\"status\":\"ok\""));

        connection.disconnect();
    }
}