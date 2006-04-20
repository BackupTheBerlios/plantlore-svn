
CREATE TABLE TAUTHORS (
    CID               INTEGER NOT NULL auto_increment,    
    CWHOLENAME        VARCHAR(50) CHARACTER SET UTF8,
    CORGANIZATION     VARCHAR(50) CHARACTER SET UTF8,
    CTELEPHONENUMBER  VARCHAR(20) CHARACTER SET UTF8,
    CROLE             VARCHAR(30) CHARACTER SET UTF8,
    CADDRESS          VARCHAR(255) CHARACTER SET UTF8,
    CEMAIL            VARCHAR(100) CHARACTER SET UTF8,
    CURL              VARCHAR(255) CHARACTER SET UTF8,
    CNOTE             VARCHAR(4096) CHARACTER SET UTF8,
    CDELETE           SMALLINT DEFAULT 0,
PRIMARY KEY (CID));

CREATE TABLE TMETADATA (
    CID                       INTEGER NOT NULL auto_increment,
    CTECHNICALCONTACTNAME     VARCHAR(50) CHARACTER SET UTF8 NOT NULL,
    CTECHNICALCONTACTEMAIL    VARCHAR(100) CHARACTER SET UTF8,
    CTECHNICALCONTACTADDRESS  VARCHAR(255) CHARACTER SET UTF8,
    CCONTENTCONTACTNAME       VARCHAR(50) CHARACTER SET UTF8 NOT NULL,
    CCONTENTCONTACTEMAIL      VARCHAR(100) CHARACTER SET UTF8,
    CCONTENTCONTACTADDRESS    VARCHAR(255) CHARACTER SET UTF8,
    CDATASETTITLE             VARCHAR(50) CHARACTER SET UTF8 NOT NULL,
    CDATASETDETAILS           VARCHAR(255) CHARACTER SET UTF8,
    CSOURCEINSTITUTIONID      VARCHAR(50) CHARACTER SET UTF8 NOT NULL,
    CSOURCEID                 VARCHAR(50) CHARACTER SET UTF8 NOT NULL,
    COWNERORGANIZATIONABBREV  VARCHAR(50) CHARACTER SET UTF8,
    CDATECREATE               TIMESTAMP NOT NULL,
    CDATEMODIFIED             TIMESTAMP NOT NULL,
    CRECORDBASIS              VARCHAR(15) CHARACTER SET UTF8,
    CBIOTOPETEXT              VARCHAR(50) CHARACTER SET UTF8,
    CVERSIONPLANTSFILE        SMALLINT DEFAULT 0,
PRIMARY KEY (CID));

CREATE TABLE TPLANTS (
    CID                    INTEGER NOT NULL auto_increment,
    CSURVEYTAXID           VARCHAR(20) CHARACTER SET UTF8 NOT NULL,
    CTAXON                 VARCHAR(255) CHARACTER SET UTF8 NOT NULL,
    CGENUS                 VARCHAR(30) CHARACTER SET UTF8,
    CSPECIES               VARCHAR(100) CHARACTER SET UTF8,
    CSCIENTIFICNAMEAUTHOR  VARCHAR(150) CHARACTER SET UTF8 NOT NULL,
    CCZECHNAME             VARCHAR(50) CHARACTER SET UTF8,
    CSYNONYMS              VARCHAR(255) CHARACTER SET UTF8,
    CNOTE                  VARCHAR(255) CHARACTER SET UTF8,
PRIMARY KEY (CID));

CREATE TABLE TPUBLICATIONS (
    CID                         INTEGER NOT NULL auto_increment,
    CCOLLECTIONNAME             VARCHAR(30) CHARACTER SET UTF8,
    CCOLLECTIONYEARPUBLICATION  SMALLINT,
    CJOURNALNAME                VARCHAR(60) CHARACTER SET UTF8,
    CJOURNALAUTHORNAME          VARCHAR(30) CHARACTER SET UTF8,
    CREFERENCECITATION          VARCHAR(255) NOT NULL,
    CREFERENCEDETAIL            VARCHAR(20) CHARACTER SET UTF8,
    CURL                        VARCHAR(100) CHARACTER SET UTF8,
    CNOTE                       VARCHAR(4096) CHARACTER SET UTF8,
    CDELETE                     SMALLINT DEFAULT 0,
PRIMARY KEY (CID));

CREATE TABLE TRIGHT (
    CID             INTEGER NOT NULL auto_increment,
    CADMINISTRATOR  SMALLINT,
    CEDITALL        SMALLINT,
    CEDITOWN        SMALLINT,
    CEDITGROUP      VARCHAR(4096) CHARACTER SET UTF8,
    CSEECOLUMNS     VARCHAR(4096) CHARACTER SET UTF8,
    CADD            SMALLINT,
PRIMARY KEY (CID));

