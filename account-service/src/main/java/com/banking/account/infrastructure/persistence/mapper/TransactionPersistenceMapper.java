package com.banking.account.infrastructure.persistence.mapper;

import com.banking.account.domain.model.Money;
import com.banking.account.domain.model.Transaction;
import com.banking.account.infrastructure.persistence.entity.TransactionJpaEntity;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class TransactionPersistenceMapper {

    public Transaction toDomain(TransactionJpaEntity entity) {
        if (Objects.isNull(entity)) {
            return null;
        }

        return Transaction.reconstitute(
                entity.getId(),
                entity.getType(),
                Money.of(entity.getAmount()),
                Money.of(entity.getBalanceBefore()),
                Money.of(entity.getBalanceAfter()),
                entity.getReference(),
                entity.getAccountId(),
                entity.getCreatedAt()
        );
    }

    public TransactionJpaEntity toEntity(Transaction domain) {
        if (Objects.isNull(domain)) {
            return null;
        }

        TransactionJpaEntity entity = new TransactionJpaEntity();

        entity.setId(domain.getId());
        mapTransactionData(domain, entity);
        entity.setCreatedAt(domain.getCreatedAt());
        entity.setUpdatedAt(domain.getUpdatedAt());

        return entity;
    }

    public void updateEntityFromDomain(Transaction domain, TransactionJpaEntity entity) {
        if (Objects.isNull(domain) || Objects.isNull(entity)) {
            return;
        }

        mapTransactionData(domain, entity);
    }

    private void mapTransactionData(Transaction domain, TransactionJpaEntity entity) {
        entity.setType(domain.getType());
        entity.setAmount(domain.getAmount().value());
        entity.setBalanceBefore(domain.getBalanceBefore().value());
        entity.setBalanceAfter(domain.getBalanceAfter().value());
        entity.setReference(domain.getReference());
        entity.setAccountId(domain.getAccountId());
    }

}