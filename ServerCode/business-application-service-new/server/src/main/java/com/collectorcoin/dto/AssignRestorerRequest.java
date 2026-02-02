package com.collectorcoin.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * Request DTO for assigning restorer.
 */
@Data
public class AssignRestorerRequest {

    @NotBlank(message = "Restorer address is required")
    private String restorerAddress;
}
