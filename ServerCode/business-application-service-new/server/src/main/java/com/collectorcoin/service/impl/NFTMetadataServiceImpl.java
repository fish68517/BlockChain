package com.collectorcoin.service.impl;

import com.collectorcoin.model.Attribute;
import com.collectorcoin.model.NFTMetadata;
import com.collectorcoin.service.IPFSService;
import com.collectorcoin.service.NFTMetadataService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

@Service
public class NFTMetadataServiceImpl implements NFTMetadataService {

    private static final Logger log = LoggerFactory.getLogger(NFTMetadataServiceImpl.class);

    private final IPFSService ipfsService;
    private final ObjectMapper objectMapper;

    @Value("${nft.metadata.external.base.url:https://collectorcoin.com/listing/}")
    private String externalBaseUrl;

    public NFTMetadataServiceImpl(IPFSService ipfsService) {
        this.ipfsService = ipfsService;
        this.objectMapper = new ObjectMapper();
        this.objectMapper.setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);
    }

    @Override
    public NFTMetadata createMetadata(
            String name,
            String description,
            String imageUrl,
            BigInteger valueEstimation,
            BigInteger repairEstimation,
            String status,
            Long listingId
    ) {
        NFTMetadata metadata = new NFTMetadata(name, description, imageUrl);
        metadata.setExternalUrl(externalBaseUrl + listingId);

        List<Attribute> attributes = new ArrayList<>();
        attributes.add(new Attribute("Value Estimation", valueEstimation.toString(), "number"));
        attributes.add(new Attribute("Repair Estimation", repairEstimation.toString(), "number"));
        attributes.add(new Attribute("Status", status));
        attributes.add(new Attribute("Listing ID", listingId.toString()));

        metadata.setAttributes(attributes);
        return metadata;
    }

    @Override
    public String uploadToIPFS(NFTMetadata metadata) {
        try {
            String json = objectMapper.writeValueAsString(metadata);
            String cid = ipfsService.uploadJson(json);
            String uri = "ipfs://" + cid;
            log.info("Uploaded NFT metadata to IPFS: {}", uri);
            return uri;
        } catch (Exception e) {
            log.error("Failed to upload metadata to IPFS", e);
            throw new RuntimeException("Failed to upload metadata", e);
        }
    }

    @Override
    public String createAndUpload(
            String name,
            String description,
            String imageUrl,
            BigInteger valueEstimation,
            BigInteger repairEstimation,
            String status,
            Long listingId
    ) {
        NFTMetadata metadata = createMetadata(
                name, description, imageUrl,
                valueEstimation, repairEstimation, status, listingId
        );
        return uploadToIPFS(metadata);
    }
}
