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
