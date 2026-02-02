package com.collectorcoin.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import java.math.BigDecimal;

/**
 * Request DTO for creating a new listing.
 */
@Data
public class CreateListingRequest {

    @NotBlank(message = "Title is required")
    private String title;

    private String description;

    private String vin;

    private String imageUrl;

    @NotNull(message = "Owner value estimation is required")
    @Positive(message = "Value estimation must be positive")
    private BigDecimal ownerValueEstimation;

    @NotNull(message = "Owner repair estimation is required")
    @Positive(message = "Repair estimation must be positive")
    private BigDecimal ownerRepairEstimation;
}
