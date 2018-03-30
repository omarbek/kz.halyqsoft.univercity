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
--     med_checkup.ALLOW_DORM,
--     med_checkup.ALLOW_STUDY,
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

INSERT INTO user_type VALUES (1, 'Сотрудник');
INSERT INTO user_type VALUES (2, 'Студент');

UPDATE users
SET user_type_id = 1, sex_id = 1, marital_status_id = 2, nationality_id = 1, citizenship_id = 1;

ALTER TABLE student
  ALTER COLUMN need_dorm TYPE BOOLEAN
  USING CASE WHEN need_dorm = 0
  THEN FALSE
        WHEN need_dorm = 1
          THEN TRUE
        ELSE NULL
        END;

create sequence S_USER_ADDRESS
minvalue 0
start with 1
no cycle;

INSERT INTO address_type VALUES (1, 'Адрес регистрации');
INSERT INTO address_type VALUES (2, 'Адрес фактического проживания');

create or replace view V_SPECIALITY as
  select spec.ID,
    spec.SPEC_NAME,
    spec.CODE,
    spec.CHAIR_ID,
    dep.DEPT_NAME CHAIR_NAME,
    dep.DEPT_SHORT_NAME CHAIR_SHORT_NAME,
    spec.LEVEL_ID,
    lvl.LEVEL_NAME,
    spec.DELETED
  from SPECIALITY spec inner join DEPARTMENT dep on spec.CHAIR_ID = dep.ID
    inner join LEVEL lvl on spec.LEVEL_ID = lvl.ID;

create sequence s_entrant_speciality
minvalue 0
start with 1
no cycle;

INSERT into passport_type VALUES (1, 'Удостоверение личности');
INSERT into passport_type VALUES (2, 'Паспорт');
INSERT into passport_type VALUES (3, 'Свидетельство о рождении');

create sequence s_user_document
minvalue 0
start with 1
no cycle;

INSERT INTO document_type VALUES (1, 'Удостоверение личности');
INSERT INTO document_type VALUES (2, 'Паспорт');
INSERT INTO document_type VALUES (3, 'Военный билет');
INSERT INTO document_type VALUES (4, 'Приписное свидетельство');
INSERT INTO document_type VALUES (5, 'Аттестат');
INSERT INTO document_type VALUES (6, 'Диплом');

create sequence s_user_document_file
minvalue 0
start with 1
no cycle;

ALTER TABLE user_document_file
  ALTER COLUMN deleted TYPE BOOLEAN
  USING CASE WHEN deleted = 0
  THEN FALSE
        WHEN deleted = 1
          THEN TRUE
        ELSE NULL
        END;

insert into military_doc_type VALUES (1, 'Приписное свидетельство');
insert into military_doc_type VALUES (2, 'Военный билет');

insert into education_doc_type VALUES (1, 'Аттестат');
insert into education_doc_type VALUES (2, 'Диплом');

INSERT INTO education_type VALUES (1, 'Среднее');
INSERT INTO education_type VALUES (2, 'Среднее специальное');
INSERT INTO education_type VALUES (3, 'Высшее');

INSERT INTO attestation_type VALUES (1, 'Первая аттестация');
INSERT INTO attestation_type VALUES (2, 'Вторая аттестация');

INSERT INTO school_certificate_type VALUES (1, 'Городской');
INSERT INTO school_certificate_type VALUES (2, 'Сельский');

INSERT INTO grant_type VALUES (1, 'Государственный грант');
INSERT INTO grant_type VALUES (2, 'Внутренний грант');

INSERT INTO payment_type VALUES (1, 'Частично');
INSERT INTO payment_type VALUES (2, 'Полностью');

INSERT INTO organization_type VALUES (1, 'Партнеры');
INSERT INTO organization_type VALUES (2, 'Рекрутеры');
INSERT INTO organization_type VALUES (3, 'Арендаторы и прочие');

create sequence S_ORGANIZATION
minvalue 0
start with 1
no cycle;

ALTER TABLE EDUCATION_DOC
  ALTER COLUMN gold_mark TYPE BOOLEAN
  USING CASE WHEN gold_mark = 0
  THEN FALSE
        WHEN gold_mark = 1
          THEN TRUE
        ELSE NULL
        END;

ALTER TABLE EDUCATION_DOC
  ALTER COLUMN high_graduated TYPE BOOLEAN
  USING CASE WHEN high_graduated = 0
  THEN FALSE
        WHEN high_graduated = 1
          THEN TRUE
        ELSE NULL
        END;

create sequence s_user_language
minvalue 0
start with 1
no cycle;

INSERT INTO document_type VALUES (11,'Преимущественное право');

drop VIEW V_MEDICAL_CHECKUP;

ALTER TABLE medical_checkup
  ALTER COLUMN allow_dorm TYPE BOOLEAN
  USING CASE WHEN allow_dorm = 0
  THEN FALSE
        WHEN allow_dorm = 1
          THEN TRUE
        ELSE NULL
        END;

ALTER TABLE medical_checkup
  ALTER COLUMN allow_study TYPE BOOLEAN
  USING CASE WHEN allow_study = 0
  THEN FALSE
        WHEN allow_study = 1
          THEN TRUE
        ELSE NULL
        END;

ALTER TABLE medical_checkup
  ALTER COLUMN allow_work TYPE BOOLEAN
  USING CASE WHEN allow_work = 0
  THEN FALSE
        WHEN allow_work = 1
          THEN TRUE
        ELSE NULL
        END;

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

INSERT INTO document_type VALUES (8,'Сведения о гранте');

create sequence s_student_relative
minvalue 0
start with 1
no cycle;

INSERT INTO document_type VALUES (9,'Данные по договору');

INSERT INTO relative_type VALUES (1, 'Отец');
INSERT INTO relative_type VALUES (2, 'Мать');

create sequence s_user_social_category
minvalue 0
start with 1
no cycle;

create sequence s_user_award
minvalue 0
start with 1
no cycle;

INSERT INTO document_type VALUES (7,'Данные ЕНТ');

create sequence s_unt_cert_subject
minvalue 0
start with 1
no cycle;