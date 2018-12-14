CREATE OR REPLACE VIEW V_LOAD_TO_CHAIR AS
  SELECT DISTINCT floor(random() * (1000) + 1) :: BIGINT id,
                  subj.id                                subject_id,
                  curr.id                                curriculum_id,
                  cast(null as bigint)                   study_year_id,
                  str.id                                 stream_id,
                  0                                      group_id,
                  sem.id                                 semester_id,
                  str.student_count                      student_number,
                  curr_det.credit,
                  subj.lc_count                          lc_count,
                  subj.pr_count * str.group_count        pr_count,
                  subj.lb_count * str.group_count        lb_count,
                  credit * 5                             with_teacher_count,
                  case
                    when curr.student_diploma_type_id not in (6, 7) then str.student_count / 4
                    else 0 end                           rating_count,
                  case
                    when subj.ticket then str.student_count / 4
                    else str.group_count * 2 end         exam_count,
                  CASE
                    WHEN curr.student_diploma_type_id in (6, 7)
                            THEN str.student_count / 5
                    ELSE 0 END                           control_count,
                  CASE
                    WHEN subj.course_work = TRUE
                            THEN str.student_count / 4
                    ELSE 0 END                           course_work_count,
                  0                                      diploma_count,
                  0                                      practice_count,
                  0                                      mek,
                  0                                      protect_diploma_count,
                  subj.lc_count
                    + subj.pr_count * str.group_count
                    + subj.lb_count * str.group_count
                    + credit * 5
                    + case
                        when curr.student_diploma_type_id not in (6, 7) then str.student_count / 4
                        else 0 end
                    + case
                        when subj.ticket then str.student_count / 4
                        else str.group_count * 2 end
                    + CASE
                        WHEN curr.student_diploma_type_id in (6, 7)
                                THEN str.student_count / 5
                        ELSE 0 END
                    + CASE
                        WHEN subj.course_work = TRUE
                                THEN str.student_count / 4
                        ELSE 0 END                       total_count
  FROM v_curriculum_detail curr_det
         INNER JOIN subject subj ON subj.id = curr_det.subject_id
         INNER JOIN curriculum curr ON curr_det.curriculum_id = curr.id
         INNER JOIN semester sem ON sem.id = curr_det.semester_id
         INNER JOIN (SELECT DISTINCT str.id, str.group_count, str.student_count, gr.speciality_id, gr.study_year_id
                     FROM v_stream str
                            INNER JOIN stream_group str_gr ON str.id = str_gr.stream_id
                            INNER JOIN v_group gr ON str_gr.group_id = gr.id
                            INNER JOIN speciality spec on spec.id = gr.speciality_id
                     where str.stream_type_id = 2) str ON str.speciality_id = curr.speciality_id
  WHERE subj.deleted = FALSE
    AND curr.deleted = FALSE
    AND sem.study_year_id = str.study_year_id
  UNION ALL
  SELECT DISTINCT floor(random() * (1000) + 1) :: BIGINT id,
                  subj.id                                subject_id,
                  curr.id                                curriculum_id,
                  cast(null as bigint)                   study_year_id,
                  str.id                                 stream_id,
                  0                                      group_id,
                  sem.id                                 semester_id,
                  str.student_count                      student_number,
                  elect_subj.credit,
                  subj.lc_count                          lc_count,
                  subj.pr_count * str.group_count        pr_count,
                  subj.lb_count * str.group_count        lb_count,
                  credit * 5                             with_teacher_count,
                  case
                    when curr.student_diploma_type_id not in (6, 7) then str.student_count / 4
                    else 0 end                           rating_count,
                  case
                    when subj.ticket then str.student_count / 4
                    else str.group_count * 2 end         exam_count,
                  CASE
                    WHEN curr.student_diploma_type_id in (6, 7)
                            THEN str.student_count / 5
                    ELSE 0 END                           control_count,
                  CASE
                    WHEN subj.course_work = TRUE
                            THEN str.student_count / 4
                    ELSE 0 END                           course_work_count,
                  0                                      diploma_count,
                  0                                      practice_count,
                  0                                      mek,
                  0                                      protect_diploma_count,
                  subj.lc_count
                    + subj.pr_count * str.group_count
                    + subj.lb_count * str.group_count
                    + credit * 5
                    + case
                        when curr.student_diploma_type_id not in (6, 7) then str.student_count / 4
                        else 0 end
                    + case
                        when subj.ticket then str.student_count / 4
                        else str.group_count * 2 end
                    + CASE
                        WHEN curr.student_diploma_type_id in (6, 7)
                                THEN str.student_count / 5
                        ELSE 0 END
                    + CASE
                        WHEN subj.course_work = TRUE
                                THEN str.student_count / 4
                        ELSE 0 END                       total_count
  FROM v_elective_subject elect_subj
         INNER JOIN subject subj ON subj.id = elect_subj.subject_id
         INNER JOIN curriculum curr ON elect_subj.curriculum_id = curr.id
         INNER JOIN semester sem ON sem.id = elect_subj.semester_id
         INNER JOIN (SELECT DISTINCT str.id, str.group_count, str.student_count, gr.speciality_id, gr.study_year_id
                     FROM v_stream str
                            INNER JOIN stream_group str_gr ON str.id = str_gr.stream_id
                            INNER JOIN v_group gr ON str_gr.group_id = gr.id
                            INNER JOIN speciality spec on spec.id = gr.speciality_id
                     where str.stream_type_id = 2) str ON str.speciality_id = curr.speciality_id
  WHERE subj.deleted = FALSE
    AND curr.deleted = FALSE
    AND sem.study_year_id = str.study_year_id
  UNION ALL
  SELECT DISTINCT floor(random() * (1000) + 1) :: BIGINT id,
                  subj.id                                subject_id,
                  curr.id                                curriculum_id,
                  cast(null as bigint)                   study_year_id,
                  str.id                                 stream_id,
                  0                                      group_id,
                  sem.id                                 semester_id,
                  str.student_count                      student_number,
                  curr_add_pr.credit,
                  subj.lc_count                          lc_count,
                  subj.pr_count * str.group_count        pr_count,
                  subj.lb_count * str.group_count        lb_count,
                  credit * 5                             with_teacher_count,
                  case
                    when curr.student_diploma_type_id not in (6, 7) then str.student_count / 4
                    else 0 end                           rating_count,
                  case
                    when subj.ticket then str.student_count / 4
                    else str.group_count * 2 end         exam_count,
                  CASE
                    WHEN curr.student_diploma_type_id in (6, 7)
                            THEN str.student_count / 5
                    ELSE 0 END                           control_count,
                  CASE
                    WHEN subj.course_work = TRUE
                            THEN str.student_count / 4
                    ELSE 0 END                           course_work_count,
                  0                                      diploma_count,
                  0                                      practice_count,
                  0                                      mek,
                  0                                      protect_diploma_count,
                  subj.lc_count
                    + subj.pr_count * str.group_count
                    + subj.lb_count * str.group_count
                    + credit * 5
                    + case
                        when curr.student_diploma_type_id not in (6, 7) then str.student_count / 4
                        else 0 end
                    + case
                        when subj.ticket then str.student_count / 4
                        else str.group_count * 2 end
                    + CASE
                        WHEN curr.student_diploma_type_id in (6, 7)
                                THEN str.student_count / 5
                        ELSE 0 END
                    + CASE
                        WHEN subj.course_work = TRUE
                                THEN str.student_count / 4
                        ELSE 0 END                       total_count
  FROM v_curriculum_add_program curr_add_pr
         INNER JOIN subject subj ON subj.id = curr_add_pr.subject_id
         INNER JOIN curriculum curr ON curr_add_pr.curriculum_id = curr.id
         INNER JOIN semester sem ON sem.id = curr_add_pr.semester_id
         INNER JOIN study_year year ON year.id = sem.study_year_id
         INNER JOIN (SELECT DISTINCT str.id, str.group_count, str.student_count, gr.speciality_id, gr.study_year_id
                     FROM v_stream str
                            INNER JOIN stream_group str_gr ON str.id = str_gr.stream_id
                            INNER JOIN v_group gr ON str_gr.group_id = gr.id
                            INNER JOIN speciality spec on spec.id = gr.speciality_id
                     where str.stream_type_id = 2) str ON str.speciality_id = curr.speciality_id
  WHERE subj.deleted = FALSE
    AND curr.deleted = FALSE
    AND sem.study_year_id = str.study_year_id
  UNION ALL
  SELECT DISTINCT floor(random() * (1000) + 1) :: BIGINT id,
                  subj.id                                subject_id,
                  curr.id                                curriculum_id,
                  cast(null as bigint)                   study_year_id,
                  0                                      stream_id,
                  gr.id                                  group_id,
                  sem.id                                 semester_id,
                  gr.student_count                       student_number,
                  curr_after_sem.credit,
                  cast(0 as numeric(3, 0))               lc_count,
                  0                                      pr_count,
                  0                                      lb_count,
                  0                                      with_teacher_count,
                  0                                      rating_count,
                  0                                      exam_count,
                  0                                      control_count,
                  0                                      course_work_count,
                  CASE
                    WHEN subj.id = 1388
                            THEN 12 * gr.student_count
                    ELSE 0 END                           diploma_count,
                  CASE
                    WHEN subj.practice_type_id IS NOT NULL
                            THEN subj.week_number * gr.student_count
                    ELSE 0 END                           practice_count,
                  CASE
                    WHEN subj.id = 1387
                            THEN gr.student_count / 2
                    ELSE 0 END                           mek,
                  CASE
                    WHEN subj.id = 1388
                            THEN gr.student_count * 0.6
                    ELSE 0 END                           protect_diploma_count,
                  CASE
                    WHEN subj.id = 1388
                            THEN 12 * gr.student_count
                    ELSE 0 END
                    + CASE
                        WHEN subj.practice_type_id IS NOT NULL
                                THEN subj.week_number * gr.student_count
                        ELSE 0 END
                    + CASE
                        WHEN subj.id = 1387
                                THEN gr.student_count / 2
                        ELSE 0 END
                    + CASE
                        WHEN subj.id = 1388
                                THEN gr.student_count * 0.6
                        ELSE 0 END                       total_count
  FROM v_curriculum_after_semester curr_after_sem
         INNER JOIN subject subj ON subj.id = curr_after_sem.subject_id
         INNER JOIN curriculum curr ON curr_after_sem.curriculum_id = curr.id
         LEFT JOIN semester sem ON sem.id = curr_after_sem.semester_id
         INNER JOIN v_group gr ON gr.speciality_id = curr.speciality_id
         INNER JOIN study_year year ON year.id = gr.study_year_id
  WHERE subj.deleted = FALSE
    AND curr.deleted = FALSE
    AND sem.study_year_id = gr.study_year_id;

