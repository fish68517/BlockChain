package com.company.models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.JoinColumn;
import javax.persistence.Table;
import com.fasterxml.jackson.annotation.JsonProperty;

@Entity
@Table(name = "investments")
public class Investment {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;// InvestmentID

  @Column(name = "amount")
  private Float amount; // investment amount

  // Foreign keys to the original listing and to the user than invested
  @ManyToOne
  @JoinColumn(name = "listing_id")
  @JsonProperty("listing")
  private ProjectListing listing;

  @ManyToOne
  @JoinColumn(name = "user_id")
  private User user;

  public Investment() {
  }

  public Investment(Float amount, ProjectListing listing, User user) {
    this.amount = amount;
    this.listing = listing;
    this.user = user;
  }

  public Float getAmount() {
    return this.amount;
  }

  public void setAmount(Float amount) {
    this.amount = amount;
  }

  public ProjectListing getListing() {
    return this.listing;
  }

  public void setListing(ProjectListing listing) {
    this.listing = listing;
  }

  public User getUser() {
    return user;
  }

  public void setUser(User user) {
    this.user = user;
  }

  public String toString() {
    return this.amount + "";
  }

  public void increaseInvestmentAmountBy(Float addAmount) {
    this.amount += addAmount;
  }
}
