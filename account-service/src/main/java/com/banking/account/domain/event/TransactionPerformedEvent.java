package com.banking.account.domain.event;

import com.banking.account.domain.model.TransactionType;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Builder
public record TransactionPerformedEvent(UUID transactionId, UUID accountId, String accountNumber, UUID customerId,
                                        TransactionType type, BigDecimal amount, BigDecimal balanceBefore,
                                        BigDecimal balanceAfter, String reference, LocalDateTime occurredAt) {

}