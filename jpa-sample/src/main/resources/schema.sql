USE `jpa-sample`;

CREATE TABLE application_user
(
  id BIGINT(20) PRIMARY KEY NOT NULL AUTO_INCREMENT,
  username VARCHAR(64) NOT NULL,
  password VARCHAR(512) NOT NULL,
  version INT(11) NOT NULL
);
create index application_user__index_username
  on application_user (username)
;
