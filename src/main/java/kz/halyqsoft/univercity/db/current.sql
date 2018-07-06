INSERT INTO TASKS (CLASS_PATH, DESCR, ICON_NAME, NAME, TASK_ORDER, TASK_TYPE, TITLE, VISIBLE, ID, PARENT_ID)
VALUES ('kz.halyqsoft.univercity.modules.checkstudents.CheckStudentsView',
        'KK=Удалить неполных абитуриентов;RU=Удалить неполных абитуриентов;EN=Delete lacking applicants;',
        NULL, 'KK=Удалить неполных абитуриентов;RU=Удалить неполных абитуриентов;EN=Delete lacking applicants;', 304,
        FALSE, 'KK=Удалить неполных абитуриентов;RU=Удалить неполных абитуриентов;EN=Delete lacking applicants;', TRUE,
        nextval('s_tasks'), 7);

INSERT INTO TASKS (CLASS_PATH, DESCR, ICON_NAME, NAME, TASK_ORDER, TASK_TYPE, TITLE, VISIBLE, ID, PARENT_ID)
VALUES ('kz.halyqsoft.univercity.modules.returnstudent.ReturnStudentView',
        'KK=Сделать абитуриентом;RU=Сделать абитуриентом;EN=Make the applicant;',
        NULL, 'KK=Сделать абитуриентом;RU=Сделать абитуриентом;EN=Make the applicant;', 404,
        FALSE, 'KK=Сделать абитуриентом;RU=Сделать абитуриентом;EN=Make the applicant;', TRUE,
        nextval('s_tasks'), 26);

INSERT INTO TASKS (CLASS_PATH, DESCR, ICON_NAME, NAME, TASK_ORDER, TASK_TYPE, TITLE, VISIBLE, ID, PARENT_ID)
VALUES ('kz.halyqsoft.univercity.modules.search.SearchByCardView',
        'KK=Поиск по карте;RU=Поиск по карте;EN=Search by card;',
        NULL, 'KK=Поиск по карте;RU=Поиск по карте;EN=Search by card;', 405,
        FALSE, 'KK=Поиск по карте;RU=Поиск по карте;EN=Search by card;', TRUE,
        nextval('s_tasks'), 26);

CREATE OR REPLACE VIEW V_TEACHER_LOAD_CALC_DETAIL AS
  SELECT
    a.ID,
    b.NAME_RU     SUBJECT_NAME,
    c.CREDIT,
    e.FORMULA,
    a.SEMESTER_PERIOD_ID,
    d.PERIOD_NAME SEMESTER_PERIOD_NAME,
    a.STUDENT_COUNT,
    a.LC_COUNT,
    a.LB_COUNT,
    a.PR_COUNT,
    a.LC_HOUR,
    a.LC_HOUR_TOTAL,
    a.LB_HOUR,
    a.LB_HOUR_TOTAL,
    a.PR_HOUR,
    a.PR_HOUR_TOTAL,
    a.TOTAL_HOUR,
    a.TOTAL_CREDIT
  FROM TEACHER_LOAD_CALC_DETAIL a INNER JOIN SUBJECT b ON a.SUBJECT_ID = b.ID
    INNER JOIN CREDITABILITY c ON b.CREDITABILITY_ID = c.ID
    INNER JOIN SEMESTER_PERIOD d ON a.SEMESTER_PERIOD_ID = d.ID
    INNER JOIN ACADEMIC_FORMULA e ON b.ACADEMIC_FORMULA_ID = e.ID;

create sequence s_teacher_load_calc
minvalue 0
start with 1
no cycle;

ALTER TABLE teacher_load_calc
  ALTER COLUMN accepted TYPE BOOLEAN
  USING CASE WHEN accepted = 0
  THEN FALSE
        WHEN accepted = 1
          THEN TRUE
        ELSE NULL
        END;

ALTER TABLE teacher_load_calc
  ALTER COLUMN deleted TYPE BOOLEAN
  USING CASE WHEN deleted = 0
  THEN FALSE
        WHEN deleted = 1
          THEN TRUE
        ELSE NULL
        END;

alter table teacher_load_assign_detail drop COLUMN LC_CREDIT;
alter table teacher_load_assign_detail drop COLUMN LB_CREDIT;
alter table teacher_load_assign_detail drop COLUMN PR_CREDIT;
alter table teacher_load_assign_detail drop COLUMN TOTAL_CREDIT;

alter table teacher_load_assign_detail drop COLUMN LC_HOUR_TOTAL;
alter table teacher_load_assign_detail drop COLUMN LB_HOUR_TOTAL;
alter table teacher_load_assign_detail drop COLUMN PR_HOUR_TOTAL;

