CREATE TABLE core.customer_projection
(
    id                UUID PRIMARY KEY,
    customer_id_value VARCHAR(50)  NOT NULL,
    full_name         VARCHAR(255) NOT NULL,
    status            VARCHAR(20)  NOT NULL CHECK (status IN ('ACTIVE', 'INACTIVE')),
    created_at        TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at        TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_event_at     TIMESTAMP    NOT NULL
);

CREATE INDEX idx_customer_projection_customer_id_value ON core.customer_projection (customer_id_value);
CREATE INDEX idx_customer_projection_status ON core.customer_projection (status);
CREATE INDEX idx_customer_projection_last_event_at ON core.customer_projection (last_event_at);