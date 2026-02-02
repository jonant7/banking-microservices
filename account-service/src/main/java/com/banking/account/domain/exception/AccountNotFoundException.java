package com.banking.account.domain.exception;

import java.util.Map;
import java.util.UUID;

public class AccountNotFoundException extends AccountDomainException {

    private AccountNotFoundException(Map<String, Object> parameters) {
        super(AccountErrorCode.ACCOUNT_BUSINESS_NOT_FOUND, parameters);
    }

    public static AccountNotFoundException withId(UUID id) {
        return new AccountNotFoundException(
                Map.of(
                        "field", "id",
                        "value", id.toString()
                )
        );
    }

    public static AccountNotFoundException withAccountNumber(String accountNumber) {
        return new AccountNotFoundException(
                Map.of(
                        "field", "accountNumber",
                        "value", accountNumber
                )
        );
    }

    public static AccountNotFoundException withCustomerId(String customerId) {
        return new AccountNotFoundException(
                Map.of(
                        "field", "customerId",
                        "value", customerId
                )
        );
    }

}