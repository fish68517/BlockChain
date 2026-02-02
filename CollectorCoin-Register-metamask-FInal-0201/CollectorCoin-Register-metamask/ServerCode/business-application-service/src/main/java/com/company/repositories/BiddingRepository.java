package com.company.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import com.company.models.Bidding;

//@PreAuthorize("hasRole('ROLE_RESTORER')")
public interface BiddingRepository extends JpaRepository<Bidding, Long> {

  List<Bidding> findByBidderId(Long bidderId);

  @Override
  Bidding save(@Param("bid") Bidding bid);
}
