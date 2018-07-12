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

ALTER TABLE elective_binded_subject DROP COLUMN first_subject_id;

ALTER TABLE elective_binded_subject DROP COLUMN second_subject_id;

CREATE TABLE pair_subject(
  id                         BIGINT  NOT NULL,
  subject_id                 BIGINT  NOT NULL,
  elective_binded_subject_id BIGINT  NOT NULL,
  pair_number                BIGINT  NOT NULL
);

CREATE SEQUENCE s_pair_subject
  minvalue 0
 start with 1
  no cycle;

ALTER TABLE pair_subject
  ADD CONSTRAINT pk_pair_subject PRIMARY KEY (id);

ALTER TABLE ONLY pair_subject
  ADD CONSTRAINT fk_pair_subject_subject FOREIGN KEY (subject_id)
REFERENCES subject (id)
ON UPDATE RESTRICT ON DELETE RESTRICT;

ALTER TABLE ONLY pair_subject
  ADD CONSTRAINT fk_pair_subject_elective_binded_subject FOREIGN KEY (elective_binded_subject_id)
REFERENCES elective_binded_subject (id)
ON UPDATE RESTRICT ON DELETE RESTRICT;

CREATE UNIQUE index idx_elective_binded_subject
  ON elective_binded_subject (semester_id, catalog_elective_subjects_id);