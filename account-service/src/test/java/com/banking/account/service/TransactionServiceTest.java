package com.banking.account.service;

import com.banking.account.application.dto.TransactionRequest;
import com.banking.account.application.dto.TransactionResponse;
import com.banking.account.application.mapper.AccountResponseMapper;
import com.banking.account.application.port.out.DomainEventPublisher;
import com.banking.account.application.service.TransactionService;
import com.banking.account.domain.exception.AccountNotFoundException;
import com.banking.account.domain.exception.InvalidTransactionException;
import com.banking.account.domain.model.Account;
import com.banking.account.domain.model.Transaction;
import com.banking.account.domain.model.TransactionType;
import com.banking.account.domain.repository.AccountRepository;
import com.banking.account.domain.repository.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.banking.account.fixtures.mothers.AccountMother.activeAccount;
import static com.banking.account.fixtures.mothers.TransactionMother.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private AccountResponseMapper mapper;

    @Mock
    private DomainEventPublisher eventPublisher;

    @InjectMocks
    private TransactionService transactionService;

    private UUID accountId;
    private Account account;

    @BeforeEach
    void setUp() {
        accountId = UUID.randomUUID();
        account = activeAccount();
    }

    @Nested
    class ExecuteTransaction {

        @Test
        void shouldExecuteDepositSuccessfully() {
            TransactionRequest request = TransactionRequest.builder()
                    .accountId(accountId)
                    .type(TransactionType.DEPOSIT)
                    .amount(new BigDecimal("500.00"))
                    .reference("DEP-001")
                    .build();

            when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));
            when(accountRepository.save(any(Account.class))).thenReturn(account);

            Transaction savedTransaction = depositTransactionForAccount(accountId, new BigDecimal("500.00"));
            when(transactionRepository.save(any(Transaction.class))).thenReturn(savedTransaction);

            TransactionResponse expectedResponse = new TransactionResponse();
            when(mapper.toResponse(savedTransaction)).thenReturn(expectedResponse);

            TransactionResponse result = transactionService.executeTransaction(request);

            assertThat(result).isEqualTo(expectedResponse);
            verify(accountRepository).save(any(Account.class));
            verify(transactionRepository).save(any(Transaction.class));
            verify(eventPublisher).publish(anyList());
        }

        @Test
        void shouldExecuteWithdrawalSuccessfully() {
            TransactionRequest request = TransactionRequest.builder()
                    .accountId(accountId)
                    .type(TransactionType.WITHDRAWAL)
                    .amount(new BigDecimal("200.00"))
                    .reference("WITH-001")
                    .build();

            when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));
            when(accountRepository.save(any(Account.class))).thenReturn(account);

            Transaction savedTransaction = withdrawalTransactionForAccount(accountId, new BigDecimal("200.00"));
            when(transactionRepository.save(any(Transaction.class))).thenReturn(savedTransaction);

            TransactionResponse expectedResponse = new TransactionResponse();
            when(mapper.toResponse(savedTransaction)).thenReturn(expectedResponse);

            TransactionResponse result = transactionService.executeTransaction(request);

            assertThat(result).isEqualTo(expectedResponse);
            verify(accountRepository).save(any(Account.class));
            verify(transactionRepository).save(any(Transaction.class));
            verify(eventPublisher).publish(anyList());
        }

        @Test
        void shouldThrowExceptionWhenAccountNotFound() {
            TransactionRequest request = TransactionRequest.builder()
                    .accountId(accountId)
                    .type(TransactionType.DEPOSIT)
                    .amount(new BigDecimal("500.00"))
                    .build();

            when(accountRepository.findById(accountId)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> transactionService.executeTransaction(request))
                    .isInstanceOf(AccountNotFoundException.class);

            verify(transactionRepository, never()).save(any());
        }

        @Test
        void shouldThrowExceptionWhenAccountIdIsNull() {
            TransactionRequest request = TransactionRequest.builder()
                    .accountId(null)
                    .type(TransactionType.DEPOSIT)
                    .amount(new BigDecimal("500.00"))
                    .build();

            assertThatThrownBy(() -> transactionService.executeTransaction(request))
                    .isInstanceOf(InvalidTransactionException.class);

            verify(accountRepository, never()).findById(any());
        }

        @Test
        void shouldThrowExceptionWhenTransactionTypeIsNull() {
            TransactionRequest request = TransactionRequest.builder()
                    .accountId(accountId)
                    .type(null)
                    .amount(new BigDecimal("500.00"))
                    .build();

            assertThatThrownBy(() -> transactionService.executeTransaction(request))
                    .isInstanceOf(InvalidTransactionException.class);

            verify(accountRepository, never()).findById(any());
        }

        @Test
        void shouldThrowExceptionWhenAmountIsNull() {
            TransactionRequest request = TransactionRequest.builder()
                    .accountId(accountId)
                    .type(TransactionType.DEPOSIT)
                    .amount(null)
                    .build();

            assertThatThrownBy(() -> transactionService.executeTransaction(request))
                    .isInstanceOf(InvalidTransactionException.class);

            verify(accountRepository, never()).findById(any());
        }

        @Test
        void shouldThrowExceptionWhenAmountIsZero() {
            TransactionRequest request = TransactionRequest.builder()
                    .accountId(accountId)
                    .type(TransactionType.DEPOSIT)
                    .amount(BigDecimal.ZERO)
                    .build();

            assertThatThrownBy(() -> transactionService.executeTransaction(request))
                    .isInstanceOf(InvalidTransactionException.class);

            verify(accountRepository, never()).findById(any());
        }

        @Test
        void shouldThrowExceptionWhenAmountIsNegative() {
            TransactionRequest request = TransactionRequest.builder()
                    .accountId(accountId)
                    .type(TransactionType.DEPOSIT)
                    .amount(new BigDecimal("-100.00"))
                    .build();

            assertThatThrownBy(() -> transactionService.executeTransaction(request))
                    .isInstanceOf(InvalidTransactionException.class);

            verify(accountRepository, never()).findById(any());
        }

        @Test
        void shouldThrowExceptionWhenAmountHasMoreThanTwoDecimals() {
            TransactionRequest request = TransactionRequest.builder()
                    .accountId(accountId)
                    .type(TransactionType.DEPOSIT)
                    .amount(new BigDecimal("100.123"))
                    .build();

            assertThatThrownBy(() -> transactionService.executeTransaction(request))
                    .isInstanceOf(InvalidTransactionException.class);

            verify(accountRepository, never()).findById(any());
        }

        @Test
        void shouldClearDomainEventsAfterPublishing() {
            TransactionRequest request = TransactionRequest.builder()
                    .accountId(accountId)
                    .type(TransactionType.DEPOSIT)
                    .amount(new BigDecimal("500.00"))
                    .reference("DEP-001")
                    .build();

            Account accountToReturn = activeAccount();
            when(accountRepository.findById(accountId)).thenReturn(Optional.of(accountToReturn));
            when(accountRepository.save(any(Account.class))).thenReturn(accountToReturn);

            Transaction savedTransaction = depositTransactionForAccount(accountId, new BigDecimal("500.00"));
            when(transactionRepository.save(any(Transaction.class))).thenReturn(savedTransaction);
            when(mapper.toResponse(savedTransaction)).thenReturn(new TransactionResponse());

            transactionService.executeTransaction(request);

            verify(eventPublisher).publish(anyList());
            assertThat(accountToReturn.getDomainEvents()).isEmpty();
        }
    }

    @Nested
    class GetTransactions {

        @Test
        void shouldGetTransactionById() {
            UUID transactionId = UUID.randomUUID();
            Transaction transaction = transactionForAccount(accountId);
            TransactionResponse expectedResponse = new TransactionResponse();

            when(transactionRepository.findById(transactionId)).thenReturn(Optional.of(transaction));
            when(mapper.toResponse(transaction)).thenReturn(expectedResponse);

            TransactionResponse result = transactionService.getTransactionById(transactionId);

            assertThat(result).isEqualTo(expectedResponse);
            verify(transactionRepository).findById(transactionId);
        }

        @Test
        void shouldThrowExceptionWhenTransactionNotFound() {
            UUID transactionId = UUID.randomUUID();
            when(transactionRepository.findById(transactionId)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> transactionService.getTransactionById(transactionId))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Transaction not found");
        }

        @Test
        void shouldGetTransactionsByAccountId() {
            Pageable pageable = PageRequest.of(0, 10);
            List<Transaction> transactions = List.of(
                    depositTransactionForAccount(accountId, new BigDecimal("100.00")),
                    withdrawalTransactionForAccount(accountId, new BigDecimal("50.00"))
            );
            Page<Transaction> transactionPage = new PageImpl<>(transactions, pageable, transactions.size());

            when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));
            when(transactionRepository.findByAccountId(accountId, pageable)).thenReturn(transactionPage);
            when(mapper.toResponse(any(Transaction.class))).thenReturn(new TransactionResponse());

            Page<TransactionResponse> result = transactionService.getTransactionsByAccountId(accountId, pageable);

            assertThat(result.getContent()).hasSize(2);
            verify(transactionRepository).findByAccountId(accountId, pageable);
        }

        @Test
        void shouldGetTransactionsByDateRange() {
            LocalDateTime startDate = LocalDateTime.now().minusDays(7);
            LocalDateTime endDate = LocalDateTime.now();
            List<Transaction> transactions = List.of(
                    depositTransaction(),
                    withdrawalTransaction()
            );

            when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));
            when(transactionRepository.findByAccountIdAndDateRange(accountId, startDate, endDate))
                    .thenReturn(transactions);
            when(mapper.toResponse(any(Transaction.class))).thenReturn(new TransactionResponse());

            List<TransactionResponse> result = transactionService.getTransactionsByDateRange(
                    accountId, startDate, endDate
            );

            assertThat(result).hasSize(2);
            verify(transactionRepository).findByAccountIdAndDateRange(accountId, startDate, endDate);
        }

        @Test
        void shouldThrowExceptionWhenStartDateIsAfterEndDate() {
            LocalDateTime startDate = LocalDateTime.now();
            LocalDateTime endDate = LocalDateTime.now().minusDays(7);

            when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));

            assertThatThrownBy(() -> transactionService.getTransactionsByDateRange(
                    accountId, startDate, endDate
            ))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Start date must be before end date");
        }

        @Test
        void shouldGetTransactionsByAccountIdAndType() {
            LocalDateTime startDate = LocalDateTime.now().minusDays(7);
            LocalDateTime endDate = LocalDateTime.now();
            List<Transaction> transactions = List.of(depositTransaction(), largeDepositTransaction());

            when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));
            when(transactionRepository.findByAccountIdAndTypeAndDateRange(
                    accountId, TransactionType.DEPOSIT, startDate, endDate
            )).thenReturn(transactions);
            when(mapper.toResponse(any(Transaction.class))).thenReturn(new TransactionResponse());

            List<TransactionResponse> result = transactionService.getTransactionsByAccountIdAndType(
                    accountId, TransactionType.DEPOSIT, startDate, endDate
            );

            assertThat(result).hasSize(2);
            verify(transactionRepository).findByAccountIdAndTypeAndDateRange(
                    accountId, TransactionType.DEPOSIT, startDate, endDate
            );
        }

        @Test
        void shouldGetTotalAmountByType() {
            LocalDateTime startDate = LocalDateTime.now().minusDays(7);
            LocalDateTime endDate = LocalDateTime.now();
            BigDecimal expectedTotal = new BigDecimal("1500.00");

            when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));
            when(transactionRepository.sumAmountByAccountIdAndTypeAndDateRange(
                    accountId, TransactionType.DEPOSIT, startDate, endDate
            )).thenReturn(expectedTotal);

            BigDecimal result = transactionService.getTotalAmountByType(
                    accountId, TransactionType.DEPOSIT, startDate, endDate
            );

            assertThat(result).isEqualByComparingTo(expectedTotal);
            verify(transactionRepository).sumAmountByAccountIdAndTypeAndDateRange(
                    accountId, TransactionType.DEPOSIT, startDate, endDate
            );
        }
    }

}