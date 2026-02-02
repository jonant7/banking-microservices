package com.banking.account.presentation.exception;

import com.banking.account.domain.exception.*;
import com.banking.account.infrastructure.util.MessageUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
@Order(Ordered.HIGHEST_PRECEDENCE)
public class GlobalExceptionHandler {

    private static final String VALIDATION_ERROR_MESSAGE = "Validation failed for one or more fields";
    private static final String INTERNAL_ERROR_MESSAGE = "An unexpected error occurred. Please try again later";
    private static final String INVALID_REQUEST_MESSAGE = "Invalid request format";

    @ExceptionHandler(AccountNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleAccountNotFound(
            AccountNotFoundException ex,
            HttpServletRequest request) {

        log.warn("Account not found - ErrorCode: {}, Parameters: {}",
                ex.getErrorCode(), ex.getParameters());

        String message = resolveMessage(ex);

        return buildErrorResponse(
                HttpStatus.NOT_FOUND,
                message,
                request.getRequestURI()
        );
    }

    @ExceptionHandler(InactiveAccountException.class)
    public ResponseEntity<ApiErrorResponse> handleInactiveAccount(
            InactiveAccountException ex,
            HttpServletRequest request) {

        log.warn("Inactive account operation attempted - ErrorCode: {}, Parameters: {}",
                ex.getErrorCode(), ex.getParameters());

        String message = resolveMessage(ex);

        return buildErrorResponse(
                HttpStatus.FORBIDDEN,
                message,
                request.getRequestURI()
        );
    }

    @ExceptionHandler(InactiveCustomerException.class)
    public ResponseEntity<ApiErrorResponse> handleInactiveCustomer(
            InactiveCustomerException ex,
            HttpServletRequest request) {

        log.warn("Inactive customer operation attempted - ErrorCode: {}, Parameters: {}",
                ex.getErrorCode(), ex.getParameters());

        String message = resolveMessage(ex);

        return buildErrorResponse(
                HttpStatus.FORBIDDEN,
                message,
                request.getRequestURI()
        );
    }

    @ExceptionHandler(InsufficientBalanceException.class)
    public ResponseEntity<ApiErrorResponse> handleInsufficientBalance(
            InsufficientBalanceException ex,
            HttpServletRequest request) {

        log.warn("Insufficient balance - ErrorCode: {}, Parameters: {}",
                ex.getErrorCode(), ex.getParameters());

        String message = resolveMessage(ex);

        return buildErrorResponse(
                HttpStatus.BAD_REQUEST,
                message,
                request.getRequestURI()
        );
    }

    @ExceptionHandler(InvalidTransactionException.class)
    public ResponseEntity<ApiErrorResponse> handleInvalidTransaction(
            InvalidTransactionException ex,
            HttpServletRequest request) {

        log.warn("Invalid transaction - ErrorCode: {}, Parameters: {}",
                ex.getErrorCode(), ex.getParameters());

        String message = resolveMessage(ex);

        return buildErrorResponse(
                HttpStatus.BAD_REQUEST,
                message,
                request.getRequestURI()
        );
    }

    @ExceptionHandler(AccountDomainException.class)
    public ResponseEntity<ApiErrorResponse> handleAccountDomainException(
            AccountDomainException ex,
            HttpServletRequest request) {

        log.error("Unhandled account domain exception - ErrorCode: {}, Parameters: {}",
                ex.getErrorCode(), ex.getParameters(), ex);

        String message = resolveMessage(ex);

        return buildErrorResponse(
                HttpStatus.BAD_REQUEST,
                message,
                request.getRequestURI()
        );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            HttpServletRequest request) {

        log.debug("Validation failed for request to {}", request.getRequestURI());

        List<ApiErrorResponse.FieldError> fieldErrors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(this::mapFieldError)
                .collect(Collectors.toList());

        ApiErrorResponse errorResponse = ApiErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .error(HttpStatus.BAD_REQUEST.getReasonPhrase())
                .message(VALIDATION_ERROR_MESSAGE)
                .path(request.getRequestURI())
                .fieldErrors(fieldErrors)
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiErrorResponse> handleConstraintViolation(
            ConstraintViolationException ex,
            HttpServletRequest request) {

        log.debug("Constraint violation in request to {}", request.getRequestURI());

        List<ApiErrorResponse.FieldError> fieldErrors = ex.getConstraintViolations()
                .stream()
                .map(this::mapConstraintViolation)
                .collect(Collectors.toList());

        ApiErrorResponse errorResponse = ApiErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .error(HttpStatus.BAD_REQUEST.getReasonPhrase())
                .message(VALIDATION_ERROR_MESSAGE)
                .path(request.getRequestURI())
                .fieldErrors(fieldErrors)
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiErrorResponse> handleTypeMismatch(
            MethodArgumentTypeMismatchException ex,
            HttpServletRequest request) {

        log.warn("Type mismatch for parameter '{}' with value '{}'",
                ex.getName(), ex.getValue());

        String message = String.format(
                "Invalid value '%s' for parameter '%s'. Expected type: %s",
                ex.getValue(),
                ex.getName(),
                ex.getRequiredType() != null ? ex.getRequiredType().getSimpleName() : "unknown"
        );

        return buildErrorResponse(
                HttpStatus.BAD_REQUEST,
                message,
                request.getRequestURI()
        );
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ApiErrorResponse> handleMissingServletRequestParameter(
            MissingServletRequestParameterException ex,
            HttpServletRequest request) {

        log.warn("Missing required parameter: {}", ex.getParameterName());

        String message = String.format(
                "Required parameter '%s' of type '%s' is missing",
                ex.getParameterName(),
                ex.getParameterType()
        );

        return buildErrorResponse(
                HttpStatus.BAD_REQUEST,
                message,
                request.getRequestURI()
        );
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiErrorResponse> handleHttpMessageNotReadable(
            HttpMessageNotReadableException ex,
            HttpServletRequest request) {

        log.warn("Malformed JSON request: {}", ex.getMessage());

        return buildErrorResponse(
                HttpStatus.BAD_REQUEST,
                INVALID_REQUEST_MESSAGE,
                request.getRequestURI()
        );
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ApiErrorResponse> handleHttpRequestMethodNotSupported(
            HttpRequestMethodNotSupportedException ex,
            HttpServletRequest request) {

        log.warn("Method not supported: {} for {}", ex.getMethod(), request.getRequestURI());

        String message = String.format(
                "HTTP method '%s' is not supported for this endpoint. Supported methods: %s",
                ex.getMethod(),
                ex.getSupportedHttpMethods()
        );

        return buildErrorResponse(
                HttpStatus.METHOD_NOT_ALLOWED,
                message,
                request.getRequestURI()
        );
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<ApiErrorResponse> handleHttpMediaTypeNotSupported(
            HttpMediaTypeNotSupportedException ex,
            HttpServletRequest request) {

        log.warn("Unsupported media type: {}", ex.getContentType());

        String message = String.format(
                "Media type '%s' is not supported. Supported media types: %s",
                ex.getContentType(),
                ex.getSupportedMediaTypes()
        );

        return buildErrorResponse(
                HttpStatus.UNSUPPORTED_MEDIA_TYPE,
                message,
                request.getRequestURI()
        );
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleNoHandlerFound(
            NoHandlerFoundException ex,
            HttpServletRequest request) {

        log.warn("No handler found for {} {}", ex.getHttpMethod(), ex.getRequestURL());

        String message = String.format(
                "No endpoint found for %s %s",
                ex.getHttpMethod(),
                ex.getRequestURL()
        );

        return buildErrorResponse(
                HttpStatus.NOT_FOUND,
                message,
                request.getRequestURI()
        );
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiErrorResponse> handleIllegalArgument(
            IllegalArgumentException ex,
            HttpServletRequest request) {

        log.warn("Illegal argument: {}", ex.getMessage());

        return buildErrorResponse(
                HttpStatus.BAD_REQUEST,
                ex.getMessage(),
                request.getRequestURI()
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleGenericException(
            Exception ex,
            HttpServletRequest request) {

        log.error("Unexpected error occurred while processing request to {}",
                request.getRequestURI(), ex);

        return buildErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR,
                INTERNAL_ERROR_MESSAGE,
                request.getRequestURI()
        );
    }

    private String resolveMessage(AccountDomainException ex) {
        Map<String, Object> params = ex.getParameters();

        if (params.isEmpty()) {
            return MessageUtils.getMessage(ex.getErrorCode().getCode());
        }

        Object[] args = params.values().toArray();
        return MessageUtils.getMessage(ex.getErrorCode().getCode(), args);
    }

    private ApiErrorResponse.FieldError mapFieldError(FieldError error) {
        return ApiErrorResponse.FieldError.builder()
                .field(error.getField())
                .message(error.getDefaultMessage())
                .rejectedValue(error.getRejectedValue())
                .build();
    }

    private ApiErrorResponse.FieldError mapConstraintViolation(
            ConstraintViolation<?> violation) {

        String fieldName = violation.getPropertyPath().toString();

        return ApiErrorResponse.FieldError.builder()
                .field(fieldName)
                .message(violation.getMessage())
                .rejectedValue(violation.getInvalidValue())
                .build();
    }

    private ResponseEntity<ApiErrorResponse> buildErrorResponse(
            HttpStatus status,
            String message,
            String path) {

        ApiErrorResponse errorResponse = ApiErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(status.value())
                .error(status.getReasonPhrase())
                .message(message)
                .path(path)
                .build();

        return ResponseEntity.status(status).body(errorResponse);
    }

}