package com.banking.customer.application.port.in;

import com.banking.customer.application.dto.CustomerResponse;

import java.util.UUID;

public interface ManageCustomerStatusUseCase {

    CustomerResponse activate(UUID id);

    CustomerResponse deactivate(UUID id);

}