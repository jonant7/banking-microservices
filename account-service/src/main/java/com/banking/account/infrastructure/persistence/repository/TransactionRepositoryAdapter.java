package com.banking.account.infrastructure.persistence.repository;

import com.banking.account.domain.model.Transaction;
import com.banking.account.domain.model.TransactionType;
import com.banking.account.domain.repository.TransactionRepository;
import com.banking.account.infrastructure.persistence.entity.TransactionJpaEntity;
import com.banking.account.infrastructure.persistence.mapper.TransactionPersistenceMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Primary
public class TransactionRepositoryAdapter implements TransactionRepository {

    private final JpaTransactionRepository jpaRepository;
    private final TransactionPersistenceMapper mapper;

    @Override
    @Transactional
    public Transaction save(Transaction transaction) {
        TransactionJpaEntity entity;

        if (Objects.nonNull(transaction.getId())) {
            entity = jpaRepository.findById(transaction.getId())
                    .map(existing -> {
                        mapper.updateEntityFromDomain(transaction, existing);
                        return existing;
                    })
                    .orElseGet(() -> mapper.toEntity(transaction));
        } else {
            entity = mapper.toEntity(transaction);
        }

        jpaRepository.save(entity);

        return transaction;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Transaction> findById(UUID id) {
        return jpaRepository.findById(id)
                .map(mapper::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Transaction> findByAccountId(UUID accountId) {
        return jpaRepository.findByAccountIdOrderByCreatedAtDesc(accountId)
                .stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Transaction> findByAccountId(UUID accountId, Pageable pageable) {
        return jpaRepository.findByAccountIdOrderByCreatedAtDesc(accountId, pageable)
                .map(mapper::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Transaction> findByAccountIdAndDateRange(
            UUID accountId,
            LocalDateTime startDate,
            LocalDateTime endDate
    ) {
        Instant startInstant = startDate.atZone(ZoneId.systemDefault()).toInstant();
        Instant endInstant = endDate.atZone(ZoneId.systemDefault()).toInstant();

        return jpaRepository.findByAccountIdAndCreatedAtBetweenOrderByCreatedAtDesc(
                        accountId, startInstant, endInstant)
                .stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<Transaction> findByAccountIdAndTypeAndDateRange(
            UUID accountId,
            TransactionType type,
            LocalDateTime startDate,
            LocalDateTime endDate
    ) {
        Instant startInstant = startDate.atZone(ZoneId.systemDefault()).toInstant();
        Instant endInstant = endDate.atZone(ZoneId.systemDefault()).toInstant();

        return jpaRepository.findByAccountIdAndTypeAndCreatedAtBetweenOrderByCreatedAtDesc(
                        accountId, type, startInstant, endInstant)
                .stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal sumAmountByAccountIdAndTypeAndDateRange(
            UUID accountId,
            TransactionType type,
            LocalDateTime startDate,
            LocalDateTime endDate
    ) {
        Instant startInstant = startDate.atZone(ZoneId.systemDefault()).toInstant();
        Instant endInstant = endDate.atZone(ZoneId.systemDefault()).toInstant();

        BigDecimal sum = jpaRepository.sumAmountByAccountIdAndTypeAndCreatedAtBetween(
                accountId, type, startInstant, endInstant
        );
        return Objects.nonNull(sum) ? sum : BigDecimal.ZERO;
    }

    @Override
    @Transactional
    public void deleteAll() {
        jpaRepository.deleteAll();
    }

    @Override
    @Transactional
    public void deleteByAccountId(UUID accountId) {
        jpaRepository.deleteByAccountId(accountId);
    }

    @Override
    @Transactional(readOnly = true)
    public long count() {
        return jpaRepository.count();
    }

}