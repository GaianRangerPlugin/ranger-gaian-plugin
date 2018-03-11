create database CDD  ;

connect to CompanyDirectoryDatabase;

drop table if exists ContactPhone;
CREATE TABLE ContactPhone (
  RecId INT NOT NULL,
  ContactType CHAR NOT NULL,
  Number VARCHAR(40) NOT NULL
) ;

load data infile '/Users/jonesn/VDCData/CompanyDirectoryDatabase-ContactPhone.csv' into table ContactPhone columns terminated by ';' ignore 1 lines;
