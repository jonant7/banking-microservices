package com.banking.account.presentation.rest;

import com.banking.account.application.dto.*;
import com.banking.account.application.port.in.CreateAccountUseCase;
import com.banking.account.application.port.in.GetAccountUseCase;
import com.banking.account.application.port.in.TransactionUseCase;
import com.banking.account.application.port.in.UpdateAccountUseCase;
import com.banking.account.presentation.dto.request.CreateAccountApiRequest;
import com.banking.account.presentation.dto.request.TransactionApiRequest;
import com.banking.account.presentation.dto.response.AccountApiResponse;
import com.banking.account.presentation.dto.response.ApiResponse;
import com.banking.account.presentation.dto.response.PageResponse;
import com.banking.account.presentation.dto.response.TransactionApiResponse;
import com.banking.account.presentation.mapper.AccountApiMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("${api.base-path}/accounts")
public class AccountController {

    private final CreateAccountUseCase createAccountUseCase;
    private final GetAccountUseCase getAccountUseCase;
    private final UpdateAccountUseCase updateAccountUseCase;
    private final TransactionUseCase transactionUseCase;
    private final AccountApiMapper apiMapper;

    @PostMapping
    public ResponseEntity<ApiResponse<AccountApiResponse>> createAccount(
            @Valid @RequestBody CreateAccountApiRequest request
    ) {
        log.info("REST request to create account: {}", request.getAccountNumber());

        AccountRequest accountRequest = apiMapper.toAccountRequest(request);
        AccountResponse response = createAccountUseCase.createAccount(accountRequest);
        AccountApiResponse apiResponse = apiMapper.toApiResponse(response);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(apiResponse, "Account created successfully"));
    }

    @GetMapping
    public ResponseEntity<PageResponse<AccountApiResponse>> getAllAccounts(AccountFilter filter) {
        log.debug("REST request to get accounts with filters: {}", filter);

        int page = Objects.nonNull(filter.getPage()) ? filter.getPage() : 0;
        int size = Objects.nonNull(filter.getSize()) ? filter.getSize() : 10;
        String sortBy = Objects.nonNull(filter.getSortBy()) ? filter.getSortBy() : "createdAt";
        String sortDirection = Objects.nonNull(filter.getSortDirection()) ? filter.getSortDirection() : "DESC";

        Sort.Direction direction = Sort.Direction.fromString(sortDirection);
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));

        Page<AccountResponse> accountPage = getAccountUseCase.getAllAccounts(filter, pageable);
        Page<AccountApiResponse> apiResponsePage = accountPage.map(apiMapper::toApiResponse);

        return ResponseEntity.ok(PageResponse.of(apiResponsePage));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<AccountApiResponse>> getAccountById(
            @PathVariable UUID id
    ) {
        log.debug("REST request to get account by ID: {}", id);

        AccountResponse response = getAccountUseCase.getAccountById(id);
        AccountApiResponse apiResponse = apiMapper.toApiResponse(response);

        return ResponseEntity.ok(ApiResponse.success(apiResponse));
    }

    @GetMapping("/number/{accountNumber}")
    public ResponseEntity<ApiResponse<AccountApiResponse>> getAccountByAccountNumber(
            @PathVariable String accountNumber
    ) {
        log.debug("REST request to get account by account number: {}", accountNumber);

        AccountResponse response = getAccountUseCase.getAccountByAccountNumber(accountNumber);
        AccountApiResponse apiResponse = apiMapper.toApiResponse(response);

        return ResponseEntity.ok(ApiResponse.success(apiResponse));
    }

    @PatchMapping("/{id}/activate")
    public ResponseEntity<ApiResponse<AccountApiResponse>> activateAccount(
            @PathVariable UUID id
    ) {
        log.info("REST request to activate account: {}", id);

        AccountResponse response = updateAccountUseCase.activateAccount(id);
        AccountApiResponse apiResponse = apiMapper.toApiResponse(response);

        return ResponseEntity.ok(ApiResponse.success(apiResponse, "Account activated successfully"));
    }

    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<ApiResponse<AccountApiResponse>> deactivateAccount(
            @PathVariable UUID id
    ) {
        log.info("REST request to deactivate account: {}", id);

        AccountResponse response = updateAccountUseCase.deactivateAccount(id);
        AccountApiResponse apiResponse = apiMapper.toApiResponse(response);

        return ResponseEntity.ok(ApiResponse.success(apiResponse, "Account deactivated successfully"));
    }

    @PostMapping("/{accountId}/transactions")
    public ResponseEntity<ApiResponse<TransactionApiResponse>> executeTransaction(
            @PathVariable UUID accountId,
            @Valid @RequestBody TransactionApiRequest request
    ) {
        log.info("REST request to execute {} transaction on account: {}", request.getType(), accountId);

        TransactionRequest transactionRequest = apiMapper.toTransactionRequest(accountId, request);
        TransactionResponse response = transactionUseCase.executeTransaction(transactionRequest);
        TransactionApiResponse apiResponse = apiMapper.toApiResponse(response);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(apiResponse, "Transaction executed successfully"));
    }

    @GetMapping("/transactions/{transactionId}")
    public ResponseEntity<ApiResponse<TransactionApiResponse>> getTransactionById(
            @PathVariable UUID transactionId
    ) {
        log.debug("REST request to get transaction by ID: {}", transactionId);

        TransactionResponse response = transactionUseCase.getTransactionById(transactionId);
        TransactionApiResponse apiResponse = apiMapper.toApiResponse(response);

        return ResponseEntity.ok(ApiResponse.success(apiResponse));
    }

    @GetMapping("/{accountId}/transactions")
    public ResponseEntity<PageResponse<TransactionApiResponse>> getTransactionsByAccount(
            @PathVariable UUID accountId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "DESC") String sortDirection
    ) {
        log.debug("REST request to get transactions for account: {}", accountId);

        Sort.Direction direction = Sort.Direction.fromString(sortDirection);
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));

        Page<TransactionResponse> transactionPage = transactionUseCase.getTransactionsByAccountId(accountId, pageable);
        Page<TransactionApiResponse> apiResponsePage = transactionPage.map(apiMapper::toApiResponse);

        return ResponseEntity.ok(PageResponse.of(apiResponsePage));
    }

    @GetMapping("/{accountId}/transactions/report")
    public ResponseEntity<ApiResponse<List<TransactionApiResponse>>> getTransactionsByDateRange(
            @PathVariable UUID accountId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate
    ) {
        log.debug("REST request to get transactions for account: {} between {} and {}",
                accountId, startDate, endDate);

        List<TransactionResponse> transactions = transactionUseCase.getTransactionsByDateRange(
                accountId,
                startDate,
                endDate
        );

        List<TransactionApiResponse> apiResponses = transactions.stream()
                .map(apiMapper::toApiResponse)
                .collect(Collectors.toList());

        return ResponseEntity.ok(ApiResponse.success(apiResponses));
    }

}