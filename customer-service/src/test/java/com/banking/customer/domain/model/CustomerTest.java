package com.banking.customer.domain.model;

import com.banking.customer.domain.event.CustomerCreatedEvent;
import com.banking.customer.domain.event.CustomerStatusChangedEvent;
import com.banking.customer.domain.event.CustomerUpdatedEvent;
import com.banking.customer.domain.exception.CustomerErrorCode;
import com.banking.customer.domain.exception.InactiveCustomerException;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static com.banking.customer.fixtures.builders.CustomerBuilder.aCustomer;
import static com.banking.customer.fixtures.mothers.CustomerMother.*;
import static org.assertj.core.api.Assertions.*;

class CustomerTest {

    @Nested
    class CreateCustomer {

        @Test
        void shouldCreateCustomerWithValidData() {
            Customer customer = newlyCreatedCustomer();

            assertThat(customer).isNotNull();
            assertThat(customer.getId()).isNotNull();
            assertThat(customer.getName()).isEqualTo("John");
            assertThat(customer.getLastName()).isEqualTo("Doe");
            assertThat(customer.getGender()).isEqualTo(Gender.MALE);
            assertThat(customer.isActive()).isTrue();
            assertThat(customer.getStatus()).isEqualTo(CustomerStatus.ACTIVE);
            assertThat(customer.getCreatedAt()).isNotNull();
            assertThat(customer.getUpdatedAt()).isNotNull();
        }

        @Test
        void shouldRegisterCustomerCreatedEvent() {
            Customer customer = newlyCreatedCustomer();

            List<Object> events = customer.getDomainEvents();

            assertThat(events).hasSize(1);
            assertThat(events.get(0)).isInstanceOf(CustomerCreatedEvent.class);

            CustomerCreatedEvent event = (CustomerCreatedEvent) events.get(0);
            assertThat(event.customerId()).isEqualTo(customer.getId());
            assertThat(event.status()).isEqualTo(CustomerStatus.ACTIVE);
        }

        @Test
        void shouldThrowExceptionWhenNameIsNull() {
            assertThatThrownBy(() -> Customer.create(
                    null,
                    "Doe",
                    Gender.MALE,
                    LocalDate.now().minusYears(30),
                    "1234567890",
                    "Address",
                    "0987654321",
                    "customer",
                    "$2a$10$hash"
            )).isInstanceOf(IllegalArgumentException.class)
                    .hasMessage(CustomerErrorCode.CUSTOMER_VALIDATION_NAME_NULL.getCode());
        }

        @Test
        void shouldThrowExceptionWhenNameIsBlank() {
            assertThatThrownBy(() -> Customer.create(
                    "   ",
                    "Doe",
                    Gender.MALE,
                    LocalDate.now().minusYears(30),
                    "1234567890",
                    "Address",
                    "0987654321",
                    "customer",
                    "$2a$10$hash"
            )).isInstanceOf(IllegalArgumentException.class)
                    .hasMessage(CustomerErrorCode.CUSTOMER_VALIDATION_NAME_NULL.getCode());
        }

        @Test
        void shouldThrowExceptionWhenLastNameIsNull() {
            assertThatThrownBy(() -> Customer.create(
                    "John",
                    null,
                    Gender.MALE,
                    LocalDate.now().minusYears(30),
                    "1234567890",
                    "Address",
                    "0987654321",
                    "customer",
                    "$2a$10$hash"
            )).isInstanceOf(IllegalArgumentException.class)
                    .hasMessage(CustomerErrorCode.CUSTOMER_VALIDATION_LASTNAME_NULL.getCode());
        }

        @Test
        void shouldThrowExceptionWhenGenderIsNull() {
            assertThatThrownBy(() -> Customer.create(
                    "John",
                    "Doe",
                    null,
                    LocalDate.now().minusYears(30),
                    "1234567890",
                    "Address",
                    "0987654321",
                    "customer",
                    "$2a$10$hash"
            )).isInstanceOf(IllegalArgumentException.class)
                    .hasMessage(CustomerErrorCode.CUSTOMER_VALIDATION_GENDER_NULL.getCode());
        }

