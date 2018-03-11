
create database CDD  ;

connect to CDD;

drop table ContactEmail;
CREATE TABLE ContactEmail (
  RedIf INT NOT NULL,
  EType CHAR NOT NULL,
  Email VARCHAR(120) NOT NULL
) ;

import from '/Users/jonesn/VDCData/CompanyDirectoryDatabase-ContactEmail.csv' of DEL insert into ContactEmail skipcount 1;
