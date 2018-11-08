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



____________________

DROP VIEW V_TEACHER_SUBJECT_MODULE;

CREATE OR REPLACE VIEW V_TEACHER_SUBJECT_MODULE AS
  SELECT
    teacher_subject.ID,
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

DROP VIEW v_subject_select;

CREATE VIEW v_subject_select AS
  SELECT subj.id,
    subj.name_ru,
    subj.name_kz,
    subj.study_direct_id,
    subj.chair_id,
    subj.level_id,
    subj.subject_cycle_id,
    subj.mandatory,
    subj.creditability_id,
    cred.credit,
    contr_type.type_name AS control_type_name
  FROM ((subject subj
    JOIN creditability cred ON ((subj.creditability_id = cred.id)))
    JOIN control_type contr_type ON ((subj.control_type_id = contr_type.id)))
  WHERE ((subj.deleted = false) AND (subj.chair_id IS NOT NULL));