CREATE TABLE TTERRITORIES (
    CID    INTEGER NOT NULL auto_increment,
    CNAME  VARCHAR(100) CHARACTER SET UTF8 NOT NULL,
PRIMARY KEY (CID));

CREATE TABLE TVILLAGES (
    CID    INTEGER NOT NULL auto_increment,
    CNAME  VARCHAR(50) CHARACTER SET UTF8 NOT NULL,
PRIMARY KEY (CID)); 

CREATE TABLE TPHYTOCHORIA (
    CID    INTEGER NOT NULL auto_increment,
    CCODE  VARCHAR(5) CHARACTER SET UTF8 NOT NULL,
    CNAME  VARCHAR(50) CHARACTER SET UTF8 NOT NULL,
PRIMARY KEY (CID));

CREATE TABLE TLASTUPDATE (
    CID          INTEGER NOT NULL auto_increment,
    CTABLENAME   VARCHAR(20) CHARACTER SET UTF8 NOT NULL,
    CLASTUPDATE  TIMESTAMP NOT NULL,
PRIMARY KEY (CID));

CREATE TABLE TUSER (
    CID          INTEGER NOT NULL auto_increment,
    CLOGIN       VARCHAR(20) NOT NULL,
    CPASSWORD    SMALLINT,
    CFIRSTNAME   VARCHAR(20) CHARACTER SET UTF8,
    CSURNAME     VARCHAR(30) CHARACTER SET UTF8,
    CWHOLENAME   VARCHAR(50) CHARACTER SET UTF8,
    CEMAIL       VARCHAR(50) CHARACTER SET UTF8,
    CADDRESS     VARCHAR(255) CHARACTER SET UTF8,
    CCREATEWHEN  TIMESTAMP NOT NULL,
    CDROPWHEN    TIMESTAMP,
    CRIGHTID     INTEGER NOT NULL ,
    CNOTE        VARCHAR(4096) CHARACTER SET UTF8,
PRIMARY KEY (CID),
FOREIGN KEY (CRIGHTID) REFERENCES tright(CID));

CREATE TABLE THABITATS (
    CID                INTEGER NOT NULL auto_increment,
    CTERRITORYID       INTEGER NOT NULL,
    CPHYTOCHORIAID     INTEGER NOT NULL,
    CQUADRANT          VARCHAR(10) CHARACTER SET UTF8,
    CDESCRIPTION       VARCHAR(255) CHARACTER SET UTF8,
    CNEARESTVILLAGEID  INTEGER NOT NULL,
    CCOUNTRY           VARCHAR(30) CHARACTER SET UTF8,
    CALTITUDE          DECIMAL(6,2),
    CLATITUDE          DOUBLE PRECISION,
    CLONGITUDE         DOUBLE PRECISION,
    CNOTE              VARCHAR(4096) CHARACTER SET UTF8,
    CDELETE            SMALLINT DEFAULT 0 ,
PRIMARY KEY (CID),
FOREIGN KEY (CTERRITORYID) REFERENCES TTERRITORIES(CID),
FOREIGN KEY (CPHYTOCHORIAID) REFERENCES TPHYTOCHORIA(CID),
FOREIGN KEY (CNEARESTVILLAGEID) REFERENCES TVILLAGES(CID));


CREATE TABLE TOCCURRENCES (
    CID                INTEGER NOT NULL auto_increment,
    CUNITIDDB          VARCHAR(30) CHARACTER SET UTF8 NOT NULL,
    CUNITVALUE         VARCHAR(30) CHARACTER SET UTF8 NOT NULL,
    CHABITATID         INTEGER NOT NULL,
    CPLANTID           INTEGER NOT NULL,
    CYEARCOLLECTED     SMALLINT DEFAULT 0 NOT NULL,
    CMONTHCOLLECTED    SMALLINT DEFAULT 0,
    CDAYCOLLECTED      SMALLINT DEFAULT 0,
    CTIMECOLLECTED     TIME,
    CISODATETIMEBEGIN  TIMESTAMP,
    CDATASOURCE        VARCHAR(50) CHARACTER SET UTF8,
    CPUBLICATIONSID    INTEGER,
    CHERBARIUM         VARCHAR(20) CHARACTER SET UTF8,
    CCREATEWHEN        TIMESTAMP NOT NULL,
    CCREATEWHO         INTEGER NOT NULL,
    CUPDATEWHEN        TIMESTAMP NOT NULL,
    CUPDATEWHO         INTEGER NOT NULL,
    CNOTE              VARCHAR(4096) CHARACTER SET UTF8,
    CMETADATAID        INTEGER NOT NULL,
    CDELETE            SMALLINT DEFAULT 0,
PRIMARY KEY (CID),
FOREIGN KEY (CHABITATID) REFERENCES THABITATS(CID),
FOREIGN KEY (CPLANTID) REFERENCES TPLANTS(CID),
FOREIGN KEY (CPUBLICATIONSID) REFERENCES TPUBLICATIONS(CID),
FOREIGN KEY (CCREATEWHO) REFERENCES TUSER(CID),
FOREIGN KEY (CUPDATEWHO) REFERENCES TUSER(CID),
FOREIGN KEY (CMETADATAID) REFERENCES TMETADATA(CID));

