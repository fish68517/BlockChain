package com.company.controllers;

import org.jbpm.services.api.model.ProcessInstanceDesc;
import org.jbpm.services.api.model.UserTaskInstanceDesc;
import org.jbpm.services.api.ProcessService;
import org.jbpm.services.api.RuntimeDataService;
import org.jbpm.services.api.UserTaskService;
import org.jbpm.services.api.model.ProcessDefinition;
import org.kie.api.task.model.TaskSummary;
import org.kie.internal.query.QueryFilter;
import org.kie.server.api.model.instance.VariableInstance;
import org.kie.server.api.model.instance.VariableInstanceList;
import org.kie.server.services.jbpm.ConvertUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.mail.Address;
import javax.mail.internet.MimeMessage;
import java.util.*;

import com.company.ServerApplication;
import com.company.enums.ProcessTaskNamesEnum;
import com.company.models.ProcessResponse;
import com.company.models.ProjectVerification;
import com.company.utils.ProcessUtility;
import com.icegreen.greenmail.spring.GreenMailBean;
import com.icegreen.greenmail.util.GreenMail;

@RestController
public class TaskController {
  private static final Logger logger = LoggerFactory.getLogger(ServerApplication.class);

  @Autowired
  private ProcessUtility processUtility;
  @Autowired
  private ProcessService processService;
  @Autowired
  private RuntimeDataService runtimeDataService;
  @Autowired
  private UserTaskService userTaskService;

  @Autowired
  private GreenMailBean greenMailBean;

  private final String deploymentId = "Evaluation_1.0.0-SNAPSHOT";

  private final String processId = "Submission_Process.NewListing";

  @PostMapping("/newListing")
  public ResponseEntity<List<ProcessResponse>> newListing(@RequestBody ProjectVerification projectVerification)
      throws Exception {
    Long processInstanceId = -1L;
    Map<String, Object> params = new HashMap<String, Object>();
    params.put("vin", projectVerification.getVin());
    params.put("make", projectVerification.getMake());
    params.put("model", projectVerification.getModel());
    params.put("vinMatched", projectVerification.getVinMatched());
    params.put("ccpg", projectVerification.getCCPG());
    params.put("fundingGoal", projectVerification.getFundingGoal());
    processInstanceId = processService.startProcess(deploymentId, processId,
        params);
    logger.info("Created process " + processInstanceId);

    try {
      List<Long> taskIds = runtimeDataService.getTasksByProcessInstanceId(processInstanceId);
      Long firstTaskId = taskIds.get(0);
      processUtility.completeTask(firstTaskId, "OwnerOne", params); // we know this
      // is always the first task
    } catch (Exception e) {
      System.out.println(e.getMessage());
    }

    List<ProcessResponse> taskResponses = processUtility.getPendingTasksByProcessId(processInstanceId);

    return ResponseEntity.ok(taskResponses);
  }

  // expects all params to be there!
  @PostMapping("/editListing")
  public ResponseEntity<List<ProcessResponse>> editListing(
      @RequestBody ProjectVerification projectVerification) throws Exception {
    Long processInstanceId = projectVerification.getProcessId();
    Map<String, Object> params = new HashMap<String, Object>();

    ProcessInstanceDesc instance = runtimeDataService.getProcessInstanceById(processInstanceId);
    if (instance == null) {
      System.out.println("Could not find instance with id = " + processInstanceId);
    }

    params.put("vin", projectVerification.getVin());
    params.put("make", projectVerification.getMake());
    params.put("model", projectVerification.getModel());
    params.put("vinMatched", projectVerification.getVinMatched());
    params.put("ccpg", projectVerification.getCCPG());
    params.put("fundingGoal", projectVerification.getFundingGoal());

    try {
      Long editListingTasks = processUtility.getTaskIdByProcessIdAndTitle(processInstanceId,
          ProcessTaskNamesEnum.editDetails);
      processUtility.completeTask(editListingTasks, "OwnerOne", params);
    } catch (Exception e) {
      System.out.println(e.getMessage());
    }

    List<ProcessResponse> taskResponses = processUtility.getPendingTasksByProcessId(processInstanceId);

    return ResponseEntity.ok(taskResponses);
  }

  @PostMapping("/verifyDetails")
  public ResponseEntity<List<ProcessResponse>> verifyDetails(
      @RequestBody ProjectVerification projectVerification) throws Exception {
    System.out.println("VERIFYING " + projectVerification.getProcessId());

    Map<String, Object> params = new HashMap<String, Object>();
    Long instanceId = projectVerification.getProcessId();
    params.put("detailsVerified", projectVerification.getVerifyDetails());
    System.out.println("detailsVerified" +
        projectVerification.getVerifyDetails());

    Long verifyTaskId = processUtility.getTaskIdByProcessIdAndTitle(instanceId,
        ProcessTaskNamesEnum.verifyDetails);
    processUtility.completeTask(verifyTaskId, "wbadmin", params);
    List<ProcessResponse> taskResponses = processUtility.getPendingTasksByProcessId(instanceId);

    return ResponseEntity.ok(taskResponses);
  }

