CREATE EXTENSION tablefunc;


CREATE TABLE ABSENCE_CAUSE (
  id      BIGINT    NOT NULL,
  name VARCHAR(255) NOT NULL
);


ALTER TABLE ABSENCE_CAUSE
  ADD CONSTRAINT pk_absence_cause PRIMARY KEY (id);

CREATE SEQUENCE S_ABSENCE_CAUSE
  MINVALUE 0
  START WITH 1
  NO CYCLE;

CREATE TABLE EMPLOYEE_ABSENCE_CAUSE (
  id BIGINT NOT NULL ,
  employee_id BIGINT NOT NULL ,
  absence_cause_id BIGINT NOT NULL ,
  starting_date TIMESTAMP NOT NULL ,
  final_date TIMESTAMP NOT NULL,
  created TIMESTAMP NOT NULL
);


ALTER TABLE EMPLOYEE_ABSENCE_CAUSE
  ADD CONSTRAINT pk_employee_absence_cause PRIMARY KEY (id);

ALTER TABLE EMPLOYEE_ABSENCE_CAUSE
  ADD CONSTRAINT fk_employee_absence_cause_absence_cause FOREIGN KEY (absence_cause_id)
REFERENCES absence_cause (id) ON UPDATE RESTRICT ON DELETE RESTRICT ;

ALTER TABLE EMPLOYEE_ABSENCE_CAUSE
  ADD CONSTRAINT fk_employee_absence_cause_employee FOREIGN KEY (employee_id)
REFERENCES employee (id) ON UPDATE RESTRICT ON DELETE RESTRICT ;

CREATE SEQUENCE S_EMPLOYEE_ABSENCE_CAUSE
  MINVALUE 0
  START WITH 1
  NO CYCLE;

CREATE TABLE load_to_teacher
(
  id                    BIGINT NOT NULL ,
  subject_id            BIGINT NOT NULL ,
  curriculum_id         BIGINT NOT NULL ,
  study_year_id         BIGINT,
  stream_id             BIGINT ,
  group_id              BIGINT,
  semester_id           BIGINT,
  student_number        NUMERIC,
  credit                NUMERIC(2) ,
  lc_count              NUMERIC(3),
  pr_count              NUMERIC,
  lb_count              NUMERIC,
  with_teacher_count    NUMERIC,
  rating_count          NUMERIC,
  exam_count            BIGINT,
  control_count         NUMERIC,
  course_work_count     NUMERIC,
  diploma_count         NUMERIC,
  practice_count        NUMERIC,
  mek                   NUMERIC,
  protect_diploma_count NUMERIC,
  total_count           NUMERIC,
  teacher_id            BIGINT
);
CREATE SEQUENCE s_load_to_teacher MINVALUE 0 START WITH 1 no CYCLE;

ALTER TABLE load_to_teacher ADD CONSTRAINT pk_load_to_teacher PRIMARY KEY (id);
ALTER TABLE load_to_teacher ADD CONSTRAINT fk_load_to_teacher_subject FOREIGN KEY (subject_id) REFERENCES subject(id) ON UPDATE RESTRICT ON DELETE RESTRICT ;
ALTER TABLE load_to_teacher ADD CONSTRAINT fk_load_to_teacher_curriculum FOREIGN KEY (curriculum_id) REFERENCES curriculum(id) ON UPDATE RESTRICT ON DELETE RESTRICT ;
ALTER TABLE load_to_teacher ADD CONSTRAINT fk_load_to_teacher_study_year FOREIGN KEY (study_year_id) REFERENCES study_year(id) ON UPDATE RESTRICT ON DELETE RESTRICT ;
ALTER TABLE load_to_teacher ADD CONSTRAINT fk_load_to_teacher_stream FOREIGN KEY (stream_id) REFERENCES stream(id) ON UPDATE RESTRICT ON DELETE RESTRICT ;
ALTER TABLE load_to_teacher ADD CONSTRAINT fk_load_to_teacher_groups FOREIGN KEY (group_id) REFERENCES groups(id) ON UPDATE RESTRICT ON DELETE RESTRICT ;
ALTER TABLE load_to_teacher ADD CONSTRAINT fk_load_to_teacher_semester FOREIGN KEY (semester_id) REFERENCES semester(id) ON UPDATE RESTRICT ON DELETE RESTRICT ;
ALTER TABLE load_to_teacher ADD CONSTRAINT fk_load_to_teacher_teacher FOREIGN KEY (teacher_id) REFERENCES employee(id) ON UPDATE RESTRICT ON DELETE RESTRICT ;