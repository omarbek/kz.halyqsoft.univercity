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

//