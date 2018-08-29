--set language in real

CREATE TABLE load_to_chair AS
  SELECT *
  FROM v_load_to_chair;

drop view v_load_to_chair_count_all;

CREATE OR REPLACE VIEW v_load_to_chair_count AS
  SELECT
    study_year,
    curriculum_id,
    subj.chair_id,
    sum(load.lc_count)           lc_count,
    sum(load.pr_count)           pr_count,
    sum(load.lb_count)           lb_count,
    sum(load.with_teacher_count) with_teacher_count,
    sum(rating_count)            rating_count,
    sum(exam_count)              exam_count,
    sum(control_count)           control_count,
    sum(course_work_count)       course_work_count,
    sum(diploma_count)           diploma_count,
    sum(practice_count)          practice_count,
    sum(mek)                     mek,
    sum(protect_diploma_count)   protect_diploma_count,
    sum(load.total_count)        total_count
  FROM load_to_chair load INNER JOIN subject subj ON subj.id = load.subject_id
  GROUP BY study_year, curriculum_id,subj.chair_id;

CREATE OR REPLACE VIEW v_load_to_chair_count_all AS
  SELECT
    curriculum_id,
    chair_id,
    sum(lc_count)              lc_count,
    sum(pr_count)              pr_count,
    sum(lb_count)              lb_count,
    sum(with_teacher_count)    with_teacher_count,
    sum(rating_count)          rating_count,
    sum(exam_count)            exam_count,
    sum(control_count)         control_count,
    sum(course_work_count)     course_work_count,
    sum(diploma_count)         diploma_count,
    sum(practice_count)        practice_count,
    sum(mek)                   mek,
    sum(protect_diploma_count) protect_diploma_count,
    sum(total_count)           total_count
  FROM v_load_to_chair_count
  GROUP BY curriculum_id, chair_id;

create sequence s_stream
minvalue 0
start with 1
no cycle;

create sequence s_stream_group
minvalue 0
start with 1
no cycle;

ALTER TABLE load_to_chair
  DROP COLUMN subject_name;

UPDATE post
SET tp = TRUE;

CREATE OR REPLACE VIEW v_stream AS
  SELECT
    str.id,
    str.name                stream_name,
    str.semester_data_id,
    str.semester_id,
    sum(gr.student_count)   student_count,
    count(gr.student_count) group_count
  FROM stream str
    INNER JOIN stream_group str_gr ON str_gr.stream_id = str.id
    INNER JOIN v_group gr ON gr.id = str_gr.group_id
  WHERE str.name IS NOT NULL
  GROUP BY str.id;

ALTER TABLE curriculum_after_semester
  DROP COLUMN semester_data_id;

ALTER TABLE ONLY curriculum_after_semester
  ADD CONSTRAINT fk_curriculum_after_semester_semester FOREIGN KEY (semester_id)
REFERENCES semester (id)
ON UPDATE RESTRICT ON DELETE RESTRICT;

CREATE INDEX idx_curriculum_after_semester
  ON curriculum_after_semester (
    curriculum_id ASC,
    subject_id ASC,
    semester_id ASC
  );

