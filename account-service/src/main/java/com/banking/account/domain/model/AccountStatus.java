package com.banking.account.domain.model;

public enum AccountStatus {
    ACTIVE,
    INACTIVE,
    SUSPENDED,
    CLOSED;

    public boolean isActive() {
        return this.equals(ACTIVE);
    }

    public boolean isInactive() {
        return this.equals(INACTIVE);
    }

    public boolean isSuspended() {
        return this.equals(SUSPENDED);
    }

    public boolean isClosed() {
        return this.equals(CLOSED);
    }

    public boolean canTransitionTo(AccountStatus newStatus) {
        if (isClosed()) {
            return false;
        }

        return this != newStatus;
    }

    public boolean allowsTransactions() {
        return isActive();
    }

}