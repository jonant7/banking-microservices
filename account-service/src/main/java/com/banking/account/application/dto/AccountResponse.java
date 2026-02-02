package com.banking.account.application.dto;

import com.banking.account.domain.model.AccountStatus;
import com.banking.account.domain.model.AccountType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountResponse {

    private UUID id;
    private String accountNumber;
    private AccountType accountType;
    private BigDecimal initialBalance;
    private BigDecimal currentBalance;
    private AccountStatus status;
    private UUID customerId;
    private String customerName;
    private Instant createdAt;
    private Instant updatedAt;

}