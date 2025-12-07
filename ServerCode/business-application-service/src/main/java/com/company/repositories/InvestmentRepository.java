package com.company.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.repository.query.Param;

import com.company.models.Investment;

@Repository
public interface InvestmentRepository extends JpaRepository<Investment, Long> {

  @Override
  Investment save(@Param("investment") Investment investment);

  @Override
  public List<Investment> findAll();

  @Override
  void deleteById(@Param("id") Long id);

  List<Investment> findByUserId(Long userId);

  Investment findFirstByUserIdAndListingId(Long userId, Long listingId);
}