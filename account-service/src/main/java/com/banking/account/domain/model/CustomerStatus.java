package com.banking.account.domain.model;

public enum CustomerStatus {
    ACTIVE,
    INACTIVE;

    public boolean isActive() {
        return this.equals(ACTIVE);
    }

    public boolean isInactive() {
        return this.equals(INACTIVE);
    }

}