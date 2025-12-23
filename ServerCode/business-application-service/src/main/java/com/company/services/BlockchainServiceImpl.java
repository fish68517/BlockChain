package com.company.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.FunctionReturnDecoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.Utf8String;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.abi.datatypes.DynamicArray;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.request.Transaction;
import org.web3j.protocol.core.methods.response.EthCall;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.tx.RawTransactionManager;
import org.web3j.tx.gas.ContractGasProvider;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class BlockchainServiceImpl implements BlockchainService {
    
    private static final Logger logger = LoggerFactory.getLogger(BlockchainServiceImpl.class);
    
    @Autowired
    private Web3j web3j;
    
    @Autowired
    private Credentials adminCredentials;
    
    @Autowired
    private ContractGasProvider gasProvider;
    
    @Value("${blockchain.contract.factory.address}")
    private String factoryAddress;
    
    @Value("${blockchain.contract.token.address}")
    private String tokenAddress;
    
    private String lastTransactionHash;
    
    // Helper method to execute transaction
    private TransactionReceipt executeTransaction(Function function, String contractAddress) throws Exception {
        String encodedFunction = FunctionEncoder.encode(function);
        
        // RawTransactionManager transactionManager = new RawTransactionManager(
        //     web3j, adminCredentials, 11155111L // Sepolia chain ID
        // );
        
        RawTransactionManager transactionManager = new RawTransactionManager(
            web3j, adminCredentials, 31337L // Hardhat local chain ID
        );

        BigInteger gasLimit = gasProvider.getGasLimit(contractAddress);
        BigInteger gasPrice = gasProvider.getGasPrice(contractAddress);
        
        org.web3j.protocol.core.methods.response.EthSendTransaction ethSendTransaction = 
            transactionManager.sendTransaction(gasPrice, gasLimit, contractAddress, encodedFunction, BigInteger.ZERO);
        
        if (ethSendTransaction.hasError()) {
            throw new RuntimeException("Transaction failed: " + ethSendTransaction.getError().getMessage());
        }
        
        String txHash = ethSendTransaction.getTransactionHash();
        lastTransactionHash = txHash;
        logger.info("Transaction sent: {}", txHash);
        
        // Poll for transaction receipt
        org.web3j.protocol.core.methods.response.TransactionReceipt receipt = null;
        int attempts = 0;
        int maxAttempts = 40; // Wait up to ~2 minutes (40 * 3 seconds)
        
        while (attempts < maxAttempts) {
            org.web3j.protocol.core.methods.response.EthGetTransactionReceipt ethGetTransactionReceipt = 
                web3j.ethGetTransactionReceipt(txHash).send();
            
            if (ethGetTransactionReceipt.getTransactionReceipt().isPresent()) {
                receipt = ethGetTransactionReceipt.getTransactionReceipt().get();
                break;
            }
            
            Thread.sleep(3000); // Wait 3 seconds before next attempt
            attempts++;
        }
        
        if (receipt == null) {
            throw new RuntimeException("Transaction receipt not found after " + maxAttempts + " attempts");
        }
        
        // Check transaction status - "0x1" means success, "0x0" means failure
        String status = receipt.getStatus();
        if (status != null && (status.equals("0x0") || status.equals("0x00"))) {
            throw new RuntimeException("Transaction reverted. Transaction hash: " + txHash + ". Check if the contract call failed or ran out of gas.");
        }
        
        logger.info("Transaction confirmed: {}", txHash);
        return receipt;
    }
    
    @Override
    public String createProjectContract(String vin, String make, String model, 
                                       BigInteger ccpg, BigInteger fundingGoal, 
                                       String ownerAddress) {
        try {
            // Validate and ensure BigInteger values are not null
            if (ccpg == null ||fundingGoal == null || vin == null || make == null || model == null || ownerAddress == null) {
                throw new IllegalArgumentException("All string parameters must be non-null");
            }
            
            // Get the project count BEFORE sending the transaction
            Function getProjectsFunction = new Function(
                "getProjects",
                Collections.emptyList(),
                Collections.singletonList(new TypeReference<DynamicArray<Address>>() {})
            );
            
            String encodedFunction = FunctionEncoder.encode(getProjectsFunction);
            int projectCountBefore = 0;
            
            try {
                EthCall initialResponse = web3j.ethCall(
                    Transaction.createEthCallTransaction(
                        adminCredentials.getAddress(), 
                        factoryAddress, 
                        encodedFunction
                    ),
                    DefaultBlockParameterName.LATEST
                ).send();
                
                if (!initialResponse.hasError() && initialResponse.getValue() != null && 
                    !initialResponse.getValue().isEmpty() && !initialResponse.getValue().equals("0x")) {
                    List<Type> initialResult = FunctionReturnDecoder.decode(
                        initialResponse.getValue(), 
                        getProjectsFunction.getOutputParameters()
                    );
                    if (initialResult != null && !initialResult.isEmpty()) {
                        @SuppressWarnings("unchecked")
                        List<Address> initialProjects = (List<Address>) initialResult.get(0).getValue();
                        projectCountBefore = initialProjects != null ? initialProjects.size() : 0;
                    }
                }
            } catch (Exception e) {
                projectCountBefore = 0; // Assume 0 if we can't get it
            }
            
            // Now send the transaction
            Function function = new Function(
                "createNewProject",
                Arrays.asList(
                    new Utf8String(vin),
                    new Utf8String(make),
                    new Utf8String(model),
                    new Uint256(ccpg),
                    new Uint256(fundingGoal),
                    new Address(ownerAddress)
                ),
                Collections.singletonList(new TypeReference<Address>() {})
            );
            
            TransactionReceipt receipt = executeTransaction(function, factoryAddress);
            
            // Check if transaction was successful
            String status = receipt.getStatus();
            if (status != null && !status.equals("0x1") && !status.equals("0x01")) {
                throw new RuntimeException("Transaction failed with status: " + status + ". Transaction hash: " + receipt.getTransactionHash());
            }
            
            // Wait a moment for state to update
            Thread.sleep(500);
            
            // Poll getProjects() until the count increases (max 10 attempts)
            for (int attempt = 0; attempt < 10; attempt++) {
                if (attempt > 0) {
                    Thread.sleep(1000); // Wait 1 second between attempts (not before first attempt)
                }
                
                EthCall response = web3j.ethCall(
                    Transaction.createEthCallTransaction(
                        adminCredentials.getAddress(), 
                        factoryAddress, 
                        encodedFunction
                    ),
                    DefaultBlockParameterName.LATEST
                ).send();
                
                if (response.hasError()) {
                    continue;
                }
                
                if (response.getValue() != null && !response.getValue().isEmpty() && !response.getValue().equals("0x")) {
                    List<Type> result = FunctionReturnDecoder.decode(
                        response.getValue(), 
                        getProjectsFunction.getOutputParameters()
                    );
                    
                    if (result != null && !result.isEmpty()) {
                        @SuppressWarnings("unchecked")
                        List<Address> projects = (List<Address>) result.get(0).getValue();
                        
                        if (projects != null && !projects.isEmpty()) {
                            int currentCount = projects.size();
                            
                            // If count increased, return the new project
                            if (currentCount > projectCountBefore) {
                                String projectAddress = projects.get(projects.size() - 1).getValue();
                                logger.info("Created project at address: {}", projectAddress);
                                return projectAddress;
                            }
                        }
                    }
                }
            }
            
            throw new RuntimeException("Could not extract project address after polling getProjects() 10 times. Transaction hash: " + receipt.getTransactionHash() + ", status: " + receipt.getStatus());
            
        } catch (Exception e) {
            logger.error("Error creating project contract", e);
            String errorMsg = e.getMessage();
            if (errorMsg == null || errorMsg.isEmpty()) {
                errorMsg = e.getClass().getSimpleName();
                if (e.getCause() != null && e.getCause().getMessage() != null) {
                    errorMsg += ": " + e.getCause().getMessage();
                }
            }
            throw new RuntimeException("Blockchain transaction failed: " + errorMsg, e);
        }
    }
    
    @Override
    public void approveProject(String projectAddress) {
        try {
            Function function = new Function(
                "approveProject",
                Collections.singletonList(new Address(projectAddress)),
                Collections.emptyList()
            );
            
            executeTransaction(function, factoryAddress);
            logger.info("Project approved: {}", projectAddress);
            
        } catch (Exception e) {
            logger.error("Error approving project", e);
            throw new RuntimeException("Blockchain transaction failed: " + e.getMessage(), e);
        }
    }
    
    @Override
    public void saveEstimations(String projectAddress, BigInteger valueEst, BigInteger repairEst) {
        try {
            Function function = new Function(
                "saveEstimations",
                Arrays.asList(
                    new Address(projectAddress),
                    new Uint256(valueEst),
                    new Uint256(repairEst)
                ),
                Collections.emptyList()
            );
            
            executeTransaction(function, factoryAddress);
            logger.info("Estimations saved for project: {}", projectAddress);
            
        } catch (Exception e) {
            logger.error("Error saving estimations", e);
            throw new RuntimeException("Blockchain transaction failed: " + e.getMessage(), e);
        }
    }
    
    @Override
    public void setRestorer(String projectAddress, String restorerAddress, BigInteger fundingGoal) {
        try {
            Function function = new Function(
                "setRestorer",
                Arrays.asList(
                    new Address(projectAddress),
                    new Address(restorerAddress),
                    new Uint256(fundingGoal)
                ),
                Collections.emptyList()
            );
            
            executeTransaction(function, factoryAddress);
            logger.info("Restorer set for project: {}", projectAddress);
            
        } catch (Exception e) {
            logger.error("Error setting restorer", e);
            throw new RuntimeException("Blockchain transaction failed: " + e.getMessage(), e);
        }
    }
    
    @Override
    public void assignRestoration(String projectAddress) {
        try {
            Function function = new Function(
                "assignForRestoration",
                Collections.singletonList(new Address(projectAddress)),
                Collections.emptyList()
            );
            
            executeTransaction(function, factoryAddress);
            logger.info("Restoration assigned for project: {}", projectAddress);
            
        } catch (Exception e) {
            logger.error("Error assigning restoration", e);
            throw new RuntimeException("Blockchain transaction failed: " + e.getMessage(), e);
        }
    }
    
    @Override
    public void openAuction(String projectAddress) {
        try {
            Function function = new Function(
                "setAuctionOpen",
                Collections.singletonList(new Address(projectAddress)),
                Collections.emptyList()
            );
            
            executeTransaction(function, factoryAddress);
            logger.info("Auction opened for project: {}", projectAddress);
            
        } catch (Exception e) {
            logger.error("Error opening auction", e);
            throw new RuntimeException("Blockchain transaction failed: " + e.getMessage(), e);
        }
    }
    
    @Override
    public void setBuyer(String projectAddress, String buyerAddress) {
        try {
            Function function = new Function(
                "setBuyer",
                Arrays.asList(
                    new Address(projectAddress),
                    new Address(buyerAddress)
                ),
                Collections.emptyList()
            );
            
            executeTransaction(function, factoryAddress);
            logger.info("Buyer set for project: {}", projectAddress);
            
        } catch (Exception e) {
            logger.error("Error setting buyer", e);
            throw new RuntimeException("Blockchain transaction failed: " + e.getMessage(), e);
        }
    }
    
    @Override
    public void redistribute(String projectAddress) {
        try {
            Function function = new Function(
                "redistribute",
                Collections.singletonList(new Address(projectAddress)),
                Collections.emptyList()
            );
            
            executeTransaction(function, factoryAddress);
            logger.info("Redistribution completed for project: {}", projectAddress);
            
        } catch (Exception e) {
            logger.error("Error redistributing", e);
            throw new RuntimeException("Blockchain transaction failed: " + e.getMessage(), e);
        }
    }
    
    @Override
    public void editProjectDetails(String projectAddress, String vin, String make, 
                                   String model, BigInteger ccpg, BigInteger fundingGoal) {
        try {
            Function function = new Function(
                "editProjectDetails",
                Arrays.asList(
                    new Address(projectAddress),
                    new Utf8String(vin),
                    new Utf8String(make),
                    new Utf8String(model),
                    new Uint256(ccpg),
                    new Uint256(fundingGoal)
                ),
                Collections.emptyList()
            );
            
            executeTransaction(function, factoryAddress);
            logger.info("Project details updated: {}", projectAddress);
            
        } catch (Exception e) {
            logger.error("Error editing project details", e);
            throw new RuntimeException("Blockchain transaction failed: " + e.getMessage(), e);
        }
    }
    
    @Override
    public BigInteger getTokenBalance(String address) {
        try {
            Function function = new Function(
                "balanceOf",
                Collections.singletonList(new Address(address)),
                Collections.singletonList(new TypeReference<Uint256>() {})
            );
            
            String encodedFunction = FunctionEncoder.encode(function);
            EthCall response = web3j.ethCall(
                Transaction.createEthCallTransaction(address, tokenAddress, encodedFunction),
                DefaultBlockParameterName.LATEST
            ).send();
            
            List<Type> result = FunctionReturnDecoder.decode(
                response.getValue(), 
                function.getOutputParameters()
            );
            
            if (!result.isEmpty()) {
                return (BigInteger) result.get(0).getValue();
            }
            
            return BigInteger.ZERO;
            
        } catch (Exception e) {
            logger.error("Error getting token balance", e);
            return BigInteger.ZERO;
        }
    }
    
    @Override
    public String getTransactionHash() {
        return lastTransactionHash;
    }
}

