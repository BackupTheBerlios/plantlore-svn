Database:  localhost:C:/Program Files/Firebird/Firebird_1_5/database/plantloreHIB.fdb, User: SYSDBA

SET SQL DIALECT 3;

/* CREATE DATABASE 'localhost:C:/Program Files/Firebird/Firebird_1_5/database/plantloreHIB.fdb' DEFAULT CHARACTER SET UNICODE_FSS */


CREATE GENERATOR GEN_NEW_TABLE_ID;
CREATE GENERATOR GEN_TAUTHORS;
CREATE GENERATOR GEN_TAUTHORSOCCURENCES;
CREATE GENERATOR GEN_TGPS;
CREATE GENERATOR GEN_THABITATS;
CREATE GENERATOR GEN_THISTORY;
CREATE GENERATOR GEN_THISTORYCHANGE;
CREATE GENERATOR GEN_THISTORYCOLUMN;
CREATE GENERATOR GEN_TMETADATA;
CREATE GENERATOR GEN_TOCCURENCES;
CREATE GENERATOR GEN_TPHYTOCHORIA;
CREATE GENERATOR GEN_TPLANTS;
CREATE GENERATOR GEN_TPUBLICATIONS;
CREATE GENERATOR GEN_TTERRITORIES;
CREATE GENERATOR GEN_TUSER;
CREATE GENERATOR GEN_TVILLAGES;


/* Table: TAUTHORS, Owner: SYSDBA */
CREATE TABLE TAUTHORS (CID INTEGER NOT NULL,
        CFIRSTNAME VARCHAR(30) NOT NULL,
        CSURNAME VARCHAR(30) NOT NULL,
        CWHOLENAME VARCHAR(50),
        CORGANIZATION VARCHAR(50),
        CROLE VARCHAR(50),
        CADDRESS VARCHAR(255),
        CPHONENUMBER VARCHAR(30),
        CEMAIL VARCHAR(100),
        CURL VARCHAR(100),
        CNOTE VARCHAR(256),
PRIMARY KEY (CID));

/* Table: TAUTHORSOCCURENCES, Owner: SYSDBA */
CREATE TABLE TAUTHORSOCCURENCES (CAUTHORID INTEGER NOT NULL,
        COCCURENCEID INTEGER NOT NULL,
        CID INTEGER NOT NULL,
PRIMARY KEY (CID));

/* Table: TGPS, Owner: SYSDBA */
CREATE TABLE TGPS (CID INTEGER NOT NULL,
        CX DOUBLE PRECISION NOT NULL,
        CY DOUBLE PRECISION NOT NULL,
        CZ DOUBLE PRECISION,
PRIMARY KEY (CID));

/* Table: THABITATS, Owner: SYSDBA */
CREATE TABLE THABITATS (CID INTEGER NOT NULL,
        CTERRITORYID INTEGER NOT NULL,
        CPHYTOCHORIAID INTEGER NOT NULL,
        CQUADRANT VARCHAR(10),
        CDESCRIPTION VARCHAR(255),
        CNEARESTVILLAGEID INTEGER NOT NULL,
        CCOUNTRY VARCHAR(30),
        CALTITUDE DECIMAL(6, 2),
        CGPSID INTEGER,
        CNOTE VARCHAR(4096),
PRIMARY KEY (CID));

/* Table: THISTORY, Owner: SYSDBA */
CREATE TABLE THISTORY (CID INTEGER NOT NULL,
        CCOLUMNID INTEGER NOT NULL,
        CCHANGEID INTEGER NOT NULL,
        COLDVALUE VARCHAR(4096) NOT NULL,
        CNEWVALUE VARCHAR(4096),
PRIMARY KEY (CID));

/* Table: THISTORYCHANGE, Owner: SYSDBA */
CREATE TABLE THISTORYCHANGE (CID INTEGER NOT NULL,
        CRECORDID INTEGER NOT NULL,
        COPERATION VARCHAR(20) NOT NULL,
        CWHEN TIMESTAMP NOT NULL,
        CWHO INTEGER NOT NULL,
PRIMARY KEY (CID));

/* Table: THISTORYCOLUMN, Owner: SYSDBA */
CREATE TABLE THISTORYCOLUMN (CID INTEGER NOT NULL,
        CTABLENAME VARCHAR(20) NOT NULL,
        CCOLUMNNAME VARCHAR(20) NOT NULL,
PRIMARY KEY (CID));