DROP VIEW V_LOAD_TO_CHAIR;
CREATE OR REPLACE VIEW V_LOAD_TO_CHAIR AS
  SELECT DISTINCT
    floor(random() * (1000) + 1) :: BIGINT id,
    subj.id                                subject_id,
    curr.id                                curriculum_id,
    sem.study_year_id,
    str.id                                 stream_id,
    0                                      group_id,
    sem.id                                 semester_id,
    str.student_count                      student_number,
    curr_subj.credit,
    subj.lc_count                          lc_count,
    subj.pr_count * str.group_count        pr_count,
    subj.lb_count * str.group_count        lb_count,
    credit * 5                             with_teacher_count,
    str.student_count / 4                  rating_count,
    str.group_count * 2                    exam_count,
    CASE WHEN curr.student_diploma_type_id = 1
      THEN 0
    ELSE str.student_count / 5 END         control_count,
    CASE WHEN subj.course_work = TRUE
      THEN str.student_count / 4
    ELSE 0 END                             course_work_count,
    CASE WHEN curr_subj.semester_id IN (7, 8)
      THEN 12 * str.student_count
    ELSE 0 END                             diploma_count,
    CASE WHEN subj.week_number IS NOT NULL
      THEN subj.week_number * str.student_count
    ELSE 0 END                             practice_count,
    CASE WHEN curr_subj.semester_id = 8
      THEN str.student_count / 2
    ELSE 0 END                             mek,
    CASE WHEN curr_subj.semester_id = 8
      THEN str.student_count * 0.6
    ELSE 0 END                             protect_diploma_count,
    lc_count + pr_count + lb_count + credit * 5
    + str.student_count / 4 + str.group_count * 2
    + CASE WHEN curr.student_diploma_type_id = 1
      THEN 0
      ELSE str.student_count / 5 END
    + CASE WHEN subj.course_work = TRUE
      THEN str.student_count / 4
      ELSE 0 END
    + CASE WHEN curr_subj.semester_id IN (7, 8)
      THEN 12 * str.student_count
      ELSE 0 END
    + CASE WHEN subj.week_number IS NOT NULL
      THEN subj.week_number * str.student_count
      ELSE 0 END
    + CASE WHEN curr_subj.semester_id = 8
      THEN str.student_count / 2
      ELSE 0 END
    + CASE WHEN curr_subj.semester_id = 8
      THEN str.student_count * 0.6
      ELSE 0 END                           total_count
  FROM v_curriculum_subject curr_subj
    INNER JOIN subject subj ON subj.id = curr_subj.subject_id
    INNER JOIN curriculum curr ON curr_subj.curriculum_id = curr.id
    INNER JOIN semester sem ON sem.id = curr_subj.semester_id
    INNER JOIN (SELECT
                  DISTINCT
                  str.id,
                  str.group_count,
                  str.student_count,
                  gr.speciality_id,
                  gr.study_year_id
                FROM v_stream str
                  INNER JOIN stream_group str_gr ON str.id = str_gr.stream_id
                  INNER JOIN v_group gr ON str_gr.group_id = gr.id
               ) str ON str.speciality_id = curr.speciality_id
  WHERE curr_subj.deleted = FALSE AND subj.deleted = FALSE
        AND curr.deleted = FALSE AND sem.study_year_id = str.study_year_id
  UNION ALL
  SELECT DISTINCT
    floor(random() * (1000) + 1) :: BIGINT id,
    subj.id                                subject_id,
    curr.id                                curriculum_id,
    sem.study_year_id,
    str.id                                 stream_id,
    0                                      group_id,
    sem.id                                 semester_id,
    str.student_count                      student_number,
    curr_add_pr.credit,
    subj.lc_count                          lc_count,
    subj.pr_count * str.group_count        pr_count,
    subj.lb_count * str.group_count        lb_count,
    credit * 5                             with_teacher_count,
    str.student_count / 4                  rating_count,
    str.group_count * 2                    exam_count,
    CASE WHEN curr.student_diploma_type_id = 1
      THEN 0
    ELSE str.student_count / 5 END         control_count,
    CASE WHEN subj.course_work = TRUE
      THEN str.student_count / 4
    ELSE 0 END                             course_work_count,
    CASE WHEN curr_add_pr.semester_id IN (7, 8)
      THEN 12 * str.student_count
    ELSE 0 END                             diploma_count,
    CASE WHEN subj.week_number IS NOT NULL
      THEN subj.week_number * str.student_count
    ELSE 0 END                             practice_count,
    CASE WHEN curr_add_pr.semester_id = 8
      THEN str.student_count / 2
    ELSE 0 END                             mek,
    CASE WHEN curr_add_pr.semester_id = 8
      THEN str.student_count * 0.6
    ELSE 0 END                             protect_diploma_count,
    lc_count + pr_count + lb_count + credit * 5
    + str.student_count / 4 + str.group_count * 2
    + CASE WHEN curr.student_diploma_type_id = 1
      THEN 0
      ELSE str.student_count / 5 END
    + CASE WHEN subj.course_work = TRUE
      THEN str.student_count / 4
      ELSE 0 END
    + CASE WHEN curr_add_pr.semester_id IN (7, 8)
      THEN 12 * str.student_count
      ELSE 0 END
    + CASE WHEN subj.week_number IS NOT NULL
      THEN subj.week_number * str.student_count
      ELSE 0 END
    + CASE WHEN curr_add_pr.semester_id = 8
      THEN str.student_count / 2
      ELSE 0 END
    + CASE WHEN curr_add_pr.semester_id = 8
      THEN str.student_count * 0.6
      ELSE 0 END                           total_count
  FROM v_curriculum_add_program curr_add_pr
    INNER JOIN subject subj ON subj.id = curr_add_pr.subject_id
    INNER JOIN curriculum curr ON curr_add_pr.curriculum_id = curr.id
    INNER JOIN semester sem ON sem.id = curr_add_pr.semester_id
    INNER JOIN study_year year ON year.id = sem.study_year_id
    INNER JOIN (SELECT
                  DISTINCT
                  str.id,
                  str.group_count,
                  str.student_count,
                  gr.speciality_id,
                  gr.study_year_id
                FROM v_stream str
                  INNER JOIN stream_group str_gr ON str.id = str_gr.stream_id
                  INNER JOIN v_group gr ON str_gr.group_id = gr.id
               ) str ON str.speciality_id = curr.speciality_id
  WHERE curr_add_pr.deleted = FALSE AND subj.deleted = FALSE
        AND curr.deleted = FALSE AND sem.study_year_id = str.study_year_id
  UNION ALL
  SELECT DISTINCT
    floor(random() * (1000) + 1) :: BIGINT id,
    subj.id                                subject_id,
    curr.id                                curriculum_id,
    sem.study_year_id,
    0                                      stream_id,
    gr.id                                  group_id,
    sem.id                                 semester_id,
    gr.student_count                       student_number,
    curr_after_sem.credit,
    subj.lc_count                          lc_count,
    subj.pr_count * 1                      pr_count,
    subj.lb_count * 1                      lb_count,
    credit * 5                             with_teacher_count,
    gr.student_count / 4                   rating_count,
    1 * 2                                  exam_count,
    CASE WHEN curr.student_diploma_type_id = 1
      THEN 0
    ELSE gr.student_count / 5 END          control_count,
    CASE WHEN subj.course_work = TRUE
      THEN gr.student_count / 4
    ELSE 0 END                             course_work_count,
    CASE WHEN curr_after_sem.semester_id IN (7, 8)
      THEN 12 * gr.student_count
    ELSE 0 END                             diploma_count,
    CASE WHEN subj.week_number IS NOT NULL
      THEN subj.week_number * gr.student_count
    ELSE 0 END                             practice_count,
    CASE WHEN curr_after_sem.semester_id = 8
      THEN gr.student_count / 2
    ELSE 0 END                             mek,
    CASE WHEN curr_after_sem.semester_id = 8
      THEN gr.student_count * 0.6
    ELSE 0 END                             protect_diploma_count,
    lc_count + pr_count + lb_count + credit * 5
    + gr.student_count / 4 + 1 * 2
    + CASE WHEN curr.student_diploma_type_id = 1
      THEN 0
      ELSE gr.student_count / 5 END
    + CASE WHEN subj.course_work = TRUE
      THEN gr.student_count / 4
      ELSE 0 END
    + CASE WHEN curr_after_sem.semester_id IN (7, 8)
      THEN 12 * gr.student_count
      ELSE 0 END
    + CASE WHEN subj.week_number IS NOT NULL
      THEN subj.week_number * gr.student_count
      ELSE 0 END
    + CASE WHEN curr_after_sem.semester_id = 8
      THEN gr.student_count / 2
      ELSE 0 END
    + CASE WHEN curr_after_sem.semester_id = 8
      THEN gr.student_count * 0.6
      ELSE 0 END                           total_count
  FROM v_curriculum_after_semester curr_after_sem
    INNER JOIN subject subj ON subj.id = curr_after_sem.subject_id
    INNER JOIN curriculum curr ON curr_after_sem.curriculum_id = curr.id
    INNER JOIN semester sem ON sem.id = curr_after_sem.semester_id
    INNER JOIN study_year year ON year.id = sem.study_year_id
    INNER JOIN v_group gr ON gr.speciality_id = curr.speciality_id
  WHERE curr_after_sem.deleted = FALSE AND subj.deleted = FALSE
        AND curr.deleted = FALSE AND sem.study_year_id = gr.study_year_id;

