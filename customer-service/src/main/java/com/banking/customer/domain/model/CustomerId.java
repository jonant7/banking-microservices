package com.banking.customer.domain.model;

import com.banking.customer.domain.exception.CustomerErrorCode;

import java.util.Objects;

public record CustomerId(String value) {

    private static final int MAX_LENGTH = 50;

    public CustomerId {
        if (Objects.isNull(value)) {
            throw new IllegalArgumentException(CustomerErrorCode.CUSTOMER_VALIDATION_ID_NULL.getCode());
        }

        String trimmed = value.trim();

        if (trimmed.isEmpty()) {
            throw new IllegalArgumentException(CustomerErrorCode.CUSTOMER_VALIDATION_ID_EMPTY.getCode());
        }

        if (trimmed.length() > MAX_LENGTH) {
            throw new IllegalArgumentException(CustomerErrorCode.CUSTOMER_VALIDATION_ID_TOO_LONG.getCode());
        }

        value = trimmed;
    }

    public static CustomerId of(String value) {
        return new CustomerId(value);
    }

}