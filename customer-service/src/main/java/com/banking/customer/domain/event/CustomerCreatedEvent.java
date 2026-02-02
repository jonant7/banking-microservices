package com.banking.customer.domain.event;

import com.banking.customer.domain.model.CustomerStatus;
import com.banking.customer.domain.model.Gender;
import lombok.Builder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Builder
public record CustomerCreatedEvent(UUID customerId, String customerIdValue, String name, String lastName, Gender gender,
                                   LocalDate birthDate, String identification, String address, String phone,
                                   CustomerStatus status, LocalDateTime occurredAt) {

}