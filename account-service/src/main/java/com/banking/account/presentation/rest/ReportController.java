package com.banking.account.presentation.rest;

import com.banking.account.application.dto.AccountStatementReport;
import com.banking.account.application.port.in.GenerateAccountStatementUseCase;
import com.banking.account.application.port.out.CustomerEventListener;
import com.banking.account.presentation.dto.response.AccountStatementResponse;
import com.banking.account.presentation.dto.response.ApiResponse;
import com.banking.account.presentation.mapper.AccountApiMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("${api.base-path}/reports")
public class ReportController {

    private final GenerateAccountStatementUseCase generateStatementUseCase;
    private final AccountApiMapper apiMapper;
    private final CustomerEventListener customerEventListener;

    @GetMapping
    public ResponseEntity<ApiResponse<AccountStatementResponse>> generateAccountStatement(
            @RequestParam UUID customerId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate
    ) {
        log.info("REST request to generate account statement for customer: {} from {} to {}",
                customerId, startDate, endDate);

        AccountStatementReport report = generateStatementUseCase.generateStatement(
                customerId,
                startDate,
                endDate
        );

        String customerName = customerEventListener.getCustomerName(customerId);
        AccountStatementResponse response = apiMapper.toStatementResponse(report, customerName);

        return ResponseEntity.ok(ApiResponse.success(response, "Report generated successfully"));
    }

}