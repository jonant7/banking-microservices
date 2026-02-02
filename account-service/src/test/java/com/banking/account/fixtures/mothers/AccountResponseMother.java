package com.banking.account.fixtures.mothers;

import com.banking.account.application.dto.AccountResponse;
import com.banking.account.domain.model.AccountStatus;
import com.banking.account.domain.model.AccountType;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public class AccountResponseMother {

    public static AccountResponse defaultResponse(UUID accountId, UUID customerId) {
        return AccountResponse.builder()
                .id(accountId)
                .accountNumber("1234567890")
                .accountType(AccountType.SAVINGS)
                .initialBalance(new BigDecimal("1000.00"))
                .currentBalance(new BigDecimal("1000.00"))
                .status(AccountStatus.ACTIVE)
                .customerId(customerId)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();
    }

    public static AccountResponse activeResponse(UUID accountId, UUID customerId) {
        return defaultResponse(accountId, customerId);
    }

    public static AccountResponse inactiveResponse(UUID accountId, UUID customerId) {
        AccountResponse response = defaultResponse(accountId, customerId);
        response.setStatus(AccountStatus.INACTIVE);
        return response;
    }

    public static AccountResponse withBalance(UUID accountId, UUID customerId, BigDecimal balance) {
        return AccountResponse.builder()
                .id(accountId)
                .accountNumber("1234567890")
                .accountType(AccountType.SAVINGS)
                .initialBalance(new BigDecimal("1000.00"))
                .currentBalance(balance)
                .status(AccountStatus.ACTIVE)
                .customerId(customerId)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();
    }

}