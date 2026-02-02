package com.banking.customer.infrastructure.messaging.publisher;

import com.banking.contracts.events.customer.CustomerCreatedEventV1;
import com.banking.contracts.events.customer.CustomerStatusChangedEventV1;
import com.banking.contracts.events.customer.CustomerUpdatedEventV1;
import com.banking.customer.application.port.out.DomainEventPublisher;
import com.banking.customer.domain.event.CustomerCreatedEvent;
import com.banking.customer.domain.event.CustomerStatusChangedEvent;
import com.banking.customer.domain.event.CustomerUpdatedEvent;
import com.banking.customer.infrastructure.config.RabbitMQConfig;
import com.banking.customer.infrastructure.messaging.mapper.CustomerEventMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

@Slf4j
@Component
@RequiredArgsConstructor
@Primary
public class RabbitMQDomainEventPublisher implements DomainEventPublisher {

    private final RabbitTemplate rabbitTemplate;
    private final CustomerEventMapper eventMapper;

    @Override
    public void publish(List<Object> domainEvents) {
        if (Objects.isNull(domainEvents) || domainEvents.isEmpty()) {
            return;
        }
        domainEvents.forEach(this::publishEvent);
    }

    private void publishEvent(Object event) {
        switch (event) {
            case CustomerCreatedEvent e -> publishCustomerCreated(e);
            case CustomerStatusChangedEvent e -> publishCustomerStatusChanged(e);
            case CustomerUpdatedEvent e -> publishCustomerUpdated(e);
            default -> log.warn("Unknown event type: {}", event.getClass().getName());
        }
    }

    private void publishCustomerCreated(CustomerCreatedEvent event) {
        log.info("Publishing CustomerCreatedEvent for customerId: {}", event.customerId());

        CustomerCreatedEventV1 contractEvent = eventMapper.toContract(event);

        rabbitTemplate.convertAndSend(
                RabbitMQConfig.CUSTOMER_EXCHANGE,
                RabbitMQConfig.CUSTOMER_CREATED_ROUTING_KEY,
                contractEvent
        );

        log.debug("CustomerCreatedEventV1 published successfully");
    }

    private void publishCustomerStatusChanged(CustomerStatusChangedEvent event) {
        log.info("Publishing CustomerStatusChangedEvent for customerId: {}", event.customerId());

        CustomerStatusChangedEventV1 contractEvent = eventMapper.toContract(event);

        String routingKey = event.newStatus().isActive()
                ? RabbitMQConfig.CUSTOMER_UPDATED_ROUTING_KEY + ".activated"
                : RabbitMQConfig.CUSTOMER_UPDATED_ROUTING_KEY + ".deactivated";

        rabbitTemplate.convertAndSend(
                RabbitMQConfig.CUSTOMER_EXCHANGE,
                routingKey,
                contractEvent
        );

        log.debug("CustomerStatusChangedEventV1 published successfully");
    }

    private void publishCustomerUpdated(CustomerUpdatedEvent event) {
        log.info("Publishing CustomerUpdatedEvent for customerId: {}", event.customerId());

        CustomerUpdatedEventV1 contractEvent = eventMapper.toContract(event);

        rabbitTemplate.convertAndSend(
                RabbitMQConfig.CUSTOMER_EXCHANGE,
                RabbitMQConfig.CUSTOMER_UPDATED_ROUTING_KEY,
                contractEvent
        );

        log.debug("CustomerUpdatedEventV1 published successfully");
    }

}