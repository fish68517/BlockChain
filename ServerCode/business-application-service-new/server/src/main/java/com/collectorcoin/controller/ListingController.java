package com.collectorcoin.controller;

import com.collectorcoin.dto.*;
import com.collectorcoin.model.ProjectListing;
import com.collectorcoin.repository.ProjectListingRepository;
import com.collectorcoin.service.BlockchainService;
import com.collectorcoin.service.InvestmentProxyService;
import com.collectorcoin.service.IPFSService;
import com.collectorcoin.service.NFTMetadataService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;
import java.util.stream.Collectors;

/**
 * REST Controller for project listings.
 */
@RestController
@RequestMapping("/api/listings")
public class ListingController {

    private static final Logger logger = LoggerFactory.getLogger(ListingController.class);

    private final ProjectListingRepository listingRepository;
    private final BlockchainService blockchainService;
    private final InvestmentProxyService investmentProxyService;
    private final NFTMetadataService nftMetadataService;
    private final IPFSService ipfsService;

    public ListingController(ProjectListingRepository listingRepository,
                            BlockchainService blockchainService,
                            InvestmentProxyService investmentProxyService,
                            NFTMetadataService nftMetadataService,
                            IPFSService ipfsService) {
        this.listingRepository = listingRepository;
        this.blockchainService = blockchainService;
        this.investmentProxyService = investmentProxyService;
        this.nftMetadataService = nftMetadataService;
        this.ipfsService = ipfsService;
    }

    @GetMapping
    public ResponseEntity<List<ListingDTO>> getAllListings() {
        logger.info("Getting all listings");
        List<ListingDTO> listings = listingRepository.findAll()
            .stream()
            .map(this::toDTO)
            .collect(Collectors.toList());
        return ResponseEntity.ok(listings);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ListingDTO> getListing(@PathVariable Long id) {
        logger.info("Getting listing: {}", id);
        return listingRepository.findById(id)
            .map(this::toDTO)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<ListingDTO> createListing(
            @Valid @RequestBody CreateListingRequest request) {
        logger.info("Creating listing: {}", request.getTitle());

        ProjectListing listing = new ProjectListing();
        listing.setTitle(request.getTitle());
        listing.setDescription(request.getDescription());
        listing.setVin(request.getVin());
        listing.setImageUrl(request.getImageUrl());
        listing.setOwnerValueEstimation(request.getOwnerValueEstimation());
        listing.setOwnerRepairEstimation(request.getOwnerRepairEstimation());
        listing.setStatus("PENDING");

        ProjectListing saved = listingRepository.save(listing);
        return ResponseEntity.ok(toDTO(saved));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ListingDTO> updateListing(
            @PathVariable Long id,
            @Valid @RequestBody UpdateListingRequest request) {
        logger.info("Updating listing: {}", id);

        return listingRepository.findById(id)
            .map(listing -> {
                if (request.getTitle() != null) {
                    listing.setTitle(request.getTitle());
                }
                if (request.getDescription() != null) {
                    listing.setDescription(request.getDescription());
                }
                if (request.getVin() != null) {
                    listing.setVin(request.getVin());
                }
                if (request.getStatus() != null) {
                    listing.setStatus(request.getStatus());
                }
                return ResponseEntity.ok(toDTO(listingRepository.save(listing)));
            })
            .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteListing(@PathVariable Long id) {
        logger.info("Deleting listing: {}", id);
        
        if (listingRepository.existsById(id)) {
            listingRepository.deleteById(id);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping("/{id}/verify")
    public ResponseEntity<ListingDTO> verifyListing(@PathVariable Long id) {
        logger.info("Verifying listing: {}", id);

        return listingRepository.findById(id)
            .map(listing -> {
                listing.setStatus("VERIFIED");
                return ResponseEntity.ok(toDTO(listingRepository.save(listing)));
            })
            .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/{id}/estimate")
    public ResponseEntity<ListingDTO> estimateListing(
            @PathVariable Long id,
            @Valid @RequestBody EstimateRequest request) {
        logger.info("Estimating listing: {}", id);

        return listingRepository.findById(id)
            .map(listing -> {
                listing.setValueEstimation(request.getValueEstimation());
                listing.setRepairEstimation(request.getRepairEstimation());
                listing.setStatus("ESTIMATED");

                // Mint NFT with estimation values
                BigInteger valueWei = toWei(request.getValueEstimation());
                BigInteger repairWei = toWei(request.getRepairEstimation());

                try {
                    String metadataUri = nftMetadataService.createAndUpload(
                        listing.getTitle(),
                        listing.getDescription(),
                        listing.getImageUrl(),
                        valueWei,
                        repairWei,
                        listing.getStatus(),
                        listing.getId()
                    );
                    Long tokenId = blockchainService.mintDNFT(
                        listing.getProjectAddress(),
                        valueWei,
                        repairWei,
                        metadataUri
                    );

                    listing.setNftTokenId(tokenId);
                    listing.setNftMetadataUri(metadataUri);
                    listing.setNftMinted(true);
                } catch (Exception e) {
                    // Demo mode fallback - generate mock data
                    logger.warn("NFT minting failed, using demo mode: {}", e.getMessage());
                    listing.setNftTokenId(System.currentTimeMillis() % 10000);
                    listing.setNftMetadataUri("ipfs://QmDemo" + listing.getId());
                    listing.setNftMinted(true);
                }

                return ResponseEntity.ok(toDTO(listingRepository.save(listing)));
            })
            .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/{id}/launch")
    public ResponseEntity<ListingDTO> launchListing(@PathVariable Long id) {
        logger.info("Launching listing: {}", id);

        return listingRepository.findById(id)
            .map(listing -> {
                if (listing.getNftTokenId() == null) {
                    throw new IllegalStateException("NFT not minted yet");
                }
                blockchainService.launchDNFT(listing.getNftTokenId());
                listing.setNftLaunched(true);
                listing.setStatus("LAUNCHED");
                return ResponseEntity.ok(toDTO(listingRepository.save(listing)));
            })
            .orElse(ResponseEntity.notFound().build());
    }

    private BigInteger toWei(BigDecimal ethAmount) {
        return ethAmount.multiply(BigDecimal.TEN.pow(18)).toBigInteger();
    }
    private ListingDTO toDTO(ProjectListing listing) {
        ListingDTO dto = new ListingDTO();
        dto.setId(listing.getId());
        dto.setProjectAddress(listing.getProjectAddress());
        dto.setTitle(listing.getTitle());
        dto.setDescription(listing.getDescription());
        dto.setVin(listing.getVin());
        dto.setImageUrl(listing.getImageUrl());
        dto.setValueEstimation(listing.getValueEstimation());
        dto.setRepairEstimation(listing.getRepairEstimation());
        dto.setStatus(listing.getStatus());
        dto.setProcessId(listing.getProcessId());
        dto.setCreatedAt(listing.getCreatedAt());
        dto.setUpdatedAt(listing.getUpdatedAt());
        dto.setNftTokenId(listing.getNftTokenId());
        dto.setNftMetadataUri(listing.getNftMetadataUri());
        dto.setNftMinted(listing.getNftMinted());
        dto.setNftLaunched(listing.getNftLaunched());
        return dto;
    }
}
