package com.company.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.company.repositories.BiddingRepository;
import com.company.models.Bidding;
import com.company.exception.ResourceNotFoundException;
import java.util.List;
import com.company.models.ProjectListing;
import com.company.repositories.ProjectListingRepository;
import java.util.Map;
import java.util.HashMap;
import com.company.utils.ProcessUtility;
import com.company.enums.ProcessTaskNamesEnum;
import com.company.services.ProcessListingService;
import com.company.ServerApplication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.company.models.ProcessResponse;
import com.company.services.BlockchainService;
import java.math.BigInteger;
import java.math.BigDecimal;

@Service
public class BidService {
  private static final Logger logger = LoggerFactory.getLogger(ServerApplication.class);
  @Autowired
  private BiddingRepository biddingRepository;

  @Autowired
  private ProjectListingRepository projectListingRepository;

  @Autowired
  private ProcessUtility processUtility;

  @Autowired
  private ProcessListingService processListingService;

  @Autowired
  private WebSocketService webSocketService;

  @Autowired
  private BlockchainService blockchainService;

  public List<Bidding> getBidListingByBidderId(Long bidderId) {
    return biddingRepository.findByBidderId(bidderId);
  }

  public List<Bidding> getAllBids() {
    return biddingRepository.findAll();
  }

  public Bidding createBid(Long bidderId, Bidding request) {
    request.setBidderId(bidderId);
    request.setCategory("car");
    Bidding bid = biddingRepository.save(request);
    
    ProjectListing listing = projectListingRepository.findById(bid.getListingId())
        .orElseThrow(() -> new ResourceNotFoundException("No listing found with ID = " + bid.getListingId()));
      
    Long instanceId = listing.getProcessId();
      Map<String, Object> params = new HashMap<>();
      params.put("biddingPrice", bid.getBiddingPrice());
      params.put("bidderId", bid.getBidderId());
      params.put("category", bid.getCategory());
      
      try {
        Long restorationBidTaskId = processUtility.getTaskIdByProcessIdAndTitle(instanceId,
            ProcessTaskNamesEnum.restorationBid);
        processUtility.completeTask(restorationBidTaskId, "RestoreOne", params);
      } catch (Exception e) {
        logger.error("Error In Creating Bid: Completing Task:", e.getMessage());
      }
      
      try {
        List<ProcessResponse> taskResponses = processUtility.getPendingTasksByProcessId(instanceId);
        List<String> pendingTasks = processListingService.convertProcessResponseToString(taskResponses);
        listing.setPendingTasks(pendingTasks);
        projectListingRepository.save(listing);
      } catch (Exception e) {
        logger.error("Error In Creating Bid: Getting Pending Tasks:", e.getMessage());
      }
    
    webSocketService.sendBidCreated(bid);
    return bid;
  }

  public Bidding updateBid(Long bidId, Bidding request) {
    Bidding bid = biddingRepository.findById(bidId)
        .orElseThrow(() -> new ResourceNotFoundException("No bid found with ID = " + bidId));

    Float newPrice = request.getBiddingPrice();
    if (newPrice != null) {
      bid.setBiddingPrice(newPrice);
    }
    String newCategory = request.getCategory();
    if (newCategory != null) {
      bid.setCategory(newCategory);
    }
    bid = biddingRepository.save(bid);
    webSocketService.sendBidUpdated(bid);
    return bid;
  }

  public ProjectListing selectBiddingForListing(Long bidId) {
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

    System.out.println("BID REVIEW" + listing.toString());
    Map<String, Object> params = new HashMap();
    params.put("bidPrice", listing.getBidPrice());
    params.put("isBidSuccess", listing.getIsBidSuccess());
    Long instanceId = listing.getProcessId();
    try {
      Long bidReviewTaskId = processUtility.getTaskIdByProcessIdAndTitle(instanceId,
          ProcessTaskNamesEnum.bidReview);
      processUtility.completeTask(bidReviewTaskId, "wbadmin", params);
    } catch (Exception e) {
      logger.error("Error In Selecting Bid: Completing Task: " + e.getMessage());
      throw new RuntimeException("Failed to complete jBPM task", e);
    }

    // Execute blockchain operation (AFTER jBPM, but in same method)
    if (listing.getProjectAddress() != null && !listing.getProjectAddress().isEmpty() 
        && bid.getRestorerAddress() != null && !bid.getRestorerAddress().isEmpty()) {
      try {
        BigDecimal fundingGoalDecimal = BigDecimal.valueOf(bid.getBiddingPrice().doubleValue());
        BigDecimal weiMultiplier = BigDecimal.valueOf(10).pow(18);
        BigInteger fundingGoal = fundingGoalDecimal.multiply(weiMultiplier).toBigInteger();
        
        blockchainService.setRestorer(
            listing.getProjectAddress(),
            bid.getRestorerAddress(),
            fundingGoal
        );
        logger.info("Blockchain operation succeeded for set restorer");
      } catch (Exception e) {
        logger.error("Blockchain operation failed for set restorer", e);
        throw new RuntimeException("Blockchain operation failed: " + e.getMessage(), e);
      }
    }

    try {
      List<ProcessResponse> taskResponses = processUtility.getPendingTasksByProcessId(instanceId);
      List<String> pendingTasks = processListingService.convertProcessResponseToString(taskResponses);
      listing.setPendingTasks(pendingTasks);
    } catch (Exception e) {
      logger.error("Error In Selecting Bid: Getting Pending Tasks: " + e.getMessage());
    }
    listing = projectListingRepository.save(listing);
    webSocketService.sendListingUpdated(listing);
    webSocketService.sendBidApproved(bid);
    return listing;
  }
}
