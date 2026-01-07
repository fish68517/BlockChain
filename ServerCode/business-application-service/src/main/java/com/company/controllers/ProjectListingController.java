package com.company.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;

import com.company.models.ProjectListing;
import com.company.services.ProjectListingService;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;

@RestController
@RequestMapping("/api")
public class ProjectListingController {
  @Autowired
  private ProjectListingService projectListingService;

  @Autowired
  private ObjectMapper mapper;

  @PostMapping("/users/{userId}/projectListings")
  public ResponseEntity<ProjectListing> createProjectListing(
      @RequestPart("walletAddress") String ownerAddress,
      @RequestPart(value = "projectAddress", required = false) String projectAddress,
      @RequestPart(value = "titleFile", required = false) MultipartFile file,
      @RequestPart("listing") String listingString,
      @PathVariable(value = "userId") Long userId) {

    ProjectListing listing = projectListingService.createProjectListing(
        ownerAddress, projectAddress, file, listingString, userId);
    return new ResponseEntity<>(listing, HttpStatus.CREATED);
  }

  @GetMapping("/projectListings")
  public ResponseEntity<List<ProjectListing>> getAllProjectListings() {
    List<ProjectListing> projectListings = projectListingService.getAllProjectListings();
    return new ResponseEntity<>(projectListings, HttpStatus.OK);
  }

  @GetMapping("/projectListings/{id}")
  public ResponseEntity<ProjectListing> getProjectListingByListingId(@PathVariable(value = "id") Long id) {
    ProjectListing projectListing = projectListingService.getProjectListingByListingId(id);
    return new ResponseEntity<>(projectListing, HttpStatus.OK);
  }

  @GetMapping("/users/{userId}/projectListings")
  public ResponseEntity<List<ProjectListing>> getAllProjectListingsByUserId(
      @PathVariable(value = "userId") Long userId) {
    List<ProjectListing> projectListings = projectListingService.getAllProjectListingsByUserId(userId);
    return new ResponseEntity<>(projectListings, HttpStatus.OK);
  }

  @PutMapping("/projectListings/{id}")
  public ResponseEntity<ProjectListing> updateProjectListing(
      @PathVariable(value = "id") Long id,
      @RequestPart(value = "listing", required = false) String listingString,
      @RequestPart(value = "titleFile", required = false) MultipartFile file) {

    ProjectListing listingDetails = null;
    if (listingString != null && !listingString.isEmpty()) {
      try {
        listingDetails = mapper.readValue(listingString, ProjectListing.class);
      } catch (Exception e) {
        throw new RuntimeException("Failed to parse listing data", e);
      }
    }

    ProjectListing updatedListing = projectListingService.updateProjectListing(id, listingDetails, file);
    return new ResponseEntity<>(updatedListing, HttpStatus.OK);
  }

  @PutMapping("/projectListings/{id}/review")
  public ResponseEntity<ProjectListing> reviewProjectListing(
      @PathVariable("id") Long id,
      @RequestBody ProjectListing listingRequest) {
    ProjectListing updatedListing = projectListingService.reviewProjectListing(id, listingRequest);
    return new ResponseEntity<>(updatedListing, HttpStatus.OK);
  }

  @DeleteMapping("/projectListings/{id}")
  public ResponseEntity<Void> deleteProjectListing(@PathVariable("id") Long id) {
    projectListingService.deleteProjectListing(id);
    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }

  @DeleteMapping("/users/{userId}/projectListings")
  public ResponseEntity<Void> deleteAllProjectListingsByUserId(@PathVariable("userId") Long userId) {
    projectListingService.deleteAllProjectListingsByUserId(userId);
    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }

  @PostMapping("/projectListings/{id}/valueEstimation")
  public ResponseEntity<ProjectListing> addValueEstimation(@PathVariable("id") Long id,
      @RequestBody ProjectListing listingRequest) {
    ProjectListing updatedListing = projectListingService.addValueEstimation(id, listingRequest);
    return new ResponseEntity<>(updatedListing, HttpStatus.OK);
  }

  @PostMapping("/projectListings/{id}/assignRestoration")
  public ResponseEntity<ProjectListing> assignRestoration(@PathVariable("id") Long id) {
    ProjectListing updatedListing = projectListingService.assignRestoration(id);
    return new ResponseEntity<>(updatedListing, HttpStatus.OK);
  }

  @PostMapping("/projectListings/{id}/finishRestoration")
  public ResponseEntity<ProjectListing> finishRestoration(@PathVariable("id") Long id) {
    ProjectListing updatedListing = projectListingService.finishRestoration(id);
    return new ResponseEntity<>(updatedListing, HttpStatus.OK);
  }

  @PostMapping("/projectListings/{id}/postItemForSale")
  public ResponseEntity<ProjectListing> postItemForSale(@PathVariable("id") Long id) {
    ProjectListing updatedListing = projectListingService.postItemForSale(id);
    return new ResponseEntity<>(updatedListing, HttpStatus.OK);
  }

  @PostMapping("/projectListings/{id}/startAuction")
  public ResponseEntity<ProjectListing> startAuction(@PathVariable("id") Long id) {
    ProjectListing updatedListing = projectListingService.startAuction(id);
    return new ResponseEntity<>(updatedListing, HttpStatus.OK);
  }

  @PostMapping("/projectListings/{id}/rejectListing")
  public ResponseEntity<ProjectListing> rejectListing(@PathVariable("id") Long id) {
    ProjectListing updatedListing = projectListingService.rejectListing(id);
    return new ResponseEntity<>(updatedListing, HttpStatus.OK);
  }

  @GetMapping("/projectListings/restoration")
  public ResponseEntity<List<ProjectListing>> getRestorationProjectListings() {
    List<ProjectListing> projectListings = projectListingService.getListingPendingBid();
    return new ResponseEntity<>(projectListings, HttpStatus.OK);
  }

  @GetMapping("/projectListings/buyer")
  public ResponseEntity<List<ProjectListing>> getBuyerProjectListings() {
    List<ProjectListing> projectListings = projectListingService.getListingPendingAuctionBid();
    return new ResponseEntity<>(projectListings, HttpStatus.OK);
  }
}
