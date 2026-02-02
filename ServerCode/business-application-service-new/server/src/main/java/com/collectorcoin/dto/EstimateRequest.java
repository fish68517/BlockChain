package com.collectorcoin.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import java.math.BigDecimal;

/**
 * Request DTO for value estimation.
 */
@Data
public class EstimateRequest {

    @NotNull(message = "Value estimation is required")
    @Positive(message = "Value must be positive")
    private BigDecimal valueEstimation;

    @NotNull(message = "Repair estimation is required")
    @Positive(message = "Repair cost must be positive")
    private BigDecimal repairEstimation;
}
