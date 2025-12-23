package com.company.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.company.services.BlockchainService;
import com.company.request.CreateProjectBlockchainRequest;
import com.company.request.EditProjectBlockchainRequest;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/blockchain")
public class BlockchainController {
    
    @Autowired
    private BlockchainService blockchainService;
    
    @Value("${blockchain.contract.token.address}")
    private String tokenAddress;
    
    @Value("${blockchain.contract.factory.address}")
    private String factoryAddress;
    
    @PostMapping("/admin/create-project")
    public ResponseEntity<String> createProject(
            @RequestBody CreateProjectBlockchainRequest request) {
        try {
            // Validate request fields
            if (request == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Error: Request body is required");
            }
            if (request.getCcppg() == null || request.getCcppg().isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Error: ccpg is required and cannot be null or empty");
            }
            if (request.getFundingGoal() == null || request.getFundingGoal().isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Error: fundingGoal is required and cannot be null or empty");
            }
            if (request.getVin() == null || request.getMake() == null || request.getModel() == null || request.getOwnerAddress() == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Error: vin, make, model, and ownerAddress are required");
            }
            
            // Convert String to BigInteger (handles both string numbers and regular numbers from JSON)
            BigInteger ccpg;
            BigInteger fundingGoal;
            try {
                ccpg = new BigInteger(request.getCcppg());
                fundingGoal = new BigInteger(request.getFundingGoal());
            } catch (NumberFormatException e) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Error: Invalid number format for ccpg or fundingGoal. Must be valid numeric strings: " + 
                          (e.getMessage() != null ? e.getMessage() : "Invalid number format"));
            }
            
            String projectAddress = blockchainService.createProjectContract(
                request.getVin(),
                request.getMake(),
                request.getModel(),
                ccpg,
                fundingGoal,
                request.getOwnerAddress()
            );
            return ResponseEntity.ok(projectAddress);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body("Error: " + (e.getMessage() != null ? e.getMessage() : "Invalid argument"));
        } catch (Exception e) {
            String errorMsg = e.getMessage();
            if (errorMsg == null || errorMsg.isEmpty()) {
                errorMsg = e.getClass().getSimpleName() + ": " + (e.getCause() != null ? e.getCause().getMessage() : "Unknown error");
            }
            e.printStackTrace(); // Log to console for debugging
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error: " + errorMsg);
        }
    }
    
    @PostMapping("/admin/approve-project")
    public ResponseEntity<?> approveProject(@RequestParam String projectAddress) {
        try {
            blockchainService.approveProject(projectAddress);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error: " + e.getMessage());
        }
    }
    
    @PostMapping("/admin/save-estimations")
    public ResponseEntity<?> saveEstimations(
            @RequestParam String projectAddress,
            @RequestParam BigInteger valueEst,
            @RequestParam BigInteger repairEst) {
        try {
            blockchainService.saveEstimations(projectAddress, valueEst, repairEst);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error: " + e.getMessage());
        }
    }
    
    @PostMapping("/admin/set-restorer")
    public ResponseEntity<?> setRestorer(
            @RequestParam String projectAddress,
            @RequestParam String restorerAddress,
            @RequestParam BigInteger fundingGoal) {
        try {
            blockchainService.setRestorer(projectAddress, restorerAddress, fundingGoal);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error: " + e.getMessage());
        }
    }
    
    @PostMapping("/admin/assign-restoration")
    public ResponseEntity<?> assignRestoration(@RequestParam String projectAddress) {
        try {
            blockchainService.assignRestoration(projectAddress);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error: " + e.getMessage());
        }
    }
    
    @PostMapping("/admin/open-auction")
    public ResponseEntity<?> openAuction(@RequestParam String projectAddress) {
        try {
            blockchainService.openAuction(projectAddress);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error: " + e.getMessage());
        }
    }
    
    @PostMapping("/admin/set-buyer")
    public ResponseEntity<?> setBuyer(
            @RequestParam String projectAddress,
            @RequestParam String buyerAddress) {
        try {
            blockchainService.setBuyer(projectAddress, buyerAddress);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error: " + e.getMessage());
        }
    }
    
    @PostMapping("/admin/redistribute")
    public ResponseEntity<?> redistribute(@RequestParam String projectAddress) {
        try {
            blockchainService.redistribute(projectAddress);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error: " + e.getMessage());
        }
    }
    
    @PostMapping("/admin/edit-project")
    public ResponseEntity<?> editProject(@RequestBody EditProjectBlockchainRequest request) {
        try {
            blockchainService.editProjectDetails(
                request.getProjectAddress(),
                request.getVin(),
                request.getMake(),
                request.getModel(),
                request.getCcppg(),
                request.getFundingGoal()
            );
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error: " + e.getMessage());
        }
    }
    
    @GetMapping("/balance/{address}")
    public ResponseEntity<BigInteger> getTokenBalance(@PathVariable String address) {
        try {
            BigInteger balance = blockchainService.getTokenBalance(address);
            return ResponseEntity.ok(balance);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Get contract addresses from application.properties
     * This endpoint allows the frontend to dynamically load contract addresses
     */
    @GetMapping("/config/contracts")
    public ResponseEntity<Map<String, String>> getContractAddresses() {
        Map<String, String> config = new HashMap<>();
        config.put("tokenAddress", tokenAddress);
        config.put("factoryAddress", factoryAddress);
        return ResponseEntity.ok(config);
    }
}

