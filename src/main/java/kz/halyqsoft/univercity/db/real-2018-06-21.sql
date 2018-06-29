drop view V_SUBJECT_SELECT;

CREATE OR REPLACE VIEW V_SUBJECT_SELECT AS
  SELECT
    subj.ID,
    subj.NAME_RU,
    subj.STUDY_DIRECT_ID,
    subj.CHAIR_ID,
    subj.LEVEL_ID,
    subj.SUBJECT_CYCLE_ID,
    subj.MANDATORY,
    subj.CREDITABILITY_ID,
    cred.CREDIT,
    contr_type.TYPE_NAME CONTROL_TYPE_NAME
  FROM SUBJECT subj INNER JOIN CREDITABILITY cred ON subj.CREDITABILITY_ID = cred.ID
    INNER JOIN CONTROL_TYPE contr_type ON subj.CONTROL_TYPE_ID = contr_type.ID
  WHERE subj.DELETED = FALSE
        AND subj.CHAIR_ID IS NOT NULL;

drop view V_TEACHER_SUBJECT;

CREATE OR REPLACE VIEW V_TEACHER_SUBJECT AS
  SELECT
    teacher_subject.ID,
    teacher_subject.EMPLOYEE_ID,
    subj.NAME_RU SUBJECT_NAME,
    teacher_subject.FALL,
    teacher_subject.SPRING,
    teacher_subject.SUMMER,
    teacher_subject.LOAD_PER_HOURS
  FROM TEACHER_SUBJECT teacher_subject INNER JOIN SUBJECT subj ON teacher_subject.SUBJECT_ID = subj.ID;

drop view v_room_subject;

CREATE OR REPLACE VIEW V_ROOM_SUBJECT AS
  SELECT
    room_subj.ID,
    room_subj.ROOM_ID,
    room_subj.SUBJECT_ID,
    subj.NAME_RU           SUBJECT_NAME_RU,
    dept.DEPT_NAME         CHAIR_NAME,
    lvl.LEVEL_NAME,
    cred.CREDIT,
    control_type.TYPE_NAME CONTROL_TYPE_NAME
  FROM ROOM_SUBJECT room_subj INNER JOIN SUBJECT subj ON room_subj.SUBJECT_ID = subj.ID
    INNER JOIN DEPARTMENT dept ON subj.CHAIR_ID = dept.ID
    INNER JOIN LEVEL lvl ON subj.LEVEL_ID = lvl.ID
    INNER JOIN CREDITABILITY cred ON subj.CREDITABILITY_ID = cred.ID
    INNER JOIN CONTROL_TYPE control_type ON subj.CONTROL_TYPE_ID = control_type.ID;

ALTER TABLE curriculum_add_program
  ADD COLUMN code CHARACTER VARYING(14) NOT NULL DEFAULT '101';
ALTER TABLE curriculum_add_program
  ADD COLUMN education_module_type_id BIGINT NOT NULL DEFAULT 1;

ALTER TABLE curriculum_add_program
  ADD CONSTRAINT fk_curriculum_add_program_education_module_type FOREIGN KEY (education_module_type_id)
REFERENCES education_module_type (id)
ON DELETE RESTRICT ON UPDATE RESTRICT;

DROP VIEW V_CURRICULUM_ADD_PROGRAM;

CREATE OR REPLACE VIEW V_CURRICULUM_ADD_PROGRAM AS
  SELECT
    curr_add_pr.ID,
    curr_add_pr.CURRICULUM_ID,
    curr_add_pr.SUBJECT_ID,
    subj.NAME_RU SUBJECT_NAME_RU,
    curr_add_pr.CODE    SUBJECT_CODE,
    subj.CREDITABILITY_ID,
    cred.CREDIT,
    curr_add_pr.SEMESTER_ID,
    sem.SEMESTER_NAME,
    edu_mod_type.id             education_module_type_id,
    edu_mod_type.type_name      education_module_type_name,
    curr_add_pr.DELETED
  FROM CURRICULUM_ADD_PROGRAM curr_add_pr INNER JOIN SUBJECT subj ON curr_add_pr.SUBJECT_ID = subj.ID
    INNER JOIN CREDITABILITY cred ON subj.CREDITABILITY_ID = cred.ID
    INNER JOIN SEMESTER sem ON curr_add_pr.SEMESTER_ID = sem.ID
    INNER JOIN education_module_type edu_mod_type ON edu_mod_type.id = curr_add_pr.education_module_type_id;

