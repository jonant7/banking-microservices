package com.banking.customer.domain.exception;

public enum CustomerErrorCode {

    CUSTOMER_VALIDATION_ID_NULL("error.customer.validation.id.null"),
    CUSTOMER_VALIDATION_ID_EMPTY("error.customer.validation.id.empty"),
    CUSTOMER_VALIDATION_ID_TOO_LONG("error.customer.validation.id.too.long"),

    CUSTOMER_VALIDATION_PASSWORD_NULL("error.customer.validation.password.null"),
    CUSTOMER_VALIDATION_PASSWORD_EMPTY("error.customer.validation.password.empty"),

    CUSTOMER_VALIDATION_NAME_NULL("error.customer.validation.name.null"),
    CUSTOMER_VALIDATION_NAME_EMPTY("error.customer.validation.name.empty"),

    CUSTOMER_VALIDATION_LASTNAME_NULL("error.customer.validation.lastname.null"),
    CUSTOMER_VALIDATION_LASTNAME_EMPTY("error.customer.validation.lastname.empty"),

    CUSTOMER_VALIDATION_ADDRESS_NULL("error.customer.validation.address.null"),
    CUSTOMER_VALIDATION_ADDRESS_EMPTY("error.customer.validation.address.empty"),

    CUSTOMER_VALIDATION_GENDER_NULL("error.customer.validation.gender.null"),

    CUSTOMER_VALIDATION_BIRTHDATE_NULL("error.customer.validation.birthdate.null"),
    CUSTOMER_VALIDATION_BIRTHDATE_FUTURE("error.customer.validation.birthdate.future"),

    CUSTOMER_VALIDATION_AGE_UNDERAGE("error.customer.validation.age.underage"),

    CUSTOMER_VALIDATION_IDENTIFICATION_NULL("error.customer.validation.identification.null"),
    CUSTOMER_VALIDATION_IDENTIFICATION_INVALID_FORMAT("error.customer.validation.identification.invalid.format"),

    CUSTOMER_VALIDATION_PHONE_NULL("error.customer.validation.phone.null"),
    CUSTOMER_VALIDATION_PHONE_EMPTY("error.customer.validation.phone.empty"),
    CUSTOMER_VALIDATION_PHONE_INVALID_FORMAT("error.customer.validation.phone.invalid.format"),

    CUSTOMER_BUSINESS_NOT_FOUND("error.customer.business.not.found"),

    CUSTOMER_BUSINESS_DUPLICATE_ID("error.customer.business.duplicate.id"),
    CUSTOMER_BUSINESS_DUPLICATE_IDENTIFICATION("error.customer.business.duplicate.identification"),

    CUSTOMER_BUSINESS_INACTIVE("error.customer.business.inactive"),
    CUSTOMER_BUSINESS_ALREADY_ACTIVE("error.customer.business.already.active"),
    CUSTOMER_BUSINESS_ALREADY_INACTIVE("error.customer.business.already.inactive"),

    CUSTOMER_OPERATION_UPDATE_FAILED("error.customer.operation.update.failed"),
    CUSTOMER_OPERATION_DELETE_FAILED("error.customer.operation.delete.failed"),
    CUSTOMER_OPERATION_CREATE_FAILED("error.customer.operation.create.failed");

    private final String code;

    CustomerErrorCode(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    @Override
    public String toString() {
        return code;
    }

}