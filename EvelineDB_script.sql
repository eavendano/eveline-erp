CREATE DATABASE evelinedb;
CREATE USER "eveline-erp" WITH ENCRYPTED PASSWORD 'TEwZn;V#3?roLBA6i=2pw8Zo';
GRANT CONNECT ON DATABASE evelinedb TO "eveline-erp";

DROP SEQUENCE IF EXISTS provider_id_seq;
CREATE SEQUENCE provider_id_seq MINVALUE 1 INCREMENT 1 MAXVALUE 99999;
GRANT USAGE, SELECT ON SEQUENCE provider_id_seq TO "eveline-erp";

DROP TABLE IF EXISTS provider;
CREATE TABLE provider (
  provider_id varchar(6) PRIMARY KEY NOT NULL DEFAULT 'p'||lpad(nextval('provider_id_seq'::regclass)::TEXT,5,'0'),
  name varchar(100) NOT NULL,
  description TEXT DEFAULT NULL,
  email varchar(100) NOT NULL,
  telephone1 varchar(25) NOT NULL,
  telephone2 varchar(25) DEFAULT NULL,
  telephone3 varchar(25) DEFAULT NULL,
  last_user varchar(100) NOT NULL,
  enabled boolean DEFAULT false,
  create_date timestamp(0) with time zone DEFAULT ('now'::text)::timestamp(6) with time zone,
  last_modified timestamp(0) with time zone DEFAULT ('now'::text)::timestamp(6) with time zone
);
GRANT SELECT, INSERT, UPDATE, DELETE ON provider TO "eveline-erp";


DROP SEQUENCE IF EXISTS product_id_seq;
CREATE SEQUENCE product_id_seq MINVALUE 1 INCREMENT 1 MAXVALUE 99999;
GRANT USAGE, SELECT ON SEQUENCE product_id_seq TO "eveline-erp";

DROP TABLE IF EXISTS product;
CREATE TABLE product (
  product_id varchar(6) PRIMARY KEY NOT NULL DEFAULT 'u'||lpad(nextval('product_id_seq'::regclass)::TEXT,5,'0'),
  upc varchar(12) UNIQUE NOT NULL,
  title varchar(100) NOT NULL,
  description TEXT DEFAULT NULL,
  sanitary_registry_number varchar(100) DEFAULT NULL,
  last_user varchar(100) NOT NULL,
  enabled boolean DEFAULT false,
  create_date timestamp(0) with time zone DEFAULT ('now'::text)::timestamp(6) with time zone,
  last_modified timestamp(0) with time zone DEFAULT ('now'::text)::timestamp(6) with time zone
);

alter table product
    add constraint check_upc_length check (length(upc) = 12);

CREATE INDEX upc_index ON product (upc);

GRANT SELECT, INSERT, UPDATE, DELETE ON provider TO "eveline-erp";
