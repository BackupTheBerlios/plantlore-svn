SET client_encoding = 'UNICODE';

SET search_path = public, pg_catalog;

SET default_tablespace = '';

SET default_with_oids = true;

CREATE ROLE Plantlore_Role_www WITH NOSUPERUSER NOCREATEDB NOCREATEROLE LOGIN;
CREATE ROLE  Plantlore_Role_User WITH NOSUPERUSER NOCREATEDB NOCREATEROLE LOGIN;
CREATE ROLE Plantlore_Role_Admin WITH SUPERUSER CREATEDB CREATEROLE LOGIN;
