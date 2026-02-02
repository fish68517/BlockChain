package com.collectorcoin.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * Request DTO for finalizing auction.
 */
@Data
public class FinalizeAuctionRequest {

    @NotBlank(message = "Winner address is required")
    private String winnerAddress;
}
