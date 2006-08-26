/********************************************************/
/*							*/
/* SQL script for creating Plantlore database for MySQL */
/* 		Version: 02.6. 2006			*/
/*	     Tested with MySQL 4.1.14			*/
/*							*/
/********************************************************/

CREATE TABLE TLASTDATAVERSION (
    CID                  INTEGER NOT NULL,
    CDATE                DATE NOT NULL,
    CPLANTSVERSION    INTEGER DEFAULT 0 NOT NULL,
    CVILLAGESVERSION     INTEGER DEFAULT 0 NOT NULL,
    CPHYTOCHORIAVERSION  INTEGER DEFAULT 0 NOT NULL,
    CTERRITORYVERSION    INTEGER DEFAULT 0 NOT NULL,
PRIMARY KEY (CID));

CREATE TABLE TPLANTS (
    CID                    INTEGER NOT NULL auto_increment,
    CSURVEYTAXID           VARCHAR(20) CHARACTER SET UTF8 NOT NULL,
    CTAXON                 VARCHAR(255) CHARACTER SET UTF8 NOT NULL,
    CGENUS                 VARCHAR(30) CHARACTER SET UTF8,
    CSPECIES               VARCHAR(100) CHARACTER SET UTF8,
    CSCIENTIFICNAMEAUTHOR  VARCHAR(150) CHARACTER SET UTF8,
    CCZECHNAME             VARCHAR(50) CHARACTER SET UTF8,
    CSYNONYMS              VARCHAR(255) CHARACTER SET UTF8,
    CNOTE                  VARCHAR(255) CHARACTER SET UTF8,
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

CREATE TABLE TRIGHT (
    CID             INTEGER NOT NULL auto_increment,
    CADMINISTRATOR  SMALLINT DEFAULT 0 NOT NULL,
    CEDITALL        SMALLINT DEFAULT 0 NOT NULL,
    CEDITGROUP      VARCHAR(4096) CHARACTER SET UTF8,
    CSEECOLUMNS     VARCHAR(4096) CHARACTER SET UTF8,
    CADD            SMALLINT DEFAULT 0 NOT NULL,
    CVERSION          INTEGER DEFAULT 0 NOT NULL,
PRIMARY KEY (CID));

CREATE TABLE TUSER (
    CID          INTEGER NOT NULL auto_increment,
    CLOGIN       VARCHAR(20) CHARACTER SET UTF8 NOT NULL,   
    CFIRSTNAME   VARCHAR(20) CHARACTER SET UTF8,
    CSURNAME     VARCHAR(30) CHARACTER SET UTF8,
    CWHOLENAME   VARCHAR(50) CHARACTER SET UTF8 NOT NULL,
    CEMAIL       VARCHAR(50) CHARACTER SET UTF8,
    CADDRESS     VARCHAR(255) CHARACTER SET UTF8,
    CCREATEWHEN  TIMESTAMP NOT NULL,
    CDROPWHEN    TIMESTAMP NULL DEFAULT NULL,
    CRIGHTID     INTEGER NOT NULL ,
    CNOTE        VARCHAR(4096) CHARACTER SET UTF8,
    CVERSION          INTEGER DEFAULT 0 NOT NULL,
PRIMARY KEY (CID),
FOREIGN KEY (CRIGHTID) REFERENCES tright(CID));

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
    CDELETE                   SMALLINT DEFAULT 0 NOT NULL,
    CVERSION          INTEGER DEFAULT 0 NOT NULL,
PRIMARY KEY (CID));

CREATE TABLE TPUBLICATIONS (
    CID                         INTEGER NOT NULL auto_increment,
    CCOLLECTIONNAME             VARCHAR(255) CHARACTER SET UTF8,
    CCOLLECTIONYEARPUBLICATION  SMALLINT,
    CJOURNALNAME                VARCHAR(255) CHARACTER SET UTF8,
    CJOURNALAUTHORNAME          VARCHAR(255) CHARACTER SET UTF8,
    CREFERENCECITATION          VARCHAR(4096) NOT NULL,
    CREFERENCEDETAIL            VARCHAR(100) CHARACTER SET UTF8,
    CURL                        VARCHAR(100) CHARACTER SET UTF8,
    CNOTE                       VARCHAR(4096) CHARACTER SET UTF8,
    CCREATEWHO                  INTEGER NOT NULL,
    CDELETE                     SMALLINT DEFAULT 0 NOT NULL,
    CVERSION          INTEGER DEFAULT 0 NOT NULL,
PRIMARY KEY (CID),
FOREIGN KEY (CCREATEWHO) REFERENCES TUSER(CID));

