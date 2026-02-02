package com.banking.customer.domain.event;

import lombok.Builder;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
public record CustomerUpdatedEvent(UUID customerId, String customerIdValue, String name, String lastName,
                                   String address, String phone, LocalDateTime occurredAt) {

}