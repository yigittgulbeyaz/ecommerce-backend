package com.yigit.ecommerce.dto.request.address;

import jakarta.validation.constraints.NotBlank;

public record AddressUpdateRequest(

        @NotBlank(message = "Address title is required")
        String title,

        @NotBlank(message = "Address line is required")
        String addressLine,

        @NotBlank(message = "City is required")
        String city,

        @NotBlank(message = "District is required")
        String district,

        @NotBlank(message = "Zip code is required")
        String zipCode,

        @NotBlank(message = "Country is required")
        String country
) {}
