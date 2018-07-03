ALTER TABLE subject
  ADD COLUMN course_work BOOLEAN NOT NULL DEFAULT FALSE;

ALTER TABLE subject
  ADD COLUMN week_number NUMERIC(2) NULL;

ALTER TABLE subject drop COLUMN group_lec_id;
ALTER TABLE subject drop COLUMN group_prac_id;
ALTER TABLE subject drop COLUMN group_lab_id;

ALTER TABLE curriculum_detail drop COLUMN elective_subject_id;
ALTER TABLE curriculum_detail drop COLUMN elective_subject_credit;
ALTER TABLE curriculum_detail drop COLUMN elective_subject_cycle_id;

ALTER TABLE curriculum_after_semester
  ADD COLUMN semester_id BIGINT NOT NULL DEFAULT 1;

drop view V_CURRICULUM_AFTER_SEMESTER;

CREATE OR REPLACE VIEW V_CURRICULUM_AFTER_SEMESTER AS
  SELECT
    curr_after_sem.ID,
    curr_after_sem.CURRICULUM_ID,
    curr_after_sem.SUBJECT_ID,
    subj.NAME_RU           SUBJECT_NAME_RU,
    curr_after_sem.CODE    SUBJECT_CODE,
    subj.CREDITABILITY_ID,
    cred.CREDIT,
    curr_after_sem.SEMESTER_ID,
    sem.SEMESTER_NAME,
    edu_mod_type.id        education_module_type_id,
    edu_mod_type.type_name education_module_type_name,
    curr_after_sem.DELETED
  FROM CURRICULUM_AFTER_SEMESTER curr_after_sem INNER JOIN SUBJECT subj ON curr_after_sem.SUBJECT_ID = subj.ID
    INNER JOIN CREDITABILITY cred ON subj.CREDITABILITY_ID = cred.ID
    INNER JOIN semester sem ON curr_after_sem.semester_id = sem.ID
    INNER JOIN education_module_type edu_mod_type ON edu_mod_type.id = curr_after_sem.education_module_type_id;

ALTER TABLE groups
  ADD COLUMN language_id BIGINT NOT NULL DEFAULT 1;
ALTER TABLE groups
  ADD COLUMN study_year_id BIGINT NOT NULL DEFAULT 1;

ALTER TABLE groups
  ADD CONSTRAINT fk_groups_language FOREIGN KEY (language_id)
REFERENCES language (id)
ON DELETE RESTRICT ON UPDATE RESTRICT;

ALTER TABLE groups
  ADD CONSTRAINT fk_groups_study_year FOREIGN KEY (study_year_id)
REFERENCES study_year (id)
ON DELETE RESTRICT ON UPDATE RESTRICT;

--not for real
-- UPDATE language
-- SET lang_name = 'Русский', lang_short_name = 'Рус'
-- WHERE id = 2;
-- INSERT INTO language VALUES (3, 'Английский', 'Анг');

CREATE TABLE stream (
  id               BIGINT       NOT NULL,
  name             VARCHAR(255) NOT NULL,
  semester_data_id BIGINT       NOT NULL,
  semester_id      BIGINT       NOT NULL,
  created          TIMESTAMP    NOT NULL,
  updated          TIMESTAMP    NOT NULL
);

ALTER TABLE stream
  ADD CONSTRAINT pk_stream PRIMARY KEY (id);

ALTER TABLE ONLY stream
  ADD CONSTRAINT fk_stream_semester_data FOREIGN KEY (semester_data_id)
REFERENCES semester_data (id)
ON UPDATE RESTRICT ON DELETE RESTRICT;

ALTER TABLE ONLY stream
  ADD CONSTRAINT fk_stream_semester FOREIGN KEY (semester_id)
REFERENCES semester (id)
ON UPDATE RESTRICT ON DELETE RESTRICT;

CREATE TABLE stream_group (
  id        BIGINT NOT NULL,
  stream_id BIGINT NOT NULL,
  group_id  BIGINT NOT NULL
);

ALTER TABLE stream_group
  ADD CONSTRAINT pk_stream_group PRIMARY KEY (id);

ALTER TABLE ONLY stream_group
  ADD CONSTRAINT fk_stream_group_stream FOREIGN KEY (stream_id)
