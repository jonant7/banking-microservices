package com.banking.customer.fixtures.mothers;

import com.banking.customer.domain.model.Customer;
import com.banking.customer.domain.model.Gender;
import com.banking.customer.fixtures.builders.CustomerBuilder;

import java.time.LocalDate;

public class CustomerMother {

    public static Customer defaultCustomer() {
        return CustomerBuilder.aCustomer().build();
    }

    public static Customer activeCustomer() {
        return CustomerBuilder.aCustomer()
                .active()
                .build();
    }

    public static Customer inactiveCustomer() {
        return CustomerBuilder.aCustomer()
                .inactive()
                .build();
    }

    public static Customer newlyCreatedCustomer() {
        return Customer.create(
                "John",
                "Doe",
                Gender.MALE,
                LocalDate.now().minusYears(30),
                "1234567890",
                "123 Main Street",
                "0987654321",
                "CUST001",
                "$2a$10$validhash"
        );
    }

    public static Customer youngCustomer() {
        return CustomerBuilder.aCustomer()
                .withAge(20)
                .withCustomerId("CUST_YOUNG")
                .build();
    }

    public static Customer seniorCustomer() {
        return CustomerBuilder.aCustomer()
                .withAge(70)
                .withCustomerId("CUST_SENIOR")
                .build();
    }

    public static Customer customerWithoutPhone() {
        return CustomerBuilder.aCustomer()
                .withPhone(null)
                .build();
    }

    public static Customer johnDoe() {
        return CustomerBuilder.aCustomer()
                .withName("John")
                .withLastName("Doe")
                .withCustomerId("john.doe")
                .withIdentification("1111111111")
                .build();
    }

    public static Customer customerReadyForUpdate() {
        return CustomerBuilder.aCustomer()
                .active()
                .withName("ToUpdate")
                .withLastName("Customer")
                .withAddress("Old Address")
                .withPhone("1234567890")
                .build();
    }

}