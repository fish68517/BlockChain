package com.dlmgroup.collectorcoin.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.bind.annotation.RequestPart;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.dlmgroup.collectorcoin.exception.FileException;
import com.dlmgroup.collectorcoin.exception.ResourceNotFoundException;
import com.dlmgroup.collectorcoin.models.ProjectListing;
import com.dlmgroup.collectorcoin.repositories.ProjectListingRepository;
import com.dlmgroup.collectorcoin.models.User;
import com.dlmgroup.collectorcoin.models.File;
import com.dlmgroup.collectorcoin.models.ProcessResponse;
import com.dlmgroup.collectorcoin.repositories.UserRepository;
import com.dlmgroup.collectorcoin.services.FileService;
import com.dlmgroup.collectorcoin.services.ProcessListingService;
import com.dlmgroup.collectorcoin.services.WebSocketService;
import com.dlmgroup.collectorcoin.repositories.FileRepository;

import com.dlmgroup.collectorcoin.jbpm.CollectorCoinjBPMProcessClientAPI;

@RestController
@RequestMapping("/api")
public class ProjectListingController {
  @Autowired
  private UserRepository userRepository;

  @Autowired
  private FileRepository fileRepository;

  @Autowired
  private ProjectListingRepository projectListingRepository;

  @Autowired
  private FileService fileService;

  @Autowired
  private WebSocketService webSocketService;

  @Autowired
  private ProcessListingService processListingService;

  private CollectorCoinjBPMProcessClientAPI jBPMProcessClient = new CollectorCoinjBPMProcessClientAPI(); // ADDED

  private ObjectMapper mapper = new ObjectMapper();

  // Get the details of each prject listing, this is similar to /projectListings
  // but since
  // it doesn't return all attributes, this API is created.
  @GetMapping("/all-projectListings-detail")
  public ResponseEntity<List<ProjectListing>> getAllProjectListingsByUserId() {
    List<ProjectListing> projectListings = projectListingRepository.findAll();
    return new ResponseEntity<>(projectListings, HttpStatus.OK);
  }

  // Get all project listings by User ID
  @GetMapping("/users/id/{userId}/projectListings")
  public ResponseEntity<List<ProjectListing>> getAllProjectListingsByUserId(
      @PathVariable(value = "userId") Long userId) {
    if (!userRepository.existsById(userId)) {
      throw new ResourceNotFoundException("No project listings found with user ID = " + userId);
    }

    List<ProjectListing> projectListings = projectListingRepository.findByUserId(userId);
    return new ResponseEntity<>(projectListings, HttpStatus.OK);
  }

  // Get all project listings by username
  @GetMapping("/users/{username}/projectListings")
  public ResponseEntity<List<ProjectListing>> getAllProjectListingsByUsername(
      @PathVariable(value = "username") String username) {
    User user = userRepository.findByUsername(username)
        .orElseThrow(() -> new ResourceNotFoundException("User not found with username = " + username));

    List<ProjectListing> projectListings = projectListingRepository.findByUserId(user.getId());
    return new ResponseEntity<>(projectListings, HttpStatus.OK);
  }

  // Get project listing by listing ID
  @GetMapping("/projectListings/{id}")
  public ResponseEntity<ProjectListing> getProjectListingsById(@PathVariable(value = "id") Long id) {
    ProjectListing listing = projectListingRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Project Listing not found with id = " + id));

    return new ResponseEntity<>(listing, HttpStatus.OK);
  }

