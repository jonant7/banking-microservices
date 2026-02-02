package com.banking.account.domain.exception;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class AccountDomainExceptionTest {

    @Nested
    class AccountNotFoundExceptionTest {

        @Test
        void shouldCreateExceptionWithId() {
            UUID id = UUID.randomUUID();

            AccountNotFoundException exception = AccountNotFoundException.withId(id);

            assertThat(exception.getErrorCode()).isEqualTo(AccountErrorCode.ACCOUNT_BUSINESS_NOT_FOUND);
            assertThat(exception.getParameters()).containsEntry("field", "id");
            assertThat(exception.getParameters()).containsEntry("value", id.toString());
        }

        @Test
        void shouldCreateExceptionWithAccountNumber() {
            String accountNumber = "1234567890";

            AccountNotFoundException exception = AccountNotFoundException.withAccountNumber(accountNumber);

            assertThat(exception.getErrorCode()).isEqualTo(AccountErrorCode.ACCOUNT_BUSINESS_NOT_FOUND);
            assertThat(exception.getParameters()).containsEntry("field", "accountNumber");
            assertThat(exception.getParameters()).containsEntry("value", accountNumber);
        }

        @Test
        void shouldCreateExceptionWithCustomerId() {
            String customerId = "CUST001";

            AccountNotFoundException exception = AccountNotFoundException.withCustomerId(customerId);

            assertThat(exception.getErrorCode()).isEqualTo(AccountErrorCode.ACCOUNT_BUSINESS_NOT_FOUND);
            assertThat(exception.getParameters()).containsEntry("field", "customerId");
            assertThat(exception.getParameters()).containsEntry("value", customerId);
        }

        @Test
        void shouldHaveTwoParameters() {
            AccountNotFoundException exception = AccountNotFoundException.withAccountNumber("123456");

            assertThat(exception.getParameters()).hasSize(2);
            assertThat(exception.getParameters().keySet()).containsExactlyInAnyOrder("field", "value");
        }
    }

    @Nested
    class InactiveAccountExceptionTest {

        @Test
        void shouldCreateExceptionWithAccountNumber() {
            String accountNumber = "1234567890";

            InactiveAccountException exception = InactiveAccountException.withAccountNumber(accountNumber);

            assertThat(exception.getErrorCode()).isEqualTo(AccountErrorCode.ACCOUNT_BUSINESS_INACTIVE);
            assertThat(exception.getParameters()).containsEntry("accountNumber", accountNumber);
        }

        @Test
        void shouldHaveCorrectErrorMessage() {
            InactiveAccountException exception = InactiveAccountException.withAccountNumber("123456");

            assertThat(exception.getMessage()).isEqualTo(AccountErrorCode.ACCOUNT_BUSINESS_INACTIVE.getCode());
        }

        @Test
        void shouldConvertParametersKeysToArray() {
            InactiveAccountException exception = InactiveAccountException.withAccountNumber("123456");

            Object[] paramKeys = exception.getParameters().keySet().toArray(new String[0]);

            assertThat(paramKeys).hasSize(1);
            assertThat(paramKeys[0]).isEqualTo("accountNumber");
        }

        @Test
        void shouldConvertParametersValuesToArray() {
            InactiveAccountException exception = InactiveAccountException.withAccountNumber("123456");

            Object[] paramValues = exception.getParameters().values().toArray(new String[0]);

            assertThat(paramValues).hasSize(1);
            assertThat(paramValues[0]).isEqualTo("123456");
        }

        @Test
        void shouldAccessParameterByKey() {
            InactiveAccountException exception = InactiveAccountException.withAccountNumber("123456");

            assertThat(exception.getParameters().get("accountNumber")).isEqualTo("123456");
        }
    }

    @Nested
    class InactiveCustomerExceptionTest {

        @Test
        void shouldCreateExceptionForNotFound() {
            UUID customerId = UUID.randomUUID();

            InactiveCustomerException exception = InactiveCustomerException.notFound(customerId);

            assertThat(exception.getErrorCode()).isEqualTo(AccountErrorCode.CUSTOMER_BUSINESS_INACTIVE);
            assertThat(exception.getParameters()).containsEntry("customerId", customerId.toString());
            assertThat(exception.getParameters()).containsEntry("status", "NOT_FOUND");
        }

        @Test
        void shouldCreateExceptionForInactive() {
            UUID customerId = UUID.randomUUID();

            InactiveCustomerException exception = InactiveCustomerException.inactive(customerId);

            assertThat(exception.getErrorCode()).isEqualTo(AccountErrorCode.CUSTOMER_BUSINESS_INACTIVE);
            assertThat(exception.getParameters()).containsEntry("customerId", customerId.toString());
            assertThat(exception.getParameters()).containsEntry("status", "INACTIVE");
        }
    }

    @Nested
    class InsufficientBalanceExceptionTest {

        @Test
        void shouldCreateExceptionWithDetails() {
            String accountNumber = "123456";
            BigDecimal currentBalance = new BigDecimal("100.00");
            BigDecimal requiredAmount = new BigDecimal("150.00");

            InsufficientBalanceException exception = InsufficientBalanceException.withDetails(
                    accountNumber, currentBalance, requiredAmount
            );

            assertThat(exception.getErrorCode()).isEqualTo(AccountErrorCode.ACCOUNT_BUSINESS_INSUFFICIENT_BALANCE);
            assertThat(exception.getParameters()).containsEntry("accountNumber", accountNumber);
            assertThat(exception.getParameters()).containsEntry("currentBalance", "100.00");
            assertThat(exception.getParameters()).containsEntry("requiredAmount", "150.00");
        }

        @Test
        void shouldHaveThreeParameters() {
            InsufficientBalanceException exception = InsufficientBalanceException.withDetails(
                    "123456", new BigDecimal("100.00"), new BigDecimal("150.00")
            );

            assertThat(exception.getParameters()).hasSize(3);
        }
    }

    @Nested
    class InvalidTransactionExceptionTest {

        @Test
        void shouldCreateExceptionWithReason() {
            String reason = "Amount must be positive";

            InvalidTransactionException exception = InvalidTransactionException.withReason(reason);

            assertThat(exception.getErrorCode()).isEqualTo(AccountErrorCode.ACCOUNT_BUSINESS_INVALID_TRANSACTION);
            assertThat(exception.getParameters()).containsEntry("reason", reason);
        }
    }

    @Nested
    class AccountErrorCodeTest {

        @Test
        void shouldHaveCorrectCodeForValidationErrors() {
            assertThat(AccountErrorCode.ACCOUNT_VALIDATION_NUMBER_EMPTY.getCode())
                    .isEqualTo("error.account.validation.number.empty");
            assertThat(AccountErrorCode.ACCOUNT_VALIDATION_TYPE_NULL.getCode())
                    .isEqualTo("error.account.validation.type.null");
        }

        @Test
        void shouldHaveCorrectCodeForBusinessErrors() {
            assertThat(AccountErrorCode.ACCOUNT_BUSINESS_NOT_FOUND.getCode())
                    .isEqualTo("error.account.business.not.found");
            assertThat(AccountErrorCode.ACCOUNT_BUSINESS_INACTIVE.getCode())
                    .isEqualTo("error.account.business.inactive");
        }

        @Test
        void shouldHaveCorrectCodeForTransactionValidationErrors() {
            assertThat(AccountErrorCode.TRANSACTION_VALIDATION_TYPE_NULL.getCode())
                    .isEqualTo("error.transaction.validation.type.null");
            assertThat(AccountErrorCode.TRANSACTION_VALIDATION_AMOUNT_NULL.getCode())
                    .isEqualTo("error.transaction.validation.amount.null");
        }

        @Test
        void shouldConvertToStringCorrectly() {
            String code = AccountErrorCode.ACCOUNT_VALIDATION_NUMBER_EMPTY.toString();

            assertThat(code).isEqualTo("error.account.validation.number.empty");
        }
    }

    @Nested
    class AccountDomainExceptionBaseTest {

        @Test
        void shouldCreateExceptionWithErrorCodeOnly() {
            AccountDomainException exception = new TestException(
                    AccountErrorCode.ACCOUNT_VALIDATION_NUMBER_EMPTY
            );

            assertThat(exception.getErrorCode()).isEqualTo(AccountErrorCode.ACCOUNT_VALIDATION_NUMBER_EMPTY);
            assertThat(exception.getParameters()).isEmpty();
        }

        @Test
        void shouldCreateExceptionWithParameters() {
            Map<String, Object> params = Map.of("field", "accountNumber", "value", "123456");

            AccountDomainException exception = new TestException(
                    AccountErrorCode.ACCOUNT_VALIDATION_NUMBER_EMPTY,
                    params
            );

            assertThat(exception.getParameters()).containsEntry("field", "accountNumber");
            assertThat(exception.getParameters()).containsEntry("value", "123456");
        }

        @Test
        void shouldHandleNullParametersGracefully() {
            AccountDomainException exception = new TestException(
                    AccountErrorCode.ACCOUNT_VALIDATION_NUMBER_EMPTY,
                    null
            );

            assertThat(exception.getParameters()).isNotNull();
            assertThat(exception.getParameters()).isEmpty();
        }

        @Test
        void shouldCreateExceptionWithCause() {
            Throwable cause = new RuntimeException("Root cause");

            AccountDomainException exception = new TestException(
                    AccountErrorCode.ACCOUNT_BUSINESS_INVALID_TRANSACTION,
                    Map.of("reason", "invalid amount"),
                    cause
            );

            assertThat(exception.getCause()).isEqualTo(cause);
            assertThat(exception.getParameters()).containsEntry("reason", "invalid amount");
        }

        @Test
        void shouldCreateImmutableCopyOfParameters() {
            Map<String, Object> originalParams = new java.util.HashMap<>();
            originalParams.put("key", "value");

            AccountDomainException exception = new TestException(
                    AccountErrorCode.ACCOUNT_VALIDATION_NUMBER_EMPTY,
                    originalParams
            );

            originalParams.put("newKey", "newValue");

            assertThat(exception.getParameters()).hasSize(1);
            assertThat(exception.getParameters()).doesNotContainKey("newKey");
        }

        @Test
        void shouldReturnErrorCodeInMessage() {
            AccountDomainException exception = new TestException(
                    AccountErrorCode.ACCOUNT_BUSINESS_NOT_FOUND
            );

            assertThat(exception.getMessage())
                    .isEqualTo(AccountErrorCode.ACCOUNT_BUSINESS_NOT_FOUND.getCode());
        }

        private static class TestException extends AccountDomainException {
            protected TestException(AccountErrorCode errorCode) {
                super(errorCode);
            }

            protected TestException(AccountErrorCode errorCode, Map<String, Object> parameters) {
                super(errorCode, parameters);
            }

            protected TestException(AccountErrorCode errorCode, Map<String, Object> parameters, Throwable cause) {
                super(errorCode, parameters, cause);
            }
        }
    }
}