        @Test
        void shouldThrowExceptionWhenBirthDateIsNull() {
            assertThatThrownBy(() -> Customer.create(
                    "John",
                    "Doe",
                    Gender.MALE,
                    null,
                    "1234567890",
                    "Address",
                    "0987654321",
                    "customer",
                    "$2a$10$hash"
            )).isInstanceOf(IllegalArgumentException.class)
                    .hasMessage(CustomerErrorCode.CUSTOMER_VALIDATION_BIRTHDATE_NULL.getCode());
        }

        @Test
        void shouldThrowExceptionWhenBirthDateIsInFuture() {
            assertThatThrownBy(() -> Customer.create(
                    "John",
                    "Doe",
                    Gender.MALE,
                    LocalDate.now().plusDays(1),
                    "1234567890",
                    "Address",
                    "0987654321",
                    "customer",
                    "$2a$10$hash"
            )).isInstanceOf(IllegalArgumentException.class)
                    .hasMessage(CustomerErrorCode.CUSTOMER_VALIDATION_BIRTHDATE_FUTURE.getCode());
        }

        @Test
        void shouldThrowExceptionWhenAgeIsLessThan18() {
            assertThatThrownBy(() -> Customer.create(
                    "John",
                    "Doe",
                    Gender.MALE,
                    LocalDate.now().minusYears(17),
                    "1234567890",
                    "Address",
                    "0987654321",
                    "customer",
                    "$2a$10$hash"
            )).isInstanceOf(IllegalArgumentException.class)
                    .hasMessage(CustomerErrorCode.CUSTOMER_VALIDATION_AGE_UNDERAGE.getCode());
        }

        @Test
        void shouldThrowExceptionWhenIdentificationIsNull() {
            assertThatThrownBy(() -> Customer.create(
                    "John",
                    "Doe",
                    Gender.MALE,
                    LocalDate.now().minusYears(30),
                    null,
                    "Address",
                    "0987654321",
                    "customer",
                    "$2a$10$hash"
            )).isInstanceOf(IllegalArgumentException.class)
                    .hasMessage(CustomerErrorCode.CUSTOMER_VALIDATION_IDENTIFICATION_NULL.getCode());
        }

        @Test
        void shouldThrowExceptionWhenCustomerIdIsNull() {
            assertThatThrownBy(() -> Customer.create(
                    "John",
                    "Doe",
                    Gender.MALE,
                    LocalDate.now().minusYears(30),
                    "1234567890",
                    "Address",
                    "0987654321",
                    null,
                    "$2a$10$hash"
            )).isInstanceOf(IllegalArgumentException.class)
                    .hasMessage(CustomerErrorCode.CUSTOMER_VALIDATION_ID_NULL.getCode());
        }

        @Test
        void shouldThrowExceptionWhenPasswordHashIsNull() {
            assertThatThrownBy(() -> Customer.create(
                    "John",
                    "Doe",
                    Gender.MALE,
                    LocalDate.now().minusYears(30),
                    "1234567890",
                    "Address",
                    "0987654321",
                    "customer",
                    null
            )).isInstanceOf(IllegalArgumentException.class)
                    .hasMessage(CustomerErrorCode.CUSTOMER_VALIDATION_PASSWORD_NULL.getCode());
        }

        @Test
        void shouldCreateCustomerWithNullPhone() {
            Customer customer = customerWithoutPhone();

            assertThat(customer.getPhone()).isNull();
        }
    }

    @Nested
    class ActivateDeactivate {

        @Test
        void shouldActivateInactiveCustomer() {
            Customer customer = inactiveCustomer();

            customer.activate();

            assertThat(customer.isActive()).isTrue();
            assertThat(customer.getStatus()).isEqualTo(CustomerStatus.ACTIVE);
        }