alter table teacher_load_assign_detail drop COLUMN LC_COUNT;
alter table teacher_load_assign_detail drop COLUMN LB_COUNT;
alter table teacher_load_assign_detail drop COLUMN PR_COUNT;

CREATE OR REPLACE VIEW V_LOAD_TO_CHAIR AS
  SELECT DISTINCT
    floor(random() * (100 - 1 + 1) + 1) :: BIGINT id,
    subj.id                                       subject_id,
    curr.id                                       curriculum_id,
    curr_subj.subject_name,
    CASE WHEN curr_subj.semester_id IN (1, 2)
      THEN 1
    WHEN curr_subj.semester_id IN (3, 4)
      THEN 2
    WHEN curr_subj.semester_id IN (5, 6)
      THEN 3
    WHEN curr_subj.semester_id IN (7, 8)
      THEN 4 END                                  study_year,
    string_agg(gr.group_name, ', ')               stream,
    sem.semester_name                             semester_name,
    sum(gr.student_count)                         student_number,
    curr_subj.credit,
    subj.lc_count                                 lc_count,
    subj.pr_count * count(gr.student_count)       pr_count,
    subj.lb_count * count(gr.student_count)       lb_count,
    credit * 5                                    with_teacher_count,
    sum(gr.student_count) / 4                     rating_count,
    count(gr.student_count) * 2                   exam_count,
    CASE WHEN curr.student_diploma_type_id = 1
      THEN 0
    ELSE sum(gr.student_count) / 5 END            control_count,
    CASE WHEN subj.course_work = TRUE
      THEN sum(gr.student_count) / 4
    ELSE 0 END                                    course_work_count,
    CASE WHEN curr_subj.semester_id IN (7, 8)
      THEN 12 * sum(gr.student_count)
    ELSE 0 END                                    diploma_count,
    CASE WHEN subj.week_number IS NOT NULL
      THEN subj.week_number * sum(gr.student_count)
    ELSE 0 END                                    practice_count,
    CASE WHEN curr_subj.semester_id = 8
      THEN sum(gr.student_count) / 2
    ELSE 0 END                                    mek,
    CASE WHEN curr_subj.semester_id = 8
      THEN sum(gr.student_count) * 0.6
    ELSE 0 END                                    protect_diploma_count,
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
      ELSE 0 END                                  total_count
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
    floor(random() * (100 - 1 + 1) + 1) :: BIGINT id,
    subj.id                                       subject_id,
    curr.id                                       curriculum_id,
    curr_add_pr.subject_name_ru                   subject_name,
    CASE WHEN curr_add_pr.semester_id IN (1, 2)
      THEN 1
    WHEN curr_add_pr.semester_id IN (3, 4)
      THEN 2
    WHEN curr_add_pr.semester_id IN (5, 6)
      THEN 3
    WHEN curr_add_pr.semester_id IN (7, 8)
      THEN 4 END                                  study_year,
    string_agg(gr.group_name, ', ')               stream,
    sem.semester_name                             semester_name,
    sum(gr.student_count)                         student_number,
    curr_add_pr.credit,
    subj.lc_count                                 lc_count,
    subj.pr_count * count(gr.student_count)       pr_count,
    subj.lb_count * count(gr.student_count)       lb_count,
    credit * 5                                    with_teacher_count,
    sum(gr.student_count) / 4                     rating_count,
    count(gr.student_count) * 2                   exam_count,
    CASE WHEN curr.student_diploma_type_id = 1
      THEN 0
    ELSE sum(gr.student_count) / 5 END            control_count,
    CASE WHEN subj.course_work = TRUE
      THEN sum(gr.student_count) / 4
    ELSE 0 END                                    course_work_count,
    CASE WHEN curr_add_pr.semester_id IN (7, 8)
      THEN 12 * sum(gr.student_count)
    ELSE 0 END                                    diploma_count,
    CASE WHEN subj.week_number IS NOT NULL
      THEN subj.week_number * sum(gr.student_count)
    ELSE 0 END                                    practice_count,
    CASE WHEN curr_add_pr.semester_id = 8
      THEN sum(gr.student_count) / 2
    ELSE 0 END                                    mek,
    CASE WHEN curr_add_pr.semester_id = 8
      THEN sum(gr.student_count) * 0.6
    ELSE 0 END                                    protect_diploma_count,
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
      ELSE 0 END                                  total_count
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
    floor(random() * (100 - 1 + 1) + 1) :: BIGINT id,
    subj.id                                       subject_id,
    curr.id                                       curriculum_id,
    curr_after_sem.subject_name_ru                subject_name,
    CASE WHEN curr_after_sem.semester_id IN (1, 2)
      THEN 1
    WHEN curr_after_sem.semester_id IN (3, 4)
      THEN 2
    WHEN curr_after_sem.semester_id IN (5, 6)
      THEN 3
    WHEN curr_after_sem.semester_id IN (7, 8)
      THEN 4 END                                  study_year,
    string_agg(gr.group_name, ', ')               stream,
    sem.semester_name                             semester_name,
    sum(gr.student_count)                         student_number,
    curr_after_sem.credit,
    subj.lc_count                                 lc_count,
    subj.pr_count * count(gr.student_count)       pr_count,
    subj.lb_count * count(gr.student_count)       lb_count,
    credit * 5                                    with_teacher_count,
    sum(gr.student_count) / 4                     rating_count,
    count(gr.student_count) * 2                   exam_count,
    CASE WHEN curr.student_diploma_type_id = 1
      THEN 0
    ELSE sum(gr.student_count) / 5 END            control_count,
    CASE WHEN subj.course_work = TRUE
      THEN sum(gr.student_count) / 4
    ELSE 0 END                                    course_work_count,
    CASE WHEN curr_after_sem.semester_id IN (7, 8)
      THEN 12 * sum(gr.student_count)
    ELSE 0 END                                    diploma_count,
    CASE WHEN subj.week_number IS NOT NULL
      THEN subj.week_number * sum(gr.student_count)
    ELSE 0 END                                    practice_count,
    CASE WHEN curr_after_sem.semester_id = 8
      THEN sum(gr.student_count) / 2
    ELSE 0 END                                    mek,
    CASE WHEN curr_after_sem.semester_id = 8
      THEN sum(gr.student_count) * 0.6
    ELSE 0 END                                    protect_diploma_count,
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
      ELSE 0 END                                  total_count
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

