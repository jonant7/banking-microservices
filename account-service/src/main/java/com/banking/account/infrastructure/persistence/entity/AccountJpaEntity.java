package com.banking.account.infrastructure.persistence.entity;

import com.banking.account.domain.model.AccountStatus;
import com.banking.account.domain.model.AccountType;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(
        name = "accounts",
        schema = "core",
        indexes = {
                @Index(name = "idx_number", columnList = "number"),
                @Index(name = "idx_customer_id", columnList = "customer_id"),
                @Index(name = "idx_status", columnList = "status")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccountJpaEntity extends AbstractAuditableEntity {

    @Column(name = "number", unique = true, nullable = false, length = 20)
    private String number;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, length = 20)
    private AccountType type;

    @Column(name = "initial_balance", nullable = false, precision = 19, scale = 2)
    private BigDecimal initialBalance;

    @Column(name = "current_balance", nullable = false, precision = 19, scale = 2)
    private BigDecimal currentBalance;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private AccountStatus status;

    @Column(name = "customer_id", nullable = false)
    private UUID customerId;

}