update groups
set deleted = true
where id not in (select id from v_group);

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

update lesson_time set lesson_number=5 where id=11;

ALTER TABLE schedule_detail ADD COLUMN stream_id int;

ALTER TABLE ONLY schedule_detail
  ADD CONSTRAINT fk_t_schedule_detail_stream FOREIGN KEY (stream_id)
REFERENCES stream (id)
ON DELETE RESTRICT ON UPDATE RESTRICT;

CREATE OR REPLACE VIEW V_TEACHER_SUBJECT_MODULE AS
  SELECT
         teacher_subject.ID,
         teacher_subject.EMPLOYEE_ID,
         subj.NAME_RU SUBJECT_NAME,
         credit.credit,
         subjm.module_name,
         teacher_subject.FALL,
         teacher_subject.SPRING,
         teacher_subject.SUMMER,
         teacher_subject.LOAD_PER_HOURS
  FROM TEACHER_SUBJECT teacher_subject
         INNER JOIN SUBJECT subj ON teacher_subject.SUBJECT_ID = subj.ID
         INNER JOIN creditability credit ON subj.creditability_id = credit.id
         INNER JOIN subject_module subjm ON subj.module_id = subjm.id;

delete from lesson_detail;
delete from lesson;

update shift_study_year set shift_id=1;
insert into shift_study_year values (nextval('s_shift_study_year'),2,2);
insert into shift_study_year values (nextval('s_shift_study_year'),3,2);
insert into shift_study_year values (nextval('s_shift_study_year'),4,1);

