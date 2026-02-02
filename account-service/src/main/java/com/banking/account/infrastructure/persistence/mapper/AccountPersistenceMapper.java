package com.banking.account.infrastructure.persistence.mapper;

import com.banking.account.domain.model.Account;
import com.banking.account.domain.model.AccountNumber;
import com.banking.account.domain.model.Money;
import com.banking.account.infrastructure.persistence.entity.AccountJpaEntity;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class AccountPersistenceMapper {

    public Account toDomain(AccountJpaEntity entity) {
        if (Objects.isNull(entity)) {
            return null;
        }

        return Account.reconstitute(
                entity.getId(),
                AccountNumber.of(entity.getNumber()),
                entity.getType(),
                Money.of(entity.getInitialBalance()),
                Money.of(entity.getCurrentBalance()),
                entity.getStatus(),
                entity.getCustomerId(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }

    public AccountJpaEntity toEntity(Account domain) {
        if (Objects.isNull(domain)) {
            return null;
        }

        AccountJpaEntity entity = new AccountJpaEntity();

        entity.setId(domain.getId());
        entity.setNumber(domain.getAccountNumberValue());
        entity.setType(domain.getAccountType());
        entity.setInitialBalance(domain.getInitialBalance().value());
        entity.setCurrentBalance(domain.getCurrentBalance().value());
        entity.setStatus(domain.getStatus());
        entity.setCustomerId(domain.getCustomerId());
        entity.setCreatedAt(domain.getCreatedAt());
        entity.setUpdatedAt(domain.getUpdatedAt());

        return entity;
    }

    public void updateEntityFromDomain(Account domain, AccountJpaEntity entity) {
        if (Objects.isNull(domain) || Objects.isNull(entity)) {
            return;
        }

        entity.setNumber(domain.getAccountNumberValue());
        entity.setType(domain.getAccountType());
        entity.setInitialBalance(domain.getInitialBalance().value());
        entity.setCurrentBalance(domain.getCurrentBalance().value());
        entity.setStatus(domain.getStatus());
        entity.setCustomerId(domain.getCustomerId());
        entity.setUpdatedAt(domain.getUpdatedAt());
    }

}