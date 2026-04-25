package com.haomin;

// For file records
public class StoredFile {
    private final String name;
    private final String content;
    private final int size;
    private final String uploadedAt;
    private final String uploadedBy;

    public StoredFile(String name, String content, String uploadedAt, String uploadedBy) {
        this.name = name;
        this.content = content;
        this.size = content == null ? 0 : content.length();
        this.uploadedAt = uploadedAt;
        this.uploadedBy = uploadedBy;
    }

    public String getName() {
        return name;
    }

    public String getContent() {
        return content;
    }

    public int getSize() {
        return size;
    }

    public String getUploadedAt() {
        return uploadedAt;
    }

    public String getUploadedBy() {
        return uploadedBy;
    }
}