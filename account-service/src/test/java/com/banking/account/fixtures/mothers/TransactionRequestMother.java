package com.banking.account.fixtures.mothers;

import com.banking.account.application.dto.TransactionRequest;
import com.banking.account.domain.model.TransactionType;

import java.math.BigDecimal;
import java.util.UUID;

public class TransactionRequestMother {

    public static TransactionRequest deposit(UUID accountId) {
        return TransactionRequest.builder()
                .accountId(accountId)
                .type(TransactionType.DEPOSIT)
                .amount(new BigDecimal("500.00"))
                .reference("DEP-001")
                .build();
    }

    public static TransactionRequest withdrawal(UUID accountId) {
        return TransactionRequest.builder()
                .accountId(accountId)
                .type(TransactionType.WITHDRAWAL)
                .amount(new BigDecimal("10000.00"))
                .reference("WITH-001")
                .build();
    }

    public static TransactionRequest withAmount(UUID accountId, TransactionType type, BigDecimal amount) {
        return TransactionRequest.builder()
                .accountId(accountId)
                .type(type)
                .amount(amount)
                .reference("TXN-001")
                .build();
    }

}