package com.banking.customer.infrastructure.messaging.publisher;

import com.banking.customer.application.port.out.DomainEventPublisher;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class NoOpDomainEventPublisher implements DomainEventPublisher {

    @Override
    public void publish(List<Object> domainEvents) {
        log.debug("Skipping domain event publishing (NoOp)");
    }

}