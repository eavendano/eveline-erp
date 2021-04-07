CREATE DATABASE evelinedb;
CREATE USER "eveline-erp" WITH ENCRYPTED PASSWORD 'TEwZn;V#3?roLBA6i=2pw8Zo';
GRANT CONNECT ON DATABASE evelinedb TO "eveline-erp";

CREATE EXTENSION postgis;

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
  last_modified timestamp(0) with time zone DEFAULT ('now'::text)::timestamp(6) with time zone,
  UNIQUE (provider_id)
);
GRANT SELECT, INSERT, UPDATE, DELETE ON provider TO "eveline-erp";

DROP INDEX IF EXISTS provider_id_index;
CREATE INDEX provider_id_index ON provider(provider_id);

DROP INDEX IF EXISTS provider_id_active_index;
CREATE INDEX provider_id_active_index ON provider USING btree(provider_id) WHERE enabled IS TRUE;

DROP INDEX IF EXISTS provider_last_modified_index;
CREATE INDEX provider_last_modified_index ON provider USING btree (last_modified DESC NULLS LAST);

DROP FUNCTION IF EXISTS insert_create_date();
CREATE FUNCTION insert_create_date()
    RETURNS trigger
    LANGUAGE 'plpgsql'
    COST 100
    VOLATILE NOT LEAKPROOF
AS $BODY$
BEGIN
    NEW.create_date := CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$BODY$;
GRANT EXECUTE ON FUNCTION insert_create_date() TO "eveline-erp";

DROP FUNCTION IF EXISTS update_create_date();
CREATE FUNCTION update_create_date()
    RETURNS trigger
    LANGUAGE 'plpgsql'
    COST 100
    VOLATILE NOT LEAKPROOF
AS $BODY$
BEGIN
    NEW.create_date := OLD.create_date;
    RETURN NEW;
END;
$BODY$;
GRANT EXECUTE ON FUNCTION update_create_date() TO "eveline-erp";

DROP FUNCTION update_last_modified();
CREATE FUNCTION update_last_modified()
    RETURNS trigger
    LANGUAGE 'plpgsql'
    COST 100
    VOLATILE NOT LEAKPROOF
AS $BODY$
BEGIN
    /*
        Force last_modified timestamp to be accurately updated,
        using wall clock time, not start of transaction as given
        by CURRENT_TIMESTAMP, now(), etc.
    */
    NEW.last_modified := timeofday()::timestamp;
    RETURN NEW;
END;
$BODY$;
GRANT EXECUTE ON FUNCTION update_last_modified() TO "eveline-erp";

DROP TRIGGER IF EXISTS provider_insert_create_date ON provider;
CREATE TRIGGER provider_insert_create_date
    BEFORE INSERT
    ON provider
    FOR EACH ROW
EXECUTE PROCEDURE insert_create_date();

DROP TRIGGER IF EXISTS provider_update_create_date ON provider;
CREATE TRIGGER provider_update_create_date
    BEFORE UPDATE
    ON provider
    FOR EACH ROW
EXECUTE PROCEDURE update_create_date();

DROP TRIGGER IF EXISTS provider_last_modified ON provider;
CREATE TRIGGER provider_last_modified
    BEFORE INSERT OR UPDATE
    ON provider
    FOR EACH ROW
EXECUTE PROCEDURE update_last_modified();



DROP SEQUENCE IF EXISTS product_id_seq;
CREATE SEQUENCE product_id_seq MINVALUE 1 INCREMENT 1 MAXVALUE 99999;
GRANT USAGE, SELECT ON SEQUENCE product_id_seq TO "eveline-erp";

DROP TABLE IF EXISTS product;
CREATE TABLE product (
  product_id varchar(6) PRIMARY KEY NOT NULL DEFAULT 's'||lpad(nextval('product_id_seq'::regclass)::TEXT,9,'0'),
  upc varchar(12) UNIQUE NOT NULL,
  title varchar(100) NOT NULL,
  description TEXT DEFAULT NULL,
  sanitary_registry_number varchar(100) DEFAULT NULL,
  last_user varchar(100) NOT NULL,
  enabled boolean DEFAULT false,
  create_date timestamp(0) with time zone DEFAULT ('now'::text)::timestamp(6) with time zone,
  last_modified timestamp(0) with time zone DEFAULT ('now'::text)::timestamp(6) with time zone,
  UNIQUE (product_id),
  UNIQUE (upc),
  UNIQUE (sanitary_registry_number),
  CONSTRAINT check_upc_length CHECK (length(upc) = 12)
);
GRANT SELECT, INSERT, UPDATE, DELETE ON product TO "eveline-erp";

DROP INDEX IF EXISTS product_id_index;
CREATE INDEX product_id_index ON product(product_id);

DROP INDEX IF EXISTS product_id_active_index;
CREATE INDEX product_id_active_index ON product USING btree(product_id) WHERE enabled IS TRUE;

