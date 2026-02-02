package com.banking.account.domain.repository;

import com.banking.account.domain.model.CustomerInfo;

import java.util.Optional;
import java.util.UUID;

public interface CustomerProjectionRepository {

    void save(CustomerInfo customerInfo);

    Optional<CustomerInfo> findById(UUID customerId);

    boolean existsById(UUID customerId);

}