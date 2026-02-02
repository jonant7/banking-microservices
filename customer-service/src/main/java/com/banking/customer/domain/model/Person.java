package com.banking.customer.domain.model;

import com.banking.customer.domain.exception.CustomerErrorCode;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;
import java.time.Period;
import java.util.Objects;

@Getter
@Setter(AccessLevel.PROTECTED)
@SuperBuilder
public abstract class Person extends AggregateRoot {

    private String name;
    private String lastName;
    private Gender gender;
    private LocalDate birthDate;
    private Identification identification;
    private String address;
    private PhoneNumber phone;

    private static final int MINIMUM_AGE = 18;

    protected Person() {
        super();
    }

    public int getAge() {
        if (Objects.isNull(birthDate)) {
            return 0;
        }
        return Period.between(birthDate, LocalDate.now()).getYears();
    }

    public String getFullName() {
        return String.format("%s %s", name, lastName);
    }

    public String getIdentificationValue() {
        return Objects.nonNull(identification) ? identification.value() : null;
    }

    public String getPhoneValue() {
        return Objects.nonNull(phone) ? phone.value() : null;
    }

    protected void validate() {
        if (Objects.isNull(name) || name.isBlank()) {
            throw new IllegalArgumentException(CustomerErrorCode.CUSTOMER_VALIDATION_NAME_NULL.getCode());
        }

        if (Objects.isNull(lastName) || lastName.isBlank()) {
            throw new IllegalArgumentException(CustomerErrorCode.CUSTOMER_VALIDATION_LASTNAME_NULL.getCode());
        }

        if (Objects.isNull(gender)) {
            throw new IllegalArgumentException(CustomerErrorCode.CUSTOMER_VALIDATION_GENDER_NULL.getCode());
        }

        if (Objects.isNull(birthDate)) {
            throw new IllegalArgumentException(CustomerErrorCode.CUSTOMER_VALIDATION_BIRTHDATE_NULL.getCode());
        }

        if (birthDate.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException(CustomerErrorCode.CUSTOMER_VALIDATION_BIRTHDATE_FUTURE.getCode());
        }

        if (getAge() < MINIMUM_AGE) {
            throw new IllegalArgumentException(CustomerErrorCode.CUSTOMER_VALIDATION_AGE_UNDERAGE.getCode());
        }

        if (Objects.isNull(identification)) {
            throw new IllegalArgumentException(CustomerErrorCode.CUSTOMER_VALIDATION_IDENTIFICATION_NULL.getCode());
        }
    }

}