UPDATE entrant_speciality
SET university_id = 1
WHERE university_id = 2;

CREATE TABLE student_difference (
  id                   BIGINT NOT NULL,
  student_education_id BIGINT NOT NULL,
  subject_id           BIGINT NOT NULL
);

ALTER TABLE student_difference
  ADD CONSTRAINT fk_student_difference_student_education FOREIGN KEY (student_education_id)
REFERENCES student_education (id)
ON DELETE RESTRICT ON UPDATE RESTRICT;


ALTER TABLE student_difference
  ADD CONSTRAINT fk_student_difference_subject FOREIGN KEY (subject_id)
REFERENCES subject (id)
ON DELETE RESTRICT ON UPDATE RESTRICT;


CREATE SEQUENCE s_student_difference
MINVALUE 0;

CREATE VIEW V_STUDENT_DIFFERENCE AS
  SELECT
    sd.ID,
    sd.student_education_id,
    sd.subject_id
  FROM student_difference sd INNER JOIN student_education se ON sd.student_education_id = se.id
    INNER JOIN subject sbj ON sd.subject_id = sbj.id;

CREATE TABLE graduate_employment (
  id            BIGINT NOT NULL,
  student_id    BIGINT NOT NULL,
  employed      BOOLEAN,
  by_speciality BOOLEAN,
  master        BOOLEAN,
  decree        BOOLEAN,
  army          BOOLEAN
);

ALTER TABLE graduate_employment
  ADD CONSTRAINT pk_graduate_employment PRIMARY KEY (id);

ALTER TABLE ONLY graduate_employment
  ADD CONSTRAINT fk_graduate_employment FOREIGN KEY (student_id)
REFERENCES student (id)
ON UPDATE RESTRICT ON DELETE RESTRICT;

CREATE SEQUENCE s_graduate_employment
MINVALUE 0
START WITH 1
NO CYCLE;

CREATE UNIQUE INDEX idx_graduate_employment
  ON graduate_employment (student_id);