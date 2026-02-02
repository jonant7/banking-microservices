package com.banking.account.domain.exception;

import lombok.Getter;

@Getter
public enum AccountErrorCode {

    ACCOUNT_VALIDATION_NUMBER_NULL("error.account.validation.number.null"),
    ACCOUNT_VALIDATION_NUMBER_EMPTY("error.account.validation.number.empty"),
    ACCOUNT_VALIDATION_NUMBER_TOO_LONG("error.account.validation.number.too.long"),
    ACCOUNT_VALIDATION_NUMBER_INVALID_FORMAT("error.account.validation.number.invalid.format"),

    ACCOUNT_VALIDATION_TYPE_NULL("error.account.validation.type.null"),
    ACCOUNT_VALIDATION_INITIAL_BALANCE_NULL("error.account.validation.initial.balance.null"),
    ACCOUNT_VALIDATION_CURRENT_BALANCE_NULL("error.account.validation.current.balance.null"),
    ACCOUNT_VALIDATION_CUSTOMER_ID_NULL("error.account.validation.customer.id.null"),
    ACCOUNT_VALIDATION_INITIAL_BALANCE_NEGATIVE("error.account.validation.initial.balance.negative"),

    ACCOUNT_VALIDATION_MONEY_VALUE_NULL("error.account.validation.money.value.null"),
    ACCOUNT_VALIDATION_MONEY_CURRENCY_NULL("error.account.validation.money.currency.null"),
    ACCOUNT_VALIDATION_MONEY_CURRENCY_MISMATCH("error.account.validation.money.currency.mismatch"),

    TRANSACTION_VALIDATION_TYPE_NULL("error.transaction.validation.type.null"),
    TRANSACTION_VALIDATION_AMOUNT_NULL("error.transaction.validation.amount.null"),
    TRANSACTION_VALIDATION_AMOUNT_POSITIVE("error.transaction.validation.amount.positive"),
    TRANSACTION_VALIDATION_BALANCE_BEFORE_NULL("error.transaction.validation.balance.before.null"),
    TRANSACTION_VALIDATION_BALANCE_AFTER_NULL("error.transaction.validation.balance.after.null"),
    TRANSACTION_VALIDATION_ACCOUNT_ID_NULL("error.transaction.validation.account.id.null"),
    TRANSACTION_VALIDATION_BALANCE_INCONSISTENT("error.transaction.validation.balance.inconsistent"),

    ACCOUNT_BUSINESS_NOT_FOUND("error.account.business.not.found"),
    ACCOUNT_BUSINESS_INACTIVE("error.account.business.inactive"),
    CUSTOMER_BUSINESS_INACTIVE("error.customer.business.inactive"),
    ACCOUNT_BUSINESS_INSUFFICIENT_BALANCE("error.account.business.insufficient.balance"),
    ACCOUNT_BUSINESS_INVALID_TRANSACTION("error.account.business.transaction.invalid"),

    ACCOUNT_BUSINESS_INVALID_STATUS_TRANSITION("error.account.business.invalid.status.transition"),
    ACCOUNT_BUSINESS_CLOSE_WITH_BALANCE("error.account.business.close.with.balance");

    private final String code;

    AccountErrorCode(String code) {
        this.code = code;
    }

    @Override
    public String toString() {
        return code;
    }

}