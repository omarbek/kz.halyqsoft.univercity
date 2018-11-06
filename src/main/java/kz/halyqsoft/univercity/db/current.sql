DROP VIEW v_curriculum_detail;
DROP VIEW v_curriculum_subject;
DROP VIEW v_load_to_chair;

ALTER TABLE curriculum_detail
  DROP COLUMN subject_cycle_id;
ALTER TABLE curriculum_detail
  DROP COLUMN recommended_semester;
ALTER TABLE curriculum_detail
  DROP COLUMN consider_credit;

CREATE OR REPLACE VIEW V_CURRICULUM_DETAIL AS
  SELECT
    cur_det.ID,
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
    --end of ids
    sem.SEMESTER_NAME,
    subj_mod.module_short_name,
    subj_cycle.cycle_short_name,
    CASE WHEN cur_det.education_module_type_id IS NULL
      THEN '-'
    ELSE edu_mod_type.type_name END education_module_type_name,
    cur_det.CODE                    SUBJECT_CODE,
    subj.NAME_kz                    SUBJECT_NAME_KZ,
    subj.name_ru                    subject_name_ru,
    cred.CREDIT,
    ects.ects                       ects_count,
    subj.lc_count,
    subj.pr_count,
    subj.lb_count,
    subj.with_teacher_count,
    subj.own_count,
    subj.total_count,
    contr_type.TYPE_NAME            CONTROL_TYPE_NAME
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
  WHERE cur_det.deleted = FALSE AND subj.deleted = FALSE;

UPDATE tasks
SET class_path = 'kz.halyqsoft.univercity.modules.curriculum.working.main.CurriculumView'
WHERE id = 30;

ALTER TABLE curriculum_detail
  ALTER COLUMN code DROP NOT NULL;
ALTER TABLE curriculum_detail
  ALTER COLUMN education_module_type_id DROP NOT NULL;

drop view v_elective_subject;

ALTER TABLE elective_subject
  DROP COLUMN subject_cycle_id;

CREATE OR REPLACE VIEW v_elective_subject AS
  SELECT
    elect_subj.ID,
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
    CASE WHEN elect_subj.education_module_type_id IS NULL
      THEN '-'
    ELSE edu_mod_type.type_name END education_module_type_name,
    elect_subj.CODE                 SUBJECT_CODE,
    subj.NAME_kz                    SUBJECT_NAME_KZ,
    subj.name_ru                    subject_name_ru,
    cred.CREDIT,
    ects.ects                       ects_count,
    subj.lc_count,
    subj.pr_count,
    subj.lb_count,
    subj.with_teacher_count,
    subj.own_count,
    subj.total_count,
    contr_type.TYPE_NAME            CONTROL_TYPE_NAME,
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
    LEFT JOIN education_module_type edu_mod_type
      ON edu_mod_type.id = elect_subj.education_module_type_id
  WHERE elect_subj.deleted = FALSE AND subj.deleted = FALSE;

ALTER TABLE elective_subject
  ALTER COLUMN code DROP NOT NULL;
ALTER TABLE elective_subject
  ALTER COLUMN education_module_type_id DROP NOT NULL;
--break curriculum

--trajectory
CREATE TABLE trajectory (
  id            BIGINT       NOT NULL,
  speciality_id BIGINT       NOT NULL,
  name          VARCHAR(255) NOT NULL
);

ALTER TABLE ONLY trajectory
  ADD CONSTRAINT pk_trajectory PRIMARY KEY (id);

ALTER TABLE ONLY trajectory
  ADD CONSTRAINT fk_trajectory_speciality FOREIGN KEY (speciality_id)
REFERENCES speciality (id)
ON UPDATE RESTRICT ON DELETE RESTRICT;

CREATE SEQUENCE s_trajectory
MINVALUE 0
START WITH 1
NO CYCLE;

--add trajectory to subject
ALTER TABLE subject
  ADD COLUMN trajectory_id BIGINT;

ALTER TABLE ONLY subject
  ADD CONSTRAINT fk_subject_trajectory FOREIGN KEY (trajectory_id)
REFERENCES trajectory (id)
ON UPDATE RESTRICT ON DELETE RESTRICT;

--continue curriculum
drop view V_CURRICULUM_ADD_PROGRAM;
CREATE OR REPLACE VIEW V_CURRICULUM_ADD_PROGRAM AS
  SELECT
    curr_add_pr.ID,
    curr_add_pr.CURRICULUM_ID,
    curr_add_pr.SEMESTER_ID,
    curr_add_pr.semester_data_id,
    curr_add_pr.SUBJECT_ID,
    subj.CREDITABILITY_ID,
    subj.ects_id,
    subj.CONTROL_TYPE_ID,
    curr_add_pr.education_module_type_id,
    subj.module_id,
    subj.subject_cycle_id,
    --end of ids
    sem.SEMESTER_NAME,
    subj_mod.module_short_name,
    subj_cycle.cycle_short_name,
    CASE WHEN curr_add_pr.education_module_type_id IS NULL
      THEN '-'
    ELSE edu_mod_type.type_name END education_module_type_name,
    curr_add_pr.CODE                SUBJECT_CODE,
    subj.NAME_kz                    SUBJECT_NAME_KZ,
    subj.name_ru                    subject_name_ru,
    cred.CREDIT,
    ects.ects                       ects_count,
    subj.lc_count,
    subj.pr_count,
    subj.lb_count,
    subj.with_teacher_count,
    subj.own_count,
    subj.total_count,
    contr_type.TYPE_NAME            CONTROL_TYPE_NAME
  FROM curriculum_add_program curr_add_pr
    INNER JOIN SEMESTER sem ON curr_add_pr.SEMESTER_ID = sem.ID
    INNER JOIN SUBJECT subj ON curr_add_pr.SUBJECT_ID = subj.ID
    INNER JOIN subject_module subj_mod ON subj.module_id = subj_mod.id
    INNER JOIN SUBJECT_CYCLE subj_cycle ON subj.SUBJECT_CYCLE_ID = subj_cycle.ID
    INNER JOIN CREDITABILITY cred ON subj.CREDITABILITY_ID = cred.ID
    INNER JOIN ACADEMIC_FORMULA form ON subj.ACADEMIC_FORMULA_ID = form.ID
    INNER JOIN CONTROL_TYPE contr_type ON subj.CONTROL_TYPE_ID = contr_type.ID
    INNER JOIN ects ON subj.ects_id = ects.id
    LEFT JOIN education_module_type edu_mod_type
      ON edu_mod_type.id = curr_add_pr.education_module_type_id
  WHERE curr_add_pr.deleted = FALSE AND subj.deleted = FALSE;

ALTER TABLE curriculum_add_program
  ALTER COLUMN code DROP NOT NULL;
ALTER TABLE curriculum_add_program
  ALTER COLUMN education_module_type_id DROP NOT NULL;

--teacher subject
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