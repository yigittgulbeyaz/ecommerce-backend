package com.yigit.ecommerce.dto.response.address;

public record AddressResponse(
        Long id,
        String title,
        String addressLine,
        String city,
        String district,
        String zipCode,
        String country
) {}