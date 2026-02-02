package com.banking.account.domain.model;

import com.banking.account.domain.exception.AccountErrorCode;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

public record Money(BigDecimal value, String currency) {

    private static final int SCALE = 2;
    private static final RoundingMode ROUNDING_MODE = RoundingMode.HALF_UP;
    private static final String DEFAULT_CURRENCY = "USD";

    public Money {
        Objects.requireNonNull(value, AccountErrorCode.ACCOUNT_VALIDATION_MONEY_VALUE_NULL.getCode());
        Objects.requireNonNull(currency, AccountErrorCode.ACCOUNT_VALIDATION_MONEY_CURRENCY_NULL.getCode());

        value = value.setScale(SCALE, ROUNDING_MODE);
    }

    public static Money of(BigDecimal value) {
        return of(value, DEFAULT_CURRENCY);
    }

    public static Money of(BigDecimal value, String currency) {
        return new Money(value, currency);
    }

    public static Money zero() {
        return new Money(BigDecimal.ZERO, DEFAULT_CURRENCY);
    }

    public Money add(Money other) {
        Objects.requireNonNull(other, "Cannot add null money");
        ensureSameCurrency(other);
        return new Money(this.value.add(other.value), this.currency);
    }

    public Money subtract(Money other) {
        Objects.requireNonNull(other, "Cannot subtract null money");
        ensureSameCurrency(other);
        return new Money(this.value.subtract(other.value), this.currency);
    }

    private void ensureSameCurrency(Money other) {
        if (!this.currency.equals(other.currency)) {
            throw new IllegalArgumentException(
                    AccountErrorCode.ACCOUNT_VALIDATION_MONEY_CURRENCY_MISMATCH.getCode()
            );
        }
    }

    public boolean isPositive() {
        return this.value.compareTo(BigDecimal.ZERO) > 0;
    }

    public boolean isNegative() {
        return this.value.compareTo(BigDecimal.ZERO) < 0;
    }

    public boolean isZero() {
        return this.value.compareTo(BigDecimal.ZERO) == 0;
    }

    public boolean isNegativeOrZero() {
        return this.value.compareTo(BigDecimal.ZERO) <= 0;
    }

    public boolean isLessThan(Money other) {
        Objects.requireNonNull(other, "Cannot compare with null money");
        ensureSameCurrency(other);
        return this.value.compareTo(other.value) < 0;
    }

    public boolean isGreaterThan(Money other) {
        Objects.requireNonNull(other, "Cannot compare with null money");
        ensureSameCurrency(other);
        return this.value.compareTo(other.value) > 0;
    }

    @Override
    public String toString() {
        return String.format("%s %s", currency, value.toPlainString());
    }

}