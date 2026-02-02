package com.banking.account.infrastructure.messaging.publisher;

import com.banking.account.application.port.out.DomainEventPublisher;
import com.banking.account.domain.event.AccountCreatedEvent;
import com.banking.account.domain.event.TransactionPerformedEvent;
import com.banking.account.infrastructure.config.RabbitMQConfig;
import com.banking.account.infrastructure.messaging.publisher.mapper.AccountEventMapper;
import com.banking.contracts.events.account.AccountCreatedEventV1;
import com.banking.contracts.events.account.TransactionPerformedEventV1;
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
    private final AccountEventMapper eventMapper;

    public void publish(List<Object> domainEvents) {
        if (Objects.isNull(domainEvents) || domainEvents.isEmpty()) {
            return;
        }
        domainEvents.forEach(this::publishEvent);
    }

    private void publishEvent(Object event) {
        switch (event) {
            case AccountCreatedEvent e -> publishAccountCreated(e);
            case TransactionPerformedEvent e -> publishTransactionPerformed(e);
            default -> log.warn("Unhandled domain event type: {}", event.getClass().getName());
        }
    }

    private void publishAccountCreated(AccountCreatedEvent event) {
        log.info("Publishing AccountCreatedEvent for account: {}", event.accountNumber());

        AccountCreatedEventV1 contractEvent = eventMapper.toContract(event);

        rabbitTemplate.convertAndSend(
                RabbitMQConfig.ACCOUNT_EXCHANGE,
                RabbitMQConfig.ACCOUNT_CREATED_ROUTING_KEY,
                contractEvent
        );

        log.debug("AccountCreatedEventV1 published successfully");
    }

    private void publishTransactionPerformed(TransactionPerformedEvent event) {
        log.info("Publishing TransactionPerformedEvent for transaction: {}", event.transactionId());

        TransactionPerformedEventV1 contractEvent = eventMapper.toContract(event);

        rabbitTemplate.convertAndSend(
                RabbitMQConfig.ACCOUNT_EXCHANGE,
                RabbitMQConfig.TRANSACTION_CREATED_ROUTING_KEY,
                contractEvent
        );

        log.debug("TransactionPerformedEventV1 published successfully");
    }

}