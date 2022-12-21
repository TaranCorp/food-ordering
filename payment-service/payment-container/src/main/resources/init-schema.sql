DROP SCHEMA IF EXISTS payment CASCADE;

CREATE SCHEMA payment;

CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

DROP TYPE IF EXISTS payment_status;

CREATE TYPE payment_status AS ENUM ('COMPLETED', 'CANCELLED', 'FAILED');

DROP TABLE IF EXISTS "payment".payments CASCADE;

CREATE TABLE "payment".payments
(
    id uuid NOT NULL,
    customer_id uuid NOT NULL,
    order_id uuid NOT NULL,
    price numeric(10,2) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    status payment_status NOT NULL,
    CONSTRAINT payments_pkey PRIMARY KEY (id)
);

DROP TABLE IF EXISTS "payment".credit_entry CASCADE;

CREATE TABLE "payment".credit_entry
(
    id uuid NOT NULL,
    customer_id uuid NOT NULL,
    total_credit_amount numeric(10,2) NOT NULL,
    CONSTRAINT credit_entry_pkey PRIMARY KEY (id)
);

DROP TYPE IF EXISTS transaction_type;

CREATE TYPE transaction_type AS ENUM ('DEBIT', 'CREDIT');

DROP TABLE IF EXISTS "payment".credit_history CASCADE;

CREATE TABLE "payment".credit_history
(
    id uuid NOT NULL,
    customer_id uuid NOT NULL,
    amount numeric(10,2) NOT NULL,
    type transaction_type NOT NULL,
    CONSTRAINT credit_history_pkey PRIMARY KEY (id)
);

drop type if exists outbox_status;

create type outbox_status as enum ('STARTED', 'COMPLETED', 'FAILED');

drop table if exists payment.order_outbox cascade;

create table payment.order_outbox (
    id uuid not null primary key,
    saga_id uuid not null,
    created_at timestamp with time zone not null,
    processed_at timestamp with time zone,
    type character varying collate pg_catalog.default not null,
    payload jsonb not null,
    outbox_status outbox_status not null,
    payment_status payment_status not null,
    version integer not null
);

create index payment_order_outbox_saga_status
    on payment.order_outbox
    (type, payment_status);

create unique index payment_order_outbox_saga_id_payment_status_outbox_status
    on payment.order_outbox
    (type, saga_id, payment_status, outbox_status);