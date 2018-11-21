INSERT INTO SUBJECT (CLASS_ROOM, COURSE_WORK, DELETED, LANG_EN, LANG_KZ, LANG_RU, LB_COUNT, LC_COUNT,
                     MANDATORY, NAME_EN, NAME_KZ, NAME_RU, OWN_COUNT, PR_COUNT, TOTAL_COUNT, WEEK_NUMBER,
                     WITH_TEACHER_COUNT, ID, ACADEMIC_FORMULA_ID, CHAIR_ID, CONTROL_TYPE_ID,
                     CREDITABILITY_ID, ECTS_ID, LEVEL_ID, PRACTICE_BREAKDOWN_ID, PRACTICE_TYPE_ID,
                     STUDY_DIRECT_ID, SUBJECT_CYCLE_ID, MODULE_ID, TRAJECTORY_ID)
VALUES (NULL, FALSE, FALSE, FALSE, FALSE, FALSE, NULL, 15, FALSE, 'Государственный экзамен по специальности',
              'Мамандық бойынша мемлекеттік емтихан', 'Государственный экзамен по специальности', 90, NULL,
                                                      105, NULL, NULL, nextval('s_subject'), NULL, NULL, 2,
        1, 3, 1, NULL, NULL, 7, NULL, 2, NULL);
INSERT INTO SUBJECT (CLASS_ROOM, COURSE_WORK, DELETED, LANG_EN, LANG_KZ, LANG_RU, LB_COUNT, LC_COUNT,
                     MANDATORY, NAME_EN, NAME_KZ, NAME_RU, OWN_COUNT, PR_COUNT, TOTAL_COUNT, WEEK_NUMBER,
                     WITH_TEACHER_COUNT, ID, ACADEMIC_FORMULA_ID, CHAIR_ID, CONTROL_TYPE_ID,
                     CREDITABILITY_ID, ECTS_ID, LEVEL_ID, PRACTICE_BREAKDOWN_ID, PRACTICE_TYPE_ID,
                     STUDY_DIRECT_ID, SUBJECT_CYCLE_ID, MODULE_ID, TRAJECTORY_ID)
VALUES (NULL, FALSE, FALSE, FALSE, FALSE, FALSE, NULL, 30, FALSE,
              'Написание и защита дипломной работы (проекта) или сдача государственных экзаменов по двум профилирующим дисциплинам',
              'Дипломдық жұмысты (жобаны) жазу және қорғау немесе екі бейінлеуші пән бойынша мемлекеттік емтихан тапсыру',
  'Написание и защита дипломной работы (проекта) или сдача государственных экзаменов по двум профилирующим дисциплинам',
  180, NULL, 210, NULL, NULL, nextval('s_subject'), NULL, NULL, 2, 2, 6, 1, NULL, NULL, 7, NULL, 2, NULL);

drop view v_curriculum_after_semester;

ALTER TABLE curriculum_after_semester
    drop COLUMN semester_id;

ALTER TABLE curriculum_after_semester
  ALTER COLUMN code DROP NOT NULL;
ALTER TABLE curriculum_after_semester
  ALTER COLUMN education_module_type_id DROP NOT NULL;

CREATE OR REPLACE VIEW v_curriculum_after_semester AS
  SELECT
    curr_after_sem.ID,
    curr_after_sem.CURRICULUM_ID,
    curr_after_sem.SUBJECT_ID,
    subj.CREDITABILITY_ID,
    subj.ects_id,
    subj.CONTROL_TYPE_ID,
    curr_after_sem.education_module_type_id,
    subj.module_id,
    --end of ids
    subj_mod.module_short_name,
    CASE WHEN curr_after_sem.education_module_type_id IS NULL
      THEN '-'
    ELSE edu_mod_type.type_name END education_module_type_name,
    curr_after_sem.CODE             SUBJECT_CODE,
    subj.NAME_kz                    SUBJECT_NAME_KZ,
    subj.name_ru                    subject_name_ru,
    cred.CREDIT,
    ects.ects                       ects_count,
    subj.lc_count,
    subj.week_number,
    subj.lb_count,
    subj.with_teacher_count,
    subj.own_count,
    subj.total_count,
    contr_type.TYPE_NAME            CONTROL_TYPE_NAME
  FROM curriculum_after_semester curr_after_sem
    INNER JOIN SUBJECT subj ON curr_after_sem.SUBJECT_ID = subj.ID
    INNER JOIN subject_module subj_mod ON subj.module_id = subj_mod.id
    INNER JOIN CREDITABILITY cred ON subj.CREDITABILITY_ID = cred.ID
    INNER JOIN CONTROL_TYPE contr_type ON subj.CONTROL_TYPE_ID = contr_type.ID
    INNER JOIN ects ON subj.ects_id = ects.id
    LEFT JOIN education_module_type edu_mod_type
      ON edu_mod_type.id = curr_after_sem.education_module_type_id
  WHERE curr_after_sem.deleted = FALSE AND subj.deleted = FALSE;