package com.banking.account.application.dto;

import com.banking.account.domain.model.AccountType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountRequest {

    private String accountNumber;
    private AccountType accountType;
    private BigDecimal initialBalance;
    private UUID customerId;

}