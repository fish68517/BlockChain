package com.collectorcoin.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO for ProjectListing entity.
 */
@Data
public class ListingDTO {

    private Long id;
    private String projectAddress;
    private String title;
    private String description;
    private String vin;
    private String imageUrl;
    private BigDecimal valueEstimation;
    private BigDecimal repairEstimation;
    private String status;
    private Long processId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // NFT fields
    private Long nftTokenId;
    private String nftMetadataUri;
    private Boolean nftMinted;
    private Boolean nftLaunched;
}