DROP INDEX IF EXISTS upc_index;
CREATE INDEX upc_index ON product(upc);

DROP INDEX IF EXISTS product_last_modified_index;
CREATE INDEX product_last_modified_index ON product USING btree (last_modified DESC NULLS LAST);

DROP TRIGGER IF EXISTS product_insert_create_date ON product;
CREATE TRIGGER product_insert_create_date
    BEFORE INSERT
    ON product
    FOR EACH ROW
EXECUTE PROCEDURE insert_create_date();

DROP TRIGGER IF EXISTS product_update_create_date ON product;
CREATE TRIGGER product_update_create_date
    BEFORE UPDATE
    ON product
    FOR EACH ROW
EXECUTE PROCEDURE update_create_date();

DROP TRIGGER IF EXISTS product_last_modified ON product;
CREATE TRIGGER product_last_modified
    BEFORE INSERT OR UPDATE
    ON product
    FOR EACH ROW
EXECUTE PROCEDURE update_last_modified();

DROP TABLE IF EXISTS product_provider_assignation;
CREATE TABLE product_provider_assignation (
    id SERIAL PRIMARY KEY,
    product_id varchar(6),
    provider_id varchar(6),
    CONSTRAINT product_id_fk
        FOREIGN KEY(product_id)
            REFERENCES product(product_id),
    CONSTRAINT provider_id_fk
        FOREIGN KEY(provider_id)
            REFERENCES provider(provider_id)
);

GRANT SELECT, INSERT, UPDATE, DELETE ON product_provider_assignation TO "eveline-erp";
GRANT USAGE, SELECT ON SEQUENCE product_provider_assignation_id_seq TO "eveline-erp";


DROP SEQUENCE IF EXISTS warehouse_id_seq;
CREATE SEQUENCE warehouse_id_seq MINVALUE 1 INCREMENT 1 MAXVALUE 99999;
GRANT USAGE, SELECT ON SEQUENCE warehouse_id_seq TO "eveline-erp";

DROP TABLE IF EXISTS warehouse;

-- postgis 3.1.1

CREATE TABLE warehouse (
                         warehouse_id varchar(6) PRIMARY KEY NOT NULL DEFAULT 'w'||lpad(nextval('warehouse_id_seq'::regclass)::TEXT,5,'0'),
                         name varchar(100) NOT NULL,
                         description TEXT DEFAULT NULL,
                         address1 TEXT NOT NULL,
                         address2 TEXT DEFAULT NULL,
                         last_user varchar(100) NOT NULL,
                         telephone1 varchar(25) NOT NULL,
                         telephone2 varchar(25) DEFAULT NULL,
                         geolocation GEOGRAPHY(Point) NOT NULL,
                         notes TEXT DEFAULT NULL,
                         enabled boolean DEFAULT false,
                         create_date timestamp(0) with time zone DEFAULT ('now'::text)::timestamp(6) with time zone,
                         last_modified timestamp(0) with time zone DEFAULT ('now'::text)::timestamp(6) with time zone,
                         UNIQUE (warehouse_id),
                         UNIQUE (name)
);
GRANT SELECT, INSERT, UPDATE, DELETE ON warehouse TO "eveline-erp";

INSERT INTO warehouse (name, address1, last_user, telephone1, geolocation, notes, enabled) VALUES ( 'test','test','user','123', 'POINT(-118.4079 33.9434)', 'notes', true);

DROP INDEX IF EXISTS warehouse_id_index;
CREATE INDEX warehouse_id_index ON warehouse(warehouse_id);

DROP INDEX IF EXISTS warehouse_id_active_index;
CREATE INDEX warehouse_id_active_index ON warehouse USING btree(warehouse_id) WHERE enabled IS TRUE;

DROP INDEX IF EXISTS warehouse_name_index;
CREATE INDEX warehouse_name_index ON warehouse(name);

DROP INDEX IF EXISTS warehouse_last_modified_index;
CREATE INDEX warehouse_last_modified_index ON warehouse USING btree (last_modified DESC NULLS LAST);

DROP TRIGGER IF EXISTS warehouse_insert_create_date ON warehouse;
CREATE TRIGGER warehouse_insert_create_date
    BEFORE INSERT
    ON warehouse
    FOR EACH ROW
EXECUTE PROCEDURE insert_create_date();

DROP TRIGGER IF EXISTS warehouse_update_create_date ON warehouse;
CREATE TRIGGER warehouse_update_create_date
    BEFORE UPDATE
    ON warehouse
    FOR EACH ROW
EXECUTE PROCEDURE update_create_date();

DROP TRIGGER IF EXISTS warehouse_last_modified ON warehouse;
CREATE TRIGGER warehouse_last_modified
    BEFORE INSERT OR UPDATE
    ON warehouse
    FOR EACH ROW
EXECUTE PROCEDURE update_last_modified();