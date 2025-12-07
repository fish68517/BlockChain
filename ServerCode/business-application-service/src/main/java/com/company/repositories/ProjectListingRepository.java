package com.company.repositories;

import java.util.List;
import java.util.Optional;
import javax.transaction.Transactional;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.data.repository.query.Param;

import com.company.models.ProjectListing;

@Repository
public interface ProjectListingRepository extends PagingAndSortingRepository<ProjectListing, Long> {

  @PreAuthorize("#projectListings?.user == null or #projectListings?.user?.username == authentication?.username")
  ProjectListing save(@Param("listing") ProjectListing listing);

  public Optional<ProjectListing> findByProcessId(String processId);

  @Override
  @PreAuthorize("#projectListings?.user?.username == authentication?.username")
  void delete(@Param("listing") ProjectListing listing);

  @Override
  public List<ProjectListing> findAll();

  List<ProjectListing> findByUserId(Long userId);

  public Optional<ProjectListing> findByProcessId(Long processId);

  @Transactional
  void deleteByUserId(Long id);
}
