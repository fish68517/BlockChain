package com.collectorcoin.repository;

import com.collectorcoin.model.RestorerBid;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface RestorerBidRepository extends JpaRepository<RestorerBid, Long> {

    List<RestorerBid> findByListingIdOrderByBidAmountAsc(Long listingId);

    List<RestorerBid> findByRestorerId(Long restorerId);

    List<RestorerBid> findByListingIdAndStatus(Long listingId, String status);
}
