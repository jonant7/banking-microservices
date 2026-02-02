package com.banking.account.infrastructure.persistence.repository;

import com.banking.account.application.dto.AccountFilter;
import com.banking.account.domain.model.Account;
import com.banking.account.domain.model.AccountNumber;
import com.banking.account.domain.model.AccountStatus;
import com.banking.account.domain.repository.AccountRepository;
import com.banking.account.infrastructure.persistence.entity.AccountJpaEntity;
import com.banking.account.infrastructure.persistence.mapper.AccountPersistenceMapper;
import com.banking.account.infrastructure.persistence.specification.AccountSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Primary
public class AccountRepositoryAdapter implements AccountRepository {

    private final JpaAccountRepository jpaRepository;
    private final AccountPersistenceMapper mapper;

    @Override
    @Transactional
    public Account save(Account account) {
        AccountJpaEntity entity;

        if (Objects.nonNull(account.getId())) {
            entity = jpaRepository.findById(account.getId())
                    .map(existing -> {
                        mapper.updateEntityFromDomain(account, existing);
                        return existing;
                    })
                    .orElseGet(() -> mapper.toEntity(account));
        } else {
            entity = mapper.toEntity(account);
        }

        jpaRepository.save(entity);

        return account;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Account> findById(UUID id) {
        return jpaRepository.findById(id)
                .map(mapper::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Account> findByAccountNumber(AccountNumber accountNumber) {
        return jpaRepository.findByNumber(accountNumber.value())
                .map(mapper::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Account> findByCustomerId(UUID customerId) {
        return jpaRepository.findByCustomerId(customerId)
                .stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Account> findByCustomerId(UUID customerId, Pageable pageable) {
        return jpaRepository.findByCustomerId(customerId, pageable)
                .map(mapper::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Account> findAll(AccountFilter filter, Pageable pageable) {
        if (Objects.isNull(filter)) {
            return jpaRepository.findAll(pageable)
                    .map(mapper::toDomain);
        }

        Specification<AccountJpaEntity> spec = AccountSpecification.withFilter(filter);

        return jpaRepository.findAll(spec, pageable)
                .map(mapper::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByAccountNumber(AccountNumber accountNumber) {
        return jpaRepository.existsByNumber(accountNumber.value());
    }

    @Override
    @Transactional(readOnly = true)
    public List<Account> findByCustomerIdAndStatus(UUID customerId, AccountStatus status) {
        return jpaRepository.findByCustomerIdAndStatus(customerId, status)
                .stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Account> findByIdAndCustomerId(UUID id, UUID customerId) {
        return jpaRepository.findByIdAndCustomerId(id, customerId)
                .map(mapper::toDomain);
    }

    @Override
    @Transactional
    public void deleteById(UUID id) {
        jpaRepository.deleteById(id);
    }

    @Override
    @Transactional
    public void deleteAll() {
        jpaRepository.deleteAll();
    }

    @Override
    @Transactional(readOnly = true)
    public long count() {
        return jpaRepository.count();
    }

}