package com.banking.account.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountStatementReport {

    private UUID customerId;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private List<AccountWithTransactions> accounts;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AccountWithTransactions {
        private AccountResponse account;
        private List<TransactionResponse> transactions;
    }

}