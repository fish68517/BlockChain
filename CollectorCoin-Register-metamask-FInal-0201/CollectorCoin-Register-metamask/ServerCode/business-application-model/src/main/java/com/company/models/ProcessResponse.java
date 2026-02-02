package com.company.models;

import java.io.Serializable;

public class ProcessResponse implements Serializable {
  Long processInstanceId;
  String taskName;
  String status;

  ProcessResponse() {
  }

  public ProcessResponse(Long id, String name) {
    this.processInstanceId = id;
    this.taskName = name;
  }

  public ProcessResponse(Long id, String name, String status) {
    this.processInstanceId = id;
    this.taskName = name;
    this.status = status;
  }

  public Long getProcessInstanceId() {
    return this.processInstanceId;
  }

  public String getTaskName() {
    return this.taskName;
  }

  public String getStatus() {
    return this.status;
  }
}
