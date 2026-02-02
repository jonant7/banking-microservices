package com.banking.customer.domain.exception;

import java.util.Map;

public class DuplicateCustomerException extends CustomerDomainException {

    private DuplicateCustomerException(CustomerErrorCode errorCode, Map<String, Object> parameters) {
        super(errorCode, parameters);
    }

    public static DuplicateCustomerException withCustomerId(String customerId) {
        return new DuplicateCustomerException(
                CustomerErrorCode.CUSTOMER_BUSINESS_DUPLICATE_ID,
                Map.of("value", customerId)
        );
    }

    public static DuplicateCustomerException withIdentification(String identification) {
        return new DuplicateCustomerException(
                CustomerErrorCode.CUSTOMER_BUSINESS_DUPLICATE_IDENTIFICATION,
                Map.of("value", identification)
        );
    }

}