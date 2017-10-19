package com.cloudyna;

import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.AmazonEC2ClientBuilder;
import com.amazonaws.services.ec2.model.Instance;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.AmazonSNSClientBuilder;
import com.cloudyna.service.AlertSender;
import com.cloudyna.service.AppContext;
import com.cloudyna.service.ImageManager;
import com.cloudyna.service.Logger;
import com.cloudyna.service.MachineFinder;

import java.io.InputStream;
import java.io.OutputStream;

/**
 * Creating image of specific EC2 instance
 * 
 * @See {@link AppContext} for mandatory and optional environment variables that need to be specified
 * 
 * @author Grzegorz_Aniol
 *
 */
public class ImageBackupApp {

    public static void main(final String[] args) {
        new ImageBackupApp().handler(null, null, null);
    }

    public void handler(InputStream inputStream, OutputStream outputStream, Context context) {
        Logger logger = new Logger(context);

        try {
            AppContext ctx = AppContext.INSTANCE;

            try {
                ctx.initialize();
            } catch (Exception e) {
                logger.log(e.getMessage());
                return;
            }

            logger.log("Starting EC2 client");
            final AmazonEC2 ec2Client = AmazonEC2ClientBuilder.defaultClient();
            
            logger.log("Finding machine");
            final MachineFinder finder = new MachineFinder(ec2Client);
            Instance instance = finder.find(ctx.getTagKey(), ctx.getTagValue());
            if (instance == null) {
                final AmazonSNS snsClient = AmazonSNSClientBuilder.defaultClient();
                final AlertSender alertSender = new AlertSender(snsClient, ctx.getSnsTopicArn());
                alertSender.sendAlertOnInstanceNotFound();
                logger.log("Instance not found");
                return;
            }

            logger.log("Creating image from the instance...");
            ImageManager imageMgr = new ImageManager(ec2Client, logger);
            imageMgr.createImage(instance);

            logger.log("OK");
            
        } catch (Throwable e) {
            logger.log("Error: " + e.getMessage());
            
        }

    }
}