UPDATE LESSON_TIME
SET BEGIN_TIME_ID = 18,
    END_TIME_ID   = 20
WHERE (ID = 7);
UPDATE LESSON_TIME
SET BEGIN_TIME_ID = 21,
    END_TIME_ID   = 23
WHERE (ID = 4);
UPDATE LESSON_TIME
SET BEGIN_TIME_ID = 6,
    END_TIME_ID   = 28
WHERE (ID = 11);
UPDATE LESSON_TIME
SET BEGIN_TIME_ID = 31,
    END_TIME_ID   = 9
WHERE (ID = 7);

drop view V_TEACHER_SUBJECT_MODULE;
CREATE OR REPLACE VIEW V_TEACHER_SUBJECT_MODULE AS
  SELECT teacher_subject.ID,
         teacher_subject.EMPLOYEE_ID,
         subj.NAME_RU SUBJECT_NAME_RU,
         subj.name_kz SUBJECT_NAME_KZ,
         credit.credit,
         subjm.module_name,
         teacher_subject.FALL,
         teacher_subject.SPRING,
         teacher_subject.SUMMER,
         teacher_subject.LOAD_PER_HOURS
  FROM TEACHER_SUBJECT teacher_subject
         INNER JOIN SUBJECT subj ON teacher_subject.SUBJECT_ID = subj.ID
         INNER JOIN creditability credit ON subj.creditability_id = credit.id
         INNER JOIN subject_module subjm ON subj.module_id = subjm.id;

