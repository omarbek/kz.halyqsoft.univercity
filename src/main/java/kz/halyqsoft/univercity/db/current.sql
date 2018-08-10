update entrant_speciality set university_id=1 where university_id=2;

CREATE TABLE student_difference (
  id      BIGINT    NOT NULL,
  student_education_id BIGINT    NOT NULL,
  subject_id BIGINT    NOT NULL
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
  FROM student_difference sd INNER JOIN student_education se ON sd.student_education_id=se.id
    INNER JOIN subject sbj ON sd.subject_id=sbj.id;