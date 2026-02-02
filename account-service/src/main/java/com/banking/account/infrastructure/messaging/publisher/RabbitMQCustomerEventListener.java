package com.banking.account.infrastructure.messaging.publisher;

import com.banking.account.application.port.out.CustomerEventListener;
import com.banking.account.infrastructure.config.RabbitMQConfig;
import com.banking.contracts.events.customer.CustomerCreatedEventV1;
import com.banking.contracts.events.customer.CustomerStatusChangedEventV1;
import com.banking.contracts.events.customer.CustomerUpdatedEventV1;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
@RequiredArgsConstructor
@Primary
public class RabbitMQCustomerEventListener implements CustomerEventListener {

    private final Map<UUID, CustomerInfo> customerCache = new ConcurrentHashMap<>();

    @RabbitListener(queues = RabbitMQConfig.CUSTOMER_CREATED_QUEUE)
    public void handleCustomerCreated(CustomerCreatedEventV1 event) {
        try {
            log.info("Received customer created event: customerId={}", event.getCustomerId());

            customerCache.put(
                    event.getCustomerId(),
                    new CustomerInfo(
                            event.getCustomerId(),
                            event.getName() + " " + event.getLastName(),
                            event.getStatus()
                    )
            );

            log.info("Customer cached: {} - {}", event.getCustomerId(), event.getName());
        } catch (Exception e) {
            log.error("Error processing customer created event", e);
            throw new RuntimeException("Failed to process customer created event", e);
        }
    }

    @RabbitListener(queues = RabbitMQConfig.CUSTOMER_UPDATED_QUEUE)
    public void handleCustomerUpdated(CustomerUpdatedEventV1 event) {
        try {
            log.info("Received customer updated event: customerId={}", event.getCustomerId());

            CustomerInfo existing = customerCache.get(event.getCustomerId());
            if (Objects.nonNull(existing)) {
                customerCache.put(
                        event.getCustomerId(),
                        new CustomerInfo(
                                event.getCustomerId(),
                                event.getName(),
                                existing.status()
                        )
                );
                log.info("Customer cache updated: {}", event.getCustomerId());
            } else {
                log.warn("Received update for unknown customer: {}", event.getCustomerId());
            }
        } catch (Exception e) {
            log.error("Error processing customer updated event", e);
        }
    }

    @RabbitListener(queues = RabbitMQConfig.CUSTOMER_STATUS_CHANGED_QUEUE)
    public void handleCustomerStatusChanged(CustomerStatusChangedEventV1 event) {
        try {
            log.info("Received customer status changed event: customerId={}, newStatus={}",
                    event.getCustomerId(), event.getNewStatus());

            CustomerInfo existing = customerCache.get(event.getCustomerId());
            if (Objects.nonNull(existing)) {
                customerCache.put(
                        event.getCustomerId(),
                        new CustomerInfo(
                                event.getCustomerId(),
                                existing.name(),
                                event.getNewStatus()
                        )
                );
                log.info("Customer status updated in cache: {}", event.getCustomerId());
            } else {
                log.warn("Received status change for unknown customer: {}", event.getCustomerId());
            }
        } catch (Exception e) {
            log.error("Error processing customer status changed event", e);
        }
    }

    @Override
    public boolean customerExists(UUID customerId) {
        boolean exists = customerCache.containsKey(customerId);

        if (!exists) {
            log.warn("Customer does not exist in cache: customerId={}", customerId);
        }

        return exists;
    }

    @Override
    public boolean isCustomerActive(UUID customerId) {
        CustomerInfo info = customerCache.get(customerId);

        if (Objects.isNull(info)) {
            log.warn("Customer not found in cache: customerId={}", customerId);
            return false;
        }

        boolean isActive = "ACTIVE".equalsIgnoreCase(info.status());

        if (!isActive) {
            log.warn("Customer is not active: customerId={}, status={}",
                    customerId, info.status());
        }

        return isActive;
    }

    @Override
    public String getCustomerName(UUID customerId) {
        CustomerInfo info = customerCache.get(customerId);
        return Objects.nonNull(info) ? info.name() : "Unknown Customer";
    }

    public void invalidateCache(UUID customerId) {
        customerCache.remove(customerId);
        log.debug("Cache invalidated for customer: {}", customerId);
    }

    public void clearCache() {
        customerCache.clear();
        log.info("Customer cache cleared");
    }

    private record CustomerInfo(UUID customerId, String name, String status) {
    }

}