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
    INNER JOIN v_group gr ON gr.id = str_gr.group_id
  GROUP BY load_assign_det.ID, subj.id, str.name,
    sem.semester_name, cred.CREDIT, academ_form.FORMULA, sem_period.PERIOD_NAME;

alter table teacher_load_assign drop CONSTRAINT fk_t_teach_load_calc_assign;

ALTER TABLE teacher_load_assign_detail
  DROP CONSTRAINT fk_teacher_load_assign_detail_stream;
ALTER TABLE ONLY teacher_load_assign_detail
  ADD CONSTRAINT fk_teacher_load_assign_detail_stream FOREIGN KEY (stream_id)
REFERENCES stream (id)
ON UPDATE RESTRICT ON DELETE RESTRICT;

INSERT INTO LESSON_TYPE VALUES (1, 'Лекция', 'Лек');
INSERT INTO LESSON_TYPE VALUES (2, 'Лабораторное', 'Лаб');
INSERT INTO LESSON_TYPE VALUES (3, 'Практическое', 'Прак');

DROP INDEX idx_t_teacher_load_assign_detail;

CREATE UNIQUE INDEX idx_t_teacher_load_assign_detail
  ON teacher_load_assign_detail (
    teacher_load_assign_id ASC,
    teacher_id ASC,
    subject_id ASC,
    semester_period_id ASC,
    stream_id ASC,
    group_id ASC
  );

create sequence s_study_direct
minvalue 0
start with 4
no cycle;