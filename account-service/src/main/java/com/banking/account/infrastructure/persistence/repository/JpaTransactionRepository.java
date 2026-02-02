package com.banking.account.infrastructure.persistence.repository;

import com.banking.account.domain.model.TransactionType;
import com.banking.account.infrastructure.persistence.entity.TransactionJpaEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Repository
public interface JpaTransactionRepository extends JpaRepository<TransactionJpaEntity, UUID> {

    List<TransactionJpaEntity> findByAccountIdOrderByCreatedAtDesc(UUID accountId);

    Page<TransactionJpaEntity> findByAccountIdOrderByCreatedAtDesc(UUID accountId, Pageable pageable);

    List<TransactionJpaEntity> findByAccountIdAndCreatedAtBetweenOrderByCreatedAtDesc(UUID accountId, Instant startDate, Instant endDate);

    List<TransactionJpaEntity> findByAccountIdAndTypeAndCreatedAtBetweenOrderByCreatedAtDesc(UUID accountId, TransactionType type, Instant startDate, Instant endDate);

    @Query("SELECT COALESCE(SUM(t.amount), 0) FROM TransactionJpaEntity t " +
            "WHERE t.accountId = :accountId " +
            "AND t.type = :type " +
            "AND t.createdAt BETWEEN :startDate AND :endDate")
    BigDecimal sumAmountByAccountIdAndTypeAndCreatedAtBetween(
            @Param("accountId") UUID accountId,
            @Param("type") TransactionType type,
            @Param("startDate") Instant startDate,
            @Param("endDate") Instant endDate
    );

    void deleteByAccountId(UUID accountId);

}