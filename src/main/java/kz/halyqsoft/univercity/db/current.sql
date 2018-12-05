INSERT INTO SUBJECT (CLASS_ROOM, COURSE_WORK, DELETED, LANG_EN, LANG_KZ, LANG_RU, LB_COUNT, LC_COUNT,
                     MANDATORY, NAME_EN, NAME_KZ, NAME_RU, OWN_COUNT, PR_COUNT, TOTAL_COUNT, WEEK_NUMBER,
                     WITH_TEACHER_COUNT, ID, ACADEMIC_FORMULA_ID, CHAIR_ID, CONTROL_TYPE_ID,
                     CREDITABILITY_ID, ECTS_ID, LEVEL_ID, PRACTICE_BREAKDOWN_ID, PRACTICE_TYPE_ID,
                     STUDY_DIRECT_ID, SUBJECT_CYCLE_ID, MODULE_ID, TRAJECTORY_ID)
VALUES (NULL, FALSE, FALSE, FALSE, FALSE, FALSE, NULL, null, FALSE, 'Диплом жұмысына жетекшілік',
        'Диплом жұмысына жетекшілік', 'Диплом жұмысына жетекшілік', 0, NULL,
        0, NULL, NULL, nextval('s_subject'), NULL, NULL, 1,
        1, 1, 1, NULL, NULL, null, NULL, 1, NULL);

drop view v_load_to_chair_count_all;
drop view v_load_to_chair_count;
CREATE OR REPLACE VIEW v_load_to_chair_count AS
       SELECT floor(random() * (1000) + 1) :: BIGINT id,
              curriculum_id,
              load.semester_id,
              study_year,
              sum(load.lc_count)                     lc_count,
              sum(load.pr_count)                     pr_count,
              sum(load.lb_count)                     lb_count,
              sum(load.with_teacher_count)           with_teacher_count,
              sum(rating_count)                      rating_count,
              sum(exam_count)                        exam_count,
              sum(control_count)                     control_count,
              sum(course_work_count)                 course_work_count,
              sum(diploma_count)                     diploma_count,
              sum(practice_count)                    practice_count,
              sum(mek)                               mek,
              sum(protect_diploma_count)             protect_diploma_count,
              sum(load.total_count)                  total_count
       FROM load_to_chair load
                   INNER JOIN subject subj ON subj.id = load.subject_id
                   INNER JOIN study_year year on year.id = load.study_year_id
       GROUP BY study_year, load.semester_id, curriculum_id;

CREATE OR REPLACE VIEW v_load_to_chair_count_all AS
       SELECT floor(random() * (1000) + 1) :: BIGINT id,
              curriculum_id,
              semester_id,
              sum(lc_count)                          lc_count,
              sum(pr_count)                          pr_count,
              sum(lb_count)                          lb_count,
              sum(with_teacher_count)                with_teacher_count,
              sum(rating_count)                      rating_count,
              sum(exam_count)                        exam_count,
              sum(control_count)                     control_count,
              sum(course_work_count)                 course_work_count,
              sum(diploma_count)                     diploma_count,
              sum(practice_count)                    practice_count,
              sum(mek)                               mek,
              sum(protect_diploma_count)             protect_diploma_count,
              sum(total_count)                       total_count
       FROM v_load_to_chair_count
       GROUP BY semester_id, curriculum_id;