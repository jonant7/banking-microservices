package com.banking.account.domain.exception;

import java.math.BigDecimal;
import java.util.Map;

public class InsufficientBalanceException extends AccountDomainException {

    private InsufficientBalanceException(Map<String, Object> parameters) {
        super(AccountErrorCode.ACCOUNT_BUSINESS_INSUFFICIENT_BALANCE, parameters);
    }

    public static InsufficientBalanceException withDetails(
            String accountNumber,
            BigDecimal currentBalance,
            BigDecimal requiredAmount
    ) {
        return new InsufficientBalanceException(
                Map.of(
                        "accountNumber", accountNumber,
                        "currentBalance", currentBalance.toString(),
                        "requiredAmount", requiredAmount.toString()
                )
        );
    }

}