REFERENCES stream (id)
ON UPDATE RESTRICT ON DELETE RESTRICT;

ALTER TABLE ONLY stream_group
  ADD CONSTRAINT fk_stream_group_group FOREIGN KEY (group_id)
REFERENCES groups (id)
ON UPDATE RESTRICT ON DELETE RESTRICT;

DROP VIEW V_STUDENT;

CREATE OR REPLACE VIEW V_STUDENT AS
  SELECT
    stu.ID,
    usr.FIRST_NAME,
    usr.LAST_NAME,
    usr.MIDDLE_NAME,
    usr.FIRST_NAME_EN,
    usr.LAST_NAME_EN,
    usr.MIDDLE_NAME_EN,
    usr.BIRTH_DATE,
    usr.SEX_ID,
    c.SEX_NAME,
    usr.MARITAL_STATUS_ID,
    d.STATUS_NAME                  MARITAL_STATUS_NAME,
    usr.NATIONALITY_ID,
    e.NATION_NAME,
    usr.CITIZENSHIP_ID,
    f.COUNTRY_NAME                 CITIZENSHIP_NAME,
    usr.CODE                       USER_CODE,
    usr.LOGIN,
    usr.EMAIL,
    usr.PHONE_MOBILE,
    stu.LEVEL_ID,
    g.LEVEL_NAME,
    stu.CATEGORY_ID,
    h.CATEGORY_NAME,
    stu.ACADEMIC_STATUS_ID,
    i.STATUS_NAME                  ACADEMIC_STATUS_NAME,
    stu.NEED_DORM,
    stu.ENTRANCE_YEAR_ID,
    j.ENTRANCE_YEAR,
    j.BEGIN_YEAR                   ENTRANCE_BEGIN_YEAR,
    j.END_YEAR                     ENTRANCE_END_YEAR,
    k.FACULTY_ID,
    l.DEPT_NAME                    FACULTY_NAME,
    l.DEPT_SHORT_NAME              FACULTY_SHORT_NAME,
    l.CODE                         FACULTY_CODE,
    k.CHAIR_ID,
    m.DEPT_NAME                    CHAIR_NAME,
    m.DEPT_SHORT_NAME              CHAIR_SHORT_NAME,
    m.CODE                         CHAIR_CODE,
    k.SPECIALITY_ID,
    n.CODE || ' - ' || n.SPEC_NAME SPECIALITY_NAME,
    n.CODE                         SPECIALITY_CODE,
    k.STUDY_YEAR_ID,
    k.EDUCATION_TYPE_ID,
    o.TYPE_NAME                    EDUCATION_TYPE_NAME,
    k.ENTRY_DATE,
    k.END_DATE,
    k.STUDENT_STATUS_ID,
    p.STATUS_NAME                  STUDENT_STATUS_NAME,
    card.id                        card_id,
    card.card_name,
    gr.id                          GROUPS_ID,
    gr.name                        GROUP_NAME,
    usr.DELETED,
    usr.CREATED,
    usr.UPDATED,
    advisor.fio                    advisor,
    coordinator.fio                coordinator
  FROM STUDENT stu INNER JOIN USERS usr ON stu.ID = usr.ID
    INNER JOIN SEX c ON usr.SEX_ID = c.ID
    INNER JOIN MARITAL_STATUS d ON usr.MARITAL_STATUS_ID = d.ID
    INNER JOIN NATIONALITY e ON usr.NATIONALITY_ID = e.ID
    INNER JOIN COUNTRY f ON usr.CITIZENSHIP_ID = f.ID
    INNER JOIN LEVEL g ON stu.LEVEL_ID = g.ID
    INNER JOIN STUDENT_CATEGORY h ON stu.CATEGORY_ID = h.ID
    LEFT JOIN ACADEMIC_STATUS i ON stu.ACADEMIC_STATUS_ID = i.ID
    INNER JOIN ENTRANCE_YEAR j ON stu.ENTRANCE_YEAR_ID = j.ID
    INNER JOIN STUDENT_EDUCATION k ON k.STUDENT_ID = stu.ID AND k.CHILD_ID IS NULL
    INNER JOIN DEPARTMENT l ON k.FACULTY_ID = l.ID AND l.parent_id IS NULL AND l.deleted = FALSE
    INNER JOIN DEPARTMENT m ON k.CHAIR_ID = m.ID AND m.parent_id IS NOT NULL AND m.deleted = FALSE
    INNER JOIN SPECIALITY n ON k.SPECIALITY_ID = n.ID AND n.deleted = FALSE
    INNER JOIN STUDENT_EDUCATION_TYPE o ON k.EDUCATION_TYPE_ID = o.ID
    INNER JOIN STUDENT_STATUS p ON k.STUDENT_STATUS_ID = p.ID
    LEFT JOIN v_advisor advisor ON advisor.id = stu.advisor_id
    LEFT JOIN v_coordinator coordinator ON coordinator.id = stu.coordinator_id
    LEFT JOIN CARD card ON card.id = usr.card_id
    LEFT JOIN groups gr ON gr.id = k.groups_id
  WHERE usr.deleted = FALSE AND usr.locked = FALSE;

