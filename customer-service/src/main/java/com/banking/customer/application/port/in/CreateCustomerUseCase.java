package com.banking.customer.application.port.in;

import com.banking.customer.application.dto.CustomerRequest;
import com.banking.customer.application.dto.CustomerResponse;

public interface CreateCustomerUseCase {

    CustomerResponse create(CustomerRequest request);

}