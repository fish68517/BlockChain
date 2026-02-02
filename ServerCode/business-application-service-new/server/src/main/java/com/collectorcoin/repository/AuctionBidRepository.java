package com.collectorcoin.repository;

import com.collectorcoin.model.AuctionBid;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AuctionBidRepository extends JpaRepository<AuctionBid, Long> {
    List<AuctionBid> findByBuyerId(Long buyerId);
    List<AuctionBid> findByListingId(Long listingId);
    List<AuctionBid> findByListingIdAndIsSelectedTrue(Long listingId);
    List<AuctionBid> findByIsSelectedFalse();
}