DROP VIEW v_load_to_chair_count_all;
DROP VIEW v_load_to_chair_count;
DROP TABLE load_to_chair;

CREATE TABLE load_to_chair (
  LIKE v_load_to_chair INCLUDING ALL
);

CREATE OR REPLACE VIEW v_load_to_chair_count AS
  SELECT
    study_year,
    curriculum_id,
    subj.chair_id,
    sum(load.lc_count)           lc_count,
    sum(load.pr_count)           pr_count,
    sum(load.lb_count)           lb_count,
    sum(load.with_teacher_count) with_teacher_count,
    sum(rating_count)            rating_count,
    sum(exam_count)              exam_count,
    sum(control_count)           control_count,
    sum(course_work_count)       course_work_count,
    sum(diploma_count)           diploma_count,
    sum(practice_count)          practice_count,
    sum(mek)                     mek,
    sum(protect_diploma_count)   protect_diploma_count,
    sum(load.total_count)        total_count
  FROM load_to_chair load
    INNER JOIN subject subj ON subj.id = load.subject_id
    INNER JOIN study_year year on year.id=load.study_year_id
  GROUP BY study_year, curriculum_id, subj.chair_id;

CREATE OR REPLACE VIEW v_load_to_chair_count_all AS
  SELECT
    curriculum_id,
    chair_id,
    sum(lc_count)              lc_count,
    sum(pr_count)              pr_count,
    sum(lb_count)              lb_count,
    sum(with_teacher_count)    with_teacher_count,
    sum(rating_count)          rating_count,
    sum(exam_count)            exam_count,
    sum(control_count)         control_count,
    sum(course_work_count)     course_work_count,
    sum(diploma_count)         diploma_count,
    sum(practice_count)        practice_count,
    sum(mek)                   mek,
    sum(protect_diploma_count) protect_diploma_count,
    sum(total_count)           total_count
  FROM v_load_to_chair_count
  GROUP BY curriculum_id, chair_id;

