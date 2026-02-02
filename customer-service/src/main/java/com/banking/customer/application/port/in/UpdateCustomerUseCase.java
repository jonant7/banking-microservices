package com.banking.customer.application.port.in;

import com.banking.customer.application.dto.CustomerRequest;
import com.banking.customer.application.dto.CustomerResponse;
import com.banking.customer.application.dto.CustomerUpdateRequest;

import java.util.UUID;

public interface UpdateCustomerUseCase {

    CustomerResponse update(UUID id, CustomerRequest request);

    CustomerResponse patch(UUID id, CustomerUpdateRequest request);

}