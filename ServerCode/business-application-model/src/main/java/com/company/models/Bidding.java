package com.company.models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "biddings")
public class Bidding {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  // this refers to the user_id but to make it clear use bidder_id instead
  // and this can be served as a foreign key to connect with user table &
  // project_listing table
  @Column(name = "bidder_id")
  private Long bidderId;

  @Column(name = "listing_id")
  private Long listingId;

  @Column(name = "bidding_price")
  private Float biddingPrice;

  // this means which part of the restoration the bidder is trying to restore
  // for instance, a car restorer could restore bumpers or car lamps, or engines,
  // etc.
  // each category could have multiple biders
  @Column(name = "category")
  private String category;

  @Column(name = "restorerAddress")
  private String restorerAddress;

  @Column(name = "is_selected")
  private Boolean isSelected = false;

  public Bidding() {
  }

  public Bidding(Long bidderId, Long listingId, Float biddingPrice, String category) {
    this.bidderId = bidderId;
    this.listingId = listingId;
    this.biddingPrice = biddingPrice;
    this.category = category;
  }

  public boolean equals(Object object) {
    if (this == object)
      return true;
    if (object == null || getClass() != object.getClass())
      return false;
    if (!super.equals(object))
      return false;
    Bidding biding = (Bidding) object;
    return id.equals(biding.id) &&
        bidderId.equals(biding.bidderId) &&
        listingId.equals(biding.listingId) &&
        biddingPrice.equals(biding.biddingPrice) &&
        category.equals(biding.category);
  }

  public int hashCode() {
    return java.util.Objects.hash(super.hashCode(), id, bidderId, listingId, biddingPrice, category);
  }

  public Long getBidderId() {
    return bidderId;
  }

  public void setBidderId(Long bidderId) {
    this.bidderId = bidderId;
  }

  public Long getListingId() {
    return listingId;
  }

  public void setListingId(Long listingId) {
    this.listingId = listingId;
  }

  public Float getBiddingPrice() {
    return biddingPrice;
  }

  public void setBiddingPrice(Float biddingPrice) {
    this.biddingPrice = biddingPrice;
  }

  public String getCategory() {
    return category;
  }

  public void setCategory(String category) {
    this.category = category;
  }

  public String getRestorerAddress() {
    return restorerAddress;
  }

  public void setRestorerAddress(String restorerAddress) {
    this.restorerAddress = restorerAddress;
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
}
