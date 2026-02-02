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
    
    /**
     * Execute a blockchain transaction and wait for confirmation
     */
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
        
        // Send transaction
        org.web3j.protocol.core.methods.response.EthSendTransaction response = 
            transactionManager.sendTransaction(gasPrice, gasLimit, contractAddress, encodedFunction, BigInteger.ZERO);
        
        if (response.hasError()) {
            throw new RuntimeException("Transaction failed: " + response.getError().getMessage());
        }
        
        String txHash = response.getTransactionHash();
        lastTransactionHash = txHash;
        logger.info("Transaction sent: {}", txHash);
        
        // Wait for receipt
        TransactionReceipt receipt = waitForTransactionReceipt(txHash);
        
        // Verify transaction succeeded
        String status = receipt.getStatus();
        if (status != null && (status.equals("0x0") || status.equals("0x00"))) {
            throw new RuntimeException("Transaction reverted. Hash: " + txHash);
        }
        
        logger.info("Transaction confirmed: {}", txHash);
        return receipt;
    }
    
    /**
     * Wait for transaction receipt
     */
    private TransactionReceipt waitForTransactionReceipt(String txHash) throws Exception {
        // For Hardhat: fast polling (100ms), max 5 seconds
        // For production: would use longer intervals (3s), max 2 minutes
        long pollInterval = 100; // milliseconds
        int maxAttempts = 50; // 50 * 100ms = 5 seconds max
        
        for (int i = 0; i < maxAttempts; i++) {
            org.web3j.protocol.core.methods.response.EthGetTransactionReceipt response = 
                web3j.ethGetTransactionReceipt(txHash).send();
            
            if (response.getTransactionReceipt().isPresent()) {
                return response.getTransactionReceipt().get();
            }
            
            Thread.sleep(pollInterval);
        }
        
        throw new RuntimeException("Transaction receipt not found after " + maxAttempts + " attempts. Hash: " + txHash);
    }
    
    /**
     * Helper method to get project addresses from factory
     */
    @SuppressWarnings("unchecked")
    private List<Address> getProjects() throws Exception {
        Function getProjectsFunction = new Function(
            "getProjects",
            Collections.emptyList(),
            Collections.singletonList(new TypeReference<DynamicArray<Address>>() {})
        );
        
        String encodedFunction = FunctionEncoder.encode(getProjectsFunction);
        EthCall response = web3j.ethCall(
            Transaction.createEthCallTransaction(adminCredentials.getAddress(), factoryAddress, encodedFunction),
            DefaultBlockParameterName.LATEST
        ).send();
        
        if (response.hasError() || response.getValue() == null || response.getValue().isEmpty() || response.getValue().equals("0x")) {
            return Collections.emptyList();
        }
        
        List<Type> result = FunctionReturnDecoder.decode(response.getValue(), getProjectsFunction.getOutputParameters());
        if (result == null || result.isEmpty()) {
            return Collections.emptyList();
        }
        
        return (List<Address>) result.get(0).getValue();
    }
    
    @Override
    public String createProjectContract(String vin, String make, String model, 
                                       BigInteger ccpg, BigInteger fundingGoal, 
                                       String ownerAddress) {
        try {
            // Get project count before creation
            int projectCountBefore = getProjects().size();
            
            // Create project transaction
            Function createFunction = new Function(
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
            
            TransactionReceipt receipt = executeTransaction(createFunction, factoryAddress);
            logger.info("Project creation transaction confirmed: {}", receipt.getTransactionHash());
            
            // Poll getProjects() until count increases
            List<Address> projectAddresses;
            while (true) {
                projectAddresses = getProjects();
                if (projectAddresses.size() > projectCountBefore) {
                    break;
                }
                Thread.sleep(500);
            }
            
            String projectAddress = projectAddresses.get(projectAddresses.size() - 1).getValue();
            logger.info("Created project at address: {}", projectAddress);
            return projectAddress;
            
        } catch (Exception e) {
            logger.error("Error creating project contract", e);
            throw new RuntimeException("Blockchain transaction failed: " + e.getMessage(), e);
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

