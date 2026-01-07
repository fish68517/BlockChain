package com.company.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.thoughtworks.xstream.mapper.Mapper.Null;
import com.company.models.ProjectListing;
import com.company.models.File;
import com.company.models.User;
import com.company.models.ProcessResponse;
import com.company.repositories.ProjectListingRepository;
import com.company.repositories.UserRepository;
import com.company.repositories.FileRepository;
import com.company.exception.ResourceNotFoundException;
import com.company.exception.FileException;
import com.company.services.FileService;
import com.company.ServerApplication;
import com.company.utils.ProcessUtility;
import org.jbpm.services.api.ProcessService;
import org.jbpm.services.api.RuntimeDataService;
import org.kie.internal.query.QueryFilter;
import org.kie.api.task.model.TaskSummary;
import com.company.enums.ProcessTaskNamesEnum;
import com.company.services.ProcessListingService;
import com.company.services.BlockchainService;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.math.BigInteger;
import java.math.BigDecimal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class ProjectListingService {
  private static final Logger logger = LoggerFactory.getLogger(ServerApplication.class);
  @Autowired
  private ProjectListingRepository projectListingRepository;

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private FileRepository fileRepository;

  @Autowired
  private ObjectMapper mapper;

  @Autowired
  private FileService fileService;

  @Autowired
  private ProcessService processService;

  @Autowired
  private RuntimeDataService runtimeDataService;

  @Autowired
  private WebSocketService webSocketService;

  @Autowired
  private ProcessUtility processUtility;

  @Autowired
  private ProcessListingService processListingService;

  @Autowired
  private BlockchainService blockchainService;

  private final String deploymentId = "Evaluation_1.0.0-SNAPSHOT";

  private final String processId = "Submission_Process.NewListing";

  public ProjectListing getProjectListingByListingId(Long id) {
    return projectListingRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Project Listing not found with id = " + id));
  }

  public ProjectListing createProjectListing(
      String ownerAddress,
      String projectAddress,
      MultipartFile file,
      String listingString,
      Long userId) {

    ProjectListing listingRequest;
    try {
      listingRequest = mapper.readValue(listingString, ProjectListing.class);
    } catch (Exception e) {
      throw new RuntimeException("Failed to parse listing data", e);
    }

    User user = userRepository.findById(userId)
        .orElseThrow(() -> new ResourceNotFoundException("User not found with id = " + userId));

    listingRequest.setUser(user);
    listingRequest.setOwnerAddress(ownerAddress);
    // projectAddress is optional - will be set by admin when approving
    if (projectAddress != null && !projectAddress.isEmpty()) {
      listingRequest.setProjectAddress(projectAddress);
    }

    // Only process file if it's provided
    if (file != null && !file.isEmpty()) {
      String path;
      try {
        path = fileService.saveToUserIdFolder(file, userId);
      } catch (Exception e) {
        throw new FileException(e.getMessage());
      }

      File titleFile = new File(file.getOriginalFilename(), file.getContentType(), path);
      titleFile = fileRepository.save(titleFile);
      listingRequest.setFile(titleFile);
    }

    Long processInstanceId = -1L;

    Map<String, Object> params = new HashMap<String, Object>();
    params.put("vin", listingRequest.getVIN());
    params.put("make", listingRequest.getMake());
    params.put("model", listingRequest.getModel());
    params.put("vinMatched", listingRequest.getVinMatched());
    params.put("ccpg", listingRequest.getCCPG());
    params.put("fundingGoal", listingRequest.getFundingGoal());

    processInstanceId = processService.startProcess(deploymentId, processId, params);

    logger.info("Created process " + processInstanceId);

    try {
      List<Long> taskIds = runtimeDataService.getTasksByProcessInstanceId(processInstanceId);
      Long firstTaskId = taskIds.get(0);
      processUtility.completeTask(firstTaskId, "OwnerOne", params);
    } catch (Exception e) {
      logger.error("Error In Creating Listing: Completing First Task: " + e.getMessage());
    }

    try {
      List<ProcessResponse> taskResponses = processUtility.getPendingTasksByProcessId(processInstanceId);
      listingRequest.setProcessId(processInstanceId);
      List<String> pendingTasks = processListingService.convertProcessResponseToString(taskResponses);
      listingRequest.setPendingTasks(pendingTasks);
    } catch (Exception e) {
      logger.error("Error In Creating Listing: Getting Pending Tasks: " + e.getMessage());
    }

    listingRequest = projectListingRepository.save(listingRequest);

    webSocketService.sendListingCreated(listingRequest);

    return listingRequest;
  }

  public List<ProjectListing> getAllProjectListingsByUserId(Long userId) {
    return projectListingRepository.findByUserId(userId);
  }

  public List<ProjectListing> getAllProjectListings() {
    return projectListingRepository.findAll();
  }

  public ProjectListing updateProjectListing(Long id, ProjectListing listingDetails, MultipartFile file) {
    ProjectListing listing = projectListingRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Project Listing not found with id = " + id));

    if (listingDetails != null) {
      if (listingDetails.getVIN() != null) {
        listing.setVIN(listingDetails.getVIN());
      }
      if (listingDetails.getMake() != null) {
        listing.setMake(listingDetails.getMake());
      }
      if (listingDetails.getModel() != null) {
        listing.setModel(listingDetails.getModel());
      }
      if (listingDetails.getCCPG() != null) {
        listing.setCCPG(listingDetails.getCCPG());
      }
      if (listingDetails.getFundingGoal() != null) {
        listing.setFundingGoal(listingDetails.getFundingGoal());
      }
      if (listingDetails.getDescription() != null) {
        listing.setDescription(listingDetails.getDescription());
      }
      if (listingDetails.getOwnerAddress() != null) {
        listing.setOwnerAddress(listingDetails.getOwnerAddress());
      }
      if (listingDetails.getProjectAddress() != null) {
        listing.setProjectAddress(listingDetails.getProjectAddress());
      }
      if (listingDetails.getAdminMessage() != null) {
        listing.setAdminMessage(listingDetails.getAdminMessage());
      }
      if (listingDetails.getValueEstimation() != null) {
        listing.setValueEstimation(listingDetails.getValueEstimation());
      }
      if (listingDetails.getRepairCostEstimation() != null) {
        listing.setRepairCostEstimation(listingDetails.getRepairCostEstimation());
      }
    }

    // Handle file update if provided
    if (file != null && !file.isEmpty()) {
      try {
        String path = fileService.saveToUserIdFolder(file, listing.getUser().getId());
        File titleFile = new File(file.getOriginalFilename(), file.getContentType(), path);
        titleFile = fileRepository.save(titleFile);
        listing.setFile(titleFile);
      } catch (Exception e) {
        throw new FileException("Failed to update file: " + e.getMessage());
      }
    }

    Map<String, Object> params = new HashMap<String, Object>();
    params.put("vin", listing.getVIN());
    params.put("make", listing.getMake());
    params.put("model", listing.getModel());
    params.put("vinMatched", listing.getVinMatched());
    params.put("ccpg", listing.getCCPG());
    params.put("fundingGoal", listing.getFundingGoal());

    try {
      Long editListingTasks = processUtility.getTaskIdByProcessIdAndTitle(listing.getProcessId(),
          ProcessTaskNamesEnum.editDetails);
      processUtility.completeTask(editListingTasks, "OwnerOne", params);
    } catch (Exception e) {
      logger.error("Error In Editing Listing: Completing Task: " + e.getMessage());
    }

    try {
      List<ProcessResponse> taskResponses = processUtility.getPendingTasksByProcessId(listing.getProcessId());
      List<String> pendingTasks = processListingService.convertProcessResponseToString(taskResponses);
      listing.setPendingTasks(pendingTasks);
    } catch (Exception e) {
      logger.error("Error In Editing Listing: Getting Pending Tasks: " + e.getMessage());
    }

    listing = projectListingRepository.save(listing);
    webSocketService.sendAllListingUpdated(listing);

    return listing;
  }

  public ProjectListing reviewProjectListing(Long id, ProjectListing listingRequest) {
    ProjectListing listing = projectListingRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Project Listing not found with id = " + id));

    Boolean verifyDetails = listingRequest.getVerifyDetails();
    if (verifyDetails != null) {
      System.out.println("VERIFYING " + listing.getProcessId());
      listing.setVerifyDetails(verifyDetails);
      Map<String, Object> params = new HashMap<String, Object>();
      Long instanceId = listing.getProcessId();
      params.put("detailsVerified", verifyDetails);

      try {
        Long verifyDetailsTaskId = processUtility.getTaskIdByProcessIdAndTitle(instanceId,
            ProcessTaskNamesEnum.verifyDetails);
        processUtility.completeTask(verifyDetailsTaskId, "wbadmin", params);
      } catch (Exception e) {
        logger.error("Error In Reviewing Listing(Verify Details): Completing Task: " + e.getMessage());
      }

      try {
        List<ProcessResponse> taskResponses = processUtility.getPendingTasksByProcessId(listing.getProcessId());
        List<String> pendingTasks = processListingService.convertProcessResponseToString(taskResponses);
        listing.setPendingTasks(pendingTasks);
      } catch (Exception e) {
        logger.error("Error In Reviewing Listing(Verify Details): Getting Pending Tasks: " + e.getMessage());
      }
    }

    Boolean receiveTitle = listingRequest.getReceiveTitle();
    if (receiveTitle != null) {
      System.out.println("RECEIVE " + listing.getProcessId());
      listing.setReceiveTitle(receiveTitle);
      Map<String, Object> params = new HashMap<String, Object>();
      Long instanceId = listing.getProcessId();
      params.put("titleReceived", receiveTitle);
      try {
        Long receiveTitleTaskId = processUtility.getTaskIdByProcessIdAndTitle(instanceId,
            ProcessTaskNamesEnum.receiveTitle);
        processUtility.completeTask(receiveTitleTaskId, "wbadmin", params);
      } catch (Exception e) {
        logger.error("Error In Reviewing Listing(Receive Title): Completing Task: " + e.getMessage());
      }
      try {
        List<ProcessResponse> taskResponses = processUtility.getPendingTasksByProcessId(listing.getProcessId());
        listing.setPendingTasks(taskResponses.stream()
            .map(ProcessResponse::getTaskName)
            .collect(java.util.stream.Collectors.toList()));
      } catch (Exception e) {
        logger.error("Error In Reviewing Listing(Receive Title): Getting Pending Tasks: " + e.getMessage());
      }
    }

    String message = listingRequest.getAdminMessage();
    if (message != null) {
      listing.setAdminMessage(message);
    }

    if (listing.getVerifyDetails() == true 
        && listing.getReceiveTitle() == true
        && (listing.getProjectAddress() == null || listing.getProjectAddress().isEmpty())) {
      
      try {
        logger.info("Creating blockchain contract for approved project listing ID: {}", id);
        BigInteger ccpg;
        try {
          BigDecimal ccpgDecimal = new BigDecimal(listing.getCCPG());
          BigDecimal weiMultiplier = BigDecimal.valueOf(10).pow(18);
          ccpg = ccpgDecimal.multiply(weiMultiplier).toBigInteger();
        } catch (NumberFormatException e) {
          throw new RuntimeException("Invalid CCPG format for project listing ID: " + id + " - " + e.getMessage());
        }

        Float fundingGoalFloat = listing.getFundingGoal();
        if (fundingGoalFloat == null) {
          throw new RuntimeException("Funding goal is null for project listing ID: " + id);
        }
        BigDecimal fundingGoalDecimal = BigDecimal.valueOf(fundingGoalFloat.doubleValue());
        BigDecimal weiMultiplier = BigDecimal.valueOf(10).pow(18);
        BigInteger fundingGoal = fundingGoalDecimal.multiply(weiMultiplier).toBigInteger();
        
        // Create project contract on blockchain
        String projectAddress = blockchainService.createProjectContract(
            listing.getVIN(),
            listing.getMake(),
            listing.getModel(),
            ccpg,
            fundingGoal,
            listing.getOwnerAddress()
        );
        
        listing.setProjectAddress(projectAddress);
        logger.info("Successfully created blockchain contract for project listing ID: {} with address: {}", 
            id, projectAddress);
      } catch (Exception e) {
        logger.error("Error creating blockchain contract for project listing ID: {} - {}", id, e.getMessage(), e);
      }
    }

    listing = projectListingRepository.save(listing);
    webSocketService.sendListingUpdated(listing);
    return listing;
  }

  public void deleteProjectListing(Long id) {
    projectListingRepository.deleteById(id);
  }

  public void deleteAllProjectListingsByUserId(Long userId) {
    if (projectListingRepository.findByUserId(userId).isEmpty()) {
      throw new ResourceNotFoundException("No project listings found for user with id = " + userId);
    }
    projectListingRepository.deleteByUserId(userId);
  }

  public ProjectListing addValueEstimation(Long id, ProjectListing listingRequest) {
    ProjectListing listing = projectListingRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Project Listing not found with id = " + id));

    listing.setValueEstimation(listingRequest.getValueEstimation());
    listing.setRepairCostEstimation(listingRequest.getRepairCostEstimation());

    System.out.println("VALUE ESTIMATION " + listing.getValueEstimation());
    System.out.println("REPAIR COST ESTIMATION " + listing.getRepairCostEstimation());

    Long instanceId = listing.getProcessId();

    Map<String, Object> params = new HashMap<String, Object>();
    params.put("carValueEstimate", listingRequest.getValueEstimation());
    params.put("restorationPriceEstimate", listingRequest.getRepairCostEstimation());

    try {
      Long valueEstimationTaskId = processUtility.getTaskIdByProcessIdAndTitle(instanceId,
          ProcessTaskNamesEnum.valueEstimation);
      processUtility.completeTask(valueEstimationTaskId, "wbadmin", params);
    } catch (Exception e) {
      logger.error("Error In Adding Value Estimation: Completing Task: " + e.getMessage());
    }

    try {
      List<ProcessResponse> taskResponses = processUtility.getPendingTasksByProcessId(instanceId);
      List<String> pendingTasks = processListingService.convertProcessResponseToString(taskResponses);
      listing.setPendingTasks(pendingTasks);
    } catch (Exception e) {
      logger.error("Error In Adding Value Estimation: Getting Pending Tasks: " + e.getMessage());
    }
    listing = projectListingRepository.save(listing);
    webSocketService.sendListingUpdated(listing);
    webSocketService.sendListingAvailableForBid(listing);

    return projectListingRepository.save(listing);
  }

  public ProjectListing assignRestoration(Long id) {
    ProjectListing listing = projectListingRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Project Listing not found with id = " + id));

    listing.setIsRestorationAssigned(true);

    System.out.println("ASSIGNING RESTORATION " + listing.toString());

    Long instanceId = listing.getProcessId();
    Map<String, Object> params = new HashMap<String, Object>();
    params.put("restorer", listing.getRestorerId());

    try {
      Long assignRestorationTaskId = processUtility.getTaskIdByProcessIdAndTitle(instanceId,
          ProcessTaskNamesEnum.assignRestoration);

      processUtility.completeTask(assignRestorationTaskId, "wbadmin", params);
    } catch (Exception e) {
      logger.error("Error In Assigning Restoration: " + e.getMessage());
    }

    try {
      List<ProcessResponse> taskResponses = processUtility.getPendingTasksByProcessId(instanceId);
      List<String> pendingTasks = processListingService.convertProcessResponseToString(taskResponses);
      listing.setPendingTasks(pendingTasks);
    } catch (Exception e) {
      logger.error("Error In Assigning Restoration: Getting Pending Tasks: " + e.getMessage());
    }

    listing = projectListingRepository.save(listing);
    webSocketService.sendListingUpdated(listing);

    return listing;
  }

  public ProjectListing finishRestoration(Long id) {
    ProjectListing listing = projectListingRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Project Listing not found with id = " + id));

    listing.setIsRestorationFinished(true);

    System.out.println("FINISHING RESTORATION " + listing.toString());

    Map<String, Object> params = new HashMap<String, Object>();

    params.put("isRestorationFinished", listing.getIsRestorationFinished());

    Long instanceId = listing.getProcessId();

    try {
      Long restorationFinishedTaskId = processUtility.getTaskIdByProcessIdAndTitle(instanceId,
          ProcessTaskNamesEnum.restorationFinished);
      processUtility.completeTask(restorationFinishedTaskId, "RestoreOne", params);
    } catch (Exception e) {
      logger.error("Error In Finishing Restoration: Completing Task: " + e.getMessage());
    }

    try {
      List<ProcessResponse> taskResponses = processUtility.getPendingTasksByProcessId(instanceId);
      List<String> pendingTasks = processListingService.convertProcessResponseToString(taskResponses);
      listing.setPendingTasks(pendingTasks);

    } catch (Exception e) {
      logger.error("Error In Finishing Restoration: Getting Pending Tasks: " + e.getMessage());
    }
    listing = projectListingRepository.save(listing);
    webSocketService.sendListingUpdated(listing);
    return listing;
  }

  public ProjectListing postItemForSale(Long id) {
    ProjectListing listing = projectListingRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Project Listing not found with id = " + id));

    listing.setIsPostedForSale(true);

    System.out.println("POSTING ITEM FOR SALE " + listing.toString());

    Long instanceId = listing.getProcessId();
    Map<String, Object> params = new HashMap<String, Object>();
    params.put("fundingGoal", listing.getFundingGoal());
    params.put("sellingPrice", listing.getSellingPrice());

    try {
      Long postItemTaskId = processUtility.getTaskIdByProcessIdAndTitle(instanceId,
          ProcessTaskNamesEnum.postItem);
      processUtility.completeTask(postItemTaskId, "wbadmin", params);
    } catch (Exception e) {
      logger.error("Error In Posting Item For Sale: Completing Task: " + e.getMessage());
    }

    try {
      List<ProcessResponse> taskResponses = processUtility.getPendingTasksByProcessId(instanceId);
      List<String> pendingTasks = processListingService.convertProcessResponseToString(taskResponses);
      listing.setPendingTasks(pendingTasks);
    } catch (Exception e) {
      logger.error("Error In Posting Item For Sale: Getting Pending Tasks: " + e.getMessage());
    }
    listing = projectListingRepository.save(listing);
    webSocketService.sendListingUpdated(listing);
    return listing;
  }

  public ProjectListing startAuction(Long id) {
    ProjectListing listing = projectListingRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Project Listing not found with id = " + id));

    listing.setAuctionPrice(listing.getSellingPrice());

    System.out.println("STARTING AUCTION " + listing.toString());

    Map<String, Object> params = new HashMap<String, Object>();
    params.put("vin", listing.getVIN());
    params.put("make", listing.getMake());
    params.put("model", listing.getModel());
    params.put("ccpg", listing.getCCPG());
    params.put("auctionPrice", listing.getAuctionPrice());

    Long instanceId = listing.getProcessId();

    try {
      Long auctionTaskId = processUtility.getTaskIdByProcessIdAndTitle(instanceId,
          ProcessTaskNamesEnum.auction);
      processUtility.completeTask(auctionTaskId, "BuyerOne", params);
    } catch (Exception e) {
      logger.error("Error In Starting Auction: Completing Task: " + e.getMessage());
    }

    try {
      List<ProcessResponse> taskResponses = processUtility.getPendingTasksByProcessId(instanceId);
      List<String> pendingTasks = processListingService.convertProcessResponseToString(taskResponses);
      listing.setPendingTasks(pendingTasks);
    } catch (Exception e) {
      logger.error("Error In Starting Auction: Getting Pending Tasks: " + e.getMessage());
    }
    listing = projectListingRepository.save(listing);
    webSocketService.sendListingUpdated(listing);
    webSocketService.sendListingAvailableForAuctionBid(listing);
    return listing;
  }

  public ProjectListing rejectListing(Long id) {
    ProjectListing listing = projectListingRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Project Listing not found with id = " + id));

    processService.signalProcessInstance(listing.getProcessId(), "rejected", "rejected");
    logger.info("Stopped Process " + listing.getProcessId());

    listing.removeAllPendingTasks();
    listing.addPendingTask("Rejected");
    listing = projectListingRepository.save(listing);
    webSocketService.sendListingUpdated(listing);

    return listing;
  }

  public List<ProjectListing> getListingPendingBid() {
    List<ProcessResponse> restorerTasks = new ArrayList<ProcessResponse>();
    List<TaskSummary> adminTasks = runtimeDataService.getTasksAssignedAsPotentialOwner("wbadmin", new QueryFilter());

    System.out.println(" RESTORER TASKS:");
    // even when process is in bid review other biddings can still come in
    adminTasks.forEach(s -> {
      if (s.getName().equals(ProcessTaskNamesEnum.bidReview)
          || s.getName().equals(ProcessTaskNamesEnum.restorationBid)) {
        System.out.println(s.getName());
        restorerTasks.add(new ProcessResponse(s.getProcessInstanceId(), s.getName(),
            s.getStatusId()));
      }
    });

    return processListingService.convertProcessResponseToProjectListing(restorerTasks);
  }

  public List<ProjectListing> getListingPendingAuctionBid() {
    List<ProcessResponse> buyerTasks = new ArrayList<ProcessResponse>();
    List<TaskSummary> adminTasks = runtimeDataService.getTasksAssignedAsPotentialOwner("wbadmin", new QueryFilter());

    System.out.println(" BUYER TASKS:");
    adminTasks.forEach(s -> {
      if (s.getName().equals(ProcessTaskNamesEnum.auctionReview)) {
        System.out.println(s.getName());
        buyerTasks.add(new ProcessResponse(s.getProcessInstanceId(), s.getName(),
            s.getStatusId()));
      }
    });

    return processListingService.convertProcessResponseToProjectListing(buyerTasks);
  }
}
