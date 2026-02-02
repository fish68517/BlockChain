package com.collectorcoin.controller;

import com.collectorcoin.dto.*;
import com.collectorcoin.model.AuctionBid;
import com.collectorcoin.service.AuctionService;
import com.collectorcoin.service.InvestmentProxyService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * REST Controller for auction operations.
 */
@RestController
@RequestMapping("/api")
public class AuctionController {

    private static final Logger logger = LoggerFactory.getLogger(AuctionController.class);

    private final InvestmentProxyService investmentProxyService;
    private final AuctionService auctionService;

    public AuctionController(InvestmentProxyService investmentProxyService, AuctionService auctionService) {
        this.investmentProxyService = investmentProxyService;
        this.auctionService = auctionService;
    }

    @GetMapping("/auction-bids")
    public ResponseEntity<List<AuctionBid>> getAllBids() {
        return ResponseEntity.ok(auctionService.getAllBids());
    }

    @GetMapping("/auction-bids/buyer/{buyerId}")
    public ResponseEntity<List<AuctionBid>> getBidsByBuyer(@PathVariable Long buyerId) {
        return ResponseEntity.ok(auctionService.getBidsByBuyerId(buyerId));
    }

    @GetMapping("/auction-bids/listing/{listingId}")
    public ResponseEntity<List<AuctionBid>> getBidsByListing(@PathVariable Long listingId) {
        return ResponseEntity.ok(auctionService.getBidsByListingId(listingId));
    }

    @PostMapping("/auction-bids")
    public ResponseEntity<?> createBid(@RequestBody Map<String, Object> request) {
        try {
            Long buyerId = ((Number) request.get("buyerId")).longValue();
            Long listingId = ((Number) request.get("listingId")).longValue();
            BigDecimal bidAmount = new BigDecimal(request.get("bidAmount").toString());
            String transactionHash = (String) request.get("transactionHash");

            AuctionBid bid = auctionService.createBid(buyerId, listingId, bidAmount, transactionHash);
            return ResponseEntity.ok(bid);
        } catch (Exception e) {
            logger.error("Error creating bid", e);
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/auction-bids/{bidId}")
    public ResponseEntity<?> updateBid(@PathVariable Long bidId, @RequestBody Map<String, Object> request) {
        try {
            BigDecimal newAmount = new BigDecimal(request.get("bidAmount").toString());
            AuctionBid bid = auctionService.updateBidAmount(bidId, newAmount);
            return ResponseEntity.ok(bid);
        } catch (Exception e) {
            logger.error("Error updating bid", e);
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/auction-bids/{bidId}/select")
    public ResponseEntity<?> selectBid(@PathVariable Long bidId) {
        try {
            AuctionBid bid = auctionService.selectBid(bidId);
            return ResponseEntity.ok(bid);
        } catch (Exception e) {
            logger.error("Error selecting bid", e);
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/listings/{listingId}/post-auction")
    public ResponseEntity<Map<String, String>> postAuction(
            @PathVariable Long listingId,
            @Valid @RequestBody AuctionRequest request) {
        logger.info("Posting auction for listing {}", listingId);

        investmentProxyService.postForAuction(
            listingId,
            request.getAuctionPrice()
        );

        return ResponseEntity.ok(Map.of(
            "status", "success",
            "message", "Item posted for auction"
        ));
    }

    @PostMapping("/listings/{listingId}/finalize")
    public ResponseEntity<Map<String, String>> finalizeAuction(
            @PathVariable Long listingId,
            @Valid @RequestBody FinalizeAuctionRequest request) {
        logger.info("Finalizing auction for listing {}", listingId);

        investmentProxyService.transferToWinner(
            listingId,
            request.getWinnerAddress()
        );

        return ResponseEntity.ok(Map.of(
            "status", "success",
            "message", "Auction finalized, NFT transferred"
        ));
    }
}
