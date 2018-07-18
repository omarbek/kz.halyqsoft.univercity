ALTER TABLE schedule_detail
  ADD COLUMN semester_data_id BIGINT NOT NULL;
ALTER TABLE schedule_detail
  ADD CONSTRAINT fk_schedule_detail_semester_data FOREIGN KEY (semester_data_id)
REFERENCES semester_data (id)
ON DELETE RESTRICT ON UPDATE RESTRICT;

ALTER TABLE schedule_detail
  DROP COLUMN schedule_id;
alter table  schedule_log drop constraint fk_t_schedule_schedule_log;
DROP TABLE schedule;

insert into TIME values (1, 8.00, '08:00');
insert into TIME values (2, 9.00, '09:00');
insert into TIME values (3, 10.00, '10:00');
insert into TIME values (4, 11.00, '11:00');
insert into TIME values (5, 12.00, '12:00');
insert into TIME values (6, 13.00, '13:00');
insert into TIME values (7, 14.00, '14:00');
insert into TIME values (8, 15.00, '15:00');
insert into TIME values (9, 16.00, '16:00');
insert into TIME values (10, 17.00, '17:00');
insert into TIME values (11, 18.00, '18:00');
insert into TIME values (12, 19.00, '19:00');
insert into TIME values (13, 20.00, '20:00');
insert into TIME values (14, 21.00, '21:00');
insert into TIME values (15, 22.00, '22:00');
insert into TIME values (16, 8.83, '08:50');
insert into TIME values (17, 9.83, '09:50');
insert into TIME values (18, 10.67, '10:40');
insert into TIME values (19, 10.83, '10:50');
insert into TIME values (20, 11.50, '11:30');
insert into TIME values (21, 11.67, '11:40');
insert into TIME values (22, 11.83, '11:50');
insert into TIME values (23, 12.67, '12:40');
insert into TIME values (24, 12.83, '12:50');
insert into TIME values (25, 13.17, '13:10');
insert into TIME values (26, 13.50, '13:30');
insert into TIME values (27, 13.67, '13:40');
insert into TIME values (28, 13.83, '13:50');
insert into TIME values (29, 14.67, '14:40');
insert into TIME values (30, 14.83, '14:50');
insert into TIME values (31, 15.17, '15:10');
insert into TIME values (32, 15.67, '15:40');
insert into TIME values (33, 15.83, '15:50');
insert into TIME values (34, 16.67, '16:40');
insert into TIME values (35, 16.83, '16:50');
insert into TIME values (36, 17.67, '17:40');
insert into TIME values (37, 17.83, '17:50');
insert into TIME values (38, 18.50, '18:30');
insert into TIME values (39, 18.83, '18:50');
insert into TIME values (40, 19.33, '19:20');
insert into TIME values (41, 19.50, '19:30');
insert into TIME values (42, 19.83, '19:50');
insert into TIME values (43, 20.33, '20:20');
insert into TIME values (44, 20.50, '20:30');
insert into TIME values (45, 20.83, '20:50');
insert into TIME values (46, 21.33, '21:20');
insert into TIME values (47, 21.50, '21:30');
insert into TIME values (48, 21.83, '21:50');
insert into TIME values (49, 22.33, '22:20');

CREATE TABLE shift (
  id   BIGINT       NOT NULL,
  name VARCHAR(255) NOT NULL
);

ALTER TABLE shift
  ADD CONSTRAINT pk_shift PRIMARY KEY (id);

INSERT INTO shift VALUES (1, 1);
INSERT INTO shift VALUES (2, 2);

CREATE TABLE shift_study_year (
  id            BIGINT NOT NULL,
  study_year_id BIGINT NOT NULL,
  shift_id      BIGINT NOT NULL
);

ALTER TABLE shift_study_year
  ADD CONSTRAINT pk_shift_study_year PRIMARY KEY (id);

ALTER TABLE shift_study_year
  ADD CONSTRAINT fk_shift_study_year_study_year FOREIGN KEY (study_year_id)
