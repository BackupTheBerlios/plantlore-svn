SET client_encoding = 'UNICODE';

SET search_path = public, pg_catalog;

SET default_tablespace = '';

SET default_with_oids = true;

CREATE TABLE TUNITIDDATABASE (
    CID                  INTEGER NOT NULL,
    CUNITIDDB            VARCHAR(30) NOT NULL,    
PRIMARY KEY (CID));

CREATE TABLE TAUTHORS (
    CID               SERIAL NOT NULL,    
    CWHOLENAME        VARCHAR(50) NOT NULL,
    CORGANIZATION     VARCHAR(50),
    CTELEPHONENUMBER  VARCHAR(20),
    CROLE             VARCHAR(30),
    CADDRESS          VARCHAR(255),
    CEMAIL            VARCHAR(100),
    CURL              VARCHAR(255),
    CNOTE             VARCHAR(4096),
    CCREATEWHO        INTEGER NOT NULL, 
    CDELETE           SMALLINT DEFAULT 0 NOT NULL,
    CVERSION          INTEGER DEFAULT 0 NOT NULL,
PRIMARY KEY (CID));

CREATE TABLE TAUTHORSOCCURRENCES (
    CAUTHORID          SERIAL NOT NULL,
    COCCURRENCEID      INTEGER NOT NULL,
    CID                INTEGER NOT NULL,
    CROLE              VARCHAR(20),
    CNOTE              VARCHAR(4096),
    CDELETE           SMALLINT DEFAULT 0 NOT NULL,
    CVERSION          INTEGER DEFAULT 0 NOT NULL,
PRIMARY KEY (CID));

CREATE TABLE THABITATS (
    CID                SERIAL NOT NULL,
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
    CCREATEWHO        INTEGER NOT NULL,
    CDELETE            SMALLINT DEFAULT 0 NOT NULL,
    CVERSION          INTEGER DEFAULT 0 NOT NULL,
PRIMARY KEY (CID));

CREATE TABLE THISTORY (
        CID       SERIAL NOT NULL,
        CCOLUMNID INTEGER NOT NULL,
        CCHANGEID INTEGER NOT NULL,
        COLDVALUE VARCHAR(4096),
        CNEWVALUE VARCHAR(4096),
        COLDRECORDID   INTEGER,
        CVERSION          INTEGER DEFAULT 0 NOT NULL,
PRIMARY KEY (CID));

CREATE TABLE THISTORYCHANGE (
    CID            SERIAL NOT NULL,    
    CRECORDID      INTEGER  DEFAULT 0 NOT NULL,    
    COPERATION     SMALLINT  DEFAULT 0 NOT NULL,
    CWHEN          TIMESTAMP NOT NULL,
    CWHO           INTEGER NOT NULL,
    CVERSION          INTEGER DEFAULT 0 NOT NULL,
PRIMARY KEY (CID));

CREATE TABLE THISTORYCOLUMN (
    CID          SERIAL NOT NULL,
    CTABLENAME   VARCHAR(20) NOT NULL,
    CCOLUMNNAME  VARCHAR(30),
PRIMARY KEY (CID));

CREATE TABLE TMETADATA (
    CID                       SERIAL NOT NULL,
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
    CDELETE                   SMALLINT DEFAULT 0 NOT NULL,
    CVERSION          INTEGER DEFAULT 0 NOT NULL,
PRIMARY KEY (CID));

CREATE TABLE TOCCURRENCES (
    CID                SERIAL NOT NULL,
    CUNITIDDB          VARCHAR(40) NOT NULL,
    CUNITVALUE         INTEGER NOT NULL,
    CHABITATID         INTEGER NOT NULL,
    CPLANTID           INTEGER NOT NULL,
    CYEARCOLLECTED     SMALLINT DEFAULT 0 NOT NULL,
    CMONTHCOLLECTED    SMALLINT DEFAULT 0,
    CDAYCOLLECTED      SMALLINT DEFAULT 0,
    CTIMECOLLECTED     TIME,
    CISODATETIMEBEGIN  TIMESTAMP,
    CDATASOURCE        VARCHAR(50),
    CPUBLICATIONSID    INTEGER,
    CHERBARIUM         VARCHAR(20),
    CCREATEWHEN        TIMESTAMP NOT NULL,
    CCREATEWHO         INTEGER NOT NULL,
    CUPDATEWHEN        TIMESTAMP NOT NULL,
    CUPDATEWHO         INTEGER NOT NULL,
    CNOTE              VARCHAR(4096),
    CMETADATAID        INTEGER NOT NULL,
    CDELETE            SMALLINT DEFAULT 0 NOT NULL,
    CVERSION          INTEGER DEFAULT 0 NOT NULL,
PRIMARY KEY (CID));

