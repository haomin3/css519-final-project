package com.haomin;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class FileStorage {
    private static final Map<String, String> FILES = new ConcurrentHashMap<>();
    static {
        FILES.put("readme.txt", "This is a sample readme file.");
        FILES.put("notes.txt", "Sample notes stored at server startup.");
        FILES.put("hello.txt", "Hello from the mock cloud server.");
    }

    public static void saveFile(String name, String content) {
        FILES.put(name, content);
    }

    public static boolean fileExists(String name) {
        return FILES.containsKey(name);
    }

    public static String getFileContent(String name) {
        return FILES.get(name);
    }

    public static List<String> listFiles() {
        return new ArrayList<>(FILES.keySet());
    }
}