package com.banking.account.domain.model;

public enum TransactionType {
    DEPOSIT,
    WITHDRAWAL;

    public boolean isDeposit() {
        return this.equals(DEPOSIT);
    }

    public boolean isWithdrawal() {
        return this.equals(WITHDRAWAL);
    }

}