package com.banking.account.fixtures.mothers;

import com.banking.account.domain.model.Transaction;
import com.banking.account.fixtures.builders.TransactionBuilder;

import java.math.BigDecimal;
import java.util.UUID;

public class TransactionMother {

    public static Transaction defaultTransaction() {
        return TransactionBuilder.aTransaction()
                .withConsistentBalances()
                .build();
    }

    public static Transaction depositTransaction() {
        return TransactionBuilder.aTransaction()
                .deposit()
                .withAmount(new BigDecimal("500.00"))
                .withBalanceBefore(new BigDecimal("1000.00"))
                .withBalanceAfter(new BigDecimal("1500.00"))
                .build();
    }

    public static Transaction withdrawalTransaction() {
        return TransactionBuilder.aTransaction()
                .withdrawal()
                .withAmount(new BigDecimal("200.00"))
                .withBalanceBefore(new BigDecimal("1000.00"))
                .withBalanceAfter(new BigDecimal("800.00"))
                .build();
    }

    public static Transaction largeDepositTransaction() {
        return TransactionBuilder.aTransaction()
                .deposit()
                .withAmount(new BigDecimal("5000.00"))
                .withBalanceBefore(new BigDecimal("1000.00"))
                .withBalanceAfter(new BigDecimal("6000.00"))
                .build();
    }

    public static Transaction smallWithdrawalTransaction() {
        return TransactionBuilder.aTransaction()
                .withdrawal()
                .withAmount(new BigDecimal("50.00"))
                .withBalanceBefore(new BigDecimal("1000.00"))
                .withBalanceAfter(new BigDecimal("950.00"))
                .build();
    }

    public static Transaction transactionForAccount(UUID accountId) {
        return TransactionBuilder.aTransaction()
                .withAccountId(accountId)
                .withConsistentBalances()
                .build();
    }

    public static Transaction transactionWithReference(String reference) {
        return TransactionBuilder.aTransaction()
                .withReference(reference)
                .withConsistentBalances()
                .build();
    }

    public static Transaction depositTransactionForAccount(UUID accountId, BigDecimal amount) {
        return TransactionBuilder.aTransaction()
                .deposit()
                .withAccountId(accountId)
                .withAmount(amount)
                .withBalanceBefore(new BigDecimal("1000.00"))
                .withConsistentBalances()
                .build();
    }

    public static Transaction withdrawalTransactionForAccount(UUID accountId, BigDecimal amount) {
        return TransactionBuilder.aTransaction()
                .withdrawal()
                .withAccountId(accountId)
                .withAmount(amount)
                .withBalanceBefore(new BigDecimal("1000.00"))
                .withConsistentBalances()
                .build();
    }

}