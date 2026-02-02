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
public class AccountCreatedEventV1 implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private UUID accountId;
    private String accountNumber;
    private UUID customerId;
    private String accountType;
    private BigDecimal initialBalance;
    private BigDecimal currentBalance;
    private String status;
    private LocalDateTime occurredAt;

}