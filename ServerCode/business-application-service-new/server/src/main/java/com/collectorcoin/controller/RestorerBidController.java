package com.collectorcoin.controller;

import com.collectorcoin.model.RestorerBid;
import com.collectorcoin.model.ProjectListing;
import com.collectorcoin.repository.RestorerBidRepository;
import com.collectorcoin.repository.ProjectListingRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/restorer-bids")
public class RestorerBidController {

    private static final Logger logger = LoggerFactory.getLogger(RestorerBidController.class);

    @Autowired
    private RestorerBidRepository restorerBidRepository;

    @Autowired
    private ProjectListingRepository listingRepository;

    /**
     * Get all bids for a listing (sorted by amount ascending)
     */
    @GetMapping("/listing/{listingId}")
    public ResponseEntity<List<RestorerBid>> getBidsByListing(@PathVariable Long listingId) {
        return ResponseEntity.ok(restorerBidRepository.findByListingIdOrderByBidAmountAsc(listingId));
    }

    /**
     * Submit a bid as a restorer
     */
    @PostMapping
    public ResponseEntity<?> submitBid(@RequestBody Map<String, Object> request) {
        Long listingId = ((Number) request.get("listingId")).longValue();
        Long restorerId = ((Number) request.get("restorerId")).longValue();
        String restorerAddress = (String) request.get("restorerAddress");
        BigDecimal bidAmount = new BigDecimal(request.get("bidAmount").toString());

        logger.info("Restorer {} submitting bid {} for listing {}", restorerId, bidAmount, listingId);

        // Verify listing exists and is in correct status
        ProjectListing listing = listingRepository.findById(listingId).orElse(null);
        if (listing == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "Listing not found"));
        }
        if (!"LAUNCHED".equals(listing.getStatus())) {
            return ResponseEntity.badRequest().body(Map.of("error", "Listing not open for bids"));
        }

        RestorerBid bid = new RestorerBid();
        bid.setListingId(listingId);
        bid.setRestorerId(restorerId);
        bid.setRestorerAddress(restorerAddress);
        bid.setBidAmount(bidAmount);
        bid.setStatus("PENDING");

        RestorerBid saved = restorerBidRepository.save(bid);
        return ResponseEntity.ok(saved);
    }

    /**
     * Admin selects a restorer (lowest bid)
     */
    @PostMapping("/select/{bidId}")
    public ResponseEntity<?> selectBid(@PathVariable Long bidId) {
        RestorerBid bid = restorerBidRepository.findById(bidId).orElse(null);
        if (bid == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "Bid not found"));
        }

        ProjectListing listing = listingRepository.findById(bid.getListingId()).orElse(null);
        if (listing == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "Listing not found"));
        }

        logger.info("Selecting bid  for listing {}", bidId, listing.getId());

        // Update bid status
        bid.setStatus("SELECTED");
        restorerBidRepository.save(bid);

        // Reject other bids
        List<RestorerBid> otherBids = restorerBidRepository.findByListingIdAndStatus(listing.getId(), "PENDING");
        for (RestorerBid other : otherBids) {
            if (!other.getId().equals(bidId)) {
                other.setStatus("REJECTED");
                restorerBidRepository.save(other);
            }
        }

        // Update listing with selected restorer info
        listing.setSelectedRestorerId(bid.getRestorerId());
        listing.setSelectedRestorerAddress(bid.getRestorerAddress());
        listing.setSelectedRestorerBid(bid.getBidAmount());
        listing.setFundingTarget(bid.getBidAmount());
        listing.setStatus("FUNDING");
        listingRepository.save(listing);

        return ResponseEntity.ok(Map.of(
            "message", "Restorer selected",
            "selectedBid", bid,
            "fundingTarget", bid.getBidAmount()
        ));
    }
}
