package com.banking.account.domain.model;

import com.banking.account.domain.exception.AccountErrorCode;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SuperBuilder
public class Transaction {

    private UUID id;
    private TransactionType type;
    private Money amount;
    private Money balanceBefore;
    private Money balanceAfter;
    private String reference;
    private UUID accountId;
    private Instant createdAt;
    private Instant updatedAt;

    public static Transaction createDeposit(
            Money amount,
            Money balanceBefore,
            Money balanceAfter,
            UUID accountId,
            String reference
    ) {
        return create(
                TransactionType.DEPOSIT,
                amount,
                balanceBefore,
                balanceAfter,
                reference,
                accountId
        );
    }

    public static Transaction createWithdrawal(
            Money amount,
            Money balanceBefore,
            Money balanceAfter,
            UUID accountId,
            String reference
    ) {
        return create(
                TransactionType.WITHDRAWAL,
                amount,
                balanceBefore,
                balanceAfter,
                reference,
                accountId
        );
    }

    public static Transaction create(
            TransactionType type,
            Money amount,
            Money balanceBefore,
            Money balanceAfter,
            String reference,
            UUID accountId
    ) {
        UUID id = UUID.randomUUID();
        Instant now = Instant.now();

        Transaction transaction = Transaction.builder()
                .id(id)
                .type(type)
                .amount(amount)
                .balanceBefore(balanceBefore)
                .balanceAfter(balanceAfter)
                .reference(reference)
                .accountId(accountId)
                .createdAt(now)
                .updatedAt(now)
                .build();

        transaction.validate();
        return transaction;
    }

    public static Transaction reconstitute(
            UUID id,
            TransactionType type,
            Money amount,
            Money balanceBefore,
            Money balanceAfter,
            String reference,
            UUID accountId,
            Instant createdAt
    ) {
        return Transaction.builder()
                .id(id)
                .type(type)
                .amount(amount)
                .balanceBefore(balanceBefore)
                .balanceAfter(balanceAfter)
                .reference(reference)
                .accountId(accountId)
                .createdAt(createdAt)
                .updatedAt(createdAt)
                .build();
    }

    private void validate() {
        if (Objects.isNull(type)) {
            throw new IllegalArgumentException(AccountErrorCode.TRANSACTION_VALIDATION_TYPE_NULL.getCode());
        }
        if (Objects.isNull(amount)) {
            throw new IllegalArgumentException(AccountErrorCode.TRANSACTION_VALIDATION_AMOUNT_NULL.getCode());
        }
        if (Objects.isNull(balanceBefore)) {
            throw new IllegalArgumentException(AccountErrorCode.TRANSACTION_VALIDATION_BALANCE_BEFORE_NULL.getCode());
        }
        if (Objects.isNull(balanceAfter)) {
            throw new IllegalArgumentException(AccountErrorCode.TRANSACTION_VALIDATION_BALANCE_AFTER_NULL.getCode());
        }
        if (Objects.isNull(accountId)) {
            throw new IllegalArgumentException(AccountErrorCode.TRANSACTION_VALIDATION_ACCOUNT_ID_NULL.getCode());
        }
        if (amount.isNegativeOrZero()) {
            throw new IllegalArgumentException(AccountErrorCode.TRANSACTION_VALIDATION_AMOUNT_POSITIVE.getCode());
        }

        validateBalanceConsistency();
    }

    private void validateBalanceConsistency() {
        Money expectedBalance = type.isDeposit()
                ? balanceBefore.add(amount)
                : balanceBefore.subtract(amount);

        if (!expectedBalance.equals(balanceAfter)) {
            throw new IllegalArgumentException(AccountErrorCode.TRANSACTION_VALIDATION_BALANCE_INCONSISTENT.getCode());
        }
    }

}