package com.banking.customer.domain.exception;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Getter
public abstract class CustomerDomainException extends RuntimeException {

    private final CustomerErrorCode errorCode;
    private final Map<String, Object> parameters;

    protected CustomerDomainException(CustomerErrorCode errorCode) {
        this(errorCode, new HashMap<>());
    }

    protected CustomerDomainException(CustomerErrorCode errorCode, Map<String, Object> parameters) {
        super(errorCode.getCode());
        this.errorCode = errorCode;
        this.parameters = Objects.nonNull(parameters) ? new HashMap<>(parameters) : new HashMap<>();
    }

    protected CustomerDomainException(CustomerErrorCode errorCode, Map<String, Object> parameters, Throwable cause) {
        super(errorCode.getCode(), cause);
        this.errorCode = errorCode;
        this.parameters = Objects.nonNull(parameters) ? new HashMap<>(parameters) : new HashMap<>();
    }

}