package com.yigit.ecommerce.service.impl;

import com.yigit.ecommerce.dto.request.address.AddressCreateRequest;
import com.yigit.ecommerce.dto.request.address.AddressUpdateRequest;
import com.yigit.ecommerce.dto.response.address.AddressResponse;
import com.yigit.ecommerce.exception.ForbiddenException;
import com.yigit.ecommerce.exception.NotFoundException;
import com.yigit.ecommerce.mapper.AddressMapper;
import com.yigit.ecommerce.model.address.Address;
import com.yigit.ecommerce.model.user.User;
import com.yigit.ecommerce.repository.AddressRepository;
import com.yigit.ecommerce.security.context.AuthenticationContext;
import com.yigit.ecommerce.service.AddressService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class AddressServiceImpl implements AddressService {

    private final AddressRepository addressRepository;
    private final AuthenticationContext authenticationContext;

    public AddressServiceImpl(AddressRepository addressRepository,
                              AuthenticationContext authenticationContext) {
        this.addressRepository = addressRepository;
        this.authenticationContext = authenticationContext;
    }

    @Override
    public AddressResponse createAddress(AddressCreateRequest request) {
        User user = authenticationContext.requireUser();

        Address address = Address.create(request, user);
        return AddressMapper.toResponse(addressRepository.save(address));
    }

    @Override
    public List<AddressResponse> getMyAddresses() {
        User user = authenticationContext.requireUser();

        return addressRepository.findAllByUser(user).stream()
                .map(AddressMapper::toResponse)
                .toList();
    }

    @Override
    public AddressResponse getAddressById(Long id) {
        Address address = getAddressOrThrow(id);
        checkOwnership(address);
        return AddressMapper.toResponse(address);
    }

    @Override
    public AddressResponse updateAddress(Long id, AddressUpdateRequest request) {
        Address address = getAddressOrThrow(id);
        checkOwnership(address);

        address.update(request);
        return AddressMapper.toResponse(address);
    }

    @Override
    public void deleteAddress(Long id) {
        Address address = getAddressOrThrow(id);
        checkOwnership(address);
        addressRepository.delete(address);
    }


    private Address getAddressOrThrow(Long id) {
        return addressRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Address not found: " + id));
    }

    private void checkOwnership(Address address) {
        Long currentUserId = authenticationContext.requireUserId();
        if (!address.isOwnedBy(currentUserId)) {
            throw new ForbiddenException("You are not allowed to access this address");
        }
    }
}
