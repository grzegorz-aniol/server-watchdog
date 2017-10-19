package com.cloudyna.service;

import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.model.DescribeInstancesRequest;
import com.amazonaws.services.ec2.model.DescribeInstancesResult;
import com.amazonaws.services.ec2.model.Instance;
import com.amazonaws.services.ec2.model.Reservation;
import com.amazonaws.services.ec2.model.Tag;

import java.util.Optional;

public class MachineFinder {

    private final AmazonEC2 ec2;
    
    public MachineFinder(final AmazonEC2 ec2) {
        this.ec2 = ec2;        
    }
    
    public Instance find(final String tagKey, final String tagValue) {
        
        while(true) {
            DescribeInstancesRequest request = new DescribeInstancesRequest();
            DescribeInstancesResult response = ec2.describeInstances(request);

            for(Reservation reservation : response.getReservations()) {
                for(Instance instance : reservation.getInstances()) {
                    
                    Optional<Tag> tag = instance.getTags().stream()
                        .filter(t -> t.getKey().equals(tagKey))
                        .filter(t -> t.getValue().equals(tagValue))
                        .findFirst();
                    
                    if (tag.isPresent()) {
                        return instance;
                    }
                }
            }

            request.setNextToken(response.getNextToken());

            if(response.getNextToken() == null) {
                return null; 
            }
        }
        
    }
}
