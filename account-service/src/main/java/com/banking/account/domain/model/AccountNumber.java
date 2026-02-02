package com.banking.account.domain.model;

import com.banking.account.domain.exception.AccountErrorCode;

import java.util.Objects;

public record AccountNumber(String value) {

    private static final int MAX_LENGTH = 20;
    private static final String PATTERN = "^[0-9]{6,20}$";

    public AccountNumber {
        validate(value);
    }

    public static AccountNumber of(String value) {
        return new AccountNumber(value);
    }

    private static void validate(String value) {
        if (Objects.isNull(value)) {
            throw new IllegalArgumentException(AccountErrorCode.ACCOUNT_VALIDATION_NUMBER_NULL.getCode());
        }

        if (value.isBlank()) {
            throw new IllegalArgumentException(AccountErrorCode.ACCOUNT_VALIDATION_NUMBER_EMPTY.getCode());
        }

        if (value.length() > MAX_LENGTH) {
            throw new IllegalArgumentException(AccountErrorCode.ACCOUNT_VALIDATION_NUMBER_TOO_LONG.getCode());
        }

        if (!value.matches(PATTERN)) {
            throw new IllegalArgumentException(
                    AccountErrorCode.ACCOUNT_VALIDATION_NUMBER_INVALID_FORMAT.getCode()
            );
        }
    }

}