package com.haomin;

import com.haomin.handler.*;
import com.sun.net.httpserver.HttpServer;

public class CloudContexts {
    public static void register(HttpServer server, AuthManager authManager, FileStorage fileStorage) {
        server.createContext("/health",   new HealthHandler());
        server.createContext("/auth",     new AuthHandler(authManager));
        server.createContext("/files",    new FilesHandler(authManager, fileStorage));
        server.createContext("/upload",   new UploadHandler(authManager, fileStorage));
        server.createContext("/download", new DownloadHandler(authManager, fileStorage));
        server.createContext("/",         new StaticFileHandler());
    }
}