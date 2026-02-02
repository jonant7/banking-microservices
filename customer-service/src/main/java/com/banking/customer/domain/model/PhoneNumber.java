package com.banking.customer.domain.model;

import com.banking.customer.domain.exception.CustomerErrorCode;

import java.util.Objects;

public record PhoneNumber(String value) {

    private static final String PHONE_PATTERN = "^\\d{7,20}$";

    public PhoneNumber {
        if (Objects.isNull(value)) {
            throw new IllegalArgumentException(CustomerErrorCode.CUSTOMER_VALIDATION_PHONE_NULL.getCode());
        }

        if (value.isBlank()) {
            throw new IllegalArgumentException(CustomerErrorCode.CUSTOMER_VALIDATION_PHONE_EMPTY.getCode());
        }

        String normalized = value.replaceAll("[^0-9]", "");

        if (!normalized.matches(PHONE_PATTERN)) {
            throw new IllegalArgumentException(
                    CustomerErrorCode.CUSTOMER_VALIDATION_PHONE_INVALID_FORMAT.getCode()
            );
        }

        value = normalized;
    }

    public static PhoneNumber of(String value) {
        return new PhoneNumber(value);
    }

    public static PhoneNumber ofNullable(String value) {
        if (Objects.isNull(value) || value.isBlank()) {
            return null;
        }
        return new PhoneNumber(value);
    }

}