CREATE TABLE TPHYTOCHORIA (
    CID    SERIAL NOT NULL,
    CCODE  VARCHAR(5) NOT NULL,
    CNAME  VARCHAR(50) NOT NULL,
    CVERSION          INTEGER DEFAULT 0 NOT NULL,
PRIMARY KEY (CID));

CREATE TABLE TPLANTS (
    CID                    SERIAL NOT NULL,
    CSURVEYTAXID           VARCHAR(20) NOT NULL,
    CTAXON                 VARCHAR(255) NOT NULL,
    CGENUS                 VARCHAR(30),
    CSPECIES               VARCHAR(100),
    CSCIENTIFICNAMEAUTHOR  VARCHAR(150),
    CCZECHNAME             VARCHAR(50),
    CSYNONYMS              VARCHAR(255),
    CNOTE                  VARCHAR(255),
PRIMARY KEY (CID));

CREATE TABLE TPUBLICATIONS (
    CID                         SERIAL NOT NULL,
    CCOLLECTIONNAME             VARCHAR(255),
    CCOLLECTIONYEARPUBLICATION  SMALLINT,
    CJOURNALNAME                VARCHAR(255),
    CJOURNALAUTHORNAME          VARCHAR(255),
    CREFERENCECITATION          VARCHAR(4096) NOT NULL,
    CREFERENCEDETAIL            VARCHAR(100),
    CURL                        VARCHAR(100),
    CNOTE                       VARCHAR(4096),
    CCREATEWHO                  INTEGER NOT NULL,
    CDELETE                     SMALLINT DEFAULT 0 NOT NULL,
    CVERSION          INTEGER DEFAULT 0 NOT NULL,
PRIMARY KEY (CID));

CREATE TABLE TRIGHT (
    CID             SERIAL NOT NULL,
    CADMINISTRATOR  SMALLINT DEFAULT 0 NOT NULL,
    CEDITALL        SMALLINT DEFAULT 0 NOT NULL,
    CEDITGROUP      VARCHAR(4096),
    CADD            SMALLINT DEFAULT 0 NOT NULL,
    CVERSION          INTEGER DEFAULT 0 NOT NULL,
PRIMARY KEY (CID));

CREATE TABLE TTERRITORIES (
    CID    SERIAL NOT NULL,
    CNAME  VARCHAR(100) NOT NULL,
PRIMARY KEY (CID));

CREATE TABLE TUSER (
    CID          SERIAL NOT NULL,
    CLOGIN       VARCHAR(20) NOT NULL,   
    CFIRSTNAME   VARCHAR(20),
    CSURNAME     VARCHAR(30),
    CWHOLENAME   VARCHAR(50) NOT NULL,
    CEMAIL       VARCHAR(50),
    CADDRESS     VARCHAR(255),
    CCREATEWHEN  TIMESTAMP NOT NULL,
    CDROPWHEN    TIMESTAMP,
    CRIGHTID     INTEGER NOT NULL,
    CNOTE        VARCHAR(4096),
    CVERSION          INTEGER DEFAULT 0 NOT NULL,
PRIMARY KEY (CID));

