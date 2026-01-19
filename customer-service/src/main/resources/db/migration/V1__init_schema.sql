CREATE SCHEMA IF NOT EXISTS core;

CREATE EXTENSION IF NOT EXISTS "uuid-ossp" SCHEMA public;
CREATE EXTENSION IF NOT EXISTS "pgcrypto" SCHEMA public;

COMMENT ON SCHEMA core IS 'Customer service core business logic schema';