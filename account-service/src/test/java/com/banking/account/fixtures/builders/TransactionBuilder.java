package com.banking.account.fixtures.builders;

import com.banking.account.domain.model.Money;
import com.banking.account.domain.model.Transaction;
import com.banking.account.domain.model.TransactionType;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public class TransactionBuilder {

    private UUID id = UUID.randomUUID();
    private TransactionType type = TransactionType.DEPOSIT;
    private Money amount = Money.of(new BigDecimal("100.00"));
    private Money balanceBefore = Money.of(new BigDecimal("1000.00"));
    private Money balanceAfter = Money.of(new BigDecimal("1100.00"));
    private String reference = "REF-001";
    private UUID accountId = UUID.randomUUID();
    private Instant createdAt = Instant.now();

    public static TransactionBuilder aTransaction() {
        return new TransactionBuilder();
    }

    public TransactionBuilder withId(UUID id) {
        this.id = id;
        return this;
    }

    public TransactionBuilder withType(TransactionType type) {
        this.type = type;
        return this;
    }

    public TransactionBuilder deposit() {
        this.type = TransactionType.DEPOSIT;
        return this;
    }

    public TransactionBuilder withdrawal() {
        this.type = TransactionType.WITHDRAWAL;
        return this;
    }

    public TransactionBuilder withAmount(BigDecimal amount) {
        this.amount = Money.of(amount);
        return this;
    }

    public TransactionBuilder withAmount(Money amount) {
        this.amount = amount;
        return this;
    }

    public TransactionBuilder withBalanceBefore(BigDecimal balance) {
        this.balanceBefore = Money.of(balance);
        return this;
    }

    public TransactionBuilder withBalanceBefore(Money balance) {
        this.balanceBefore = balance;
        return this;
    }

    public TransactionBuilder withBalanceAfter(BigDecimal balance) {
        this.balanceAfter = Money.of(balance);
        return this;
    }

    public TransactionBuilder withBalanceAfter(Money balance) {
        this.balanceAfter = balance;
        return this;
    }

    public TransactionBuilder withReference(String reference) {
        this.reference = reference;
        return this;
    }

    public TransactionBuilder withAccountId(UUID accountId) {
        this.accountId = accountId;
        return this;
    }

    public TransactionBuilder withCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
        return this;
    }

    public TransactionBuilder withConsistentBalances() {
        if (type == TransactionType.DEPOSIT) {
            this.balanceAfter = this.balanceBefore.add(this.amount);
        } else {
            this.balanceAfter = this.balanceBefore.subtract(this.amount);
        }
        return this;
    }

    public Transaction build() {
        return Transaction.reconstitute(
                id,
                type,
                amount,
                balanceBefore,
                balanceAfter,
                reference,
                accountId,
                createdAt
        );
    }

}