ALTER TABLE teacher_load_assign_detail
  ADD COLUMN study_year_id BIGINT NOT NULL;
ALTER TABLE teacher_load_assign_detail
  ADD COLUMN student_count NUMERIC(3) NOT NULL;
ALTER TABLE teacher_load_assign_detail
  ADD COLUMN creditability_id BIGINT NOT NULL;
ALTER TABLE teacher_load_assign_detail
  ADD COLUMN with_teacher_hour NUMERIC(5, 2);
ALTER TABLE teacher_load_assign_detail
  ADD COLUMN rating_hour NUMERIC(5, 2);
ALTER TABLE teacher_load_assign_detail
  ADD COLUMN exam_hour NUMERIC(5, 2);
ALTER TABLE teacher_load_assign_detail
  ADD COLUMN control_hour NUMERIC(5, 2);
ALTER TABLE teacher_load_assign_detail
  ADD COLUMN course_work_hour NUMERIC(5, 2);
ALTER TABLE teacher_load_assign_detail
  ADD COLUMN diploma_hour NUMERIC(5, 2);
ALTER TABLE teacher_load_assign_detail
  ADD COLUMN practice_hour NUMERIC(5, 2);
ALTER TABLE teacher_load_assign_detail
  ADD COLUMN mek NUMERIC(5, 2);
