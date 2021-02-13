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
  product_id varchar(6) PRIMARY KEY NOT NULL DEFAULT 'u'||lpad(nextval('product_id_seq'::regclass)::TEXT,5,'0'),
  provider_id varchar(6) NOT NULL,
  upc varchar(12) UNIQUE NOT NULL,
  title varchar(100) NOT NULL,
  description TEXT DEFAULT NULL,
  sanitary_registry_number varchar(100) DEFAULT NULL,
  last_user varchar(100) NOT NULL,
  enabled boolean DEFAULT false,
  create_date timestamp(0) with time zone DEFAULT ('now'::text)::timestamp(6) with time zone,
  last_modified timestamp(0) with time zone DEFAULT ('now'::text)::timestamp(6) with time zone,
  CONSTRAINT fk_provider
                     FOREIGN KEY(provider_id)
                     REFERENCES provider(provider_id),
  CONSTRAINT check_upc_length
      check (length(upc) = 12)

);

CREATE INDEX upc_index ON product (upc);

GRANT SELECT, INSERT, UPDATE, DELETE ON provider TO "eveline-erp";
