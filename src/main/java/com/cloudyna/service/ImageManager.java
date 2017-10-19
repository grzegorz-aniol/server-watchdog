package com.cloudyna.service;

import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.model.CreateImageRequest;
import com.amazonaws.services.ec2.model.CreateImageResult;
import com.amazonaws.services.ec2.model.CreateTagsRequest;
import com.amazonaws.services.ec2.model.DeleteSnapshotRequest;
import com.amazonaws.services.ec2.model.DeregisterImageRequest;
import com.amazonaws.services.ec2.model.DescribeImagesRequest;
import com.amazonaws.services.ec2.model.DescribeImagesResult;
import com.amazonaws.services.ec2.model.Filter;
import com.amazonaws.services.ec2.model.Image;
import com.amazonaws.services.ec2.model.Instance;
import com.amazonaws.services.ec2.model.Tag;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class ImageManager {

    private final AmazonEC2 ec2;
    private List<Image> images;

    
    public ImageManager(final AmazonEC2 ec2) {
        this.ec2 = ec2;        
    }    
    
    public void createImage(Instance instance) {
        fetchImages();
        if (images!=null && images.size() >= AppContext.INSTANCE.getMaxImagesCount()) {
            deleteImage(images.get(0));
        }
        createNewImage(instance.getInstanceId());
    }
    
    private boolean fetchImages(){
        DescribeImagesRequest request = new DescribeImagesRequest()
                .withOwners(AppContext.IMAGE_OWNER)
                .withFilters(new Filter("tag:" + AppContext.INSTANCE.getTagKey(), Arrays.asList(AppContext.INSTANCE.getTagValue())) );
        
        DescribeImagesResult result = ec2.describeImages(request);
        images = result.getImages();
        if (images == null) {
            return false; 
        }        
        images = images.stream()
            .sorted((i1,i2) -> i1.getCreationDate().compareTo(i2.getCreationDate())) // Comparing as strings! But first should be the most old
            .collect(Collectors.toList());
        return (images != null && images.size() > 0);
    }
    
    private void createNewImage(String instanceId) {
        System.out.println(" > creating new image");
        
        AppContext ctx = AppContext.INSTANCE;
        
        CreateImageRequest request = new CreateImageRequest()
                .withInstanceId(instanceId)
                .withName(AppContext.IMAGE_TAG + Long.toHexString(UUID.randomUUID().getLeastSignificantBits()))
                .withNoReboot(true);
        CreateImageResult response = ec2.createImage(request);
        String imageId = response.getImageId();
        System.out.println(" > image created: " + imageId);

        CreateTagsRequest tagRequest = new CreateTagsRequest()
                .withResources(imageId)
                .withTags(new Tag(ctx.getTagKey(), ctx.getTagValue()));
        ec2.createTags(tagRequest);
        System.out.println(" > image has beed tagged");
    }
    
    private void deleteImage(Image image) {
        
        List<String> snapshotIds = image.getBlockDeviceMappings().stream()
            .map(d -> d.getEbs().getSnapshotId())
            .collect(Collectors.toList());
        
        // unregister image
        System.out.println(" > unregistering an old image: " + image.getImageId());
        ec2.deregisterImage(new DeregisterImageRequest().withImageId(image.getImageId()));
        
        // delete snapshot
        snapshotIds.forEach( id -> {
            System.out.println(" > removing snapshot for removed image : " + id);
            DeleteSnapshotRequest delSnapshotRequest = new DeleteSnapshotRequest()
                    .withSnapshotId(id);
            ec2.deleteSnapshot(delSnapshotRequest);
        });
        
    }
}
