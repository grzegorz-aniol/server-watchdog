# Environment variables:
WATCH_TAGKEY    // REQUIRED
WATCH_TAGVALUE  // REQUIRED
WATCH_SNSTOPIC  // REQUIRED (arn for the topic)
WATCH_PROTOCOL  // default: http
WATCH_PORT      // default: 80
WATCH_FILEPATH  // default: /
WATCH_MAXIMAGES // default: 3

_Only first instance with tag=$WATCH_TAGKEY and value=$WATCH_TAGVALUE will be considered for processing!_

# Health checker

## Run as standalone application
> java -cp server-watchdog-1.1.0.jar com.cloudyna.HealthCheckApp

## Run as AWS lambda
> com.cloudyna.HealthCheckApp::handler

# Image backup

## Run as standalone application
> java -cp server-watchdog-1.1.0.jar com.cloudyna.ImageBackupApp

## Run as AWS lambda
> com.cloudyna.ImageBackupApp::handler

