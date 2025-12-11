package com.company.utils;

import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.Uint;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.contracts.eip20.generated.ERC20;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.RawTransaction;
import org.web3j.crypto.TransactionEncoder;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.request.Transaction;
import org.web3j.protocol.core.methods.response.EthGetBalance;
import org.web3j.protocol.core.methods.response.EthSendTransaction;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.RawTransactionManager;
import org.web3j.tx.TransactionManager;
import org.web3j.utils.Convert;
import org.web3j.utils.Numeric;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Web3 utility class for blockchain interactions
 * Handles contract interactions with CCToken, CCProjectFactory, and CCProject contracts
 */
public class Web3Utils {
    
    private static final String CCTOKEN_ADDRESS = "0x60c5eb023F7778031F542ece23dff7165a515CC0";
    private static final String FACTORY_ADDRESS = "0xc2B45f7DfCf4Fb869133cC35BEdaA9c63D95Fda3";
    private static final String RPC_URL = "http://localhost:8545"; // Update with your RPC endpoint
    
    private Web3j web3j;
    private Credentials credentials;
    
    public Web3Utils(String rpcUrl, String privateKey) {
        this.web3j = Web3j.build(new HttpService(rpcUrl));
        this.credentials = Credentials.create(privateKey);
    }
    
    public Web3Utils() {
        this.web3j = Web3j.build(new HttpService(RPC_URL));
    }

    /**
     * Get CC Token balance for a given address
     */
    public String getCCTokenBalance(String walletAddress) throws Exception {
        try {
            EthGetBalance balance = web3j.ethGetBalance(walletAddress, DefaultBlockParameterName.LATEST).send();
            BigDecimal tokenBalance = Convert.fromWei(new BigDecimal(balance.getBalance()), Convert.Unit.ETHER);
            return tokenBalance.toString();
        } catch (Exception ex) {
            throw new RuntimeException("Web3 Error: " + ex.getMessage(), ex);
        }
    }

    /**
     * Append zeros to amount (for decimal conversion)
     */
    public static BigInteger appendZeros(String amount, int count) {
        String result = amount;
        for (int i = 0; i < count; i++) {
            result = result + "0";
        }
        return new BigInteger(result);
    }

    /**
     * Append zeros to amount (for BigInteger)
     */
    public static BigInteger appendZeros(BigInteger amount, int count) {
        return appendZeros(amount.toString(), count);
    }

    /**
     * Approve funding for a project
     */
    public String approveFunding(String projectAddress, String amount) throws Exception {
        try {
            TransactionManager transactionManager = new RawTransactionManager(web3j, credentials);
            
            // Create approve function
            List<Type> inputParameters = new ArrayList<>();
            inputParameters.add(new Address(projectAddress));
            inputParameters.add(new Uint256(appendZeros(amount, 18)));
            
            List<TypeReference<?>> outputParameters = new ArrayList<>();
            outputParameters.add(new TypeReference<>() {});
            
            Function function = new Function("increaseAllowance", inputParameters, outputParameters);
            String encodedFunction = FunctionEncoder.encode(function);
            
            EthSendTransaction response = web3j.ethSendRawTransaction(encodedFunction).send();
            
            if (response.hasError()) {
                throw new RuntimeException("Transaction failed: " + response.getError().getMessage());
            }
            
            return response.getTransactionHash();
        } catch (Exception ex) {
            throw new RuntimeException("Web3 Error: " + ex.getMessage(), ex);
        }
    }

    /**
     * Create a new CC Project contract
     */
    public String createCCProjectContract(String walletAddress, String vin, String make, String model, 
                                         String ccpg, String fundingGoal) throws Exception {
        try {
            TransactionManager transactionManager = new RawTransactionManager(web3j, credentials);
            
            // Create createNewProject function
            List<Type> inputParameters = new ArrayList<>();
            inputParameters.add(new org.web3j.abi.datatypes.Utf8String(vin));
            inputParameters.add(new org.web3j.abi.datatypes.Utf8String(make));
            inputParameters.add(new org.web3j.abi.datatypes.Utf8String(model));
            inputParameters.add(new Uint256(appendZeros(ccpg, 18)));
            inputParameters.add(new Uint256(appendZeros(fundingGoal, 18)));
            inputParameters.add(new Address(walletAddress));
            
            List<TypeReference<?>> outputParameters = new ArrayList<>();
            
            Function function = new Function("createNewProject", inputParameters, outputParameters);
            String encodedFunction = FunctionEncoder.encode(function);
            
            EthSendTransaction response = web3j.ethSendRawTransaction(encodedFunction).send();
            
            if (response.hasError()) {
                throw new RuntimeException("Transaction failed: " + response.getError().getMessage());
            }
            
            return response.getTransactionHash();
        } catch (Exception ex) {
            throw new RuntimeException("Web3 Error: " + ex.getMessage(), ex);
        }
    }

