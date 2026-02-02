package com.banking.account.fixtures.mothers;

import com.banking.account.application.dto.AccountResponse;
import com.banking.account.application.dto.AccountStatementReport;
import com.banking.account.application.dto.TransactionResponse;
import com.banking.account.domain.model.AccountStatus;
import com.banking.account.domain.model.AccountType;
import com.banking.account.domain.model.TransactionType;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class ReportMother {

    public static AccountStatementReport defaultReport(UUID customerId, UUID accountId, LocalDateTime startDate, LocalDateTime endDate) {
        AccountResponse accountResponse = AccountResponse.builder()
                .id(accountId)
                .accountNumber("1234567890")
                .accountType(AccountType.SAVINGS)
                .initialBalance(new BigDecimal("1000.00"))
                .currentBalance(new BigDecimal("1500.00"))
                .status(AccountStatus.ACTIVE)
                .customerId(customerId)
                .createdAt(Instant.now())
                .build();

        TransactionResponse transactionResponse = TransactionResponse.builder()
                .id(UUID.randomUUID())
                .type(TransactionType.DEPOSIT)
                .amount(new BigDecimal("500.00"))
                .balanceBefore(new BigDecimal("1000.00"))
                .balanceAfter(new BigDecimal("1500.00"))
                .accountId(accountId)
                .createdAt(Instant.now())
                .build();

        return AccountStatementReport.builder()
                .customerId(customerId)
                .startDate(startDate)
                .endDate(endDate)
                .accounts(List.of(
                        AccountStatementReport.AccountWithTransactions.builder()
                                .account(accountResponse)
                                .transactions(List.of(transactionResponse))
                                .build()
                ))
                .build();
    }

    public static AccountStatementReport emptyReport(UUID customerId, LocalDateTime startDate, LocalDateTime endDate) {
        return AccountStatementReport.builder()
                .customerId(customerId)
                .startDate(startDate)
                .endDate(endDate)
                .accounts(List.of())
                .build();
    }

    public static AccountStatementReport multiAccountReport(UUID customerId, UUID accountId1, UUID accountId2, LocalDateTime startDate, LocalDateTime endDate) {
        AccountResponse account1 = AccountResponse.builder()
                .id(accountId1)
                .accountNumber("1234567890")
                .accountType(AccountType.SAVINGS)
                .currentBalance(new BigDecimal("1000.00"))
                .status(AccountStatus.ACTIVE)
                .customerId(customerId)
                .build();

        AccountResponse account2 = AccountResponse.builder()
                .id(accountId2)
                .accountNumber("0987654321")
                .accountType(AccountType.CHECKING)
                .currentBalance(new BigDecimal("2000.00"))
                .status(AccountStatus.ACTIVE)
                .customerId(customerId)
                .build();

        return AccountStatementReport.builder()
                .customerId(customerId)
                .startDate(startDate)
                .endDate(endDate)
                .accounts(List.of(
                        AccountStatementReport.AccountWithTransactions.builder()
                                .account(account1)
                                .transactions(List.of())
                                .build(),
                        AccountStatementReport.AccountWithTransactions.builder()
                                .account(account2)
                                .transactions(List.of())
                                .build()
                ))
                .build();
    }

}