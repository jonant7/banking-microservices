package com.banking.account.application.dto;

import com.banking.account.domain.model.AccountStatus;
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
public class AccountFilter {

    private String accountNumber;
    private AccountType accountType;
    private AccountStatus status;
    private UUID customerId;
    private BigDecimal minBalance;
    private BigDecimal maxBalance;

    private Integer page;
    private Integer size;
    private String sortBy;
    private String sortDirection;

}