  @PostMapping("/users/{userId}/projectListings")
  public ResponseEntity<ProjectListing> createProjectListing(
      @RequestPart("walletAddress") String ownerAddress,
      @RequestPart("projectAddress") String projectAddress,
      @RequestPart("titleFile") MultipartFile file,
      @RequestPart("listing") String listingString,
      @PathVariable(value = "userId") Long userId) {
    ProjectListing listingRequest;
    try {
      listingRequest = this.mapper.readValue(listingString, ProjectListing.class);
    } catch (Exception e) {
      e.printStackTrace();
      return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    ProjectListing listing = userRepository.findById(userId).map(user -> {
      listingRequest.setUser(user);
      return listingRequest;
    }).orElseThrow(() -> new ResourceNotFoundException("User not found with id = " + userId));

    listing.setOwnerAddress(ownerAddress);
    listing.setProjectAddress(projectAddress);

    String path;
    try {
      path = fileService.saveToUserIdFolder(file, userId);
    } catch (Exception e) {
      throw new FileException(e.getMessage());
    }

    File titleFile = new File(file.getOriginalFilename(), file.getContentType(), path);

    titleFile = fileRepository.save(titleFile);
    listing.setFile(titleFile);

    try {
      List<ProcessResponse> response = jBPMProcessClient.startNewProjectListingProcess(listing);
      listing.setProcessId(response.get(0).getProcessInstanceId());
      for (ProcessResponse r : response) {
        listing.addPendingTask(r.getTaskName());
      }
    } catch (Exception e) {
      e.printStackTrace();
    }

    listing = projectListingRepository.save(listing);

    webSocketService.sendListingCreated(listing);

    return new ResponseEntity<>(listing, HttpStatus.CREATED);
  }

  // Update a specified project listing
  @PutMapping("/projectListings/{id}")
  public ResponseEntity<ProjectListing> updateProjectListing(
      @PathVariable(value = "id") Long id,
      @RequestPart(value = "titleFile", required = false) MultipartFile file,
      @RequestPart(value = "listing", required = false) String listingString) {
    ProjectListing listingRequest;

    try {
      listingRequest = this.mapper.readValue(listingString, ProjectListing.class);
    } catch (Exception e) {
      e.printStackTrace();
      return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    ProjectListing listing = projectListingRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("ProjectListingId " + id + " not found"));

    System.out.println(listingRequest);
    System.out.println(listing);
    System.out.println(file);

    listing.setVIN(listingRequest.getVIN());
    listing.setMake(listingRequest.getMake());
    listing.setModel(listingRequest.getModel());
    listing.setCCPG(listingRequest.getCCPG());
    listing.setFundingGoal(listingRequest.getFundingGoal());

    if (file != null) {
      String path;
      File oldFile = fileRepository.findById(listing.getFile().getId())
          .orElseThrow(() -> new ResourceNotFoundException("Could not find file for listing " + id));

      try {
        path = fileService.saveToUserIdFolder(file, listing.getUser().getId());
        fileService.deleteFile(oldFile.getPath());
      } catch (Exception e) {
        throw new FileException(e.getMessage());
      }

      File titleFile = new File(file.getOriginalFilename(), file.getContentType(), path);
      titleFile = fileRepository.save(titleFile);
      listing.setFile(titleFile);
    }

    try {
      List<ProcessResponse> response = jBPMProcessClient.editProjectListing(listing);
      List<String> taskNames = processListingService.convertProcessResponseToString(response);
      listing.setPendingTasks(taskNames);
    } catch (Exception e) {
      e.printStackTrace();
    }

    listing = projectListingRepository.save(listing);
    webSocketService.sendAllListingUpdated(listing);

    return new ResponseEntity<>(listing, HttpStatus.OK);
  }

  // Mark the project listing to be reviewed by setting its verifyDetails &
  // receiveTitle
  @PutMapping("/projectListings/{id}/review")
  public ResponseEntity<ProjectListing> reviewProjectListing(
      @PathVariable("id") Long id,
      @RequestBody ProjectListing listingRequest) {
    ProjectListing listing = projectListingRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("ProjectListingId " + id + " not found"));

    Boolean verifyDetails = listingRequest.getVerifyDetails();
    Boolean recieveTitle = listingRequest.getReceiveTitle();
    String message = listingRequest.getAdminMessage();

    listing.setVerifyDetails(verifyDetails);
    listing.setReceiveTitle(recieveTitle);
    listing.setAdminMessage(message);

    List<ProcessResponse> newTasks = jBPMProcessClient.reviewListing(listing);
    List<String> taskNames = processListingService.convertProcessResponseToString(newTasks);
    listing.setPendingTasks(taskNames);
    listing = projectListingRepository.save(listing);
    webSocketService.sendListingUpdated(listing);

    return new ResponseEntity<>(listing, HttpStatus.OK);
  }

  // Delete a specified project listing
  @DeleteMapping("/projectListings/{id}")
  public ResponseEntity<HttpStatus> deleteProjectListing(@PathVariable("id") long id) {
    projectListingRepository.deleteById(id);

    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }

  // Delete project listings by user ID
  @DeleteMapping("/users/{userId}/projectListings")
  public ResponseEntity<List<ProjectListing>> deleteAllProjectListingsOfUser(
      @PathVariable(value = "userId") Long userId) {
    if (!userRepository.existsById(userId)) {
      throw new ResourceNotFoundException("User not found with id = " + userId);
    }

    projectListingRepository.deleteByUserId(userId);
    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }

  @PostMapping("/projectListings/{id}/valueEstimation")
  public ResponseEntity<ProjectListing> addValueEstimation(
      @PathVariable(value = "id") Long id,
      @RequestBody ProjectListing request) {
    ProjectListing listing = projectListingRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("ProjectListingId " + id + " not found"));

    listing.setValueEstimation(request.getValueEstimation());
    listing.setRepairCostEstimation(request.getRepairCostEstimation());