ALTER TABLE teacher_load_assign_detail
  ADD COLUMN protect_diploma_hour NUMERIC(5, 2);
ALTER TABLE teacher_load_assign_detail
  ADD COLUMN total_hour NUMERIC(5, 2);

ALTER TABLE teacher_load_assign_detail
  ADD CONSTRAINT fk_teacher_load_assign_detail_study_year FOREIGN KEY (study_year_id)
REFERENCES study_year (id)
ON DELETE RESTRICT ON UPDATE RESTRICT;

ALTER TABLE teacher_load_assign_detail
  ADD CONSTRAINT fk_teacher_load_assign_detail_creditability FOREIGN KEY (creditability_id)
REFERENCES creditability (id)
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
    LC_HOUR + PR_HOUR + LB_HOUR
    + CASE WHEN pr_hour != 0
      THEN CASE WHEN load_assign_det.student_diploma_type_id = 1
        THEN credit * 5
           WHEN load_assign_det.student_diploma_type_id IN (6, 7)
             THEN (lc_hour + lb_hour + pr_hour) / 2
           ELSE 0 END
      ELSE 0 END
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

--assyl
-- NON ADMISSION

CREATE TABLE non_admission_cause (
  id BIGINT NOT NULL ,
  name VARCHAR(255) NOT NULL
);

ALTER TABLE non_admission_cause ADD CONSTRAINT pk_non_admission_cause PRIMARY KEY (id);

CREATE SEQUENCE s_non_admission_cause
MINVALUE 0
start WITH 1
no CYCLE ;

CREATE TABLE non_admission_exam (
  id BIGINT NOT NULL ,
  student_id BIGINT NOT NULL,
  non_admission_cause_id BIGINT NOT NULL,
  semester_data_id BIGINT NOT NULL,
  created TIMESTAMP NOT NULL DEFAULT now()
);

ALTER TABLE non_admission_exam ADD CONSTRAINT pk_non_admission_exam PRIMARY KEY (id);

CREATE SEQUENCE s_non_admission_exam
MINVALUE 0
START WITH 1
NO CYCLE ;

ALTER TABLE non_admission_exam ADD CONSTRAINT fk_non_admission_exam_student
FOREIGN KEY (student_id) REFERENCES student(id)
ON DELETE RESTRICT
ON UPDATE RESTRICT;

ALTER TABLE non_admission_exam ADD CONSTRAINT fk_non_admission_exam_semester_data
FOREIGN KEY (semester_data_id) REFERENCES semester_data(id)
ON DELETE RESTRICT
ON UPDATE RESTRICT;

ALTER TABLE non_admission_exam ADD CONSTRAINT fk_non_admission_exam_non_admission_cause
FOREIGN KEY (non_admission_cause_id) REFERENCES non_admission_cause(id)
ON DELETE RESTRICT
ON UPDATE RESTRICT ;


CREATE TABLE non_admission_subject (
  id BIGINT NOT NULL ,
  non_admission_exam_id BIGINT NOT NULL,
  subject_id BIGINT NOT NULL
);

ALTER TABLE non_admission_subject ADD CONSTRAINT pk_non_admission_subject
FOREIGN KEY (non_admission_exam_id) REFERENCES non_admission_exam(id);

CREATE SEQUENCE s_non_admission_subject
MINVALUE 0
START WITH 1
NO CYCLE ;

ALTER TABLE non_admission_subject ADD CONSTRAINT fk_non_admission_subject_non_admission_exam
FOREIGN KEY (non_admission_exam_id) REFERENCES non_admission_exam(id)
ON UPDATE RESTRICT ON DELETE RESTRICT ;

ALTER TABLE non_admission_subject ADD CONSTRAINT fk_non_admission_subject_subject
FOREIGN KEY (subject_id) REFERENCES subject(id)
ON DELETE RESTRICT ON UPDATE RESTRICT;

DROP VIEW v_groups_creation_needed;