REFERENCES study_year (id)
ON DELETE RESTRICT ON UPDATE RESTRICT;

ALTER TABLE shift_study_year
  ADD CONSTRAINT fk_shift_study_year_shift FOREIGN KEY (shift_id)
REFERENCES shift (id)
ON DELETE RESTRICT ON UPDATE RESTRICT;

CREATE SEQUENCE s_shift_study_year
MINVALUE 0
START WITH 1
NO CYCLE;

CREATE TABLE lesson_time (
  id            BIGINT     NOT NULL,
  lesson_number NUMERIC(2) NOT NULL,
  begin_time_id BIGINT     NOT NULL,
  end_time_id   BIGINT     NOT NULL,
  shift_id      BIGINT     NOT NULL
);

ALTER TABLE lesson_time
  ADD CONSTRAINT pk_lesson_time PRIMARY KEY (id);

ALTER TABLE lesson_time
  ADD CONSTRAINT fk_lesson_time_begin_time FOREIGN KEY (begin_time_id)
REFERENCES time (id)
ON DELETE RESTRICT ON UPDATE RESTRICT;

ALTER TABLE lesson_time
  ADD CONSTRAINT fk_lesson_time_end_time FOREIGN KEY (end_time_id)
REFERENCES time (id)
ON DELETE RESTRICT ON UPDATE RESTRICT;

ALTER TABLE lesson_time
  ADD CONSTRAINT fk_lesson_time_shift FOREIGN KEY (shift_id)
REFERENCES shift (id)
ON DELETE RESTRICT ON UPDATE RESTRICT;

CREATE SEQUENCE s_lesson_time
MINVALUE 0
START WITH 1
NO CYCLE;

ALTER TABLE schedule_detail drop COLUMN begin_time_id;
ALTER TABLE schedule_detail drop COLUMN end_time_id;

ALTER table schedule_detail add column lesson_time_id BIGINT NOT NULL;
ALTER TABLE schedule_detail
  ADD CONSTRAINT fk_schedule_detail_lesson_time FOREIGN KEY (lesson_time_id)
REFERENCES lesson_time (id)
ON DELETE RESTRICT ON UPDATE RESTRICT;

create unique index idx_shift_study_year on shift_study_year (
  study_year_id asc
);

CREATE SEQUENCE s_time
MINVALUE 0
START WITH 50
NO CYCLE;

CREATE UNIQUE INDEX idx_time
  ON time (
    time_value ASC
  );

ALTER TABLE schedule_detail
  ADD COLUMN group_id BIGINT NOT NULL;
ALTER TABLE schedule_detail
  ADD CONSTRAINT fk_schedule_detail_group FOREIGN KEY (group_id)
REFERENCES groups (id)
ON DELETE RESTRICT ON UPDATE RESTRICT;

ALTER table schedule_detail drop COLUMN lesson_name;

CREATE SEQUENCE S_SCHEDULE_DETAIL
MINVALUE 0
START WITH 1
NO CYCLE;

ALTER TABLE schedule_detail
  DROP CONSTRAINT fk_t_schedule_detail_subject;

ALTER TABLE schedule_detail
  ADD CONSTRAINT fk_t_schedule_detail_subject FOREIGN KEY (subject_id)
REFERENCES subject (id)
ON DELETE RESTRICT ON UPDATE RESTRICT;