CREATE OR REPLACE VIEW v_elective_subject AS
  SELECT elect_subj.ID,
         elect_subj.CURRICULUM_ID,
         elect_subj.SEMESTER_ID,
         elect_subj.semester_data_id,
         elect_subj.SUBJECT_ID,
         subj.CREDITABILITY_ID,
         subj.ects_id,
         subj.CONTROL_TYPE_ID,
         elect_subj.education_module_type_id,
         subj.module_id,
         subj.subject_cycle_id,
      --end of ids
         sem.SEMESTER_NAME,
         subj_mod.module_short_name,
         subj_cycle.cycle_short_name,
         CASE
           WHEN elect_subj.education_module_type_id IS NULL
                   THEN '-'
           ELSE edu_mod_type.type_name END               education_module_type_name,
         elect_subj.CODE                                 SUBJECT_CODE,
         subj.NAME_kz                                    SUBJECT_NAME_KZ,
         subj.name_ru                                    subject_name_ru,
         case
           when consider_credit then cred.CREDIT
           else cast(0 as numeric(2, 0)) end             credit,
         case
           when consider_credit then ects.ects
           else cast(0 as numeric(2, 0)) end             ects_count,
         case
           when consider_credit then subj.lc_count
           else cast(0 as numeric(3, 0)) end             lc_count,
         case
           when consider_credit then subj.pr_count
           else cast(0 as numeric(3, 0)) end             pr_count,
         case
           when consider_credit then subj.lb_count
           else cast(0 as numeric(3, 0)) end             lb_count,
         case
           when consider_credit then subj.with_teacher_count
           else cast(0 as numeric(3, 0)) end             with_teacher_count,
         case
           when consider_credit then subj.own_count
           else cast(0 as numeric(3, 0)) end             own_count,
         case
           when consider_credit then subj.total_count
           else cast(0 as numeric(3, 0)) end             total_count,
         case
           when consider_credit then contr_type.TYPE_NAME
           else cast(null as character varying(128)) end CONTROL_TYPE_NAME,
         elect_subj.consider_credit
  FROM elective_subject elect_subj
         INNER JOIN SEMESTER sem ON elect_subj.SEMESTER_ID = sem.ID
         INNER JOIN SUBJECT subj ON elect_subj.SUBJECT_ID = subj.ID
         INNER JOIN subject_module subj_mod ON subj.module_id = subj_mod.id
         INNER JOIN SUBJECT_CYCLE subj_cycle ON subj.SUBJECT_CYCLE_ID = subj_cycle.ID
         INNER JOIN CREDITABILITY cred ON subj.CREDITABILITY_ID = cred.ID
         INNER JOIN ACADEMIC_FORMULA form ON subj.ACADEMIC_FORMULA_ID = form.ID
         INNER JOIN CONTROL_TYPE contr_type ON subj.CONTROL_TYPE_ID = contr_type.ID
         INNER JOIN ects ON subj.ects_id = ects.id
         LEFT JOIN education_module_type edu_mod_type ON edu_mod_type.id = elect_subj.education_module_type_id
  WHERE elect_subj.deleted = FALSE
    AND subj.deleted = FALSE;