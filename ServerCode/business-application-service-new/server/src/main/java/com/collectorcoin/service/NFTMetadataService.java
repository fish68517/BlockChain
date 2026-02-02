package com.collectorcoin.service;

import com.collectorcoin.model.NFTMetadata;

import java.math.BigInteger;

/**
 * Service for creating and managing NFT metadata
 */
public interface NFTMetadataService {

    /**
     * Create metadata for a collectible item
     */
    NFTMetadata createMetadata(
            String name,
            String description,
            String imageUrl,
            BigInteger valueEstimation,
            BigInteger repairEstimation,
            String status,
            Long listingId
    );

    /**
     * Upload metadata to IPFS and return URI
     */
    String uploadToIPFS(NFTMetadata metadata);

    /**
     * Create and upload metadata in one step
     */
    String createAndUpload(
            String name,
            String description,
            String imageUrl,
            BigInteger valueEstimation,
            BigInteger repairEstimation,
            String status,
            Long listingId
    );
}
