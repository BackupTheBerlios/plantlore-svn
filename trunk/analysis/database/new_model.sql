CREATE TABLE tPhytochoria (
    cId INTEGER NOT NULL PRIMARY KEY,
    cCode VARCHAR(30),
    cName VARCHAR(30)
);

CREATE TABLE tVillages (
    cId INTEGER NOT NULL PRIMARY KEY,
    cName VARCHAR(30)
);

CREATE TABLE tTerritories (
    cId INTEGER NOT NULL PRIMARY KEY,
    cName VARCHAR(30)
);

CREATE TABLE tPlants (
    cId INTEGER NOT NULL PRIMARY KEY,
    cAdoptedName VARCHAR(30),
    cCzechName VARCHAR(30),
    cPublishableName VARCHAR(30),
    cNote VARCHAR(256)
);

CREATE TABLE tGPS (
    cId INTEGER NOT NULL PRIMARY KEY,
    cX INTEGER,
    cY INTEGER,
    cZ INTEGER
);

CREATE TABLE tAuthors (
    cId INTEGER NOT NULL PRIMARY KEY,
    cName VARCHAR(30),
    cContact VARCHAR(30),
    cReliability VARCHAR(30),
    cAbbreviation VARCHAR(30),
    cInstitution VARCHAR(30),
    cNote VARCHAR(256)
);


CREATE TABLE tUsers (
    cId INTEGER NOT NULL PRIMARY KEY,
    cUsername VARCHAR(30),
    cPassword VARCHAR(30),
    cFullName VARCHAR(30),
    cRights INTEGER
);

CREATE TABLE tHabitats (
    cId INTEGER NOT NULL PRIMARY KEY,
    cTerritoryId INTEGER REFERENCES tTerritories(cId),
    cPhytochoriaId INTEGER REFERENCES tPhytochoria(cId),
    cQuadrant VARCHAR(30),
    cPas VARCHAR(30),
    cDescription VARCHAR(512),
    cNearestVillageId INTEGER REFERENCES tVillages(cId),
    cLandscape VARCHAR(30),
    cAltitude INTEGER,
    cGPSId INTEGER REFERENCES tGPS(cId),
    cNote VARCHAR(256)
);

CREATE TABLE tOccurences (
    cId INTEGER NOT NULL PRIMARY KEY,
    cHabitatId INTEGER REFERENCES tHabitats(cId),
    cPlantId INTEGER REFERENCES tPlants(cId),
    cDate DATE,
    cSource VARCHAR(30),
    cHerbarium VARCHAR(30),
    cNote VARCHAR(256)
);

CREATE TABLE tAuthorsOccurences (
    cAuthorId INTEGER REFERENCES tAuthors(cId),
    cOccurenceId INTEGER REFERENCES tOccurences(cId)
);





