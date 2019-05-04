drop table if exists singer1 cascade;

CREATE TABLE SINGER1 (
ID INT NOT NULL AUTO_INCREMENT
, FIRST_NAME VARCHAR(60) NOT NULL
, LAST_NAME VARCHAR(40) NOT NULL
, BIRTH_DATE DATE
, VERSION INT NOT NULL DEFAULT 0
, UNIQUE UQ_SINGER_1 (FIRST_NAME, LAST_NAME)
, PRIMARY KEY (ID)
);

drop table if exists singer2 cascade;
CREATE TABLE SINGER2 (
ID INT NOT NULL AUTO_INCREMENT
, FIRST_NAME VARCHAR(60) NOT NULL
, LAST_NAME VARCHAR(40) NOT NULL
, BIRTH_DATE DATE
, VERSION INT NOT NULL DEFAULT 0
, UNIQUE UQ_SINGER_2 (FIRST_NAME, LAST_NAME)
, PRIMARY KEY (ID)
);
