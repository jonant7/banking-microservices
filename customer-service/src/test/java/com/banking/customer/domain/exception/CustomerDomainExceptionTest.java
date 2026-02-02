package com.banking.customer.domain.exception;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class CustomerDomainExceptionTest {

    @Nested
    class CustomerNotFoundExceptionTest {

        @Test
        void shouldCreateExceptionWithId() {
            UUID id = UUID.randomUUID();

            CustomerNotFoundException exception = CustomerNotFoundException.withId(id);

            assertThat(exception.getErrorCode()).isEqualTo(CustomerErrorCode.CUSTOMER_BUSINESS_NOT_FOUND);
            assertThat(exception.getParameters()).containsEntry("field", "id");
            assertThat(exception.getParameters()).containsEntry("value", id.toString());
        }

        @Test
        void shouldCreateExceptionWithCustomerId() {
            String customerId = "CUST001";

            CustomerNotFoundException exception = CustomerNotFoundException.withCustomerId(customerId);

            assertThat(exception.getErrorCode()).isEqualTo(CustomerErrorCode.CUSTOMER_BUSINESS_NOT_FOUND);
            assertThat(exception.getParameters()).containsEntry("field", "customerId");
            assertThat(exception.getParameters()).containsEntry("value", customerId);
        }

        @Test
        void shouldCreateExceptionWithIdentification() {
            String identification = "1234567890";

            CustomerNotFoundException exception = CustomerNotFoundException.withIdentification(identification);

            assertThat(exception.getErrorCode()).isEqualTo(CustomerErrorCode.CUSTOMER_BUSINESS_NOT_FOUND);
            assertThat(exception.getParameters()).containsEntry("field", "identification");
            assertThat(exception.getParameters()).containsEntry("value", identification);
        }

        @Test
        void shouldHaveTwoParameters() {
            CustomerNotFoundException exception = CustomerNotFoundException.withCustomerId("CUST001");

            assertThat(exception.getParameters()).hasSize(2);
            assertThat(exception.getParameters().keySet()).containsExactlyInAnyOrder("field", "value");
        }
    }

    @Nested
    class DuplicateCustomerExceptionTest {

        @Test
        void shouldCreateExceptionWithCustomerId() {
            String customerId = "CUST001";

            DuplicateCustomerException exception = DuplicateCustomerException.withCustomerId(customerId);

            assertThat(exception.getErrorCode()).isEqualTo(CustomerErrorCode.CUSTOMER_BUSINESS_DUPLICATE_ID);
            assertThat(exception.getParameters()).containsEntry("value", customerId);
        }

        @Test
        void shouldCreateExceptionWithIdentification() {
            String identification = "1234567890";

            DuplicateCustomerException exception = DuplicateCustomerException.withIdentification(identification);

            assertThat(exception.getErrorCode()).isEqualTo(CustomerErrorCode.CUSTOMER_BUSINESS_DUPLICATE_IDENTIFICATION);
            assertThat(exception.getParameters()).containsEntry("value", identification);
        }

        @Test
        void shouldHaveCorrectErrorMessage() {
            DuplicateCustomerException exception = DuplicateCustomerException.withCustomerId("CUST001");

            assertThat(exception.getMessage()).isEqualTo(CustomerErrorCode.CUSTOMER_BUSINESS_DUPLICATE_ID.getCode());
        }
    }

    @Nested
    class InactiveCustomerExceptionTest {

        @Test
        void shouldCreateExceptionWithCustomerId() {
            String customerId = "CUST001";

            InactiveCustomerException exception = InactiveCustomerException.withCustomerId(customerId);

            assertThat(exception.getErrorCode()).isEqualTo(CustomerErrorCode.CUSTOMER_BUSINESS_INACTIVE);
            assertThat(exception.getParameters()).containsEntry("customerId", customerId);
        }

        @Test
        void shouldHaveCorrectErrorMessage() {
            InactiveCustomerException exception = InactiveCustomerException.withCustomerId("CUST001");

            assertThat(exception.getMessage()).isEqualTo(CustomerErrorCode.CUSTOMER_BUSINESS_INACTIVE.getCode());
        }

        @Test
        void shouldConvertParametersKeysToArray() {
            InactiveCustomerException exception = InactiveCustomerException.withCustomerId("CUST001");

            Object[] paramKeys = exception.getParameters().keySet().toArray(new String[0]);

            assertThat(paramKeys).hasSize(1);
            assertThat(paramKeys[0]).isEqualTo("customerId");
        }

        @Test
        void shouldConvertParametersValuesToArray() {
            InactiveCustomerException exception = InactiveCustomerException.withCustomerId("CUST001");

            Object[] paramValues = exception.getParameters().values().toArray(new String[0]);

            assertThat(paramValues).hasSize(1);
            assertThat(paramValues[0]).isEqualTo("CUST001");
        }

        @Test
        void shouldAccessParameterByKey() {
            InactiveCustomerException exception = InactiveCustomerException.withCustomerId("CUST001");

            assertThat(exception.getParameters().get("customerId")).isEqualTo("CUST001");
        }
    }

    @Nested
    class CustomerErrorCodeTest {

        @Test
        void shouldHaveCorrectCodeForValidationErrors() {
            assertThat(CustomerErrorCode.CUSTOMER_VALIDATION_ID_NULL.getCode())
                    .isEqualTo("error.customer.validation.id.null");
            assertThat(CustomerErrorCode.CUSTOMER_VALIDATION_NAME_NULL.getCode())
                    .isEqualTo("error.customer.validation.name.null");
        }

        @Test
        void shouldHaveCorrectCodeForBusinessErrors() {
            assertThat(CustomerErrorCode.CUSTOMER_BUSINESS_NOT_FOUND.getCode())
                    .isEqualTo("error.customer.business.not.found");
            assertThat(CustomerErrorCode.CUSTOMER_BUSINESS_INACTIVE.getCode())
                    .isEqualTo("error.customer.business.inactive");
        }

        @Test
        void shouldHaveCorrectCodeForOperationErrors() {
            assertThat(CustomerErrorCode.CUSTOMER_OPERATION_UPDATE_FAILED.getCode())
                    .isEqualTo("error.customer.operation.update.failed");
        }

        @Test
        void shouldConvertToStringCorrectly() {
            String code = CustomerErrorCode.CUSTOMER_VALIDATION_ID_NULL.toString();

            assertThat(code).isEqualTo("error.customer.validation.id.null");
        }
    }

    @Nested
    class CustomerDomainExceptionBaseTest {

        @Test
        void shouldCreateExceptionWithErrorCodeOnly() {
            CustomerDomainException exception = new TestException(
                    CustomerErrorCode.CUSTOMER_VALIDATION_ID_NULL
            );

            assertThat(exception.getErrorCode()).isEqualTo(CustomerErrorCode.CUSTOMER_VALIDATION_ID_NULL);
            assertThat(exception.getParameters()).isEmpty();
        }

        @Test
        void shouldCreateExceptionWithParameters() {
            Map<String, Object> params = Map.of("field", "name", "value", "John");

            CustomerDomainException exception = new TestException(
                    CustomerErrorCode.CUSTOMER_VALIDATION_NAME_NULL,
                    params
            );

            assertThat(exception.getParameters()).containsEntry("field", "name");
            assertThat(exception.getParameters()).containsEntry("value", "John");
        }

        @Test
        void shouldHandleNullParametersGracefully() {
            CustomerDomainException exception = new TestException(
                    CustomerErrorCode.CUSTOMER_VALIDATION_ID_NULL,
                    null
            );

            assertThat(exception.getParameters()).isNotNull();
            assertThat(exception.getParameters()).isEmpty();
        }

        @Test
        void shouldCreateExceptionWithCause() {
            Throwable cause = new RuntimeException("Root cause");

            CustomerDomainException exception = new TestException(
                    CustomerErrorCode.CUSTOMER_OPERATION_UPDATE_FAILED,
                    Map.of("reason", "database error"),
                    cause
            );

            assertThat(exception.getCause()).isEqualTo(cause);
            assertThat(exception.getParameters()).containsEntry("reason", "database error");
        }

        @Test
        void shouldCreateExceptionWithCauseAndNullParameters() {
            Throwable cause = new RuntimeException("Root cause");

            CustomerDomainException exception = new TestException(
                    CustomerErrorCode.CUSTOMER_OPERATION_UPDATE_FAILED,
                    null,
                    cause
            );

            assertThat(exception.getCause()).isEqualTo(cause);
            assertThat(exception.getParameters()).isNotNull();
            assertThat(exception.getParameters()).isEmpty();
        }

        @Test
        void shouldCreateImmutableCopyOfParameters() {
            Map<String, Object> originalParams = new java.util.HashMap<>();
            originalParams.put("key", "value");

            CustomerDomainException exception = new TestException(
                    CustomerErrorCode.CUSTOMER_VALIDATION_ID_NULL,
                    originalParams
            );

            originalParams.put("newKey", "newValue");

            assertThat(exception.getParameters()).hasSize(1);
            assertThat(exception.getParameters()).doesNotContainKey("newKey");
        }

        @Test
        void shouldReturnErrorCodeInMessage() {
            CustomerDomainException exception = new TestException(
                    CustomerErrorCode.CUSTOMER_BUSINESS_NOT_FOUND
            );

            assertThat(exception.getMessage())
                    .isEqualTo(CustomerErrorCode.CUSTOMER_BUSINESS_NOT_FOUND.getCode());
        }

        private static class TestException extends CustomerDomainException {
            protected TestException(CustomerErrorCode errorCode) {
                super(errorCode);
            }

            protected TestException(CustomerErrorCode errorCode, Map<String, Object> parameters) {
                super(errorCode, parameters);
            }

            protected TestException(CustomerErrorCode errorCode, Map<String, Object> parameters, Throwable cause) {
                super(errorCode, parameters, cause);
            }
        }
    }

}