package com.banking.contracts.events.account;

import lombok.*;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class TransactionPerformedEventV1 implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private UUID transactionId;
    private UUID accountId;
    private String accountNumber;
    private UUID customerId;
    private String transactionType;
    private BigDecimal amount;
    private BigDecimal balanceBefore;
    private BigDecimal balanceAfter;
    private String reference;
    private LocalDateTime occurredAt;

}