CREATE TABLE TVILLAGES (
    CID    SERIAL NOT NULL,
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

ALTER TABLE TOCCURRENCES ADD FOREIGN KEY (CHABITATID) REFERENCES THABITATS (CID);

ALTER TABLE TOCCURRENCES ADD FOREIGN KEY (CPLANTID) REFERENCES TPLANTS (CID);

ALTER TABLE TOCCURRENCES ADD FOREIGN KEY (CCREATEWHO) REFERENCES TUSER (CID);

ALTER TABLE TOCCURRENCES ADD FOREIGN KEY (CUPDATEWHO) REFERENCES TUSER (CID);

ALTER TABLE TOCCURRENCES ADD FOREIGN KEY (CMETADATAID) REFERENCES TMETADATA (CID);

ALTER TABLE TOCCURRENCES ADD FOREIGN KEY (CPUBLICATIONSID) REFERENCES TPUBLICATIONS (CID);

ALTER TABLE TUSER ADD FOREIGN KEY (CRIGHTID) REFERENCES TRIGHT (CID);

ALTER TABLE TAUTHORS ADD FOREIGN KEY (CCREATEWHO) REFERENCES TUSER (CID);

ALTER TABLE TPUBLICATIONS ADD FOREIGN KEY (CCREATEWHO) REFERENCES TUSER (CID);

ALTER TABLE THABITATS ADD FOREIGN KEY (CCREATEWHO) REFERENCES TUSER (CID);

CREATE VIEW voccurrences AS SELECT * FROM toccurrences WHERE cdelete = 0;        

CREATE VIEW vmetadata AS SELECT * FROM tmetadata WHERE cdelete = 0;

CREATE VIEW vhabitats AS SELECT * FROM thabitats WHERE cdelete = 0;

CREATE VIEW vpublications AS SELECT * FROM tpublications WHERE cdelete = 0;

CREATE OR REPLACE VIEW vauthorscollected AS SELECT ao.cid, ao.coccurrenceid, ao.cnote, a.cwholename, a.corganization, a.ctelephonenumber, a.crole, a.cemail, a.caddress, a.curl FROM  tauthors a JOIN tauthorsoccurrences ao on a.cid = ao.cauthorid WHERE ao.crole = 'collected' AND ao.cdelete = 0;

CREATE OR REPLACE VIEW vauthorsrevised AS SELECT ao.cid, ao.coccurrenceid, ao.cnote, a.cwholename, a.corganization, a.ctelephonenumber, a.crole, a.cemail, a.caddress, a.curl FROM  tauthors a JOIN tauthorsoccurrences ao on a.cid = ao.cauthorid WHERE ao.crole = 'revised' AND ao.cdelete = 0;

CREATE OR REPLACE VIEW vauthorsidentified AS SELECT ao.cid, ao.coccurrenceid, ao.cnote, a.cwholename, a.corganization, a.ctelephonenumber, a.crole, a.cemail, a.caddress, a.curl FROM  tauthors a JOIN tauthorsoccurrences ao on a.cid = ao.cauthorid WHERE ao.crole = 'identified' AND ao.cdelete = 0;

GRANT DELETE, INSERT, SELECT, UPDATE, REFERENCES ON tauthors TO Plantlore_Role_Admin;
GRANT DELETE, INSERT, SELECT, UPDATE, REFERENCES ON tauthorsoccurrences TO Plantlore_Role_Admin;
GRANT DELETE, INSERT, SELECT, UPDATE, REFERENCES ON THABITATS TO Plantlore_Role_Admin;
GRANT DELETE, INSERT, SELECT, UPDATE, REFERENCES ON THISTORY TO Plantlore_Role_Admin;
GRANT DELETE, INSERT, SELECT, UPDATE, REFERENCES ON THISTORYCHANGE TO Plantlore_Role_Admin;
GRANT DELETE, INSERT, SELECT, UPDATE, REFERENCES ON THISTORYCOLUMN TO Plantlore_Role_Admin;
GRANT DELETE, INSERT, SELECT, UPDATE, REFERENCES ON TMETADATA TO Plantlore_Role_Admin;
GRANT DELETE, INSERT, SELECT, UPDATE, REFERENCES ON TOCCURRENCES TO Plantlore_Role_Admin;
GRANT DELETE, INSERT, SELECT, UPDATE, REFERENCES ON TPHYTOCHORIA TO Plantlore_Role_Admin;
GRANT DELETE, INSERT, SELECT, UPDATE, REFERENCES ON TPLANTS TO Plantlore_Role_Admin;
GRANT DELETE, INSERT, SELECT, UPDATE, REFERENCES ON TTERRITORIES TO Plantlore_Role_Admin;
GRANT DELETE, INSERT, SELECT, UPDATE, REFERENCES ON TUSER TO Plantlore_Role_Admin;
GRANT DELETE, INSERT, SELECT, UPDATE, REFERENCES ON TRIGHT TO Plantlore_Role_Admin;
GRANT DELETE, INSERT, SELECT, UPDATE, REFERENCES ON TVILLAGES TO Plantlore_Role_Admin;
GRANT DELETE, INSERT, SELECT, UPDATE, REFERENCES ON TPUBLICATIONS TO Plantlore_Role_Admin;
GRANT DELETE, INSERT, SELECT, UPDATE, REFERENCES ON TUNITIDDATABASE TO Plantlore_Role_Admin;

GRANT DELETE, INSERT, SELECT, UPDATE, REFERENCES ON TAUTHORS TO Plantlore_Role_User;
GRANT DELETE, INSERT, SELECT, UPDATE, REFERENCES ON TAUTHORSOCCURRENCES TO Plantlore_Role_User;
GRANT DELETE, INSERT, SELECT, UPDATE, REFERENCES ON THABITATS TO Plantlore_Role_User;
GRANT DELETE, INSERT, SELECT, UPDATE, REFERENCES ON THISTORY TO Plantlore_Role_User;
GRANT DELETE, INSERT, SELECT, UPDATE, REFERENCES ON THISTORYCHANGE TO Plantlore_Role_User;
GRANT DELETE, INSERT, SELECT, UPDATE, REFERENCES ON TMETADATA TO Plantlore_Role_User;
GRANT DELETE, INSERT, SELECT, UPDATE, REFERENCES ON TOCCURRENCES TO Plantlore_Role_User;
GRANT DELETE, INSERT, SELECT, UPDATE, REFERENCES ON TPHYTOCHORIA TO Plantlore_Role_User;
GRANT DELETE, INSERT, SELECT, UPDATE, REFERENCES ON TPLANTS TO Plantlore_Role_User;
GRANT DELETE, INSERT, SELECT, UPDATE, REFERENCES ON TTERRITORIES TO Plantlore_Role_User;
GRANT DELETE, INSERT, SELECT, UPDATE, REFERENCES ON TVILLAGES TO Plantlore_Role_User;
GRANT DELETE, INSERT, SELECT, UPDATE, REFERENCES ON TPUBLICATIONS TO Plantlore_Role_User;
GRANT DELETE, INSERT, SELECT, UPDATE, REFERENCES ON TUNITIDDATABASE TO Plantlore_Role_User;

GRANT SELECT, REFERENCES ON THISTORYCOLUMN TO Plantlore_Role_User;
GRANT SELECT, REFERENCES ON TUSER TO Plantlore_Role_User;
GRANT SELECT, REFERENCES ON TRIGHT TO Plantlore_Role_User;

GRANT SELECT ON vhabitats TO Plantlore_Role_www;
GRANT SELECT ON vmetadata TO Plantlore_Role_www;
GRANT SELECT ON voccurrences TO Plantlore_Role_www;
GRANT SELECT ON tphytochoria TO Plantlore_Role_www;
GRANT SELECT ON tplants TO Plantlore_Role_www;
GRANT SELECT ON tterritories TO Plantlore_Role_www;
GRANT SELECT ON tuser TO Plantlore_Role_www;
GRANT SELECT ON tvillages TO Plantlore_Role_www;
GRANT SELECT ON vpublications TO Plantlore_Role_www;
GRANT SELECT ON vauthorscollected TO Plantlore_Role_www;
GRANT SELECT ON vauthorsrevised TO Plantlore_Role_www;
GRANT SELECT ON vauthorsidentified TO Plantlore_Role_www;

INSERT INTO thistorycolumn (cid, ctablename, ccolumnname) VALUES (1, 'AUTHOROCCURRENCE', NULL);
INSERT INTO thistorycolumn (cid, ctablename, ccolumnname) VALUES (2, 'OCCURRENCE', NULL);
INSERT INTO thistorycolumn (cid, ctablename, ccolumnname) VALUES (3, 'HABITAT', NULL);
INSERT INTO thistorycolumn (cid, ctablename, ccolumnname) VALUES (4, 'AUTHOR', NULL);
INSERT INTO thistorycolumn (cid, ctablename, ccolumnname) VALUES (5, 'METADATA', NULL);
INSERT INTO thistorycolumn (cid, ctablename, ccolumnname) VALUES (6, 'PUBLICATION', NULL);
INSERT INTO thistorycolumn (cid, ctablename, ccolumnname) VALUES (7, 'TERRITORY', NULL);
INSERT INTO thistorycolumn (cid, ctablename, ccolumnname) VALUES (8, 'VILLAGE', NULL);
INSERT INTO thistorycolumn (cid, ctablename, ccolumnname) VALUES (9, 'PHYTOCHORION', NULL);
INSERT INTO thistorycolumn (cid, ctablename, ccolumnname) VALUES (10, 'AUTHOROCCURRENCE', 'role');
INSERT INTO thistorycolumn (cid, ctablename, ccolumnname) VALUES (11, 'AUTHOROCCURRENCE', 'note');
INSERT INTO thistorycolumn (cid, ctablename, ccolumnname) VALUES (12, 'OCCURRENCE', 'plant');
INSERT INTO thistorycolumn (cid, ctablename, ccolumnname) VALUES (13, 'OCCURRENCE', 'habitat');
INSERT INTO thistorycolumn (cid, ctablename, ccolumnname) VALUES (14, 'OCCURRENCE', 'yearCollected');
INSERT INTO thistorycolumn (cid, ctablename, ccolumnname) VALUES (15, 'OCCURRENCE', 'monthCollected');
INSERT INTO thistorycolumn (cid, ctablename, ccolumnname) VALUES (16, 'OCCURRENCE', 'dayCollected');
INSERT INTO thistorycolumn (cid, ctablename, ccolumnname) VALUES (17, 'OCCURRENCE', 'timeCollected');
INSERT INTO thistorycolumn (cid, ctablename, ccolumnname) VALUES (18, 'OCCURRENCE', 'dataSource');
INSERT INTO thistorycolumn (cid, ctablename, ccolumnname) VALUES (19, 'OCCURRENCE', 'publication');
INSERT INTO thistorycolumn (cid, ctablename, ccolumnname) VALUES (20, 'OCCURRENCE', 'herbarium');
INSERT INTO thistorycolumn (cid, ctablename, ccolumnname) VALUES (21, 'OCCURRENCE', 'metadata');
INSERT INTO thistorycolumn (cid, ctablename, ccolumnname) VALUES (22, 'OCCURRENCE', 'note');
INSERT INTO thistorycolumn (cid, ctablename, ccolumnname) VALUES (23, 'HABITAT', 'territory');
INSERT INTO thistorycolumn (cid, ctablename, ccolumnname) VALUES (24, 'HABITAT', 'phytochorion');
INSERT INTO thistorycolumn (cid, ctablename, ccolumnname) VALUES (25, 'HABITAT', 'village');
INSERT INTO thistorycolumn (cid, ctablename, ccolumnname) VALUES (26, 'HABITAT', 'quadrant');
INSERT INTO thistorycolumn (cid, ctablename, ccolumnname) VALUES (27, 'HABITAT', 'description');
INSERT INTO thistorycolumn (cid, ctablename, ccolumnname) VALUES (28, 'HABITAT', 'country');
INSERT INTO thistorycolumn (cid, ctablename, ccolumnname) VALUES (29, 'HABITAT', 'altitude');
INSERT INTO thistorycolumn (cid, ctablename, ccolumnname) VALUES (30, 'HABITAT', 'latitude');
INSERT INTO thistorycolumn (cid, ctablename, ccolumnname) VALUES (31, 'HABITAT', 'longitude');
INSERT INTO thistorycolumn (cid, ctablename, ccolumnname) VALUES (32, 'HABITAT', 'note');
INSERT INTO thistorycolumn (cid, ctablename, ccolumnname) VALUES (33, 'AUTHOR', 'wholeName');
INSERT INTO thistorycolumn (cid, ctablename, ccolumnname) VALUES (34, 'AUTHOR', 'organization');
INSERT INTO thistorycolumn (cid, ctablename, ccolumnname) VALUES (35, 'AUTHOR', 'role');
INSERT INTO thistorycolumn (cid, ctablename, ccolumnname) VALUES (36, 'AUTHOR', 'address');
INSERT INTO thistorycolumn (cid, ctablename, ccolumnname) VALUES (37, 'AUTHOR', 'phoneNumber');
INSERT INTO thistorycolumn (cid, ctablename, ccolumnname) VALUES (38, 'AUTHOR', 'email');
INSERT INTO thistorycolumn (cid, ctablename, ccolumnname) VALUES (39, 'AUTHOR', 'url');
INSERT INTO thistorycolumn (cid, ctablename, ccolumnname) VALUES (40, 'AUTHOR', 'note');
INSERT INTO thistorycolumn (cid, ctablename, ccolumnname) VALUES (41, 'METADATA', 'technicalContactName');
INSERT INTO thistorycolumn (cid, ctablename, ccolumnname) VALUES (42, 'METADATA', 'technicalContactAddress');
INSERT INTO thistorycolumn (cid, ctablename, ccolumnname) VALUES (43, 'METADATA', 'technicalContactEmail');
INSERT INTO thistorycolumn (cid, ctablename, ccolumnname) VALUES (44, 'METADATA', 'contentContactName');
INSERT INTO thistorycolumn (cid, ctablename, ccolumnname) VALUES (45, 'METADATA', 'contentContactAddress');
INSERT INTO thistorycolumn (cid, ctablename, ccolumnname) VALUES (46, 'METADATA', 'contentContactEmail');
INSERT INTO thistorycolumn (cid, ctablename, ccolumnname) VALUES (47, 'METADATA', 'dataSetTitle');
INSERT INTO thistorycolumn (cid, ctablename, ccolumnname) VALUES (48, 'METADATA', 'dataSetDetails');
INSERT INTO thistorycolumn (cid, ctablename, ccolumnname) VALUES (49, 'METADATA', 'sourceInstitutionId');
INSERT INTO thistorycolumn (cid, ctablename, ccolumnname) VALUES (50, 'METADATA', 'sourceId');
INSERT INTO thistorycolumn (cid, ctablename, ccolumnname) VALUES (51, 'METADATA', 'ownerOrganizationAbbrev');
INSERT INTO thistorycolumn (cid, ctablename, ccolumnname) VALUES (52, 'METADATA', 'dateCreate');
INSERT INTO thistorycolumn (cid, ctablename, ccolumnname) VALUES (53, 'METADATA', 'dateModified');
INSERT INTO thistorycolumn (cid, ctablename, ccolumnname) VALUES (54, 'METADATA', 'recordBasis');
INSERT INTO thistorycolumn (cid, ctablename, ccolumnname) VALUES (55, 'METADATA', 'biotopeText');
INSERT INTO thistorycolumn (cid, ctablename, ccolumnname) VALUES (56, 'METADATA', 'versionPlantsFile');
INSERT INTO thistorycolumn (cid, ctablename, ccolumnname) VALUES (57, 'PUBLICATION', 'collectionName');
INSERT INTO thistorycolumn (cid, ctablename, ccolumnname) VALUES (58, 'PUBLICATION', 'collectionYearPUBLICATION');
INSERT INTO thistorycolumn (cid, ctablename, ccolumnname) VALUES (59, 'PUBLICATION', 'journalName');
INSERT INTO thistorycolumn (cid, ctablename, ccolumnname) VALUES (60, 'PUBLICATION', 'journalAuthorName');
INSERT INTO thistorycolumn (cid, ctablename, ccolumnname) VALUES (61, 'PUBLICATION', 'referenceCitation');
INSERT INTO thistorycolumn (cid, ctablename, ccolumnname) VALUES (62, 'PUBLICATION', 'referenceDetail');
INSERT INTO thistorycolumn (cid, ctablename, ccolumnname) VALUES (63, 'PUBLICATION', 'url');
INSERT INTO thistorycolumn (cid, ctablename, ccolumnname) VALUES (64, 'PUBLICATION', 'note');
INSERT INTO thistorycolumn (cid, ctablename, ccolumnname) VALUES (65, 'VILLAGE', 'name');
INSERT INTO thistorycolumn (cid, ctablename, ccolumnname) VALUES (66, 'TERRITORY', 'name');
INSERT INTO thistorycolumn (cid, ctablename, ccolumnname) VALUES (67, 'PHYTOCHORIA', 'name');
INSERT INTO thistorycolumn (cid, ctablename, ccolumnname) VALUES (68, 'PHYTOCHORIA', 'code');
INSERT INTO thistorycolumn (cid, ctablename, ccolumnname) VALUES (69, 'HABITAT', 'nearestVillage');
