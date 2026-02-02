package com.banking.customer.infrastructure.messaging.mapper;

import com.banking.contracts.events.customer.CustomerCreatedEventV1;
import com.banking.contracts.events.customer.CustomerStatusChangedEventV1;
import com.banking.contracts.events.customer.CustomerUpdatedEventV1;
import com.banking.customer.domain.event.CustomerCreatedEvent;
import com.banking.customer.domain.event.CustomerStatusChangedEvent;
import com.banking.customer.domain.event.CustomerUpdatedEvent;
import org.springframework.stereotype.Component;

@Component
public class CustomerEventMapper {

    public CustomerCreatedEventV1 toContract(CustomerCreatedEvent event) {
        return CustomerCreatedEventV1.builder()
                .customerId(event.customerId())
                .customerIdValue(event.customerIdValue())
                .name(event.name())
                .lastName(event.lastName())
                .gender(event.gender().name())
                .birthDate(event.birthDate())
                .identification(event.identification())
                .address(event.address())
                .phone(event.phone())
                .status(event.status().name())
                .occurredAt(event.occurredAt())
                .build();
    }

    public CustomerUpdatedEventV1 toContract(CustomerUpdatedEvent event) {
        return CustomerUpdatedEventV1.builder()
                .customerId(event.customerId())
                .customerIdValue(event.customerIdValue())
                .name(event.name())
                .lastName(event.lastName())
                .address(event.address())
                .phone(event.phone())
                .occurredAt(event.occurredAt())
                .build();
    }

    public CustomerStatusChangedEventV1 toContract(CustomerStatusChangedEvent event) {
        return CustomerStatusChangedEventV1.builder()
                .customerId(event.customerId())
                .customerIdValue(event.customerIdValue())
                .newStatus(event.newStatus().name())
                .reason(event.reason())
                .occurredAt(event.occurredAt())
                .build();
    }

}