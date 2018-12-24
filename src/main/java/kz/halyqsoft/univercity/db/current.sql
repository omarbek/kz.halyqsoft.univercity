drop view v_load_to_chair_work;
drop view v_load_to_chair;
drop view v_curriculum_detail;
drop view v_elective_subject;

CREATE OR REPLACE VIEW V_CURRICULUM_DETAIL AS
  SELECT cur_det.ID,
         cur_det.CURRICULUM_ID,
         cur_det.SEMESTER_ID,
         cur_det.semester_data_id,
         cur_det.SUBJECT_ID,
         subj.CREDITABILITY_ID,
         subj.ects_id,
         subj.CONTROL_TYPE_ID,
         cur_det.education_module_type_id,
         subj.module_id,
         subj.subject_cycle_id,
         subj.trajectory_id,
      --end of ids
         sem.SEMESTER_NAME,
         subj_mod.module_short_name,
         subj_cycle.cycle_short_name,
         CASE
           WHEN cur_det.education_module_type_id IS NULL
                   THEN '-'
           ELSE edu_mod_type.type_name END education_module_type_name,
         cur_det.CODE                      SUBJECT_CODE,
         subj.NAME_kz                      SUBJECT_NAME_KZ,
         subj.name_ru                      subject_name_ru,
         cred.CREDIT,
         ects.ects                         ects_count,
         subj.lc_count,
         subj.pr_count,
         subj.lb_count,
         subj.with_teacher_count,
         subj.own_count,
         subj.total_count,
         contr_type.TYPE_NAME              CONTROL_TYPE_NAME,
         traj.name                         trajectory_name
  FROM CURRICULUM_DETAIL cur_det
         INNER JOIN SEMESTER sem ON cur_det.SEMESTER_ID = sem.ID
         INNER JOIN SUBJECT subj ON cur_det.SUBJECT_ID = subj.ID
         INNER JOIN subject_module subj_mod ON subj.module_id = subj_mod.id
         INNER JOIN SUBJECT_CYCLE subj_cycle ON subj.SUBJECT_CYCLE_ID = subj_cycle.ID
         INNER JOIN CREDITABILITY cred ON subj.CREDITABILITY_ID = cred.ID
         INNER JOIN ACADEMIC_FORMULA form ON subj.ACADEMIC_FORMULA_ID = form.ID
         INNER JOIN CONTROL_TYPE contr_type ON subj.CONTROL_TYPE_ID = contr_type.ID
         INNER JOIN ects ON subj.ects_id = ects.id
         LEFT JOIN education_module_type edu_mod_type ON edu_mod_type.id = cur_det.education_module_type_id
         left join trajectory traj on subj.trajectory_id = traj.id
  WHERE cur_det.deleted = FALSE
    AND subj.deleted = FALSE
  order by trajectory_name;

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
         subj.trajectory_id,
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
         elect_subj.consider_credit,
         traj.name                                       trajectory_name
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
         left join trajectory traj on subj.trajectory_id = traj.id
  WHERE elect_subj.deleted = FALSE
    AND subj.deleted = FALSE
  order by trajectory_name;

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

