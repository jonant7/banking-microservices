package com.banking.customer.application.port.in;

import com.banking.customer.application.dto.CustomerFilter;
import com.banking.customer.application.dto.CustomerResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface GetCustomerUseCase {

    CustomerResponse findById(UUID id);

    Page<CustomerResponse> findAll(CustomerFilter filter, Pageable pageable);

}