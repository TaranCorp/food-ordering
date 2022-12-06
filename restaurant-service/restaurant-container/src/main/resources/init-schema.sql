DROP SCHEMA IF EXISTS restaurant CASCADE;

CREATE SCHEMA restaurant;

create extension if not exists "uuid-ossp";

drop table if exists restaurant.restaurants;

create table restaurant.restaurants (
    id uuid not null primary key,
    name character varying collate pg_catalog."default" NOT NULL,
    active boolean not null
);

drop type if exists order_status;

create type order_status as enum ('APPROVED', 'REJECTED');

drop table if exists restaurant.order_approval cascade;

create table restaurant.order_approval (
    id uuid not null primary key,
    restaurant_id uuid not null,
    order_id uuid not null,
    status order_approval not null
);

drop table if exists restaurant.products;

create table restaurant.products (
    id uuid not null primary key,
    name character varying collate pg_catalog."default" not null,
    price numeric(10, 2) not null,
    available boolean not null
);


drop table if exists restaurant.restaurant_products cascade;

create table restaurant.restaurant_products (
    id uuid not null primary key,
    restaurant_id uuid not null,
    product_id uuid not null
);

alter table restaurant.restaurant_products
    add constraint "FK_RESTAURANT_ID" foreign key (restaurant_id)
    references restaurant.restaurants (id) match simple
    on update no action
    on delete restrict
    not valid;

alter table restaurant.restaurant_products
    add constraint "FK_PRODUCT_ID" foreign key (product_id)
    references restaurant.products (id) match simple
    on update no action
    in delete restrict
    not valid;

create materialized view restaurant.order_restaurant_m_view
tablespace pg_default
as
 select r.id as restaurant_id,
 r.name as restaurant_name,
 r.active as restaurant_active,
 p.id as product_id,
 p.name as product_name,
 p.price as product_price,
 p.available as product_available
from restaurant.restaurants r,
restaurant.products p,
restaurant.restaurant_products rp,
where r.id = rp.restaurant_id and p.id = rp.product_id
with data;

refresh materialized view restaurant.refresh_order_restaurant_m_view;

create or replace function restaurant.refresh_order_restaurant_m_view()
returns trigger
as '
begin
refresh materialized view restaurant.order_restaurant_m_view;
return null;
end;
' language plpgsql;

drop trigger if exists refresh_order_restaurant_m_view on restaurant.restaurant_products;

create trigger refresh_order_restaurant_m_view
after insert or update or delete or truncate
on restaurant.restaurant_products for each statement
execute procedure restaurant.refresh_order_restaurant_m_view();




