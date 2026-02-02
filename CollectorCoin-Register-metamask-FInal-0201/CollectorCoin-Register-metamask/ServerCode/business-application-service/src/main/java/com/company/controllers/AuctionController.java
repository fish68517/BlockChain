package com.company.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import java.util.List;
import com.company.models.AuctionBid;
import com.company.services.AuctionService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.beans.factory.annotation.Autowired;
import com.company.models.ProjectListing;

@RestController
@RequestMapping("/api")
public class AuctionController {
  @Autowired
  private AuctionService auctionService;

  @GetMapping("/buyer/{buyerId}/auctionBidListings")
  public ResponseEntity<List<AuctionBid>> getAuctionBidListingByBuyerId(@PathVariable(value = "buyerId") Long buyerId) {
    List<AuctionBid> auctions = auctionService.getAuctionBidListingByBuyerId(buyerId);
    return new ResponseEntity<>(auctions, HttpStatus.OK);
  }

  @GetMapping("/auctionBidListings")
  public ResponseEntity<List<AuctionBid>> getAllAuctions() {
    List<AuctionBid> auctions = auctionService.getAllAuctions();
    return new ResponseEntity<>(auctions, HttpStatus.OK);
  }

  @PostMapping("/buyer/{buyerId}/auctionBid")
  public ResponseEntity<AuctionBid> createBid(
      @PathVariable(value = "buyerId") Long buyerId,
      @RequestBody AuctionBid request) {

    AuctionBid auctionBid = auctionService.createBid(buyerId, request);
    return new ResponseEntity<>(auctionBid, HttpStatus.OK);
  }

  @PutMapping("/auctionBid/{bidId}")
  public ResponseEntity<AuctionBid> updateAuctionBid(
      @PathVariable(value = "bidId") Long bidId,
      @RequestBody AuctionBid request) {
    AuctionBid auctionBid = auctionService.updateBid(bidId, request);
    return new ResponseEntity<>(auctionBid, HttpStatus.OK);
  }

  @PostMapping("/auctionBidding/{bidId}/select")
  public ResponseEntity<ProjectListing> selectBiddingForListing(@PathVariable(value = "bidId") Long bidId) {
    ProjectListing listing = auctionService.selectBiddingForListing(bidId);
    return new ResponseEntity<>(listing, HttpStatus.OK);
  }
}
