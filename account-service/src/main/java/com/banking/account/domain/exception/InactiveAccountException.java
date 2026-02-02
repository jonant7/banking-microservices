package com.banking.account.domain.exception;

import java.util.Map;

public class InactiveAccountException extends AccountDomainException {

    private InactiveAccountException(Map<String, Object> parameters) {
        super(AccountErrorCode.ACCOUNT_BUSINESS_INACTIVE, parameters);
    }

    public static InactiveAccountException withAccountNumber(String accountNumber) {
        return new InactiveAccountException(
                Map.of(
                        "accountNumber", accountNumber
                )
        );
    }
}