package com.cloudyna.service;

import java.time.Duration;

public class AppContext {

    public static final String EV_TAGKEY   = "WATCH_TAGKEY";    // REQUIRED
    public static final String EV_TAGVALUE = "WATCH_TAGVALUE";  // REQUIRED
    public static final String EV_SNSTOPIC = "WATCH_SNSTOPIC";  // REQUIRED
    public static final String EV_BUCKETNAME = "WATCH_BUCKETNAME";  // REQUIRED for S3FileCheckApp
    public static final String EV_S3FILEPATH = "WATCH_S3FILEPATH";  // REQUIRED for S3FileCheckApp
    public static final String EV_PROTOCOL = "WATCH_PROTOCOL";  // default: http
    public static final String EV_PORT     = "WATCH_PORT";      // default: 80
    public static final String EV_FILEPATH = "WATCH_FILEPATH";  // default: /
    public static final String EV_MAXIMAGES= "WATCH_MAXIMAGES"; // default: 3
    public static final String EV_MAXFILEAGE = "WATCH_MAXFILEAGE";  // s3 file max age in minutes (default: 60)
    
    public String tagKey = "cloudyna";
    public String tagValue = "yes";
    
    public static final String IMAGE_OWNER = "self";
    public static final String IMAGE_TAG = "cloudyna";
    
    private String protocol = "http";
    private int port = 80;
    private String filePath = "/";
    private String snsTopicArn = null; // required to set up
    private int maxImagesCount = 3;
    private String s3BucketName;
    private String s3FilePath; 
    private Duration maxFileAge;
    
    public static final AppContext INSTANCE = new AppContext();

    private String getEnvValue(final String name, final String defaultValue) {
        String val = System.getenv(name);
        if (val != null && !val.isEmpty()) {
            return val;
        }
        if (defaultValue == null) {
            throw new RuntimeException("Missing value for environment variable " + name);
        }
        return defaultValue; 
    }
    private int getEnvValue(final String name, final int defaultValue) {
        String val = System.getenv(name);
        if (val != null && !val.isEmpty()) {
            return Integer.parseInt(val);
        }
        return defaultValue; 
    }    
    
    public void initialize() throws Exception {
        this.tagKey = getEnvValue(EV_TAGKEY, tagKey);
        this.tagValue = getEnvValue(EV_TAGVALUE, tagValue);
        this.protocol = getEnvValue(EV_PROTOCOL, this.protocol);       
        this.filePath = getEnvValue(EV_FILEPATH, this.filePath);
        this.port = getEnvValue(EV_PORT, port);
        this.snsTopicArn = getEnvValue(EV_SNSTOPIC, null);
        this.maxImagesCount  = Math.max(1, getEnvValue(EV_MAXIMAGES, maxImagesCount));
        this.s3BucketName = getEnvValue(EV_BUCKETNAME, "");
        this.s3FilePath = getEnvValue(EV_S3FILEPATH, "");
        this.maxFileAge = Duration.ofMinutes(getEnvValue(EV_MAXFILEAGE, 60));
    }

    public String getTagValue() {
        return tagValue;
    }
    
    public String getTagKey() {
        return tagKey;
    }

    public String getProtocol() {
        return protocol;
    }

    public int getPort() {
        return port;
    }

    public String getFilePath() {
        return filePath;
    }

    public String getSnsTopicArn() {
        return snsTopicArn;
    }

    public void setSnsTopicArn(String snsTopicArn) {
        this.snsTopicArn = snsTopicArn;
    }
    public int getMaxImagesCount() {
        return maxImagesCount;
    }
    public String getS3BucketName() {
        return s3BucketName;
    }
    public String getS3FilePath() {
        return s3FilePath;
    }
    public Duration getMaxFileAge() {
        return maxFileAge;
    }
}
