create sequence S_NATIONALITY
minvalue 0
start with 1
no cycle;

create sequence S_MARITAL_STATUS
minvalue 0
start with 1
no cycle;

create sequence S_AWARD
minvalue 0
start with 1
no cycle;

create sequence S_CONTRACT_TYPE
minvalue 0
start with 1
no cycle;

create sequence S_CORPUS
minvalue 0
start with 1
no cycle;

create sequence S_COUNTRY
minvalue 0
start with 1
no cycle;

create sequence S_DEGREE
minvalue 0
start with 1
no cycle;

create sequence S_ENTRANCE_YEAR
minvalue 0
start with 1
no cycle;

create sequence S_EQUIPMENT
minvalue 0
start with 1
no cycle;

create sequence S_LANGUAGE
minvalue 0
start with 1
no cycle;

create sequence S_LANGUAGE_LEVEL
minvalue 0
start with 1
no cycle;

create sequence S_MEDICAL_CHECKUP_TYPE
minvalue 0
start with 1
no cycle;

create sequence S_MILITARY_STATUS
minvalue 0
start with 1
no cycle;

create sequence S_ORDER_TYPE
minvalue 0
start with 1
no cycle;

create sequence S_POST
minvalue 0
start with 1
no cycle;

create sequence S_RATING_SCALE
minvalue 0
start with 1
no cycle;

create sequence S_SCHOOL_TYPE
minvalue 0
start with 1
no cycle;

create sequence S_SOCIAL_CATEGORY
minvalue 0
start with 1
no cycle;

create sequence S_STUDENT_STATUS
minvalue 0
start with 1
no cycle;

ALTER TABLE department
  ALTER COLUMN DELETED TYPE BOOLEAN
  USING CASE WHEN DELETED = 0
  THEN FALSE
        WHEN DELETED = 1
          THEN TRUE
        ELSE NULL
        END;

create sequence S_DEPARTMENT
minvalue 0
start with 1
no cycle;

create sequence S_UNT_SUBJECT
minvalue 0
start with 1
no cycle;

ALTER TABLE department DROP COLUMN skd_id;

ALTER TABLE department
  ALTER COLUMN FC TYPE BOOLEAN
  USING CASE WHEN FC = 0
  THEN FALSE
        WHEN FC = 1
          THEN TRUE
        ELSE NULL
        END;

ALTER TABLE university
  ALTER COLUMN use_default TYPE BOOLEAN
  USING CASE WHEN use_default = 0
  THEN FALSE
        WHEN use_default = 1
          THEN TRUE
        ELSE NULL
        END;

create sequence S_UNIVERSITY
minvalue 0
start with 1
no cycle;

ALTER TABLE post
  ALTER COLUMN tp TYPE BOOLEAN
  USING CASE WHEN tp = 0
  THEN FALSE
        WHEN tp = 1
          THEN TRUE
        ELSE NULL
        END;

update users set passwd='bbc5e661e106c6dcd8dc6dd186454c2fcba3c710fb4d8e71a60c93eaf077f073';
update users set login='admin' where id=2;

ALTER SEQUENCE s_tasks RESTART WITH 10;

INSERT into tasks (id,name,title,task_type,task_order,class_path,parent_id,descr,visible)
VALUES (nextval('s_tasks'),'KK=Справочник;RU=Справочник;EN=Catalog;','KK=Справочник;RU=Справочник;EN=Catalog;','f',204,
'kz.halyqsoft.univercity.modules.catalog.CatalogView',3,'KK=Справочник;RU=Справочник;EN=Catalog;','t');

INSERT into tasks (id,name,title,task_type,task_order,class_path,parent_id,descr,visible)
VALUES (nextval('s_tasks'),'KK=Регистрация абитуриентов;RU=Регистрация абитуриентов;EN=Register applicants;',
        'KK=Регистрация абитуриентов;RU=Регистрация абитуриентов;EN=Register applicants;','f',205,
        'kz.halyqsoft.univercity.modules.regapplicants.RegisterApplicantsView',3,
        'KK=Регистрация абитуриентов;RU=Регистрация абитуриентов;EN=Register applicants;','t');

