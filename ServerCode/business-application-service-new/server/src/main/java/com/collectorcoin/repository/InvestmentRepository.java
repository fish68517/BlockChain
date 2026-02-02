package com.collectorcoin.repository;

import com.collectorcoin.model.Investment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InvestmentRepository extends JpaRepository<Investment, Long> {
    List<Investment> findByUserId(Long userId);
    List<Investment> findByListingId(Long listingId);
    Optional<Investment> findFirstByUserIdAndListingId(Long userId, Long listingId);
    List<Investment> findByStatus(String status);
}
