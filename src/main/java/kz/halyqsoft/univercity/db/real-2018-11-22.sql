CREATE OR REPLACE VIEW V_LOAD_TO_CHAIR AS
  SELECT DISTINCT floor(random() * (1000) + 1) :: BIGINT id,
                  subj.id                                subject_id,
                  curr.id                                curriculum_id,
                  sem.study_year_id,
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
                    when curr.student_diploma_type_id in (6, 7) then str.student_count / 4
                    else 0 end                           rating_count,
                  str.group_count * 2                    exam_count,
                  CASE
                    WHEN curr.student_diploma_type_id not in (6, 7)
                            THEN 0
                    ELSE str.student_count / 5 END       control_count,
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
                        when curr.student_diploma_type_id in (6, 7) then str.student_count / 4
                        else 0 end
                    + str.group_count * 2
                    + CASE
                        WHEN curr.student_diploma_type_id not in (6, 7)
                                THEN 0
                        ELSE str.student_count / 5 END
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
                            INNER JOIN v_group gr ON str_gr.group_id = gr.id) str
           ON str.speciality_id = curr.speciality_id
  WHERE subj.deleted = FALSE
    AND curr.deleted = FALSE
    AND sem.study_year_id = str.study_year_id
  UNION ALL
  SELECT DISTINCT floor(random() * (1000) + 1) :: BIGINT id,
                  subj.id                                subject_id,
                  curr.id                                curriculum_id,
                  sem.study_year_id,
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
                    when curr.student_diploma_type_id in (6, 7) then str.student_count / 4
                    else 0 end                           rating_count,
                  str.group_count * 2                    exam_count,
                  CASE
                    WHEN curr.student_diploma_type_id not in (6, 7)
                            THEN 0
                    ELSE str.student_count / 5 END       control_count,
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
                        when curr.student_diploma_type_id in (6, 7) then str.student_count / 4
                        else 0 end
                    + str.group_count * 2
                    + CASE
                        WHEN curr.student_diploma_type_id not in (6, 7)
                                THEN 0
                        ELSE str.student_count / 5 END
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
                            INNER JOIN v_group gr ON str_gr.group_id = gr.id) str
           ON str.speciality_id = curr.speciality_id
  WHERE subj.deleted = FALSE
    AND curr.deleted = FALSE
    AND sem.study_year_id = str.study_year_id
  UNION ALL
  SELECT DISTINCT floor(random() * (1000) + 1) :: BIGINT id,
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
                  case
                    when curr.student_diploma_type_id in (6, 7) then str.student_count / 4
                    else 0 end                           rating_count,
                  str.group_count * 2                    exam_count,
                  CASE
                    WHEN curr.student_diploma_type_id not in (6, 7)
                            THEN 0
                    ELSE str.student_count / 5 END       control_count,
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
                        when curr.student_diploma_type_id in (6, 7) then str.student_count / 4
                        else 0 end
                    + str.group_count * 2
                    + CASE
                        WHEN curr.student_diploma_type_id not in (6, 7)
                                THEN 0
                        ELSE str.student_count / 5 END
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
                            INNER JOIN v_group gr ON str_gr.group_id = gr.id) str
           ON str.speciality_id = curr.speciality_id
  WHERE subj.deleted = FALSE
    AND curr.deleted = FALSE
    AND sem.study_year_id = str.study_year_id
  UNION ALL
  SELECT DISTINCT floor(random() * (1000) + 1) :: BIGINT id,
                  subj.id                                subject_id,
                  curr.id                                curriculum_id,
                  gr.study_year_id,
                  0                                      stream_id,
                  gr.id                                  group_id,
                  0                                      semester_id,
                  gr.student_count                       student_number,
                  curr_after_sem.credit,
                  subj.lc_count                          lc_count,
                  subj.pr_count * 1                      pr_count,
                  subj.lb_count * 1                      lb_count,
                  credit * 5                             with_teacher_count,
                  case
                    when curr.student_diploma_type_id in (6, 7) then gr.student_count / 4
                    else 0 end                           rating_count,
                  1 * 2                                  exam_count,
                  CASE
                    WHEN curr.student_diploma_type_id not in (6, 7)
                            THEN 0
                    ELSE gr.student_count / 5 END        control_count,
                  CASE
                    WHEN subj.course_work = TRUE
                            THEN gr.student_count / 4
                    ELSE 0 END                           course_work_count,
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
                  subj.lc_count + subj.pr_count + subj.lb_count + credit * 5
                    + case
                        when curr.student_diploma_type_id in (6, 7) then gr.student_count / 4
                        else 0 end
                    + 1 * 2
                    + CASE
                        WHEN curr.student_diploma_type_id not in (6, 7)
                                THEN 0
                        ELSE gr.student_count / 5 END
                    + CASE
                        WHEN subj.course_work = TRUE
                                THEN gr.student_count / 4
                        ELSE 0 END
                    + CASE
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
         INNER JOIN v_group gr ON gr.speciality_id = curr.speciality_id
         INNER JOIN study_year year ON year.id = gr.study_year_id
  WHERE subj.deleted = FALSE
    AND curr.deleted = FALSE;