    /**
     * Approve a CC Project listing
     */
    public String approveCCProjectListing(String projectAddress) throws Exception {
        try {
            TransactionManager transactionManager = new RawTransactionManager(web3j, credentials);
            
            List<Type> inputParameters = new ArrayList<>();
            inputParameters.add(new Address(projectAddress));
            
            List<TypeReference<?>> outputParameters = new ArrayList<>();
            
            Function function = new Function("approveProject", inputParameters, outputParameters);
            String encodedFunction = FunctionEncoder.encode(function);
            
            EthSendTransaction response = web3j.ethSendRawTransaction(encodedFunction).send();
            
            if (response.hasError()) {
                throw new RuntimeException("Transaction failed: " + response.getError().getMessage());
            }
            
            return response.getTransactionHash();
        } catch (Exception ex) {
            throw new RuntimeException("Web3 Error: " + ex.getMessage(), ex);
        }
    }

    /**
     * Save estimations for a project
     */
    public String saveEstCCProjectListing(String projectAddress, BigInteger valueEstimate, 
                                         BigInteger repairEstimate) throws Exception {
        try {
            TransactionManager transactionManager = new RawTransactionManager(web3j, credentials);
            
            List<Type> inputParameters = new ArrayList<>();
            inputParameters.add(new Address(projectAddress));
            inputParameters.add(new Uint256(valueEstimate));
            inputParameters.add(new Uint256(repairEstimate));
            
            List<TypeReference<?>> outputParameters = new ArrayList<>();
            
            Function function = new Function("saveEstimations", inputParameters, outputParameters);
            String encodedFunction = FunctionEncoder.encode(function);
            
            EthSendTransaction response = web3j.ethSendRawTransaction(encodedFunction).send();
            
            if (response.hasError()) {
                throw new RuntimeException("Transaction failed: " + response.getError().getMessage());
            }
            
            return response.getTransactionHash();
        } catch (Exception ex) {
            throw new RuntimeException("Web3 Error: " + ex.getMessage(), ex);
        }
    }

    /**
     * Set restorer for a project
     */
    public String setRestorerCCProjectListing(String projectAddress, String restorerAddress, 
                                             String fundingGoal) throws Exception {
        try {
            TransactionManager transactionManager = new RawTransactionManager(web3j, credentials);
            
            List<Type> inputParameters = new ArrayList<>();
            inputParameters.add(new Address(projectAddress));
            inputParameters.add(new Address(restorerAddress));
            inputParameters.add(new Uint256(appendZeros(fundingGoal, 18)));
            
            List<TypeReference<?>> outputParameters = new ArrayList<>();
            
            Function function = new Function("setRestorer", inputParameters, outputParameters);
            String encodedFunction = FunctionEncoder.encode(function);
            
            EthSendTransaction response = web3j.ethSendRawTransaction(encodedFunction).send();
            
            if (response.hasError()) {
                throw new RuntimeException("Transaction failed: " + response.getError().getMessage());
            }
            
            return response.getTransactionHash();
        } catch (Exception ex) {
            throw new RuntimeException("Web3 Error: " + ex.getMessage(), ex);
        }
    }

    /**
     * Assign project for restoration
     */
    public String assignRestorationCCProjectListing(String projectAddress) throws Exception {
        try {
            TransactionManager transactionManager = new RawTransactionManager(web3j, credentials);
            
            List<Type> inputParameters = new ArrayList<>();
            inputParameters.add(new Address(projectAddress));
            
            List<TypeReference<?>> outputParameters = new ArrayList<>();
            
            Function function = new Function("assignForRestoration", inputParameters, outputParameters);
            String encodedFunction = FunctionEncoder.encode(function);
            
            EthSendTransaction response = web3j.ethSendRawTransaction(encodedFunction).send();
            
            if (response.hasError()) {
                throw new RuntimeException("Transaction failed: " + response.getError().getMessage());
            }
            
            return response.getTransactionHash();
        } catch (Exception ex) {
            throw new RuntimeException("Web3 Error: " + ex.getMessage(), ex);
        }
    }

    /**
     * Open auction for a project
     */
    public String openAuctionCCProjectListing(String projectAddress) throws Exception {
        try {
            TransactionManager transactionManager = new RawTransactionManager(web3j, credentials);
            
            List<Type> inputParameters = new ArrayList<>();
            inputParameters.add(new Address(projectAddress));
            
            List<TypeReference<?>> outputParameters = new ArrayList<>();
            
            Function function = new Function("setAuctionOpen", inputParameters, outputParameters);
            String encodedFunction = FunctionEncoder.encode(function);
            
            EthSendTransaction response = web3j.ethSendRawTransaction(encodedFunction).send();
            
            if (response.hasError()) {
                throw new RuntimeException("Transaction failed: " + response.getError().getMessage());
            }
            
            return response.getTransactionHash();
        } catch (Exception ex) {
            throw new RuntimeException("Web3 Error: " + ex.getMessage(), ex);
        }
    }

