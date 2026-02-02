package com.banking.customer.domain.event;

import com.banking.customer.domain.model.CustomerStatus;
import com.banking.customer.domain.model.Gender;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class CustomerEventsTest {

    @Nested
    class CustomerCreatedEventTest {

        @Test
        void shouldCreateEventWithAllFields() {
            UUID customerId = UUID.randomUUID();
            LocalDateTime occurredAt = LocalDateTime.now();

            CustomerCreatedEvent event = CustomerCreatedEvent.builder()
                    .customerId(customerId)
                    .customerIdValue("john.doe")
                    .name("John")
                    .lastName("Doe")
                    .gender(Gender.MALE)
                    .birthDate(LocalDate.now().minusYears(30))
                    .identification("1234567890")
                    .address("123 Main St")
                    .phone("0987654321")
                    .status(CustomerStatus.ACTIVE)
                    .occurredAt(occurredAt)
                    .build();

            assertThat(event).isNotNull();
            assertThat(event.customerId()).isEqualTo(customerId);
            assertThat(event.customerIdValue()).isEqualTo("john.doe");
            assertThat(event.name()).isEqualTo("John");
            assertThat(event.lastName()).isEqualTo("Doe");
            assertThat(event.gender()).isEqualTo(Gender.MALE);
            assertThat(event.identification()).isEqualTo("1234567890");
            assertThat(event.address()).isEqualTo("123 Main St");
            assertThat(event.phone()).isEqualTo("0987654321");
            assertThat(event.status()).isEqualTo(CustomerStatus.ACTIVE);
            assertThat(event.occurredAt()).isEqualTo(occurredAt);
        }

        @Test
        void shouldCreateEventWithMinimalFields() {
            UUID customerId = UUID.randomUUID();

            CustomerCreatedEvent event = CustomerCreatedEvent.builder()
                    .customerId(customerId)
                    .customerIdValue("customer")
                    .name("Name")
                    .lastName("LastName")
                    .gender(Gender.MALE)
                    .birthDate(LocalDate.now().minusYears(20))
                    .identification("1234567890")
                    .address("Address")
                    .status(CustomerStatus.ACTIVE)
                    .occurredAt(LocalDateTime.now())
                    .build();

            assertThat(event).isNotNull();
            assertThat(event.customerId()).isEqualTo(customerId);
        }

        @Test
        void shouldAllowNullPhone() {
            CustomerCreatedEvent event = CustomerCreatedEvent.builder()
                    .customerId(UUID.randomUUID())
                    .customerIdValue("customer")
                    .name("Name")
                    .lastName("LastName")
                    .gender(Gender.MALE)
                    .birthDate(LocalDate.now().minusYears(20))
                    .identification("1234567890")
                    .address("Address")
                    .phone(null)
                    .status(CustomerStatus.ACTIVE)
                    .occurredAt(LocalDateTime.now())
                    .build();

            assertThat(event.phone()).isNull();
        }

        @Test
        void shouldCreateEventWithInactiveStatus() {
            CustomerCreatedEvent event = CustomerCreatedEvent.builder()
                    .customerId(UUID.randomUUID())
                    .customerIdValue("customer")
                    .name("Name")
                    .lastName("LastName")
                    .gender(Gender.FEMALE)
                    .birthDate(LocalDate.now().minusYears(25))
                    .identification("9876543210")
                    .address("Address")
                    .status(CustomerStatus.INACTIVE)
                    .occurredAt(LocalDateTime.now())
                    .build();

            assertThat(event.status()).isEqualTo(CustomerStatus.INACTIVE);
        }
    }

    @Nested
    class CustomerStatusChangedEventTest {

        @Test
        void shouldCreateStatusChangedEvent() {
            UUID customerId = UUID.randomUUID();
            LocalDateTime occurredAt = LocalDateTime.now();

            CustomerStatusChangedEvent event = CustomerStatusChangedEvent.builder()
                    .customerId(customerId)
                    .customerIdValue("john.doe")
                    .newStatus(CustomerStatus.INACTIVE)
                    .reason("Deactivated")
                    .occurredAt(occurredAt)
                    .build();

            assertThat(event).isNotNull();
            assertThat(event.customerId()).isEqualTo(customerId);
            assertThat(event.customerIdValue()).isEqualTo("john.doe");
            assertThat(event.newStatus()).isEqualTo(CustomerStatus.INACTIVE);
            assertThat(event.reason()).isEqualTo("Deactivated");
            assertThat(event.occurredAt()).isEqualTo(occurredAt);
        }

        @Test
        void shouldCreateActivationEvent() {
            CustomerStatusChangedEvent event = CustomerStatusChangedEvent.builder()
                    .customerId(UUID.randomUUID())
                    .customerIdValue("customer")
                    .newStatus(CustomerStatus.ACTIVE)
                    .reason("Activated")
                    .occurredAt(LocalDateTime.now())
                    .build();

            assertThat(event.newStatus()).isEqualTo(CustomerStatus.ACTIVE);
            assertThat(event.reason()).isEqualTo("Activated");
        }

        @Test
        void shouldCreateDeactivationEvent() {
            CustomerStatusChangedEvent event = CustomerStatusChangedEvent.builder()
                    .customerId(UUID.randomUUID())
                    .customerIdValue("customer")
                    .newStatus(CustomerStatus.INACTIVE)
                    .reason("Deactivated")
                    .occurredAt(LocalDateTime.now())
                    .build();

            assertThat(event.newStatus()).isEqualTo(CustomerStatus.INACTIVE);
            assertThat(event.reason()).isEqualTo("Deactivated");
        }
    }

    @Nested
    class CustomerUpdatedEventTest {

        @Test
        void shouldCreateUpdatedEventWithAllFields() {
            UUID customerId = UUID.randomUUID();
            LocalDateTime occurredAt = LocalDateTime.now();

            CustomerUpdatedEvent event = CustomerUpdatedEvent.builder()
                    .customerId(customerId)
                    .customerIdValue("john.doe")
                    .name("Jane")
                    .lastName("Smith")
                    .address("456 Oak St")
                    .phone("0991234567")
                    .occurredAt(occurredAt)
                    .build();

            assertThat(event).isNotNull();
            assertThat(event.customerId()).isEqualTo(customerId);
            assertThat(event.customerIdValue()).isEqualTo("john.doe");
            assertThat(event.name()).isEqualTo("Jane");
            assertThat(event.lastName()).isEqualTo("Smith");
            assertThat(event.address()).isEqualTo("456 Oak St");
            assertThat(event.phone()).isEqualTo("0991234567");
            assertThat(event.occurredAt()).isEqualTo(occurredAt);
        }

        @Test
        void shouldCreatePartialUpdateEvent() {
            CustomerUpdatedEvent event = CustomerUpdatedEvent.builder()
                    .customerId(UUID.randomUUID())
                    .customerIdValue("customer")
                    .address("New Address")
                    .occurredAt(LocalDateTime.now())
                    .build();

            assertThat(event.address()).isEqualTo("New Address");
            assertThat(event.name()).isNull();
            assertThat(event.lastName()).isNull();
            assertThat(event.phone()).isNull();
        }

        @Test
        void shouldAllowNullFields() {
            CustomerUpdatedEvent event = CustomerUpdatedEvent.builder()
                    .customerId(UUID.randomUUID())
                    .customerIdValue("customer")
                    .name(null)
                    .lastName(null)
                    .address(null)
                    .phone(null)
                    .occurredAt(LocalDateTime.now())
                    .build();

            assertThat(event.name()).isNull();
            assertThat(event.lastName()).isNull();
            assertThat(event.address()).isNull();
            assertThat(event.phone()).isNull();
        }
    }

    @Nested
    class EventTimestamps {

        @Test
        void shouldCaptureOccurredAtTimestamp() {
            LocalDateTime before = LocalDateTime.now().minusSeconds(1);

            CustomerCreatedEvent event = CustomerCreatedEvent.builder()
                    .customerId(UUID.randomUUID())
                    .customerIdValue("customer")
                    .name("Name")
                    .lastName("LastName")
                    .gender(Gender.MALE)
                    .birthDate(LocalDate.now().minusYears(20))
                    .identification("1234567890")
                    .address("Address")
                    .status(CustomerStatus.ACTIVE)
                    .occurredAt(LocalDateTime.now())
                    .build();

            LocalDateTime after = LocalDateTime.now().plusSeconds(1);

            assertThat(event.occurredAt()).isAfter(before);
            assertThat(event.occurredAt()).isBefore(after);
        }

        @Test
        void shouldAllowSpecificTimestamp() {
            LocalDateTime specificTime = LocalDateTime.of(2024, 1, 15, 10, 30, 0);

            CustomerUpdatedEvent event = CustomerUpdatedEvent.builder()
                    .customerId(UUID.randomUUID())
                    .customerIdValue("customer")
                    .occurredAt(specificTime)
                    .build();

            assertThat(event.occurredAt()).isEqualTo(specificTime);
        }
    }

    @Nested
    class EventEquality {

        @Test
        void shouldBeEqualWhenSameData() {
            UUID customerId = UUID.randomUUID();
            LocalDateTime occurredAt = LocalDateTime.now();

            CustomerStatusChangedEvent event1 = CustomerStatusChangedEvent.builder()
                    .customerId(customerId)
                    .customerIdValue("customer")
                    .newStatus(CustomerStatus.ACTIVE)
                    .reason("Activated")
                    .occurredAt(occurredAt)
                    .build();

            CustomerStatusChangedEvent event2 = CustomerStatusChangedEvent.builder()
                    .customerId(customerId)
                    .customerIdValue("customer")
                    .newStatus(CustomerStatus.ACTIVE)
                    .reason("Activated")
                    .occurredAt(occurredAt)
                    .build();

            assertThat(event1).isEqualTo(event2);
        }

        @Test
        void shouldNotBeEqualWhenDifferentData() {
            CustomerStatusChangedEvent event1 = CustomerStatusChangedEvent.builder()
                    .customerId(UUID.randomUUID())
                    .customerIdValue("customer1")
                    .newStatus(CustomerStatus.ACTIVE)
                    .reason("Activated")
                    .occurredAt(LocalDateTime.now())
                    .build();

            CustomerStatusChangedEvent event2 = CustomerStatusChangedEvent.builder()
                    .customerId(UUID.randomUUID())
                    .customerIdValue("customer2")
                    .newStatus(CustomerStatus.INACTIVE)
                    .reason("Deactivated")
                    .occurredAt(LocalDateTime.now())
                    .build();

            assertThat(event1).isNotEqualTo(event2);
        }
    }

}