  @PostMapping("/receiveTitle")
  public ResponseEntity<List<ProcessResponse>> receiveTitle(
      @RequestBody ProjectVerification projectVerification) throws Exception {
    System.out.println("RECEIVE " + projectVerification.toString());

    Map<String, Object> params = new HashMap<String, Object>();
    Long instanceId = projectVerification.getProcessId();
    params.put("titleReceived", projectVerification.getReceiveTitle());
    System.out.println("receivedTitle" + projectVerification.getReceiveTitle());

    Long recieveTitleTaskId = processUtility.getTaskIdByProcessIdAndTitle(instanceId,
        ProcessTaskNamesEnum.receiveTitle);
    processUtility.completeTask(recieveTitleTaskId, "wbadmin", params);
    List<ProcessResponse> taskResponses = processUtility.getPendingTasksByProcessId(instanceId);

    return ResponseEntity.ok(taskResponses);
  }

  @PostMapping("/valueEstimation")
  public ResponseEntity<List<ProcessResponse>> valueEstimation(@RequestBody ProjectVerification projectVerification)
      throws Exception {
    System.out.println("ESTIMATION " + projectVerification.toString());

    Map<String, Object> params = new HashMap<String, Object>();
    Long instanceId = projectVerification.getProcessId();
    params.put("carValueEstimate", projectVerification.getValueEstimation());
    params.put("restorationPriceEstimate",
        projectVerification.getRepairCostEstimation());

    Long valueEstimtionTaskId = processUtility.getTaskIdByProcessIdAndTitle(instanceId,
        ProcessTaskNamesEnum.valueEstimation);
    processUtility.completeTask(valueEstimtionTaskId, "wbadmin", params);
    List<ProcessResponse> taskResponses = processUtility.getPendingTasksByProcessId(instanceId);

    return ResponseEntity.ok(taskResponses);
  }

  /*
   * Funding begins and keeps in this state until funding reaches funding goal
   */
  @PostMapping("/funding")
  public ResponseEntity<List<ProcessResponse>> startRaisingFunding(@RequestBody ProjectVerification projectVerification)
      throws Exception {
    System.out.println("FUNDING " + projectVerification.toString());
    Map<String, Object> params = new HashMap();

    Long instanceId = projectVerification.getProcessId();
    params.put("currFundingAmount", projectVerification.getCurrFundingAmount());
    params.put("isFundingEnough", projectVerification.getIsFundingEnough());
    params.put("amount", projectVerification.getAmount());

    Long fundingTaskId = processUtility.getTaskIdByProcessIdAndTitle(instanceId,
        ProcessTaskNamesEnum.funding);
    processUtility.completeTask(fundingTaskId, "ContributorOne", params);
    List<ProcessResponse> taskResponses = processUtility.getPendingTasksByProcessId(instanceId);

    return ResponseEntity.ok(taskResponses);
  }

  /*
   * Restoration bidding begins
   */
  // @PostMapping("/restorationBid")
  // public ResponseEntity<List<ProcessResponse>>
  // addRestorationBid(@RequestBody
  // ProjectVerification projectVerification) throws Exception {
  // Map<String,Object> params=new HashMap();
  // System.out.println("RESTORATION BID " + projectVerification.toString());
  // params.put("fundingGoal", projectVerification.getBidPrice());
  // params.put("isBidSuccess", projectVerification.getIsBidSuccess());

  // Long instanceId = projectVerification.getProcessId();
  // Long restorationTaskId =
  // processUtility.getTaskIdByProcessIdAndTitle(instanceId,
  // ProcessTaskNamesEnum.bidReview);
  // processUtility.completeTask(restorationTaskId, "wbadmin", params);
  // List<ProcessResponse> taskResponses =
  // processUtility.getPendingTasksByProcessId(instanceId);

  // return ResponseEntity.ok(taskResponses);
  // }

  /*
   * Restorers are bidding until a bid is accepted
   */
  @PostMapping("/bidreview")

  public ResponseEntity<List<ProcessResponse>> reviewRestorationBid(
      @RequestBody ProjectVerification projectVerification) throws Exception {
    System.out.println("BID REVIEW " + projectVerification.toString());
    Map<String, Object> params = new HashMap();
    params.put("bidPrice", projectVerification.getBidPrice());
    params.put("isBidSuccess", projectVerification.getIsBidSuccess());
    // List<TaskSummary> taskSummaries =
    // runtimeDataService.getTasksAssignedAsPotentialOwner(name, new
    // QueryFilter());
    // // ORIGINAL
    Long instanceId = projectVerification.getProcessId();
    Long bidReviewTaskId = processUtility.getTaskIdByProcessIdAndTitle(instanceId,
        ProcessTaskNamesEnum.bidReview);
    processUtility.completeTask(bidReviewTaskId, "wbadmin", params);
    List<ProcessResponse> taskResponses = processUtility.getPendingTasksByProcessId(instanceId);

    return ResponseEntity.ok(taskResponses);
  }

