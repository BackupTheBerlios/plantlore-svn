
SET SQL DIALECT 3;
SET NAMES UNICODE_FSS;


CREATE GENERATOR GEN_TAUTHORS;
CREATE GENERATOR GEN_TAUTHORSOCCURRENCES;
CREATE GENERATOR GEN_THABITATS;
CREATE GENERATOR GEN_THISTORY;
CREATE GENERATOR GEN_THISTORYCHANGE;
CREATE GENERATOR GEN_THISTORYCOLUMN;
CREATE GENERATOR GEN_TMETADATA;
CREATE GENERATOR GEN_TOCCURRENCES;
CREATE GENERATOR GEN_TPHYTOCHORIA;
CREATE GENERATOR GEN_TPLANTS;
CREATE GENERATOR GEN_TPUBLICATIONS;
CREATE GENERATOR GEN_TRIGHT;
CREATE GENERATOR GEN_TTERRITORIES;
CREATE GENERATOR GEN_TUSER;
CREATE GENERATOR GEN_TVILLAGES;


/* Table: TAUTHORS, Owner: SYSDBA */
CREATE TABLE TAUTHORS (
    CID               INTEGER NOT NULL,
    CFIRSTNAME        VARCHAR(20) NOT NULL,
    CSURNAME          VARCHAR(30) NOT NULL,
    CWHOLENAME        VARCHAR(50),
    CORGANIZATION     VARCHAR(50),
    CTELEPHONENUMBER  VARCHAR(20),
    CROLE             VARCHAR(30),
    CADDRESS          VARCHAR(255),
    CEMAIL            VARCHAR(100),
    CURL              VARCHAR(255),
    CNOTE             VARCHAR(4096),
PRIMARY KEY (CID));

/* Table: TAUTHORSOCCURRENCES, Owner: SYSDBA */
CREATE TABLE TAUTHORSOCCURRENCES (
    CAUTHORID          INTEGER NOT NULL,
    COCCURRENCEID      INTEGER NOT NULL,
    CID                INTEGER NOT NULL,
    CROLE              VARCHAR(20),
    CRESULTREVISITION  VARCHAR(30),
PRIMARY KEY (CID));

/* Table: THABITATS, Owner: SYSDBA */
CREATE TABLE THABITATS (
    CID                INTEGER NOT NULL,
    CTERRITORYID       INTEGER NOT NULL,
    CPHYTOCHORIAID     INTEGER NOT NULL,
    CQUADRANT          VARCHAR(10),
    CDESCRIPTION       VARCHAR(255),
    CNEARESTVILLAGEID  INTEGER NOT NULL,
    CCOUNTRY           VARCHAR(30),
    CALTITUDE          DECIMAL(6,2),
    CLATITUDE          DOUBLE PRECISION,
    CLONGITUDE         DOUBLE PRECISION,
    CNOTE              VARCHAR(4096),
    CDELETE            SMALLINT,
PRIMARY KEY (CID));

/* Table: THISTORY, Owner: SYSDBA */
CREATE TABLE THISTORY (
        CID       INTEGER NOT NULL,
        CCOLUMNID INTEGER NOT NULL,
        CCHANGEID INTEGER NOT NULL,
        COLDVALUE VARCHAR(4096),
        CNEWVALUE VARCHAR(4096),
PRIMARY KEY (CID));

/* Table: THISTORYCHANGE, Owner: SYSDBA */
CREATE TABLE THISTORYCHANGE (
    CID            INTEGER NOT NULL,
    COCCURRENCEID  INTEGER NOT NULL,
    CRECORDID      INTEGER NOT NULL,
    COPERATION     VARCHAR(10) NOT NULL,
    CWHEN          TIMESTAMP NOT NULL,
    CWHO           INTEGER NOT NULL,
PRIMARY KEY (CID));

/* Table: THISTORYCOLUMN, Owner: SYSDBA */
CREATE TABLE THISTORYCOLUMN (
    CID          INTEGER NOT NULL,
    CTABLENAME   VARCHAR(20) NOT NULL,
    CCOLUMNNAME  VARCHAR(20),
PRIMARY KEY (CID));

/* Table: TMETADATA, Owner: SYSDBA */
CREATE TABLE TMETADATA (
    CID                       INTEGER NOT NULL,
    CTECHNICALCONTACTNAME     VARCHAR(50) NOT NULL,
    CTECHNICALCONTACTEMAIL    VARCHAR(100),
    CTECHNICALCONTACTADDRESS  VARCHAR(255),
    CCONTENTCONTACTNAME       VARCHAR(50) NOT NULL,
    CCONTENTCONTACTEMAIL      VARCHAR(100),
    CCONTENTCONTACTADDRESS    VARCHAR(255),
    CDATASETTITLE             VARCHAR(50) NOT NULL,
    CDATASETDETAILS           VARCHAR(255),
    CSOURCEINSTITUTIONID      VARCHAR(50) NOT NULL,
    CSOURCEID                 VARCHAR(50) NOT NULL,
    COWNERORGANIZATIONABBREV  VARCHAR(50),
    CDATECREATE               TIMESTAMP NOT NULL,
    CDATEMODIFIED             TIMESTAMP NOT NULL,
    CRECORDBASIS              VARCHAR(15),
    CBIOTOPETEXT              VARCHAR(50),
    CVERSIONPLANTSFILE        SMALLINT,
PRIMARY KEY (CID));

