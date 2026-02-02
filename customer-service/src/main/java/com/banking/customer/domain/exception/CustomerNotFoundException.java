package com.banking.customer.domain.exception;

import java.util.Map;
import java.util.UUID;

public class CustomerNotFoundException extends CustomerDomainException {

    private CustomerNotFoundException(Map<String, Object> parameters) {
        super(CustomerErrorCode.CUSTOMER_BUSINESS_NOT_FOUND, parameters);
    }

    public static CustomerNotFoundException withId(UUID id) {
        return new CustomerNotFoundException(
                Map.of(
                        "field", "id",
                        "value", id.toString()
                )
        );
    }

    public static CustomerNotFoundException withCustomerId(String customerId) {
        return new CustomerNotFoundException(
                Map.of(
                        "field", "customerId",
                        "value", customerId
                )
        );
    }

    public static CustomerNotFoundException withIdentification(String identification) {
        return new CustomerNotFoundException(
                Map.of(
                        "field", "identification",
                        "value", identification
                )
        );
    }

}