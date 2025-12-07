package com.dlmgroup.collectorcoin.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.security.access.prepost.PreAuthorize;
import javax.transaction.Transactional;

import com.dlmgroup.collectorcoin.models.ProjectListing;


//@PreAuthorize("hasRole('ROLE_OWNER')")
public interface ProjectListingRepository extends PagingAndSortingRepository<ProjectListing, Long> {

	@Override
	@PreAuthorize("#projectListings?.user == null or #projectListings?.user?.username == authentication?.username")
	ProjectListing save(@Param("listing") ProjectListing listing);

	@Override
	void deleteById(@Param("id") Long id);

	@Override
	@PreAuthorize("#projectListings?.user?.username == authentication?.username")
	void delete(@Param("listing") ProjectListing listing);

	@Override
	public List<ProjectListing> findAll();

  public Optional<ProjectListing> findByProcessId(Long processId);

	List<ProjectListing> findByUserId(Long userId);

	@Transactional
	void deleteByUserId(long userId);
}