        @Test
        void shouldRegisterStatusChangedEventWhenActivating() {
            Customer customer = inactiveCustomer();

            customer.activate();

            List<Object> events = customer.getDomainEvents();
            assertThat(events).hasSize(1);
            assertThat(events.get(0)).isInstanceOf(CustomerStatusChangedEvent.class);

            CustomerStatusChangedEvent event = (CustomerStatusChangedEvent) events.get(0);
            assertThat(event.newStatus()).isEqualTo(CustomerStatus.ACTIVE);
            assertThat(event.reason()).isEqualTo("Activated");
        }

        @Test
        void shouldNotRegisterEventWhenActivatingAlreadyActiveCustomer() {
            Customer customer = activeCustomer();

            customer.activate();

            assertThat(customer.getDomainEvents()).isEmpty();
        }

        @Test
        void shouldDeactivateActiveCustomer() {
            Customer customer = activeCustomer();

            customer.deactivate();

            assertThat(customer.isActive()).isFalse();
            assertThat(customer.getStatus()).isEqualTo(CustomerStatus.INACTIVE);
        }

        @Test
        void shouldRegisterStatusChangedEventWhenDeactivating() {
            Customer customer = activeCustomer();

            customer.deactivate();

            List<Object> events = customer.getDomainEvents();
            assertThat(events).hasSize(1);
            assertThat(events.get(0)).isInstanceOf(CustomerStatusChangedEvent.class);

            CustomerStatusChangedEvent event = (CustomerStatusChangedEvent) events.get(0);
            assertThat(event.newStatus()).isEqualTo(CustomerStatus.INACTIVE);
            assertThat(event.reason()).isEqualTo("Deactivated");
        }

        @Test
        void shouldNotRegisterEventWhenDeactivatingAlreadyInactiveCustomer() {
            Customer customer = inactiveCustomer();

            customer.deactivate();

            assertThat(customer.getDomainEvents()).isEmpty();
        }

        @Test
        void shouldUpdateTimestampWhenActivating() {
            Customer customer = inactiveCustomer();
            var originalUpdatedAt = customer.getUpdatedAt();

            customer.activate();

            assertThat(customer.getUpdatedAt()).isAfter(originalUpdatedAt);
        }

        @Test
        void shouldUpdateTimestampWhenDeactivating() {
            Customer customer = activeCustomer();
            var originalUpdatedAt = customer.getUpdatedAt();

            customer.deactivate();

            assertThat(customer.getUpdatedAt()).isAfter(originalUpdatedAt);
        }
    }

    @Nested
    class EnsureActive {

        @Test
        void shouldNotThrowExceptionWhenCustomerIsActive() {
            Customer customer = activeCustomer();

            assertThatCode(customer::ensureActive).doesNotThrowAnyException();
        }

        @Test
        void shouldThrowExceptionWhenCustomerIsInactive() {
            Customer customer = inactiveCustomer();

            assertThatThrownBy(customer::ensureActive)
                    .isInstanceOf(InactiveCustomerException.class);
        }
    }

    @Nested
    class UpdatePassword {

        @Test
        void shouldUpdatePasswordForActiveCustomer() {
            Customer customer = activeCustomer();
            PasswordHash newHash = PasswordHash.fromHash("$2a$10$newhash");

            customer.updatePassword(newHash);

            assertThat(customer.getPasswordHash()).isEqualTo(newHash);
        }

        @Test
        void shouldUpdateTimestampWhenUpdatingPassword() {
            Customer customer = activeCustomer();
            var originalUpdatedAt = customer.getUpdatedAt();
            PasswordHash newHash = PasswordHash.fromHash("$2a$10$newhash");

            customer.updatePassword(newHash);

            assertThat(customer.getUpdatedAt()).isAfter(originalUpdatedAt);
        }

        @Test
        void shouldThrowExceptionWhenUpdatingPasswordForInactiveCustomer() {
            Customer customer = inactiveCustomer();
            PasswordHash newHash = PasswordHash.fromHash("$2a$10$newhash");

            assertThatThrownBy(() -> customer.updatePassword(newHash))
                    .isInstanceOf(InactiveCustomerException.class);
        }