CREATE OR REPLACE VIEW V_GROUP AS
  SELECT
    gr.id,
    gr.speciality_id,
    gr.name  group_name,
    gr.language_id,
    gr.study_year_id,
    COUNT(1) student_count
  FROM groups gr
    INNER JOIN v_student stu ON stu.groups_id = gr.id
  WHERE stu.category_id = 3 and gr.deleted=FALSE
  GROUP BY gr.id;

CREATE or REPLACE view v_stream as
  SELECT
    str.id,
    str.name              stream_name,
    str.semester_data_id,
    str.semester_id,
    sum(gr.student_count) student_count
  FROM stream str
    INNER JOIN stream_group str_gr ON str_gr.stream_id = str.id
    INNER JOIN v_group gr ON gr.id = str_gr.group_id
  GROUP BY str.id;

create sequence S_V_LOAD_TO_CHAIR
minvalue 0
start with 1
no cycle;

ALTER TABLE curriculum
  ADD COLUMN student_diploma_type_id BIGINT NOT NULL DEFAULT 1;

ALTER TABLE ONLY curriculum
  ADD CONSTRAINT fk_curriculum_student_diploma_type FOREIGN KEY (student_diploma_type_id)
REFERENCES student_diploma_type (id)
ON UPDATE RESTRICT ON DELETE RESTRICT;

