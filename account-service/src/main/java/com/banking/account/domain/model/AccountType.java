package com.banking.account.domain.model;

public enum AccountType {
    SAVINGS,
    CHECKING;

    public boolean isSavings() {
        return this.equals(SAVINGS);
    }

    public boolean isChecking() {
        return this.equals(CHECKING);
    }

}