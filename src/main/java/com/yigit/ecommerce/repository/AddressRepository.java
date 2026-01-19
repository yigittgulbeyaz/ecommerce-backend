package com.yigit.ecommerce.repository;

import com.yigit.ecommerce.model.address.Address;
import com.yigit.ecommerce.model.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AddressRepository extends JpaRepository<Address, Long> {
    List<Address> findAllByUser(User user);
}