ALTER SEQUENCE s_role_tasks RESTART WITH 10;

INSERT into role_tasks VALUES (nextval('s_role_tasks'),3,3,'t');
INSERT into role_tasks VALUES (nextval('s_role_tasks'),3,10,'t');
INSERT into role_tasks VALUES (nextval('s_role_tasks'),3,11,'t');

insert into level VALUES (1, 'Бакалавриат');
insert into level VALUES (2, 'Магистратура');
insert into level VALUES (3, 'Докторантура');

create sequence S_SPECIALITY
minvalue 0
start with 1
no cycle;

ALTER TABLE speciality
  ALTER COLUMN deleted TYPE BOOLEAN
  USING CASE WHEN deleted = 0
  THEN FALSE
        WHEN deleted = 1
          THEN TRUE
        ELSE NULL
        END;

create sequence S_ACADEMIC_DEGREE
minvalue 0
start with 1
no cycle;

create sequence S_STUDENT_CATEGORY
minvalue 0
start with 1
no cycle;

insert into semester_period VALUES (1, 'Осенний');
insert into semester_period VALUES (2, 'Весенний');
insert into semester_period VALUES (3, 'Летний');

create sequence S_SEMESTER_DATA
minvalue 0
start with 1
no cycle;

INSERT into sex VALUES (1, 'Мужской');
INSERT into sex VALUES (2, 'Женский');

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
SET user_type_id = 1, sex_id = 1, marital_status_id = 1, nationality_id = 1, citizenship_id = 1;

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

create sequence s_users_code
minvalue 0
start with 1
no cycle;

--
INSERT INTO student_education_type VALUES (1, 'Договор');
INSERT INTO student_education_type VALUES (2, 'Грант');
INSERT INTO student_education_type VALUES (3, 'Внутренний грант');
INSERT INTO student_education_type VALUES (4, 'Кредит');

INSERT INTO study_year VALUES (1, 1);
INSERT INTO study_year VALUES (2, 2);
INSERT INTO study_year VALUES (3, 3);
INSERT INTO study_year VALUES (4, 4);

CREATE SEQUENCE s_student_education
MINVALUE 0
START WITH 1
NO CYCLE;

ALTER TABLE STUDENT_FIN_DEBT
  ALTER COLUMN DELETED TYPE BOOLEAN
  USING CASE WHEN DELETED = 0
  THEN FALSE
        WHEN DELETED = 1
          THEN TRUE
        ELSE NULL
        END;

CREATE OR REPLACE VIEW V_ORDER_DOC AS
  SELECT
    order_doc.ID,
    usr_doc.USER_ID,
    usr_doc.DOCUMENT_NO,
    order_doc.ORDER_TYPE_ID,
    order_type.TYPE_NAME ORDER_TYPE_NAME,
    order_doc.STUDY_YEAR_ID,
    study_year.STUDY_YEAR,
    usr_doc.ISSUE_DATE,
    usr_doc.EXPIRE_DATE,
    order_doc.DESCR,
    order_doc.HIDE_TRANSCRIPT,
    usr_doc.DELETED
  FROM ORDER_DOC order_doc INNER JOIN USER_DOCUMENT usr_doc ON order_doc.ID = usr_doc.ID
    INNER JOIN ORDER_TYPE order_type ON order_doc.ORDER_TYPE_ID = order_type.ID
    LEFT JOIN STUDY_YEAR study_year ON order_doc.STUDY_YEAR_ID = study_year.ID;

CREATE SEQUENCE S_USER_PHOTO
MINVALUE 0
START WITH 1
NO CYCLE;

INSERT INTO academic_status VALUES (1, 'Полный');
INSERT INTO academic_status VALUES (2, 'Условный');