/* Table: TMETADATA, Owner: SYSDBA */
CREATE TABLE TMETADATA (CID INTEGER NOT NULL,
        CTECHNICALCONTACTNAME VARCHAR(50),
        CCONTENTCONTACTNAME VARCHAR(50),
        CDATASETTITLE VARCHAR(50),
        CSOURCEINSTITUTIONID VARCHAR(50),
        CSOURCEID VARCHAR(50),
        COWNERORGANIZATIONABBREV VARCHAR(50),
        CDATECREATE TIMESTAMP,
        CDATEMODIFIED TIMESTAMP,
        CLANGUAGE VARCHAR(10),
        CRECORDBASIS VARCHAR(15),
PRIMARY KEY (CID));

/* Table: TOCCURENCES, Owner: SYSDBA */
CREATE TABLE TOCCURENCES (CID INTEGER NOT NULL,        
        CHABITATID INTEGER NOT NULL,
        CPLANTID INTEGER NOT NULL,
        CYEARCOLLECTED SMALLINT,
        CMONTHCOLLECTED SMALLINT,
        CDAYCOLLECTED SMALLINT,
        CTIMECOLLECTED TIME,
        CDATESOURCE VARCHAR(50),
        CPUBLICATIONSID INTEGER,
        CHERBARIUM VARCHAR(20),
        CCREATEWHEN TIMESTAMP NOT NULL,
        CCREATEWHO INTEGER NOT NULL,
        CUPDATEWHEN TIMESTAMP NOT NULL,
        CUPDATEWHO INTEGER NOT NULL,
        CNOTE VARCHAR(4096),
        CMETADATAID INTEGER NOT NULL,
        CUNITIDDB VARCHAR(30),
        CUNITVALUE VARCHAR(30),
PRIMARY KEY (CID));

/* Table: TPHYTOCHORIA, Owner: SYSDBA */
CREATE TABLE TPHYTOCHORIA (CID INTEGER NOT NULL,
        CCODE VARCHAR(5) NOT NULL,
        CNAME VARCHAR(50) NOT NULL,
PRIMARY KEY (CID));

/* Table: TPLANTS, Owner: SYSDBA */
CREATE TABLE TPLANTS (CID INTEGER NOT NULL,
        CADOPTEDNAME VARCHAR(30) NOT NULL,
        CCZECHNAME VARCHAR(30),
        CPUBLISHABLENAME VARCHAR(30),
        CABBREVIATION VARCHAR(15),
        CNOTE VARCHAR(256),
PRIMARY KEY (CID));

/* Table: TPUBLICATIONS, Owner: SYSDBA */
CREATE TABLE TPUBLICATIONS (CID INTEGER NOT NULL,
        CCOLLECTIONNAME VARCHAR(30),
        CCOLLECTIONYEARPUBLICATION INTEGER,
        CJOURNALNAME VARCHAR(60),
        CJOURNALAUTHORNAME VARCHAR(30),
PRIMARY KEY (CID));

/* Table: TTERRITORIES, Owner: SYSDBA */
CREATE TABLE TTERRITORIES (CID INTEGER NOT NULL,
        CNAME VARCHAR(100) NOT NULL,
PRIMARY KEY (CID));

/* Table: TUSER, Owner: SYSDBA */
CREATE TABLE TUSER (CID INTEGER NOT NULL,
        CLOGIN VARCHAR(20) NOT NULL,
        CFIRSTNAME VARCHAR(20),
        CSURNAME VARCHAR(30),
        CEMAIL VARCHAR(50),
        CADDRESS VARCHAR(50),
        CWHENCREATE TIMESTAMP NOT NULL,
        CWHENDROP TIMESTAMP,       
        CNOTE VARCHAR(4096),
PRIMARY KEY (CID));

/* Table: TVILLAGES, Owner: SYSDBA */
CREATE TABLE TVILLAGES (CID INTEGER NOT NULL,
        CNAME VARCHAR(30) NOT NULL,
PRIMARY KEY (CID));

ALTER TABLE TAUTHORSOCCURENCES ADD FOREIGN KEY (CAUTHORID) REFERENCES TAUTHORS (CID);

