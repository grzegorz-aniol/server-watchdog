Environment variables:
WATCH_TAGKEY    // REQUIRED
WATCH_TAGVALUE  // REQUIRED
WATCH_SNSTOPIC  // REQUIRED
WATCH_PROTOCOL  // default: http
WATCH_PORT      // default: 80
WATCH_FILEPATH  // default: /
WATCH_MAXIMAGES // default: 3

# Only first instance with tag=$WATCH_TAGKEY and value=$WATCH_TAGVALUE will be considered for processing!

# run service health checker
java -cp server-watchdog-1.0.jar com.cloudyna.HealthCheckApp

# run creating image
java -cp server-watchdog-1.0.jar com.cloudyna.ImageBackupApp
