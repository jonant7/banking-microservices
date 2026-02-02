package com.banking.account.service;

import com.banking.account.application.service.CustomerProjectionService;
import com.banking.account.domain.model.CustomerInfo;
import com.banking.account.domain.model.CustomerStatus;
import com.banking.account.domain.repository.CustomerProjectionRepository;
import com.banking.contracts.events.customer.CustomerCreatedEventV1;
import com.banking.contracts.events.customer.CustomerStatusChangedEventV1;
import com.banking.contracts.events.customer.CustomerUpdatedEventV1;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerProjectionServiceTest {

    @Mock
    private CustomerProjectionRepository customerProjectionRepository;

    @InjectMocks
    private CustomerProjectionService customerProjectionService;

    private UUID customerId;

    @BeforeEach
    void setUp() {
        customerId = UUID.randomUUID();
    }

    @Test
    @DisplayName("Should save customer projection when handling customer created event")
    void shouldSaveCustomerProjectionWhenHandlingCustomerCreatedEvent() {
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

        customerProjectionService.handleCustomerCreated(event);

        ArgumentCaptor<CustomerInfo> captor = ArgumentCaptor.forClass(CustomerInfo.class);
        verify(customerProjectionRepository).save(captor.capture());

        CustomerInfo savedCustomer = captor.getValue();
        assertThat(savedCustomer.customerId()).isEqualTo(customerId);
        assertThat(savedCustomer.fullName()).isEqualTo("John Doe");
        assertThat(savedCustomer.status()).isEqualTo(CustomerStatus.ACTIVE);
    }

    @Test
    @DisplayName("Should update customer name when handling customer updated event")
    void shouldUpdateCustomerNameWhenHandlingCustomerUpdatedEvent() {
        CustomerInfo existingCustomer = new CustomerInfo(
                customerId,
                "John Doe",
                CustomerStatus.ACTIVE
        );
        when(customerProjectionRepository.findById(customerId))
                .thenReturn(Optional.of(existingCustomer));

        CustomerUpdatedEventV1 event = CustomerUpdatedEventV1.builder()
                .customerId(customerId)
                .customerIdValue("CUST-001")
                .name("Jane")
                .lastName("Smith")
                .address("456 Oak Ave")
                .phone("555-5678")
                .occurredAt(LocalDateTime.now())
                .build();

        customerProjectionService.handleCustomerUpdated(event);

        ArgumentCaptor<CustomerInfo> captor = ArgumentCaptor.forClass(CustomerInfo.class);
        verify(customerProjectionRepository).save(captor.capture());

        CustomerInfo updatedCustomer = captor.getValue();
        assertThat(updatedCustomer.customerId()).isEqualTo(customerId);
        assertThat(updatedCustomer.fullName()).isEqualTo("Jane Smith");
        assertThat(updatedCustomer.status()).isEqualTo(CustomerStatus.ACTIVE);
    }

    @Test
    @DisplayName("Should not update when customer not found in updated event")
    void shouldNotUpdateWhenCustomerNotFoundInUpdatedEvent() {
        when(customerProjectionRepository.findById(customerId))
                .thenReturn(Optional.empty());

        CustomerUpdatedEventV1 event = CustomerUpdatedEventV1.builder()
                .customerId(customerId)
                .customerIdValue("CUST-001")
                .name("Jane")
                .lastName("Smith")
                .address("456 Oak Ave")
                .phone("555-5678")
                .occurredAt(LocalDateTime.now())
                .build();

        customerProjectionService.handleCustomerUpdated(event);

        verify(customerProjectionRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should update customer status when handling status changed event")
    void shouldUpdateCustomerStatusWhenHandlingStatusChangedEvent() {
        CustomerInfo existingCustomer = new CustomerInfo(
                customerId,
                "John Doe",
                CustomerStatus.ACTIVE
        );
        when(customerProjectionRepository.findById(customerId))
                .thenReturn(Optional.of(existingCustomer));

        CustomerStatusChangedEventV1 event = CustomerStatusChangedEventV1.builder()
                .customerId(customerId)
                .customerIdValue("CUST-001")
                .newStatus("INACTIVE")
                .reason("Requested by customer")
                .occurredAt(LocalDateTime.now())
                .build();

        customerProjectionService.handleCustomerStatusChanged(event);

        ArgumentCaptor<CustomerInfo> captor = ArgumentCaptor.forClass(CustomerInfo.class);
        verify(customerProjectionRepository).save(captor.capture());

        CustomerInfo updatedCustomer = captor.getValue();
        assertThat(updatedCustomer.customerId()).isEqualTo(customerId);
        assertThat(updatedCustomer.fullName()).isEqualTo("John Doe");
        assertThat(updatedCustomer.status()).isEqualTo(CustomerStatus.INACTIVE);
    }

    @Test
    @DisplayName("Should not update status when customer not found")
    void shouldNotUpdateStatusWhenCustomerNotFound() {
        when(customerProjectionRepository.findById(customerId))
                .thenReturn(Optional.empty());

        CustomerStatusChangedEventV1 event = CustomerStatusChangedEventV1.builder()
                .customerId(customerId)
                .customerIdValue("CUST-001")
                .newStatus("INACTIVE")
                .reason("Requested by customer")
                .occurredAt(LocalDateTime.now())
                .build();

        customerProjectionService.handleCustomerStatusChanged(event);

        verify(customerProjectionRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should map ACTIVE status correctly")
    void shouldMapActiveStatusCorrectly() {
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

        customerProjectionService.handleCustomerCreated(event);

        ArgumentCaptor<CustomerInfo> captor = ArgumentCaptor.forClass(CustomerInfo.class);
        verify(customerProjectionRepository).save(captor.capture());

        assertThat(captor.getValue().status()).isEqualTo(CustomerStatus.ACTIVE);
    }

    @Test
    @DisplayName("Should map non-ACTIVE status to INACTIVE")
    void shouldMapNonActiveStatusToInactive() {
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
                .status("SUSPENDED")
                .occurredAt(LocalDateTime.now())
                .build();

        customerProjectionService.handleCustomerCreated(event);

        ArgumentCaptor<CustomerInfo> captor = ArgumentCaptor.forClass(CustomerInfo.class);
        verify(customerProjectionRepository).save(captor.capture());

        assertThat(captor.getValue().status()).isEqualTo(CustomerStatus.INACTIVE);
    }

}