
ALTER TABLE education_doc
  ADD entrance_year DATE;

ALTER TABLE education_doc
  ADD qualification_id INT;


ALTER TABLE education_doc
  ADD CONSTRAINT fk_qualification
FOREIGN KEY (qualification_id)
REFERENCES qualification (id)
ON UPDATE RESTRICT ON DELETE RESTRICT;

--------------------------------------
ALTER TABLE PREVIOUS_EXPERIENCE
  DROP COLUMN DUTY;

ALTER TABLE PREVIOUS_EXPERIENCE
  ADD COLUMN DUTY  TEXT;

--------------------

DROP VIEW V_MASTER;

ALTER TABLE master
  DROP COLUMN qulification_id;

ALTER TABLE master
  DROP COLUMN entrance_year;

ALTER TABLE master
  DROP COLUMN university_id;

ALTER TABLE master
  DROP COLUMN speciality_id;

ALTER TABLE master
  ADD COLUMN university_name VARCHAR(255);

ALTER TABLE master
  ADD COLUMN speciality_name VARCHAR(255);

ALTER TABLE master
  DROP COLUMN ENTRANCE_YEAR ;

ALTER TABLE master
  DROP COLUMN GRADUATION_YEAR;

ALTER TABLE master
  ADD COLUMN ENTRANCE_YEAR VARCHAR(255);

ALTER TABLE master
  ADD COLUMN GRADUATION_YEAR VARCHAR(255);

CREATE OR REPLACE VIEW V_MASTER AS
  SELECT
    m.id,
    m.entrance_year,
    m.graduation_year,
    m.university_name,
    m.speciality_name,
    m.diploma_number,
    m.employee_id,
    e.id                 EMPLOYEE,
    m.deleted
  FROM master m
    INNER JOIN users e ON m.employee_id = e.id
  WHERE m.deleted = FALSE;

ALTER TABLE education_doc
  DROP COLUMN entrance_year;