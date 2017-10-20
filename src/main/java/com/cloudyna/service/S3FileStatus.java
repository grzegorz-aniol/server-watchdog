package com.cloudyna.service;

import java.util.Date;

public enum S3FileStatus {
    OK("OK"), 
    ERROR("An error occured"), 
    UNKNOWN("Unknown error"),
    FILE_NOT_FOUND("File not found"), 
    ZERO_SIZE("File is 0 bytes length"), 
    NOT_MODIFIED("File has not been modified since expected time")
    ;
    
    S3FileStatus(String message) {
        this.message = message;
    }
    
    public String getMessage() {
        return this.message;
    }
    
    public Long getFileSize() {
        return fileSize;
    }

    public S3FileStatus withFileSize(long fileSize) {
        this.fileSize = fileSize;
        return this;
    }

    public Date getModificationDate() {
        return modificationDate;
    }

    public S3FileStatus withModificationDate(Date modificationDate) {
        this.modificationDate = modificationDate;
        return this;
    }

    private String message;
    
    private Long fileSize = null;
    
    private Date modificationDate = null;
}