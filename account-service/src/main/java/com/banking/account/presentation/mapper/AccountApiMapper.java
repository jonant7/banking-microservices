package com.banking.account.presentation.mapper;

import com.banking.account.application.dto.*;
import com.banking.account.presentation.dto.request.CreateAccountApiRequest;
import com.banking.account.presentation.dto.request.TransactionApiRequest;
import com.banking.account.presentation.dto.response.AccountApiResponse;
import com.banking.account.presentation.dto.response.AccountStatementResponse;
import com.banking.account.presentation.dto.response.TransactionApiResponse;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class AccountApiMapper {

    public AccountRequest toAccountRequest(CreateAccountApiRequest apiRequest) {
        if (Objects.isNull(apiRequest)) {
            return null;
        }

        return AccountRequest.builder()
                .accountNumber(apiRequest.getAccountNumber())
                .accountType(apiRequest.getAccountType())
                .initialBalance(apiRequest.getInitialBalance())
                .customerId(apiRequest.getCustomerId())
                .build();
    }

    public TransactionRequest toTransactionRequest(
            UUID accountId,
            TransactionApiRequest apiRequest
    ) {
        if (Objects.isNull(apiRequest)) {
            return null;
        }

        return TransactionRequest.builder()
                .accountId(accountId)
                .type(apiRequest.getType())
                .amount(apiRequest.getAmount())
                .reference(apiRequest.getReference())
                .build();
    }

    public AccountApiResponse toApiResponse(AccountResponse response) {
        if (Objects.isNull(response)) {
            return null;
        }

        return AccountApiResponse.builder()
                .id(response.getId())
                .accountNumber(response.getAccountNumber())
                .accountType(response.getAccountType())
                .initialBalance(response.getInitialBalance())
                .currentBalance(response.getCurrentBalance())
                .status(response.getStatus())
                .customerId(response.getCustomerId())
                .customerName(response.getCustomerName())
                .createdAt(response.getCreatedAt())
                .updatedAt(response.getUpdatedAt())
                .build();
    }

    public TransactionApiResponse toApiResponse(TransactionResponse response) {
        if (Objects.isNull(response)) {
            return null;
        }

        return TransactionApiResponse.builder()
                .id(response.getId())
                .type(response.getType())
                .amount(response.getAmount())
                .balanceBefore(response.getBalanceBefore())
                .balanceAfter(response.getBalanceAfter())
                .reference(response.getReference())
                .accountId(response.getAccountId())
                .createdAt(response.getCreatedAt())
                .build();
    }

    public AccountStatementResponse toStatementResponse(
            AccountStatementReport report,
            String customerName
    ) {
        if (Objects.isNull(report)) {
            return null;
        }

        List<AccountStatementResponse.AccountWithTransactions> accounts = report.getAccounts().stream()
                .map(this::toAccountWithTransactions)
                .collect(Collectors.toList());

        return AccountStatementResponse.builder()
                .customerId(report.getCustomerId())
                .customerName(customerName)
                .reportGeneratedAt(LocalDateTime.now())
                .startDate(report.getStartDate())
                .endDate(report.getEndDate())
                .accounts(accounts)
                .build();
    }

    private AccountStatementResponse.AccountWithTransactions toAccountWithTransactions(
            AccountStatementReport.AccountWithTransactions source
    ) {
        AccountApiResponse account = toApiResponse(source.getAccount());

        List<TransactionApiResponse> transactions = source.getTransactions().stream()
                .map(this::toApiResponse)
                .collect(Collectors.toList());

        return AccountStatementResponse.AccountWithTransactions.builder()
                .account(account)
                .transactions(transactions)
                .build();
    }

}