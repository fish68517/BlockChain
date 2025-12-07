package com.company.services;

import com.company.models.*;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Component
public class WebSocketService {

  private SimpMessagingTemplate simpMessagingTemplate;

  public WebSocketService(SimpMessagingTemplate simpMessagingTemplate) {
    this.simpMessagingTemplate = simpMessagingTemplate;
  }

  public void sendListingReject() {
    simpMessagingTemplate.convertAndSend("/topic/projectListing/reject", "Edit your stuff");
  }

  public void sendListingCreated(ProjectListing listing) {
    simpMessagingTemplate.convertAndSend("/topic/newProjectListing", listing);
  }

  public void sendAllListingUpdated(ProjectListing listing) {
    simpMessagingTemplate.convertAndSend("/topic/updateProjectListing", listing);
  }

  public void sendListingUpdated(ProjectListing listing) {
    Long userId = listing.getUser().getId();
    simpMessagingTemplate.convertAndSend("/topic/" + userId + "/updateProjectListing", listing);
  }

  public void sendBidCreated(Bidding bid) {
    simpMessagingTemplate.convertAndSend("/topic/biddingCreated", bid);
  }

  public void sendBidUpdated(Bidding bid) {
    simpMessagingTemplate.convertAndSend("/topic/biddingUpdated", bid);
  }

  public void sendListingAvailableForBid(ProjectListing listing) {
    simpMessagingTemplate.convertAndSend("/topic/listingAvailableForBid", listing);
  }

  public void sendListingAvailableForFunding(ProjectListing listing) {
    simpMessagingTemplate.convertAndSend("/topic/listingAvailableForFunding", listing);
  }

  public void sendBidApproved(Bidding bid) {
    Long userId = bid.getBidderId();
    simpMessagingTemplate.convertAndSend("/topic/" + userId + "/bidApproved", bid);
  }

  public void sendAuctionBidCreated(AuctionBid bid) {
    simpMessagingTemplate.convertAndSend("/topic/auctionBiddingCreated", bid);
  }

  public void sendAuctionBidUpdated(AuctionBid bid) {
    simpMessagingTemplate.convertAndSend("/topic/auctionBiddingUpdated", bid);
  }

  public void sendListingAvailableForAuctionBid(ProjectListing listing) {
    simpMessagingTemplate.convertAndSend("/topic/listingAvailableForAuctionBid", listing);
  }

  public void sendAuctionBidApproved(AuctionBid bid) {
    Long userId = bid.getBuyerId();
    simpMessagingTemplate.convertAndSend("/topic/" + userId + "/auctionBidApproved", bid);
  }

}