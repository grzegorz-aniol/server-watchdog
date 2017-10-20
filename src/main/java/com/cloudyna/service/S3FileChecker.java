package com.cloudyna.service;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.http.HttpResponse;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.GetObjectMetadataRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import org.apache.http.HttpStatus;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;

public class S3FileChecker {

    private AmazonS3 s3Client;
    private Logger logger;
    public S3FileChecker(AmazonS3 client, Logger logger) {
        this.s3Client = client; 
        this.logger = logger; 
    }
    
    public S3FileStatus checkFile(String bucketName, String filePath, Duration maxAge) {
        logger.log("Checking S3 for file: " + filePath);
        GetObjectMetadataRequest getMetadataRequest = new GetObjectMetadataRequest(bucketName, filePath);
        ObjectMetadata metadata = null;  
        try {
            metadata = s3Client.getObjectMetadata(getMetadataRequest);
        } catch (AmazonServiceException e) {
            logger.log("Error: " + e.getMessage());
            if (e.getStatusCode() == HttpStatus.SC_NOT_FOUND) {
                return S3FileStatus.FILE_NOT_FOUND;
            }
            return S3FileStatus.ERROR; 
        }
        logger.log("Modification date: " + metadata.getLastModified());
        logger.log("Size: " + metadata.getInstanceLength());
        
        if (metadata.getInstanceLength() == 0) {
            return S3FileStatus.ZERO_SIZE.withFileSize(0).withModificationDate(metadata.getLastModified());
        }
        
        LocalDateTime modificationDate = LocalDateTime.ofInstant(metadata.getLastModified().toInstant(), ZoneId.systemDefault());
        LocalDateTime barrierDate = LocalDateTime.now().minusSeconds(maxAge.getSeconds());
        if (modificationDate.isBefore(barrierDate)) {
            return S3FileStatus.NOT_MODIFIED.withFileSize(metadata.getInstanceLength()).withModificationDate(metadata.getLastModified());
        }
        
        return S3FileStatus.OK.withFileSize(metadata.getInstanceLength()).withModificationDate(metadata.getLastModified()); 
    }
}
