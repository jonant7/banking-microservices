package com.banking.account.service;

import com.banking.account.application.dto.AccountResponse;
import com.banking.account.application.dto.AccountStatementReport;
import com.banking.account.application.dto.TransactionResponse;
import com.banking.account.application.mapper.AccountResponseMapper;
import com.banking.account.application.port.out.CustomerEventListener;
import com.banking.account.application.service.AccountStatementService;
import com.banking.account.domain.model.Account;
import com.banking.account.domain.model.Transaction;
import com.banking.account.domain.repository.AccountRepository;
import com.banking.account.domain.repository.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static com.banking.account.fixtures.mothers.AccountMother.activeAccount;
import static com.banking.account.fixtures.mothers.AccountMother.savingsAccount;
import static com.banking.account.fixtures.mothers.TransactionMother.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AccountStatementServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private AccountResponseMapper mapper;

    @Mock
    private CustomerEventListener customerEventListener;

    @InjectMocks
    private AccountStatementService accountStatementService;

    private UUID customerId;
    private LocalDateTime startDate;
    private LocalDateTime endDate;

    @BeforeEach
    void setUp() {
        customerId = UUID.randomUUID();
        startDate = LocalDateTime.now().minusDays(30);
        endDate = LocalDateTime.now();
    }

    @Nested
    class GenerateStatement {

        @Test
        void shouldGenerateStatementSuccessfully() {
            List<Account> accounts = List.of(activeAccount(), savingsAccount());

            when(customerEventListener.customerExists(customerId)).thenReturn(true);
            when(customerEventListener.isCustomerActive(customerId)).thenReturn(true);
            when(accountRepository.findByCustomerId(customerId)).thenReturn(accounts);
            when(transactionRepository.findByAccountIdAndDateRange(any(), any(), any()))
                    .thenReturn(List.of());
            when(mapper.toResponse(any(Account.class))).thenReturn(new AccountResponse());

            AccountStatementReport result = accountStatementService.generateStatement(
                    customerId, startDate, endDate
            );

            assertThat(result).isNotNull();
            assertThat(result.getCustomerId()).isEqualTo(customerId);
            assertThat(result.getStartDate()).isEqualTo(startDate);
            assertThat(result.getEndDate()).isEqualTo(endDate);
            assertThat(result.getAccounts()).hasSize(2);

            verify(accountRepository).findByCustomerId(customerId);
        }

        @Test
        void shouldThrowExceptionWhenStartDateIsAfterEndDate() {
            LocalDateTime invalidStartDate = LocalDateTime.now();
            LocalDateTime invalidEndDate = LocalDateTime.now().minusDays(7);

            assertThatThrownBy(() -> accountStatementService.generateStatement(
                    customerId, invalidStartDate, invalidEndDate
            ))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Start date must be before end date");

            verify(accountRepository, never()).findByCustomerId(any());
        }

        @Test
        void shouldThrowExceptionWhenCustomerDoesNotExist() {
            when(customerEventListener.customerExists(customerId)).thenReturn(false);

            assertThatThrownBy(() -> accountStatementService.generateStatement(
                    customerId, startDate, endDate
            ))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("does not exist");

            verify(accountRepository, never()).findByCustomerId(any());
        }

        @Test
        void shouldThrowExceptionWhenCustomerIsNotActive() {
            when(customerEventListener.customerExists(customerId)).thenReturn(true);
            when(customerEventListener.isCustomerActive(customerId)).thenReturn(false);

            assertThatThrownBy(() -> accountStatementService.generateStatement(
                    customerId, startDate, endDate
            ))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("is not active");

            verify(accountRepository, never()).findByCustomerId(any());
        }

        @Test
        void shouldReturnEmptyAccountsWhenCustomerHasNoAccounts() {
            when(customerEventListener.customerExists(customerId)).thenReturn(true);
            when(customerEventListener.isCustomerActive(customerId)).thenReturn(true);
            when(accountRepository.findByCustomerId(customerId)).thenReturn(List.of());

            AccountStatementReport result = accountStatementService.generateStatement(
                    customerId, startDate, endDate
            );

            assertThat(result.getAccounts()).isEmpty();
            verify(accountRepository).findByCustomerId(customerId);
        }

        @Test
        void shouldIncludeTransactionsInStatement() {
            Account account = activeAccount();
            List<Account> accounts = List.of(account);
            List<Transaction> transactions = List.of(
                    depositTransaction(),
                    withdrawalTransaction()
            );

            when(customerEventListener.customerExists(customerId)).thenReturn(true);
            when(customerEventListener.isCustomerActive(customerId)).thenReturn(true);
            when(accountRepository.findByCustomerId(customerId)).thenReturn(accounts);
            when(transactionRepository.findByAccountIdAndDateRange(account.getId(), startDate, endDate))
                    .thenReturn(transactions);
            when(mapper.toResponse(any(Transaction.class))).thenReturn(new TransactionResponse());
            when(mapper.toResponse(any(Account.class))).thenReturn(new AccountResponse());

            AccountStatementReport result = accountStatementService.generateStatement(
                    customerId, startDate, endDate
            );

            assertThat(result.getAccounts()).hasSize(1);
            assertThat(result.getAccounts().get(0).getTransactions()).hasSize(2);
            verify(transactionRepository).findByAccountIdAndDateRange(account.getId(), startDate, endDate);
        }

        @Test
        void shouldHandleMultipleAccountsWithDifferentTransactions() {
            Account account1 = activeAccount();
            Account account2 = savingsAccount();
            List<Account> accounts = List.of(account1, account2);

            List<Transaction> transactions1 = List.of(depositTransaction());
            List<Transaction> transactions2 = List.of(
                    depositTransactionForAccount(account2.getId(), new BigDecimal("200.00")),
                    withdrawalTransactionForAccount(account2.getId(), new BigDecimal("50.00"))
            );

            when(customerEventListener.customerExists(customerId)).thenReturn(true);
            when(customerEventListener.isCustomerActive(customerId)).thenReturn(true);
            when(accountRepository.findByCustomerId(customerId)).thenReturn(accounts);
            when(transactionRepository.findByAccountIdAndDateRange(account1.getId(), startDate, endDate))
                    .thenReturn(transactions1);
            when(transactionRepository.findByAccountIdAndDateRange(account2.getId(), startDate, endDate))
                    .thenReturn(transactions2);
            when(mapper.toResponse(any(Transaction.class))).thenReturn(new TransactionResponse());
            when(mapper.toResponse(any(Account.class))).thenReturn(new AccountResponse());

            AccountStatementReport result = accountStatementService.generateStatement(
                    customerId, startDate, endDate
            );

            assertThat(result.getAccounts()).hasSize(2);
            assertThat(result.getAccounts().get(0).getTransactions()).hasSize(1);
            assertThat(result.getAccounts().get(1).getTransactions()).hasSize(2);
            verify(transactionRepository, times(2)).findByAccountIdAndDateRange(any(), eq(startDate), eq(endDate));
        }

        @Test
        void shouldHandleAccountWithNoTransactions() {
            Account account = activeAccount();
            List<Account> accounts = List.of(account);

            when(customerEventListener.customerExists(customerId)).thenReturn(true);
            when(customerEventListener.isCustomerActive(customerId)).thenReturn(true);
            when(accountRepository.findByCustomerId(customerId)).thenReturn(accounts);
            when(transactionRepository.findByAccountIdAndDateRange(account.getId(), startDate, endDate))
                    .thenReturn(List.of());
            when(mapper.toResponse(any(Account.class))).thenReturn(new AccountResponse());

            AccountStatementReport result = accountStatementService.generateStatement(
                    customerId, startDate, endDate
            );

            assertThat(result.getAccounts()).hasSize(1);
            assertThat(result.getAccounts().get(0).getTransactions()).isEmpty();
            verify(transactionRepository).findByAccountIdAndDateRange(account.getId(), startDate, endDate);
        }

        @Test
        void shouldGenerateStatementForSpecificDateRange() {
            LocalDateTime specificStart = LocalDateTime.of(2024, 1, 1, 0, 0);
            LocalDateTime specificEnd = LocalDateTime.of(2024, 1, 31, 23, 59);

            Account account = activeAccount();
            List<Account> accounts = List.of(account);

            when(customerEventListener.customerExists(customerId)).thenReturn(true);
            when(customerEventListener.isCustomerActive(customerId)).thenReturn(true);
            when(accountRepository.findByCustomerId(customerId)).thenReturn(accounts);
            when(transactionRepository.findByAccountIdAndDateRange(account.getId(), specificStart, specificEnd))
                    .thenReturn(List.of());
            when(mapper.toResponse(any(Account.class))).thenReturn(new AccountResponse());

            AccountStatementReport result = accountStatementService.generateStatement(
                    customerId, specificStart, specificEnd
            );

            assertThat(result.getStartDate()).isEqualTo(specificStart);
            assertThat(result.getEndDate()).isEqualTo(specificEnd);
            verify(transactionRepository).findByAccountIdAndDateRange(account.getId(), specificStart, specificEnd);
        }
    }
}