CREATE TABLE core.accounts
(
    id              UUID PRIMARY KEY        DEFAULT uuid_generate_v4(),
    number          VARCHAR(20)    NOT NULL UNIQUE,
    type            VARCHAR(20)    NOT NULL CHECK (type IN ('SAVINGS', 'CHECKING')),
    initial_balance DECIMAL(19, 2) NOT NULL,
    current_balance DECIMAL(19, 2) NOT NULL,
    status          VARCHAR(20)    NOT NULL CHECK (status IN ('ACTIVE', 'INACTIVE', 'SUSPENDED', 'CLOSED')),
    customer_id     UUID           NOT NULL,
    created_at      TIMESTAMP      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP      NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_number ON core.accounts (number);
CREATE INDEX idx_customer_id ON core.accounts (customer_id);
CREATE INDEX idx_status ON core.accounts (status);

CREATE TABLE core.transactions
(
    id             UUID PRIMARY KEY        DEFAULT uuid_generate_v4(),
    type           VARCHAR(20)    NOT NULL CHECK (type IN ('DEPOSIT', 'WITHDRAWAL')),
    amount         DECIMAL(19, 2) NOT NULL,
    balance_before DECIMAL(19, 2) NOT NULL,
    balance_after  DECIMAL(19, 2) NOT NULL,
    reference      VARCHAR(255),
    account_id     UUID           NOT NULL,
    created_at     TIMESTAMP      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at     TIMESTAMP      NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_transactions_accounts FOREIGN KEY (account_id)
        REFERENCES core.accounts (id)
        ON DELETE CASCADE
        ON UPDATE CASCADE
);

CREATE INDEX idx_transaction_account_id ON core.transactions (account_id);
CREATE INDEX idx_transaction_type ON core.transactions (type);
CREATE INDEX idx_transaction_created_at ON core.transactions (created_at);
CREATE INDEX idx_transaction_reference ON core.transactions (reference);