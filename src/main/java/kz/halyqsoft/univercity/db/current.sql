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

ALTER TABLE user_document
  ALTER COLUMN DELETED TYPE BOOLEAN
  USING CASE WHEN DELETED = 0
  THEN FALSE
        WHEN DELETED = 1
          THEN TRUE
        ELSE NULL
        END;

CREATE VIEW V_USER_LANGUAGE AS
  SELECT
    usr_lang.ID,
    usr_lang.USER_ID,
    usr_lang.LANGUAGE_ID,
    lang.LANG_NAME,
    usr_lang.LANGUAGE_LEVEL_ID,
    lang_lvl.LEVEL_NAME
  FROM USER_LANGUAGE usr_lang INNER JOIN LANGUAGE lang ON usr_lang.LANGUAGE_ID = lang.ID
    INNER JOIN LANGUAGE_LEVEL lang_lvl ON usr_lang.LANGUAGE_LEVEL_ID = lang_lvl.ID;

CREATE OR REPLACE VIEW V_MEDICAL_CHECKUP AS
  SELECT
    med_checkup.ID,
    usr_doc.USER_ID,
    usr_doc.DOCUMENT_NO,
    usr_doc.ISSUE_DATE,
    usr_doc.EXPIRE_DATE,
    med_checkup.CHECKUP_TYPE_ID,
    med_checkup_type.TYPE_NAME CHECKUP_TYPE_NAME,
    med_checkup.ISSUER_NAME,
    med_checkup.ALLOW_DORM,
    med_checkup.ALLOW_STUDY,
    med_checkup.ALLOW_WORK,
    usr_doc.DELETED
  FROM MEDICAL_CHECKUP med_checkup INNER JOIN USER_DOCUMENT usr_doc ON med_checkup.ID = usr_doc.ID
    INNER JOIN MEDICAL_CHECKUP_TYPE med_checkup_type ON med_checkup.CHECKUP_TYPE_ID = med_checkup_type.ID;

CREATE VIEW V_UNT_CERT_SUBJECT AS
  SELECT
    unt_cert.ID,
    unt_cert.UNT_CERTIFICATE_ID,
    unt_cert.UNT_SUBJECT_ID,
    unt_subj.SUBJECT_NAME UNT_SUBJECT_NAME,
    unt_cert.RATE
  FROM UNT_CERT_SUBJECT unt_cert INNER JOIN UNT_SUBJECT unt_subj ON unt_cert.UNT_SUBJECT_ID = unt_subj.ID;

CREATE VIEW V_USER_SOCIAL_CATEGORY AS
  SELECT
    usr_soc_cat.ID,
    usr_soc_cat.USER_ID,
    usr_soc_cat.SOCIAL_CATEGORY_ID,
    soc_cat.CATEGORY_NAME SOCIAL_CATEGORY_NAME
  FROM
    USER_SOCIAL_CATEGORY usr_soc_cat INNER JOIN SOCIAL_CATEGORY soc_cat ON usr_soc_cat.SOCIAL_CATEGORY_ID = soc_cat.ID;

CREATE VIEW V_USER_AWARD AS
  SELECT
    usr_award.ID,
    usr_award.USER_ID,
    usr_award.AWARD_ID,
    award.AWARD_NAME
  FROM USER_AWARD usr_award INNER JOIN AWARD award ON usr_award.AWARD_ID = award.ID;