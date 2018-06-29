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