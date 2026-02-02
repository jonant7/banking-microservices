package com.banking.account.application.service;

import com.banking.account.application.dto.TransactionRequest;
import com.banking.account.application.dto.TransactionResponse;
import com.banking.account.application.mapper.AccountResponseMapper;
import com.banking.account.application.port.in.TransactionUseCase;
import com.banking.account.application.port.out.DomainEventPublisher;
import com.banking.account.domain.exception.AccountNotFoundException;
import com.banking.account.domain.exception.InvalidTransactionException;
import com.banking.account.domain.model.Account;
import com.banking.account.domain.model.Money;
import com.banking.account.domain.model.Transaction;
import com.banking.account.domain.model.TransactionType;
import com.banking.account.domain.repository.AccountRepository;
import com.banking.account.domain.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class TransactionService implements TransactionUseCase {

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;
    private final AccountResponseMapper mapper;
    private final DomainEventPublisher eventPublisher;

    @Override
    @Transactional
    public TransactionResponse executeTransaction(TransactionRequest request) {
        log.info("Executing transaction: {} for account: {}",
                request.getType(), request.getAccountId());

        validateTransactionRequest(request);

        Account account = accountRepository.findById(request.getAccountId())
                .orElseThrow(() -> AccountNotFoundException.withId(request.getAccountId()));

        Money amount = Money.of(request.getAmount());
        Money balanceBefore = account.getCurrentBalance();

        if (request.getType() == TransactionType.DEPOSIT) {
            account.deposit(amount);
            log.info("Deposit of {} executed successfully on account: {}",
                    amount, account.getAccountNumberValue());
        } else if (request.getType() == TransactionType.WITHDRAWAL) {
            account.withdraw(amount);
            log.info("Withdrawal of {} executed successfully on account: {}",
                    amount, account.getAccountNumberValue());
        } else {
            throw InvalidTransactionException.withReason("Unsupported transaction type: " + request.getType());
        }

        Money balanceAfter = account.getCurrentBalance();

        Transaction transaction = Transaction.create(
                request.getType(),
                amount,
                balanceBefore,
                balanceAfter,
                request.getReference(),
                account.getId()
        );

        Account savedAccount = accountRepository.save(account);
        Transaction savedTransaction = transactionRepository.save(transaction);

        savedAccount.registerTransactionPerformed(
                savedTransaction.getId(),
                savedTransaction.getType(),
                savedTransaction.getAmount(),
                savedTransaction.getBalanceBefore(),
                savedTransaction.getBalanceAfter(),
                savedTransaction.getReference()
        );

        eventPublisher.publish(savedAccount.getDomainEvents());
        savedAccount.clearDomainEvents();

        log.debug("Transaction {} completed and events published", savedTransaction.getId());

        return mapper.toResponse(savedTransaction);
    }

    @Override
    @Transactional(readOnly = true)
    public TransactionResponse getTransactionById(UUID id) {
        log.debug("Fetching transaction by ID: {}", id);

        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Transaction not found with ID: " + id));

        return mapper.toResponse(transaction);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<TransactionResponse> getTransactionsByAccountId(UUID accountId, Pageable pageable) {
        log.debug("Fetching transactions for account: {}", accountId);

        accountRepository.findById(accountId)
                .orElseThrow(() -> AccountNotFoundException.withId(accountId));

        Page<Transaction> transactions = transactionRepository.findByAccountId(accountId, pageable);

        return transactions.map(mapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TransactionResponse> getTransactionsByDateRange(
            UUID accountId,
            LocalDateTime startDate,
            LocalDateTime endDate
    ) {
        log.debug("Fetching transactions for account: {} between {} and {}",
                accountId, startDate, endDate);

        accountRepository.findById(accountId)
                .orElseThrow(() -> AccountNotFoundException.withId(accountId));

        if (startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("Start date must be before end date");
        }

        List<Transaction> transactions = transactionRepository.findByAccountIdAndDateRange(
                accountId,
                startDate,
                endDate
        );

        return transactions.stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<TransactionResponse> getTransactionsByAccountIdAndType(
            UUID accountId,
            TransactionType type,
            LocalDateTime startDate,
            LocalDateTime endDate
    ) {
        log.debug("Fetching {} transactions for account: {} between {} and {}",
                type, accountId, startDate, endDate);

        accountRepository.findById(accountId)
                .orElseThrow(() -> AccountNotFoundException.withId(accountId));

        if (startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("Start date must be before end date");
        }

        List<Transaction> transactions = transactionRepository.findByAccountIdAndTypeAndDateRange(
                accountId,
                type,
                startDate,
                endDate
        );

        return transactions.stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal getTotalAmountByType(
            UUID accountId,
            TransactionType type,
            LocalDateTime startDate,
            LocalDateTime endDate
    ) {
        log.debug("Calculating total amount for {} transactions on account: {}",
                type, accountId);

        accountRepository.findById(accountId)
                .orElseThrow(() -> AccountNotFoundException.withId(accountId));

        if (startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("Start date must be before end date");
        }

        return transactionRepository.sumAmountByAccountIdAndTypeAndDateRange(
                accountId,
                type,
                startDate,
                endDate
        );
    }

    private void validateTransactionRequest(TransactionRequest request) {
        if (Objects.isNull(request.getAccountId())) {
            throw InvalidTransactionException.withReason("Account ID is required");
        }
        if (Objects.isNull(request.getType())) {
            throw InvalidTransactionException.withReason("Transaction type is required");
        }
        if (Objects.isNull(request.getAmount()) || request.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw InvalidTransactionException.withReason("Transaction amount must be positive");
        }
        if (request.getAmount().scale() > 2) {
            throw InvalidTransactionException.withReason(
                    "Transaction amount cannot have more than 2 decimal places"
            );
        }
    }

}