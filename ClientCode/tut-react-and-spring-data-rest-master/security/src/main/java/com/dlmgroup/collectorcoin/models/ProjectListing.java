package com.dlmgroup.collectorcoin.models;

import java.util.Objects;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.JoinColumn;
import javax.persistence.Table;
import javax.persistence.Version;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name = "project_listings")
public class ProjectListing {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "process_id")
	private Long processId; // jBPM process ID for the new project listing


	// Variables for the New Listing Proposed task
	// Note: some of these "New Listing" variables are input variables to other tasks
	@Column(name = "vin")
	private String vin; // task output

	@Column(name = "make")
	private String make; // task output

	@Column(name = "model")
	private String model; // task output

	@Column(name = "ccpg")
	private String ccpg; // CCPG (collector car price guide value), task output

	@Column(name = "funding_goal")
	private Float fundingGoal; // task output

	@Column(name = "vin_matched")
	private boolean vinMatched = false; // task output

	// Variables for the Verify Details task
	@Column(name = "details_verified")
	private boolean verifyDetails = false; // task output

	// Variables for the Receive Title task
	@Column(name = "title_received")
	private boolean receiveTitle = false; // task output

  @OneToOne
	@JoinColumn(name = "file_id")
	@OnDelete(action = OnDeleteAction.CASCADE)
  private File file;

	// Variables for the Funding task
	@Column(name = "curr_funding_amount")
	private Float currFundingAmount; // task input

	@Column(name = "amount")
	private Float amount;            // task output

	@Column(name = "is_funding_enough")
	private Boolean isFundingEnough = false; // task output

//	 Variables for the Restoration Bid task
	@Column(name = "bid_price")
	private Float bidPrice; // task output

//	 Variables for the Bid Review task
	@Column(name = "is_bid_success")
	private Boolean isBidSuccess = false; // task output

//	 Variables for the Assign Restoration task
	@Column(name = "restorerId")
	private Long restorerId; // task output

//	 Variables for the Restoration Finished task
	@Column(name = "is_restoration_finished")
	private Boolean isRestorationFinished = false; // task output

//	 Variables for the Restoration Assignement task
	@Column(name = "is_restoration_assigned")
	private Boolean isRestorationAssigned = false; // task output

//	 Variables for the Post Item task
	@Column(name = "is_posted_for_sale")
	private Boolean isPostedForSale = false; // task output

//	 Variables for the Post Item task
	@Column(name = "selling_price")
	private Float sellingPrice; // task output

//	 Variables for the Auction task
	@Column(name = "auction_price")
	private Float auctionPrice; // task output

//	 Variables for the Auction Review task
	@Column(name = "is_auction_accept")
	private Boolean isAuctionAccept = false; // task output

	@Column(name = "close_date")
	private Date closeDate;

	@Column(name = "is_redistributed")
	private Boolean isRedistributed;

//	 Variables for the auction review task
	@Column(name = "buyer_id")
	private Long buyerId; // task output

//	@Column(name = "description")
//	private String description;