CREATE OR REPLACE VIEW V_TEACHER_LOAD_ASSIGN_DETAIL AS
  SELECT
    load_assign_det.ID,
    subj.id                     subject_id,
    subj.NAME_RU                SUBJECT_NAME,
    load_assign_det.stream_id,
    str.name                    stream_name,
    load_assign_det.semester_id,
    sem.semester_name,
    load_assign_det.SEMESTER_PERIOD_ID,
    sem_period.PERIOD_NAME      SEMESTER_PERIOD_NAME,
    sum(gr.student_count)       STUDENT_COUNT,
    cred.CREDIT,
    academ_form.FORMULA,
    load_assign_det.LC_HOUR,
    load_assign_det.LB_HOUR,
    load_assign_det.PR_HOUR,
    CASE WHEN pr_hour != 0
      THEN CASE WHEN load_assign_det.student_diploma_type_id = 1
        THEN credit * 5
           WHEN load_assign_det.student_diploma_type_id IN (6, 7)
             THEN (lc_hour + lb_hour + pr_hour) / 2
           ELSE 0 END
    ELSE 0 END                  with_teacher_hour,
    sum(gr.student_count) / 4   rating_hour,
    COUNT(gr.student_count) * 2 exam_hour,
    CASE WHEN load_assign_det.student_diploma_type_id IN (6, 7)
      THEN sum(gr.student_count) / 5
    ELSE 0 END                  control_hour,
    CASE WHEN subj.course_work = TRUE
      THEN sum(gr.student_count) / 4
    ELSE 0 END                  course_work_hour,
    CASE WHEN load_assign_det.semester_id IN (7, 8)
      THEN 12 * sum(gr.student_count)
    ELSE 0 END                  diploma_hour,
    CASE WHEN subj.week_number IS NOT NULL
      THEN subj.week_number * sum(gr.student_count)
    ELSE 0 END                  practice_hour,
    CASE WHEN load_assign_det.semester_id = 8
      THEN sum(gr.student_count) / 2
    ELSE 0 END                  mek,
    CASE WHEN load_assign_det.semester_id = 8
      THEN sum(gr.student_count) * 0.6
    ELSE 0 END                  protect_diploma_hour,
    LC_HOUR + PR_HOUR + LB_HOUR + with_teacher_count
    + sum(gr.student_count) / 4 + COUNT(gr.student_count) * 2
    + CASE WHEN load_assign_det.student_diploma_type_id = 1
      THEN 0
      ELSE sum(gr.student_count) / 5 END
    + CASE WHEN subj.course_work = TRUE
      THEN sum(gr.student_count) / 4
      ELSE 0 END
    + CASE WHEN load_assign_det.semester_id IN (7, 8)
      THEN 12 * sum(gr.student_count)
      ELSE 0 END
    + CASE WHEN subj.week_number IS NOT NULL
      THEN subj.week_number * sum(gr.student_count)
      ELSE 0 END
    + CASE WHEN load_assign_det.semester_id = 8
      THEN sum(gr.student_count) / 2
      ELSE 0 END
    + CASE WHEN load_assign_det.semester_id = 8
      THEN sum(gr.student_count) * 0.6
      ELSE 0 END                total_hour,
    load_assign_det.teacher_load_assign_id,
    load_assign_det.teacher_id,
    load_assign_det.student_diploma_type_id,
    load_assign_det.group_id
  FROM TEACHER_LOAD_ASSIGN_DETAIL load_assign_det
    INNER JOIN SUBJECT subj ON load_assign_det.SUBJECT_ID = subj.ID
    INNER JOIN CREDITABILITY cred ON subj.CREDITABILITY_ID = cred.ID
    INNER JOIN SEMESTER_PERIOD sem_period ON load_assign_det.SEMESTER_PERIOD_ID = sem_period.ID
    INNER JOIN ACADEMIC_FORMULA academ_form ON subj.ACADEMIC_FORMULA_ID = academ_form.ID
    INNER JOIN stream str ON str.id = load_assign_det.stream_id
    INNER JOIN semester sem ON sem.id = load_assign_det.semester_id
    INNER JOIN stream_group str_gr ON str_gr.stream_id = str.id
    LEFT JOIN v_group gr ON gr.id = str_gr.group_id
  GROUP BY load_assign_det.ID, subj.id, subj.NAME_RU, load_assign_det.stream_id, str.name,
    load_assign_det.semester_id, sem.semester_name, cred.CREDIT, academ_form.FORMULA,
    load_assign_det.SEMESTER_PERIOD_ID, sem_period.PERIOD_NAME, load_assign_det.LC_HOUR,
    load_assign_det.LB_HOUR, load_assign_det.PR_HOUR;