ALTER TABLE curriculum_after_semester
  ADD COLUMN code CHARACTER VARYING(14) NULL;
ALTER TABLE curriculum_after_semester
  ADD COLUMN education_module_type_id BIGINT NOT NULL DEFAULT 1;

ALTER TABLE curriculum_after_semester
  ADD CONSTRAINT fk_curriculum_after_semester_education_module_type FOREIGN KEY (education_module_type_id)
REFERENCES education_module_type (id)
ON DELETE RESTRICT ON UPDATE RESTRICT;

DROP VIEW v_curriculum_after_semester;

CREATE OR REPLACE VIEW V_CURRICULUM_AFTER_SEMESTER AS
  SELECT
    curr_after_sem.ID,
    curr_after_sem.CURRICULUM_ID,
    curr_after_sem.SUBJECT_ID,
    subj.NAME_RU                                SUBJECT_NAME_RU,
    curr_after_sem.CODE                                   SUBJECT_CODE,
    subj.CREDITABILITY_ID,
    cred.CREDIT,
    sem.id                                      semester_data_id,
    sem.entrance_year || ' ' || sem.period_name semester_data_name,
    edu_mod_type.id             education_module_type_id,
    edu_mod_type.type_name      education_module_type_name,
    curr_after_sem.DELETED
  FROM CURRICULUM_AFTER_SEMESTER curr_after_sem INNER JOIN SUBJECT subj ON curr_after_sem.SUBJECT_ID = subj.ID
    INNER JOIN CREDITABILITY cred ON subj.CREDITABILITY_ID = cred.ID
    INNER JOIN V_SEMESTER_DATA sem ON curr_after_sem.semester_data_id = sem.ID
    INNER JOIN education_module_type edu_mod_type ON edu_mod_type.id = curr_after_sem.education_module_type_id;

drop view v_elective_subject_label;

ALTER TABLE curriculum_detail
  ADD COLUMN code CHARACTER VARYING(14) NOT NULL DEFAULT '101';
ALTER TABLE curriculum_detail
  ADD COLUMN education_module_type_id BIGINT NOT NULL DEFAULT 1;

ALTER TABLE curriculum_detail
  ADD CONSTRAINT fk_curriculum_detail_education_module_type FOREIGN KEY (education_module_type_id)
REFERENCES education_module_type (id)
ON DELETE RESTRICT ON UPDATE RESTRICT;

DROP VIEW V_CURRICULUM_DETAIL;

