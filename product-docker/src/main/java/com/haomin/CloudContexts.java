package com.haomin;

import com.haomin.handler.*;
import com.sun.net.httpserver.HttpServer;

public class CloudContexts {
    public static void register(HttpServer server) {
        server.createContext("/health",   new HealthHandler());
        server.createContext("/auth",     new AuthHandler());
        server.createContext("/files",    new FilesHandler());
        server.createContext("/upload",   new UploadHandler());
        server.createContext("/download", new DownloadHandler());
    }
}