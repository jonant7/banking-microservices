package com.banking.account.application.service;

import com.banking.account.domain.model.CustomerInfo;
import com.banking.account.domain.model.CustomerStatus;
import com.banking.account.domain.repository.CustomerProjectionRepository;
import com.banking.contracts.events.customer.CustomerCreatedEventV1;
import com.banking.contracts.events.customer.CustomerStatusChangedEventV1;
import com.banking.contracts.events.customer.CustomerUpdatedEventV1;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomerProjectionService {

    private final CustomerProjectionRepository customerProjectionRepository;

    @Transactional
    public void handleCustomerCreated(CustomerCreatedEventV1 event) {
        log.info("Processing customer created event: customerId={}", event.getCustomerId());

        CustomerInfo customerInfo = new CustomerInfo(
                event.getCustomerId(),
                buildFullName(event.getName(), event.getLastName()),
                mapStatus(event.getStatus())
        );

        customerProjectionRepository.save(customerInfo);

        log.info("Customer projection created: customerId={}", event.getCustomerId());
    }

    @Transactional
    public void handleCustomerUpdated(CustomerUpdatedEventV1 event) {
        log.info("Processing customer updated event: customerId={}", event.getCustomerId());

        Optional<CustomerInfo> existingOpt = customerProjectionRepository.findById(event.getCustomerId());

        if (existingOpt.isEmpty()) {
            log.warn("Customer not found for update event: customerId={}", event.getCustomerId());
            return;
        }

        CustomerInfo existing = existingOpt.get();
        CustomerInfo updated = new CustomerInfo(
                existing.customerId(),
                buildFullName(event.getName(), event.getLastName()),
                existing.status()
        );

        customerProjectionRepository.save(updated);

        log.info("Customer projection updated: customerId={}", event.getCustomerId());
    }

    @Transactional
    public void handleCustomerStatusChanged(CustomerStatusChangedEventV1 event) {
        log.info("Processing customer status changed event: customerId={}, newStatus={}",
                event.getCustomerId(), event.getNewStatus());

        Optional<CustomerInfo> existingOpt = customerProjectionRepository.findById(event.getCustomerId());

        if (existingOpt.isEmpty()) {
            log.warn("Customer not found for status change event: customerId={}", event.getCustomerId());
            return;
        }

        CustomerInfo existing = existingOpt.get();
        CustomerInfo updated = new CustomerInfo(
                existing.customerId(),
                existing.fullName(),
                mapStatus(event.getNewStatus())
        );

        customerProjectionRepository.save(updated);

        log.info("Customer projection status updated: customerId={}, newStatus={}",
                event.getCustomerId(), event.getNewStatus());
    }

    private String buildFullName(String name, String lastName) {
        if (lastName == null || lastName.isBlank()) {
            return name;
        }
        return name + " " + lastName;
    }

    private CustomerStatus mapStatus(String status) {
        if (status == null) {
            return CustomerStatus.INACTIVE;
        }
        return "ACTIVE".equalsIgnoreCase(status) ? CustomerStatus.ACTIVE : CustomerStatus.INACTIVE;
    }

}