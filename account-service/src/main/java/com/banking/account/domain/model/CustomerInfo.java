package com.banking.account.domain.model;

import java.util.UUID;

public record CustomerInfo(
        UUID customerId,
        String fullName,
        CustomerStatus status
) {
    public boolean isActive() {
        return status.isActive();
    }
}