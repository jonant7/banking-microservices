package com.banking.account.fixtures.mothers;

import com.banking.account.application.dto.AccountRequest;
import com.banking.account.domain.model.AccountType;

import java.math.BigDecimal;
import java.util.UUID;

public class AccountRequestMother {

    public static AccountRequest defaultRequest(UUID customerId) {
        return AccountRequest.builder()
                .accountNumber("1234567890")
                .accountType(AccountType.SAVINGS)
                .initialBalance(new BigDecimal("1000.00"))
                .customerId(customerId)
                .build();
    }

    public static AccountRequest withAccountNumber(UUID customerId, String accountNumber) {
        return AccountRequest.builder()
                .accountNumber(accountNumber)
                .accountType(AccountType.SAVINGS)
                .initialBalance(new BigDecimal("1000.00"))
                .customerId(customerId)
                .build();
    }

    public static AccountRequest withBalance(UUID customerId, BigDecimal balance) {
        return AccountRequest.builder()
                .accountNumber("1234567890")
                .accountType(AccountType.SAVINGS)
                .initialBalance(balance)
                .customerId(customerId)
                .build();
    }

}