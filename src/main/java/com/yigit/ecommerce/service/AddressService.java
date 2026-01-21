package com.yigit.ecommerce.service;

import com.yigit.ecommerce.dto.request.address.AddressCreateRequest;
import com.yigit.ecommerce.dto.request.address.AddressUpdateRequest;
import com.yigit.ecommerce.dto.response.address.AddressResponse;

import java.util.List;

public interface AddressService {

    AddressResponse createAddress(AddressCreateRequest request);

    List<AddressResponse> getMyAddresses();

    AddressResponse getAddressById(Long id);

    AddressResponse updateAddress(Long id, AddressUpdateRequest request);

    void deleteAddress(Long id);
}