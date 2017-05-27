USE `jpa_sample`;

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


CREATE TABLE email_address
(
    id BIGINT(20) PRIMARY KEY NOT NULL AUTO_INCREMENT,
    address VARCHAR(127) NOT NULL
);
CREATE UNIQUE INDEX email_address_address_uindex ON email_address (address);

create table user_email
(
  user_id bigint not null,
  email_id bigint not null,
  created_at datetime not null,
  primary key (user_id, email_id),
  constraint user_email_application_user_id_fk
  foreign key (user_id) references application_user (id),
  constraint user_email_email_address_id_fk
  foreign key (email_id) references email_address (id)
);

create index user_email_email_address_id_fk
  on user_email (email_id);
