package com.banking.account.infrastructure.persistence.inmemory;

import com.banking.account.application.dto.AccountFilter;
import com.banking.account.domain.model.Account;
import com.banking.account.domain.model.AccountNumber;
import com.banking.account.domain.model.AccountStatus;
import com.banking.account.domain.repository.AccountRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Repository
public class InMemoryAccountRepository implements AccountRepository {

    private final Map<UUID, Account> store = new ConcurrentHashMap<>();

    @Override
    public Account save(Account account) {
        Objects.requireNonNull(account, "Account must not be null");
        store.put(account.getId(), account);
        return account;
    }

    @Override
    public Optional<Account> findById(UUID id) {
        Objects.requireNonNull(id, "Account id must not be null");
        return Optional.ofNullable(store.get(id));
    }

    @Override
    public Optional<Account> findByAccountNumber(AccountNumber accountNumber) {
        Objects.requireNonNull(accountNumber, "Account number must not be null");
        return store.values().stream()
                .filter(a -> accountNumber.equals(a.getAccountNumber()))
                .findFirst();
    }

    @Override
    public List<Account> findByCustomerId(UUID customerId) {
        Objects.requireNonNull(customerId, "Customer id must not be null");
        return store.values().stream()
                .filter(a -> customerId.equals(a.getCustomerId()))
                .sorted(Comparator.comparing(Account::getCreatedAt).reversed())
                .collect(Collectors.toList());
    }

    @Override
    public Page<Account> findByCustomerId(UUID customerId, Pageable pageable) {
        Objects.requireNonNull(customerId, "Customer id must not be null");

        List<Account> accounts = findByCustomerId(customerId);
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), accounts.size());

        List<Account> pageContent = start >= accounts.size()
                ? Collections.emptyList()
                : accounts.subList(start, end);

        return new PageImpl<>(pageContent, pageable, accounts.size());
    }

    @Override
    public Page<Account> findAll(AccountFilter filter, Pageable pageable) {
        List<Account> accounts = store.values().stream()
                .filter(a -> applyFilter(a, filter))
                .sorted(Comparator.comparing(Account::getCreatedAt).reversed())
                .collect(Collectors.toList());

        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), accounts.size());

        List<Account> pageContent = start >= accounts.size()
                ? Collections.emptyList()
                : accounts.subList(start, end);

        return new PageImpl<>(pageContent, pageable, accounts.size());
    }

    @Override
    public boolean existsByAccountNumber(AccountNumber accountNumber) {
        Objects.requireNonNull(accountNumber, "Account number must not be null");
        return store.values().stream()
                .anyMatch(a -> accountNumber.equals(a.getAccountNumber()));
    }

    @Override
    public List<Account> findByCustomerIdAndStatus(UUID customerId, AccountStatus status) {
        Objects.requireNonNull(customerId, "Customer id must not be null");
        Objects.requireNonNull(status, "Status must not be null");
        return store.values().stream()
                .filter(a -> customerId.equals(a.getCustomerId()))
                .filter(a -> status.equals(a.getStatus()))
                .sorted(Comparator.comparing(Account::getCreatedAt).reversed())
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Account> findByIdAndCustomerId(UUID id, UUID customerId) {
        Objects.requireNonNull(id, "Account id must not be null");
        Objects.requireNonNull(customerId, "Customer id must not be null");
        return store.values().stream()
                .filter(a -> id.equals(a.getId()))
                .filter(a -> customerId.equals(a.getCustomerId()))
                .findFirst();
    }

    @Override
    public void deleteById(UUID id) {
        Objects.requireNonNull(id, "Account id must not be null");
        store.remove(id);
    }

    @Override
    public void deleteAll() {
        store.clear();
    }

    @Override
    public long count() {
        return store.size();
    }

    private boolean applyFilter(Account account, AccountFilter filter) {
        if (Objects.isNull(filter)) {
            return true;
        }

        if (Objects.nonNull(filter.getAccountNumber())
                && !filter.getAccountNumber().equals(account.getAccountNumberValue())) {
            return false;
        }

        if (Objects.nonNull(filter.getAccountType())
                && !filter.getAccountType().equals(account.getAccountType())) {
            return false;
        }

        if (Objects.nonNull(filter.getStatus())
                && !filter.getStatus().equals(account.getStatus())) {
            return false;
        }

        if (Objects.nonNull(filter.getCustomerId())
                && !filter.getCustomerId().equals(account.getCustomerId())) {
            return false;
        }

        if (Objects.nonNull(filter.getMinBalance())
                && account.getCurrentBalance().value().compareTo(filter.getMinBalance()) < 0) {
            return false;
        }

        if (Objects.nonNull(filter.getMaxBalance())
                && account.getCurrentBalance().value().compareTo(filter.getMaxBalance()) > 0) {
            return false;
        }

        return true;
    }

}