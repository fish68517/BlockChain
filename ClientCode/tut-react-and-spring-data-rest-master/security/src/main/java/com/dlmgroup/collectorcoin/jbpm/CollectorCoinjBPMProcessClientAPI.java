package com.dlmgroup.collectorcoin.jbpm;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import java.util.Base64;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dlmgroup.collectorcoin.models.ProcessResponse;
import com.dlmgroup.collectorcoin.models.ProjectListing;

public class CollectorCoinjBPMProcessClientAPI {
  private static final Logger log = LoggerFactory.getLogger(CollectorCoinjBPMProcessClientAPI.class);
  private String serverjBPMBaseUrl;
  private Long jBPMProcessID;

  public CollectorCoinjBPMProcessClientAPI() {
    this.serverjBPMBaseUrl = "http://localhost:8090";
  }

  public Long getjBPMProcessID() {
    return this.jBPMProcessID;
  }

  public void setjBPMProcessID(long jBPMProcessID) {
    this.jBPMProcessID = jBPMProcessID;
  }

  private HttpHeaders getHeaders() {
    HttpHeaders headers = new HttpHeaders();
    headers.add("content-type", "application/json");
    headers.add("accept", "application/json");
    HttpHeaders headersWbadmin = new HttpHeaders();
    headersWbadmin.addAll(headers);
    headersWbadmin.add("Authorization",
        "Basic " + new String(Base64.getEncoder().encode("wbadmin:wbadmin".getBytes())));

    return headersWbadmin;
  }

  private ResponseEntity<List<ProcessResponse>> sendJBPMPostRequest(
      ProjectListing projVerification,
      String path) {
    RestTemplate restTemplate = new RestTemplate();
    HttpHeaders headersWbadmin = getHeaders();

    return restTemplate.exchange(
        this.serverjBPMBaseUrl + path,
        HttpMethod.POST,
        new HttpEntity<ProjectListing>(projVerification, headersWbadmin),
        new ParameterizedTypeReference<List<ProcessResponse>>() {
        });
  }

  private ResponseEntity<List<ProcessResponse>> sendJBPMGetRequest(String path) {
    RestTemplate restTemplate = new RestTemplate();
    HttpHeaders headersWbadmin = getHeaders();

    return restTemplate.exchange(
        this.serverjBPMBaseUrl + path,
        HttpMethod.GET,
        new HttpEntity<ProjectListing>(null, headersWbadmin),
        new ParameterizedTypeReference<List<ProcessResponse>>() {
        });
  }

  public List<ProcessResponse> startNewProjectListingProcess(ProjectListing newListing) {
    ProjectListing projVerification = new ProjectListing();

    projVerification.setVIN(newListing.getVIN());
    projVerification.setMake(newListing.getMake());
    projVerification.setModel(newListing.getModel());
    projVerification.setCCPG(newListing.getCCPG());
    projVerification.setVinMatched(newListing.getVinMatched());
    projVerification.setFundingGoal(newListing.getFundingGoal());

    ResponseEntity<List<ProcessResponse>> newListingTasks = this.sendJBPMPostRequest(projVerification, "/newListing");
    log.info("Started Process Instance: " + newListingTasks.getBody().get(0).getProcessInstanceId());

    return newListingTasks.getBody();
  }

  public List<ProcessResponse> editProjectListing(ProjectListing newListing) {
    ResponseEntity<List<ProcessResponse>> newListingTasks = this.sendJBPMPostRequest(newListing, "/editListing");

    return newListingTasks.getBody();
  }

  public List<ProcessResponse> reviewListing(ProjectListing listing) {
    ProjectListing projVerification = new ProjectListing();
    projVerification.setProcessId(listing.getProcessId());
    projVerification.setVerifyDetails(listing.getVerifyDetails());
    projVerification.setReceiveTitle(listing.getReceiveTitle());

    Boolean shouldVerifyDetails = listing.getPendingTasks().contains("Verify Details");
    Boolean shouldReceiveTitle = listing.getPendingTasks().contains("Receive Title");

    if (shouldVerifyDetails) {
      this.sendJBPMPostRequest(projVerification, "/verifyDetails").getBody();
    }

    if (shouldReceiveTitle) {
      this.sendJBPMPostRequest(projVerification, "/receiveTitle").getBody();
    }

    ResponseEntity<List<ProcessResponse>> processTasks = this
        .sendJBPMGetRequest("/processes/" + projVerification.getProcessId() + "/tasks?status=Ready");

    return processTasks.getBody();
  }

