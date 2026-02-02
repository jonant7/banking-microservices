package com.banking.account.infrastructure.messaging.publisher;

import com.banking.account.application.port.out.CustomerEventListener;
import com.banking.account.application.service.CustomerProjectionService;
import com.banking.account.domain.model.CustomerInfo;
import com.banking.account.domain.repository.CustomerProjectionRepository;
import com.banking.account.infrastructure.config.RabbitMQConfig;
import com.banking.contracts.events.customer.CustomerCreatedEventV1;
import com.banking.contracts.events.customer.CustomerStatusChangedEventV1;
import com.banking.contracts.events.customer.CustomerUpdatedEventV1;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
@Primary
public class RabbitMQCustomerEventListener implements CustomerEventListener {

    private final CustomerProjectionService customerProjectionService;
    private final CustomerProjectionRepository customerProjectionRepository;

    @RabbitListener(queues = RabbitMQConfig.CUSTOMER_CREATED_QUEUE)
    public void handleCustomerCreated(CustomerCreatedEventV1 event) {
        try {
            log.info("Received customer created event: customerId={}", event.getCustomerId());
            customerProjectionService.handleCustomerCreated(event);
        } catch (Exception e) {
            log.error("Error processing customer created event: customerId={}", event.getCustomerId(), e);
            throw new RuntimeException("Failed to process customer created event", e);
        }
    }

    @RabbitListener(queues = RabbitMQConfig.CUSTOMER_UPDATED_QUEUE)
    public void handleCustomerUpdated(CustomerUpdatedEventV1 event) {
        try {
            log.info("Received customer updated event: customerId={}", event.getCustomerId());
            customerProjectionService.handleCustomerUpdated(event);
        } catch (Exception e) {
            log.error("Error processing customer updated event: customerId={}", event.getCustomerId(), e);
        }
    }

    @RabbitListener(queues = RabbitMQConfig.CUSTOMER_STATUS_CHANGED_QUEUE)
    public void handleCustomerStatusChanged(CustomerStatusChangedEventV1 event) {
        try {
            log.info("Received customer status changed event: customerId={}, newStatus={}",
                    event.getCustomerId(), event.getNewStatus());
            customerProjectionService.handleCustomerStatusChanged(event);
        } catch (Exception e) {
            log.error("Error processing customer status changed event: customerId={}", event.getCustomerId(), e);
        }
    }

    @Override
    public boolean customerExists(UUID customerId) {
        boolean exists = customerProjectionRepository.existsById(customerId);

        if (!exists) {
            log.warn("Customer does not exist in projection: customerId={}", customerId);
        }

        return exists;
    }


    @Override
    public boolean isCustomerActive(UUID customerId) {
        Optional<CustomerInfo> customerInfoOpt = customerProjectionRepository.findById(customerId);

        if (customerInfoOpt.isEmpty()) {
            log.warn("Customer not found in projection: customerId={}", customerId);
            return false;
        }

        CustomerInfo customerInfo = customerInfoOpt.get();
        boolean isActive = customerInfo.isActive();

        if (!isActive) {
            log.warn("Customer is not active: customerId={}, status={}",
                    customerId, customerInfo.status());
        }

        return isActive;
    }

    @Override
    public String getCustomerName(UUID customerId) {
        return customerProjectionRepository.findById(customerId)
                .map(CustomerInfo::fullName)
                .orElse("Unknown Customer");
    }

}