package com.company.request;

import java.math.BigInteger;

public class EditProjectBlockchainRequest {
    private String projectAddress;
    private String vin;
    private String make;
    private String model;
    private BigInteger ccpg;
    private BigInteger fundingGoal;

    public EditProjectBlockchainRequest() {}

    public String getProjectAddress() {
        return projectAddress;
    }

    public void setProjectAddress(String projectAddress) {
        this.projectAddress = projectAddress;
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

    public BigInteger getCcppg() {
        return ccpg;
    }

    public void setCcppg(BigInteger ccpg) {
        this.ccpg = ccpg;
    }

    public BigInteger getFundingGoal() {
        return fundingGoal;
    }

    public void setFundingGoal(BigInteger fundingGoal) {
        this.fundingGoal = fundingGoal;
    }
}



