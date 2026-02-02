package com.banking.account.fixtures.builders;

import com.banking.account.domain.model.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public class AccountBuilder {

    private UUID id = UUID.randomUUID();
    private AccountNumber accountNumber = AccountNumber.of("1234567890");
    private AccountType accountType = AccountType.SAVINGS;
    private Money initialBalance = Money.of(new BigDecimal("1000.00"));
    private Money currentBalance = Money.of(new BigDecimal("1000.00"));
    private AccountStatus status = AccountStatus.ACTIVE;
    private UUID customerId = UUID.randomUUID();
    private Instant createdAt = Instant.now();
    private Instant updatedAt = Instant.now();

    public static AccountBuilder anAccount() {
        return new AccountBuilder();
    }

    public AccountBuilder withId(UUID id) {
        this.id = id;
        return this;
    }

    public AccountBuilder withAccountNumber(String accountNumber) {
        this.accountNumber = AccountNumber.of(accountNumber);
        return this;
    }

    public AccountBuilder withAccountType(AccountType accountType) {
        this.accountType = accountType;
        return this;
    }

    public AccountBuilder savings() {
        this.accountType = AccountType.SAVINGS;
        return this;
    }

    public AccountBuilder checking() {
        this.accountType = AccountType.CHECKING;
        return this;
    }

    public AccountBuilder withInitialBalance(BigDecimal balance) {
        this.initialBalance = Money.of(balance);
        return this;
    }

    public AccountBuilder withCurrentBalance(BigDecimal balance) {
        this.currentBalance = Money.of(balance);
        return this;
    }

    public AccountBuilder withStatus(AccountStatus status) {
        this.status = status;
        return this;
    }

    public AccountBuilder active() {
        this.status = AccountStatus.ACTIVE;
        return this;
    }

    public AccountBuilder inactive() {
        this.status = AccountStatus.INACTIVE;
        return this;
    }

    public AccountBuilder suspended() {
        this.status = AccountStatus.SUSPENDED;
        return this;
    }

    public AccountBuilder closed() {
        this.status = AccountStatus.CLOSED;
        return this;
    }

    public AccountBuilder withCustomerId(UUID customerId) {
        this.customerId = customerId;
        return this;
    }

    public AccountBuilder withCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
        return this;
    }

    public AccountBuilder withUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
        return this;
    }

    public AccountBuilder withZeroBalance() {
        this.initialBalance = Money.zero();
        this.currentBalance = Money.zero();
        return this;
    }

    public AccountBuilder withNegativeBalance() {
        this.initialBalance = Money.of(new BigDecimal("-100.00"));
        this.currentBalance = Money.of(new BigDecimal("-100.00"));
        return this;
    }

    public Account build() {
        Account account = Account.reconstitute(
                id,
                accountNumber,
                accountType,
                initialBalance,
                currentBalance,
                status,
                customerId,
                createdAt,
                updatedAt
        );
        account.clearDomainEvents();
        return account;
    }

}