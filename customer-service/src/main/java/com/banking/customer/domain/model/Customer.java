package com.banking.customer.domain.model;

import com.banking.customer.domain.event.CustomerCreatedEvent;
import com.banking.customer.domain.event.CustomerStatusChangedEvent;
import com.banking.customer.domain.event.CustomerUpdatedEvent;
import com.banking.customer.domain.exception.CustomerErrorCode;
import com.banking.customer.domain.exception.InactiveCustomerException;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

@Getter
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Customer extends Person {

    private CustomerId customerId;
    private PasswordHash passwordHash;
    private CustomerStatus status;

    public static Customer create(
            String name,
            String lastName,
            Gender gender,
            LocalDate birthDate,
            String identification,
            String address,
            String phone,
            String customerId,
            String hashedPassword
    ) {
        UUID id = UUID.randomUUID();

        Customer customer = Customer.builder()
                .id(id)
                .name(name)
                .lastName(lastName)
                .gender(gender)
                .birthDate(birthDate)
                .identification(Identification.of(identification))
                .address(address)
                .phone(PhoneNumber.ofNullable(phone))
                .customerId(CustomerId.of(customerId))
                .passwordHash(PasswordHash.fromHash(hashedPassword))
                .status(CustomerStatus.ACTIVE)
                .build();

        customer.markAsCreated();
        customer.validate();

        customer.registerEvent(CustomerCreatedEvent.builder()
                .customerId(id)
                .customerIdValue(customerId)
                .name(name)
                .lastName(lastName)
                .gender(gender)
                .birthDate(birthDate)
                .identification(identification)
                .address(address)
                .phone(phone)
                .status(CustomerStatus.ACTIVE)
                .occurredAt(LocalDateTime.now())
                .build());

        return customer;
    }

    public static Customer reconstitute(
            UUID id,
            String name,
            String lastName,
            Gender gender,
            LocalDate birthDate,
            Identification identification,
            String address,
            PhoneNumber phone,
            CustomerId customerId,
            PasswordHash passwordHash,
            CustomerStatus status,
            Instant createdAt,
            Instant updatedAt
    ) {
        return Customer.builder()
                .id(id)
                .name(name)
                .lastName(lastName)
                .gender(gender)
                .birthDate(birthDate)
                .identification(identification)
                .address(address)
                .phone(phone)
                .customerId(customerId)
                .passwordHash(passwordHash)
                .status(status)
                .createdAt(createdAt)
                .updatedAt(updatedAt)
                .build();
    }

    @Override
    protected void validate() {
        super.validate();

        if (Objects.isNull(customerId)) {
            throw new IllegalArgumentException(CustomerErrorCode.CUSTOMER_VALIDATION_ID_NULL.getCode());
        }

        if (Objects.isNull(passwordHash)) {
            throw new IllegalArgumentException(CustomerErrorCode.CUSTOMER_VALIDATION_PASSWORD_NULL.getCode());
        }
    }

    public boolean isActive() {
        return status == CustomerStatus.ACTIVE;
    }

    public void activate() {
        if (this.status == CustomerStatus.ACTIVE) {
            return;
        }

        this.status = CustomerStatus.ACTIVE;
        markAsUpdated();

        registerEvent(CustomerStatusChangedEvent.builder()
                .customerId(this.getId())
                .customerIdValue(this.customerId.value())
                .newStatus(CustomerStatus.ACTIVE)
                .reason("Activated")
                .occurredAt(LocalDateTime.now())
                .build());
    }

    public void deactivate() {
        if (this.status == CustomerStatus.INACTIVE) {
            return;
        }

        this.status = CustomerStatus.INACTIVE;
        markAsUpdated();

        registerEvent(CustomerStatusChangedEvent.builder()
                .customerId(this.getId())
                .customerIdValue(this.customerId.value())
                .newStatus(CustomerStatus.INACTIVE)
                .reason("Deactivated")
                .occurredAt(LocalDateTime.now())
                .build());
    }

    public void ensureActive() {
        if (!isActive()) {
            throw InactiveCustomerException.withCustomerId(customerId.value());
        }
    }

    public void updatePassword(PasswordHash newPasswordHash) {
        ensureActive();
        this.passwordHash = Objects.requireNonNull(
                newPasswordHash,
                CustomerErrorCode.CUSTOMER_VALIDATION_PASSWORD_NULL.getCode()
        );
        markAsUpdated();
    }

    public void updatePersonalInfo(String name, String lastName, String address, String phone) {
        ensureActive();
        validatePersonalInfo(name, lastName, address);

        this.setName(name);
        this.setLastName(lastName);
        this.setAddress(address);
        this.setPhone(PhoneNumber.of(phone));
        markAsUpdated();

        registerEvent(CustomerUpdatedEvent.builder()
                .customerId(this.getId())
                .customerIdValue(this.customerId.value())
                .name(name)
                .lastName(lastName)
                .address(address)
                .phone(phone)
                .occurredAt(LocalDateTime.now())
                .build());
    }

    public void updateContactInfo(String address, String phone) {
        ensureActive();

        if (Objects.nonNull(address) && !address.isBlank()) {
            this.setAddress(address);
        }

        if (Objects.nonNull(phone) && !phone.isBlank()) {
            this.setPhone(PhoneNumber.of(phone));
        }

        markAsUpdated();

        registerEvent(CustomerUpdatedEvent.builder()
                .customerId(this.getId())
                .customerIdValue(this.customerId.value())
                .name(this.getName())
                .address(this.getAddress())
                .phone(this.getPhoneValue())
                .occurredAt(LocalDateTime.now())
                .build());
    }

    public String getCustomerIdValue() {
        return Objects.nonNull(customerId) ? customerId.value() : null;
    }

    private void validatePersonalInfo(String name, String lastName, String address) {
        if (Objects.isNull(name) || name.isBlank()) {
            throw new IllegalArgumentException(CustomerErrorCode.CUSTOMER_VALIDATION_NAME_EMPTY.getCode());
        }

        if (Objects.isNull(lastName) || lastName.isBlank()) {
            throw new IllegalArgumentException(CustomerErrorCode.CUSTOMER_VALIDATION_LASTNAME_EMPTY.getCode());
        }

        if (Objects.isNull(address) || address.isBlank()) {
            throw new IllegalArgumentException(CustomerErrorCode.CUSTOMER_VALIDATION_ADDRESS_EMPTY.getCode());
        }
    }

}