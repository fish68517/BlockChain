package com.collectorcoin.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import java.math.BigDecimal;

/**
 * Request DTO for posting auction.
 */
@Data
public class AuctionRequest {

    @NotNull(message = "Auction price is required")
    @Positive(message = "Price must be positive")
    private BigDecimal auctionPrice;
}
