package com.banking.account.infrastructure.persistence.inmemory;

import com.banking.account.domain.model.Money;
import com.banking.account.domain.model.Transaction;
import com.banking.account.domain.model.TransactionType;
import com.banking.account.domain.repository.TransactionRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Repository
public class InMemoryTransactionRepository implements TransactionRepository {

    private final Map<UUID, Transaction> store = new ConcurrentHashMap<>();

    @Override
    public Transaction save(Transaction transaction) {
        Objects.requireNonNull(transaction, "Transaction must not be null");

        if (Objects.isNull(transaction.getId())) {
            Transaction newTransaction = Transaction.reconstitute(
                    UUID.randomUUID(),
                    transaction.getType(),
                    transaction.getAmount(),
                    transaction.getBalanceBefore(),
                    transaction.getBalanceAfter(),
                    transaction.getReference(),
                    transaction.getAccountId(),
                    transaction.getCreatedAt()
            );
            store.put(newTransaction.getId(), newTransaction);
            return newTransaction;
        }

        store.put(transaction.getId(), transaction);
        return transaction;
    }

    @Override
    public Optional<Transaction> findById(UUID id) {
        Objects.requireNonNull(id, "Transaction id must not be null");
        return Optional.ofNullable(store.get(id));
    }

    @Override
    public List<Transaction> findByAccountId(UUID accountId) {
        Objects.requireNonNull(accountId, "Account id must not be null");
        return store.values().stream()
                .filter(t -> accountId.equals(t.getAccountId()))
                .sorted(Comparator.comparing(Transaction::getCreatedAt).reversed())
                .collect(Collectors.toList());
    }

    @Override
    public Page<Transaction> findByAccountId(UUID accountId, Pageable pageable) {
        Objects.requireNonNull(accountId, "Account id must not be null");

        List<Transaction> transactions = findByAccountId(accountId);
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), transactions.size());

        List<Transaction> pageContent = start >= transactions.size()
                ? Collections.emptyList()
                : transactions.subList(start, end);

        return new PageImpl<>(pageContent, pageable, transactions.size());
    }

    @Override
    public List<Transaction> findByAccountIdAndDateRange(
            UUID accountId,
            LocalDateTime startDate,
            LocalDateTime endDate
    ) {
        Objects.requireNonNull(accountId, "Account id must not be null");
        Objects.requireNonNull(startDate, "Start date must not be null");
        Objects.requireNonNull(endDate, "End date must not be null");

        return store.values().stream()
                .filter(t -> accountId.equals(t.getAccountId()))
                .filter(t -> {
                    LocalDateTime transactionDate = LocalDateTime.ofInstant(
                            t.getCreatedAt(),
                            ZoneId.systemDefault()
                    );
                    return !transactionDate.isBefore(startDate) && !transactionDate.isAfter(endDate);
                })
                .sorted(Comparator.comparing(Transaction::getCreatedAt).reversed())
                .collect(Collectors.toList());
    }

    @Override
    public List<Transaction> findByAccountIdAndTypeAndDateRange(
            UUID accountId,
            TransactionType type,
            LocalDateTime startDate,
            LocalDateTime endDate
    ) {
        Objects.requireNonNull(accountId, "Account id must not be null");
        Objects.requireNonNull(type, "Transaction type must not be null");
        Objects.requireNonNull(startDate, "Start date must not be null");
        Objects.requireNonNull(endDate, "End date must not be null");

        return findByAccountIdAndDateRange(accountId, startDate, endDate).stream()
                .filter(t -> type.equals(t.getType()))
                .collect(Collectors.toList());
    }

    @Override
    public BigDecimal sumAmountByAccountIdAndTypeAndDateRange(
            UUID accountId,
            TransactionType type,
            LocalDateTime startDate,
            LocalDateTime endDate
    ) {
        Objects.requireNonNull(accountId, "Account id must not be null");
        Objects.requireNonNull(type, "Transaction type must not be null");
        Objects.requireNonNull(startDate, "Start date must not be null");
        Objects.requireNonNull(endDate, "End date must not be null");

        return findByAccountIdAndTypeAndDateRange(accountId, type, startDate, endDate).stream()
                .map(Transaction::getAmount)
                .map(Money::value)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Override
    public void deleteAll() {
        store.clear();
    }

    @Override
    public void deleteByAccountId(UUID accountId) {
        Objects.requireNonNull(accountId, "Account id must not be null");
        store.entrySet().removeIf(entry -> accountId.equals(entry.getValue().getAccountId()));
    }

    @Override
    public long count() {
        return store.size();
    }

}