    List<ProcessResponse> tasks = jBPMProcessClient.estimateValue(listing);
    List<String> taskNames = processListingService.convertProcessResponseToString(tasks);

    listing.setPendingTasks(taskNames);
    listing = projectListingRepository.save(listing);

    webSocketService.sendListingUpdated(listing);
    webSocketService.sendListingAvailableForBid(listing);

    return new ResponseEntity<>(listing, HttpStatus.OK);
  }

  @PostMapping("/projectListings/{id}/assignRestoration")
  public ResponseEntity<ProjectListing> assignRestoration(
      @PathVariable(value = "id") Long id) {
    ProjectListing listing = projectListingRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("ProjectListingId " + id + " not found"));

    listing.setIsRestorationAssigned(true);

    List<ProcessResponse> tasks = jBPMProcessClient.assignRestoration(listing);
    List<String> taskNames = processListingService.convertProcessResponseToString(tasks);

    listing.setPendingTasks(taskNames);
    listing = projectListingRepository.save(listing);

    webSocketService.sendListingUpdated(listing);

    return new ResponseEntity<>(listing, HttpStatus.OK);
  }

  @PostMapping("/projectListings/{id}/finishRestoration")
  public ResponseEntity<ProjectListing> finishRestoration(
      @PathVariable(value = "id") Long id) {
    ProjectListing listing = projectListingRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("ProjectListingId " + id + " not found"));

    listing.setIsRestorationFinished(true);

    List<ProcessResponse> tasks = jBPMProcessClient.finishRestoration(listing);
    List<String> taskNames = processListingService.convertProcessResponseToString(tasks);

    listing.setPendingTasks(taskNames);
    listing = projectListingRepository.save(listing);

    webSocketService.sendListingUpdated(listing);

    return new ResponseEntity<>(listing, HttpStatus.OK);
  }

  @PostMapping("/projectListings/{id}/postItemForSale")
  public ResponseEntity<ProjectListing> postItemForSale(
      @PathVariable(value = "id") Long id) {
    ProjectListing listing = projectListingRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("ProjectListingId " + id + " not found"));

    listing.setIsPostedForSale(true);
    listing.setSellingPrice(listing.getValueEstimation());

    List<ProcessResponse> tasks = jBPMProcessClient.postItemForSale(listing);
    List<String> taskNames = processListingService.convertProcessResponseToString(tasks);

    listing.setPendingTasks(taskNames);
    listing = projectListingRepository.save(listing);

    webSocketService.sendListingUpdated(listing);

    return new ResponseEntity<>(listing, HttpStatus.OK);
  }

  @PostMapping("/projectListings/{id}/startAuction")
  public ResponseEntity<ProjectListing> startAuction(
      @PathVariable(value = "id") Long id) {
    ProjectListing listing = projectListingRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("ProjectListingId " + id + " not found"));

    listing.setAuctionPrice(listing.getSellingPrice());

    List<ProcessResponse> tasks = jBPMProcessClient.startAuction(listing);
    List<String> taskNames = processListingService.convertProcessResponseToString(tasks);

    listing.setPendingTasks(taskNames);
    listing = projectListingRepository.save(listing);

    webSocketService.sendListingUpdated(listing);
    webSocketService.sendListingAvailableForAuctionBid(listing);

    return new ResponseEntity<>(listing, HttpStatus.OK);
  }

  @PostMapping("/projectListings/{id}/reject")
  public ResponseEntity<ProjectListing> addValueEstimation(@PathVariable(value = "id") Long id) {
    ProjectListing listing = projectListingRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("ProjectListingId " + id + " not found"));

    jBPMProcessClient.rejectListing(listing);
    listing.removeAllPendingTasks();
    listing.addPendingTask("Rejected");

    listing = projectListingRepository.save(listing);
    webSocketService.sendListingUpdated(listing);

    return new ResponseEntity<>(listing, HttpStatus.OK);
  }

  @GetMapping("/projectListings/restoration")
  public ResponseEntity<List<ProjectListing>> getListingsPendingBid() {
    List<ProcessResponse> tasks = jBPMProcessClient.getListingsPendingBid();

    return new ResponseEntity<>(processListingService.convertProcessResponseToProjectListing(tasks), HttpStatus.OK);
  }

  @GetMapping("/projectListings/buyer")
  public ResponseEntity<List<ProjectListing>> getListingsPendingAuctionBid() {
    List<ProcessResponse> tasks = jBPMProcessClient.getListingsPendingAuctionBid();

    return new ResponseEntity<>(processListingService.convertProcessResponseToProjectListing(tasks), HttpStatus.OK);
  }
}
