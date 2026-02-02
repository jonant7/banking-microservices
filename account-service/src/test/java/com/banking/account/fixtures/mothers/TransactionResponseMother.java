package com.banking.account.fixtures.mothers;

import com.banking.account.application.dto.TransactionResponse;
import com.banking.account.domain.model.TransactionType;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public class TransactionResponseMother {

    public static TransactionResponse depositResponse(UUID transactionId, UUID accountId) {
        return TransactionResponse.builder()
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

    public static TransactionResponse withdrawalResponse(UUID transactionId, UUID accountId) {
        return TransactionResponse.builder()
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

    public static TransactionResponse withAmount(UUID transactionId, UUID accountId, BigDecimal amount) {
        return TransactionResponse.builder()
                .id(transactionId)
                .type(TransactionType.DEPOSIT)
                .amount(amount)
                .balanceBefore(new BigDecimal("1000.00"))
                .balanceAfter(new BigDecimal("1000.00").add(amount))
                .reference("TXN-001")
                .accountId(accountId)
                .createdAt(Instant.now())
                .build();
    }

}