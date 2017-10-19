package com.cloudyna;

import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.AmazonEC2ClientBuilder;
import com.amazonaws.services.ec2.model.Instance;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.AmazonSNSClientBuilder;
import com.cloudyna.service.AlertSender;
import com.cloudyna.service.AppContext;
import com.cloudyna.service.HttpMonitor;
import com.cloudyna.service.Logger;
import com.cloudyna.service.MachineFinder;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;

/**
 * Monitor activity of an application on EC2 instance.
 * 
 * @See {@link AppContext} for mandatory and optional environment variables that need to be specified
 * 
 * @author Grzegorz_Aniol
 *
 */
public class HealthCheckApp {

    public static void main(String[] args) {
        new HealthCheckApp().handler(null, null, null);
    }

    public void handler(InputStream inputStream, OutputStream outputStream, Context context) {
        Logger logger = new Logger(context);

        try {
            logger.log("Starting HealthCheckApp.handler...");
            AppContext ctx = AppContext.INSTANCE;

            try {
                ctx.initialize();
            } catch (Exception e) {
                logger.log(e.getMessage());
                return;
            }

            logger.log("Starting EC2 client");
            AmazonEC2 ec2Client = AmazonEC2ClientBuilder.defaultClient();

            logger.log("Finding machine");
            final MachineFinder finder = new MachineFinder(ec2Client);

            Instance instance = finder.find(ctx.getTagKey(), ctx.getTagValue());
            if (instance == null) {
                final AmazonSNS snsClient = AmazonSNSClientBuilder.defaultClient();
                final AlertSender alertSender = new AlertSender(snsClient, ctx.getSnsTopicArn());
                alertSender.sendAlertOnInstanceNotFound();
                logger.log("Instance not found!");
                return;
            }

            HttpMonitor monitor = null;
            try {
                monitor = new HttpMonitor(ctx.getProtocol(), instance.getPublicIpAddress(),
                        ctx.getPort(), ctx.getFilePath());
            } catch (MalformedURLException e) {
                logger.log(e.getMessage());
                return;
            }

            logger.log("Checking host...");
            if (!monitor.check()) {
                final AmazonSNS snsClient = AmazonSNSClientBuilder.defaultClient();
                final AlertSender alertSender = new AlertSender(snsClient, ctx.getSnsTopicArn());
                alertSender.sendAlertOnInstanceUnhealty();
                return;
            }

            logger.log("OK");
            
        } catch (Throwable e) {
            logger.log("Error:" + e.getMessage());
            
        }

    }
}
