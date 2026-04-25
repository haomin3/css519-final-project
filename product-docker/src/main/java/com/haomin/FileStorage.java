package com.haomin;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class FileStorage {
    private final Map<String, StoredFile> FILES = new ConcurrentHashMap<>();
    public FileStorage() {
        FILES.put("readme.txt", new StoredFile(
                "readme.txt",
                "This is a sample readme file.",
                "2026-04-18 09:14:22",
                "haomin"
        ));
        FILES.put("notes.txt", new StoredFile(
                "notes.txt",
                "Sample notes stored at server startup.",
                "2026-04-19 14:37:08",
                "test"
        ));
        FILES.put("hello.txt", new StoredFile(
                "hello.txt",
                "Hello from the mock cloud server.",
                "2026-04-22 11:05:41",
                "admin"
        ));
    }

    public void saveFile(String name, String content, String uploadedAt, String uploadedBy) {
        FILES.put(name, new StoredFile(name, content, uploadedAt, uploadedBy));
    }

    public boolean fileExists(String name) {
        return FILES.containsKey(name);
    }

    public String getFileContent(String name) {
        StoredFile file = FILES.get(name);
        return file == null ? null : file.getContent();
    }

    public List<StoredFile> listFiles() {
        return new ArrayList<>(FILES.values());
    }
}