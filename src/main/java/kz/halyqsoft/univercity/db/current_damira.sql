CREATE TABLE speciality_code (
  id        BIGINT                 NOT NULL,
  speciality_id BIGINT                 NOT NULL,
  code_name CHARACTER VARYING(100) NOT NULL
);

ALTER TABLE ONLY speciality_code
  add CONSTRAINT pk_speciality_code PRIMARY KEY (id);

ALTER TABLE employee_degree
ADD COLUMN speciality_code_id BIGINT  NULL;

ALTER TABLE ONLY employee_degree
  ADD CONSTRAINT fk_employee_degree_speciality_code FOREIGN KEY (speciality_code_id) REFERENCES speciality_code (id)
ON UPDATE RESTRICT ON DELETE RESTRICT;

ALTER TABLE ONLY speciality_code
  ADD CONSTRAINT fk_speciality_code_speciality_id FOREIGN KEY (speciality_id) REFERENCES speciality (id)
ON UPDATE RESTRICT ON DELETE RESTRICT;

CREATE SEQUENCE s_speciality_code
  MINVALUE 0
  START WITH 1
  NO CYCLE;

CREATE TABLE academic_title (
  id        BIGINT                 NOT NULL,
  title_name CHARACTER VARYING(100) NOT NULL
);

ALTER TABLE ONLY academic_title
  add CONSTRAINT pk_academic_title PRIMARY KEY (id);

ALTER TABLE employee_degree
ADD COLUMN academic_title_id BIGINT  NULL;

ALTER TABLE ONLY employee_degree
  ADD CONSTRAINT fk_employee_degree_academic_title FOREIGN KEY (academic_title_id) REFERENCES academic_title (id)
ON UPDATE RESTRICT ON DELETE RESTRICT;

CREATE SEQUENCE s_academic_title
  MINVALUE 0
  START WITH 1
  NO CYCLE;

DROP VIEW v_employee_degree;

CREATE  VIEW v_employee_degree AS
  SELECT empl_degree.id,
    usr_doc.user_id AS employee_id,
    usr_doc.document_no,
    usr_doc.issue_date,
    usr_doc.expire_date,
    empl_degree.degree_id,
    degree.degree_name,
    empl_degree.place_of_issue,
    empl_degree.dissertation_topic,
    empl_degree.condidate_id,
    empl_degree.speciality_id,
    empl_degree.qualification_id,
    empl_degree.entrance_year,
    empl_degree.speciality_code_id,
    empl_degree.academic_title_id,
    usr_doc.deleted
  FROM ((employee_degree empl_degree
    JOIN user_document usr_doc ON ((empl_degree.id = usr_doc.id)))
    JOIN degree degree ON ((empl_degree.degree_id = degree.id)));

