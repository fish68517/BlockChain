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
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.springframework.context.annotation.Lazy;

import javax.mail.Address;
import javax.mail.internet.MimeMessage;
import java.util.*;

import com.company.ServerApplication;
import com.company.enums.ProcessTaskNamesEnum;
import com.company.models.ProcessResponse;
import com.company.models.ProjectVerification;
import com.icegreen.greenmail.spring.GreenMailBean;
import com.icegreen.greenmail.util.GreenMail;

@RestController
@ImportAutoConfiguration(GreenMailBean.class)
@RequestMapping("/processes")
public class ProcessesController {
  private static final Logger logger = LoggerFactory.getLogger(ServerApplication.class);

  @Autowired
  private RuntimeDataService runtimeDataService;

  private final String processId = "Submission_Process.NewListing";

  @GetMapping("/all")
  public ResponseEntity<List<ProcessResponse>> getProcesses() {
    List<ProcessResponse> all = new ArrayList<ProcessResponse>();
    Collection<ProcessInstanceDesc> instances = runtimeDataService
        .getProcessInstancesByProcessDefinition(processId, new QueryFilter());
    instances.forEach(instance -> {
      Long instanceId = instance.getId();
      List<Long> taskIds = runtimeDataService.getTasksByProcessInstanceId(instanceId);
      if (taskIds == null) {
        return;
      }

      System.out.println(" TASKS:");
      taskIds.forEach(taskId -> {
        UserTaskInstanceDesc task = runtimeDataService.getTaskById(taskId);
        all.add(new ProcessResponse(instanceId, task.getName(), task.getStatus()));
      });
    });

    return ResponseEntity.ok(all);
  }

  @GetMapping("/admin")
  public ResponseEntity<List<ProcessResponse>> getAdminTasks() {
    List<ProcessResponse> adminTasks = new ArrayList<ProcessResponse>();
    List<TaskSummary> taskSummaries = runtimeDataService.getTasksAssignedAsPotentialOwner("wbadmin", new QueryFilter());
    System.out.println(" ADMIN TASKS 2222:");

    taskSummaries.forEach(s -> {
      System.out.println(s.getName());
      adminTasks.add(new ProcessResponse(s.getProcessInstanceId(), s.getName(),
          s.getStatusId()));
    });

    return ResponseEntity.ok(adminTasks);
  }

  @GetMapping("/{id}/tasks")
  public ResponseEntity<List<ProcessResponse>> getProcessTasksById(
      @PathVariable("id") Long instanceId,
      @RequestParam(value = "status", required = false) String status) {
    List<ProcessResponse> taskResponses = new ArrayList<ProcessResponse>();
    List<Long> taskIds = runtimeDataService.getTasksByProcessInstanceId(instanceId);
    taskIds.forEach(t -> {
      UserTaskInstanceDesc task = runtimeDataService.getTaskById(t);

      if (status == null || task.getStatus().equals(status)) {
        taskResponses.add(new ProcessResponse(instanceId, task.getName(),
            task.getStatus()));
      }
    });

    return ResponseEntity.ok(taskResponses);
  }

  @GetMapping("/restorer")
  public ResponseEntity<List<ProcessResponse>> getRestorerTasks() {
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

    return ResponseEntity.ok(restorerTasks);
  }

  @GetMapping("/buyer")
  public ResponseEntity<List<ProcessResponse>> getBuyerTasks() {
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

    return ResponseEntity.ok(buyerTasks);
  }

  @GetMapping("/investor")
  public ResponseEntity<List<ProcessResponse>> getInvestorTasks() {
    List<ProcessResponse> investorTasks = new ArrayList<ProcessResponse>();
    List<TaskSummary> taskSummaries = runtimeDataService.getTasksAssignedAsPotentialOwner("ContributorOne",
        new QueryFilter());
    System.out.println(" INVESTOR TASKS:");

    taskSummaries.forEach(s -> {
      System.out.println(s.getName());
      investorTasks.add(new ProcessResponse(s.getProcessInstanceId(), s.getName(),
          s.getStatusId()));
    });

    return ResponseEntity.ok(investorTasks);
  }
}
