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
  , CONSTRAINT employees_organization_fk FOREIGN KEY (organization_id) REFERENCES organizations(id)
  , CONSTRAINT employees_user_fk FOREIGN KEY (user_id) REFERENCES users(id)
) CHARACTER SET utf8mb4 ENGINE InnoDB;

-- path-enum

CREATE TABLE path_enum_dept(
    id BIGINT NOT NULL PRIMARY KEY
  , organization_id BIGINT NOT NULL
  , name VARCHAR(30) NOT NULL
  , path VARCHAR(500) NOT NULL
  , display_order INT NOT NULL
  , CONSTRAINT path_enum_dept_organization_fk FOREIGN KEY (organization_id) REFERENCES organizations(id)
) CHARACTER SET utf8mb4 ENGINE InnoDB;

CREATE INDEX path_enum_dept_path_idx ON path_enum_dept(path ASC, display_order ASC);

CREATE TABLE path_enum_dept_user(
    organization_id BIGINT NOT NULL
  , user_id BIGINT NOT NULL
  , dept_id BIGINT NOT NULL
  , CONSTRAINT path_enum_dept_user_pk PRIMARY KEY (organization_id, user_id, dept_id)
  , CONSTRAINT path_enum_dept_user_employees_fk
FOREIGN KEY (organization_id, user_id) REFERENCES employees (organization_id, user_id)
  , CONSTRAINT path_enum_dept_user_dept_fk FOREIGN KEY (dept_id) REFERENCES path_enum_dept(id)
) CHARACTER SET utf8mb4 ENGINE InnoDB;

-- nested set

CREATE TABLE nested_set_dept(
    id BIGINT NOT NULL PRIMARY KEY
  , organization_id BIGINT NOT NULL
  , name VARCHAR(30) NOT NULL
  , left_index BIGINT NOT NULL
  , right_index BIGINT NOT NULL
  , display_order INT NOT NULL
  , CONSTRAINT nested_set_dept_organization_fk FOREIGN KEY (organization_id) REFERENCES organizations(id)
) CHARACTER SET utf8mb4 ENGINE InnoDB;

CREATE INDEX nested_set_dept_left_index_idx ON
  nested_set_dept(organization_id ASC, left_index ASC, display_order ASC );
CREATE INDEX nested_set_dept_right_index_idx ON
  nested_set_dept(organization_id ASC, right_index DESC, display_order ASC);

CREATE TABLE nested_set_dept_user(
    organization_id BIGINT NOT NULL
  , user_id BIGINT NOT NULL
  , dept_id BIGINT NOT NULL
  , CONSTRAINT nested_set_dept_user_pk PRIMARY KEY (organization_id, user_id, dept_id)
  , CONSTRAINT nested_set_dept_user_employees_fk
FOREIGN KEY (organization_id, user_id) REFERENCES employees (organization_id, user_id)
  , CONSTRAINT nested_set_dept_user_dept_fk FOREIGN KEY (dept_id) REFERENCES nested_set_dept(id)
) CHARACTER SET utf8mb4 ENGINE InnoDB;

-- closure table

CREATE TABLE dept_names(
    id BIGINT NOT NULL PRIMARY KEY
  , organization_id BIGINT NOT NULL
  , name VARCHAR(30) NOT NULL
  , display_order INT NOT NULL
  , CONSTRAINT dept_names_organization_fk FOREIGN KEY (organization_id) REFERENCES organizations(id)
) CHARACTER SET utf8mb4 ENGINE InnoDB;

CREATE INDEX dept_names_display_order_idx ON dept_names(organization_id ASC, display_order ASC);

CREATE TABLE closure_table_dept(
    parent_id BIGINT NOT NULL
  , child_id BIGINT NOT NULL
  , CONSTRAINT closure_table_dept_pk PRIMARY KEY (parent_id, child_id)
  , CONSTRAINT closure_table_dept_parent_names_fk FOREIGN KEY (parent_id) REFERENCES dept_names(id)
  , CONSTRAINT closure_table_dept_child_names_fk FOREIGN KEY (child_id) REFERENCES dept_names(id)
) CHARACTER SET utf8mb4 ENGINE InnoDB;

CREATE TABLE closure_table_dept_user(
    organization_id BIGINT NOT NULL
  , user_id BIGINT NOT NULL
  , dept_id BIGINT NOT NULL
  , CONSTRAINT closure_table_dept_user_pk PRIMARY KEY (organization_id, user_id, dept_id)
  , CONSTRAINT closure_table_dept_user_employees_fk
FOREIGN KEY (organization_id, user_id) REFERENCES employees(organization_id, user_id)
  , CONSTRAINT closure_table_dept_user_dept_fk FOREIGN KEY (dept_id) REFERENCES dept_names(id)
) CHARACTER SET utf8mb4 ENGINE InnoDB;
