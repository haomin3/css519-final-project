package com.haomin;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class FileStorage {
    private final Map<String, String> FILES = new ConcurrentHashMap<>();
    public FileStorage() {
        FILES.put("readme.txt", "This is a sample readme file.");
        FILES.put("notes.txt", "Sample notes stored at server startup.");
        FILES.put("hello.txt", "Hello from the mock cloud server.");
    }

    public void saveFile(String name, String content) {
        FILES.put(name, content);
    }

    public boolean fileExists(String name) {
        return FILES.containsKey(name);
    }

    public String getFileContent(String name) {
        return FILES.get(name);
    }

    public List<String> listFiles() {
        return new ArrayList<>(FILES.keySet());
    }
}