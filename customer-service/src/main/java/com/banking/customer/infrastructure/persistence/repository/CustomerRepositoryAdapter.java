package com.banking.customer.infrastructure.persistence.repository;

import com.banking.customer.application.dto.CustomerFilter;
import com.banking.customer.domain.model.Customer;
import com.banking.customer.domain.model.CustomerId;
import com.banking.customer.domain.model.Identification;
import com.banking.customer.domain.repository.CustomerRepository;
import com.banking.customer.infrastructure.persistence.entity.CustomerJpaEntity;
import com.banking.customer.infrastructure.persistence.mapper.CustomerMapper;
import com.banking.customer.infrastructure.persistence.specification.CustomerSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Primary
public class CustomerRepositoryAdapter implements CustomerRepository {

    private final JpaCustomerRepository jpaRepository;
    private final CustomerMapper mapper;

    @Override
    @Transactional
    public Customer save(Customer customer) {
        CustomerJpaEntity entity;

        if (Objects.nonNull(customer.getId())) {
            Optional<CustomerJpaEntity> existingEntity = jpaRepository.findById(customer.getId());

            if (existingEntity.isPresent()) {
                entity = existingEntity.get();
                mapper.updateEntityFromDomain(customer, entity);
            } else {
                entity = mapper.toEntity(customer);
            }
        } else {
            entity = mapper.toEntity(customer);
        }

        jpaRepository.save(entity);

        return customer;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Customer> findById(UUID id) {
        return jpaRepository.findById(id)
                .map(mapper::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Customer> findAll(Object filter, Pageable pageable) {
        if (Objects.isNull(filter)) {
            return findAll(pageable);
        }

        if (!(filter instanceof CustomerFilter customerFilter)) {
            throw new IllegalArgumentException("Filter must be of type CustomerFilter");
        }

        Specification<CustomerJpaEntity> spec = CustomerSpecification.withFilter(customerFilter);

        return jpaRepository.findAll(spec, pageable)
                .map(mapper::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByCustomerId(CustomerId customerId) {
        return jpaRepository.existsByCustomerId(customerId.value());
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByIdentification(Identification identification) {
        return jpaRepository.existsByIdentification(identification.value());
    }

    private Page<Customer> findAll(Pageable pageable) {
        return jpaRepository.findAll(pageable)
                .map(mapper::toDomain);
    }

}