CREATE TABLE TAUTHORS (
    CID               INTEGER NOT NULL auto_increment,    
    CWHOLENAME        VARCHAR(50) CHARACTER SET UTF8 NOT NULL,
    CORGANIZATION     VARCHAR(50) CHARACTER SET UTF8,
    CTELEPHONENUMBER  VARCHAR(20) CHARACTER SET UTF8,
    CROLE             VARCHAR(30) CHARACTER SET UTF8,
    CADDRESS          VARCHAR(255) CHARACTER SET UTF8,
    CEMAIL            VARCHAR(100) CHARACTER SET UTF8,
    CURL              VARCHAR(255) CHARACTER SET UTF8,
    CNOTE             VARCHAR(4096) CHARACTER SET UTF8,
    CCREATEWHO         INTEGER NOT NULL,
    CDELETE           SMALLINT DEFAULT 0 NOT NULL,
    CVERSION          INTEGER DEFAULT 0 NOT NULL,
PRIMARY KEY (CID),
FOREIGN KEY (CCREATEWHO) REFERENCES TUSER(CID));

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
    CCREATEWHO        INTEGER NOT NULL,
    CDELETE            SMALLINT DEFAULT 0 NOT NULL,
    CVERSION          INTEGER DEFAULT 0 NOT NULL,
PRIMARY KEY (CID),
FOREIGN KEY (CTERRITORYID) REFERENCES TTERRITORIES(CID),
FOREIGN KEY (CPHYTOCHORIAID) REFERENCES TPHYTOCHORIA(CID),
FOREIGN KEY (CCREATEWHO) REFERENCES TUSER(CID),
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
    CDELETE            SMALLINT DEFAULT 0 NOT NULL,
    CVERSION          INTEGER DEFAULT 0 NOT NULL,
PRIMARY KEY (CID),
FOREIGN KEY (CHABITATID) REFERENCES THABITATS(CID),
FOREIGN KEY (CPLANTID) REFERENCES TPLANTS(CID),
FOREIGN KEY (CPUBLICATIONSID) REFERENCES TPUBLICATIONS(CID),
FOREIGN KEY (CCREATEWHO) REFERENCES TUSER(CID),
FOREIGN KEY (CUPDATEWHO) REFERENCES TUSER(CID),
FOREIGN KEY (CMETADATAID) REFERENCES TMETADATA(CID));

CREATE TABLE TAUTHORSOCCURRENCES (
    CAUTHORID          INTEGER NOT NULL auto_increment,
    COCCURRENCEID      INTEGER,
    CID                INTEGER NOT NULL,
    CROLE              VARCHAR(20) CHARACTER SET UTF8,
    Cnote  VARCHAR(4096) CHARACTER SET UTF8,
    CDELETE           SMALLINT DEFAULT 0 NOT NULL,
    CVERSION          INTEGER DEFAULT 0 NOT NULL,
PRIMARY KEY (CID),
FOREIGN KEY (CAUTHORID) REFERENCES TAUTHORS(CID),
FOREIGN KEY (COCCURRENCEID) REFERENCES TOCCURRENCES(CID));

CREATE TABLE THISTORYCOLUMN (
    CID          INTEGER NOT NULL auto_increment,
    CTABLENAME   VARCHAR(20) CHARACTER SET UTF8 NOT NULL,
    CCOLUMNNAME  VARCHAR(30) CHARACTER SET UTF8,
PRIMARY KEY (CID));