CREATE OR REPLACE VIEW V_LOAD_TO_CHAIR AS
  SELECT DISTINCT
    nextval('s_v_load_to_chair')            id,
    subj.id                                 subject_id,
    curr.id                                 curriculum_id,
    curr_subj.subject_name,
    CASE WHEN curr_subj.semester_id IN (1, 2)
      THEN 1
    WHEN curr_subj.semester_id IN (3, 4)
      THEN 2
    WHEN curr_subj.semester_id IN (5, 6)
      THEN 3
    WHEN curr_subj.semester_id IN (7, 8)
      THEN 4 END                            study_year,
    string_agg(gr.group_name, ', ')         stream,
    sem.semester_name                       semester_name,
    sum(gr.student_count)                   student_number,
    curr_subj.credit,
    subj.lc_count                           lc_count,
    subj.pr_count * count(gr.student_count) pr_count,
    subj.lb_count * count(gr.student_count) lb_count,
    credit * 5                              with_teacher_count,
    sum(gr.student_count) / 4               rating_count,
    count(gr.student_count) * 2             exam_count,
    CASE WHEN curr.student_diploma_type_id = 1
      THEN 0
    ELSE sum(gr.student_count) / 5 END      control_count,
    CASE WHEN subj.course_work = TRUE
      THEN sum(gr.student_count) / 4
    ELSE 0 END                              course_work_count,
    CASE WHEN curr_subj.semester_id IN (7, 8)
      THEN 12 * sum(gr.student_count)
    ELSE 0 END                              diploma_count,
    CASE WHEN subj.week_number IS NOT NULL
      THEN subj.week_number * sum(gr.student_count)
    ELSE 0 END                              practice_count,
    CASE WHEN curr_subj.semester_id = 8
      THEN sum(gr.student_count) / 2
    ELSE 0 END                              mek,
    CASE WHEN curr_subj.semester_id = 8
      THEN sum(gr.student_count) * 0.6
    ELSE 0 END                              protect_diploma_count,
    lc_count + pr_count + lb_count + with_teacher_count
    + sum(gr.student_count) / 4 + count(gr.student_count) * 2
    + CASE WHEN curr.student_diploma_type_id = 1
      THEN 0
      ELSE sum(gr.student_count) / 5 END
    + CASE WHEN subj.course_work = TRUE
      THEN sum(gr.student_count) / 4
      ELSE 0 END
    + CASE WHEN curr_subj.semester_id IN (7, 8)
      THEN 12 * sum(gr.student_count)
      ELSE 0 END
    + CASE WHEN subj.week_number IS NOT NULL
      THEN subj.week_number * sum(gr.student_count)
      ELSE 0 END
    + CASE WHEN curr_subj.semester_id = 8
      THEN sum(gr.student_count) / 2
      ELSE 0 END
    + CASE WHEN curr_subj.semester_id = 8
      THEN sum(gr.student_count) * 0.6
      ELSE 0 END                            total_count
  FROM v_curriculum_subject curr_subj
    INNER JOIN subject subj ON subj.id = curr_subj.subject_id
    INNER JOIN curriculum curr ON curr_subj.curriculum_id = curr.id
    INNER JOIN v_group gr ON gr.speciality_id = curr.speciality_id
    INNER JOIN stream_group str_gr ON str_gr.group_id = gr.id
    INNER JOIN semester sem ON sem.id = curr_subj.semester_id
  WHERE curr_subj.deleted = FALSE AND subj.deleted = FALSE
        AND curr.deleted = FALSE
  GROUP BY subject_name, curr_subj.semester_id, sem.semester_name, curr_subj.credit,
    subj.lc_count, subj.pr_count, subj.lb_count, subj.course_work, subj.week_number,
    curr_subj.id, subj.id, curr.id
  UNION ALL
  SELECT DISTINCT
    nextval('s_v_load_to_chair')            id,
    subj.id                                 subject_id,
    curr.id                                 curriculum_id,
    curr_add_pr.subject_name_ru             subject_name,
    CASE WHEN curr_add_pr.semester_id IN (1, 2)
      THEN 1
    WHEN curr_add_pr.semester_id IN (3, 4)
      THEN 2
    WHEN curr_add_pr.semester_id IN (5, 6)
      THEN 3
    WHEN curr_add_pr.semester_id IN (7, 8)
      THEN 4 END                            study_year,
    string_agg(gr.group_name, ', ')         stream,
    sem.semester_name                       semester_name,
    sum(gr.student_count)                   student_number,
    curr_add_pr.credit,
    subj.lc_count                           lc_count,
    subj.pr_count * count(gr.student_count) pr_count,
    subj.lb_count * count(gr.student_count) lb_count,
    credit * 5                              with_teacher_count,
    sum(gr.student_count) / 4               rating_count,
    count(gr.student_count) * 2             exam_count,
    CASE WHEN curr.student_diploma_type_id = 1
      THEN 0
    ELSE sum(gr.student_count) / 5 END      control_count,
    CASE WHEN subj.course_work = TRUE
      THEN sum(gr.student_count) / 4
    ELSE 0 END                              course_work_count,
    CASE WHEN curr_add_pr.semester_id IN (7, 8)
      THEN 12 * sum(gr.student_count)
    ELSE 0 END                              diploma_count,
    CASE WHEN subj.week_number IS NOT NULL
      THEN subj.week_number * sum(gr.student_count)
    ELSE 0 END                              practice_count,
    CASE WHEN curr_add_pr.semester_id = 8
      THEN sum(gr.student_count) / 2
    ELSE 0 END                              mek,
    CASE WHEN curr_add_pr.semester_id = 8
      THEN sum(gr.student_count) * 0.6
    ELSE 0 END                              protect_diploma_count,
    lc_count + pr_count + lb_count + with_teacher_count
    + sum(gr.student_count) / 4 + count(gr.student_count) * 2
    + CASE WHEN curr.student_diploma_type_id = 1
      THEN 0
      ELSE sum(gr.student_count) / 5 END
    + CASE WHEN subj.course_work = TRUE
      THEN sum(gr.student_count) / 4
      ELSE 0 END
    + CASE WHEN curr_add_pr.semester_id IN (7, 8)
      THEN 12 * sum(gr.student_count)
      ELSE 0 END
    + CASE WHEN subj.week_number IS NOT NULL
      THEN subj.week_number * sum(gr.student_count)
      ELSE 0 END
    + CASE WHEN curr_add_pr.semester_id = 8
      THEN sum(gr.student_count) / 2
      ELSE 0 END
    + CASE WHEN curr_add_pr.semester_id = 8
      THEN sum(gr.student_count) * 0.6
      ELSE 0 END                            total_count
  FROM v_curriculum_add_program curr_add_pr
    INNER JOIN subject subj ON subj.id = curr_add_pr.subject_id
    INNER JOIN curriculum curr ON curr_add_pr.curriculum_id = curr.id
    INNER JOIN v_group gr ON gr.speciality_id = curr.speciality_id
    INNER JOIN stream_group str_gr ON str_gr.group_id = gr.id
    INNER JOIN semester sem ON sem.id = curr_add_pr.semester_id
  WHERE curr_add_pr.deleted = FALSE AND subj.deleted = FALSE
        AND curr.deleted = FALSE
  GROUP BY subject_name, curr_add_pr.semester_id, sem.semester_name, curr_add_pr.credit,
    subj.lc_count, subj.pr_count, subj.lb_count, subj.course_work, subj.week_number,
    curr_add_pr.id, subj.id, curr.id
  UNION ALL
  SELECT DISTINCT
    nextval('s_v_load_to_chair')            id,
    subj.id                                 subject_id,
    curr.id                                 curriculum_id,
    curr_after_sem.subject_name_ru          subject_name,
    CASE WHEN curr_after_sem.semester_id IN (1, 2)
      THEN 1
    WHEN curr_after_sem.semester_id IN (3, 4)
      THEN 2
    WHEN curr_after_sem.semester_id IN (5, 6)
      THEN 3
    WHEN curr_after_sem.semester_id IN (7, 8)
      THEN 4 END                            study_year,
    string_agg(gr.group_name, ', ')         stream,
    sem.semester_name                       semester_name,
    sum(gr.student_count)                   student_number,
    curr_after_sem.credit,
    subj.lc_count                           lc_count,
    subj.pr_count * count(gr.student_count) pr_count,
    subj.lb_count * count(gr.student_count) lb_count,
    credit * 5                              with_teacher_count,
    sum(gr.student_count) / 4               rating_count,
    count(gr.student_count) * 2             exam_count,
    CASE WHEN curr.student_diploma_type_id = 1
      THEN 0
    ELSE sum(gr.student_count) / 5 END      control_count,
    CASE WHEN subj.course_work = TRUE
      THEN sum(gr.student_count) / 4
    ELSE 0 END                              course_work_count,
    CASE WHEN curr_after_sem.semester_id IN (7, 8)
      THEN 12 * sum(gr.student_count)
    ELSE 0 END                              diploma_count,
    CASE WHEN subj.week_number IS NOT NULL
      THEN subj.week_number * sum(gr.student_count)
    ELSE 0 END                              practice_count,
    CASE WHEN curr_after_sem.semester_id = 8
      THEN sum(gr.student_count) / 2
    ELSE 0 END                              mek,
    CASE WHEN curr_after_sem.semester_id = 8
      THEN sum(gr.student_count) * 0.6
    ELSE 0 END                              protect_diploma_count,
    lc_count + pr_count + lb_count + with_teacher_count
    + sum(gr.student_count) / 4 + count(gr.student_count) * 2
    + CASE WHEN curr.student_diploma_type_id = 1
      THEN 0
      ELSE sum(gr.student_count) / 5 END
    + CASE WHEN subj.course_work = TRUE
      THEN sum(gr.student_count) / 4
      ELSE 0 END
    + CASE WHEN curr_after_sem.semester_id IN (7, 8)
      THEN 12 * sum(gr.student_count)
      ELSE 0 END
    + CASE WHEN subj.week_number IS NOT NULL
      THEN subj.week_number * sum(gr.student_count)
      ELSE 0 END
    + CASE WHEN curr_after_sem.semester_id = 8
      THEN sum(gr.student_count) / 2
      ELSE 0 END
    + CASE WHEN curr_after_sem.semester_id = 8
      THEN sum(gr.student_count) * 0.6
      ELSE 0 END                            total_count
  FROM v_curriculum_after_semester curr_after_sem
    INNER JOIN subject subj ON subj.id = curr_after_sem.subject_id
    INNER JOIN curriculum curr ON curr_after_sem.curriculum_id = curr.id
    INNER JOIN v_group gr ON gr.speciality_id = curr.speciality_id
    INNER JOIN stream_group str_gr ON str_gr.group_id = gr.id
    INNER JOIN semester sem ON sem.id = curr_after_sem.semester_id
  WHERE curr_after_sem.deleted = FALSE AND subj.deleted = FALSE
        AND curr.deleted = FALSE
  GROUP BY subject_name, curr_after_sem.semester_id, sem.semester_name, curr_after_sem.credit,
    subj.lc_count, subj.pr_count, subj.lb_count, subj.course_work, subj.week_number,
    curr_after_sem.id, subj.id, curr.id
  ORDER BY semester_name;