//	// Variable in jBPM model
//	@Column(name = "payload")
//	private Object payload;

	private @Version @JsonIgnore Long version;

  @Column(name="valueEstimation")
  private Float valueEstimation;

  @Column(name="repairCostEstimation")
  private Float repairCostEstimation;

  @Column(name="adminMessage")
  private String adminMessage;

  @Column(name="description")
  private String description;

  @Column(name="ownerAddress")
  private String ownerAddress;

  @Column(name="projectAddress")
  private String projectAddress;


	//private @ManyToOne User user; // ORIGINAL

	// this ignore will solve the issue that it can't load users
	//	@JsonIgnore -> previously this is added, but it just don't return user which is not we want
	@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "user_id", nullable = false)
	@OnDelete(action = OnDeleteAction.NO_ACTION)
	private User user;

  @ElementCollection
  @CollectionTable(name = "pending_tasks", joinColumns = @JoinColumn(name = "listing_id"))
  @Column(name="pending_tasks")
  private List<String> pendingTasks = new ArrayList<String>();

	public ProjectListing() {
		this.currFundingAmount = 0.0f;
	}

	public ProjectListing(String vin, String make, String model, String ccpg, Float fundingGoal, User user) {
		this.vin = vin;
		this.make = make;
		this.model = model;
		this.ccpg = ccpg;
		this.fundingGoal = fundingGoal;
		this.user = user;
		this.currFundingAmount = 0.0f;
	}

  public ProjectListing(Boolean verifyDetails, Boolean receiveTitle) {
    this.verifyDetails = verifyDetails;
    this.receiveTitle = receiveTitle;
  }

  public ProjectListing(Float valueEstimation, Float repairCostEstimation) {
    this.valueEstimation = valueEstimation;
    this.repairCostEstimation = repairCostEstimation;
  }

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		ProjectListing listing = (ProjectListing) o;
		return Objects.equals(id, listing.id) &&
			Objects.equals(vin, listing.vin) &&
			Objects.equals(make, listing.make) &&
			Objects.equals(model, listing.model) &&
			Objects.equals(ccpg, listing.ccpg) &&
			Objects.equals(fundingGoal, listing.fundingGoal) &&
			Objects.equals(version, listing.version) &&
			Objects.equals(user, listing.user);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, vin, make, model, ccpg, version, user);
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getProcessId() {
		return this.processId;
	}

	public void setProcessId(Long id) {
		this.processId = id;
	}

	public String getVIN() {
		return this.vin;
	}

	public void setVIN(String vin) {
		this.vin = vin;
	}

	public Boolean getIsRedistributed() {
		return this.isRedistributed;
	}

	public void setIsRedistributed(Boolean isRedistributed) {
		this.isRedistributed = isRedistributed;
	}

	public String getMake() {
		return this.make;
	}

	public void setMake(String make) {
		this.make = make;
	}

	public String getModel() {
		return this.model;
	}

	public void setModel(String model) {
		this.model = model;
	}

	public String getCCPG() {
		return this.ccpg;
	}

	public void setCCPG(String ccpg) {
		this.ccpg = ccpg;
	}

	public Float getFundingGoal() {
		return this.fundingGoal;
	}

	public void setFundingGoal(Float fundingGoal) {
		this.fundingGoal = fundingGoal;
	}

	public boolean getVinMatched() {
		return this.vinMatched;
	}

	public void setVinMatched(boolean vinMatched) {
		this.vinMatched = vinMatched;
	}

	public boolean getVerifyDetails() {
		return this.verifyDetails;
	}

	public void setVerifyDetails(boolean verifyDetails) {
		this.verifyDetails = verifyDetails;
	}

	public boolean getReceiveTitle() {
		return this.receiveTitle;
	}

	public void setReceiveTitle(boolean receiveTitle) {
		this.receiveTitle = receiveTitle;
	}

  public File getFile() {
    return this.file;
  }

  public void setFile(File f) {
    this.file = f;
  }

  public Float getValueEstimation() {
    return this.valueEstimation;
  }

  public void setValueEstimation(Float estimation) {
    this.valueEstimation = estimation;
  }

  public Float getRepairCostEstimation() {
    return this.repairCostEstimation;
  }

  public void setRepairCostEstimation(Float cost) {
    this.repairCostEstimation = cost;
  }

	public Float getAmount() {
		return this.amount;
	}

	public void setAmount(Float amount) {
		this.amount = amount;
	}

	public Float getAuctionPrice() {
		return this.auctionPrice;
	}

	public void setAuctionPrice(Float auctionPrice) {
		this.auctionPrice = auctionPrice;
	}

	public Float getBidPrice() {
		return this.bidPrice;
	}

	public void setBidPrice(Float bidPrice) {
		this.bidPrice = bidPrice;
	}

	public Float getCurrFundingAmount() {
		return this.currFundingAmount;
	}

	public void setCurrFundingAmount(Float currFundingAmount) {
		this.currFundingAmount = currFundingAmount;
	}

	public Float calcCurrFundingRequiredAmount() {
		return this.fundingGoal - this.currFundingAmount;
	}

	public void increaseCurrFundingAmount(Float newInvestment) {
		//if (this.currFundingAmount == null) this.currFundingAmount = newInvestment;
		this.currFundingAmount += newInvestment;
		if(this.currFundingAmount >= this.fundingGoal){
			this.isFundingEnough = true;
		}
	}
