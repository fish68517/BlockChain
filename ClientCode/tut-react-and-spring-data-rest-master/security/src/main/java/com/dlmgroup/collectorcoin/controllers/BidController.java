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
import com.dlmgroup.collectorcoin.models.Bidding;
import com.dlmgroup.collectorcoin.models.ProjectListing;
import com.dlmgroup.collectorcoin.repositories.ProjectListingRepository;
import com.dlmgroup.collectorcoin.models.ProcessResponse;
import com.dlmgroup.collectorcoin.repositories.UserRepository;
import com.dlmgroup.collectorcoin.services.ProcessListingService;
import com.dlmgroup.collectorcoin.services.WebSocketService;
import com.dlmgroup.collectorcoin.repositories.BiddingRepository;

import com.dlmgroup.collectorcoin.jbpm.CollectorCoinjBPMProcessClientAPI;

@RestController
@RequestMapping("/api")
public class BidController {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProjectListingRepository projectListingRepository;

    @Autowired
    private BiddingRepository biddingRepository;

    @Autowired
    private ProcessListingService processListingService;

    @Autowired
    private WebSocketService webSocketService;

    private CollectorCoinjBPMProcessClientAPI jBPMProcessClient = new CollectorCoinjBPMProcessClientAPI(); // ADDED

    // Get all project listings by User ID
    @GetMapping("/restorer/{bidderId}/bidListings")
    public ResponseEntity<List<Bidding>> getBidListingByBidderId(@PathVariable(value = "bidderId") Long bidderId) {
        if(!userRepository.existsById(bidderId)) {
            throw new ResourceNotFoundException("No project listings found with bidder ID = " + bidderId);
        }

        List<Bidding> bids = biddingRepository.findByBidderId(bidderId);
        return new ResponseEntity<>(bids, HttpStatus.OK);
    }

    // Get all project listings
    @GetMapping("/bidListings")
    public ResponseEntity<List<Bidding>> getAllBids() {
        List<Bidding> bids = biddingRepository.findAll();
        return new ResponseEntity<>(bids, HttpStatus.OK);
    }

    @PostMapping("/restorer/{bidderId}/bid")
    public ResponseEntity<Bidding> createBid(
      @PathVariable(value = "bidderId") Long bidderId,
      @RequestBody Bidding request
    ) {
        request.setBidderId(bidderId);
        request.setCategory("car");
        request = biddingRepository.save(request);
        webSocketService.sendBidCreated(request);
        return new ResponseEntity<>(request, HttpStatus.OK);
    }

    @PutMapping("/bid/{bidId}")
    public ResponseEntity<Bidding> updateBid(
      @PathVariable(value = "bidId") Long bidId,
      @RequestBody Bidding request
    ) {
      Bidding bid = biddingRepository.findById(bidId)
        .orElseThrow(() -> new ResourceNotFoundException("No bid found with ID = " + bidId));

        Float newPrice = request.getBiddingPrice();
        if (newPrice != null) {
          bid.setBiddingPrice(newPrice);
        }
        String newCatefory = request.getCategory();
        if (newCatefory != null) {
          bid.setCategory(newCatefory);
        }
        bid = biddingRepository.save(bid);
        webSocketService.sendBidUpdated(bid);
        return new ResponseEntity<>(bid, HttpStatus.OK);
    }

    @PostMapping("/bidding/{bidId}/select")
    public ResponseEntity<ProjectListing> selectBiddingForListing(@PathVariable(value = "bidId") Long bidId) {
      Bidding bid = biddingRepository.findById(bidId)
        .orElseThrow(() -> new ResourceNotFoundException("No bid found with ID = " + bidId));

      Long listingId = bid.getListingId();
      ProjectListing listing = projectListingRepository.findById(listingId)
        .orElseThrow(() -> new ResourceNotFoundException("No listing found with ID = " + listingId));

      bid.setIsSelected(true);
      bid = biddingRepository.save(bid);

      listing.setBidPrice(bid.getBiddingPrice());
      listing.setIsBidSuccess(true);
      listing.setRestorerId(bid.getBidderId());
      listing.setFundingGoal(bid.getBiddingPrice());

      listing = projectListingRepository.save(listing);

      List<ProcessResponse> tasks = jBPMProcessClient.selectBid(listing);
      List<String> taskNames = processListingService.convertProcessResponseToString(tasks);

      //System.out.println("TASKS AFTER BID SELECTION");
      //System.out.println(taskNames);
      listing.setPendingTasks(taskNames);
      listing = projectListingRepository.save(listing);

      webSocketService.sendListingUpdated(listing);
      webSocketService.sendBidApproved(bid);

      return new ResponseEntity<>(listing, HttpStatus.OK);
    }
}
