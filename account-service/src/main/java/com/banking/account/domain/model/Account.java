package com.banking.account.domain.model;

import com.banking.account.domain.event.AccountCreatedEvent;
import com.banking.account.domain.event.TransactionPerformedEvent;
import com.banking.account.domain.exception.AccountErrorCode;
import com.banking.account.domain.exception.InactiveAccountException;
import com.banking.account.domain.exception.InsufficientBalanceException;
import com.banking.account.domain.exception.InvalidTransactionException;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SuperBuilder
public class Account extends AggregateRoot {

    private AccountNumber accountNumber;
    private AccountType accountType;
    private Money initialBalance;
    private Money currentBalance;
    private AccountStatus status;
    private UUID customerId;

    public static Account create(
            String accountNumber,
            AccountType accountType,
            BigDecimal initialBalance,
            UUID customerId
    ) {
        UUID id = UUID.randomUUID();

        Account account = Account.builder()
                .id(id)
                .accountNumber(AccountNumber.of(accountNumber))
                .accountType(accountType)
                .initialBalance(Money.of(initialBalance))
                .currentBalance(Money.of(initialBalance))
                .status(AccountStatus.ACTIVE)
                .customerId(customerId)
                .build();

        account.markAsCreated();
        account.validate();

        account.registerEvent(AccountCreatedEvent.builder()
                .accountId(id)
                .accountNumber(accountNumber)
                .customerId(customerId)
                .accountType(accountType)
                .initialBalance(initialBalance)
                .status(AccountStatus.ACTIVE)
                .occurredAt(LocalDateTime.now())
                .build());

        return account;
    }

    public static Account reconstitute(
            UUID id,
            AccountNumber accountNumber,
            AccountType accountType,
            Money initialBalance,
            Money currentBalance,
            AccountStatus status,
            UUID customerId,
            java.time.Instant createdAt,
            java.time.Instant updatedAt
    ) {
        return Account.builder()
                .id(id)
                .accountNumber(accountNumber)
                .accountType(accountType)
                .initialBalance(initialBalance)
                .currentBalance(currentBalance)
                .status(status)
                .customerId(customerId)
                .createdAt(createdAt)
                .updatedAt(updatedAt)
                .build();
    }

    public void deposit(Money amount) {
        ensureActive();
        validateTransactionAmount(amount);

        this.currentBalance = this.currentBalance.add(amount);
        markAsUpdated();
    }

    public void withdraw(Money amount) {
        ensureActive();
        validateTransactionAmount(amount);
        ensureSufficientBalance(amount);

        this.currentBalance = this.currentBalance.subtract(amount);
        markAsUpdated();
    }

    public void registerTransactionPerformed(
            UUID transactionId,
            TransactionType type,
            Money amount,
            Money balanceBefore,
            Money balanceAfter,
            String reference
    ) {
        registerEvent(TransactionPerformedEvent.builder()
                .transactionId(transactionId)
                .accountId(this.getId())
                .accountNumber(this.accountNumber.value())
                .customerId(this.customerId)
                .type(type)
                .amount(amount.value())
                .balanceBefore(balanceBefore.value())
                .balanceAfter(balanceAfter.value())
                .reference(reference)
                .occurredAt(LocalDateTime.now())
                .build());
    }

    public void activate() {
        if (!this.status.canTransitionTo(AccountStatus.ACTIVE)) {
            throw new IllegalArgumentException(
                    AccountErrorCode.ACCOUNT_BUSINESS_INVALID_STATUS_TRANSITION.getCode()
            );
        }
        this.status = AccountStatus.ACTIVE;
        markAsUpdated();
    }

    public void deactivate() {
        if (!this.status.canTransitionTo(AccountStatus.INACTIVE)) {
            throw new IllegalArgumentException(
                    AccountErrorCode.ACCOUNT_BUSINESS_INVALID_STATUS_TRANSITION.getCode()
            );
        }
        this.status = AccountStatus.INACTIVE;
        markAsUpdated();
    }

    public void suspend() {
        if (!this.status.canTransitionTo(AccountStatus.SUSPENDED)) {
            throw new IllegalArgumentException(
                    AccountErrorCode.ACCOUNT_BUSINESS_INVALID_STATUS_TRANSITION.getCode()
            );
        }
        this.status = AccountStatus.SUSPENDED;
        markAsUpdated();
    }

    public void close() {
        if (!this.status.canTransitionTo(AccountStatus.CLOSED)) {
            throw new IllegalArgumentException(
                    AccountErrorCode.ACCOUNT_BUSINESS_INVALID_STATUS_TRANSITION.getCode()
            );
        }
        if (this.currentBalance.isPositive()) {
            throw new IllegalArgumentException(
                    AccountErrorCode.ACCOUNT_BUSINESS_CLOSE_WITH_BALANCE.getCode()
            );
        }
        this.status = AccountStatus.CLOSED;
        markAsUpdated();
    }

    public boolean isActive() {
        return this.status.isActive();
    }

    public void ensureActive() {
        if (!isActive()) {
            throw InactiveAccountException.withAccountNumber(accountNumber.value());
        }
    }

    private void validate() {
        if (Objects.isNull(accountNumber)) {
            throw new IllegalArgumentException(AccountErrorCode.ACCOUNT_VALIDATION_NUMBER_NULL.getCode());
        }
        if (Objects.isNull(accountType)) {
            throw new IllegalArgumentException(AccountErrorCode.ACCOUNT_VALIDATION_TYPE_NULL.getCode());
        }
        if (Objects.isNull(initialBalance)) {
            throw new IllegalArgumentException(AccountErrorCode.ACCOUNT_VALIDATION_INITIAL_BALANCE_NULL.getCode());
        }
        if (Objects.isNull(currentBalance)) {
            throw new IllegalArgumentException(AccountErrorCode.ACCOUNT_VALIDATION_CURRENT_BALANCE_NULL.getCode());
        }
        if (Objects.isNull(customerId)) {
            throw new IllegalArgumentException(AccountErrorCode.ACCOUNT_VALIDATION_CUSTOMER_ID_NULL.getCode());
        }
        if (initialBalance.isNegative()) {
            throw new IllegalArgumentException(AccountErrorCode.ACCOUNT_VALIDATION_INITIAL_BALANCE_NEGATIVE.getCode());
        }
    }

    private void validateTransactionAmount(Money amount) {
        if (Objects.isNull(amount)) {
            throw InvalidTransactionException.withReason(
                    AccountErrorCode.TRANSACTION_VALIDATION_AMOUNT_NULL.getCode()
            );
        }
        if (amount.isNegativeOrZero()) {
            throw InvalidTransactionException.withReason(
                    AccountErrorCode.TRANSACTION_VALIDATION_AMOUNT_POSITIVE.getCode()
            );
        }
    }

    private void ensureSufficientBalance(Money amount) {
        if (this.currentBalance.isLessThan(amount)) {
            throw InsufficientBalanceException.withDetails(
                    accountNumber.value(),
                    this.currentBalance.value(),
                    amount.value()
            );
        }
    }

    public String getAccountNumberValue() {
        return Objects.nonNull(accountNumber) ? accountNumber.value() : null;
    }

}