package com.company.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;
import com.company.services.BidService;
import com.company.models.Bidding;
import com.company.models.ProjectListing;
import java.util.List;

@RestController
@RequestMapping("/api")
public class BIdController {

  @Autowired
  private BidService bidService;

  @GetMapping("/restorer/{bidderId}/bidListings")
  public ResponseEntity<List<Bidding>> getBidListingByBidderId(@PathVariable(value = "bidderId") Long bidderId) {
    List<Bidding> bids = bidService.getBidListingByBidderId(bidderId);
    return ResponseEntity.ok(bids);
  }

  @GetMapping("/bidListings")
  public ResponseEntity<List<Bidding>> getAllBids() {
    List<Bidding> bids = bidService.getAllBids();
    return ResponseEntity.ok(bids);
  }

  @PostMapping("/restorer/{bidderId}/bid")
  public ResponseEntity<Bidding> createBid(
      @PathVariable(value = "bidderId") Long bidderId,
      @RequestBody Bidding request) {
    Bidding bid = bidService.createBid(bidderId, request);
    return ResponseEntity.ok(bid);
  }

  @PutMapping("/bid/{bidId}")
  public ResponseEntity<Bidding> updateBid(
      @PathVariable(value = "bidId") Long bidId,
      @RequestBody Bidding request) {
    Bidding bid = bidService.updateBid(bidId, request);
    return ResponseEntity.ok(bid);
  }

  @PostMapping("/bidding/{bidId}/select")
  public ResponseEntity<ProjectListing> selectBiddingForListing(@PathVariable(value = "bidId") Long bidId) {
    ProjectListing listing = bidService.selectBiddingForListing(bidId);
    return ResponseEntity.ok(listing);
  }

}
