package com.company.request;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CreateProjectBlockchainRequest {
    private String vin;
    private String make;
    private String model;
    private String ccpg;  // Accept as String for easier JSON deserialization
    private String fundingGoal;  // Accept as String for easier JSON deserialization
    private String ownerAddress;

    public CreateProjectBlockchainRequest() {}

    public CreateProjectBlockchainRequest(String vin, String make, String model, 
                                         String ccpg, String fundingGoal, String ownerAddress) {
        this.vin = vin;
        this.make = make;
        this.model = model;
        this.ccpg = ccpg;
        this.fundingGoal = fundingGoal;
        this.ownerAddress = ownerAddress;
    }

    public String getVin() {
        return vin;
    }

    public void setVin(String vin) {
        this.vin = vin;
    }

    public String getMake() {
        return make;
    }

    public void setMake(String make) {
        this.make = make;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    @JsonProperty("ccpg")
    public String getCcppg() {
        return ccpg;
    }

    @JsonProperty("ccpg")
    public void setCcppg(String ccpg) {
        this.ccpg = ccpg;
    }

    public String getFundingGoal() {
        return fundingGoal;
    }

    public void setFundingGoal(String fundingGoal) {
        this.fundingGoal = fundingGoal;
    }

    public String getOwnerAddress() {
        return ownerAddress;
    }

    public void setOwnerAddress(String ownerAddress) {
        this.ownerAddress = ownerAddress;
    }

    @Override
    public String toString() {
        return "CreateProjectBlockchainRequest{" +
                "vin='" + vin + '\'' +
                ", make='" + make + '\'' +
                ", model='" + model + '\'' +
                ", ccpg='" + ccpg + '\'' +
                ", fundingGoal='" + fundingGoal + '\'' +
                ", ownerAddress='" + ownerAddress + '\'' +
                '}';
    }
}