CREATE OR REPLACE VIEW V_LOAD_TO_CHAIR_WORK AS
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
                  str.group_count * 2                    exam_count,
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
                  subj.lc_count + subj.pr_count + subj.lb_count + credit * 5
                    + case
                        when curr.student_diploma_type_id not in (6, 7) then str.student_count / 4
                        else 0 end
                    + str.group_count * 2
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
         inner join speciality spec on spec.id = curr.speciality_id
         INNER JOIN semester sem ON sem.id = curr_det.semester_id
         INNER JOIN (SELECT DISTINCT str.id,
                                     str.subject_id,
                                     str.group_count,
                                     str.student_count,
                                     gr.speciality_id,
                                     gr.study_year_id
                     FROM v_stream str
                            INNER JOIN stream_group str_gr ON str.id = str_gr.stream_id
                            INNER JOIN v_group gr ON str_gr.group_id = gr.id
                            INNER JOIN speciality spec on spec.id = gr.speciality_id
                     where str.stream_type_id = 1) str ON str.speciality_id = curr.speciality_id
                                                            and str.subject_id = subj.id
  WHERE subj.deleted = FALSE
    AND curr.deleted = FALSE
    AND sem.study_year_id = str.study_year_id
    and subj.chair_id = spec.chair_id
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
                  str.group_count * 2                    exam_count,
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
                  subj.lc_count + subj.pr_count + subj.lb_count + credit * 5
                    + case
                        when curr.student_diploma_type_id not in (6, 7) then str.student_count / 4
                        else 0 end
                    + str.group_count * 2
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
         inner join speciality spec on spec.id = curr.speciality_id
         INNER JOIN semester sem ON sem.id = elect_subj.semester_id
         INNER JOIN (SELECT DISTINCT str.id,
                                     str.subject_id,
                                     str.group_count,
                                     str.student_count,
                                     gr.speciality_id,
                                     gr.study_year_id
                     FROM v_stream str
                            INNER JOIN stream_group str_gr ON str.id = str_gr.stream_id
                            INNER JOIN v_group gr ON str_gr.group_id = gr.id
                            INNER JOIN speciality spec on spec.id = gr.speciality_id
                     where str.stream_type_id = 1) str ON str.speciality_id = curr.speciality_id
                                                            and str.subject_id = subj.id
  WHERE subj.deleted = FALSE
    AND curr.deleted = FALSE
    AND sem.study_year_id = str.study_year_id
    and subj.chair_id = spec.chair_id
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
                  str.group_count * 2                    exam_count,
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
                  subj.lc_count + subj.pr_count + subj.lb_count + credit * 5
                    + case
                        when curr.student_diploma_type_id not in (6, 7) then str.student_count / 4
                        else 0 end
                    + str.group_count * 2
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
         inner join speciality spec on spec.id = curr.speciality_id
         INNER JOIN semester sem ON sem.id = curr_add_pr.semester_id
         INNER JOIN (SELECT DISTINCT str.id,
                                     str.subject_id,
                                     str.group_count,
                                     str.student_count,
                                     gr.speciality_id,
                                     gr.study_year_id
                     FROM v_stream str
                            INNER JOIN stream_group str_gr ON str.id = str_gr.stream_id
                            INNER JOIN v_group gr ON str_gr.group_id = gr.id
                            INNER JOIN speciality spec on spec.id = gr.speciality_id
                     where str.stream_type_id = 1) str ON str.speciality_id = curr.speciality_id
                                                            and str.subject_id = subj.id
  WHERE subj.deleted = FALSE
    AND curr.deleted = FALSE
    AND sem.study_year_id = str.study_year_id
    and subj.chair_id = spec.chair_id
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
         inner join speciality spec on spec.id = curr.speciality_id
         LEFT JOIN semester sem ON sem.id = curr_after_sem.semester_id
         INNER JOIN v_group gr ON gr.speciality_id = curr.speciality_id
         INNER JOIN study_year year ON year.id = gr.study_year_id
  WHERE subj.deleted = FALSE
    AND curr.deleted = FALSE
    AND sem.study_year_id = gr.study_year_id
    and spec.chair_id = subj.chair_id;

ALTER TABLE schedule_detail DROP COLUMN stream_id;

UPDATE degree
SET degree_name = 'кандидат наук'
WHERE id = 1;
UPDATE degree
SET degree_name = 'доктор наук'
WHERE id = 2;
UPDATE degree
SET degree_name = 'доктор Ph.D'
WHERE id = 3;
UPDATE degree
SET degree_name = 'ассоциированный профессор (доцент)'
WHERE id = 4;

INSERT INTO DEGREE (DEGREE_NAME, ID)
VALUES ('профессор', 7);
INSERT INTO DEGREE (DEGREE_NAME, ID)
VALUES ('доцент', 8);
INSERT INTO DEGREE (DEGREE_NAME, ID)
VALUES ('профессоров (по новой квалификации)', 9);