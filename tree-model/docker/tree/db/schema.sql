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
  , CONSTRAINT path_enum_project_user_project_fk FOREIGN KEY (project_id) REFERENCES path_enum_project(id)
) CHARACTER SET utf8mb4 ENGINE InnoDB;

-- nested set

CREATE TABLE nested_set_project(
    id BIGINT NOT NULL PRIMARY KEY
  , organization_id BIGINT NOT NULL
  , name VARCHAR(30) NOT NULL
  , left_index BIGINT NOT NULL
  , right_index BIGINT NOT NULL
  , display_order INT NOT NULL
  , CONSTRAINT nested_set_project_organization_fk FOREIGN KEY (organization_id) REFERENCES organizations(id)
) CHARACTER SET utf8mb4 ENGINE InnoDB;

CREATE INDEX nested_set_project_left_index_idx ON
  nested_set_project(organization_id ASC, left_index ASC, display_order ASC );
CREATE INDEX nested_set_project_right_index_idx ON
  nested_set_project(organization_id ASC, right_index DESC, display_order ASC);

CREATE TABLE nested_set_project_user(
    organization_id BIGINT NOT NULL
  , user_id BIGINT NOT NULL
  , project_id BIGINT NOT NULL
  , CONSTRAINT nested_set_project_user_pk PRIMARY KEY (organization_id, user_id, project_id)
  , CONSTRAINT nested_set_project_user_employees_fk
FOREIGN KEY (organization_id, user_id) REFERENCES employees (organization_id, user_id)
  , CONSTRAINT nested_set_project_user_project_fk FOREIGN KEY (project_id) REFERENCES nested_set_project(id)
) CHARACTER SET utf8mb4 ENGINE InnoDB;

-- closure table

CREATE TABLE project_names(
    id BIGINT NOT NULL PRIMARY KEY
  , organization_id BIGINT NOT NULL
  , name VARCHAR(30) NOT NULL
  , display_order INT NOT NULL
  , CONSTRAINT project_names_organization_fk FOREIGN KEY (organization_id) REFERENCES organizations(id)
) CHARACTER SET utf8mb4 ENGINE InnoDB;

CREATE INDEX project_names_display_order_idx ON project_names(organization_id ASC, display_order ASC);

CREATE TABLE closure_table_project(
    parent_id BIGINT NOT NULL
  , child_id BIGINT NOT NULL
  , CONSTRAINT closure_table_project_pk PRIMARY KEY (parent_id, child_id)
  , CONSTRAINT closure_table_project_parent_names_fk FOREIGN KEY (parent_id) REFERENCES project_names(id)
  , CONSTRAINT closure_table_project_child_names_fk FOREIGN KEY (child_id) REFERENCES project_names(id)
) CHARACTER SET utf8mb4 ENGINE InnoDB;

CREATE TABLE closure_table_project_user(
    organization_id BIGINT NOT NULL
  , user_id BIGINT NOT NULL
  , project_id BIGINT NOT NULL
  , CONSTRAINT closure_table_project_user_pk PRIMARY KEY (organization_id, user_id, project_id)
  , CONSTRAINT closure_table_project_user_employees_fk
FOREIGN KEY (organization_id, user_id) REFERENCES employees(organization_id, user_id)
  , CONSTRAINT closure_table_project_user_project_fk FOREIGN KEY (project_id) REFERENCES project_names(id)
) CHARACTER SET utf8mb4 ENGINE InnoDB;