CREATE VIEW v_groups_creation_needed
  AS (select
        grs.id,
        s2.id speciality_id,
        grs.language_id ,
        vs.entrance_year_id,
        grs.study_year_id,
        c2.corpus_id
      FROM groups grs
        INNER JOIN speciality s2
          ON grs.speciality_id = s2.id
        INNER JOIN speciality_corpus c2
          ON s2.id = c2.speciality_id
        INNER JOIN v_student vs
          ON vs.groups_id = grs.id
      WHERE
        grs.deleted = FALSE
        and s2.deleted = FALSE);

ALTER TABLE groups ALTER COLUMN study_year_id DROP NOT NULL ;

UPDATE tasks SET name = 'KK=Жатақхана;RU=Общежитие;EN=Dorm;', title = 'KK=Жатақхана;RU=Общежитие;EN=Dorm;', task_type = false, task_order = 221, class_path = 'kz.halyqsoft.univercity.modules.dorm.DormView', parent_id = 3, icon_name = null, descr = 'KK=Жатақхана;RU=Общежитие;EN=Dorm;', visible = true WHERE id = 44;

-- INSERT INTO student_status VALUES (nextval('s_student_status' ), 'Оставлен на перекурс');

ALTER TABLE user_arrival ADD COLUMN manually_signed BOOLEAN NULL DEFAULT FALSE;

--damira
CREATE VIEW v_student_debts AS
  SELECT
    s2.id,
    x.user_code,
    trim(x.LAST_NAME || ' ' || x.FIRST_NAME || ' ' || coalesce(x.MIDDLE_NAME, '')) fio,
    CASE WHEN x.id = payment.student_id
      THEN (a.price - (sum(payment.payment_sum)))
    ELSE a.price END                                                               DEBT_SUM
  FROM v_student x
    INNER JOIN student s2 ON x.id = s2.id
    INNER JOIN student_diploma_type s3 ON s2.diploma_type_id = s3.id
    INNER JOIN accountant_price a ON s3.id = a.student_diploma_type_id
    LEFT JOIN student_payment AS payment ON payment.student_id = s2.id
  WHERE a.contract_payment_type_id = 2 AND
        a.level_id = x.level_id AND x.deleted = FALSE AND a.deleted = FALSE
  GROUP BY payment.student_id, s2.id, x.user_code, x.last_name, x.first_name, x.middle_name, x.id, a.price;

ALTER TABLE EMPLOYEE_DEPT
  ADD priority BOOLEAN NOT NULL DEFAULT FALSE;

DROP VIEW V_EMPLOYEE_DEPT;

CREATE OR REPLACE VIEW V_EMPLOYEE_DEPT AS
  SELECT
    empl_dept.ID,
    empl_dept.EMPLOYEE_ID,
    empl_dept.EMPLOYEE_TYPE_ID,
    empl_type.TYPE_NAME EMPLOYEE_TYPE_NAME,
    empl_dept.DEPT_ID,
    dep.DEPT_NAME,
    dep.DEPT_SHORT_NAME,
    empl_dept.POST_ID,
    post.POST_NAME,
    empl_dept.LIVE_LOAD,
    empl_dept.WAGE_RATE,
    empl_dept.RATE_LOAD,
    empl_dept.HOUR_COUNT,
    empl_dept.HIRE_DATE,
    empl_dept.DISMISS_DATE,
    empl_dept.ADVISER,
    empl_dept.PARENT_ID,
    empl_dept.lecturer,
    empl_dept.priority
  FROM EMPLOYEE_DEPT empl_dept INNER JOIN EMPLOYEE_TYPE empl_type ON empl_dept.EMPLOYEE_TYPE_ID = empl_type.ID
    INNER JOIN DEPARTMENT dep ON empl_dept.DEPT_ID = dep.ID
    LEFT JOIN POST post ON empl_dept.POST_ID = post.ID
    INNER JOIN EMPLOYEE empl ON empl_dept.EMPLOYEE_ID = empl.ID;