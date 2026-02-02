package com.banking.account.domain.exception;

import java.util.Map;
import java.util.UUID;

public class InactiveCustomerException extends AccountDomainException {

    private InactiveCustomerException(Map<String, Object> parameters) {
        super(AccountErrorCode.CUSTOMER_BUSINESS_INACTIVE, parameters);
    }

    public static InactiveCustomerException notFound(UUID customerId) {
        return new InactiveCustomerException(
                Map.of(
                        "customerId", customerId.toString(),
                        "status", "NOT_FOUND"
                )
        );
    }

    public static InactiveCustomerException inactive(UUID customerId) {
        return new InactiveCustomerException(
                Map.of(
                        "customerId", customerId.toString(),
                        "status", "INACTIVE"
                )
        );
    }
}