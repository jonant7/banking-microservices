CREATE TABLE core.persons
(
    id             UUID PRIMARY KEY      DEFAULT uuid_generate_v4(),

    name           VARCHAR(100) NOT NULL,
    last_name      VARCHAR(100) NOT NULL,
    gender         VARCHAR(10)  NOT NULL CHECK (gender IN ('MALE', 'FEMALE')),
    birth_date     DATE         NOT NULL,
    identification VARCHAR(20)  NOT NULL UNIQUE,
    address        TEXT,
    phone          VARCHAR(20),

    created_at     TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at     TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_persons_name ON core.persons (name);
CREATE INDEX idx_persons_last_name ON core.persons (last_name);

CREATE TABLE core.customers
(
    id            UUID PRIMARY KEY      DEFAULT uuid_generate_v4(),
    customer_id   VARCHAR(50)  NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    status        VARCHAR(10)  NOT NULL CHECK (status IN ('ACTIVE', 'INACTIVE')),
    created_at    TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at    TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_customers_persons FOREIGN KEY (id)
        REFERENCES core.persons (id)
        ON DELETE CASCADE
        ON UPDATE CASCADE
);

CREATE INDEX idx_customers_status ON core.customers (status);