package com.banking.customer.domain.model;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.Instant;
import java.util.*;

@Getter
@Setter(AccessLevel.PROTECTED)
@SuperBuilder
public abstract class AggregateRoot {

    private UUID id;
    private Instant createdAt;
    private Instant updatedAt;

    @Getter(AccessLevel.NONE)
    @Builder.Default
    private List<Object> domainEvents = new ArrayList<>();

    protected AggregateRoot() {
        this.domainEvents = new ArrayList<>();
    }

    protected void markAsCreated() {
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
    }

    protected void markAsUpdated() {
        this.updatedAt = Instant.now();
    }

    protected void registerEvent(Object event) {
        if (Objects.isNull(this.domainEvents)) {
            this.domainEvents = new ArrayList<>();
        }
        this.domainEvents.add(event);
    }

    public List<Object> getDomainEvents() {
        if (Objects.isNull(this.domainEvents)) {
            return Collections.emptyList();
        }
        return Collections.unmodifiableList(domainEvents);
    }

    public void clearDomainEvents() {
        if (Objects.nonNull(this.domainEvents)) {
            this.domainEvents.clear();
        }
    }

}