CREATE TABLE TAUTHORSOCCURRENCES (
    CAUTHORID          INTEGER NOT NULL auto_increment,
    COCCURRENCEID      INTEGER NOT NULL,
    CID                INTEGER NOT NULL,
    CROLE              VARCHAR(20) CHARACTER SET UTF8,
    CRESULTREVISION  VARCHAR(30) CHARACTER SET UTF8,
PRIMARY KEY (CID),
FOREIGN KEY (CAUTHORID) REFERENCES TAUTHORS(CID),
FOREIGN KEY (COCCURRENCEID) REFERENCES TOCCURRENCES(CID));

CREATE TABLE THISTORYCOLUMN (
    CID          INTEGER NOT NULL auto_increment,
    CTABLENAME   VARCHAR(20) CHARACTER SET UTF8 NOT NULL,
    CCOLUMNNAME  VARCHAR(20) CHARACTER SET UTF8,
PRIMARY KEY (CID));

CREATE TABLE THISTORYCHANGE (
    CID            INTEGER NOT NULL auto_increment,
    COCCURRENCEID  INTEGER  DEFAULT 0 NOT NULL,
    CRECORDID      INTEGER  DEFAULT 0 NOT NULL,
    COLDRECORDID   INTEGER,
    COPERATION     SMALLINT  DEFAULT 0 NOT NULL,
    CWHEN          TIMESTAMP NOT NULL,
    CWHO           INTEGER NOT NULL,
PRIMARY KEY (CID),
FOREIGN KEY (COCCURRENCEID) REFERENCES TOCCURRENCES(CID),
FOREIGN KEY (CWHO) REFERENCES TUSER(CID));

CREATE TABLE THISTORY (
        CID       INTEGER NOT NULL auto_increment,
        CCOLUMNID INTEGER NOT NULL,
        CCHANGEID INTEGER NOT NULL,
        COLDVALUE VARCHAR(4096) CHARACTER SET UTF8,
        CNEWVALUE VARCHAR(4096) CHARACTER SET UTF8,
PRIMARY KEY (CID),
FOREIGN KEY (CCOLUMNID) REFERENCES THISTORYCOLUMN(CID),
FOREIGN KEY (CCHANGEID) REFERENCES THISTORYCHANGE(CID));



/**********************************ZATIM NEUPRAVENO PRO MYSQL*******************/
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

/* View: TAUTHORREVISION */
CREATE VIEW TAUTHORREVISION(
    CID,
    COCCURRENCEID,
    CWHOLENAME,
    CEMAIL,
    CADDRESS,
    CRESULTREVISION,
    CDAY,
    CMONTH,
    CYEAR)
AS
select AO.CID, AO.coccurrenceid, A.cwholename, A.CEMAIL, A.CADDRESS, AO.cresultrevisition, O.cdaycollected, O.cmonthcollected, O.cyearcollected
from TAUTHORS A JOIN TAUTHORSOCCURRENCES AO JOIN toccurrences O  ON (A.CID = AO.cauthorid) ON (AO.coccurrenceid = O.cid)
WHERE AO.crole = 'revision'
;

/* View: TAUTHORCOLLECT */
CREATE VIEW TAUTHORCOLLECT(
    CID,    
    COCCURRENCEID,
    CWHOLENAME,
    CORGANIZATION,
    CEMAIL,
    CADDRESS)
AS
select AO.CID, AO.coccurrenceid, A.CWHOLENAME, A.CORGANIZATION, A.CEMAIL, A.CADDRESS
from TAUTHORS A JOIN TAUTHORSOCCURRENCES AO ON (A.CID = AO.cauthorid)
WHERE AO.crole = 'collect'
;

/* View: TAUTHORIDENTIFY */
CREATE VIEW TAUTHORIDENTIFY(
    CID,
    COCCURRENCEID,
    CWHOLENAME,
    CEMAIL,
    CADDRESS)
AS
select AO.CID, AO.coccurrenceid, A.CWHOLENAME, A.CEMAIL, A.CADDRESS
from TAUTHORS A JOIN TAUTHORSOCCURRENCES AO ON (A.CID = AO.cauthorid)
WHERE AO.crole = 'identify'
;