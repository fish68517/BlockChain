package com.company.models;

import java.io.Serializable;

public class ProjectVerification implements Serializable {
	private Long processId; // jBPM process ID for the new project listing

	// Variables for the New Listing Proposed task
	// Note: some of these "New Listing" variables are input variables to other
	// tasks
	private String ccAdministrator; // task output
	private String vin; // task output
	private String make; // task output
	private String model; // task output
	private Boolean vinMatched; // task output
	private String ccpg; // task output
	private String fundingGoal; // task output

	// Variables for the Verify Details task
	private boolean verifyDetails = false; // task output

	// Variables for the Receive Title task
	private boolean receiveTitle = false; // task output

	// Variables for the Value Estimation task
	private Float valueEstimation; // task output
	private Float repairCostEstimation; // task output

	// Variables for the Funding task
	private Float currFundingAmount; // task input
	private Float amount; // task output
	private Boolean isFundingEnough; // task output

	// Variables for the Restoration Bid task
	private Float bidPrice; // task output

	// Variables for the Bid Review task
	private Boolean isBidSuccess; // task output

	// Variables for the Assign Restoration task
	private String restorerId; // task output

	// Variables for the Restoration Finished task
	private Boolean isRestorationFinished; // task output

	// Variables for the Post Item task
	private Float sellingPrice; // task output

	// Variables for the Auction task
	private Float auctionPrice; // task output

	// Variables for the Auction Review task
	private Boolean isAuctionAccept; // task output

	// Variables for the Auction Review task
	private Boolean isRedistributed; // task output

	// // Variable in jBPM model
	// private Object payload;

	public Long getProcessId() { // new project listing ID (provided by new process instance ID)
		return this.processId;
	}

	public void setProcessId(Long id) {
		this.processId = id;
	}

	public String getCCAdministrator() {
		return this.ccAdministrator;
	}

	public void setCCAdministrator(String ccAdministrator) {
		this.ccAdministrator = ccAdministrator;
	}

	public String getVin() {
		return this.vin;
	}

	public void setVin(String vin) {
		this.vin = vin;
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

	public Boolean getVinMatched() {
		return this.vinMatched;
	}

	public void setVinMatched(Boolean vinMatched) {
		this.vinMatched = vinMatched;
	}

	public String getCCPG() {
		return this.ccpg;
	}

	public void setCCPG(String ccpg) {
		this.ccpg = ccpg;
	}

	public String getFundingGoal() {
		return this.fundingGoal;
	}

	public void setFundingGoal(String fundingGoal) {
		this.fundingGoal = fundingGoal;
	}

	public Boolean getVerifyDetails() {
		return this.verifyDetails;
	}

	public void setCerifyDetails(Boolean dv) {
		this.verifyDetails = dv;
	}

	public Boolean getReceiveTitle() {
		return this.receiveTitle;
	}

	public void setReceiveTitle(Boolean receivetitle) {
		this.receiveTitle = receivetitle;
	}

	public void setValueEstimation(Float ve) {
		this.valueEstimation = ve;
	}

	public Float getValueEstimation() {
		return this.valueEstimation;
	}

	public void setRepairCostEstimation(Float cost) {
		this.repairCostEstimation = cost;
	}

	public Float getRepairCostEstimation() {
		return this.repairCostEstimation;
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

	public void setCurrFundingAmoount(Float currFundingAmount) {
		this.currFundingAmount = currFundingAmount;
	}

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

	public Boolean getIsFundingEnough() {
		return this.isFundingEnough;
	}

	public void setIsFundingEnough(Boolean isFundingEnough) {
		this.isFundingEnough = isFundingEnough;
	}

	public Boolean getIsRedistributed() {
		return this.isRedistributed;
	}

	public void setIsRedistributed(Boolean isRedistributed) {
		this.isRedistributed = isRedistributed;
	}

	public Boolean getIsRestorationFinished() {
		return this.isRestorationFinished;
	}

	public void setIsRestorationFinished(Boolean isRestorationFinished) {
		this.isRestorationFinished = isRestorationFinished;
	}

	public String getRestorerId() {
		return this.restorerId;
	}

	public void setRestoreId(String restorer) {
		this.restorerId = restorer;
	}

	public Float getSellingPrice() {
		return this.sellingPrice;
	}

	public void setSellingPrice(Float sellingPrice) {
		this.sellingPrice = sellingPrice;
	}

	// public Object getPayload() {
	// return this.payload;
	// }
	//
	// public void setPayload(Object payload) {
	// this.payload = payload;
	// }

	public String toString() {
		return "New Listing: " + this.processId +
				" Verify Details: " + this.verifyDetails +
				" Receive Title: " + this.receiveTitle +
				" vin: " + this.vin +
				" Make: " + this.make +
				" Model: " + this.model +
				" Funding Goal: " + this.fundingGoal;
	}
}