ALTER TABLE TAUTHORSOCCURENCES ADD FOREIGN KEY (COCCURENCEID) REFERENCES TOCCURENCES (CID);

ALTER TABLE THABITATS ADD FOREIGN KEY (CTERRITORYID) REFERENCES TTERRITORIES (CID);

ALTER TABLE THABITATS ADD FOREIGN KEY (CPHYTOCHORIAID) REFERENCES TPHYTOCHORIA (CID);

ALTER TABLE THABITATS ADD FOREIGN KEY (CNEARESTVILLAGEID) REFERENCES TVILLAGES (CID);

ALTER TABLE THABITATS ADD FOREIGN KEY (CGPSID) REFERENCES TGPS (CID);

ALTER TABLE THISTORY ADD FOREIGN KEY (CCOLUMNID) REFERENCES THISTORYCOLUMN (CID);

ALTER TABLE THISTORY ADD FOREIGN KEY (CCHANGEID) REFERENCES THISTORYCHANGE (CID);

ALTER TABLE THISTORYCHANGE ADD FOREIGN KEY (CWHO) REFERENCES TUSER (CID);

ALTER TABLE TOCCURENCES ADD FOREIGN KEY (CHABITATID) REFERENCES THABITATS (CID);

ALTER TABLE TOCCURENCES ADD FOREIGN KEY (CPLANTID) REFERENCES TPLANTS (CID);

ALTER TABLE TOCCURENCES ADD FOREIGN KEY (CCREATEWHO) REFERENCES TUSER (CID);

ALTER TABLE TOCCURENCES ADD FOREIGN KEY (CUPDATEWHO) REFERENCES TUSER (CID);

ALTER TABLE TOCCURENCES ADD FOREIGN KEY (CMETADATAID) REFERENCES TMETADATA (CID);

ALTER TABLE TOCCURENCES ADD FOREIGN KEY (CPUBLICATIONSID) REFERENCES TPUBLICATIONS (CID);

/* Grant role for this database */

/* Role: BOTANIK, Owner: SYSDBA */
CREATE ROLE BOTANIK;

/* Grant permissions for this database */
GRANT DELETE, INSERT, SELECT, UPDATE, REFERENCES ON TAUTHORS TO ROLE BOTANIK;
GRANT DELETE, INSERT, SELECT, UPDATE, REFERENCES ON TAUTHORSOCCURENCES TO ROLE BOTANIK;
GRANT DELETE, INSERT, SELECT, UPDATE, REFERENCES ON TGPS TO ROLE BOTANIK;
GRANT DELETE, INSERT, SELECT, UPDATE, REFERENCES ON THABITATS TO ROLE BOTANIK;
GRANT DELETE, INSERT, SELECT, UPDATE, REFERENCES ON THISTORY TO ROLE BOTANIK;
GRANT DELETE, INSERT, SELECT, UPDATE, REFERENCES ON THISTORYCHANGE TO ROLE BOTANIK;
GRANT DELETE, INSERT, SELECT, UPDATE, REFERENCES ON THISTORYCOLUMN TO ROLE BOTANIK;
GRANT DELETE, INSERT, SELECT, UPDATE, REFERENCES ON TMETADATA TO ROLE BOTANIK;
GRANT DELETE, INSERT, SELECT, UPDATE, REFERENCES ON TOCCURENCES TO ROLE BOTANIK;
GRANT DELETE, INSERT, SELECT, UPDATE, REFERENCES ON TPHYTOCHORIA TO ROLE BOTANIK;
GRANT DELETE, INSERT, SELECT, UPDATE, REFERENCES ON TPLANTS TO ROLE BOTANIK;
GRANT DELETE, INSERT, SELECT, UPDATE, REFERENCES ON TTERRITORIES TO ROLE BOTANIK;
GRANT DELETE, INSERT, SELECT, UPDATE, REFERENCES ON TUSER TO ROLE BOTANIK;
GRANT DELETE, INSERT, SELECT, UPDATE, REFERENCES ON TVILLAGES TO ROLE BOTANIK;
GRANT BOTANIK TO FRAKTALEK;
GRANT BOTANIK TO LADA;
GRANT BOTANIK TO REIMEI;
GRANT BOTANIK TO SYSDBA;