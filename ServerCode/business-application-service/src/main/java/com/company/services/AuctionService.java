package com.company.services;

import java.util.List;
import com.company.models.AuctionBid;
import com.company.repositories.AuctionBidRepository;
import org.springframework.beans.factory.annotation.Autowired;
import com.company.exception.ResourceNotFoundException;
import com.company.models.ProjectListing;
import java.util.Map;
import java.util.HashMap;
import org.springframework.stereotype.Service;
import com.company.utils.ProcessUtility;
import com.company.enums.ProcessTaskNamesEnum;
import com.company.models.ProcessResponse;
import com.company.services.ProcessListingService;
import com.company.repositories.ProjectListingRepository;
import com.company.services.BlockchainService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.math.BigInteger;

@Service
public class AuctionService {
  private static final Logger logger = LoggerFactory.getLogger(AuctionService.class);

  @Autowired
  private AuctionBidRepository auctionBidRepository;

  @Autowired
  private WebSocketService webSocketService;

  @Autowired
  private ProcessUtility processUtility;

  @Autowired
  private ProcessListingService processListingService;

  @Autowired
  private ProjectListingRepository projectListingRepository;

  @Autowired
  private BlockchainService blockchainService;

  public List<AuctionBid> getAuctionBidListingByBuyerId(Long buyerId) {
    return auctionBidRepository.findByBuyerId(buyerId);
  }

  public List<AuctionBid> getAllAuctions() {
    return auctionBidRepository.findAll();
  }

  public AuctionBid createBid(Long buyerId, AuctionBid request) {
    request.setBuyerId(buyerId);
    return auctionBidRepository.save(request);
  }

  public AuctionBid updateBid(Long bidId, AuctionBid request) {
    AuctionBid auctionBid = auctionBidRepository.findById(bidId)
        .orElseThrow(() -> new ResourceNotFoundException("No auction bid found with ID = " + bidId));

    Float newPrice = request.getBuyerPrice();
    if (newPrice != null) {
      auctionBid.setBuyerPrice(newPrice);
    } else {
      throw new ResourceNotFoundException("No new price found for auction bid with ID = " + bidId);
    }
    return auctionBidRepository.save(auctionBid);
  }

  public ProjectListing selectBiddingForListing(Long bidId) {
    AuctionBid auctionBid = auctionBidRepository.findById(bidId)
        .orElseThrow(() -> new ResourceNotFoundException("No auction bid found with ID = " + bidId));
    Long listingId = auctionBid.getListingId();
    ProjectListing listing = projectListingRepository.findById(listingId)
        .orElseThrow(() -> new ResourceNotFoundException("No listing found with ID = " + listingId));

    auctionBid.setIsSelected(true);
    auctionBidRepository.save(auctionBid);

    listing.setAuctionPrice(auctionBid.getBuyerPrice());
    listing.setIsAuctionAccept(true);
    listing.setBuyerId(auctionBid.getBuyerId());

    System.out.println("AUCTION REVIEW " + listing.toString());
    Map<String, Object> params = new HashMap();
    params.put("auctionPrice", listing.getAuctionPrice());
    params.put("isAuctionAccept", listing.getIsAuctionAccept());

    Long instanceId = listing.getProcessId();

    try {
      Long auctionReviewTaskId = processUtility.getTaskIdByProcessIdAndTitle(instanceId,
          ProcessTaskNamesEnum.auctionReview);
      processUtility.completeTask(auctionReviewTaskId, "wbadmin", params);
    } catch (Exception e) {
      logger.error("Error In Selecting Bidding For Listing: Completing Auction Review Task: " + e.getMessage());
    }
    try {
      List<ProcessResponse> taskResponses = processUtility.getPendingTasksByProcessId(instanceId);
      List<String> taskNames = processListingService.convertProcessResponseToString(taskResponses);
      listing.setPendingTasks(taskNames);
    } catch (Exception e) {
      logger.error("Error In Selecting Bidding For Listing: Getting Pending Tasks: " + e.getMessage());
    }

    listing = projectListingRepository.save(listing);

    webSocketService.sendListingUpdated(listing);
    webSocketService.sendAuctionBidApproved(auctionBid);

    // Execute blockchain operations (set buyer and redistribute)
    if (listing.getProjectAddress() != null && !listing.getProjectAddress().isEmpty() 
        && auctionBid.getBuyerAddress() != null && !auctionBid.getBuyerAddress().isEmpty()) {
      try {
        // Set buyer on blockchain
        blockchainService.setBuyer(listing.getProjectAddress(), auctionBid.getBuyerAddress());
        logger.info("Blockchain operation succeeded for set buyer");
        
        // Redistribute funds on blockchain
        blockchainService.redistribute(listing.getProjectAddress());
        logger.info("Blockchain operation succeeded for redistribute");
      } catch (Exception e) {
        logger.error("Blockchain operation failed for set buyer/redistribute", e);
        throw new RuntimeException("Blockchain operation failed: " + e.getMessage(), e);
      }
    }

    listing.setIsRedistributed(true);

    System.out.println("REDISTRIBUTION " + listing.toString());
    Map<String, Object> params2 = new HashMap();
    params2.put("auctionPrice", listing.getAuctionPrice());
    params2.put("isRedistributed", listing.getIsRedistributed());

    instanceId = listing.getProcessId();

    try {
      Long redistributionTaskId = processUtility.getTaskIdByProcessIdAndTitle(instanceId,
          ProcessTaskNamesEnum.redistribution);
      processUtility.completeTask(redistributionTaskId, "wbadmin", params2);
    } catch (Exception e) {
      logger.error("Error In Selecting Bidding For Listing: Completing Redistribution Task: " + e.getMessage());
      throw new RuntimeException("Failed to complete jBPM task", e);
    }

    try {
      List<ProcessResponse> taskResponses = processUtility.getPendingTasksByProcessId(instanceId);
      List<String> taskNames = processListingService.convertProcessResponseToString(taskResponses);
      listing.setPendingTasks(taskNames);
    } catch (Exception e) {
      logger.error("Error In Selecting Bidding For Listing: Getting Pending Tasks: " + e.getMessage());
    }

    listing = projectListingRepository.save(listing);
    webSocketService.sendListingUpdated(listing);

    return listing;
  }
}