ALTER TABLE teacher_load_assign
  ADD COLUMN chair_id BIGINT NOT NULL;
ALTER TABLE teacher_load_assign
  ADD COLUMN entrance_year_id BIGINT NOT NULL;

ALTER TABLE ONLY teacher_load_assign
  ADD CONSTRAINT fk_teacher_load_assign_department FOREIGN KEY (chair_id)
REFERENCES department (id)
ON UPDATE RESTRICT ON DELETE RESTRICT;
ALTER TABLE ONLY teacher_load_assign
  ADD CONSTRAINT fk_teacher_load_assign_entrance_year FOREIGN KEY (entrance_year_id)
REFERENCES entrance_year (id)
ON UPDATE RESTRICT ON DELETE RESTRICT;

create or replace view V_TEACHER_LOAD_ASSIGN_DETAIL as
  select a.ID,
    b.NAME_RU SUBJECT_NAME,
    c.CREDIT,
    e.FORMULA,
    a.SEMESTER_PERIOD_ID,
    d.PERIOD_NAME SEMESTER_PERIOD_NAME,
    a.STUDENT_COUNT,
    a.LC_HOUR,
    a.LB_HOUR,
    a.PR_HOUR,
    a.TOTAL_HOUR
  from TEACHER_LOAD_ASSIGN_DETAIL a inner join SUBJECT b on a.SUBJECT_ID = b.ID
    inner join CREDITABILITY c on b.CREDITABILITY_ID = c.ID
    inner join SEMESTER_PERIOD d on a.SEMESTER_PERIOD_ID = d.ID
    inner join ACADEMIC_FORMULA e on b.ACADEMIC_FORMULA_ID = e.ID;

create sequence s_teacher_load_assign
minvalue 0
start with 1
no cycle;

ALTER TABLE teacher_load_assign
  ALTER COLUMN accepted TYPE BOOLEAN
  USING CASE WHEN accepted = 0
  THEN FALSE
        WHEN accepted = 1
          THEN TRUE
        ELSE NULL
        END;

ALTER TABLE teacher_load_assign
  ALTER COLUMN deleted TYPE BOOLEAN
  USING CASE WHEN deleted = 0
  THEN FALSE
        WHEN deleted = 1
          THEN TRUE
        ELSE NULL
        END;

create sequence S_TEACHER_LOAD_ASSIGN_DETAIL
minvalue 0
start with 1
no cycle;

drop view v_load_to_chair_count;
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
FROM v_load_to_chair load INNER JOIN subject subj ON subj.id = load.subject_id
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

ALTER table teacher_load_assign_detail add COLUMN stream_id BIGINT NULL;
ALTER TABLE ONLY teacher_load_assign_detail
  ADD CONSTRAINT fk_teacher_load_assign_detail_stream FOREIGN KEY (stream_id)
