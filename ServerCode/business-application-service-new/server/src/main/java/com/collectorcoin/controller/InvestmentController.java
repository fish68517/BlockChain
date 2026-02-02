package com.collectorcoin.controller;

import com.collectorcoin.dto.*;
import com.collectorcoin.model.Investment;
import com.collectorcoin.service.InvestmentService;
import com.collectorcoin.service.InvestmentProxyService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * REST Controller for investment operations.
 */
@RestController
@RequestMapping("/api")
public class InvestmentController {

    private static final Logger logger = LoggerFactory.getLogger(InvestmentController.class);

    private final InvestmentProxyService investmentProxyService;
    private final InvestmentService investmentService;

    public InvestmentController(InvestmentProxyService investmentProxyService, InvestmentService investmentService) {
        this.investmentProxyService = investmentProxyService;
        this.investmentService = investmentService;
    }

    @GetMapping("/investments")
    public ResponseEntity<List<Investment>> getAllInvestments() {
        return ResponseEntity.ok(investmentService.getAllInvestments());
    }

    @GetMapping("/investments/user/{userId}")
    public ResponseEntity<List<Investment>> getInvestmentsByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(investmentService.getInvestmentsByUserId(userId));
    }

    @GetMapping("/investments/listing/{listingId}")
    public ResponseEntity<List<Investment>> getInvestmentsByListing(@PathVariable Long listingId) {
        return ResponseEntity.ok(investmentService.getInvestmentsByListingId(listingId));
    }

    @PostMapping("/investments")
    public ResponseEntity<?> createInvestment(@RequestBody Map<String, Object> request) {
        try {
            Long userId = ((Number) request.get("userId")).longValue();
            Long listingId = ((Number) request.get("listingId")).longValue();
            BigDecimal amount = new BigDecimal(request.get("amount").toString());
            String transactionHash = (String) request.get("transactionHash");

            Investment investment = investmentService.createInvestment(userId, listingId, amount, transactionHash);
            return ResponseEntity.ok(investment);
        } catch (Exception e) {
            logger.error("Error creating investment", e);
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/investments/user/{userId}/total")
    public ResponseEntity<Map<String, Object>> getUserTotalInvestment(@PathVariable Long userId) {
        BigDecimal total = investmentService.getTotalInvestmentByUser(userId);
        return ResponseEntity.ok(Map.of("userId", userId, "totalInvestment", total));
    }

    @PostMapping("/listings/{listingId}/invest")
    public ResponseEntity<Map<String, String>> invest(
            @PathVariable Long listingId,
            @Valid @RequestBody InvestRequest request) {
        logger.info("Processing investment for listing {}", listingId);

        investmentProxyService.processInvestment(
            listingId,
            request.getInvestorAddress(),
            request.getAmount()
        );

        return ResponseEntity.ok(Map.of(
            "status", "success",
            "message", "Investment processed"
        ));
    }

    @PostMapping("/listings/{listingId}/assign-restorer")
    public ResponseEntity<Map<String, String>> assignRestorer(
            @PathVariable Long listingId,
            @Valid @RequestBody AssignRestorerRequest request) {
        logger.info("Assigning restorer for listing {}", listingId);

        investmentProxyService.assignRestorer(
            listingId,
            request.getRestorerAddress()
        );

        return ResponseEntity.ok(Map.of(
            "status", "success",
            "message", "Restorer assigned"
        ));
    }

    @PostMapping("/listings/{listingId}/complete-restoration")
    public ResponseEntity<Map<String, String>> completeRestoration(
            @PathVariable Long listingId) {
        logger.info("Completing restoration for listing {}", listingId);

        investmentProxyService.completeRestoration(listingId);

        return ResponseEntity.ok(Map.of(
            "status", "success",
            "message", "Restoration completed"
        ));
    }
}
