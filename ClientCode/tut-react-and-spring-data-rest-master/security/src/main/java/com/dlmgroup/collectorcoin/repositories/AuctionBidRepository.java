package com.dlmgroup.collectorcoin.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import com.dlmgroup.collectorcoin.models.AuctionBid;

public interface AuctionBidRepository extends JpaRepository<AuctionBid, Long> {

    List<AuctionBid> findByBuyerId(Long buyerId);

    List<AuctionBid> findByListingId(Long listingId);

    AuctionBid findByBuyerIdAndListingId(Long buyerId, Long listingId);

    @Override
    AuctionBid save(@Param("auction_bid") AuctionBid auction_bid);

    @Override
    List<AuctionBid> findAll();

}