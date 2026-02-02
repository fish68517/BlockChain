package com.collectorcoin.service;

import com.collectorcoin.model.AuctionBid;
import com.collectorcoin.model.ProjectListing;
import com.collectorcoin.model.User;
import com.collectorcoin.repository.AuctionBidRepository;
import com.collectorcoin.repository.ProjectListingRepository;
import com.collectorcoin.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class AuctionService {
    
    private static final Logger logger = LoggerFactory.getLogger(AuctionService.class);
    
    private final AuctionBidRepository auctionBidRepository;
    private final ProjectListingRepository listingRepository;
    private final UserRepository userRepository;
    
    public AuctionService(AuctionBidRepository auctionBidRepository, 
                          ProjectListingRepository listingRepository,
                          UserRepository userRepository) {
        this.auctionBidRepository = auctionBidRepository;
        this.listingRepository = listingRepository;
        this.userRepository = userRepository;
    }
    
    public List<AuctionBid> getAllBids() {
        return auctionBidRepository.findAll();
    }
    
    public List<AuctionBid> getBidsByBuyerId(Long buyerId) {
        return auctionBidRepository.findByBuyerId(buyerId);
    }
    
    public List<AuctionBid> getBidsByListingId(Long listingId) {
        return auctionBidRepository.findByListingId(listingId);
    }
    
    public Optional<AuctionBid> getBidById(Long id) {
        return auctionBidRepository.findById(id);
    }
    
    @Transactional
    public AuctionBid createBid(Long buyerId, Long listingId, BigDecimal bidAmount, String transactionHash) {
        // Validate listing exists
        ProjectListing listing = listingRepository.findById(listingId)
            .orElseThrow(() -> new IllegalArgumentException("Listing not found: " + listingId));
        
        // Validate buyer exists
        User buyer = userRepository.findById(buyerId)
            .orElseThrow(() -> new IllegalArgumentException("User not found: " + buyerId));
        
        // Validate bid amount
        if (bidAmount == null || bidAmount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Bid amount must be positive");
        }
        
        AuctionBid bid = new AuctionBid();
        bid.setBuyerId(buyerId);
        bid.setListingId(listingId);
        bid.setBidAmount(bidAmount);
        bid.setBuyerAddress(buyer.getWalletAddress());
        bid.setTransactionHash(transactionHash);
        
        logger.info("Created auction bid: buyer={}, listing={}, amount={}", buyerId, listingId, bidAmount);
        return auctionBidRepository.save(bid);
    }
    
    @Transactional
    public AuctionBid updateBidAmount(Long bidId, BigDecimal newAmount) {
        AuctionBid bid = auctionBidRepository.findById(bidId)
            .orElseThrow(() -> new IllegalArgumentException("Bid not found: " + bidId));
        
        if (bid.getIsSelected()) {
            throw new IllegalArgumentException("Cannot update a selected bid");
        }
        
        bid.setBidAmount(newAmount);
        logger.info("Updated bid amount: bidId={}, newAmount={}", bidId, newAmount);
        return auctionBidRepository.save(bid);
    }
    
    @Transactional
    public AuctionBid selectBid(Long bidId) {
        AuctionBid bid = auctionBidRepository.findById(bidId)
            .orElseThrow(() -> new IllegalArgumentException("Bid not found: " + bidId));
        
        // Update listing with auction winner
        ProjectListing listing = listingRepository.findById(bid.getListingId())
            .orElseThrow(() -> new IllegalArgumentException("Listing not found"));
        
        listing.setStatus("SOLD");
        listingRepository.save(listing);
        
        // Mark bid as selected
        bid.setIsSelected(true);
        logger.info("Selected auction bid: bidId={}, listingId={}", bidId, bid.getListingId());
        return auctionBidRepository.save(bid);
    }
    
    @Transactional
    public AuctionBid processBid(Long bidId, String transactionHash) {
        AuctionBid bid = auctionBidRepository.findById(bidId)
            .orElseThrow(() -> new IllegalArgumentException("Bid not found: " + bidId));
        
        if (!bid.getIsSelected()) {
            throw new IllegalArgumentException("Bid must be selected before processing");
        }
        
        bid.setIsProcessed(true);
        bid.setTransactionHash(transactionHash);
        logger.info("Processed auction bid: bidId={}, txHash={}", bidId, transactionHash);
        return auctionBidRepository.save(bid);
    }
}
