package com.yigit.ecommerce.controller.address;

import com.yigit.ecommerce.common.ApiResponse;
import com.yigit.ecommerce.dto.request.address.AddressCreateRequest;
import com.yigit.ecommerce.dto.request.address.AddressUpdateRequest;
import com.yigit.ecommerce.dto.response.address.AddressResponse;
import com.yigit.ecommerce.service.AddressService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/addresses")
public class AddressController {

    private final AddressService addressService;

    public AddressController(AddressService addressService) {
        this.addressService = addressService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<AddressResponse>> create(
            @Valid @RequestBody AddressCreateRequest request) {

        AddressResponse address = addressService.createAddress(request);
        ApiResponse<AddressResponse> res =
                ApiResponse.success(address, "Address created");

        return ResponseEntity.status(res.getStatus()).body(res);
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<AddressResponse>>> getMyAddresses() {

        List<AddressResponse> addresses = addressService.getMyAddresses();
        ApiResponse<List<AddressResponse>> res =
                ApiResponse.success(addresses, "Addresses fetched");

        return ResponseEntity.status(res.getStatus()).body(res);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<AddressResponse>> getById(@PathVariable Long id) {

        AddressResponse address = addressService.getAddressById(id);
        ApiResponse<AddressResponse> res =
                ApiResponse.success(address, "Address fetched");

        return ResponseEntity.status(res.getStatus()).body(res);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<AddressResponse>> update(
            @PathVariable Long id,
            @Valid @RequestBody AddressUpdateRequest request) {

        AddressResponse address = addressService.updateAddress(id, request);
        ApiResponse<AddressResponse> res =
                ApiResponse.success(address, "Address updated");

        return ResponseEntity.status(res.getStatus()).body(res);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {

        addressService.deleteAddress(id);
        ApiResponse<Void> res =
                ApiResponse.success(null, "Address deleted");

        return ResponseEntity.status(res.getStatus()).body(res);
    }
}
