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