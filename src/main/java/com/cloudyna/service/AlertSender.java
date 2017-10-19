package com.cloudyna.service;

import com.amazonaws.services.sns.AmazonSNS;

public class AlertSender {

    private final AmazonSNS snsClient;
    
    private final String topicArn;

    public AlertSender(final AmazonSNS snsClient, final String topicArn) {
        this.snsClient = snsClient;
        this.topicArn = topicArn; 
    }
    
    public String generateInstanceNotFoundMessage() {
        StringBuilder sb = new StringBuilder();
        sb.append("Can't find specific instance. TAG=")
            .append(AppContext.INSTANCE.getTagKey())
            .append(", VALUE=")
            .append(AppContext.INSTANCE.getTagValue());
        return sb.toString();
    }
    
    public String generateInstanceUnhealthyMessage() {
        StringBuilder sb = new StringBuilder();
        sb.append("Alert! EC2 machine is not responding. TAG=")
        .append(AppContext.INSTANCE.getTagKey())
        .append(", VALUE=")
        .append(AppContext.INSTANCE.getTagValue());
        return sb.toString();
        
    }

    public void sendAlertOnInstanceUnhealty() {
        String msg = generateInstanceUnhealthyMessage();
        System.out.println(msg);
        snsClient.publish(topicArn, msg);
    }

    public void sendAlertOnInstanceNotFound() {
        String msg = generateInstanceUnhealthyMessage();
        System.out.println(msg);
        snsClient.publish(topicArn, msg);
    }
    
}
