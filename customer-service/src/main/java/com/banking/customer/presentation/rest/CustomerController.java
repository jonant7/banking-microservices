package com.banking.customer.presentation.rest;

import com.banking.customer.application.dto.CustomerFilter;
import com.banking.customer.application.dto.CustomerRequest;
import com.banking.customer.application.dto.CustomerResponse;
import com.banking.customer.application.dto.CustomerUpdateRequest;
import com.banking.customer.application.port.in.CreateCustomerUseCase;
import com.banking.customer.application.port.in.GetCustomerUseCase;
import com.banking.customer.application.port.in.ManageCustomerStatusUseCase;
import com.banking.customer.application.port.in.UpdateCustomerUseCase;
import com.banking.customer.presentation.dto.request.CreateCustomerApiRequest;
import com.banking.customer.presentation.dto.request.PatchCustomerApiRequest;
import com.banking.customer.presentation.dto.request.UpdateCustomerApiRequest;
import com.banking.customer.presentation.dto.response.ApiResponse;
import com.banking.customer.presentation.dto.response.CustomerApiResponse;
import com.banking.customer.presentation.dto.response.PageResponse;
import com.banking.customer.presentation.mapper.CustomerApiMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;
import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("${api.base-path}/customers")
public class CustomerController {

    private final CreateCustomerUseCase createCustomerUseCase;
    private final UpdateCustomerUseCase updateCustomerUseCase;
    private final GetCustomerUseCase getCustomerUseCase;
    private final ManageCustomerStatusUseCase manageCustomerStatusUseCase;
    private final CustomerApiMapper apiMapper;

    @PostMapping
    public ResponseEntity<ApiResponse<CustomerApiResponse>> createCustomer(
            @Valid @RequestBody CreateCustomerApiRequest request) {

        log.info("REST request to create customer: {}", request.getCustomerId());

        CustomerRequest customerRequest = apiMapper.toApplicationRequest(request);
        CustomerResponse response = createCustomerUseCase.create(customerRequest);
        CustomerApiResponse apiResponse = apiMapper.toApiResponse(response);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(apiResponse, "Customer created successfully"));
    }

    @GetMapping
    public ResponseEntity<PageResponse<CustomerApiResponse>> getAllCustomers(CustomerFilter filter) {

        log.debug("REST request to get customers with filters: {}", filter);

        int page = Objects.nonNull(filter.getPage()) ? filter.getPage() : 0;
        int size = Objects.nonNull(filter.getSize()) ? filter.getSize() : 10;
        String sortBy = Objects.nonNull(filter.getSortBy()) ? filter.getSortBy() : "createdAt";
        String sortDirection = Objects.nonNull(filter.getSortDirection()) ? filter.getSortDirection() : "DESC";

        Sort.Direction direction = Sort.Direction.fromString(sortDirection);
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));

        Page<CustomerResponse> customerPage = getCustomerUseCase.findAll(filter, pageable);
        Page<CustomerApiResponse> apiResponsePage = customerPage.map(apiMapper::toApiResponse);

        return ResponseEntity.ok(PageResponse.of(apiResponsePage));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CustomerApiResponse>> getCustomerById(@PathVariable UUID id) {

        log.debug("REST request to get customer by ID: {}", id);

        CustomerResponse response = getCustomerUseCase.findById(id);
        CustomerApiResponse apiResponse = apiMapper.toApiResponse(response);

        return ResponseEntity.ok(ApiResponse.success(apiResponse));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<CustomerApiResponse>> updateCustomer(@PathVariable UUID id, @Valid @RequestBody UpdateCustomerApiRequest request) {

        log.info("REST request to update customer: {}", id);

        CustomerRequest customerRequest = apiMapper.toApplicationUpdateRequest(request);
        CustomerResponse response = updateCustomerUseCase.update(id, customerRequest);
        CustomerApiResponse apiResponse = apiMapper.toApiResponse(response);

        return ResponseEntity.ok(ApiResponse.success(apiResponse, "Customer updated successfully"));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ApiResponse<CustomerApiResponse>> patchCustomer(@PathVariable UUID id, @Valid @RequestBody PatchCustomerApiRequest request) {

        log.info("REST request to patch customer: {}", id);

        CustomerUpdateRequest updateRequest = apiMapper.toApplicationPatchRequest(request);
        CustomerResponse response = updateCustomerUseCase.patch(id, updateRequest);
        CustomerApiResponse apiResponse = apiMapper.toApiResponse(response);

        return ResponseEntity.ok(ApiResponse.success(apiResponse, "Customer updated successfully"));
    }

    @PatchMapping("/{id}/activate")
    public ResponseEntity<ApiResponse<CustomerApiResponse>> activateCustomer(@PathVariable UUID id) {

        log.info("REST request to activate customer: {}", id);

        CustomerResponse response = manageCustomerStatusUseCase.activate(id);
        CustomerApiResponse apiResponse = apiMapper.toApiResponse(response);

        return ResponseEntity.ok(ApiResponse.success(apiResponse, "Customer activated successfully"));
    }

    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<ApiResponse<CustomerApiResponse>> deactivateCustomer(@PathVariable UUID id) {

        log.info("REST request to deactivate customer: {}", id);

        CustomerResponse response = manageCustomerStatusUseCase.deactivate(id);
        CustomerApiResponse apiResponse = apiMapper.toApiResponse(response);

        return ResponseEntity.ok(ApiResponse.success(apiResponse, "Customer deactivated successfully"));
    }

}