/* Table: TOCCURRENCES, Owner: SYSDBA */
CREATE TABLE TOCCURRENCES (
    CID                INTEGER NOT NULL,
    CUNITIDDB          VARCHAR(30) NOT NULL,
    CUNITVALUE         VARCHAR(30) NOT NULL,
    CHABITATID         INTEGER NOT NULL,
    CPLANTID           INTEGER NOT NULL,
    CYEARCOLLECTED     SMALLINT NOT NULL,
    CMONTHCOLLECTED    SMALLINT,
    CDAYCOLLECTED      SMALLINT,
    CTIMECOLLECTED     TIME,
    CISODATETIMEBEGIN  TIMESTAMP,
    CDATESOURCE        VARCHAR(50),
    CPUBLICATIONSID    INTEGER,
    CHERBARIUM         VARCHAR(20),
    CCREATEWHEN        TIMESTAMP NOT NULL,
    CCREATEWHO         INTEGER NOT NULL,
    CUPDATEWHEN        TIMESTAMP NOT NULL,
    CUPDATEWHO         INTEGER NOT NULL,
    CNOTE              VARCHAR(4096),
    CMETADATAID        INTEGER NOT NULL,
    CDELETE            SMALLINT,
PRIMARY KEY (CID));

/* Table: TPHYTOCHORIA, Owner: SYSDBA */
CREATE TABLE TPHYTOCHORIA (
    CID    INTEGER NOT NULL,
    CCODE  VARCHAR(5) NOT NULL,
    CNAME  VARCHAR(50) NOT NULL,
PRIMARY KEY (CID));

/* Table: TPLANTS, Owner: SYSDBA */
CREATE TABLE TPLANTS (
    CID                    INTEGER NOT NULL,
    CSURVEYTAXID           VARCHAR(20) NOT NULL,
    CTAXON                 VARCHAR(255) NOT NULL,
    CGENUS                 VARCHAR(30),
    CSPECIES               VARCHAR(100),
    CSCIENTIFICNAMEAUTHOR  VARCHAR(150) NOT NULL,
    CCZECHNAME             VARCHAR(50),
    CSYNONYMS              VARCHAR(255),
    CNOTE                  VARCHAR(255),
PRIMARY KEY (CID));


/* Table: TPUBLICATIONS, Owner: SYSDBA */
CREATE TABLE TPUBLICATIONS (
    CID                         INTEGER NOT NULL,
    CCOLLECTIONNAME             VARCHAR(30),
    CCOLLECTIONYEARPUBLICATION  SMALLINT,
    CJOURNALNAME                VARCHAR(60),
    CJOURNALAUTHORNAME          VARCHAR(30),
    CREFERENCECITATION          VARCHAR(255) NOT NULL,
    CREFERENCEDETAIL            VARCHAR(20),
    CURL                        VARCHAR(100),
    CDELETE                     SMALLINT,
PRIMARY KEY (CID));

/* Table: TRIGHT, Owner: SYSDBA */
CREATE TABLE TRIGHT (
    CID             INTEGER NOT NULL,
    CADMINISTRATOR  SMALLINT,
    CEDITALL        SMALLINT,
    CEDITOWN        SMALLINT,
    CEDITGROUP      VARCHAR(4096),
    CSEECOLUMNS     VARCHAR(4096),
    CADD            SMALLINT,
PRIMARY KEY (CID));

/* Table: TTERRITORIES, Owner: SYSDBA */
CREATE TABLE TTERRITORIES (
    CID    INTEGER NOT NULL,
    CNAME  VARCHAR(100) NOT NULL,
PRIMARY KEY (CID));

/* Table: TUSER, Owner: SYSDBA */
CREATE TABLE TUSER (
    CID          INTEGER NOT NULL,
    CLOGIN       VARCHAR(20) NOT NULL,
    CFIRSTNAME   VARCHAR(20),
    CSURNAME     VARCHAR(30),
    CWHOLENAME   VARCHAR(50),
    CEMAIL       VARCHAR(50),
    CADDRESS     VARCHAR(255),
    CCREATEWHEN  TIMESTAMP NOT NULL,
    CDROPWHEN    TIMESTAMP,
    CRIGHTID     INTEGER NOT NULL,
    CNOTE        VARCHAR(4096),
PRIMARY KEY (CID));


