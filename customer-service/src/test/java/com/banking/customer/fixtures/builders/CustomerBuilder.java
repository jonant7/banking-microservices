package com.banking.customer.fixtures.builders;

import com.banking.customer.domain.model.*;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public class CustomerBuilder {

    private UUID id = UUID.randomUUID();
    private String name = "John";
    private String lastName = "Doe";
    private Gender gender = Gender.MALE;
    private LocalDate birthDate = LocalDate.now().minusYears(30);
    private String identification = "1234567890";
    private String address = "123 Main Street";
    private String phone = "0987654321";
    private String customerId = "CUST001";
    private String passwordHash = "$2a$10$abcdefghijklmnopqrstuvwxyz123456";
    private CustomerStatus status = CustomerStatus.ACTIVE;
    private Instant createdAt = Instant.now();
    private Instant updatedAt = Instant.now();

    public static CustomerBuilder aCustomer() {
        return new CustomerBuilder();
    }

    public CustomerBuilder withId(UUID id) {
        this.id = id;
        return this;
    }

    public CustomerBuilder withName(String name) {
        this.name = name;
        return this;
    }

    public CustomerBuilder withLastName(String lastName) {
        this.lastName = lastName;
        return this;
    }

    public CustomerBuilder withGender(Gender gender) {
        this.gender = gender;
        return this;
    }

    public CustomerBuilder withBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
        return this;
    }

    public CustomerBuilder withAge(int years) {
        this.birthDate = LocalDate.now().minusYears(years);
        return this;
    }

    public CustomerBuilder withIdentification(String identification) {
        this.identification = identification;
        return this;
    }

    public CustomerBuilder withAddress(String address) {
        this.address = address;
        return this;
    }

    public CustomerBuilder withPhone(String phone) {
        this.phone = phone;
        return this;
    }

    public CustomerBuilder withCustomerId(String customerId) {
        this.customerId = customerId;
        return this;
    }

    public CustomerBuilder withPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
        return this;
    }

    public CustomerBuilder active() {
        this.status = CustomerStatus.ACTIVE;
        return this;
    }

    public CustomerBuilder inactive() {
        this.status = CustomerStatus.INACTIVE;
        return this;
    }

    public CustomerBuilder withStatus(CustomerStatus status) {
        this.status = status;
        return this;
    }

    public CustomerBuilder withCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
        return this;
    }

    public CustomerBuilder withUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
        return this;
    }

    public CustomerBuilder female() {
        this.gender = Gender.FEMALE;
        this.name = "Jane";
        return this;
    }

    public CustomerBuilder male() {
        this.gender = Gender.MALE;
        this.name = "John";
        return this;
    }

    public CustomerBuilder minor() {
        this.birthDate = LocalDate.now().minusYears(15);
        return this;
    }

    public Customer build() {
        Customer customer = Customer.reconstitute(
                id,
                name,
                lastName,
                gender,
                birthDate,
                Identification.of(identification),
                address,
                PhoneNumber.ofNullable(phone),
                CustomerId.of(customerId),
                PasswordHash.fromHash(passwordHash),
                status,
                createdAt,
                updatedAt
        );
        customer.clearDomainEvents();
        return customer;
    }

}