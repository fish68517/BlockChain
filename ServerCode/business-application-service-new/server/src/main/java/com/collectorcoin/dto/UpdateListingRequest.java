package com.collectorcoin.dto;

import lombok.Data;

/**
 * Request DTO for updating a listing.
 */
@Data
public class UpdateListingRequest {

    private String title;
    private String description;
    private String vin;
    private String status;
}