        @Test
        void shouldThrowExceptionWhenNewPasswordIsNull() {
            Customer customer = activeCustomer();

            assertThatThrownBy(() -> customer.updatePassword(null))
                    .isInstanceOf(NullPointerException.class)
                    .hasMessage(CustomerErrorCode.CUSTOMER_VALIDATION_PASSWORD_NULL.getCode());
        }
    }

    @Nested
    class UpdatePersonalInfo {

        @Test
        void shouldUpdatePersonalInfoForActiveCustomer() {
            Customer customer = customerReadyForUpdate();

            customer.updatePersonalInfo("Jane", "Smith", "456 Oak St", "0991234567");

            assertThat(customer.getName()).isEqualTo("Jane");
            assertThat(customer.getLastName()).isEqualTo("Smith");
            assertThat(customer.getAddress()).isEqualTo("456 Oak St");
            assertThat(customer.getPhoneValue()).isEqualTo("0991234567");
        }

        @Test
        void shouldRegisterCustomerUpdatedEvent() {
            Customer customer = activeCustomer();

            customer.updatePersonalInfo("Jane", "Smith", "456 Oak St", "0991234567");

            List<Object> events = customer.getDomainEvents();
            assertThat(events).hasSize(1);
            assertThat(events.get(0)).isInstanceOf(CustomerUpdatedEvent.class);

            CustomerUpdatedEvent event = (CustomerUpdatedEvent) events.get(0);
            assertThat(event.name()).isEqualTo("Jane");
            assertThat(event.lastName()).isEqualTo("Smith");
            assertThat(event.address()).isEqualTo("456 Oak St");
        }

        @Test
        void shouldUpdateTimestampWhenUpdatingPersonalInfo() {
            Customer customer = activeCustomer();
            var originalUpdatedAt = customer.getUpdatedAt();

            customer.updatePersonalInfo("Jane", "Smith", "456 Oak St", "0991234567");

            assertThat(customer.getUpdatedAt()).isAfter(originalUpdatedAt);
        }

        @Test
        void shouldThrowExceptionWhenUpdatingInfoForInactiveCustomer() {
            Customer customer = inactiveCustomer();

            assertThatThrownBy(() -> customer.updatePersonalInfo(
                    "Jane", "Smith", "456 Oak St", "0991234567"
            )).isInstanceOf(InactiveCustomerException.class);
        }

        @Test
        void shouldThrowExceptionWhenNameIsNull() {
            Customer customer = activeCustomer();

            assertThatThrownBy(() -> customer.updatePersonalInfo(
                    null, "Smith", "456 Oak St", "0991234567"
            )).isInstanceOf(IllegalArgumentException.class)
                    .hasMessage(CustomerErrorCode.CUSTOMER_VALIDATION_NAME_EMPTY.getCode());
        }

        @Test
        void shouldThrowExceptionWhenNameIsBlank() {
            Customer customer = activeCustomer();

            assertThatThrownBy(() -> customer.updatePersonalInfo(
                    "   ", "Smith", "456 Oak St", "0991234567"
            )).isInstanceOf(IllegalArgumentException.class)
                    .hasMessage(CustomerErrorCode.CUSTOMER_VALIDATION_NAME_EMPTY.getCode());
        }

        @Test
        void shouldThrowExceptionWhenLastNameIsNull() {
            Customer customer = activeCustomer();

            assertThatThrownBy(() -> customer.updatePersonalInfo(
                    "Jane", null, "456 Oak St", "0991234567"
            )).isInstanceOf(IllegalArgumentException.class)
                    .hasMessage(CustomerErrorCode.CUSTOMER_VALIDATION_LASTNAME_EMPTY.getCode());
        }

        @Test
        void shouldThrowExceptionWhenAddressIsNull() {
            Customer customer = activeCustomer();

            assertThatThrownBy(() -> customer.updatePersonalInfo(
                    "Jane", "Smith", null, "0991234567"
            )).isInstanceOf(IllegalArgumentException.class)
                    .hasMessage(CustomerErrorCode.CUSTOMER_VALIDATION_ADDRESS_EMPTY.getCode());
        }
    }

    @Nested
    class UpdateContactInfo {

