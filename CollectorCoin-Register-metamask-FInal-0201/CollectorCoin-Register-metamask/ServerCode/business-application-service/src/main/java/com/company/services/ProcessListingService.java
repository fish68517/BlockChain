package com.company.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.hibernate.annotations.common.util.impl.Log_$logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.company.models.ProcessResponse;
import com.company.models.ProjectListing;
import com.company.repositories.ProjectListingRepository;

@Component
public class ProcessListingService {

  @Autowired
  private ProjectListingRepository projectListingRepository;

  public List<String> convertProcessResponseToString(List<ProcessResponse> list) {
    List<String> names = new ArrayList<String>();
    for (ProcessResponse r : list) {
      names.add(r.getTaskName());
    }

    return names;
  }

  public List<ProjectListing> convertProcessResponseToProjectListing(List<ProcessResponse> list) {
    List<ProjectListing> listings = new ArrayList<ProjectListing>();
    for (ProcessResponse r : list) {
      // System.out.println(r.getProcessInstanceId());
      // System.out.println(r.getTaskName());
      // System.out.println(r.getStatus());
      Optional<ProjectListing> l = projectListingRepository.findByProcessId(r.getProcessInstanceId());
      if (l.isPresent()) {
        ProjectListing listing = l.get();
        listings.add(listing);
      } else {
        System.out.println("Listing not found with id = " + r.getProcessInstanceId());

      }
      // ProjectListing l =
      // projectListingRepository.findByProcessId(r.getProcessInstanceId())
      // .orElseThrow(() -> new ResourceNotFoundException("Listing not found with id =
      // " + r.getProcessInstanceId()));

      // listings.add(l);

    }
    System.out.println(listings);
    return listings;
  }

}
