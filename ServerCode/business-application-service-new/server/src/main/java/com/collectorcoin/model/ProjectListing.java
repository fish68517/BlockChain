package com.collectorcoin.model;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Entity representing a project listing in the CollectorCoin platform.
 */
@Data
@Entity
@Table(name = "project_listing")
public class ProjectListing {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "project_address")
    private String projectAddress;

    @Column(name = "title")
    private String title;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "vin")
    private String vin;

    @Column(name = "image_url", columnDefinition = "TEXT")
    private String imageUrl;

    @Column(name = "value_estimation", precision = 18, scale = 2)
    private BigDecimal valueEstimation;

    @Column(name = "repair_estimation", precision = 18, scale = 2)
    private BigDecimal repairEstimation;

    // ============ Owner Estimation (submitted by owner) ============

    @Column(name = "owner_value_estimation", precision = 18, scale = 2)
    private BigDecimal ownerValueEstimation;

    @Column(name = "owner_repair_estimation", precision = 18, scale = 2)
    private BigDecimal ownerRepairEstimation;

    // ============ Restorer Bid Fields ============

    @Column(name = "selected_restorer_id")
    private Long selectedRestorerId;

    @Column(name = "selected_restorer_address")
    private String selectedRestorerAddress;

    @Column(name = "selected_restorer_bid", precision = 18, scale = 2)
    private BigDecimal selectedRestorerBid;

    @Column(name = "funding_target", precision = 18, scale = 2)
    private BigDecimal fundingTarget;

    @Column(name = "current_funding", precision = 18, scale = 2)
    private BigDecimal currentFunding = BigDecimal.ZERO;

    @Column(name = "status")
    private String status;

    @Column(name = "process_id")
    private Long processId;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // ============ NFT Fields ============

    @Column(name = "nft_token_id")
    private Long nftTokenId;

    @Column(name = "nft_metadata_uri")
    private String nftMetadataUri;

    @Column(name = "nft_minted")
    private Boolean nftMinted = false;

    @Column(name = "nft_launched")
    private Boolean nftLaunched = false;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
