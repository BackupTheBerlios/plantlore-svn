
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
CREATE GENERATOR GEN_TLASTVERSION;

/* Table: TLASTVERSION, Owner: SYSDBA */
CREATE TABLE TLASTDATAVERSION (
    CID                  INTEGER NOT NULL,
    CDATE                DATE NOT NULL,
    CPLANTSVERSION    INTEGER DEFAULT 0 NOT NULL,
    CVILLAGESVERSION     INTEGER DEFAULT 0 NOT NULL,
    CPHYTOCHORIAVERSION  INTEGER DEFAULT 0 NOT NULL,
    CTERRITORYVERSION    INTEGER DEFAULT 0 NOT NULL,
PRIMARY KEY (CID));

/* Table: TAUTHORS, Owner: SYSDBA */
CREATE TABLE TAUTHORS (
    CID               INTEGER NOT NULL,    
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

/* Table: TAUTHORSOCCURRENCES, Owner: SYSDBA */
CREATE TABLE TAUTHORSOCCURRENCES (
    CAUTHORID          INTEGER NOT NULL,
    COCCURRENCEID      INTEGER NOT NULL,
    CID                INTEGER NOT NULL,
    CROLE              VARCHAR(20),
    CNOTE              VARCHAR(4096),
    CDELETE           SMALLINT DEFAULT 0 NOT NULL,
    CVERSION          INTEGER DEFAULT 0 NOT NULL,
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
    CCREATEWHO        INTEGER NOT NULL,
    CDELETE            SMALLINT DEFAULT 0 NOT NULL,
    CVERSION          INTEGER DEFAULT 0 NOT NULL,
PRIMARY KEY (CID));

/* Table: THISTORY, Owner: SYSDBA */
CREATE TABLE THISTORY (
        CID       INTEGER NOT NULL,
        CCOLUMNID INTEGER NOT NULL,
        CCHANGEID INTEGER NOT NULL,
        COLDVALUE VARCHAR(4096),
        CNEWVALUE VARCHAR(4096),
        COLDRECORDID   INTEGER  DEFAULT 0,
        CVERSION          INTEGER DEFAULT 0 NOT NULL,
PRIMARY KEY (CID));

/* Table: THISTORYCHANGE, Owner: SYSDBA */
CREATE TABLE THISTORYCHANGE (
    CID            INTEGER NOT NULL,    
    CRECORDID      INTEGER  DEFAULT 0 NOT NULL,    
    COPERATION     SMALLINT  DEFAULT 0 NOT NULL,
    CWHEN          TIMESTAMP NOT NULL,
    CWHO           INTEGER NOT NULL,
    CVERSION          INTEGER DEFAULT 0 NOT NULL,
PRIMARY KEY (CID));

/* Table: THISTORYCOLUMN, Owner: SYSDBA */
CREATE TABLE THISTORYCOLUMN (
    CID          INTEGER NOT NULL,
    CTABLENAME   VARCHAR(20) NOT NULL,
    CCOLUMNNAME  VARCHAR(30),
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
    CDELETE                   SMALLINT DEFAULT 0 NOT NULL,
    CVERSION          INTEGER DEFAULT 0 NOT NULL,
PRIMARY KEY (CID));

/* Table: TOCCURRENCES, Owner: SYSDBA */
CREATE TABLE TOCCURRENCES (
    CID                INTEGER NOT NULL,
    CUNITIDDB          VARCHAR(30) NOT NULL,
    CUNITVALUE         VARCHAR(30) NOT NULL,
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
    CSCIENTIFICNAMEAUTHOR  VARCHAR(150),
    CCZECHNAME             VARCHAR(50),
    CSYNONYMS              VARCHAR(255),
    CNOTE                  VARCHAR(255),
PRIMARY KEY (CID));


/* Table: TPUBLICATIONS, Owner: SYSDBA */
CREATE TABLE TPUBLICATIONS (
    CID                         INTEGER NOT NULL,
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

/* Table: TRIGHT, Owner: SYSDBA */
CREATE TABLE TRIGHT (
    CID             INTEGER NOT NULL,
    CADMINISTRATOR  SMALLINT NOT NULL,
    CEDITALL        SMALLINT NOT NULL,    
    CEDITGROUP      VARCHAR(4096),
    CSEECOLUMNS     VARCHAR(4096),
    CADD            SMALLINT NOT NULL,
    CVERSION          INTEGER DEFAULT 0 NOT NULL,
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
    CWHOLENAME   VARCHAR(50) NOT NULL,
    CEMAIL       VARCHAR(50),
    CADDRESS     VARCHAR(255),
    CCREATEWHEN  TIMESTAMP NOT NULL,
    CDROPWHEN    TIMESTAMP,
    CRIGHTID     INTEGER NOT NULL,
    CNOTE        VARCHAR(4096),
    CVERSION          INTEGER DEFAULT 0 NOT NULL,
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

/* Grant role for this database */
CREATE ROLE adminPlantlore;
CREATE ROLE userPlantlore;
CREATE ROLE wwwPlantlore;

/* Grant permissions for this database - defaoutl administrator */
GRANT DELETE, INSERT, SELECT, UPDATE, REFERENCES ON TAUTHORS TO ROLE adminPlantlore;
GRANT DELETE, INSERT, SELECT, UPDATE, REFERENCES ON TAUTHORSOCCURRENCES TO ROLE adminPlantlore;
GRANT DELETE, INSERT, SELECT, UPDATE, REFERENCES ON THABITATS TO ROLE adminPlantlore;
GRANT DELETE, INSERT, SELECT, UPDATE, REFERENCES ON THISTORY TO ROLE adminPlantlore;
GRANT DELETE, INSERT, SELECT, UPDATE, REFERENCES ON THISTORYCHANGE TO ROLE adminPlantlore;
GRANT DELETE, INSERT, SELECT, UPDATE, REFERENCES ON THISTORYCOLUMN TO ROLE adminPlantlore;
GRANT DELETE, INSERT, SELECT, UPDATE, REFERENCES ON TMETADATA TO ROLE adminPlantlore;
GRANT DELETE, INSERT, SELECT, UPDATE, REFERENCES ON TOCCURRENCES TO ROLE adminPlantlore;
GRANT DELETE, INSERT, SELECT, UPDATE, REFERENCES ON TPHYTOCHORIA TO ROLE adminPlantlore;
GRANT DELETE, INSERT, SELECT, UPDATE, REFERENCES ON TPLANTS TO ROLE adminPlantlore;
GRANT DELETE, INSERT, SELECT, UPDATE, REFERENCES ON TTERRITORIES TO ROLE adminPlantlore;
GRANT DELETE, INSERT, SELECT, UPDATE, REFERENCES ON TUSER TO ROLE adminPlantlore;
GRANT DELETE, INSERT, SELECT, UPDATE, REFERENCES ON TRIGHT TO ROLE adminPlantlore;
GRANT DELETE, INSERT, SELECT, UPDATE, REFERENCES ON TVILLAGES TO ROLE adminPlantlore;
GRANT DELETE, INSERT, SELECT, UPDATE, REFERENCES ON TPUBLICATIONS TO ROLE adminPlantlore;

/* Grant permissions for this database - defaoutl user */
GRANT DELETE, INSERT, SELECT, UPDATE, REFERENCES ON TAUTHORS TO ROLE userPlantlore;
GRANT DELETE, INSERT, SELECT, UPDATE, REFERENCES ON TAUTHORSOCCURRENCES TO ROLE userPlantlore;
GRANT DELETE, INSERT, SELECT, UPDATE, REFERENCES ON THABITATS TO ROLE userPlantlore;
GRANT DELETE, INSERT, SELECT, UPDATE, REFERENCES ON THISTORY TO ROLE userPlantlore;
GRANT DELETE, INSERT, SELECT, UPDATE, REFERENCES ON THISTORYCHANGE TO ROLE userPlantlore;
GRANT DELETE, INSERT, SELECT, UPDATE, REFERENCES ON TMETADATA TO ROLE userPlantlore;
GRANT DELETE, INSERT, SELECT, UPDATE, REFERENCES ON TOCCURRENCES TO ROLE userPlantlore;
GRANT DELETE, INSERT, SELECT, UPDATE, REFERENCES ON TPHYTOCHORIA TO ROLE userPlantlore;
GRANT DELETE, INSERT, SELECT, UPDATE, REFERENCES ON TPLANTS TO ROLE userPlantlore;
GRANT DELETE, INSERT, SELECT, UPDATE, REFERENCES ON TTERRITORIES TO ROLE userPlantlore;
GRANT DELETE, INSERT, SELECT, UPDATE, REFERENCES ON TVILLAGES TO ROLE userPlantlore;
GRANT DELETE, INSERT, SELECT, UPDATE, REFERENCES ON TPUBLICATIONS TO ROLE userPlantlore;

/* Grant permissions for this database - www user */
GRANT SELECT ON vhabitats TO wwwPlantlore;
GRANT SELECT ON vmetadata TO wwwPlantlore;
GRANT SELECT ON voccurrences TO wwwPlantlore;
GRANT SELECT ON tphytochoria TO wwwPlantlore;
GRANT SELECT ON tplants TO wwwPlantlore;
GRANT SELECT ON tterritories TO wwwPlantlore;
GRANT SELECT ON tuser TO wwwPlantlore;
GRANT SELECT ON tvillages TO wwwPlantlore;
GRANT SELECT ON vpublications TO wwwPlantlore;
GRANT SELECT ON vauthorscollected TO wwwPlantlore;
GRANT SELECT ON vauthorsrevised TO wwwPlantlore;
GRANT SELECT ON vauthorsidentified TO wwwPlantlore;

/*Create default users and set grant */
CREATE USER www PASSWORD 'plantlore' ;
GRANT wwwPlantlore TO www;

/*View: VOCCURRENCES - active occurrence */
CREATE VIEW voccurrences
AS SELECT * FROM toccurrences WHERE cdelete = 0;        

/*View: VMETADATA - active metadata */
CREATE VIEW vmetadata
AS SELECT * FROM tmetadata WHERE cdelete = 0;

/*View: VHABITAT - active habitat */
CREATE VIEW vhabitats
AS SELECT * FROM thabitats WHERE cdelete = 0;

/*View: VPUBLICATIONS - active publications */
CREATE VIEW vpublications
AS SELECT * FROM tpublications WHERE cdelete = 0;

/* View: VAUTHORSCOLLECTED */
CREATE OR REPLACE VIEW vauthorscollected
AS SELECT ao.cid, ao.coccurrenceid, ao.cnote, a.cwholename, a.corganization, a.ctelephonenumber, a.crole, a.cemail, a.caddress, a.curl
FROM  tauthors a JOIN tauthorsoccurrences ao on a.cid = ao.cauthorid
WHERE ao.crole = 'collected' AND ao.cdelete = 0

/* View: VAUTHORSREVISED */
CREATE OR REPLACE VIEW vauthorsrevised
AS SELECT ao.cid, ao.coccurrenceid, ao.cnote, a.cwholename, a.corganization, a.ctelephonenumber, a.crole, a.cemail, a.caddress, a.curl
FROM  tauthors a JOIN tauthorsoccurrences ao on a.cid = ao.cauthorid
WHERE ao.crole = 'revised' AND ao.cdelete = 0

/* View: VAUTHORSIDENTIFIED */
CREATE OR REPLACE VIEW vauthorsidentified
AS SELECT ao.cid, ao.coccurrenceid, ao.cnote, a.cwholename, a.corganization, a.ctelephonenumber, a.crole, a.cemail, a.caddress, a.curl
FROM  tauthors a JOIN tauthorsoccurrences ao on a.cid = ao.cauthorid
WHERE ao.crole = 'identified' AND ao.cdelete = 0


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
