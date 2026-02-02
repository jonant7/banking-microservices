package com.banking.account.infrastructure.messaging.publisher.mapper;

import com.banking.account.domain.event.AccountCreatedEvent;
import com.banking.account.domain.event.TransactionPerformedEvent;
import com.banking.contracts.events.account.AccountCreatedEventV1;
import com.banking.contracts.events.account.TransactionPerformedEventV1;
import org.springframework.stereotype.Component;

@Component
public class AccountEventMapper {

    public AccountCreatedEventV1 toContract(AccountCreatedEvent event) {
        return AccountCreatedEventV1.builder()
                .accountId(event.accountId())
                .accountNumber(event.accountNumber())
                .customerId(event.customerId())
                .accountType(event.accountType().name())
                .initialBalance(event.initialBalance())
                .currentBalance(event.initialBalance())
                .status(event.status().name())
                .occurredAt(event.occurredAt())
                .build();
    }

    public TransactionPerformedEventV1 toContract(TransactionPerformedEvent event) {
        return TransactionPerformedEventV1.builder()
                .transactionId(event.transactionId())
                .accountId(event.accountId())
                .accountNumber(event.accountNumber())
                .customerId(event.customerId())
                .transactionType(event.type().name())
                .amount(event.amount())
                .balanceBefore(event.balanceBefore())
                .balanceAfter(event.balanceAfter())
                .reference(event.reference())
                .occurredAt(event.occurredAt())
                .build();
    }

}