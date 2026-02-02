package com.banking.account.fixtures.mothers;

import com.banking.account.domain.model.AccountStatus;
import com.banking.account.domain.model.AccountType;
import com.banking.account.presentation.dto.response.AccountApiResponse;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public class AccountApiResponseMother {

    public static AccountApiResponse defaultResponse(UUID accountId, UUID customerId) {
        return AccountApiResponse.builder()
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

    public static AccountApiResponse activeResponse(UUID accountId, UUID customerId) {
        return defaultResponse(accountId, customerId);
    }

    public static AccountApiResponse inactiveResponse(UUID accountId, UUID customerId) {
        return AccountApiResponse.builder()
                .id(accountId)
                .accountNumber("1234567890")
                .accountType(AccountType.SAVINGS)
                .initialBalance(new BigDecimal("1000.00"))
                .currentBalance(new BigDecimal("1000.00"))
                .status(AccountStatus.INACTIVE)
                .customerId(customerId)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();
    }

}