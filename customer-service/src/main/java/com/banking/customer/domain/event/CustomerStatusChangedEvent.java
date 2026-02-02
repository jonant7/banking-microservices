package com.banking.customer.domain.event;

import com.banking.customer.domain.model.CustomerStatus;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
public record CustomerStatusChangedEvent(UUID customerId, String customerIdValue, CustomerStatus newStatus,
                                         String reason,
                                         LocalDateTime occurredAt) {

}