package com.banking.account.fixtures.mothers;

import com.banking.account.domain.model.AccountStatus;
import com.banking.account.domain.model.AccountType;
import com.banking.account.domain.model.TransactionType;
import com.banking.account.presentation.dto.response.AccountApiResponse;
import com.banking.account.presentation.dto.response.AccountStatementResponse;
import com.banking.account.presentation.dto.response.TransactionApiResponse;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class StatementResponseMother {

    public static AccountStatementResponse defaultResponse(UUID customerId, String customerName, LocalDateTime startDate, LocalDateTime endDate, UUID accountId) {
        AccountApiResponse accountApi = AccountApiResponse.builder()
                .id(accountId)
                .accountNumber("1234567890")
                .accountType(AccountType.SAVINGS)
                .currentBalance(new BigDecimal("1500.00"))
                .status(AccountStatus.ACTIVE)
                .customerId(customerId)
                .build();

        TransactionApiResponse transactionApi = TransactionApiResponse.builder()
                .id(UUID.randomUUID())
                .type(TransactionType.DEPOSIT)
                .amount(new BigDecimal("500.00"))
                .balanceBefore(new BigDecimal("1000.00"))
                .balanceAfter(new BigDecimal("1500.00"))
                .accountId(accountId)
                .build();

        return AccountStatementResponse.builder()
                .customerId(customerId)
                .customerName(customerName)
                .reportGeneratedAt(LocalDateTime.now())
                .startDate(startDate)
                .endDate(endDate)
                .accounts(List.of(
                        AccountStatementResponse.AccountWithTransactions.builder()
                                .account(accountApi)
                                .transactions(List.of(transactionApi))
                                .build()
                ))
                .build();
    }

    public static AccountStatementResponse emptyResponse(UUID customerId, String customerName, LocalDateTime startDate, LocalDateTime endDate) {
        return AccountStatementResponse.builder()
                .customerId(customerId)
                .customerName(customerName)
                .reportGeneratedAt(LocalDateTime.now())
                .startDate(startDate)
                .endDate(endDate)
                .accounts(List.of())
                .build();
    }

    public static AccountStatementResponse multiAccountResponse(UUID customerId, String customerName, LocalDateTime startDate, LocalDateTime endDate, UUID accountId1, UUID accountId2) {
        AccountApiResponse account1 = AccountApiResponse.builder()
                .id(accountId1)
                .accountNumber("1234567890")
                .accountType(AccountType.SAVINGS)
                .currentBalance(new BigDecimal("1000.00"))
                .status(AccountStatus.ACTIVE)
                .customerId(customerId)
                .build();

        AccountApiResponse account2 = AccountApiResponse.builder()
                .id(accountId2)
                .accountNumber("0987654321")
                .accountType(AccountType.CHECKING)
                .currentBalance(new BigDecimal("2000.00"))
                .status(AccountStatus.ACTIVE)
                .customerId(customerId)
                .build();

        return AccountStatementResponse.builder()
                .customerId(customerId)
                .customerName(customerName)
                .reportGeneratedAt(LocalDateTime.now())
                .startDate(startDate)
                .endDate(endDate)
                .accounts(List.of(
                        AccountStatementResponse.AccountWithTransactions.builder()
                                .account(account1)
                                .transactions(List.of())
                                .build(),
                        AccountStatementResponse.AccountWithTransactions.builder()
                                .account(account2)
                                .transactions(List.of())
                                .build()
                ))
                .build();
    }

}