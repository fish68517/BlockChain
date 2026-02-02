package com.company.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.GetMapping;
import com.company.services.InvestmentService;
import com.company.models.Investment;
import org.springframework.http.HttpStatus;
import java.util.List;

@RestController
@RequestMapping("/api")
public class InvestmentController {

  @Autowired
  private InvestmentService investmentService;

  @PostMapping("/users/{userId}/investments")
  public ResponseEntity<Investment> createInvestment(
      @RequestBody Investment investment,
      @PathVariable(value = "userId") Long userId) {
    Investment investments = investmentService.createInvestment(investment, userId);
    if (investments == null) {
      return new ResponseEntity<>(investment, HttpStatus.BAD_REQUEST);
    }
    return new ResponseEntity<>(investments, HttpStatus.CREATED);
  }

  /**
   * Create investment with blockchain transaction verification
   * 
   */
  @PostMapping("/users/{userId}/investments/with-transaction")
  public ResponseEntity<?> createInvestmentWithTransaction(
      @RequestBody java.util.Map<String, Object> request,
      @PathVariable(value = "userId") Long userId) {
    try {
      Investment investment = new Investment();
      if (request.get("amount") != null) {
        investment.setAmount(((Number) request.get("amount")).floatValue());
      }
      if (request.get("listing") != null) {
        @SuppressWarnings("unchecked")
        java.util.Map<String, Object> listingMap = (java.util.Map<String, Object>) request.get("listing");
        com.company.models.ProjectListing listing = new com.company.models.ProjectListing();
        if (listingMap.get("id") != null) {
          listing.setId(((Number) listingMap.get("id")).longValue());
        }
        investment.setListing(listing);
      }
      
      String transactionHash = (String) request.get("transactionHash");
      if (transactionHash == null || transactionHash.isEmpty()) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body("Error: transactionHash is required");
      }
      
      Investment createdInvestment = investmentService.createInvestmentWithTransaction(
          investment, userId, transactionHash);
      
      if (createdInvestment == null) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body("Error: Failed to create investment");
      }
      
      return new ResponseEntity<>(createdInvestment, HttpStatus.CREATED);
    } catch (RuntimeException e) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST)
          .body("Error: " + e.getMessage());
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body("Error: " + e.getMessage());
    }
  }

  @GetMapping("/users/id/{userId}/investments")
  public ResponseEntity<List<Investment>> getAllInvestmentsByUserId(@PathVariable(value = "userId") Long userId) {
    List<Investment> investments = investmentService.getAllInvestmentsByUserId(userId);
    return new ResponseEntity<>(investments, HttpStatus.OK);
  }

  @GetMapping("/investments")
  public ResponseEntity<List<Investment>> getAllInvestments() {
    List<Investment> investments = investmentService.getAllInvestments();
    return new ResponseEntity<>(investments, HttpStatus.OK);
  }
}
