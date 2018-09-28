ALTER TABLE pair_subject
  ADD COLUMN code BIGINT NULL;

ALTER TABLE ONLY pair_subject
  ADD CONSTRAINT fk_pair_subject_postrequisite FOREIGN KEY (postrequisite_id)
REFERENCES subject (id) ON UPDATE RESTRICT ON DELETE RESTRICT;

ALTER TABLE ONLY pair_subject
  ADD CONSTRAINT fk_pair_subject_prerequisite FOREIGN KEY (prerequisite_id)
REFERENCES subject (id) ON UPDATE RESTRICT ON DELETE RESTRICT;
--raikhan
ALTER TABLE pair_subject
  ALTER COLUMN code TYPE VARCHAR(255);

CREATE TABLE student_teacher_subject (
  id                   BIGINT NOT NULL,
  student_education_id BIGINT NOT NULL,
  teacher_subject_id   BIGINT NOT NULL,
  semester_id          BIGINT NOT NULL
);

CREATE SEQUENCE s_student_teacher_subject
MINVALUE 0
START WITH 1
NO CYCLE;

ALTER TABLE student_teacher_subject
  ADD CONSTRAINT pk_student_teacher_subject PRIMARY KEY (id);

ALTER TABLE student_teacher_subject
  ADD CONSTRAINT fk_student_teacher_subject_student_education FOREIGN KEY (student_education_id)
REFERENCES student_education (id)
ON DELETE RESTRICT ON UPDATE RESTRICT;

ALTER TABLE student_teacher_subject
  ADD CONSTRAINT fk_student_teacher_subject_teacher_subject FOREIGN KEY (teacher_subject_id)
REFERENCES teacher_subject (id)
ON DELETE RESTRICT ON UPDATE RESTRICT;

ALTER TABLE student_teacher_subject
  ADD CONSTRAINT fk_student_teacher_subject_semester FOREIGN KEY (semester_id)
REFERENCES semester (id)
ON DELETE RESTRICT ON UPDATE RESTRICT;

ALTER TABLE dorm_room
    ALTER COLUMN bed_count TYPE NUMERIC(2);