ALTER TABLE lock_reason
  DROP COLUMN lock_type;

ALTER TABLE lock_reason
  ADD COLUMN USER_TYPE_ID BIGINT NOT NULL DEFAULT 2;
ALTER TABLE ONLY lock_reason
  ADD CONSTRAINT fk_lock_reason_user_type FOREIGN KEY (USER_TYPE_ID) REFERENCES user_type (id) ON UPDATE RESTRICT ON DELETE RESTRICT;

CREATE SEQUENCE S_LOCK_REASON
MINVALUE 0
START WITH 1
NO CYCLE;

ALTER TABLE users
  ADD COLUMN updated TIMESTAMP;
ALTER TABLE users
  ADD COLUMN UPDATED_BY VARCHAR(255);

INSERT INTO document_type VALUES (10, 'Приказ об обучении');

DROP VIEW v_order_doc;

ALTER TABLE order_doc
  ALTER COLUMN hide_transcript TYPE BOOLEAN
  USING CASE WHEN hide_transcript = 0
  THEN FALSE
        WHEN hide_transcript = 1
          THEN TRUE
        ELSE NULL
        END;

CREATE OR REPLACE VIEW V_ORDER_DOC AS
  SELECT
    order_doc.ID,
    usr_doc.USER_ID,
    usr_doc.DOCUMENT_NO,
    order_doc.ORDER_TYPE_ID,
    order_type.TYPE_NAME ORDER_TYPE_NAME,
    order_doc.STUDY_YEAR_ID,
    study_year.STUDY_YEAR,
    usr_doc.ISSUE_DATE,
    usr_doc.EXPIRE_DATE,
    order_doc.DESCR,
    order_doc.HIDE_TRANSCRIPT,
    usr_doc.DELETED
  FROM ORDER_DOC order_doc INNER JOIN USER_DOCUMENT usr_doc ON order_doc.ID = usr_doc.ID
    INNER JOIN ORDER_TYPE order_type ON order_doc.ORDER_TYPE_ID = order_type.ID
    LEFT JOIN STUDY_YEAR study_year ON order_doc.STUDY_YEAR_ID = study_year.ID;

INSERT INTO employee_status VALUES (1, 'Штатный');
INSERT INTO employee_status VALUES (2, 'Внештатный');
INSERT INTO employee_status VALUES (3, 'Совместитель');
INSERT INTO employee_status VALUES (4, 'Не работает');
INSERT INTO employee_status VALUES (5, 'Почасовик');

ALTER TABLE employee
  ALTER COLUMN bachelor TYPE BOOLEAN
  USING CASE WHEN bachelor = 0
  THEN FALSE
        WHEN bachelor = 1
          THEN TRUE
        ELSE NULL
        END;

ALTER TABLE employee
  ALTER COLUMN master TYPE BOOLEAN
  USING CASE WHEN master = 0
  THEN FALSE
        WHEN master = 1
          THEN TRUE
        ELSE NULL
        END;

CREATE OR REPLACE VIEW V_EMPLOYEE_DEGREE AS
  SELECT
    empl_degree.ID,
    usr_doc.USER_ID    EMPLOYEE_ID,
    usr_doc.DOCUMENT_NO,
    usr_doc.ISSUE_DATE,
    usr_doc.EXPIRE_DATE,
    empl_degree.DEGREE_ID,
    degree.DEGREE_NAME DEGREE_NAME,
    empl_degree.SCHOOL_NAME,
    empl_degree.DISSERTATION_TOPIC,
    usr_doc.DELETED
  FROM EMPLOYEE_DEGREE empl_degree INNER JOIN USER_DOCUMENT usr_doc ON empl_degree.ID = usr_doc.ID
    INNER JOIN DEGREE degree ON empl_degree.DEGREE_ID = degree.ID;

INSERT INTO document_type VALUES (12, 'Научная степень');

