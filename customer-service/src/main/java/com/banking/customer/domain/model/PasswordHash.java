package com.banking.customer.domain.model;

import com.banking.customer.domain.exception.CustomerErrorCode;

import java.util.Objects;

public record PasswordHash(String value) {

    public PasswordHash {
        if (Objects.isNull(value)) {
            throw new IllegalArgumentException(CustomerErrorCode.CUSTOMER_VALIDATION_PASSWORD_NULL.getCode());
        }

        if (value.isBlank()) {
            throw new IllegalArgumentException(CustomerErrorCode.CUSTOMER_VALIDATION_PASSWORD_EMPTY.getCode());
        }
    }

    public static PasswordHash fromHash(String hashedValue) {
        return new PasswordHash(hashedValue);
    }

    @Override
    public String toString() {
        return "[PROTECTED]";
    }

}