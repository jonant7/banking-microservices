package com.banking.account.presentation.rest;

import com.banking.account.application.dto.AccountStatementReport;
import com.banking.account.application.port.in.GenerateAccountStatementUseCase;
import com.banking.account.application.port.out.CustomerEventListener;
import com.banking.account.fixtures.mothers.ReportMother;
import com.banking.account.fixtures.mothers.StatementResponseMother;
import com.banking.account.infrastructure.util.MessageUtils;
import com.banking.account.presentation.dto.response.AccountStatementResponse;
import com.banking.account.presentation.mapper.AccountApiMapper;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ReportController.class)
class ReportControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private GenerateAccountStatementUseCase generateStatementUseCase;

    @MockitoBean
    private AccountApiMapper apiMapper;

    @MockitoBean
    private CustomerEventListener customerEventListener;

    @MockitoBean
    private MessageUtils messageUtils;


    private static final String BASE_PATH = "/api/v1/reports";

    @Nested
    class GenerateAccountStatement {

        @Test
        void shouldGenerateAccountStatementAndReturn200() throws Exception {
            UUID customerId = UUID.randomUUID();
            UUID accountId = UUID.randomUUID();
            LocalDateTime startDate = LocalDateTime.now().minusDays(30);
            LocalDateTime endDate = LocalDateTime.now();
            String customerName = "John Doe";

            AccountStatementReport report = ReportMother.defaultReport(customerId, accountId, startDate, endDate);
            AccountStatementResponse response = StatementResponseMother.defaultResponse(customerId, customerName, startDate, endDate, accountId);

            when(generateStatementUseCase.generateStatement(eq(customerId), any(), any())).thenReturn(report);
            when(customerEventListener.getCustomerName(customerId)).thenReturn(customerName);
            when(apiMapper.toStatementResponse(report, customerName)).thenReturn(response);

            mockMvc.perform(get(BASE_PATH)
                            .param("customerId", customerId.toString())
                            .param("startDate", startDate.toString())
                            .param("endDate", endDate.toString()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.data.customerId").value(customerId.toString()))
                    .andExpect(jsonPath("$.data.customerName").value(customerName))
                    .andExpect(jsonPath("$.data.accounts").isArray());

            verify(generateStatementUseCase).generateStatement(eq(customerId), any(), any());
            verify(customerEventListener).getCustomerName(customerId);
        }

        @Test
        void shouldReturn400WhenCustomerIdIsMissing() throws Exception {
            LocalDateTime startDate = LocalDateTime.now().minusDays(30);
            LocalDateTime endDate = LocalDateTime.now();

            mockMvc.perform(get(BASE_PATH)
                            .param("startDate", startDate.toString())
                            .param("endDate", endDate.toString()))
                    .andExpect(status().isBadRequest());

            verify(generateStatementUseCase, never()).generateStatement(any(), any(), any());
        }

        @Test
        void shouldReturn400WhenStartDateIsMissing() throws Exception {
            UUID customerId = UUID.randomUUID();
            LocalDateTime endDate = LocalDateTime.now();

            mockMvc.perform(get(BASE_PATH)
                            .param("customerId", customerId.toString())
                            .param("endDate", endDate.toString()))
                    .andExpect(status().isBadRequest());
        }

        @Test
        void shouldReturn400WhenEndDateIsMissing() throws Exception {
            UUID customerId = UUID.randomUUID();
            LocalDateTime startDate = LocalDateTime.now().minusDays(30);

            mockMvc.perform(get(BASE_PATH)
                            .param("customerId", customerId.toString())
                            .param("startDate", startDate.toString()))
                    .andExpect(status().isBadRequest());
        }

        @Test
        void shouldReturn400WhenStartDateIsAfterEndDate() throws Exception {
            UUID customerId = UUID.randomUUID();
            LocalDateTime startDate = LocalDateTime.now();
            LocalDateTime endDate = LocalDateTime.now().minusDays(30);

            when(generateStatementUseCase.generateStatement(eq(customerId), any(), any()))
                    .thenThrow(new IllegalArgumentException("Start date must be before end date"));

            mockMvc.perform(get(BASE_PATH)
                            .param("customerId", customerId.toString())
                            .param("startDate", startDate.toString())
                            .param("endDate", endDate.toString()))
                    .andExpect(status().isBadRequest());
        }

        @Test
        void shouldReturnEmptyAccountsWhenCustomerHasNoAccounts() throws Exception {
            UUID customerId = UUID.randomUUID();
            LocalDateTime startDate = LocalDateTime.now().minusDays(30);
            LocalDateTime endDate = LocalDateTime.now();
            String customerName = "Jane Doe";

            AccountStatementReport emptyReport = ReportMother.emptyReport(customerId, startDate, endDate);
            AccountStatementResponse emptyResponse = StatementResponseMother.emptyResponse(customerId, customerName, startDate, endDate);

            when(generateStatementUseCase.generateStatement(eq(customerId), any(), any())).thenReturn(emptyReport);
            when(customerEventListener.getCustomerName(customerId)).thenReturn(customerName);
            when(apiMapper.toStatementResponse(emptyReport, customerName)).thenReturn(emptyResponse);

            mockMvc.perform(get(BASE_PATH)
                            .param("customerId", customerId.toString())
                            .param("startDate", startDate.toString())
                            .param("endDate", endDate.toString()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.accounts").isEmpty());
        }

        @Test
        void shouldReturn500WhenCustomerDoesNotExist() throws Exception {
            UUID customerId = UUID.randomUUID();
            LocalDateTime startDate = LocalDateTime.now().minusDays(30);
            LocalDateTime endDate = LocalDateTime.now();

            when(generateStatementUseCase.generateStatement(eq(customerId), any(), any()))
                    .thenThrow(new IllegalStateException("Customer " + customerId + " does not exist"));

            mockMvc.perform(get(BASE_PATH)
                            .param("customerId", customerId.toString())
                            .param("startDate", startDate.toString())
                            .param("endDate", endDate.toString()))
                    .andExpect(status().isInternalServerError());
        }

        @Test
        void shouldReturnStatementWithMultipleAccounts() throws Exception {
            UUID customerId = UUID.randomUUID();
            UUID accountId1 = UUID.randomUUID();
            UUID accountId2 = UUID.randomUUID();
            LocalDateTime startDate = LocalDateTime.now().minusDays(30);
            LocalDateTime endDate = LocalDateTime.now();
            String customerName = "Multi Account User";

            AccountStatementReport report = ReportMother.multiAccountReport(customerId, accountId1, accountId2, startDate, endDate);
            AccountStatementResponse response = StatementResponseMother.multiAccountResponse(customerId, customerName, startDate, endDate, accountId1, accountId2);

            when(generateStatementUseCase.generateStatement(eq(customerId), any(), any())).thenReturn(report);
            when(customerEventListener.getCustomerName(customerId)).thenReturn(customerName);
            when(apiMapper.toStatementResponse(report, customerName)).thenReturn(response);

            mockMvc.perform(get(BASE_PATH)
                            .param("customerId", customerId.toString())
                            .param("startDate", startDate.toString())
                            .param("endDate", endDate.toString()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.accounts.length()").value(2));
        }
    }

}