CREATE OR REPLACE VIEW V_SEMESTER_SUBJECT AS
  SELECT
    a.ID,
    b.NAME_RU   SUBJECT_NAME,
    h.DEPT_NAME CHAIR_NAME,
    c.LEVEL_NAME,
    d.CYCLE_SHORT_NAME,
    e.CREDIT,
    f.FORMULA,
    g.TYPE_NAME CONTROL_TYPE_NAME
  FROM SEMESTER_SUBJECT a INNER JOIN SUBJECT b ON a.SUBJECT_ID = b.ID
    INNER JOIN LEVEL c ON b.LEVEL_ID = c.ID
    INNER JOIN SUBJECT_CYCLE d ON b.SUBJECT_CYCLE_ID = d.ID
    INNER JOIN CREDITABILITY e ON b.CREDITABILITY_ID = e.ID
    INNER JOIN ACADEMIC_FORMULA f ON b.ACADEMIC_FORMULA_ID = f.ID
    INNER JOIN CONTROL_TYPE g ON b.CONTROL_TYPE_ID = g.ID
    INNER JOIN DEPARTMENT h ON b.CHAIR_ID = h.ID;

create sequence s_student_subject
minvalue 0
start with 1
no cycle;

ALTER TABLE users ADD COLUMN reason varchar(180) null;