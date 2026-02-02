package com.banking.account.presentation.rest;

import com.banking.account.application.dto.AccountFilter;
import com.banking.account.application.dto.AccountResponse;
import com.banking.account.application.dto.TransactionResponse;
import com.banking.account.application.port.in.CreateAccountUseCase;
import com.banking.account.application.port.in.GetAccountUseCase;
import com.banking.account.application.port.in.TransactionUseCase;
import com.banking.account.application.port.in.UpdateAccountUseCase;
import com.banking.account.domain.exception.AccountNotFoundException;
import com.banking.account.domain.exception.InsufficientBalanceException;
import com.banking.account.fixtures.mothers.*;
import com.banking.account.infrastructure.util.MessageUtils;
import com.banking.account.presentation.mapper.AccountApiMapper;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AccountController.class)
class AccountControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CreateAccountUseCase createAccountUseCase;

    @MockitoBean
    private GetAccountUseCase getAccountUseCase;

    @MockitoBean
    private UpdateAccountUseCase updateAccountUseCase;

    @MockitoBean
    private TransactionUseCase transactionUseCase;

    @MockitoBean
    private AccountApiMapper apiMapper;

    @MockitoBean
    private MessageUtils messageUtils;

    private static final String BASE_PATH = "/api/v1/accounts";

    @Nested
    class CreateAccount {

        @Test
        void shouldCreateAccountAndReturn201() throws Exception {
            UUID customerId = UUID.randomUUID();
            UUID accountId = UUID.randomUUID();

            String requestJson = """
                    {
                        "accountNumber": "1234567890",
                        "accountType": "SAVINGS",
                        "initialBalance": 1000.00,
                        "customerId": "%s"
                    }
                    """.formatted(customerId);

            AccountResponse serviceResponse = AccountResponseMother.defaultResponse(accountId, customerId);
            when(apiMapper.toAccountRequest(any())).thenReturn(AccountRequestMother.defaultRequest(customerId));
            when(createAccountUseCase.createAccount(any())).thenReturn(serviceResponse);
            when(apiMapper.toApiResponse(serviceResponse)).thenReturn(AccountApiResponseMother.defaultResponse(accountId, customerId));

            mockMvc.perform(post(BASE_PATH)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestJson))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.data.id").value(accountId.toString()))
                    .andExpect(jsonPath("$.data.accountNumber").value("1234567890"));

            verify(createAccountUseCase).createAccount(any());
        }

        @Test
        void shouldReturn400WhenAccountNumberIsBlank() throws Exception {
            String requestJson = """
                    {
                        "accountNumber": "",
                        "accountType": "SAVINGS",
                        "initialBalance": 1000.00,
                        "customerId": "%s"
                    }
                    """.formatted(UUID.randomUUID());

            mockMvc.perform(post(BASE_PATH)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestJson))
                    .andExpect(status().isBadRequest());

            verify(createAccountUseCase, never()).createAccount(any());
        }

        @Test
        void shouldReturn400WhenInitialBalanceIsNegative() throws Exception {
            String requestJson = """
                    {
                        "accountNumber": "1234567890",
                        "accountType": "SAVINGS",
                        "initialBalance": -100.00,
                        "customerId": "%s"
                    }
                    """.formatted(UUID.randomUUID());

            mockMvc.perform(post(BASE_PATH)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestJson))
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    class GetAccount {

        @Test
        void shouldGetAccountByIdAndReturn200() throws Exception {
            UUID accountId = UUID.randomUUID();
            UUID customerId = UUID.randomUUID();

            AccountResponse serviceResponse = AccountResponseMother.defaultResponse(accountId, customerId);
            when(getAccountUseCase.getAccountById(accountId)).thenReturn(serviceResponse);
            when(apiMapper.toApiResponse(serviceResponse)).thenReturn(AccountApiResponseMother.defaultResponse(accountId, customerId));

            mockMvc.perform(get(BASE_PATH + "/{id}", accountId))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.data.id").value(accountId.toString()));

            verify(getAccountUseCase).getAccountById(accountId);
        }

        @Test
        void shouldReturn404WhenAccountNotFound() throws Exception {
            UUID accountId = UUID.randomUUID();

            when(getAccountUseCase.getAccountById(accountId))
                    .thenThrow(AccountNotFoundException.withId(accountId));

            mockMvc.perform(get(BASE_PATH + "/{id}", accountId))
                    .andExpect(status().isNotFound());
        }

        @Test
        void shouldGetAccountByAccountNumber() throws Exception {
            String accountNumber = "1234567890";
            UUID accountId = UUID.randomUUID();
            UUID customerId = UUID.randomUUID();

            AccountResponse serviceResponse = AccountResponseMother.defaultResponse(accountId, customerId);
            when(getAccountUseCase.getAccountByAccountNumber(accountNumber)).thenReturn(serviceResponse);
            when(apiMapper.toApiResponse(serviceResponse)).thenReturn(AccountApiResponseMother.defaultResponse(accountId, customerId));

            mockMvc.perform(get(BASE_PATH + "/number/{accountNumber}", accountNumber))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.accountNumber").value(accountNumber));
        }

        @Test
        void shouldGetAllAccountsWithPagination() throws Exception {
            UUID accountId = UUID.randomUUID();
            UUID customerId = UUID.randomUUID();

            AccountResponse serviceResponse = AccountResponseMother.defaultResponse(accountId, customerId);
            Page<AccountResponse> page = new PageImpl<>(List.of(serviceResponse));

            when(getAccountUseCase.getAllAccounts(any(AccountFilter.class), any(Pageable.class))).thenReturn(page);
            when(apiMapper.toApiResponse(serviceResponse)).thenReturn(AccountApiResponseMother.defaultResponse(accountId, customerId));

            mockMvc.perform(get(BASE_PATH)
                            .param("page", "0")
                            .param("size", "10"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content").isArray())
                    .andExpect(jsonPath("$.totalElements").value(1));
        }
    }

    @Nested
    class UpdateAccountStatus {

        @Test
        void shouldActivateAccountAndReturn200() throws Exception {
            UUID accountId = UUID.randomUUID();
            UUID customerId = UUID.randomUUID();

            AccountResponse serviceResponse = AccountResponseMother.activeResponse(accountId, customerId);
            when(updateAccountUseCase.activateAccount(accountId)).thenReturn(serviceResponse);
            when(apiMapper.toApiResponse(serviceResponse)).thenReturn(AccountApiResponseMother.activeResponse(accountId, customerId));

            mockMvc.perform(patch(BASE_PATH + "/{id}/activate", accountId))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.data.status").value("ACTIVE"));
        }

        @Test
        void shouldDeactivateAccountAndReturn200() throws Exception {
            UUID accountId = UUID.randomUUID();
            UUID customerId = UUID.randomUUID();

            AccountResponse serviceResponse = AccountResponseMother.inactiveResponse(accountId, customerId);
            when(updateAccountUseCase.deactivateAccount(accountId)).thenReturn(serviceResponse);
            when(apiMapper.toApiResponse(serviceResponse)).thenReturn(AccountApiResponseMother.inactiveResponse(accountId, customerId));

            mockMvc.perform(patch(BASE_PATH + "/{id}/deactivate", accountId))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.status").value("INACTIVE"));
        }
    }

    @Nested
    class Transactions {

        @Test
        void shouldExecuteDepositTransactionAndReturn201() throws Exception {
            UUID accountId = UUID.randomUUID();
            UUID transactionId = UUID.randomUUID();

            String requestJson = """
                    {
                        "type": "DEPOSIT",
                        "amount": 500.00,
                        "reference": "DEP-001"
                    }
                    """;

            TransactionResponse serviceResponse = TransactionResponseMother.depositResponse(transactionId, accountId);
            when(apiMapper.toTransactionRequest(eq(accountId), any())).thenReturn(TransactionRequestMother.deposit(accountId));
            when(transactionUseCase.executeTransaction(any())).thenReturn(serviceResponse);
            when(apiMapper.toApiResponse(serviceResponse)).thenReturn(TransactionApiResponseMother.depositResponse(transactionId, accountId));

            mockMvc.perform(post(BASE_PATH + "/{accountId}/transactions", accountId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestJson))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.data.type").value("DEPOSIT"));
        }

        @Test
        void shouldReturn400WhenTransactionAmountIsZero() throws Exception {
            UUID accountId = UUID.randomUUID();

            String requestJson = """
                    {
                        "type": "DEPOSIT",
                        "amount": 0.00,
                        "reference": "DEP-001"
                    }
                    """;

            mockMvc.perform(post(BASE_PATH + "/{accountId}/transactions", accountId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestJson))
                    .andExpect(status().isBadRequest());
        }

        @Test
        void shouldReturn400WhenInsufficientBalance() throws Exception {
            UUID accountId = UUID.randomUUID();

            String requestJson = """
                    {
                        "type": "WITHDRAWAL",
                        "amount": 10000.00,
                        "reference": "WITH-001"
                    }
                    """;

            when(apiMapper.toTransactionRequest(eq(accountId), any())).thenReturn(TransactionRequestMother.withdrawal(accountId));
            when(transactionUseCase.executeTransaction(any()))
                    .thenThrow(InsufficientBalanceException.withDetails(
                            "123456789",
                            new BigDecimal("1000.00"),
                            new BigDecimal("10000.00")
                    ));

            mockMvc.perform(post(BASE_PATH + "/{accountId}/transactions", accountId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestJson))
                    .andExpect(status().isBadRequest());
        }

        @Test
        void shouldGetTransactionsByAccountWithPagination() throws Exception {
            UUID accountId = UUID.randomUUID();
            UUID transactionId = UUID.randomUUID();

            TransactionResponse serviceResponse = TransactionResponseMother.depositResponse(transactionId, accountId);
            Page<TransactionResponse> page = new PageImpl<>(List.of(serviceResponse));

            when(transactionUseCase.getTransactionsByAccountId(eq(accountId), any(Pageable.class))).thenReturn(page);
            when(apiMapper.toApiResponse(serviceResponse)).thenReturn(TransactionApiResponseMother.depositResponse(transactionId, accountId));

            mockMvc.perform(get(BASE_PATH + "/{accountId}/transactions", accountId)
                            .param("page", "0")
                            .param("size", "10"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content").isArray());
        }

        @Test
        void shouldGetTransactionsByDateRange() throws Exception {
            UUID accountId = UUID.randomUUID();
            UUID transactionId = UUID.randomUUID();
            LocalDateTime startDate = LocalDateTime.now().minusDays(30);
            LocalDateTime endDate = LocalDateTime.now();

            TransactionResponse serviceResponse = TransactionResponseMother.depositResponse(transactionId, accountId);
            when(transactionUseCase.getTransactionsByDateRange(eq(accountId), any(), any()))
                    .thenReturn(List.of(serviceResponse));
            when(apiMapper.toApiResponse(serviceResponse)).thenReturn(TransactionApiResponseMother.depositResponse(transactionId, accountId));

            mockMvc.perform(get(BASE_PATH + "/{accountId}/transactions/report", accountId)
                            .param("startDate", startDate.toString())
                            .param("endDate", endDate.toString()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data").isArray());
        }
    }

}