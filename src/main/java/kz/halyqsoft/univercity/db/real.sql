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
