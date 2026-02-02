package com.banking.account.fixtures.mothers;

import com.banking.account.domain.model.TransactionType;
import com.banking.account.presentation.dto.response.TransactionApiResponse;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public class TransactionApiResponseMother {

    public static TransactionApiResponse depositResponse(UUID transactionId, UUID accountId) {
        return TransactionApiResponse.builder()
                .id(transactionId)
                .type(TransactionType.DEPOSIT)
                .amount(new BigDecimal("500.00"))
                .balanceBefore(new BigDecimal("1000.00"))
                .balanceAfter(new BigDecimal("1500.00"))
                .reference("DEP-001")
                .accountId(accountId)
                .createdAt(Instant.now())
                .build();
    }

    public static TransactionApiResponse withdrawalResponse(UUID transactionId, UUID accountId) {
        return TransactionApiResponse.builder()
                .id(transactionId)
                .type(TransactionType.WITHDRAWAL)
                .amount(new BigDecimal("300.00"))
                .balanceBefore(new BigDecimal("1000.00"))
                .balanceAfter(new BigDecimal("700.00"))
                .reference("WITH-001")
                .accountId(accountId)
                .createdAt(Instant.now())
                .build();
    }

}