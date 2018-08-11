CREATE TABLE candidate
(
  id           BIGINT                NOT NULL,
  candidate_name CHARACTER VARYING(32) NOT NULL,
    PRIMARY KEY (id)
);

  Alter TABLE employee_degree
add entrance_year DATE;


Alter TABLE employee_degree
    add qualification_id int;

ALTER TABLE employee_degree
  ADD CONSTRAINT fk_qualification
FOREIGN KEY (qualification_id)
REFERENCES qualification (id)
ON UPDATE RESTRICT ON DELETE RESTRICT ;

Alter TABLE employee_degree
    add condidate_id int;

ALTER TABLE employee_degree
  ADD CONSTRAINT fk_qualification
FOREIGN KEY (condidate_id)
REFERENCES condidate (id)
ON UPDATE RESTRICT ON DELETE RESTRICT ;

Alter TABLE employee_degree
    add speciality_id int;

ALTER TABLE employee_degree
  ADD CONSTRAINT fk_qualification
FOREIGN KEY (speciality_id)
REFERENCES speciality (id)
ON UPDATE RESTRICT ON DELETE RESTRICT ;

DROP VIEW V_EMPLOYEE_DEGREE;

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
    empl_degree.candidate_id,
    empl_degree.speciality_id,
    empl_degree.qualification_id,
    empl_degree.entrance_year,
    usr_doc.DELETED
  FROM EMPLOYEE_DEGREE empl_degree
    INNER JOIN USER_DOCUMENT usr_doc ON empl_degree.ID = usr_doc.ID
    INNER JOIN DEGREE degree ON empl_degree.DEGREE_ID = degree.ID;

ALTER TABLE employee_degree RENAME COLUMN "school_name" TO "place_of_issue";


