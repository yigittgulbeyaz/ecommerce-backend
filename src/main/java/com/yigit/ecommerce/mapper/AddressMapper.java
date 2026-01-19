package com.yigit.ecommerce.mapper;

import com.yigit.ecommerce.dto.response.address.AddressResponse;
import com.yigit.ecommerce.model.address.Address;

public final class AddressMapper {

    private AddressMapper() {
    }

    public static AddressResponse toResponse(Address address) {
        if (address == null)
            return null;

        return new AddressResponse(
                address.getId(),
                address.getTitle(),
                address.getAddressLine(),
                address.getCity(),
                address.getDistrict(),
                address.getZipCode(),
                address.getCountry()
        );
    }
}
