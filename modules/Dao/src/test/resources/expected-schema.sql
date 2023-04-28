PRAGMA foreign_keys=OFF;
BEGIN TRANSACTION;
CREATE TABLE files (id varchar(36) not null, path text not null, primary key (id));
CREATE TABLE fingerprints (id varchar(36) not null, algorithm varchar(16) not null, file_id varchar(36) not null, name text not null, timestamp timestamp not null, fingerprint varchar(32) not null, primary key (id));
COMMIT;
