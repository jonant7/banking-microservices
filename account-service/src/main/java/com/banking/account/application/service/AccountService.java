package com.banking.account.application.service;

import com.banking.account.application.dto.AccountFilter;
import com.banking.account.application.dto.AccountRequest;
import com.banking.account.application.dto.AccountResponse;
import com.banking.account.application.mapper.AccountResponseMapper;
import com.banking.account.application.port.in.CreateAccountUseCase;
import com.banking.account.application.port.in.GetAccountUseCase;
import com.banking.account.application.port.in.UpdateAccountUseCase;
import com.banking.account.application.port.out.CustomerEventListener;
import com.banking.account.application.port.out.DomainEventPublisher;
import com.banking.account.domain.exception.AccountNotFoundException;
import com.banking.account.domain.exception.InactiveCustomerException;
import com.banking.account.domain.model.Account;
import com.banking.account.domain.model.AccountNumber;
import com.banking.account.domain.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class AccountService implements
        CreateAccountUseCase,
        GetAccountUseCase,
        UpdateAccountUseCase {

    private final AccountRepository accountRepository;
    private final AccountResponseMapper mapper;
    private final DomainEventPublisher eventPublisher;
    private final CustomerEventListener customerEventListener;

    @Override
    @Transactional
    public AccountResponse createAccount(AccountRequest request) {
        log.info("Creating account with number: {}", request.getAccountNumber());

        if (request.getInitialBalance().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Initial balance cannot be negative");
        }

        validateCustomerActive(request.getCustomerId());

        AccountNumber accountNumber = AccountNumber.of(request.getAccountNumber());
        if (accountRepository.existsByAccountNumber(accountNumber)) {
            throw new IllegalArgumentException(
                    String.format("Account number %s already exists", request.getAccountNumber())
            );
        }

        log.debug("Customer validation passed for ID: {}", request.getCustomerId());

        Account account = Account.create(
                request.getAccountNumber(),
                request.getAccountType(),
                request.getInitialBalance(),
                request.getCustomerId()
        );

        Account savedAccount = accountRepository.save(account);
        log.info("Account created successfully with ID: {}", savedAccount.getId());

        eventPublisher.publish(savedAccount.getDomainEvents());
        savedAccount.clearDomainEvents();

        return mapper.toResponse(savedAccount);
    }

    @Override
    @Transactional(readOnly = true)
    public AccountResponse getAccountById(UUID id) {
        log.debug("Fetching account by ID: {}", id);

        Account account = accountRepository.findById(id)
                .orElseThrow(() -> AccountNotFoundException.withId(id));

        return mapper.toResponse(account);
    }

    @Override
    @Transactional(readOnly = true)
    public AccountResponse getAccountByAccountNumber(String accountNumber) {
        log.debug("Fetching account by account number: {}", accountNumber);

        AccountNumber number = AccountNumber.of(accountNumber);
        Account account = accountRepository.findByAccountNumber(number)
                .orElseThrow(() -> AccountNotFoundException.withAccountNumber(accountNumber));

        return mapper.toResponse(account);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AccountResponse> getAllAccounts(AccountFilter filter, Pageable pageable) {
        log.debug("Fetching accounts with filter: {}", filter);

        Page<Account> accounts = accountRepository.findAll(filter, pageable);

        return accounts.map(mapper::toResponse);
    }

    @Override
    @Transactional
    public AccountResponse activateAccount(UUID id) {
        log.info("Activating account with ID: {}", id);

        Account account = accountRepository.findById(id)
                .orElseThrow(() -> AccountNotFoundException.withId(id));

        validateCustomerActive(account.getCustomerId());

        account.activate();
        Account savedAccount = accountRepository.save(account);
        log.info("Account activated successfully: {}", id);

        return mapper.toResponse(savedAccount);
    }

    @Override
    @Transactional
    public AccountResponse deactivateAccount(UUID id) {
        log.info("Deactivating account with ID: {}", id);

        Account account = accountRepository.findById(id)
                .orElseThrow(() -> AccountNotFoundException.withId(id));

        account.deactivate();
        Account savedAccount = accountRepository.save(account);
        log.info("Account deactivated successfully: {}", id);

        return mapper.toResponse(savedAccount);
    }

    private void validateCustomerActive(UUID customerId) {
        if (!customerEventListener.customerExists(customerId)) {
            throw InactiveCustomerException.notFound(customerId);
        }

        if (!customerEventListener.isCustomerActive(customerId)) {
            throw InactiveCustomerException.inactive(customerId);
        }
    }

}