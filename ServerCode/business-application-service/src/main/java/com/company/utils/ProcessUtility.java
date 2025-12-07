package com.company.utils;

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
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

import com.company.models.ProcessResponse;
import com.company.models.ProjectVerification;
import com.icegreen.greenmail.spring.GreenMailBean;
import com.icegreen.greenmail.util.GreenMail;

@Component
public class ProcessUtility {
  private static final Logger logger = LoggerFactory.getLogger(ProcessUtility.class);
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

  public Long getTaskIdByProcessIdAndTitle(Long instanceId, String title) throws Exception {
    System.out.println("AAAA " + instanceId + ' ' + title);
    ProcessInstanceDesc instance = runtimeDataService.getProcessInstanceById(instanceId);
    if (instance == null) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Could not find instance " + instanceId);
    }

    List<UserTaskInstanceDesc> tasks = instance.getActiveTasks();
    Long recieveTitleTaskId = null;
    for (UserTaskInstanceDesc t : tasks) {
      if (t.getName().equals(title)) {
        recieveTitleTaskId = t.getTaskId();
      }
    }

    if (recieveTitleTaskId == null) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, title + " task not found for " + instanceId);
    }

    return recieveTitleTaskId;
  }

  public void completeTask(Long id, String name, Map<String, Object> params) {
    userTaskService.claim(id, name);
    userTaskService.start(id, name);
    userTaskService.complete(id, name, params);
  }

  public List<ProcessResponse> getPendingTasksByProcessId(Long instanceId) throws Exception {
    List<ProcessResponse> taskResponses = new ArrayList<ProcessResponse>();
    List<Long> taskIds = runtimeDataService.getTasksByProcessInstanceId(instanceId);
    for (Long taskId : taskIds) {
      UserTaskInstanceDesc task = runtimeDataService.getTaskById(taskId);
      if (task.getStatus().equals("Ready")) {
        taskResponses.add(new ProcessResponse(instanceId, task.getName(), task.getStatus()));
      }
    }
    ;

    return taskResponses;
  }
}