CREATE OR REPLACE VIEW V_CURRICULUM_DETAIL AS
  SELECT
    a.ID,
    a.CURRICULUM_ID,
    a.SEMESTER_ID,
    b.SEMESTER_NAME,
    a.SUBJECT_ID,
    a.CODE                      SUBJECT_CODE,
    c.NAME_RU                   SUBJECT_NAME,
    CASE WHEN c.SUBJECT_CYCLE_ID = 4
      THEN a.SUBJECT_CYCLE_ID
    ELSE c.SUBJECT_CYCLE_ID END SUBJECT_CYCLE_ID,
    CASE WHEN c.SUBJECT_CYCLE_ID = 4
      THEN i.CYCLE_SHORT_NAME
    ELSE d.CYCLE_SHORT_NAME END CYCLE_SHORT_NAME,
    c.CREDITABILITY_ID,
    e.CREDIT,
    c.ACADEMIC_FORMULA_ID,
    f.FORMULA,
    a.RECOMMENDED_SEMESTER,
    a.CONSIDER_CREDIT,
    c.CONTROL_TYPE_ID,
    h.TYPE_NAME                 CONTROL_TYPE_NAME,
    edu_mod_type.id             education_module_type_id,
    edu_mod_type.type_name      education_module_type_name,
    a.DELETED,
    FALSE                       ELECTIVE
  FROM CURRICULUM_DETAIL a INNER JOIN SEMESTER b ON a.SEMESTER_ID = b.ID
    INNER JOIN SUBJECT c ON a.SUBJECT_ID = c.ID
    INNER JOIN SUBJECT_CYCLE d ON c.SUBJECT_CYCLE_ID = d.ID
    INNER JOIN CREDITABILITY e ON c.CREDITABILITY_ID = e.ID
    INNER JOIN ACADEMIC_FORMULA f ON c.ACADEMIC_FORMULA_ID = f.ID
    INNER JOIN CONTROL_TYPE h ON c.CONTROL_TYPE_ID = h.ID
    INNER JOIN education_module_type edu_mod_type ON edu_mod_type.id = a.education_module_type_id
    LEFT JOIN SUBJECT_CYCLE i ON a.SUBJECT_CYCLE_ID = i.ID;

ALTER TABLE elective_subject
  ADD COLUMN code CHARACTER VARYING(14) NOT NULL DEFAULT '101';
ALTER TABLE elective_subject
  ADD COLUMN education_module_type_id BIGINT NOT NULL DEFAULT 1;

ALTER TABLE elective_subject
  ADD CONSTRAINT fk_elective_subject_education_module_type FOREIGN KEY (education_module_type_id)
REFERENCES education_module_type (id)
ON DELETE RESTRICT ON UPDATE RESTRICT;

drop view V_ELECTIVE_SUBJECT;

CREATE OR REPLACE VIEW V_ELECTIVE_SUBJECT AS
  SELECT
    a.ID,
    a.CURRICULUM_ID,
    a.SEMESTER_ID,
    d.SEMESTER_NAME,
    a.SUBJECT_ID,
    a.CODE                      SUBJECT_CODE,
    c.NAME_RU                   SUBJECT_NAME,
    CASE WHEN c.subject_cycle_id = 4
      THEN a.subject_cycle_id
    ELSE c.subject_cycle_id END SUBJECT_CYCLE_ID,
    CASE WHEN c.subject_cycle_id = 4
      THEN i.CYCLE_SHORT_NAME
    ELSE g.CYCLE_SHORT_NAME END CYCLE_SHORT_NAME,
    c.CREDITABILITY_ID,
    f.CREDIT,
    c.ACADEMIC_FORMULA_ID,
    e.FORMULA,
    c.CONTROL_TYPE_ID,
    h.TYPE_NAME                 CONTROL_TYPE_NAME,
    edu_mod_type.id             education_module_type_id,
    edu_mod_type.type_name      education_module_type_name,
    a.consider_credit,
    a.DELETED
  FROM ELECTIVE_SUBJECT a INNER JOIN CURRICULUM b ON b.ID = a.CURRICULUM_ID
    INNER JOIN SUBJECT c ON c.ID = a.SUBJECT_ID
    INNER JOIN SEMESTER d ON d.ID = a.SEMESTER_ID
    INNER JOIN ACADEMIC_FORMULA e ON c.ACADEMIC_FORMULA_ID = e.ID
    INNER JOIN CREDITABILITY f ON c.CREDITABILITY_ID = f.ID
    INNER JOIN SUBJECT_CYCLE g ON c.SUBJECT_CYCLE_ID = g.ID
    INNER JOIN CONTROL_TYPE h ON c.CONTROL_TYPE_ID = h.ID
    INNER JOIN education_module_type edu_mod_type ON edu_mod_type.id = a.education_module_type_id
    LEFT JOIN SUBJECT_CYCLE i ON a.SUBJECT_CYCLE_ID = i.ID;

