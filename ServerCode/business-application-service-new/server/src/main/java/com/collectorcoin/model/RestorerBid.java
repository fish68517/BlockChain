package com.collectorcoin.model;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Entity representing a restorer's bid for a project.
 */
@Data
@Entity
@Table(name = "restorer_bid")
public class RestorerBid {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "listing_id", nullable = false)
    private Long listingId;

    @Column(name = "restorer_id", nullable = false)
    private Long restorerId;

    @Column(name = "restorer_address")
    private String restorerAddress;

    @Column(name = "bid_amount", precision = 18, scale = 2, nullable = false)
    private BigDecimal bidAmount;

    @Column(name = "status")
    private String status = "PENDING"; // PENDING, SELECTED, REJECTED

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
