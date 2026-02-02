package com.collectorcoin.repository;

import com.collectorcoin.model.ProjectListing;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository for ProjectListing entity.
 */
@Repository
public interface ProjectListingRepository extends JpaRepository<ProjectListing, Long> {

    Optional<ProjectListing> findByProjectAddress(String projectAddress);

    Optional<ProjectListing> findByProcessId(Long processId);

    Optional<ProjectListing> findByNftTokenId(Long nftTokenId);
}