CREATE TABLE THISTORYCHANGE (
    CID            INTEGER NOT NULL auto_increment,    
    CRECORDID      INTEGER  DEFAULT 0 NOT NULL,    
    COPERATION     SMALLINT  DEFAULT 0 NOT NULL,
    CWHEN          TIMESTAMP NOT NULL,
    CWHO           INTEGER NOT NULL,
    CVERSION          INTEGER DEFAULT 0 NOT NULL,
PRIMARY KEY (CID),
FOREIGN KEY (CWHO) REFERENCES TUSER(CID));

CREATE TABLE THISTORY (
        CID       INTEGER NOT NULL auto_increment,
        CCOLUMNID INTEGER NOT NULL,
        CCHANGEID INTEGER NOT NULL,
        COLDVALUE VARCHAR(4096) CHARACTER SET UTF8,
        CNEWVALUE VARCHAR(4096) CHARACTER SET UTF8,
        COLDRECORDID   INTEGER,
        CVERSION          INTEGER DEFAULT 0 NOT NULL,
PRIMARY KEY (CID),
FOREIGN KEY (CCOLUMNID) REFERENCES THISTORYCOLUMN(CID),
FOREIGN KEY (CCHANGEID) REFERENCES THISTORYCHANGE(CID));

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

/* CREATE USER was added in MySQL 5.0.2 therefore this was not tested... */

CREATE USER 'plantlore' IDENTIFIED BY PASSWORD 'plantlore';
CREATE USER 'www' IDENTIFIED BY PASSWORD 'www';

GRANT DELETE, INSERT, SELECT, UPDATE, REFERENCES ON TAUTHORS TO plantlore;
GRANT DELETE, INSERT, SELECT, UPDATE, REFERENCES ON TAUTHORSOCCURRENCES TO plantlore;
GRANT DELETE, INSERT, SELECT, UPDATE, REFERENCES ON THABITATS TO plantlore;
GRANT DELETE, INSERT, SELECT, UPDATE, REFERENCES ON THISTORY TO plantlore;
GRANT DELETE, INSERT, SELECT, UPDATE, REFERENCES ON THISTORYCHANGE TO plantlore;
GRANT DELETE, INSERT, SELECT, UPDATE, REFERENCES ON THISTORYCOLUMN TO plantlore;
GRANT DELETE, INSERT, SELECT, UPDATE, REFERENCES ON TMETADATA TO plantlore;
GRANT DELETE, INSERT, SELECT, UPDATE, REFERENCES ON TOCCURRENCES TO plantlore;
GRANT DELETE, INSERT, SELECT, UPDATE, REFERENCES ON TPHYTOCHORIA TO plantlore;
GRANT DELETE, INSERT, SELECT, UPDATE, REFERENCES ON TPLANTS TO plantlore;
GRANT DELETE, INSERT, SELECT, UPDATE, REFERENCES ON TTERRITORIES TO plantlore;
GRANT DELETE, INSERT, SELECT, UPDATE, REFERENCES ON TUSER TO plantlore;
GRANT DELETE, INSERT, SELECT, UPDATE, REFERENCES ON TRIGHT TO plantlore;
GRANT DELETE, INSERT, SELECT, UPDATE, REFERENCES ON TVILLAGES TO plantlore;
GRANT DELETE, INSERT, SELECT, UPDATE, REFERENCES ON TPUBLICATIONS TO plantlore;
GRANT DELETE, INSERT, SELECT, UPDATE, REFERENCES ON TLASTDATAVERSION TO plantlore;

GRANT SELECT ON vhabitats TO www;
GRANT SELECT ON vmetadata TO www;
GRANT SELECT ON voccurrences TO www;
GRANT SELECT ON tphytochoria TO www;
GRANT SELECT ON tplants TO www;
GRANT SELECT ON tterritories TO www;
GRANT SELECT ON tuser TO www;
GRANT SELECT ON tvillages TO www;
GRANT SELECT ON vpublications TO www;
GRANT SELECT ON vauthorscollected TO www;
GRANT SELECT ON vauthorsrevised TO www;
GRANT SELECT ON vauthorsidentified TO www;

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
