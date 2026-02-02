package com.banking.customer.infrastructure.persistence.entity;

import com.banking.customer.domain.model.CustomerStatus;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
        name = "customers",
        schema = "core",
        indexes = {
                @Index(name = "idx_customer_id", columnList = "customer_id"),
                @Index(name = "idx_customers_status", columnList = "status")
        }
)
@PrimaryKeyJoinColumn(name = "id")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CustomerJpaEntity extends PersonJpaEntity {

    @Column(name = "customer_id", unique = true, nullable = false, length = 50)
    private String customerId;

    @Column(name = "password_hash", nullable = false, length = 255)
    private String passwordHash;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 10)
    private CustomerStatus status;

}