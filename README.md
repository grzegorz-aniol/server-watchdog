# Environment variables:

## Variables applicable for Health Checker 
WATCH_TAGKEY    // REQUIRED
WATCH_TAGVALUE  // REQUIRED
WATCH_SNSTOPIC  // REQUIRED (arn for the topic)
WATCH_PROTOCOL  // default: http
WATCH_PORT      // default: 80
WATCH_FILEPATH  // default: /

_Only first instance with tag=$WATCH_TAGKEY and value=$WATCH_TAGVALUE will be considered for processing!_

## Variables applicable for Image Backup
* WATCH_TAGKEY    // REQUIRED
* WATCH_TAGVALUE  // REQUIRED
* WATCH_SNSTOPIC  // REQUIRED (arn for the topic)
* WATCH_MAXIMAGES // default: 3

_Only first instance with tag=$WATCH_TAGKEY and value=$WATCH_TAGVALUE will be considered for processing!_


## Variables applicable for S3 File Checker
* WATCH_SNSTOPIC   // REQUIRED (arn for the topic)
* WATCH_BUCKETNAME - bucket name
* WATCH_S3FILEPATH - file path in bucket, e.g. folder/folder2/file.dat
* WATCH_MAXFILEAGE - max accepted age of the file in minutes


# Health checker

## Run as standalone application
`java -cp <jarfile> com.cloudyna.HealthCheckApp`

## Run as AWS lambda
`com.cloudyna.HealthCheckApp::handler`

# Image backup

## Run as standalone application
`java -cp <jarfile> com.cloudyna.ImageBackupApp`

## Run as AWS lambda
`com.cloudyna.ImageBackupApp::handler`

# S3 File modification checker

## Run as standalone application
`java -cp <jarfile> com.cloudyna.S3FileCheckApp`

## Run as AWS lambda
`com.cloudyna.S3FileCheckApp::handler`

