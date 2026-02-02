package com.banking.customer.domain.exception;

import java.util.Map;

public class InactiveCustomerException extends CustomerDomainException {

    private InactiveCustomerException(Map<String, Object> parameters) {
        super(CustomerErrorCode.CUSTOMER_BUSINESS_INACTIVE, parameters);
    }

    public static InactiveCustomerException withCustomerId(String customerId) {
        return new InactiveCustomerException(
                Map.of("customerId", customerId)
        );
    }

}