INSERT INTO TASKS (CLASS_PATH, DESCR, ICON_NAME, NAME, TASK_ORDER, TASK_TYPE, TITLE, VISIBLE, ID, PARENT_ID)
VALUES ('kz.halyqsoft.univercity.modules.loadtochair.LoadToChairView',
        'KK=Нагрузка на кафедру;RU=Нагрузка на кафедру;EN=Load to chair;', NULL,
        'KK=Нагрузка на кафедру;RU=Нагрузка на кафедру;EN=Load to chair;', 509, FALSE,
        'KK=Нагрузка на кафедру;RU=Нагрузка на кафедру;EN=Load to chair;', TRUE, nextval('s_tasks'), 29);

drop INDEX idx_t_curriculum;
CREATE UNIQUE INDEX idx_t_curriculum
  ON public.curriculum USING BTREE (speciality_id, entrance_year_id,student_diploma_type_id);

CREATE OR REPLACE VIEW v_load_to_chair_count AS
  SELECT
    study_year,
    curriculum_id,
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
  FROM v_load_to_chair
  GROUP BY study_year, curriculum_id;

CREATE OR REPLACE VIEW v_load_to_chair_count_all AS
  SELECT
    curriculum_id,
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
  GROUP BY curriculum_id;

CREATE VIEW v_groups_creation_needed
  as SELECT DISTINCT
       grs.id as id , spy.id as speciality_id,
       stt_edt.language_id, stt.entrance_year_id,
       stt_edt.study_year_id , sc.corpus_id
     from groups grs
       INNER JOIN speciality spy
         on grs.speciality_id = spy.id
       INNER JOIN student_education stt_edt
         on grs.id = stt_edt.groups_id
       INNER JOIN speciality_corpus sc
         ON spy.id = sc.speciality_id
       INNER JOIN student stt ON stt_edt.student_id = stt.id
     where grs.deleted = false and spy.deleted = false;

ALTER TABLE stream
  ALTER COLUMN semester_id DROP NOT NULL;
ALTER TABLE stream
  ALTER COLUMN updated DROP NOT NULL;
ALTER TABLE stream
  ALTER COLUMN name DROP NOT NULL;

ALTER TABLE employee_dept
  ADD COLUMN lecturer BOOLEAN NULL;

UPDATE employee_dept
SET lecturer = TRUE
WHERE employee_type_id = 2;
UPDATE employee_dept
SET lecturer = FALSE
WHERE employee_type_id = 1;

ALTER TABLE employee_dept
  ALTER COLUMN lecturer SET NOT NULL;

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
    empl_dept.lecturer
  FROM EMPLOYEE_DEPT empl_dept INNER JOIN EMPLOYEE_TYPE empl_type ON empl_dept.EMPLOYEE_TYPE_ID = empl_type.ID
    INNER JOIN DEPARTMENT dep ON empl_dept.DEPT_ID = dep.ID
    LEFT JOIN POST post ON empl_dept.POST_ID = post.ID
    INNER JOIN EMPLOYEE empl ON empl_dept.EMPLOYEE_ID = empl.ID;