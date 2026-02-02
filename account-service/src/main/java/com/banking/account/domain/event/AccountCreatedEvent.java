package com.banking.account.domain.event;

import com.banking.account.domain.model.AccountStatus;
import com.banking.account.domain.model.AccountType;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Builder
public record AccountCreatedEvent(UUID accountId, String accountNumber, UUID customerId, AccountType accountType,
                                  BigDecimal initialBalance, AccountStatus status, LocalDateTime occurredAt) {

}