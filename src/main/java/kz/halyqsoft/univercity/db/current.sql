CREATE TABLE candidate
(
  id             BIGINT                NOT NULL,
  candidate_name CHARACTER VARYING(32) NOT NULL,
  PRIMARY KEY (id)
);

ALTER TABLE employee_degree
  ADD entrance_year DATE;

ALTER TABLE employee_degree
  ADD qualification_id INT;

ALTER TABLE employee_degree
  ADD CONSTRAINT fk_qualification
FOREIGN KEY (qualification_id)
REFERENCES qualification (id)
ON UPDATE RESTRICT ON DELETE RESTRICT;

ALTER TABLE employee_degree
  ADD condidate_id INT;

ALTER TABLE employee_degree
  ADD CONSTRAINT fk_employee_degree_qualification
FOREIGN KEY (condidate_id)
REFERENCES candidate (id)
ON UPDATE RESTRICT ON DELETE RESTRICT;

ALTER TABLE employee_degree
  ADD speciality_id INT;

ALTER TABLE employee_degree
  ADD CONSTRAINT fk_employee_degree_qualification_speciality
FOREIGN KEY (speciality_id)
REFERENCES speciality (id)
ON UPDATE RESTRICT ON DELETE RESTRICT;

DROP VIEW V_EMPLOYEE_DEGREE;

ALTER TABLE employee_degree
  RENAME COLUMN "school_name" TO "place_of_issue";

CREATE OR REPLACE VIEW V_EMPLOYEE_DEGREE AS
  SELECT
    empl_degree.ID,
    usr_doc.USER_ID    EMPLOYEE_ID,
    usr_doc.DOCUMENT_NO,
    usr_doc.ISSUE_DATE,
    usr_doc.EXPIRE_DATE,
    empl_degree.DEGREE_ID,
    degree.DEGREE_NAME DEGREE_NAME,
    empl_degree.place_of_issue,
    empl_degree.DISSERTATION_TOPIC,
    empl_degree.condidate_id,
    empl_degree.speciality_id,
    empl_degree.qualification_id,
    empl_degree.entrance_year,
    usr_doc.DELETED
  FROM EMPLOYEE_DEGREE empl_degree
    INNER JOIN USER_DOCUMENT usr_doc ON empl_degree.ID = usr_doc.ID
    INNER JOIN DEGREE degree ON empl_degree.DEGREE_ID = degree.ID;

_____________________________________________________________________________

CREATE TABLE master (
  id              BIGINT  NOT NULL,
  entrance_year   DATE    NOT NULL,
  graduation_year DATE    NOT NULL,
  university_id   BIGINT  NOT NULL,
  speciality_id   BIGINT  NOT NULL,
  qulification_id BIGINT  NOT NULL,
  diploma_number  BIGINT  NOT NULL,
  employee_id     BIGINT  NOT NULL,
  deleted         BOOLEAN NOT NULL
);

CREATE SEQUENCE s_master
MINVALUE 0
START WITH 1
NO CYCLE;

ALTER TABLE master
  ADD CONSTRAINT pk_master PRIMARY KEY (id);

ALTER TABLE ONLY master
  ADD CONSTRAINT fk_master_university FOREIGN KEY (university_id)
REFERENCES university (id)
ON UPDATE RESTRICT ON DELETE RESTRICT;

ALTER TABLE master
  ADD CONSTRAINT fk_master_speciality FOREIGN KEY (speciality_id)
REFERENCES speciality (id)
ON DELETE RESTRICT ON UPDATE RESTRICT;

ALTER TABLE master
  ADD CONSTRAINT fk_master_qualification FOREIGN KEY (qulification_id)
REFERENCES qualification (id)
ON DELETE RESTRICT ON UPDATE RESTRICT;

ALTER TABLE master
  ADD CONSTRAINT fk_master_employee FOREIGN KEY (employee_id)
REFERENCES users (id)
ON DELETE RESTRICT ON UPDATE RESTRICT;

CREATE UNIQUE INDEX idx_master
  ON master USING BTREE
  (university_id, speciality_id, qulification_id)
  WHERE deleted = FALSE;

CREATE OR REPLACE VIEW V_MASTER AS
  SELECT
    m.id,
    m.entrance_year,
    m.graduation_year,
    m.university_id,
    u.university_name    UNIVERSITY_NAME,
    m.speciality_id,
    s.spec_name          SPECIALITY_NAME,
    m.qulification_id,
    q.qualification_name QUALIFICATION_NAME,
    m.diploma_number,
    m.employee_id,
    e.id                 EMPLOYEE,
    m.deleted
  FROM master m
    INNER JOIN university u ON m.speciality_id = u.id
    INNER JOIN speciality s ON m.speciality_id = s.id
    INNER JOIN qualification q ON m.qulification_id = q.id
    INNER JOIN users e ON m.employee_id = e.id
  WHERE m.deleted = FALSE;