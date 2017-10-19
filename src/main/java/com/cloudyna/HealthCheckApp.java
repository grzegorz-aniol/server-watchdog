package com.cloudyna;

import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.AmazonEC2ClientBuilder;
import com.amazonaws.services.ec2.model.Instance;
import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.AmazonSNSClientBuilder;
import com.cloudyna.service.AlertSender;
import com.cloudyna.service.AppContext;
import com.cloudyna.service.HttpMonitor;
import com.cloudyna.service.MachineFinder;

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
        new HealthCheckApp().start();
    }
     
    public void start() {
        AppContext ctx = AppContext.INSTANCE;
        
        try {
           ctx.initialize();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.exit(-1);
        } 
        
        final AmazonEC2 ec2Client = AmazonEC2ClientBuilder.defaultClient();
        
        final MachineFinder finder = new MachineFinder(ec2Client);
        
        Instance instance = finder.find(ctx.getTagKey(),ctx.getTagValue());
        if (instance == null) {
            final AmazonSNS snsClient = AmazonSNSClientBuilder.defaultClient();
            final AlertSender alertSender = new AlertSender(snsClient,ctx.getSnsTopicArn());            
            alertSender.sendAlertOnInstanceNotFound();
            System.exit(-3);
        }
        
        HttpMonitor monitor = null;
        try {
            monitor = new HttpMonitor(ctx.getProtocol(), instance.getPublicIpAddress(), ctx.getPort(),ctx.getFilePath());
        } catch (MalformedURLException e) {
            System.out.println(e.getMessage());
            System.exit(-2);
        }
        
        System.out.println("Checking host...");
        if (!monitor.check()) {
            final AmazonSNS snsClient = AmazonSNSClientBuilder.defaultClient();
            final AlertSender alertSender = new AlertSender(snsClient, ctx.getSnsTopicArn());
            alertSender.sendAlertOnInstanceUnhealty();
            return;
        }
        System.out.println("OK");
        
    }
}