CREATE OR REPLACE VIEW V_CURRICULUM_SUBJECT AS
  SELECT
    a.ID,
    a.CURRICULUM_ID,
    a.SEMESTER_ID,
    b.SEMESTER_NAME,
    a.SUBJECT_ID,
    a.CODE                      SUBJECT_CODE,
    c.NAME_RU                   SUBJECT_NAME,
    CASE WHEN c.SUBJECT_CYCLE_ID = 4
      THEN a.SUBJECT_CYCLE_ID
    ELSE c.SUBJECT_CYCLE_ID END SUBJECT_CYCLE_ID,
    CASE WHEN c.SUBJECT_CYCLE_ID = 4
      THEN i.CYCLE_SHORT_NAME
    ELSE d.CYCLE_SHORT_NAME END CYCLE_SHORT_NAME,
    c.CREDITABILITY_ID,
    e.CREDIT,
    c.ACADEMIC_FORMULA_ID,
    f.FORMULA,
    a.RECOMMENDED_SEMESTER,
    a.CONSIDER_CREDIT,
    c.CONTROL_TYPE_ID,
    h.TYPE_NAME                 CONTROL_TYPE_NAME,
    a.DELETED,
    FALSE                       ELECTIVE
  FROM CURRICULUM_DETAIL a INNER JOIN SEMESTER b ON a.SEMESTER_ID = b.ID
    INNER JOIN SUBJECT c ON a.SUBJECT_ID = c.ID
    INNER JOIN SUBJECT_CYCLE d ON c.SUBJECT_CYCLE_ID = d.ID
    INNER JOIN CREDITABILITY e ON c.CREDITABILITY_ID = e.ID
    INNER JOIN ACADEMIC_FORMULA f ON c.ACADEMIC_FORMULA_ID = f.ID
    INNER JOIN CONTROL_TYPE h ON c.CONTROL_TYPE_ID = h.ID
    LEFT JOIN SUBJECT_CYCLE i ON a.SUBJECT_CYCLE_ID = i.ID
  UNION ALL
  SELECT
    aa.ID,
    aa.CURRICULUM_ID,
    aa.SEMESTER_ID,
    bb.SEMESTER_NAME,
    aa.subject_id                SUBJECT_ID,
    aa.code                      SUBJECT_CODE,
    subject.NAME_RU              SUBJECT_NAME,
    CASE WHEN aa.subject_cycle_id IS NULL
      THEN cycle.id
    ELSE aa.subject_cycle_id END SUBJECT_CYCLE_ID,
    CASE WHEN dd.CYCLE_SHORT_NAME
              IS NULL
      THEN cycle.cycle_short_name
    ELSE dd.cycle_short_name END,
    credit.id                    CREDITABILITY_ID,
    credit.credit                CREDIT,
    subject.academic_formula_id  ACADEMIC_FORMULA_ID,
    f.formula                    FORMULA,
    NULL                         RECOMMENDED_SEMESTER,
    aa.CONSIDER_CREDIT,
    subject.control_type_id      CONTROL_TYPE_ID,
    h.type_name                  CONTROL_TYPE_NAME,
    aa.DELETED,
    TRUE                         ELECTIVE
  FROM elective_subject aa INNER JOIN SEMESTER bb ON aa.SEMESTER_ID = bb.ID
    LEFT JOIN SUBJECT_CYCLE dd ON aa.subject_cycle_id = dd.ID
    INNER JOIN subject subject ON subject.id = aa.subject_id
    INNER JOIN creditability credit ON credit.id = subject.creditability_id
    INNER JOIN subject_cycle cycle ON cycle.id = subject.subject_cycle_id
    INNER JOIN ACADEMIC_FORMULA f ON subject.ACADEMIC_FORMULA_ID = f.ID
    INNER JOIN CONTROL_TYPE h ON subject.CONTROL_TYPE_ID = h.ID;

--damira

alter table subject drop column education_module_type_id;
alter table subject drop column code;