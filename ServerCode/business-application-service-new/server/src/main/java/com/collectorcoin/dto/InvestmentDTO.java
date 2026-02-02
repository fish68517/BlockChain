package com.collectorcoin.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO for investment information.
 */
@Data
public class InvestmentDTO {

    private Long id;
    private Long listingId;
    private String investorAddress;
    private BigDecimal amount;
    private String transactionHash;
    private LocalDateTime investedAt;
}
