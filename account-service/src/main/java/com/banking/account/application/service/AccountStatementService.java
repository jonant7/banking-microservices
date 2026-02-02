package com.banking.account.application.service;

import com.banking.account.application.dto.AccountResponse;
import com.banking.account.application.dto.AccountStatementReport;
import com.banking.account.application.dto.TransactionResponse;
import com.banking.account.application.mapper.AccountResponseMapper;
import com.banking.account.application.port.in.GenerateAccountStatementUseCase;
import com.banking.account.application.port.out.CustomerEventListener;
import com.banking.account.domain.model.Account;
import com.banking.account.domain.model.Transaction;
import com.banking.account.domain.repository.AccountRepository;
import com.banking.account.domain.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AccountStatementService implements GenerateAccountStatementUseCase {

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;
    private final AccountResponseMapper mapper;
    private final CustomerEventListener customerEventListener;

    @Override
    @Transactional(readOnly = true)
    public AccountStatementReport generateStatement(
            UUID customerId,
            LocalDateTime startDate,
            LocalDateTime endDate
    ) {
        log.info("Generating account statement for customer: {} from {} to {}",
                customerId, startDate, endDate);

        if (startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("Start date must be before end date");
        }

        if (!customerEventListener.customerExists(customerId)) {
            throw new IllegalStateException(
                    String.format("Customer %s does not exist", customerId)
            );
        }

        if (!customerEventListener.isCustomerActive(customerId)) {
            throw new IllegalStateException(
                    String.format("Customer %s is not active", customerId)
            );
        }

        List<Account> accounts = accountRepository.findByCustomerId(customerId);

        if (accounts.isEmpty()) {
            log.warn("No accounts found for customer: {}", customerId);
        }

        List<AccountStatementReport.AccountWithTransactions> accountsWithTransactions = accounts.stream()
                .map(account -> buildAccountWithTransactions(account, startDate, endDate))
                .collect(Collectors.toList());

        return AccountStatementReport.builder()
                .customerId(customerId)
                .startDate(startDate)
                .endDate(endDate)
                .accounts(accountsWithTransactions)
                .build();
    }

    private AccountStatementReport.AccountWithTransactions buildAccountWithTransactions(
            Account account,
            LocalDateTime startDate,
            LocalDateTime endDate
    ) {
        AccountResponse accountResponse = mapper.toResponse(account);

        List<Transaction> transactions = transactionRepository.findByAccountIdAndDateRange(
                account.getId(),
                startDate,
                endDate
        );

        List<TransactionResponse> transactionResponses = transactions.stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());

        return AccountStatementReport.AccountWithTransactions.builder()
                .account(accountResponse)
                .transactions(transactionResponses)
                .build();
    }

}