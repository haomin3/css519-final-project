package com.haomin;

import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

/**
 * This is the Cloudsploitable mock product for CSS 519
 * Expected environment: Java SE 21
 */
public class MockCloudServerMain {
    public static final int MAX_CONNECTIONS = 3;
    public static final int ACCESS_PORT = 8080;

    static HttpServer startServer() throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(ACCESS_PORT), 0);
        CloudContexts.register(server, new AuthManager(), new FileStorage());
        server.setExecutor(Executors.newFixedThreadPool(MAX_CONNECTIONS));
        server.start();
        return server;
    }

    public static void main(String[] args) throws IOException {
        System.out.println("Starting server on http://localhost:" + ACCESS_PORT);
        startServer();
    }
}
