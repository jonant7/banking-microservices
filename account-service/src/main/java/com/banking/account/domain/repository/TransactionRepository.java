package com.banking.account.domain.repository;

import com.banking.account.domain.model.Transaction;
import com.banking.account.domain.model.TransactionType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TransactionRepository {

    Transaction save(Transaction transaction);

    Optional<Transaction> findById(UUID id);

    List<Transaction> findByAccountId(UUID accountId);

    Page<Transaction> findByAccountId(UUID accountId, Pageable pageable);

    List<Transaction> findByAccountIdAndDateRange(UUID accountId, LocalDateTime startDate, LocalDateTime endDate);

    List<Transaction> findByAccountIdAndTypeAndDateRange(UUID accountId, TransactionType type, LocalDateTime startDate, LocalDateTime endDate);

    BigDecimal sumAmountByAccountIdAndTypeAndDateRange(UUID accountId, TransactionType type, LocalDateTime startDate, LocalDateTime endDate);

    void deleteAll();

    void deleteByAccountId(UUID accountId);

    long count();

}