/* Table: TVILLAGES, Owner: SYSDBA */
CREATE TABLE TVILLAGES (
    CID    INTEGER NOT NULL,
    CNAME  VARCHAR(50) NOT NULL,
PRIMARY KEY (CID));

ALTER TABLE TAUTHORSOCCURRENCES ADD FOREIGN KEY (CAUTHORID) REFERENCES TAUTHORS (CID);

ALTER TABLE TAUTHORSOCCURRENCES ADD FOREIGN KEY (COCCURRENCEID) REFERENCES TOCCURRENCES (CID);

ALTER TABLE THABITATS ADD FOREIGN KEY (CTERRITORYID) REFERENCES TTERRITORIES (CID);

ALTER TABLE THABITATS ADD FOREIGN KEY (CPHYTOCHORIAID) REFERENCES TPHYTOCHORIA (CID);

ALTER TABLE THABITATS ADD FOREIGN KEY (CNEARESTVILLAGEID) REFERENCES TVILLAGES (CID);

ALTER TABLE THISTORY ADD FOREIGN KEY (CCOLUMNID) REFERENCES THISTORYCOLUMN (CID);

ALTER TABLE THISTORY ADD FOREIGN KEY (CCHANGEID) REFERENCES THISTORYCHANGE (CID);

ALTER TABLE THISTORYCHANGE ADD FOREIGN KEY (CWHO) REFERENCES TUSER (CID);

ALTER TABLE THISTORYCHANGE ADD FOREIGN KEY (COCCURRENCEID) REFERENCES TOCCURRENCES (CID);

ALTER TABLE TOCCURRENCES ADD FOREIGN KEY (CHABITATID) REFERENCES THABITATS (CID);

ALTER TABLE TOCCURRENCES ADD FOREIGN KEY (CPLANTID) REFERENCES TPLANTS (CID);

ALTER TABLE TOCCURRENCES ADD FOREIGN KEY (CCREATEWHO) REFERENCES TUSER (CID);

ALTER TABLE TOCCURRENCES ADD FOREIGN KEY (CUPDATEWHO) REFERENCES TUSER (CID);

ALTER TABLE TOCCURRENCES ADD FOREIGN KEY (CMETADATAID) REFERENCES TMETADATA (CID);

ALTER TABLE TOCCURRENCES ADD FOREIGN KEY (CPUBLICATIONSID) REFERENCES TPUBLICATIONS (CID);

ALTER TABLE TUSER ADD FOREIGN KEY (CRIGHTID) REFERENCES TRIGHT (CID);

/* Grant role for this database */

/* Role: BOTANIK, Owner: SYSDBA */
CREATE ROLE defaultAdmin;
CREATE ROLE defaultUser;
CREATE ROLE WWW;

/* Grant permissions for this database */
GRANT DELETE, INSERT, SELECT, UPDATE, REFERENCES ON TAUTHORS TO ROLE defaultAdmin;
GRANT DELETE, INSERT, SELECT, UPDATE, REFERENCES ON TAUTHORSOCCURRENCES TO ROLE defaultAdmin;
GRANT DELETE, INSERT, SELECT, UPDATE, REFERENCES ON THABITATS TO ROLE defaultAdmin;
GRANT DELETE, INSERT, SELECT, UPDATE, REFERENCES ON THISTORY TO ROLE defaultAdmin;
GRANT DELETE, INSERT, SELECT, UPDATE, REFERENCES ON THISTORYCHANGE TO ROLE defaultAdmin;
GRANT DELETE, INSERT, SELECT, UPDATE, REFERENCES ON THISTORYCOLUMN TO ROLE defaultAdmin;
GRANT DELETE, INSERT, SELECT, UPDATE, REFERENCES ON TMETADATA TO ROLE defaultAdmin;
GRANT DELETE, INSERT, SELECT, UPDATE, REFERENCES ON TOCCURRENCES TO ROLE defaultAdmin;
GRANT DELETE, INSERT, SELECT, UPDATE, REFERENCES ON TPHYTOCHORIA TO ROLE defaultAdmin;
GRANT DELETE, INSERT, SELECT, UPDATE, REFERENCES ON TPLANTS TO ROLE defaultAdmin;
GRANT DELETE, INSERT, SELECT, UPDATE, REFERENCES ON TTERRITORIES TO ROLE defaultAdmin;
GRANT DELETE, INSERT, SELECT, UPDATE, REFERENCES ON TUSER TO ROLE defaultAdmin;
GRANT DELETE, INSERT, SELECT, UPDATE, REFERENCES ON TRIGHT TO ROLE defaultAdmin;
GRANT DELETE, INSERT, SELECT, UPDATE, REFERENCES ON TVILLAGES TO ROLE defaultAdmin;
GRANT DELETE, INSERT, SELECT, UPDATE, REFERENCES ON TPUBLICATIONS TO ROLE defaultAdmin;
GRANT defaultAdmin TO LADA;
GRANT defaultAdmin TO SYSDBA;
