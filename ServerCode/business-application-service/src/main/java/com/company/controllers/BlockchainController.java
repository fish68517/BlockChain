package com.company.controllers;

import com.company.utils.Web3Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.math.BigInteger;

/**
 * REST Controller for blockchain operations
 * Handles all blockchain interactions for the CollectorCoin application
 */
@RestController
@RequestMapping("/api/blockchain")
@CrossOrigin(origins = "*")
public class BlockchainController {
    
    @Autowired
    private Web3Utils web3Utils;

    /**
     * Get CC Token balance for a wallet address
     */
    @GetMapping("/token-balance/{walletAddress}")
    public ResponseEntity<?> getCCTokenBalance(@PathVariable String walletAddress) {
        try {
            String balance = web3Utils.getCCTokenBalance(walletAddress);
            return ResponseEntity.ok().body(new TokenBalanceResponse(walletAddress, balance));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ErrorResponse("Error fetching token balance: " + e.getMessage()));
        }
    }

    /**
     * Approve funding for a project
     */
    @PostMapping("/approve-funding")
    public ResponseEntity<?> approveFunding(@RequestBody ApprovalRequest request) {
        try {
            String txHash = web3Utils.approveFunding(request.getProjectAddress(), request.getAmount());
            return ResponseEntity.ok().body(new TransactionResponse("Funding approved", txHash));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ErrorResponse("Error approving funding: " + e.getMessage()));
        }
    }

    /**
     * Create a new CC Project contract
     */
    @PostMapping("/create-project")
    public ResponseEntity<?> createCCProjectContract(@RequestBody CreateProjectRequest request) {
        try {
            String projectAddress = web3Utils.createCCProjectContract(
                request.getWalletAddress(),
                request.getVin(),
                request.getMake(),
                request.getModel(),
                request.getCcpg(),
                request.getFundingGoal()
            );
            return ResponseEntity.ok().body(new ProjectCreationResponse("Project created", projectAddress));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ErrorResponse("Error creating project: " + e.getMessage()));
        }
    }

    /**
     * Approve a project listing
     */
    @PostMapping("/approve-project")
    public ResponseEntity<?> approveCCProjectListing(@RequestBody ProjectActionRequest request) {
        try {
            String txHash = web3Utils.approveCCProjectListing(request.getProjectAddress());
            return ResponseEntity.ok().body(new TransactionResponse("Project approved", txHash));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ErrorResponse("Error approving project: " + e.getMessage()));
        }
    }

    /**
     * Save estimations for a project
     */
    @PostMapping("/save-estimations")
    public ResponseEntity<?> saveEstCCProjectListing(@RequestBody EstimationRequest request) {
        try {
            String txHash = web3Utils.saveEstCCProjectListing(
                request.getProjectAddress(),
                request.getValueEstimate(),
                request.getRepairEstimate()
            );
            return ResponseEntity.ok().body(new TransactionResponse("Estimations saved", txHash));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ErrorResponse("Error saving estimations: " + e.getMessage()));
        }
    }

    /**
     * Set restorer for a project
     */
    @PostMapping("/set-restorer")
    public ResponseEntity<?> setRestorerCCProjectListing(@RequestBody SetRestorerRequest request) {
        try {
            String txHash = web3Utils.setRestorerCCProjectListing(
                request.getProjectAddress(),
                request.getRestorerAddress(),
                request.getFundingGoal()
            );
            return ResponseEntity.ok().body(new TransactionResponse("Restorer assigned", txHash));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ErrorResponse("Error setting restorer: " + e.getMessage()));
        }
    }

    /**
     * Assign project for restoration
     */
    @PostMapping("/assign-restoration")
    public ResponseEntity<?> assignRestorationCCProjectListing(@RequestBody ProjectActionRequest request) {
        try {
            String txHash = web3Utils.assignRestorationCCProjectListing(request.getProjectAddress());
            return ResponseEntity.ok().body(new TransactionResponse("Project assigned for restoration", txHash));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ErrorResponse("Error assigning for restoration: " + e.getMessage()));
        }
    }

    /**
     * Open auction for a project
     */
    @PostMapping("/open-auction")
    public ResponseEntity<?> openAuctionCCProjectListing(@RequestBody ProjectActionRequest request) {
        try {
            String txHash = web3Utils.openAuctionCCProjectListing(request.getProjectAddress());
            return ResponseEntity.ok().body(new TransactionResponse("Auction opened", txHash));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ErrorResponse("Error opening auction: " + e.getMessage()));
        }
    }

    /**
     * Set buyer for a project
     */
    @PostMapping("/set-buyer")
    public ResponseEntity<?> setBuyerCCProjectListing(@RequestBody SetBuyerRequest request) {
        try {
            String txHash = web3Utils.setBuyerCCProjectListing(
                request.getProjectAddress(),
                request.getBuyerAddress()
            );
            return ResponseEntity.ok().body(new TransactionResponse("Buyer assigned", txHash));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ErrorResponse("Error setting buyer: " + e.getMessage()));
        }
    }

    /**
     * Redistribute funds for a project
     */
    @PostMapping("/redistribute")
    public ResponseEntity<?> redistributeCCProjectListing(@RequestBody ProjectActionRequest request) {
        try {
            String txHash = web3Utils.redistributeCCProjectListing(request.getProjectAddress());
            return ResponseEntity.ok().body(new TransactionResponse("Funds redistributed", txHash));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ErrorResponse("Error redistributing funds: " + e.getMessage()));
        }
    }

    /**
     * Update project listing details
     */
    @PutMapping("/update-project")
    public ResponseEntity<?> updateCCProjectListing(@RequestBody UpdateProjectRequest request) {
        try {
            String txHash = web3Utils.updateCCProjectListing(
                request.getProjectAddress(),
                request.getVin(),
                request.getMake(),
                request.getModel(),
                request.getCcpg(),
                request.getFundingGoal()
            );
            return ResponseEntity.ok().body(new TransactionResponse("Project updated", txHash));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ErrorResponse("Error updating project: " + e.getMessage()));
        }
    }

    /**
     * Invest in a project
     */
    @PostMapping("/invest")
    public ResponseEntity<?> investInCCProjectListing(@RequestBody InvestmentRequest request) {
        try {
            String txHash = web3Utils.investInCCProjectListing(
                request.getProjectAddress(),
                request.getAmount()
            );
            return ResponseEntity.ok().body(new TransactionResponse("Investment successful", txHash));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ErrorResponse("Error investing in project: " + e.getMessage()));
        }
    }

    /**
     * Place a bid in an auction
     */
    @PostMapping("/bid")
    public ResponseEntity<?> bidForCCProjectListing(@RequestBody BidRequest request) {
        try {
            String txHash = web3Utils.bidForCCProjectListing(
                request.getProjectAddress(),
                request.getAmount()
            );
            return ResponseEntity.ok().body(new TransactionResponse("Bid placed successfully", txHash));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ErrorResponse("Error placing bid: " + e.getMessage()));
        }
    }

    /**
     * Get contract addresses
     */
    @GetMapping("/contract-addresses")
    public ResponseEntity<?> getContractAddresses() {
        return ResponseEntity.ok().body(new ContractAddressesResponse(
            web3Utils.getCCTokenAddress(),
            web3Utils.getFactoryAddress()
        ));
    }

    // ==================== Response Classes ====================
    
    public static class TokenBalanceResponse {
        private String walletAddress;
        private String balance;

        public TokenBalanceResponse(String walletAddress, String balance) {
            this.walletAddress = walletAddress;
            this.balance = balance;
        }

        public String getWalletAddress() { return walletAddress; }
        public String getBalance() { return balance; }
    }

    public static class TransactionResponse {
        private String message;
        private String transactionHash;

        public TransactionResponse(String message, String transactionHash) {
            this.message = message;
            this.transactionHash = transactionHash;
        }

        public String getMessage() { return message; }
        public String getTransactionHash() { return transactionHash; }
    }

    public static class ProjectCreationResponse {
        private String message;
        private String projectAddress;

        public ProjectCreationResponse(String message, String projectAddress) {
            this.message = message;
            this.projectAddress = projectAddress;
        }

        public String getMessage() { return message; }
        public String getProjectAddress() { return projectAddress; }
    }

    public static class ErrorResponse {
        private String error;

        public ErrorResponse(String error) {
            this.error = error;
        }

        public String getError() { return error; }
    }

    public static class ContractAddressesResponse {
        private String ccTokenAddress;
        private String factoryAddress;

        public ContractAddressesResponse(String ccTokenAddress, String factoryAddress) {
            this.ccTokenAddress = ccTokenAddress;
            this.factoryAddress = factoryAddress;
        }

        public String getCcTokenAddress() { return ccTokenAddress; }
        public String getFactoryAddress() { return factoryAddress; }
    }

    // ==================== Request Classes ====================

    public static class ApprovalRequest {
        private String projectAddress;
        private String amount;

        public String getProjectAddress() { return projectAddress; }
        public void setProjectAddress(String projectAddress) { this.projectAddress = projectAddress; }
        public String getAmount() { return amount; }
        public void setAmount(String amount) { this.amount = amount; }
    }

    public static class CreateProjectRequest {
        private String walletAddress;
        private String vin;
        private String make;
        private String model;
        private String ccpg;
        private String fundingGoal;

        public String getWalletAddress() { return walletAddress; }
        public void setWalletAddress(String walletAddress) { this.walletAddress = walletAddress; }
        public String getVin() { return vin; }
        public void setVin(String vin) { this.vin = vin; }
        public String getMake() { return make; }
        public void setMake(String make) { this.make = make; }
        public String getModel() { return model; }
        public void setModel(String model) { this.model = model; }
        public String getCcpg() { return ccpg; }
        public void setCcpg(String ccpg) { this.ccpg = ccpg; }
        public String getFundingGoal() { return fundingGoal; }
        public void setFundingGoal(String fundingGoal) { this.fundingGoal = fundingGoal; }
    }

    public static class ProjectActionRequest {
        private String projectAddress;

        public String getProjectAddress() { return projectAddress; }
        public void setProjectAddress(String projectAddress) { this.projectAddress = projectAddress; }
    }

    public static class EstimationRequest {
        private String projectAddress;
        private BigInteger valueEstimate;
        private BigInteger repairEstimate;

        public String getProjectAddress() { return projectAddress; }
        public void setProjectAddress(String projectAddress) { this.projectAddress = projectAddress; }
        public BigInteger getValueEstimate() { return valueEstimate; }
        public void setValueEstimate(BigInteger valueEstimate) { this.valueEstimate = valueEstimate; }
        public BigInteger getRepairEstimate() { return repairEstimate; }
        public void setRepairEstimate(BigInteger repairEstimate) { this.repairEstimate = repairEstimate; }
    }

    public static class SetRestorerRequest {
        private String projectAddress;
        private String restorerAddress;
        private String fundingGoal;

        public String getProjectAddress() { return projectAddress; }
        public void setProjectAddress(String projectAddress) { this.projectAddress = projectAddress; }
        public String getRestorerAddress() { return restorerAddress; }
        public void setRestorerAddress(String restorerAddress) { this.restorerAddress = restorerAddress; }
        public String getFundingGoal() { return fundingGoal; }
        public void setFundingGoal(String fundingGoal) { this.fundingGoal = fundingGoal; }
    }

    public static class SetBuyerRequest {
        private String projectAddress;
        private String buyerAddress;

        public String getProjectAddress() { return projectAddress; }
        public void setProjectAddress(String projectAddress) { this.projectAddress = projectAddress; }
        public String getBuyerAddress() { return buyerAddress; }
        public void setBuyerAddress(String buyerAddress) { this.buyerAddress = buyerAddress; }
    }

    public static class UpdateProjectRequest {
        private String projectAddress;
        private String vin;
        private String make;
        private String model;
        private String ccpg;
        private String fundingGoal;

        public String getProjectAddress() { return projectAddress; }
        public void setProjectAddress(String projectAddress) { this.projectAddress = projectAddress; }
        public String getVin() { return vin; }
        public void setVin(String vin) { this.vin = vin; }
        public String getMake() { return make; }
        public void setMake(String make) { this.make = make; }
        public String getModel() { return model; }
        public void setModel(String model) { this.model = model; }
        public String getCcpg() { return ccpg; }
        public void setCcpg(String ccpg) { this.ccpg = ccpg; }
        public String getFundingGoal() { return fundingGoal; }
        public void setFundingGoal(String fundingGoal) { this.fundingGoal = fundingGoal; }
    }

    public static class InvestmentRequest {
        private String projectAddress;
        private String amount;

        public String getProjectAddress() { return projectAddress; }
        public void setProjectAddress(String projectAddress) { this.projectAddress = projectAddress; }
        public String getAmount() { return amount; }
        public void setAmount(String amount) { this.amount = amount; }
    }

    public static class BidRequest {
        private String projectAddress;
        private String amount;

        public String getProjectAddress() { return projectAddress; }
        public void setProjectAddress(String projectAddress) { this.projectAddress = projectAddress; }
        public String getAmount() { return amount; }
        public void setAmount(String amount) { this.amount = amount; }
    }
}
