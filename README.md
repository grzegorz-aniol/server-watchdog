
# Health checker

The application performs HTTP health check against public IP of EC2 machine that is indicated by provided tag:key.
If machine is not responding then SNS alter is sent. 

_Only first instance with tag=$WATCH_TAGKEY and value=$WATCH_TAGVALUE will be considered for processing!_

## Environment variables applicable for Health Checker 
* WATCH_TAGKEY    // REQUIRED
* WATCH_TAGVALUE  // REQUIRED
* WATCH_SNSTOPIC  // REQUIRED (arn for the topic)
* WATCH_PROTOCOL  // default: http
* WATCH_PORT      // default: 80
* WATCH_FILEPATH  // default: /

## Run as standalone application
`java -cp <jarfile> com.cloudyna.HealthCheckApp`

## Run as AWS lambda
`com.cloudyna.HealthCheckApp::handler`

# Image backup

Application creates an AMI images for EC2 machine that is marked with specific tag:key. 
If the number of images exceeds the limit then old images are removed. 

_Only first instance with tag=$WATCH_TAGKEY and value=$WATCH_TAGVALUE will be considered for processing!_

## Variables applicable for Image Backup
* WATCH_TAGKEY    // REQUIRED
* WATCH_TAGVALUE  // REQUIRED
* WATCH_SNSTOPIC  // REQUIRED (arn for the topic)
* WATCH_MAXIMAGES // default: 3

## Run as standalone application
`java -cp <jarfile> com.cloudyna.ImageBackupApp`

## Run as AWS lambda
`com.cloudyna.ImageBackupApp::handler`

# S3 File modification checker

Applications check existence of file in S3 bucket and sends an SNS alter in following scenarios:
* the file doesn't exist
* file has 0 bytes
* file is older than expected

## Variables applicable for S3 File Checker
* WATCH_SNSTOPIC   // REQUIRED (arn for the topic)
* WATCH_BUCKETNAME - bucket name
* WATCH_S3FILEPATH - file path in bucket, e.g. folder/folder2/file.dat
* WATCH_MAXFILEAGE - max accepted age of the file in minutes

## Run as standalone application
`java -cp <jarfile> com.cloudyna.S3FileCheckApp`

## Run as AWS lambda
`com.cloudyna.S3FileCheckApp::handler`

