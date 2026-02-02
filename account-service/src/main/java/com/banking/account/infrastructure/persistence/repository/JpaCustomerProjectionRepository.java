package com.banking.account.infrastructure.persistence.repository;

import com.banking.account.infrastructure.persistence.entity.CustomerProjectionJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface JpaCustomerProjectionRepository extends JpaRepository<CustomerProjectionJpaEntity, UUID> {

    Optional<CustomerProjectionJpaEntity> findByCustomerIdValue(String customerIdValue);

    boolean existsByCustomerIdValue(String customerIdValue);

}