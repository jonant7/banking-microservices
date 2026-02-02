package com.banking.account.domain.exception;

import java.util.Map;

public class InvalidTransactionException extends AccountDomainException {

    private InvalidTransactionException(Map<String, Object> parameters) {
        super(AccountErrorCode.ACCOUNT_BUSINESS_INVALID_TRANSACTION, parameters);
    }

    public static InvalidTransactionException withReason(String reason) {
        return new InvalidTransactionException(
                Map.of(
                        "reason", reason
                )
        );
    }

}