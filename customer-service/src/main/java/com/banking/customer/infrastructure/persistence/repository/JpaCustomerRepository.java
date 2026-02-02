package com.banking.customer.infrastructure.persistence.repository;

import com.banking.customer.infrastructure.persistence.entity.CustomerJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface JpaCustomerRepository extends JpaRepository<CustomerJpaEntity, UUID>, JpaSpecificationExecutor<CustomerJpaEntity> {

    Optional<CustomerJpaEntity> findByCustomerId(String customerId);

    Optional<CustomerJpaEntity> findByIdentification(String identification);

    boolean existsByCustomerId(String customerId);

    boolean existsByIdentification(String identification);

}