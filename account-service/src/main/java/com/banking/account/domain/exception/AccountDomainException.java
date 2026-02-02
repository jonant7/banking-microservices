package com.banking.account.domain.exception;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Getter
public abstract class AccountDomainException extends RuntimeException {

    private final AccountErrorCode errorCode;
    private final Map<String, Object> parameters;

    protected AccountDomainException(AccountErrorCode errorCode) {
        this(errorCode, new HashMap<>());
    }

    protected AccountDomainException(AccountErrorCode errorCode, Map<String, Object> parameters) {
        super(errorCode.getCode());
        this.errorCode = errorCode;
        this.parameters = Objects.nonNull(parameters) ? new HashMap<>(parameters) : new HashMap<>();
    }

    protected AccountDomainException(AccountErrorCode errorCode, Map<String, Object> parameters, Throwable cause) {
        super(errorCode.getCode(), cause);
        this.errorCode = errorCode;
        this.parameters = Objects.nonNull(parameters) ? new HashMap<>(parameters) : new HashMap<>();
    }

}