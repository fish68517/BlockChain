package com.dlmgroup.collectorcoin.models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "auction_bids")
public class AuctionBid {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "buyer_id")  // user id of the buyer making this bid
    private Long buyerId;   // also foreign key to connect to project_listings

    @Column(name = "listing_id")    // id of the project listed
    private Long listingId;

    @Column(name = "buyer_address") // wallet address of the bidder
    private String buyerAddress;

    @Column(name = "buyer_price")   // price that this buyer proposed
    private Float buyerPrice;

    @Column(name = "is_selected")   // whether the auction bid is selected
    private Boolean isSelected = false;

    @Column(name = "is_processed")  // whether the transaction has been processed
    private Boolean isProcessed = false;

    public AuctionBid() {}

    public AuctionBid(Long buyerId, Long listingId, Float buyerPrice) {
      this.buyerId = buyerId;
      this.listingId = listingId;
      this.buyerPrice = buyerPrice;
    }

    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        if (!super.equals(object)) return false;
        AuctionBid auctionBid = (AuctionBid) object;
        return id.equals(auctionBid.id) &&
                buyerId.equals(auctionBid.buyerId) &&
                listingId.equals(auctionBid.listingId) &&
                buyerPrice.equals(auctionBid.buyerPrice);
    }

    public int hashCode() {
        return java.util.Objects.hash(super.hashCode(), id, buyerId, listingId, buyerPrice);
    }

    public Long getBuyerId() {
        return buyerId;
    }

    public void setBuyerId(Long buyerId) {
        this.buyerId = buyerId;
    }

    public Long getListingId() {
        return listingId;
    }

    public void setListingId(Long listingId) {
        this.listingId = listingId;
    }

    public Float getBuyerPrice() {
        return buyerPrice;
    }

    public void setBuyerPrice(Float buyerPrice) {
        this.buyerPrice = buyerPrice;
    }

    public String getBuyerAddress() {
        return buyerAddress;
    }

    public void setBuyerAddress(String buyerAddress) {
        this.buyerAddress = buyerAddress;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Boolean getIsSelected() {
      return this.isSelected;
    }

    public void setIsSelected(Boolean s) {
      this.isSelected = s;
    }

    public Boolean getIsProcessed() {
      return this.isProcessed;
    }

    public void setIsProcessed(Boolean s) {
      this.isSelected = s;
    }
}