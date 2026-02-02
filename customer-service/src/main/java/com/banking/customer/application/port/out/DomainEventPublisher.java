package com.banking.customer.application.port.out;

import java.util.List;

public interface DomainEventPublisher {

    void publish(List<Object> domainEvents);

}