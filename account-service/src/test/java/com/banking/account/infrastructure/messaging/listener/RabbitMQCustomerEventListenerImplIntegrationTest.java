package com.banking.account.infrastructure.messaging.listener;

import com.banking.account.IntegrationTest;
import com.banking.account.domain.model.CustomerInfo;
import com.banking.account.domain.model.CustomerStatus;
import com.banking.account.domain.repository.CustomerProjectionRepository;
import com.banking.account.infrastructure.messaging.publisher.RabbitMQCustomerEventListener;
import com.banking.contracts.events.customer.CustomerCreatedEventV1;
import com.banking.contracts.events.customer.CustomerStatusChangedEventV1;
import com.banking.contracts.events.customer.CustomerUpdatedEventV1;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class RabbitMQCustomerEventListenerImplIntegrationTest extends IntegrationTest {

    @Autowired
    private RabbitMQCustomerEventListener eventListener;

    @Autowired
    private CustomerProjectionRepository customerProjectionRepository;

    private UUID customerId;

    @BeforeEach
    void setUp() {
        customerId = UUID.randomUUID();
    }

    @Test
    @DisplayName("Should persist customer when customer created event is received")
    void shouldPersistCustomerWhenCustomerCreatedEventReceived() {
        CustomerCreatedEventV1 event = CustomerCreatedEventV1.builder()
                .customerId(customerId)
                .customerIdValue("CUST-001")
                .name("John")
                .lastName("Doe")
                .gender("M")
                .birthDate(LocalDate.of(1990, 1, 1))
                .identification("1234567890")
                .address("123 Main St")
                .phone("555-1234")
                .status("ACTIVE")
                .occurredAt(LocalDateTime.now())
                .build();

        eventListener.handleCustomerCreated(event);

        Optional<CustomerInfo> result = customerProjectionRepository.findById(customerId);

        assertThat(result).isPresent();
        assertThat(result.get().customerId()).isEqualTo(customerId);
        assertThat(result.get().fullName()).isEqualTo("John Doe");
        assertThat(result.get().status()).isEqualTo(CustomerStatus.ACTIVE);
    }

    @Test
    @DisplayName("Should update customer name when customer updated event is received")
    void shouldUpdateCustomerNameWhenCustomerUpdatedEventReceived() {
        CustomerInfo initialCustomer = new CustomerInfo(
                customerId,
                "John Doe",
                CustomerStatus.ACTIVE
        );
        customerProjectionRepository.save(initialCustomer);

        CustomerUpdatedEventV1 event = CustomerUpdatedEventV1.builder()
                .customerId(customerId)
                .customerIdValue("CUST-001")
                .name("Jane")
                .lastName("Smith")
                .address("456 Oak Ave")
                .phone("555-5678")
                .occurredAt(LocalDateTime.now())
                .build();

        eventListener.handleCustomerUpdated(event);

        Optional<CustomerInfo> result = customerProjectionRepository.findById(customerId);

        assertThat(result).isPresent();
        assertThat(result.get().fullName()).isEqualTo("Jane Smith");
        assertThat(result.get().status()).isEqualTo(CustomerStatus.ACTIVE);
    }

    @Test
    @DisplayName("Should update customer status when status changed event is received")
    void shouldUpdateCustomerStatusWhenStatusChangedEventReceived() {
        CustomerInfo initialCustomer = new CustomerInfo(
                customerId,
                "John Doe",
                CustomerStatus.ACTIVE
        );
        customerProjectionRepository.save(initialCustomer);

        CustomerStatusChangedEventV1 event = CustomerStatusChangedEventV1.builder()
                .customerId(customerId)
                .customerIdValue("CUST-001")
                .newStatus("INACTIVE")
                .reason("Requested by customer")
                .occurredAt(LocalDateTime.now())
                .build();

        eventListener.handleCustomerStatusChanged(event);

        Optional<CustomerInfo> result = customerProjectionRepository.findById(customerId);

        assertThat(result).isPresent();
        assertThat(result.get().fullName()).isEqualTo("John Doe");
        assertThat(result.get().status()).isEqualTo(CustomerStatus.INACTIVE);
    }

    @Test
    @DisplayName("Should return true when customer exists")
    void shouldReturnTrueWhenCustomerExists() {
        CustomerInfo customer = new CustomerInfo(
                customerId,
                "John Doe",
                CustomerStatus.ACTIVE
        );
        customerProjectionRepository.save(customer);

        boolean exists = eventListener.customerExists(customerId);

        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("Should return false when customer does not exist")
    void shouldReturnFalseWhenCustomerDoesNotExist() {
        UUID nonExistentId = UUID.randomUUID();

        boolean exists = eventListener.customerExists(nonExistentId);

        assertThat(exists).isFalse();
    }

    @Test
    @DisplayName("Should return true when customer is active")
    void shouldReturnTrueWhenCustomerIsActive() {
        CustomerInfo customer = new CustomerInfo(
                customerId,
                "John Doe",
                CustomerStatus.ACTIVE
        );
        customerProjectionRepository.save(customer);

        boolean isActive = eventListener.isCustomerActive(customerId);

        assertThat(isActive).isTrue();
    }

    @Test
    @DisplayName("Should return false when customer is inactive")
    void shouldReturnFalseWhenCustomerIsInactive() {
        CustomerInfo customer = new CustomerInfo(
                customerId,
                "John Doe",
                CustomerStatus.INACTIVE
        );
        customerProjectionRepository.save(customer);

        boolean isActive = eventListener.isCustomerActive(customerId);

        assertThat(isActive).isFalse();
    }

    @Test
    @DisplayName("Should return customer name when customer exists")
    void shouldReturnCustomerNameWhenCustomerExists() {
        CustomerInfo customer = new CustomerInfo(
                customerId,
                "John Doe",
                CustomerStatus.ACTIVE
        );
        customerProjectionRepository.save(customer);

        String customerName = eventListener.getCustomerName(customerId);

        assertThat(customerName).isEqualTo("John Doe");
    }

    @Test
    @DisplayName("Should return unknown customer when customer does not exist")
    void shouldReturnUnknownCustomerWhenCustomerDoesNotExist() {
        UUID nonExistentId = UUID.randomUUID();

        String customerName = eventListener.getCustomerName(nonExistentId);

        assertThat(customerName).isEqualTo("Unknown Customer");
    }

}