CREATE VIEW V_PUBLICATION AS
  SELECT
    publ.ID,
    empl_scient.EMPLOYEE_ID,
    publ.PUBLICATION_TYPE_ID,
    publ_type.TYPE_NAME PUBLICATION_TYPE_NAME,
    empl_scient.TOPIC
  FROM PUBLICATION publ INNER JOIN EMPLOYEE_SCIENTIFIC empl_scient ON publ.ID = empl_scient.ID
    INNER JOIN PUBLICATION_TYPE publ_type ON publ.PUBLICATION_TYPE_ID = publ_type.ID;

CREATE VIEW V_SCIENTIFIC_ACTIVITY AS
  SELECT
    scient_act.ID,
    empl_scient.EMPLOYEE_ID,
    scient_act.SCIENTIFIC_ACTIVITY_TYPE_ID,
    scient_act_type.TYPE_NAME SCIENTIFIC_ACTIVITY_TYPE_NAME,
    empl_scient.TOPIC,
    scient_act.BEGIN_DATE,
    scient_act.END_DATE
  FROM SCIENTIFIC_ACTIVITY scient_act INNER JOIN EMPLOYEE_SCIENTIFIC empl_scient ON scient_act.ID = empl_scient.ID
    INNER JOIN SCIENTIFIC_ACTIVITY_TYPE scient_act_type ON scient_act.SCIENTIFIC_ACTIVITY_TYPE_ID = scient_act_type.ID;

CREATE VIEW V_SCIENTIFIC_MANAGEMENT AS
  SELECT
    scient_managem.ID,
    empl_scient.EMPLOYEE_ID,
    scient_managem.SCIENTIFIC_MANAGEMENT_TYPE_ID,
    scient_managem_type.TYPE_NAME SCIENTIFIC_MANAGEMENT_TYPE_NAME,
    scient_managem.STUDENTS_FIO,
    scient_managem.STUDENTS_COUNT,
    scient_managem.PROJECT_NAME,
    empl_scient.TOPIC,
    scient_managem.RESULT_,
    scient_managem.ACHIEVEMENT
  FROM SCIENTIFIC_MANAGEMENT scient_managem INNER JOIN EMPLOYEE_SCIENTIFIC empl_scient
      ON scient_managem.ID = empl_scient.ID
    INNER JOIN SCIENTIFIC_MANAGEMENT_TYPE scient_managem_type
      ON scient_managem.SCIENTIFIC_MANAGEMENT_TYPE_ID = scient_managem_type.ID;

INSERT INTO scientific_management_type VALUES (1, 'Научные интересы');
INSERT INTO scientific_management_type VALUES (2, 'Проведенные семинары');
INSERT INTO scientific_management_type VALUES (3, 'Планируемые семинары');
INSERT INTO scientific_management_type VALUES (4, 'Членство (СМУ, СНО)');
INSERT INTO scientific_management_type VALUES (5, 'Патенты и заявки');
INSERT INTO scientific_management_type VALUES (6, 'Участие в проектах');
INSERT INTO scientific_management_type VALUES (7, 'Участие в конференциях');

INSERT INTO scientific_activity_type VALUES (1, 'Олимпиада');
INSERT INTO scientific_activity_type VALUES (2, 'Конкурс');
INSERT INTO scientific_activity_type VALUES (3, 'Проект');

INSERT INTO publication_type VALUES (1, 'Публикация с импакт-фактором');
INSERT INTO publication_type VALUES (2, 'Международные публикации');
INSERT INTO publication_type VALUES (3, 'Другие публикации');
INSERT INTO publication_type VALUES (4, 'Опубликованные книги');
INSERT INTO publication_type VALUES (5, 'Опубликованные методические и иные пособия');

INSERT INTO scientific_type VALUES (1, 'Публикации');
INSERT INTO scientific_type VALUES (2, 'Научная деятельность, интересы и направления');
INSERT INTO scientific_type VALUES (3, 'Научное руководство и кураторство');

