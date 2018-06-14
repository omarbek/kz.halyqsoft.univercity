CREATE TABLE education_module_type (
  id        BIGINT       NOT NULL,
  type_name VARCHAR(128) NOT NULL
);

ALTER TABLE education_module_type
  ADD CONSTRAINT pk_education_module_type PRIMARY KEY (id);

CREATE SEQUENCE s_education_module_type
  minvalue 0
  start with 1
  no cycle;

ALTER TABLE subject
  ADD COLUMN education_module_type_id BIGINT NOT NULL;

ALTER TABLE ONLY subject
  ADD CONSTRAINT fk_subject_education_module_type FOREIGN KEY (education_module_type_id)
REFERENCES education_module_type (id)
ON UPDATE RESTRICT ON DELETE RESTRICT;

update users set created_by = 'admin' where created_by is null;

alter table users
  alter column created_by set not null;