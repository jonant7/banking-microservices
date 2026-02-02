package com.banking.customer.domain.repository;

import com.banking.customer.domain.model.Customer;
import com.banking.customer.domain.model.CustomerId;
import com.banking.customer.domain.model.Identification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.UUID;

public interface CustomerRepository {

    Customer save(Customer customer);

    Optional<Customer> findById(UUID id);

    Page<Customer> findAll(Object filter, Pageable pageable);

    boolean existsByCustomerId(CustomerId customerId);

    boolean existsByIdentification(Identification identification);

}