  public List<ProcessResponse> estimateValue(ProjectListing listing) {
    ProjectListing projVerification = new ProjectListing();
    projVerification.setProcessId(listing.getProcessId());
    projVerification.setRepairCostEstimation(listing.getRepairCostEstimation());
    projVerification.setValueEstimation(listing.getValueEstimation());
    ResponseEntity<List<ProcessResponse>> tasks = this.sendJBPMPostRequest(projVerification, "/valueEstimation");

    return tasks.getBody();
  }

  public void rejectListing(ProjectListing listing) {
    this.sendJBPMPostRequest(listing, "/reject");
  }

  public List<ProcessResponse> getListingsPendingBid() {
    ResponseEntity<List<ProcessResponse>> tasks = this.sendJBPMGetRequest("/processes/restorer");

    return tasks.getBody();
  }

  public List<ProcessResponse> getListingsPendingAuctionBid() {
    ResponseEntity<List<ProcessResponse>> tasks = this.sendJBPMGetRequest("/processes/buyer");

    return tasks.getBody();
  }

  public List<ProcessResponse> selectBid(ProjectListing listing) {
    ProjectListing projVerification = new ProjectListing();
    projVerification.setProcessId(listing.getProcessId());
    projVerification.setBidPrice(listing.getBidPrice());
    projVerification.setIsBidSuccess(listing.getIsBidSuccess());
    return this.sendJBPMPostRequest(projVerification, "/bidreview").getBody();
  }

  public List<ProcessResponse> updateFunding(ProjectListing listing) {
    ProjectListing projVerification = new ProjectListing();
    projVerification.setProcessId(listing.getProcessId());
    projVerification.setCurrFundingAmount(listing.getCurrFundingAmount());
    projVerification.setIsFundingEnough(listing.getIsFundingEnough());
    projVerification.setAmount(listing.getAmount());
    return this.sendJBPMPostRequest(projVerification, "/funding").getBody();
  }

  public List<ProcessResponse> assignRestoration(ProjectListing listing) {
    ProjectListing projVerification = new ProjectListing();
    projVerification.setProcessId(listing.getProcessId());
    projVerification.setRestorerId(listing.getRestorerId());
    return this.sendJBPMPostRequest(projVerification, "/assignrestoration").getBody();
  }

  public List<ProcessResponse> postItemForSale(ProjectListing listing) {
    ProjectListing projVerification = new ProjectListing();
    projVerification.setProcessId(listing.getProcessId());
    projVerification.setSellingPrice(listing.getSellingPrice());
    projVerification.setFundingGoal(listing.getFundingGoal());
    return this.sendJBPMPostRequest(projVerification, "/postitem").getBody();
  }

  public List<ProcessResponse> startAuction(ProjectListing listing) {
    ProjectListing projVerification = new ProjectListing();
    projVerification.setProcessId(listing.getProcessId());
    projVerification.setAuctionPrice(listing.getAuctionPrice());
    projVerification.setVIN(listing.getVIN());
    projVerification.setMake(listing.getMake());
    projVerification.setModel(listing.getModel());
    projVerification.setCCPG(listing.getCCPG());
    return this.sendJBPMPostRequest(projVerification, "/auction").getBody();
  }

  public List<ProcessResponse> finishRestoration(ProjectListing listing) {
    ProjectListing projVerification = new ProjectListing();
    projVerification.setProcessId(listing.getProcessId());
    projVerification.setIsRestorationFinished(listing.getIsRestorationFinished());
    return this.sendJBPMPostRequest(projVerification, "/restorationfinished").getBody();
  }

  public List<ProcessResponse> selectAuctionBid(ProjectListing listing) {
    ProjectListing projVerification = new ProjectListing();
    projVerification.setProcessId(listing.getProcessId());
    projVerification.setAuctionPrice(listing.getAuctionPrice());
    projVerification.setIsAuctionAccept(listing.getIsAuctionAccept());
    return this.sendJBPMPostRequest(projVerification, "/auctionreview").getBody();
  }

  public List<ProcessResponse> redistribute(ProjectListing listing) {
    ProjectListing projVerification = new ProjectListing();
    projVerification.setProcessId(listing.getProcessId());
    projVerification.setAuctionPrice(listing.getAuctionPrice());
    projVerification.setIsRedistributed(listing.getIsRedistributed());
    return this.sendJBPMPostRequest(projVerification, "/redistribution").getBody();
  }
}
