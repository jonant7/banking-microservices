package com.banking.customer.infrastructure.persistence.inmemory;

import com.banking.customer.domain.model.Customer;
import com.banking.customer.domain.model.CustomerId;
import com.banking.customer.domain.model.Identification;
import com.banking.customer.domain.repository.CustomerRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class InMemoryCustomerRepository implements CustomerRepository {

    private final Map<UUID, Customer> store = new ConcurrentHashMap<>();

    @Override
    public Customer save(Customer customer) {
        Objects.requireNonNull(customer, "Customer must not be null");
        store.put(customer.getId(), customer);
        return customer;
    }

    @Override
    public Optional<Customer> findById(UUID id) {
        Objects.requireNonNull(id, "Customer id must not be null");
        return Optional.ofNullable(store.get(id));
    }


    @Override
    public Page<Customer> findAll(Object filter, Pageable pageable) {
        List<Customer> customers = new ArrayList<>(store.values());

        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), customers.size());

        if (start > end) {
            return Page.empty(pageable);
        }

        return new PageImpl<>(
                customers.subList(start, end),
                pageable,
                customers.size()
        );
    }

    @Override
    public boolean existsByCustomerId(CustomerId customerId) {
        Objects.requireNonNull(customerId, "CustomerId must not be null");
        return store.values().stream()
                .anyMatch(c -> c.getCustomerId().equals(customerId));
    }

    @Override
    public boolean existsByIdentification(Identification identification) {
        Objects.requireNonNull(identification, "Identification must not be null");
        return store.values().stream()
                .anyMatch(c -> c.getIdentification().equals(identification));
    }

}