REFERENCES department (id)
ON UPDATE RESTRICT ON DELETE RESTRICT;

ALTER table teacher_load_assign_detail add COLUMN group_id BIGINT NULL;
ALTER TABLE ONLY teacher_load_assign_detail
  ADD CONSTRAINT fk_teacher_load_assign_detail_group FOREIGN KEY (group_id)
REFERENCES groups (id)
ON UPDATE RESTRICT ON DELETE RESTRICT;

alter table teacher_load_assign_detail drop COLUMN student_count;
alter table teacher_load_assign_detail drop COLUMN pr_hour;
alter table teacher_load_assign_detail drop COLUMN total_hour;

ALTER TABLE teacher_load_assign_detail
  ADD COLUMN semester_id BIGINT NOT NULL;

ALTER TABLE teacher_load_assign_detail
  ADD COLUMN student_diploma_type_id BIGINT NOT NULL;
ALTER TABLE ONLY teacher_load_assign_detail
  ADD CONSTRAINT fk_teacher_load_assign_detail_student_diploma_type FOREIGN KEY (student_diploma_type_id)
REFERENCES student_diploma_type (id)
ON UPDATE RESTRICT ON DELETE RESTRICT;

DROP VIEW v_teacher_load_assign_detail;

create or replace view V_TEACHER_LOAD_ASSIGN_DETAIL as
SELECT
  load_assign_det.ID,
  subj.id                            subject_id,
  subj.NAME_RU                       SUBJECT_NAME,
  load_assign_det.stream_id,
  str.name                           stream_name,
  load_assign_det.semester_id,
  sem.semester_name,
  load_assign_det.SEMESTER_PERIOD_ID,
  sem_period.PERIOD_NAME             SEMESTER_PERIOD_NAME,
  sum(gr.student_count)              STUDENT_COUNT,
  cred.CREDIT,
  academ_form.FORMULA,
  load_assign_det.LC_HOUR,
  load_assign_det.LB_HOUR,
  load_assign_det.PR_HOUR,
  CASE WHEN pr_hour != 0
    THEN credit * 5
  ELSE 0 END                         with_teacher_hour,
  sum(gr.student_count) / 4          rating_hour,
  count(gr.student_count) * 2        exam_hour,
  CASE WHEN load_assign_det.student_diploma_type_id = 1
    THEN 0
  ELSE sum(gr.student_count) / 5 END control_hour,
  CASE WHEN subj.course_work = TRUE
    THEN sum(gr.student_count) / 4
  ELSE 0 END                         course_work_hour,
  CASE WHEN load_assign_det.semester_id IN (7, 8)
    THEN 12 * sum(gr.student_count)
  ELSE 0 END                         diploma_hour,
  CASE WHEN subj.week_number IS NOT NULL
    THEN subj.week_number * sum(gr.student_count)
  ELSE 0 END                         practice_hour,
  CASE WHEN load_assign_det.semester_id = 8
    THEN sum(gr.student_count) / 2
  ELSE 0 END                         mek,
  CASE WHEN load_assign_det.semester_id = 8
    THEN sum(gr.student_count) * 0.6
  ELSE 0 END                         protect_diploma_hour,
  LC_HOUR + PR_HOUR + LB_HOUR + with_teacher_count
  + sum(gr.student_count) / 4 + count(gr.student_count) * 2
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
    ELSE 0 END                       total_hour
FROM TEACHER_LOAD_ASSIGN_DETAIL load_assign_det
  INNER JOIN SUBJECT subj ON load_assign_det.SUBJECT_ID = subj.ID
  INNER JOIN CREDITABILITY cred ON subj.CREDITABILITY_ID = cred.ID
  INNER JOIN SEMESTER_PERIOD sem_period ON load_assign_det.SEMESTER_PERIOD_ID = sem_period.ID
  INNER JOIN ACADEMIC_FORMULA academ_form ON subj.ACADEMIC_FORMULA_ID = academ_form.ID
  INNER JOIN stream str ON str.id = load_assign_det.stream_id
  INNER JOIN semester sem ON sem.id = load_assign_det.semester_id
  INNER JOIN stream_group str_gr ON str_gr.stream_id = str.id
  INNER JOIN v_group gr ON gr.id = str_gr.group_id
GROUP BY load_assign_det.ID, subj.id, subj.NAME_RU, load_assign_det.stream_id, str.name,
  load_assign_det.semester_id, sem.semester_name, cred.CREDIT, academ_form.FORMULA,
  load_assign_det.SEMESTER_PERIOD_ID, sem_period.PERIOD_NAME, load_assign_det.LC_HOUR,
  load_assign_det.LB_HOUR, load_assign_det.PR_HOUR;