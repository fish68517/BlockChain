package com.dlmgroup.collectorcoin.controllers;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.dlmgroup.collectorcoin.exception.ResourceNotFoundException;
import com.dlmgroup.collectorcoin.models.ProjectListing;
import com.dlmgroup.collectorcoin.repositories.ProjectListingRepository;
import com.dlmgroup.collectorcoin.repositories.InvestmentRepository;
import com.dlmgroup.collectorcoin.models.Investment;
import com.dlmgroup.collectorcoin.models.ProcessResponse;
import com.dlmgroup.collectorcoin.services.ProcessListingService;

import com.dlmgroup.collectorcoin.repositories.UserRepository;

import com.dlmgroup.collectorcoin.jbpm.CollectorCoinjBPMProcessClientAPI;



@RestController
@RequestMapping("/api")
public class InvestmentController {

  @Autowired
  private UserRepository userRepository;

	@Autowired
  private ProjectListingRepository projectListingRepository;

  @Autowired
  private InvestmentRepository investmentRepository;

  @Autowired
  private ProcessListingService processListingService;

  private CollectorCoinjBPMProcessClientAPI jBPMProcessClient = new CollectorCoinjBPMProcessClientAPI();

  @PostMapping("/users/{userId}/investments")
  public ResponseEntity<Investment> createInvestment(
    @RequestBody Investment investment,
    @PathVariable(value = "userId") Long userId
  ) {
    System.out.println(investment.toString());

    Long listingId;
    ProjectListing listing;
    Investment investmentInRepo;
    Investment newinvestment;

    if(investment == null || investment.getListing() == null){
        return new ResponseEntity<>(investment,HttpStatus.BAD_REQUEST);
    }

    listingId = investment.getListing().getId();
    listing = projectListingRepository.findById(listingId).orElseThrow(() -> new ResourceNotFoundException("listing not found with id = " + investment.getListing().getId())); 

    //verify if investment amount <= funding needed
    if (listing.calcCurrFundingRequiredAmount() < investment.getAmount()){
      return new ResponseEntity<>(investment,HttpStatus.BAD_REQUEST);
    }

    listing.increaseCurrFundingAmount(investment.getAmount());

    if(listing.getIsFundingEnough()){
      List<ProcessResponse> tasks = jBPMProcessClient.updateFunding(listing);
      List<String> taskNames = processListingService.convertProcessResponseToString(tasks);
      //System.out.println("FUNDING Client" + taskNames);

      listing.setPendingTasks(taskNames);
    }
    projectListingRepository.save(listing);

    investmentInRepo = investmentRepository.findFirstByUserIdAndListingId(userId, listingId);
    if(investmentInRepo != null){
      //update investment
      investmentInRepo.increaseInvestmentAmountBy(investment.getAmount());
      investmentRepository.save(investmentInRepo);
      //projectListingRepository.save(listing);
      return new ResponseEntity<>(investment,HttpStatus.CREATED);
    }

    //create new investment

    newinvestment = userRepository.findById(userId).map(user -> {
    investment.setUser(user);
    return investment;
    }).orElseThrow(() -> new ResourceNotFoundException("User not found with id = " + userId));

    newinvestment.setAmount(investment.getAmount());
    newinvestment.setListing(listing);
    newinvestment = investmentRepository.save(investment);
    //webSocketService.sendListingCreated(investment);

    return new ResponseEntity<>(newinvestment,HttpStatus.CREATED);
  }

    @GetMapping("/users/id/{userId}/investments")
	public ResponseEntity<List<Investment>> getAllInvestmentsByUserId(@PathVariable(value = "userId") Long userId) {
		if(!userRepository.existsById(userId)) {
			throw new ResourceNotFoundException("No investment found with user ID = " + userId);
		}

		List<Investment> investments = investmentRepository.findByUserId(userId);
		return new ResponseEntity<>(investments, HttpStatus.OK);
	}

    @GetMapping("/investments")
	public ResponseEntity<List<Investment>> getAllInvestments() {
		List<Investment> investments = investmentRepository.findAll();
		return new ResponseEntity<>(investments, HttpStatus.OK);
	}
    
}
