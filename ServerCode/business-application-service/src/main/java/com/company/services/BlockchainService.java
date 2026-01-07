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
    
    /**
     * Verify a blockchain transaction by hash
     * @param transactionHash The transaction hash to verify
     * @return true if transaction exists and was successful, false otherwise
     */
    boolean verifyTransaction(String transactionHash);
    
    /**
     * Get transaction receipt for a given transaction hash
     * @param transactionHash The transaction hash
     * @return TransactionReceipt if found, null otherwise
     */
    org.web3j.protocol.core.methods.response.TransactionReceipt getTransactionReceipt(String transactionHash);
}