        @Test
        void shouldUpdateBothAddressAndPhone() {
            Customer customer = customerReadyForUpdate();

            customer.updateContactInfo("789 Pine Ave", "0999876543");

            assertThat(customer.getAddress()).isEqualTo("789 Pine Ave");
            assertThat(customer.getPhoneValue()).isEqualTo("0999876543");
        }

        @Test
        void shouldUpdateOnlyAddressWhenPhoneIsNull() {
            Customer customer = activeCustomer();
            String originalPhone = customer.getPhoneValue();

            customer.updateContactInfo("789 Pine Ave", null);

            assertThat(customer.getAddress()).isEqualTo("789 Pine Ave");
            assertThat(customer.getPhoneValue()).isEqualTo(originalPhone);
        }

        @Test
        void shouldUpdateOnlyPhoneWhenAddressIsNull() {
            Customer customer = activeCustomer();
            String originalAddress = customer.getAddress();

            customer.updateContactInfo(null, "0999876543");

            assertThat(customer.getAddress()).isEqualTo(originalAddress);
            assertThat(customer.getPhoneValue()).isEqualTo("0999876543");
        }

        @Test
        void shouldNotUpdateWhenBothAreNull() {
            Customer customer = activeCustomer();
            String originalAddress = customer.getAddress();
            String originalPhone = customer.getPhoneValue();

            customer.updateContactInfo(null, null);

            assertThat(customer.getAddress()).isEqualTo(originalAddress);
            assertThat(customer.getPhoneValue()).isEqualTo(originalPhone);
        }

        @Test
        void shouldRegisterCustomerUpdatedEvent() {
            Customer customer = activeCustomer();

            customer.updateContactInfo("789 Pine Ave", "0999876543");

            List<Object> events = customer.getDomainEvents();
            assertThat(events).hasSize(1);
            assertThat(events.get(0)).isInstanceOf(CustomerUpdatedEvent.class);
        }

        @Test
        void shouldThrowExceptionWhenCustomerIsInactive() {
            Customer customer = inactiveCustomer();

            assertThatThrownBy(() -> customer.updateContactInfo("789 Pine Ave", "0999876543"))
                    .isInstanceOf(InactiveCustomerException.class);
        }
    }

    @Nested
    class PersonBehavior {

        @Test
        void shouldCalculateAgeCorrectly() {
            Customer customer = youngCustomer();

            assertThat(customer.getAge()).isEqualTo(20);
        }

        @Test
        void shouldCalculateAgeCorrectionForSenior() {
            Customer customer = seniorCustomer();

            assertThat(customer.getAge()).isEqualTo(70);
        }

        @Test
        void shouldReturnFullName() {
            Customer customer = johnDoe();

            assertThat(customer.getFullName()).isEqualTo("John Doe");
        }

        @Test
        void shouldReturnIdentificationValue() {
            Customer customer = aCustomer()
                    .withIdentification("1234567890")
                    .build();

            assertThat(customer.getIdentificationValue()).isEqualTo("1234567890");
        }

        @Test
        void shouldReturnPhoneValue() {
            Customer customer = aCustomer()
                    .withPhone("0987654321")
                    .build();

            assertThat(customer.getPhoneValue()).isEqualTo("0987654321");
        }
    }

    @Nested
    class DomainEvents {

        @Test
        void shouldClearDomainEvents() {
            Customer customer = newlyCreatedCustomer();

            assertThat(customer.getDomainEvents()).isNotEmpty();

            customer.clearDomainEvents();

            assertThat(customer.getDomainEvents()).isEmpty();
        }

        @Test
        void shouldReturnUnmodifiableListOfEvents() {
            Customer customer = newlyCreatedCustomer();

            List<Object> events = customer.getDomainEvents();

            assertThatThrownBy(() -> events.add(new Object()))
                    .isInstanceOf(UnsupportedOperationException.class);
        }

        @Test
        void shouldHandleNullDomainEventsGracefully() {
            Customer customer = defaultCustomer();

            assertThat(customer.getDomainEvents()).isEmpty();

            customer.clearDomainEvents();

            assertThat(customer.getDomainEvents()).isEmpty();
        }
    }

}