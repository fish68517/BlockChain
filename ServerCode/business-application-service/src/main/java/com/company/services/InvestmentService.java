package com.company.services;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import com.company.repositories.InvestmentRepository;
import com.company.models.Investment;
import com.company.models.ProjectListing;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import com.company.repositories.ProjectListingRepository;
import com.company.exception.ResourceNotFoundException;
import java.util.Map;
import java.util.HashMap;
import com.company.utils.ProcessUtility;
import com.company.enums.ProcessTaskNamesEnum;
import java.util.List;
import com.company.models.ProcessResponse;
import com.company.services.ProcessListingService;
import com.company.repositories.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class InvestmentService {
  private static final Logger logger = LoggerFactory.getLogger(InvestmentService.class);

  @Autowired
  private InvestmentRepository investmentRepository;

  @Autowired
  private ProjectListingRepository projectListingRepository;

  @Autowired
  private ProcessUtility processUtility;

  @Autowired
  private ProcessListingService processListingService;

  @Autowired
  private UserRepository userRepository;

  public Investment createInvestment(Investment investment, Long userId) {
    System.out.println(investment.toString());
    Long listingId;
    ProjectListing listing;
    Investment investmentInRepo;
    Investment newinvestment;

    if (investment == null || investment.getListing() == null) {
      return null;
    }

    listingId = investment.getListing().getId();
    listing = projectListingRepository.findById(listingId).orElseThrow(
        () -> new ResourceNotFoundException("listing not found with id = " + investment.getListing().getId()));

    // verify if investment amount <= funding needed
    if (listing.calcCurrFundingRequiredAmount() < investment.getAmount()) {
      return null;
    }

    listing.increaseCurrFundingAmount(investment.getAmount());

    if (listing.getIsFundingEnough()) {
      System.out.println("FUNDING Client" + listing.toString());
    }

    Map<String, Object> params = new HashMap<>();
    params.put("currFundingAmount", listing.getCurrFundingAmount());
    params.put("isFundingEnough", listing.getIsFundingEnough());
    params.put("amount", listing.getAmount());

    Long instanceId = listing.getProcessId();
    try {
      Long fundingTaskId = processUtility.getTaskIdByProcessIdAndTitle(instanceId, ProcessTaskNamesEnum.funding);
      processUtility.completeTask(fundingTaskId, "ContributorOne", params);
    } catch (Exception e) {
      logger.error("Error In Selecting Bidding For Listing: Completing Funding Task: " + e.getMessage());
    }
    try {
      List<ProcessResponse> taskResponses = processUtility.getPendingTasksByProcessId(instanceId);
      List<String> taskNames = processListingService.convertProcessResponseToString(taskResponses);
      listing.setPendingTasks(taskNames);
    } catch (Exception e) {
      logger.error("Error In Selecting Bidding For Listing: Getting Pending Tasks: " + e.getMessage());
    }

    projectListingRepository.save(listing);

    investmentInRepo = investmentRepository.findFirstByUserIdAndListingId(userId, listingId);
    if (investmentInRepo != null) {
      investmentInRepo.increaseInvestmentAmountBy(investment.getAmount());
      investmentRepository.save(investmentInRepo);
      return investmentInRepo;
    }

    newinvestment = userRepository.findById(userId).map(user -> {
      investment.setUser(user);
      return investment;
    }).orElseThrow(() -> new ResourceNotFoundException("User not found with id = " + userId));

    newinvestment.setAmount(investment.getAmount());
    newinvestment.setListing(listing);
    newinvestment = investmentRepository.save(newinvestment);
    return newinvestment;
  }

  public List<Investment> getAllInvestmentsByUserId(Long userId) {
    if (!userRepository.existsById(userId)) {
      throw new ResourceNotFoundException("No investment found with user ID = " + userId);
    }

    List<Investment> investments = investmentRepository.findByUserId(userId);
    return investments;
  }

  public List<Investment> getAllInvestments() {
    return investmentRepository.findAll();
  }
}
