package com.banking.customer.application.service;

import com.banking.customer.application.dto.CustomerFilter;
import com.banking.customer.application.dto.CustomerRequest;
import com.banking.customer.application.dto.CustomerResponse;
import com.banking.customer.application.dto.CustomerUpdateRequest;
import com.banking.customer.application.mapper.CustomerResponseMapper;
import com.banking.customer.application.port.in.CreateCustomerUseCase;
import com.banking.customer.application.port.in.GetCustomerUseCase;
import com.banking.customer.application.port.in.ManageCustomerStatusUseCase;
import com.banking.customer.application.port.in.UpdateCustomerUseCase;
import com.banking.customer.application.port.out.DomainEventPublisher;
import com.banking.customer.domain.exception.CustomerNotFoundException;
import com.banking.customer.domain.exception.DuplicateCustomerException;
import com.banking.customer.domain.model.Customer;
import com.banking.customer.domain.model.CustomerId;
import com.banking.customer.domain.model.Identification;
import com.banking.customer.domain.model.PasswordHash;
import com.banking.customer.domain.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomerService implements
        CreateCustomerUseCase,
        UpdateCustomerUseCase,
        GetCustomerUseCase,
        ManageCustomerStatusUseCase {

    private final CustomerRepository customerRepository;
    private final CustomerResponseMapper customerMapper;
    private final PasswordHashingService passwordHashingService;
    private final DomainEventPublisher eventPublisher;

    @Override
    @Transactional
    public CustomerResponse create(CustomerRequest request) {
        log.info("Creating customer with customerId: {}", request.getCustomerId());

        validateUniqueness(request);

        String hashedPassword = passwordHashingService.hashPassword(request.getPassword());

        Customer customer = Customer.create(
                request.getName(),
                request.getLastName(),
                request.getGender(),
                request.getBirthDate(),
                request.getIdentification(),
                request.getAddress(),
                request.getPhone(),
                request.getCustomerId(),
                hashedPassword
        );

        Customer savedCustomer = customerRepository.save(customer);

        eventPublisher.publish(savedCustomer.getDomainEvents());
        savedCustomer.clearDomainEvents();

        log.info("Customer created successfully with ID: {}", savedCustomer.getId());
        return customerMapper.toResponse(savedCustomer);
    }

    @Override
    @Transactional
    public CustomerResponse update(UUID id, CustomerRequest request) {
        log.info("Updating customer: {}", id);

        Customer customer = findCustomerById(id);

        customer.updatePersonalInfo(
                request.getName(),
                request.getLastName(),
                request.getAddress(),
                request.getPhone()
        );

        updatePasswordIfProvided(customer, request.getPassword());

        Customer updated = customerRepository.save(customer);

        eventPublisher.publish(updated.getDomainEvents());
        updated.clearDomainEvents();

        log.info("Customer updated successfully: {}", id);
        return customerMapper.toResponse(updated);
    }

    @Override
    @Transactional
    public CustomerResponse patch(UUID id, CustomerUpdateRequest request) {
        log.info("Partial update for customer: {}", id);

        Customer customer = findCustomerById(id);

        updateContactInfoIfProvided(customer, request);
        updatePasswordIfProvided(customer, request.getPassword());
        updateStatusIfProvided(customer, request.getStatus());

        Customer updated = customerRepository.save(customer);

        eventPublisher.publish(updated.getDomainEvents());
        updated.clearDomainEvents();

        log.info("Customer partially updated successfully: {}", id);
        return customerMapper.toResponse(updated);
    }

    @Override
    @Transactional(readOnly = true)
    public CustomerResponse findById(UUID id) {
        log.debug("Finding customer by ID: {}", id);

        Customer customer = findCustomerById(id);
        return customerMapper.toResponse(customer);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CustomerResponse> findAll(CustomerFilter filter, Pageable pageable) {
        log.debug("Finding customers with filter: {}", filter);
        return customerRepository.findAll(filter, pageable)
                .map(customerMapper::toResponse);
    }

    @Override
    @Transactional
    public CustomerResponse activate(UUID id) {
        log.info("Activating customer: {}", id);

        Customer customer = findCustomerById(id);
        customer.activate();

        Customer activated = customerRepository.save(customer);

        eventPublisher.publish(activated.getDomainEvents());
        activated.clearDomainEvents();

        log.info("Customer activated successfully: {}", id);
        return customerMapper.toResponse(activated);
    }

    @Override
    @Transactional
    public CustomerResponse deactivate(UUID id) {
        log.info("Deactivating customer: {}", id);

        Customer customer = findCustomerById(id);
        customer.deactivate();

        Customer deactivated = customerRepository.save(customer);

        eventPublisher.publish(deactivated.getDomainEvents());
        deactivated.clearDomainEvents();

        log.info("Customer deactivated successfully: {}", id);
        return customerMapper.toResponse(deactivated);
    }

    private Customer findCustomerById(UUID id) {
        return customerRepository.findById(id)
                .orElseThrow(() -> CustomerNotFoundException.withId(id));
    }

    private void validateUniqueness(CustomerRequest request) {
        CustomerId customerId = CustomerId.of(request.getCustomerId());
        Identification identification = Identification.of(request.getIdentification());

        if (customerRepository.existsByCustomerId(customerId)) {
            throw DuplicateCustomerException.withCustomerId(request.getCustomerId());
        }

        if (customerRepository.existsByIdentification(identification)) {
            throw DuplicateCustomerException.withIdentification(request.getIdentification());
        }
    }

    private void updatePasswordIfProvided(Customer customer, String password) {
        if (Objects.nonNull(password) && !password.isBlank()) {
            String hashedPassword = passwordHashingService.hashPassword(password);
            PasswordHash newPasswordHash = PasswordHash.fromHash(hashedPassword);
            customer.updatePassword(newPasswordHash);
        }
    }

    private void updateContactInfoIfProvided(Customer customer, CustomerUpdateRequest request) {
        if (Objects.nonNull(request.getAddress()) || Objects.nonNull(request.getPhone())) {
            customer.updateContactInfo(
                    request.getAddress(),
                    request.getPhone()
            );
        }
    }

    private void updateStatusIfProvided(Customer customer, Boolean status) {
        if (Objects.nonNull(status)) {
            if (status) {
                customer.activate();
            } else {
                customer.deactivate();
            }
        }
    }

}