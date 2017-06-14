-- common tables

CREATE TABLE organizations (
    id BIGINT NOT NULL PRIMARY KEY
  , name VARCHAR(30) NOT NULL
  , version INT NOT NULL
) CHARACTER SET utf8mb4 ENGINE InnoDB;

CREATE TABLE users (
    id BIGINT NOT NULL PRIMARY KEY
  , name VARCHAR(30) NOT NULL
) CHARACTER SET utf8mb4 ENGINE InnoDB;

CREATE TABLE employees (
    organization_id BIGINT NOT NULL
  , user_id BIGINT NOT NULL
  , version INT NOT NULL
  , CONSTRAINT employees_pk PRIMARY KEY (organization_id, user_id)
) CHARACTER SET utf8mb4 ENGINE InnoDB;

-- path-enum

CREATE TABLE path_enum_project(
    id BIGINT NOT NULL PRIMARY KEY
  , organization_id BIGINT NOT NULL
  , name VARCHAR(30) NOT NULL
  , path VARCHAR(500) NOT NULL
  , display_order INT NOT NULL

  , CONSTRAINT path_enum_project_organization_fk FOREIGN KEY (organization_id) REFERENCES organizations(id)
) CHARACTER SET utf8mb4 ENGINE InnoDB;

CREATE INDEX path_enum_project_path_idx ON path_enum_project(path ASC, display_order ASC);

CREATE TABLE path_enum_project_user(
    organization_id BIGINT NOT NULL
  , user_id BIGINT NOT NULL
  , project_id BIGINT NOT NULL
  , CONSTRAINT path_enum_project_user_pk PRIMARY KEY (organization_id, user_id, project_id)
  , CONSTRAINT path_enum_project_user_employees_fk
FOREIGN KEY (organization_id, user_id) REFERENCES employees (organization_id, user_id)
) CHARACTER SET utf8mb4 ENGINE InnoDB;
