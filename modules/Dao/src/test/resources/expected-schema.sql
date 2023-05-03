CREATE TABLE files (id varchar(36) not null, path text not null, primary key (id));
CREATE TABLE fingerprints (id varchar(36) not null, algorithm varchar(16) not null, file_id varchar(36) not null, name text not null, timestamp timestamp not null, fingerprint varchar(32) not null, primary key (id));
CREATE INDEX files__id on files (id);
CREATE INDEX files__path on files (path);
CREATE INDEX fingerprints__id on fingerprints (id);
CREATE INDEX fingerprints__name on fingerprints (name);
CREATE INDEX fingerprints__timestamp on fingerprints (timestamp);
CREATE INDEX fingerprints__fingerprint on fingerprints (fingerprint);
CREATE INDEX fingerprints__file_id on fingerprints (file_id);