    /**
     * Set buyer for a project
     */
    public String setBuyerCCProjectListing(String projectAddress, String buyerAddress) throws Exception {
        try {
            TransactionManager transactionManager = new RawTransactionManager(web3j, credentials);
            
            List<Type> inputParameters = new ArrayList<>();
            inputParameters.add(new Address(projectAddress));
            inputParameters.add(new Address(buyerAddress));
            
            List<TypeReference<?>> outputParameters = new ArrayList<>();
            
            Function function = new Function("setBuyer", inputParameters, outputParameters);
            String encodedFunction = FunctionEncoder.encode(function);
            
            EthSendTransaction response = web3j.ethSendRawTransaction(encodedFunction).send();
            
            if (response.hasError()) {
                throw new RuntimeException("Transaction failed: " + response.getError().getMessage());
            }
            
            return response.getTransactionHash();
        } catch (Exception ex) {
            throw new RuntimeException("Web3 Error: " + ex.getMessage(), ex);
        }
    }

    /**
     * Redistribute funds for a project
     */
    public String redistributeCCProjectListing(String projectAddress) throws Exception {
        try {
            TransactionManager transactionManager = new RawTransactionManager(web3j, credentials);
            
            List<Type> inputParameters = new ArrayList<>();
            inputParameters.add(new Address(projectAddress));
            
            List<TypeReference<?>> outputParameters = new ArrayList<>();
            
            Function function = new Function("redistribute", inputParameters, outputParameters);
            String encodedFunction = FunctionEncoder.encode(function);
            
            EthSendTransaction response = web3j.ethSendRawTransaction(encodedFunction).send();
            
            if (response.hasError()) {
                throw new RuntimeException("Transaction failed: " + response.getError().getMessage());
            }
            
            return response.getTransactionHash();
        } catch (Exception ex) {
            throw new RuntimeException("Web3 Error: " + ex.getMessage(), ex);
        }
    }

    /**
     * Update project listing details
     */
    public String updateCCProjectListing(String projectAddress, String vin, String make, String model,
                                        String ccpg, String fundingGoal) throws Exception {
        try {
            TransactionManager transactionManager = new RawTransactionManager(web3j, credentials);
            
            List<Type> inputParameters = new ArrayList<>();
            inputParameters.add(new Address(projectAddress));
            inputParameters.add(new org.web3j.abi.datatypes.Utf8String(vin));
            inputParameters.add(new org.web3j.abi.datatypes.Utf8String(make));
            inputParameters.add(new org.web3j.abi.datatypes.Utf8String(model));
            inputParameters.add(new Uint256(appendZeros(ccpg, 18)));
            inputParameters.add(new Uint256(appendZeros(fundingGoal, 18)));
            
            List<TypeReference<?>> outputParameters = new ArrayList<>();
            
            Function function = new Function("editProjectDetails", inputParameters, outputParameters);
            String encodedFunction = FunctionEncoder.encode(function);
            
            EthSendTransaction response = web3j.ethSendRawTransaction(encodedFunction).send();
            
            if (response.hasError()) {
                throw new RuntimeException("Transaction failed: " + response.getError().getMessage());
            }
            
            return response.getTransactionHash();
        } catch (Exception ex) {
            throw new RuntimeException("Web3 Error: " + ex.getMessage(), ex);
        }
    }

    /**
     * Invest in a project
     */
    public String investInCCProjectListing(String projectAddress, String amount) throws Exception {
        try {
            // First approve funding
            approveFunding(projectAddress, amount);
            
            TransactionManager transactionManager = new RawTransactionManager(web3j, credentials);
            
            List<Type> inputParameters = new ArrayList<>();
            inputParameters.add(new Uint256(appendZeros(amount, 18)));
            
            List<TypeReference<?>> outputParameters = new ArrayList<>();
            
            Function function = new Function("acceptFunds", inputParameters, outputParameters);
            String encodedFunction = FunctionEncoder.encode(function);
            
            EthSendTransaction response = web3j.ethSendRawTransaction(encodedFunction).send();
            
            if (response.hasError()) {
                throw new RuntimeException("Transaction failed: " + response.getError().getMessage());
            }
            
            return response.getTransactionHash();
        } catch (Exception ex) {
            throw new RuntimeException("Web3 Error: " + ex.getMessage(), ex);
        }
    }

    /**
     * Place a bid for a project in auction
     */
    public String bidForCCProjectListing(String projectAddress, String amount) throws Exception {
        try {
            // First approve funding
            approveFunding(projectAddress, amount);
            
            TransactionManager transactionManager = new RawTransactionManager(web3j, credentials);
            
            List<Type> inputParameters = new ArrayList<>();
            inputParameters.add(new Uint256(appendZeros(amount, 18)));
            
            List<TypeReference<?>> outputParameters = new ArrayList<>();
            
            Function function = new Function("addNewAuctionBid", inputParameters, outputParameters);
            String encodedFunction = FunctionEncoder.encode(function);
            
            EthSendTransaction response = web3j.ethSendRawTransaction(encodedFunction).send();
            
            if (response.hasError()) {
                throw new RuntimeException("Transaction failed: " + response.getError().getMessage());
            }
            
            return response.getTransactionHash();
        } catch (Exception ex) {
            throw new RuntimeException("Web3 Error: " + ex.getMessage(), ex);
        }
    }

    /**
     * Get contract addresses
     */
    public String getCCTokenAddress() {
        return CCTOKEN_ADDRESS;
    }

    public String getFactoryAddress() {
        return FACTORY_ADDRESS;
    }
}
