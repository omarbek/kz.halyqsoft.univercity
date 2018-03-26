CREATE OR REPLACE VIEW V_ADVISOR AS
  SELECT
    empl.ID,
    trim(usr.LAST_NAME || ' ' || usr.FIRST_NAME || ' ' || coalesce(usr.MIDDLE_NAME, '')) FIO,
    usr.CODE,
    empl_dept.DEPT_ID,
    dep.DEPT_NAME,
    empl_dept.POST_ID,
    post.POST_NAME
  FROM EMPLOYEE empl INNER JOIN USERS usr
      ON empl.ID = usr.ID AND empl.EMPLOYEE_STATUS_ID = 1 AND usr.LOCKED = FALSE AND length(usr.LAST_NAME) > 0
    INNER JOIN EMPLOYEE_DEPT empl_dept
      ON empl.ID = empl_dept.EMPLOYEE_ID AND empl_dept.EMPLOYEE_TYPE_ID = 2 AND empl_dept.DISMISS_DATE IS NULL
    INNER JOIN DEPARTMENT dep ON empl_dept.DEPT_ID = dep.ID AND dep.DELETED = FALSE
    LEFT JOIN POST post ON empl_dept.POST_ID = post.ID;

CREATE OR REPLACE VIEW V_ENTRANT_SPECIALITY AS
  SELECT
    entrant_spec.ID,
    entrant_spec.STUDENT_ID,
    entrant_spec.UNIVERSITY_ID,
    univer.UNIVERSITY_NAME,
    entrant_spec.SPECIALITY_ID,
    spec.SPEC_NAME SPECIALITY_NAME,
    entrant_spec.LANGUAGE_ID,
    lang.LANG_NAME LANGUAGE_NAME
  FROM ENTRANT_SPECIALITY entrant_spec INNER JOIN UNIVERSITY univer ON entrant_spec.UNIVERSITY_ID = univer.ID
    INNER JOIN SPECIALITY spec ON entrant_spec.SPECIALITY_ID = spec.ID
    INNER JOIN LANGUAGE lang ON entrant_spec.LANGUAGE_ID = lang.ID;