  /*
   * Restoration project is assigned to accepted bid
   */
  @PostMapping("/assignrestoration")
  public ResponseEntity<List<ProcessResponse>> assignRestoration(@RequestBody ProjectVerification projectVerification)
      throws Exception {
    System.out.println("ASSIGN RESTORATION " + projectVerification.toString());
    Map<String, Object> params = new HashMap();
    params.put("restorer", projectVerification.getRestorerId());
    // List<TaskSummary> taskSummaries =
    // runtimeDataService.getTasksAssignedAsPotentialOwner(name, new
    // QueryFilter());
    // // ORIGINAL
    Long instanceId = projectVerification.getProcessId();
    Long assignRestorationTaskId = processUtility.getTaskIdByProcessIdAndTitle(instanceId,
        ProcessTaskNamesEnum.assignRestoration);
    processUtility.completeTask(assignRestorationTaskId, "wbadmin", params);
    List<ProcessResponse> taskResponses = processUtility.getPendingTasksByProcessId(instanceId);

    return ResponseEntity.ok(taskResponses);
  }

  /*
   * Restoration finished and car can be put up for auction
   */
  @PostMapping("/restorationfinished")
  public ResponseEntity<List<ProcessResponse>> finishRestoration(@RequestBody ProjectVerification projectVerification)
      throws Exception {
    System.out.println("RESTORATION FINISHED " + projectVerification.toString());
    Map<String, Object> params = new HashMap();
    params.put("isRestorationFinished",
        projectVerification.getIsRestorationFinished());
    // List<TaskSummary> taskSummaries =
    // runtimeDataService.getTasksAssignedAsPotentialOwner(name, new
    // QueryFilter());
    // // ORIGINAL
    Long instanceId = projectVerification.getProcessId();
    Long restorationFinishedTaskId = processUtility.getTaskIdByProcessIdAndTitle(instanceId,
        ProcessTaskNamesEnum.restorationFinished);
    processUtility.completeTask(restorationFinishedTaskId, "RestoreOne", params);
    List<ProcessResponse> taskResponses = processUtility.getPendingTasksByProcessId(instanceId);

    return ResponseEntity.ok(taskResponses);
  }

  /*
   * Selling price for auction item is set
   */
  @PostMapping("/postitem")
  public ResponseEntity<List<ProcessResponse>> postItem(@RequestBody ProjectVerification projectVerification)
      throws Exception {
    System.out.println("POST ITEM " + projectVerification.toString());
    Map<String, Object> params = new HashMap();
    params.put("fundingGoal", projectVerification.getFundingGoal());
    params.put("sellingPrice", projectVerification.getSellingPrice());
    // List<TaskSummary> taskSummaries =
    // runtimeDataService.getTasksAssignedAsPotentialOwner(name, new
    // QueryFilter());
    // // ORIGINAL
    Long instanceId = projectVerification.getProcessId();
    Long postItemTaskId = processUtility.getTaskIdByProcessIdAndTitle(instanceId,
        ProcessTaskNamesEnum.postItem);
    processUtility.completeTask(postItemTaskId, "wbadmin", params);
    List<ProcessResponse> taskResponses = processUtility.getPendingTasksByProcessId(instanceId);

    return ResponseEntity.ok(taskResponses);
  }

  /*
   * Auction begins
   */
  @PostMapping("/auction")
  public ResponseEntity<List<ProcessResponse>> startAuction(@RequestBody ProjectVerification projectVerification)
      throws Exception {
    System.out.println("AUCTION " + projectVerification.toString());
    Map<String, Object> params = new HashMap();
    params.put("vin", projectVerification.getVin());
    params.put("make", projectVerification.getMake());
    params.put("model", projectVerification.getModel());
    params.put("ccpg", projectVerification.getCCPG());
    params.put("auctionPrice", projectVerification.getAuctionPrice());
    // List<TaskSummary> taskSummaries =
    // runtimeDataService.getTasksAssignedAsPotentialOwner(name, new
    // QueryFilter());
    // // ORIGINAL
    Long instanceId = projectVerification.getProcessId();
    Long auctionTaskId = processUtility.getTaskIdByProcessIdAndTitle(instanceId,
        ProcessTaskNamesEnum.auction);
    processUtility.completeTask(auctionTaskId, "BuyerOne", params);
    List<ProcessResponse> taskResponses = processUtility.getPendingTasksByProcessId(instanceId);

    return ResponseEntity.ok(taskResponses);
  }

