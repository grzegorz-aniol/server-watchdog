package com.cloudyna;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.AmazonSNSClientBuilder;
import com.cloudyna.service.AlertSender;
import com.cloudyna.service.AppContext;
import com.cloudyna.service.Logger;
import com.cloudyna.service.S3FileChecker;
import com.cloudyna.service.S3FileStatus;

import java.io.InputStream;
import java.io.OutputStream;

/**
 * Check existence of specific file in S3 bucket that is created/modified during recent time
 * 
 * @See {@link AppContext} for mandatory and optional environment variables that need to be specified
 * 
 * @author Grzegorz_Aniol
 *
 */
public class S3FileCheckApp {

    public static void main(String[] args) {
        new S3FileCheckApp().handler(null, null, null);
    }

    public void handler(InputStream inputStream, OutputStream outputStream, Context context) {
        Logger logger = new Logger(context);

        try {
            logger.log("Starting S3FileCheckApp.handler...");
            
            AppContext ctx = AppContext.INSTANCE;

            try {
                ctx.initialize();
                validateParams(ctx);
            } catch (Exception e) {
                logger.log(e.getMessage());
                return;
            }
            

            AmazonS3 client = AmazonS3ClientBuilder.defaultClient();
            
            logger.log("Checking S3 file...");
            
            String filePath = ctx.getS3FilePath();
            String bucketName = ctx.getS3BucketName();            
            S3FileChecker s3Checker = new S3FileChecker(client, logger);
            
            S3FileStatus status = s3Checker.checkFile(bucketName, filePath, ctx.getMaxFileAge());
            
            if (status != S3FileStatus.OK) {                
                final AmazonSNS snsClient = AmazonSNSClientBuilder.defaultClient();
                final AlertSender alertSender = new AlertSender(snsClient, ctx.getSnsTopicArn());
                String msg = alertSender.generateS3FileStatusMessage(status, filePath, status.getModificationDate(), status.getFileSize());
                logger.log("Status: " + status.getMessage());
                alertSender.sendMessage(msg);
                return;
            }

            logger.log("Status: " +  status.getMessage());
            
        } catch (Throwable e) {
            logger.log("Error:" + e.getMessage());
            
        }

    }

    private void validateParams(AppContext ctx) throws Exception {
        if (ctx.getS3BucketName() == null || ctx.getS3BucketName().isEmpty()) {
            throw new Exception("S3 bucket name is not specified!");
        }
        if (ctx.getS3FilePath() == null || ctx.getS3FilePath().isEmpty()) {
            throw new Exception("S3 file path is not specified!");
        }
    }
}
