CREATE OR REPLACE VIEW v_stream AS
  SELECT
    str.id,
    str.name                stream_name,
    str.semester_id,
    sem.semester_name,
    str.language_id,
    lang.lang_name,
    str.stream_type_id,
    str_type.type_name      stream_type_name,
    sum(gr.student_count)   student_count,
    count(gr.student_count) group_count
  FROM stream str
    INNER JOIN semester sem ON str.semester_id = sem.id
    INNER JOIN language lang ON str.language_id = lang.id
    INNER JOIN stream_type str_type ON str.stream_type_id = str_type.id
    LEFT JOIN stream_group str_gr ON str_gr.stream_id = str.id
    LEFT JOIN v_group gr ON gr.id = str_gr.group_id
  WHERE str.deleted = FALSE
  GROUP BY str.id, sem.semester_name, lang.lang_name, str_type.type_name;

DROP VIEW v_stream;

ALTER TABLE stream
  DROP COLUMN semester_id;

ALTER TABLE stream
  ADD COLUMN semester_period_id BIGINT;
ALTER TABLE ONLY stream
  ADD CONSTRAINT fk_stream_semester_period
FOREIGN KEY (semester_period_id)
REFERENCES semester_period (id)
ON UPDATE RESTRICT ON DELETE RESTRICT;

ALTER TABLE stream
  ADD COLUMN subject_id BIGINT;
ALTER TABLE ONLY stream
  ADD CONSTRAINT fk_stream_subject
FOREIGN KEY (subject_id)
REFERENCES subject (id)
ON UPDATE RESTRICT ON DELETE RESTRICT;

CREATE OR REPLACE VIEW v_stream AS
  SELECT
    str.id,
    str.name                stream_name,
    str.semester_period_id,
    sem_period.period_name,
    str.language_id,
    lang.lang_name,
    str.stream_type_id,
    str_type.type_name      stream_type_name,
    str.subject_id,
    subj.name_kz            subject_name_kz,
    subj.name_ru            subject_name_ru,
    sum(gr.student_count)   student_count,
    count(gr.student_count) group_count
  FROM stream str
    LEFT JOIN semester_period sem_period ON str.semester_period_id = sem_period.id
    LEFT JOIN subject subj ON subj.id = str.subject_id
    INNER JOIN language lang ON str.language_id = lang.id
    INNER JOIN stream_type str_type ON str.stream_type_id = str_type.id
    LEFT JOIN stream_group str_gr ON str_gr.stream_id = str.id
    LEFT JOIN v_group gr ON gr.id = str_gr.group_id
  WHERE str.deleted = FALSE
  GROUP BY str.id, sem_period.id, lang.id, str_type.id,subj.id;

--curriculum
CREATE TABLE curriculum_individual_plan (
  id            BIGINT      NOT NULL,
  speciality_id BIGINT      NOT NULL,
  student_code  VARCHAR(20) NOT NULL
);

ALTER TABLE ONLY curriculum_individual_plan
  ADD CONSTRAINT pk_curriculum_individual_plan PRIMARY KEY (id);

CREATE SEQUENCE s_curriculum_individual_plan
MINVALUE 0
START WITH 1
NO CYCLE;

ALTER TABLE curriculum_individual_plan
  ADD CONSTRAINT fk_curriculum_individual_plan_speciality FOREIGN KEY (speciality_id)
REFERENCES speciality (id)
ON DELETE RESTRICT ON UPDATE RESTRICT;

ALTER TABLE curriculum_individual_plan
  ADD COLUMN entrance_year_id BIGINT NOT NULL;

ALTER TABLE curriculum_individual_plan
  ADD CONSTRAINT fk_curriculum_individual_plan_entrance_year FOREIGN KEY (entrance_year_id)
REFERENCES entrance_year (id)
ON DELETE RESTRICT ON UPDATE RESTRICT;

ALTER TABLE curriculum_individual_plan
  ADD COLUMN diploma_type_id BIGINT NOT NULL;

ALTER TABLE curriculum_individual_plan
  ADD CONSTRAINT fk_curriculum_individual_plan_diploma_type FOREIGN KEY (diploma_type_id)
REFERENCES student_diploma_type (id)
ON DELETE RESTRICT ON UPDATE RESTRICT;
--end curriculum

