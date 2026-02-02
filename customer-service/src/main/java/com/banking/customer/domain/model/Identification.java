package com.banking.customer.domain.model;

import com.banking.customer.domain.exception.CustomerErrorCode;

import java.util.Objects;
import java.util.regex.Pattern;

public record Identification(String value) {

    private static final Pattern VALID_PATTERN = Pattern.compile("^[0-9]{10,20}$");

    public Identification {
        if (Objects.isNull(value)) {
            throw new IllegalArgumentException(CustomerErrorCode.CUSTOMER_VALIDATION_IDENTIFICATION_NULL.getCode());
        }

        String cleaned = value.replaceAll("[^0-9]", "");

        if (!VALID_PATTERN.matcher(cleaned).matches()) {
            throw new IllegalArgumentException(
                    CustomerErrorCode.CUSTOMER_VALIDATION_IDENTIFICATION_INVALID_FORMAT.getCode()
            );
        }

        value = cleaned;
    }

    public static Identification of(String value) {
        return new Identification(value);
    }

}