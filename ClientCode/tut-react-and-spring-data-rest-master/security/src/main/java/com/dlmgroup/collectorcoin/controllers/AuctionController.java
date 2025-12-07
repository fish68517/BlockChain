package com.dlmgroup.collectorcoin.controllers;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.dlmgroup.collectorcoin.exception.ResourceNotFoundException;
import com.dlmgroup.collectorcoin.models.AuctionBid;
import com.dlmgroup.collectorcoin.models.ProjectListing;
import com.dlmgroup.collectorcoin.repositories.ProjectListingRepository;
import com.dlmgroup.collectorcoin.models.ProcessResponse;
import com.dlmgroup.collectorcoin.repositories.UserRepository;
import com.dlmgroup.collectorcoin.services.ProcessListingService;
import com.dlmgroup.collectorcoin.services.WebSocketService;
import com.dlmgroup.collectorcoin.repositories.AuctionBidRepository;

import com.dlmgroup.collectorcoin.jbpm.CollectorCoinjBPMProcessClientAPI;

@RestController
@RequestMapping("/api")
public class AuctionController {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProjectListingRepository projectListingRepository;

    @Autowired
    private AuctionBidRepository auctionBiddingRepository;

    @Autowired
    private ProcessListingService processListingService;

    @Autowired
    private WebSocketService webSocketService;

    private CollectorCoinjBPMProcessClientAPI jBPMProcessClient = new CollectorCoinjBPMProcessClientAPI(); // ADDED

    // Get all project listings by User ID
    @GetMapping("/buyer/{buyerId}/auctionBidListings")
    public ResponseEntity<List<AuctionBid>> getAuctionBidListingByBuyerId(@PathVariable(value = "buyerId") Long buyerId) {
        if(!userRepository.existsById(buyerId)) {
            throw new ResourceNotFoundException("No project listings found with buyer ID = " + buyerId);
        }

        List<AuctionBid> bids = auctionBiddingRepository.findByBuyerId(buyerId);
        return new ResponseEntity<>(bids, HttpStatus.OK);
    }

    // Get all project listings
    @GetMapping("/auctionBidListings")
    public ResponseEntity<List<AuctionBid>> getAllAuctionBids() {
        List<AuctionBid> bids = auctionBiddingRepository.findAll();
        return new ResponseEntity<>(bids, HttpStatus.OK);
    }

    @PostMapping("/buyer/{buyerId}/auctionBid")
    public ResponseEntity<AuctionBid> createBid(
      @PathVariable(value = "buyerId") Long buyerId,
      @RequestBody AuctionBid request
    ) {
        request.setBuyerId(buyerId);
        request = auctionBiddingRepository.save(request);
        //webSocketService.sendAuctionBidCreated(request);
        return new ResponseEntity<>(request, HttpStatus.OK);
    }

    @PutMapping("/auctionBid/{bidId}")
    public ResponseEntity<AuctionBid> updateAuctionBid(
      @PathVariable(value = "bidId") Long bidId,
      @RequestBody AuctionBid request
    ) {
      AuctionBid bid = auctionBiddingRepository.findById(bidId)
        .orElseThrow(() -> new ResourceNotFoundException("No auction bid found with ID = " + bidId));

        Float newPrice = request.getBuyerPrice();
        if (newPrice != null) {
          bid.setBuyerPrice(newPrice);
        }
        bid = auctionBiddingRepository.save(bid);
        //webSocketService.sendAuctionBidUpdated(bid);
        return new ResponseEntity<>(bid, HttpStatus.OK);
    }

    @PostMapping("/auctionBidding/{bidId}/select")
    public ResponseEntity<ProjectListing> selectBiddingForListing(@PathVariable(value = "bidId") Long bidId) {
      AuctionBid bid = auctionBiddingRepository.findById(bidId)
        .orElseThrow(() -> new ResourceNotFoundException("No buy bid found with ID = " + bidId));

      Long listingId = bid.getListingId();
      ProjectListing listing = projectListingRepository.findById(listingId)
        .orElseThrow(() -> new ResourceNotFoundException("No listing found with ID = " + listingId));

      bid.setIsSelected(true);
      bid = auctionBiddingRepository.save(bid);

      listing.setAuctionPrice(bid.getBuyerPrice());
      listing.setIsAuctionAccept(true);
      listing.setBuyerId(bid.getBuyerId());

      List<ProcessResponse> tasks = jBPMProcessClient.selectAuctionBid(listing);
      List<String> taskNames = processListingService.convertProcessResponseToString(tasks);

      listing.setPendingTasks(taskNames);
      listing = projectListingRepository.save(listing);

      webSocketService.sendListingUpdated(listing);
      webSocketService.sendAuctionBidApproved(bid);

      listing.setIsRedistributed(true);
      
      tasks = jBPMProcessClient.redistribute(listing);
      taskNames = processListingService.convertProcessResponseToString(tasks);

      listing.setPendingTasks(taskNames);
      listing = projectListingRepository.save(listing);

      webSocketService.sendListingUpdated(listing);

      return new ResponseEntity<>(listing, HttpStatus.OK);
    }
}