//
//	public void setCurrFundingAmoount(Float currFundingAmount) {
//		this.currFundingAmount = currFundingAmount;
//	}
//
	public Boolean getIsAuctionAccept() {
		return this.isAuctionAccept;
	}

	public void setIsAuctionAccept(Boolean isAuctionAccept) {
		this.isAuctionAccept = isAuctionAccept;
	}

	public Boolean getIsBidSuccess() {
		return this.isBidSuccess;
	}

	public void setIsBidSuccess(Boolean isBidSuccess) {
		this.isBidSuccess = isBidSuccess;
	}

	public Boolean getIsRestorationAssigned() {
		return this.isRestorationAssigned;
	}

	public void setIsRestorationAssigned(Boolean isRestorationAssigned) {
		this.isRestorationAssigned = isRestorationAssigned;
	}

	public Boolean getIsPostedForSale() {
		return this.isPostedForSale;
	}

	public void setIsPostedForSale(Boolean getIsPostedForSale) {
		this.isPostedForSale = getIsPostedForSale;
	}

	public Boolean getIsFundingEnough() {
		return this.isFundingEnough;
	}

	public void setIsFundingEnough(Boolean isFundingEnough) {
		this.isFundingEnough = isFundingEnough;
	}

	public Boolean getIsRestorationFinished() {
		return this.isRestorationFinished;
	}

	public void setIsRestorationFinished(Boolean isRestorationFinished) {
		this.isRestorationFinished = isRestorationFinished;
	}

	public Long getRestorerId() {
		return this.restorerId;
	}

	public void setRestorerId(Long id) {
		this.restorerId = id;
	}

	public Long getBuyerId() {
		return this.buyerId;
	}

	public void setBuyerId(Long id) {
		this.buyerId = id;
	}

	public Float getSellingPrice() {
		return this.sellingPrice;
	}

	public void setSellingPrice(Float sellingPrice) {
		this.sellingPrice = sellingPrice;
	}

	public User getUser() {
		return this.user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public String getAdminMessage() {
		return this.adminMessage;
	}

	public void setAdminMessage(String s) {
		this.adminMessage = s;
	}

	public String getOwnerAddress() {
		return this.ownerAddress;
	}

	public void setOwnerAddress(String ownerAddress) {
		this.ownerAddress = ownerAddress;
	}

	public String getProjectAddress() {
		return this.projectAddress;
	}

	public void setProjectAddress(String projectAddress) {
		this.projectAddress = projectAddress;
	}

	public void addPendingTask(String t) {
		this.pendingTasks.add(t);
	}

	public void removeTask(String t) {
		this.pendingTasks.remove(t);
	}

	public List<String> getPendingTasks() {
		return this.pendingTasks;
	}

	public void setPendingTasks(List<String> list) {
		this.pendingTasks = list;
	}

	public void removeAllPendingTasks() {
		this.pendingTasks.clear();
	}

	public String getDescription() {
		return this.description;
	}

	public void setDescription(String s) {
		this.description = s;
	}

	public void closeListing() {
		this.closeDate = new Date();
	}

	@Override
	public String toString() {
		return "ProjectListing{" +
			"id=" + id +
			", processId='" + processId + '\'' +
			", vin='" + vin + '\'' +
			", make='" + make + '\'' +
			", model='" + model + '\'' +
			", ccpg='" + ccpg + '\'' +
			", fundingGoal='" + fundingGoal + '\'' +
			", version=" + version +
			", user=" + user +
      ", verifyDetails=" + verifyDetails +
      ", titleDetails=" + receiveTitle +
			'}';
	}
}
