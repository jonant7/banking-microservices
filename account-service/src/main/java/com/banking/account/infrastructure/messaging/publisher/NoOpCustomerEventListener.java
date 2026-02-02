package com.banking.account.infrastructure.messaging.publisher;

import com.banking.account.application.port.out.CustomerEventListener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Component
public class NoOpCustomerEventListener implements CustomerEventListener {

    @Override
    public boolean isCustomerActive(UUID customerId) {
        log.debug("Checking if customer is active for ID: {}", customerId);
        return false;
    }

    @Override
    public String getCustomerName(UUID customerId) {
        log.debug("Fetching customer name for ID: {}", customerId);
        return "";
    }

    @Override
    public boolean customerExists(UUID customerId) {
        log.debug("Checking if customer exists for ID: {}", customerId);
        return false;
    }

}