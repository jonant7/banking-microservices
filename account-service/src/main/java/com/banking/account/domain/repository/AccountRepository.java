package com.banking.account.domain.repository;

import com.banking.account.application.dto.AccountFilter;
import com.banking.account.domain.model.Account;
import com.banking.account.domain.model.AccountNumber;
import com.banking.account.domain.model.AccountStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AccountRepository {

    Account save(Account account);

    Optional<Account> findById(UUID id);

    Optional<Account> findByAccountNumber(AccountNumber accountNumber);

    List<Account> findByCustomerId(UUID customerId);

    Page<Account> findByCustomerId(UUID customerId, Pageable pageable);

    Page<Account> findAll(AccountFilter filter, Pageable pageable);

    boolean existsByAccountNumber(AccountNumber accountNumber);

    List<Account> findByCustomerIdAndStatus(UUID customerId, AccountStatus status);

    Optional<Account> findByIdAndCustomerId(UUID id, UUID customerId);

    void deleteById(UUID id);

    void deleteAll();

    long count();
}