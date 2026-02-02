package com.company.services;

import java.math.BigInteger;

public interface BlockchainService {
    
    String createProjectContract(String vin, String make, String model, 
                                 BigInteger ccpg, BigInteger fundingGoal, 
                                 String ownerAddress);
    
    void approveProject(String projectAddress);
    
    void saveEstimations(String projectAddress, BigInteger valueEst, BigInteger repairEst);
    
    void setRestorer(String projectAddress, String restorerAddress, BigInteger fundingGoal);
    
    void assignRestoration(String projectAddress);
    
    void openAuction(String projectAddress);
    
    void setBuyer(String projectAddress, String buyerAddress);
    
    void redistribute(String projectAddress);
    
    void editProjectDetails(String projectAddress, String vin, String make, 
                           String model, BigInteger ccpg, BigInteger fundingGoal);

    BigInteger getTokenBalance(String address);
    
    String getTransactionHash();
    
}



