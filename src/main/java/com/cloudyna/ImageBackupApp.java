package com.cloudyna;

import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.AmazonEC2ClientBuilder;
import com.amazonaws.services.ec2.model.Instance;
import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.AmazonSNSClientBuilder;
import com.cloudyna.service.AlertSender;
import com.cloudyna.service.AppContext;
import com.cloudyna.service.ImageManager;
import com.cloudyna.service.MachineFinder;

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
        new ImageBackupApp().start();
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
        Instance instance = finder.find(ctx.getTagKey(), ctx.getTagValue());
        if (instance == null) {
            final AmazonSNS snsClient = AmazonSNSClientBuilder.defaultClient();
            final AlertSender alertSender = new AlertSender(snsClient,ctx.getSnsTopicArn());            
            alertSender.sendAlertOnInstanceNotFound();
            System.exit(-3);
        }

        System.out.println("Creating image from the instance...");
        ImageManager imageMgr = new ImageManager(ec2Client);
        imageMgr.createImage(instance);
        
        System.out.println("OK");
        
    }
}