CREATE SEQUENCE S_EMPLOYEE_SCIENTIFIC
MINVALUE 0
START WITH 1
NO CYCLE;

CREATE SEQUENCE s_previous_experience
MINVALUE 0
START WITH 1
NO CYCLE;

INSERT INTO employee_type VALUES (1, 'Администрированный сотрудник');
INSERT INTO employee_type VALUES (2, 'Преподаватель');

ALTER TABLE employee_dept
  ALTER COLUMN adviser TYPE BOOLEAN
  USING CASE WHEN adviser = 0
  THEN FALSE
        WHEN adviser = 1
          THEN TRUE
        ELSE NULL
        END;

CREATE OR REPLACE VIEW V_EMPLOYEE_DEPT AS
  SELECT
    empl_dept.ID,
    empl_dept.EMPLOYEE_ID,
    empl_dept.EMPLOYEE_TYPE_ID,
    empl_type.TYPE_NAME EMPLOYEE_TYPE_NAME,
    empl_dept.DEPT_ID,
    dep.DEPT_NAME,
    dep.DEPT_SHORT_NAME,
    empl_dept.POST_ID,
    post.POST_NAME,
    empl_dept.LIVE_LOAD,
    empl_dept.WAGE_RATE,
    empl_dept.RATE_LOAD,
    empl_dept.HOUR_COUNT,
    empl_dept.HIRE_DATE,
    empl_dept.DISMISS_DATE,
    empl_dept.ADVISER,
    empl_dept.PARENT_ID
  FROM EMPLOYEE_DEPT empl_dept INNER JOIN EMPLOYEE_TYPE empl_type ON empl_dept.EMPLOYEE_TYPE_ID = empl_type.ID
    INNER JOIN DEPARTMENT dep ON empl_dept.DEPT_ID = dep.ID
    LEFT JOIN POST post ON empl_dept.POST_ID = post.ID
    INNER JOIN EMPLOYEE empl ON empl_dept.EMPLOYEE_ID = empl.ID;

CREATE SEQUENCE s_employee_dept
MINVALUE 0
START WITH 1
NO CYCLE;

CREATE VIEW V_TEACHER_ROOM AS
  SELECT
    teacher_room.ID,
    teacher_room.TEACHER_ID,
    corpus.CORPUS_NAME,
    room.ROOM_NO,
    room_type.TYPE_NAME ROOM_TYPE_NAME,
    room.CAPACITY,
    room.EQUIPMENT,
    room.DESCR
  FROM TEACHER_ROOM teacher_room INNER JOIN ROOM room ON teacher_room.ROOM_ID = room.ID
    INNER JOIN CORPUS corpus ON room.CORPUS_ID = corpus.ID
    INNER JOIN ROOM_TYPE room_type ON room.ROOM_TYPE_ID = room_type.ID;

ALTER TABLE teacher_subject
  ALTER COLUMN LOAD_PER_HOURS TYPE BOOLEAN
  USING CASE WHEN LOAD_PER_HOURS = 0
  THEN FALSE
        WHEN LOAD_PER_HOURS = 1
          THEN TRUE
        ELSE NULL
        END;