  /*
   * Auction bid accepted
   */
  @PostMapping("/auctionreview")
  public ResponseEntity<List<ProcessResponse>> reviewAuction(@RequestBody ProjectVerification projectVerification)
      throws Exception {
    System.out.println("AUCTION REVIEW " + projectVerification.toString());
    Map<String, Object> params = new HashMap();
    params.put("auctionPrice", projectVerification.getAuctionPrice());
    params.put("isAuctionAccept", projectVerification.getIsAuctionAccept());
    // List<TaskSummary> taskSummaries =
    // runtimeDataService.getTasksAssignedAsPotentialOwner(name, new
    // QueryFilter());
    // // ORIGINAL
    Long instanceId = projectVerification.getProcessId();
    Long auctionReviewTaskId = processUtility.getTaskIdByProcessIdAndTitle(instanceId,
        ProcessTaskNamesEnum.auctionReview);
    processUtility.completeTask(auctionReviewTaskId, "wbadmin", params);
    List<ProcessResponse> taskResponses = processUtility.getPendingTasksByProcessId(instanceId);

    return ResponseEntity.ok(taskResponses);
  }

  @PostMapping("/redistribution")
  public ResponseEntity<List<ProcessResponse>> startRedistribution(@RequestBody ProjectVerification projectVerification)
      throws Exception {
    System.out.println("REDISTRIBUTION " + projectVerification.toString());
    Map<String, Object> params = new HashMap();
    params.put("auctionPrice", projectVerification.getAuctionPrice());
    params.put("isRedistributed", projectVerification.getIsRedistributed());
    // List<TaskSummary> taskSummaries =
    // runtimeDataService.getTasksAssignedAsPotentialOwner(name, new
    // QueryFilter());
    // // ORIGINAL
    Long instanceId = projectVerification.getProcessId();
    Long redistributionTaskId = processUtility.getTaskIdByProcessIdAndTitle(instanceId,
        ProcessTaskNamesEnum.redistribution);
    processUtility.completeTask(redistributionTaskId, "wbadmin", params);
    List<ProcessResponse> taskResponses = processUtility.getPendingTasksByProcessId(instanceId);

    return ResponseEntity.ok(taskResponses);
  }

  /*
   * Process is completed
   */
  // @GetMapping("/completed")
  // public ResponseEntity<List<Collection<VariableDesc>>>
  // completedProjected(Principal principal) throws Exception {
  // Collection<ProcessInstanceDesc> processInstances =
  //
  // runtimeDataService.getProcessInstances(Collections.singletonList(ProcessInstance.STATE_COMPLETED),
  // principal.getName(), new QueryContext());
  // return ResponseEntity.ok(processInstances.stream()
  // .map(pi->{return
  // runtimeDataService.getVariablesCurrentState(pi.getId());})
  // .collect(Collectors.toList())
  // );
  // }

  @GetMapping("/instances")
  public ResponseEntity<List<VariableInstance>> instances(@RequestParam Long processInstanceId) throws Exception {
    // List<ProcessInstanceWithVarsDesc> result =
    // queryService.query("getVariablesCurrentState",
    // ProcessInstanceWithVarsQueryMapper.get(), new QueryContext(),
    // QueryParam.equalsTo(COLUMN_PROCESSINSTANCEID, 1L));
    // System.out.println(result);
    VariableInstanceList vi = ConvertUtils
        .convertToVariablesList(runtimeDataService.getVariablesCurrentState(processInstanceId));
    return ResponseEntity.ok(Arrays.asList(vi.getVariableInstances()));
  }

  @GetMapping("/emails")
  public ResponseEntity<List<String>> instances() throws Exception {
    GreenMail greenMail = greenMailBean.getGreenMail();
    MimeMessage[] emails = greenMail.getReceivedMessages();
    List<String> emailStrings = new ArrayList<>();
    for (MimeMessage email : emails) {
      StringBuilder sb = new StringBuilder();
      for (Address from : email.getFrom()) {
        sb.append(" From[" + from.toString() + "]");
      }
      for (Address recip : email.getAllRecipients()) {
        sb.append(" Recipient[" + recip.toString() + "]");
      }
      sb.append(" Subject[" + email.getSubject() + "]");
      sb.append(" Content[" + email.getContent() + "]");
      emailStrings.add(sb.toString());
    }
    return ResponseEntity.ok(emailStrings);
  }

  @PostMapping("/reject")
  public void rejectListing(@RequestBody ProjectVerification projectVerification) {
    processService.signalProcessInstance(projectVerification.getProcessId(),
        "rejected", "rejected");
    logger.info("Stopped Process " + projectVerification.getProcessId());
  }
}
