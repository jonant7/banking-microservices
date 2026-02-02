package com.banking.account.application.port.out;

import java.util.UUID;

public interface CustomerEventListener {

    boolean customerExists(UUID customerId);

    boolean isCustomerActive(UUID customerId);

    String getCustomerName(UUID customerId);

}