CREATE OR REPLACE VIEW V_TEACHER_SUBJECT AS
  SELECT
    teacher_subject.ID,
    teacher_subject.EMPLOYEE_ID,
    subj.NAME_RU     SUBJECT_NAME,
    subj.CODE        SUBJECT_CODE,
    teacher_subject.GROUP_LEC_COUNT,
    gr_size_lec.SIZE GROUP_LEC_SIZE,
    teacher_subject.GROUP_LAB_COUNT,
    gr_size_lab.SIZE GROUP_LAB_SIZE,
    teacher_subject.GROUP_PRAC_COUNT,
    gr_size_pr.SIZE  GROUP_PRAC_SIZE,
    teacher_subject.FALL,
    teacher_subject.SPRING,
    teacher_subject.SUMMER,
    teacher_subject.LOAD_PER_HOURS
  FROM TEACHER_SUBJECT teacher_subject INNER JOIN SUBJECT subj ON teacher_subject.SUBJECT_ID = subj.ID
    LEFT JOIN GROUP_SIZE_LECTURE gr_size_lec ON teacher_subject.GROUP_LEC_ID = gr_size_lec.ID
    LEFT JOIN GROUP_SIZE_LAB gr_size_lab ON teacher_subject.GROUP_LAB_ID = gr_size_lab.ID
    LEFT JOIN GROUP_SIZE_PRAC gr_size_pr ON teacher_subject.GROUP_PRAC_ID = gr_size_pr.ID;

INSERT INTO day_hour VALUES (1, 8.00, 9.00, '08:00-09:00');
INSERT INTO day_hour VALUES (2, 09.00, 10.00, '09:00-10:00');
INSERT INTO day_hour VALUES (3, 10.00, 11.00, '10:00-11:00');
INSERT INTO day_hour VALUES (4, 11.00, 12.00, '11:00-12:00');
INSERT INTO day_hour VALUES (5, 12.00, 13.00, '12:00-13:00');
INSERT INTO day_hour VALUES (6, 13.00, 14.00, '13:00-14:00');
INSERT INTO day_hour VALUES (7, 14.00, 15.00, '14:00-15:00');
INSERT INTO day_hour VALUES (8, 15.00, 16.00, '15:00-16:00');
INSERT INTO day_hour VALUES (9, 16.00, 17.00, '16:00-17:00');
INSERT INTO day_hour VALUES (10, 17.00, 18.00, '17:00-18:00');
INSERT INTO day_hour VALUES (11, 18.00, 19.00, '18:00-19:00');
INSERT INTO day_hour VALUES (12, 19.00, 20.00, '19:00-20:00');
INSERT INTO day_hour VALUES (13, 20.00, 21.00, '20:00-21:00');
INSERT INTO day_hour VALUES (14, 21.00, 22.00, '21:00-22:00');

INSERT INTO week_day VALUES (1, 'Понедельник', 'Пн', 'Дүйсенбі', 'Дүй', 'Monday', 'Mon');
INSERT INTO week_day VALUES (2, 'Вторник', 'Вт', 'Сейсенбі', 'Сей', 'Tuesday', 'Tue');
INSERT INTO week_day VALUES (3, 'Среда', 'Ср', 'Сәрсенбі', 'Сәр', 'Wednesday', 'Wed');
INSERT INTO week_day VALUES (4, 'Четверг', 'Чт', 'Бейсенбі', 'Бей', 'Thursday', 'Thu');
INSERT INTO week_day VALUES (5, 'Пятница', 'Пт', 'Жұма', 'Жұма', 'Friday', 'Fri');
INSERT INTO week_day VALUES (6, 'Суббота', 'Сб', 'Сенбі', 'Сб', 'Saturday', 'Sat');
INSERT INTO week_day VALUES (7, 'Воскресенье', 'Вс', 'Жексенбі', 'Жекс', 'Sunday', 'Sun');

CREATE SEQUENCE S_EMPLOYEE_WORK_HOUR
MINVALUE 0
START WITH 1
NO CYCLE;

INSERT into work_hour_status VALUES (1, 'Доступно');
INSERT into work_hour_status VALUES (2, 'Не доступно');

CREATE VIEW V_ACADEMIC_DEGREE AS
  SELECT
    academ_degree.ID,
    academ_degree.SPECIALITY_ID,
    spec.SPEC_NAME,
    academ_degree.DEGREE_NAME,
    academ_degree.STUDY_PERIOD
  FROM ACADEMIC_DEGREE academ_degree INNER JOIN SPECIALITY spec ON academ_degree.SPECIALITY_ID = spec.ID;
