package com.banking.account.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(
        name = "customer_projection",
        schema = "core",
        indexes = {
                @Index(name = "idx_customer_projection_customer_id_value", columnList = "customer_id_value"),
                @Index(name = "idx_customer_projection_status", columnList = "status"),
                @Index(name = "idx_customer_projection_last_event_at", columnList = "last_event_at")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CustomerProjectionJpaEntity extends AbstractEntity {

    @Column(name = "customer_id_value", nullable = false, length = 50)
    private String customerIdValue;

    @Column(name = "full_name", nullable = false, length = 255)
    private String fullName;

    @Column(name = "status", nullable = false, length = 20)
    private String status;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @Column(name = "last_event_at", nullable = false)
    private Instant lastEventAt;

    @PrePersist
    protected void onCreate() {
        Instant now = Instant.now();
        this.createdAt = now;
        this.updatedAt = now;
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = Instant.now();
    }

}