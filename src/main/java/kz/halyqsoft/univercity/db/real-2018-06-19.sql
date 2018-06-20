CREATE OR REPLACE VIEW V_CURRICULUM_SUBJECT AS
  SELECT
    a.ID,
    a.CURRICULUM_ID,
    a.SEMESTER_ID,
    b.SEMESTER_NAME,
    a.SUBJECT_ID,
    c.CODE                      SUBJECT_CODE,
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
    subject.code                 SUBJECT_CODE,
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

CREATE SEQUENCE S_academic_formula
MINVALUE 0
START WITH 2
NO CYCLE;

CREATE TABLE elective_binded_subject (
  id                BIGINT    NOT NULL,
  first_subject_id  BIGINT    NOT NULL,
  second_subject_id BIGINT    NOT NULL,
  created           TIMESTAMP NOT NULL
);

CREATE SEQUENCE s_elective_binded_subject
MINVALUE 0
START WITH 1
NO CYCLE;

ALTER TABLE elective_binded_subject
  ADD CONSTRAINT pk_elective_binded_subject PRIMARY KEY (id);

ALTER TABLE elective_binded_subject
  ADD CONSTRAINT fk_elective_binded_subject_first_subject FOREIGN KEY (first_subject_id)
REFERENCES subject (id)
ON DELETE RESTRICT ON UPDATE RESTRICT;

ALTER TABLE elective_binded_subject
  ADD CONSTRAINT fk_elective_binded_subject_second_subject FOREIGN KEY (second_subject_id)
REFERENCES subject (id)
ON DELETE RESTRICT ON UPDATE RESTRICT;

INSERT INTO TASKS (CLASS_PATH, DESCR, ICON_NAME, NAME, TASK_ORDER, TASK_TYPE, TITLE, VISIBLE, ID,
                   PARENT_ID)
VALUES ('kz.halyqsoft.univercity.modules.bindingelectivesubject.BindingElectiveSubjectView',
        'KK=КЭД;RU=КЭД;EN=Elective subjects list;', NULL,
        'KK=КЭД;RU=КЭД;EN=Elective subjects list;', 505, FALSE,
        'KK=КЭД;RU=КЭД;EN=Elective subjects list;', TRUE, nextval('s_tasks'), 29);

CREATE UNIQUE INDEX idx_elective_binded_subject
  ON elective_binded_subject (
    first_subject_id ASC,
    second_subject_id ASC
  );