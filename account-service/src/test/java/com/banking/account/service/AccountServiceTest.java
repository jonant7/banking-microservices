package com.banking.account.service;

import com.banking.account.application.dto.AccountFilter;
import com.banking.account.application.dto.AccountRequest;
import com.banking.account.application.dto.AccountResponse;
import com.banking.account.application.mapper.AccountResponseMapper;
import com.banking.account.application.port.out.CustomerEventListener;
import com.banking.account.application.port.out.DomainEventPublisher;
import com.banking.account.application.service.AccountService;
import com.banking.account.domain.exception.AccountNotFoundException;
import com.banking.account.domain.exception.InactiveCustomerException;
import com.banking.account.domain.model.Account;
import com.banking.account.domain.model.AccountNumber;
import com.banking.account.domain.model.AccountType;
import com.banking.account.domain.repository.AccountRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.banking.account.fixtures.mothers.AccountMother.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private AccountResponseMapper mapper;

    @Mock
    private DomainEventPublisher eventPublisher;

    @Mock
    private CustomerEventListener customerEventListener;

    @InjectMocks
    private AccountService accountService;

    private UUID customerId;
    private AccountRequest validRequest;

    @BeforeEach
    void setUp() {
        customerId = UUID.randomUUID();
        validRequest = AccountRequest.builder()
                .accountNumber("1234567890")
                .accountType(AccountType.SAVINGS)
                .initialBalance(new BigDecimal("1000.00"))
                .customerId(customerId)
                .build();
    }

    @Nested
    class CreateAccount {

        @Test
        void shouldCreateAccountSuccessfully() {
            when(customerEventListener.customerExists(customerId)).thenReturn(true);
            when(customerEventListener.isCustomerActive(customerId)).thenReturn(true);
            when(accountRepository.existsByAccountNumber(any(AccountNumber.class))).thenReturn(false);

            Account savedAccount = activeAccount();
            when(accountRepository.save(any(Account.class))).thenReturn(savedAccount);

            AccountResponse expectedResponse = new AccountResponse();
            when(mapper.toResponse(savedAccount)).thenReturn(expectedResponse);

            AccountResponse result = accountService.createAccount(validRequest);

            assertThat(result).isEqualTo(expectedResponse);
            verify(accountRepository).save(any(Account.class));
            verify(eventPublisher).publish(anyList());
        }

        @Test
        void shouldThrowExceptionWhenCustomerDoesNotExist() {
            when(customerEventListener.customerExists(customerId)).thenReturn(false);

            assertThatThrownBy(() -> accountService.createAccount(validRequest))
                    .isInstanceOf(InactiveCustomerException.class);

            verify(accountRepository, never()).save(any());
        }

        @Test
        void shouldThrowExceptionWhenCustomerIsInactive() {
            when(customerEventListener.customerExists(customerId)).thenReturn(true);
            when(customerEventListener.isCustomerActive(customerId)).thenReturn(false);

            assertThatThrownBy(() -> accountService.createAccount(validRequest))
                    .isInstanceOf(InactiveCustomerException.class);

            verify(accountRepository, never()).save(any());
        }

        @Test
        void shouldThrowExceptionWhenAccountNumberAlreadyExists() {
            when(customerEventListener.customerExists(customerId)).thenReturn(true);
            when(customerEventListener.isCustomerActive(customerId)).thenReturn(true);
            when(accountRepository.existsByAccountNumber(any(AccountNumber.class))).thenReturn(true);

            assertThatThrownBy(() -> accountService.createAccount(validRequest))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("already exists");

            verify(accountRepository, never()).save(any());
        }

        @Test
        void shouldThrowExceptionWhenInitialBalanceIsNegative() {
            validRequest.setInitialBalance(new BigDecimal("-100.00"));

            assertThatThrownBy(() -> accountService.createAccount(validRequest))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("cannot be negative");

            verify(accountRepository, never()).save(any());
        }

        @Test
        void shouldClearDomainEventsAfterPublishing() {
            when(customerEventListener.customerExists(customerId)).thenReturn(true);
            when(customerEventListener.isCustomerActive(customerId)).thenReturn(true);
            when(accountRepository.existsByAccountNumber(any(AccountNumber.class))).thenReturn(false);

            Account accountToReturn = activeAccount();
            when(accountRepository.save(any(Account.class))).thenReturn(accountToReturn);
            when(mapper.toResponse(accountToReturn)).thenReturn(new AccountResponse());

            accountService.createAccount(validRequest);

            verify(eventPublisher).publish(anyList());

            assertThat(accountToReturn.getDomainEvents()).isEmpty();
        }
    }

    @Nested
    class GetAccount {

        @Test
        void shouldGetAccountById() {
            UUID accountId = UUID.randomUUID();
            Account account = activeAccount();
            AccountResponse expectedResponse = new AccountResponse();

            when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));
            when(mapper.toResponse(account)).thenReturn(expectedResponse);

            AccountResponse result = accountService.getAccountById(accountId);

            assertThat(result).isEqualTo(expectedResponse);
            verify(accountRepository).findById(accountId);
        }

        @Test
        void shouldThrowExceptionWhenAccountNotFoundById() {
            UUID accountId = UUID.randomUUID();
            when(accountRepository.findById(accountId)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> accountService.getAccountById(accountId))
                    .isInstanceOf(AccountNotFoundException.class);
        }

        @Test
        void shouldGetAccountByAccountNumber() {
            String accountNumber = "1234567890";
            Account account = activeAccount();
            AccountResponse expectedResponse = new AccountResponse();

            when(accountRepository.findByAccountNumber(any(AccountNumber.class)))
                    .thenReturn(Optional.of(account));
            when(mapper.toResponse(account)).thenReturn(expectedResponse);

            AccountResponse result = accountService.getAccountByAccountNumber(accountNumber);

            assertThat(result).isEqualTo(expectedResponse);
            verify(accountRepository).findByAccountNumber(any(AccountNumber.class));
        }

        @Test
        void shouldThrowExceptionWhenAccountNotFoundByAccountNumber() {
            String accountNumber = "1234567890";
            when(accountRepository.findByAccountNumber(any(AccountNumber.class)))
                    .thenReturn(Optional.empty());

            assertThatThrownBy(() -> accountService.getAccountByAccountNumber(accountNumber))
                    .isInstanceOf(AccountNotFoundException.class);
        }

        @Test
        void shouldGetAllAccountsWithFilter() {
            AccountFilter filter = AccountFilter.builder()
                    .accountType(AccountType.SAVINGS)
                    .build();
            Pageable pageable = PageRequest.of(0, 10);

            List<Account> accounts = List.of(activeAccount(), savingsAccount());
            Page<Account> accountPage = new PageImpl<>(accounts, pageable, accounts.size());

            when(accountRepository.findAll(filter, pageable)).thenReturn(accountPage);
            when(mapper.toResponse(any(Account.class))).thenReturn(new AccountResponse());

            Page<AccountResponse> result = accountService.getAllAccounts(filter, pageable);

            assertThat(result.getContent()).hasSize(2);
            verify(accountRepository).findAll(filter, pageable);
        }
    }

    @Nested
    class UpdateAccountStatus {

        @Test
        void shouldActivateAccount() {
            UUID accountId = UUID.randomUUID();
            Account inactiveAccount = inactiveAccount();
            AccountResponse expectedResponse = new AccountResponse();

            when(accountRepository.findById(accountId)).thenReturn(Optional.of(inactiveAccount));
            when(customerEventListener.customerExists(any())).thenReturn(true);
            when(customerEventListener.isCustomerActive(any())).thenReturn(true);
            when(accountRepository.save(any(Account.class))).thenReturn(inactiveAccount);
            when(mapper.toResponse(any(Account.class))).thenReturn(expectedResponse);

            AccountResponse result = accountService.activateAccount(accountId);

            assertThat(result).isEqualTo(expectedResponse);
            verify(accountRepository).save(any(Account.class));
        }

        @Test
        void shouldThrowExceptionWhenActivatingNonExistentAccount() {
            UUID accountId = UUID.randomUUID();
            when(accountRepository.findById(accountId)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> accountService.activateAccount(accountId))
                    .isInstanceOf(AccountNotFoundException.class);

            verify(accountRepository, never()).save(any());
        }

        @Test
        void shouldDeactivateAccount() {
            UUID accountId = UUID.randomUUID();
            Account activeAccount = activeAccount();
            AccountResponse expectedResponse = new AccountResponse();

            when(accountRepository.findById(accountId)).thenReturn(Optional.of(activeAccount));
            when(accountRepository.save(any(Account.class))).thenReturn(activeAccount);
            when(mapper.toResponse(any(Account.class))).thenReturn(expectedResponse);

            AccountResponse result = accountService.deactivateAccount(accountId);

            assertThat(result).isEqualTo(expectedResponse);
            verify(accountRepository).save(any(Account.class));
        }

        @Test
        void shouldThrowExceptionWhenDeactivatingNonExistentAccount() {
            UUID accountId = UUID.randomUUID();
            when(accountRepository.findById(accountId)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> accountService.deactivateAccount(accountId))
                    .isInstanceOf(AccountNotFoundException.class);

            verify(accountRepository, never()).save(any());
        }
    }
}