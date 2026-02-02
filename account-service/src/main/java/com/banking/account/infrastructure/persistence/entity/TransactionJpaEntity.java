package com.banking.account.infrastructure.persistence.entity;

import com.banking.account.domain.model.TransactionType;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(
        name = "transactions",
        schema = "core",
        indexes = {
                @Index(name = "idx_transaction_account_id", columnList = "account_id"),
                @Index(name = "idx_transaction_type", columnList = "type"),
                @Index(name = "idx_transaction_created_at", columnList = "created_at"),
                @Index(name = "idx_transaction_reference", columnList = "reference")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransactionJpaEntity extends AbstractAuditableEntity {

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, length = 20)
    private TransactionType type;

    @Column(name = "amount", nullable = false, precision = 19, scale = 2)
    private BigDecimal amount;

    @Column(name = "balance_before", nullable = false, precision = 19, scale = 2)
    private BigDecimal balanceBefore;

    @Column(name = "balance_after", nullable = false, precision = 19, scale = 2)
    private BigDecimal balanceAfter;

    @Column(name = "reference", length = 255)
    private String reference;

    @Column(name = "account_id", nullable = false)
    private UUID accountId;

}