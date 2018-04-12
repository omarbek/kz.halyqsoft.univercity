INSERT INTO student_education_type VALUES (1, 'Договор');
INSERT INTO student_education_type VALUES (2, 'Грант');
INSERT INTO student_education_type VALUES (3, 'Внутренний грант');
INSERT INTO student_education_type VALUES (4, 'Кредит');

INSERT INTO student_status VALUES (2, 'Выпущен');
INSERT INTO student_status VALUES (3, 'Отчислен');
INSERT INTO student_status VALUES (4, 'Переведен на другой факультет/специальность');

INSERT into study_year VALUES (1,1);
INSERT into study_year VALUES (2,2);
INSERT into study_year VALUES (3,3);
INSERT into study_year VALUES (4,4);

create sequence s_student_education
minvalue 0
start with 1
no cycle;

ALTER TABLE STUDENT_FIN_DEBT
  ALTER COLUMN DELETED TYPE BOOLEAN
  USING CASE WHEN DELETED = 0
  THEN FALSE
        WHEN DELETED = 1
          THEN TRUE
        ELSE NULL
        END;

create or replace view V_ORDER_DOC as
  select order_doc.ID,
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
  from  ORDER_DOC order_doc inner join  USER_DOCUMENT usr_doc on order_doc.ID = usr_doc.ID
    inner join  ORDER_TYPE order_type on order_doc.ORDER_TYPE_ID = order_type.ID
    left join  STUDY_YEAR study_year on order_doc.STUDY_YEAR_ID = study_year.ID;

create sequence S_USER_PHOTO
minvalue 0
start with 1
no cycle;

insert into academic_status VALUES (1, 'Полный');
insert into academic_status VALUES (2, 'Условный');

ALTER TABLE lock_reason drop COLUMN lock_type;

ALTER TABLE lock_reason ADD COLUMN USER_TYPE_ID bigint NOT NULL DEFAULT 2;
ALTER TABLE ONLY lock_reason
  ADD CONSTRAINT fk_lock_reason_user_type FOREIGN KEY (USER_TYPE_ID) REFERENCES user_type(id) ON UPDATE RESTRICT ON DELETE RESTRICT;

create sequence S_LOCK_REASON
minvalue 0
start with 1
no cycle;

ALTER TABLE users ADD COLUMN updated TIMESTAMP;
ALTER TABLE users ADD COLUMN UPDATED_BY VARCHAR(255);

INSERT into student_status VALUES (5, 'Академический отпуск');
INSERT into student_status VALUES (6, 'Переведен на следующий год');
INSERT into student_status VALUES (7, 'Переводен на другой вид обучения');
INSERT into student_status VALUES (8, 'Переводен на другой язык обучения');

insert into order_type values(3, 'Приказ о переводе');
insert into order_type values(4, 'Приказ об академическом отпуске');
insert into order_type values(5, 'Приказ о восстановлении');
insert into order_type values(7, 'Приказ о выпуске');
insert into order_type values(8, 'Приказ об очислении');

INSERT INTO document_type VALUES (10, 'Приказ об обучении');

drop VIEW v_order_doc;

ALTER TABLE order_doc
  ALTER COLUMN hide_transcript TYPE BOOLEAN
  USING CASE WHEN hide_transcript = 0
  THEN FALSE
        WHEN hide_transcript = 1
          THEN TRUE
        ELSE NULL
        END;

create or replace view V_ORDER_DOC as
  select order_doc.ID,
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
  from  ORDER_DOC order_doc inner join  USER_DOCUMENT usr_doc on order_doc.ID = usr_doc.ID
    inner join  ORDER_TYPE order_type on order_doc.ORDER_TYPE_ID = order_type.ID
    left join  STUDY_YEAR study_year on order_doc.STUDY_YEAR_ID = study_year.ID;

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

INSERT into document_type VALUES (12, 'Научная степень');

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

INSERT into scientific_management_type VALUES (1, 'Научные интересы');
INSERT into scientific_management_type VALUES (2, 'Проведенные семинары');
INSERT into scientific_management_type VALUES (3, 'Планируемые семинары');
INSERT into scientific_management_type VALUES (4, 'Членство (СМУ, СНО)');
INSERT into scientific_management_type VALUES (5, 'Патенты и заявки');
INSERT into scientific_management_type VALUES (6, 'Участие в проектах');
INSERT into scientific_management_type VALUES (7, 'Участие в конференциях');

INSERT into scientific_activity_type values (1, 'Олимпиада');
INSERT into scientific_activity_type values (2, 'Конкурс');
INSERT into scientific_activity_type values (3, 'Проект');

insert into publication_type VALUES (1, 'Публикация с импакт-фактором');
insert into publication_type VALUES (2, 'Международные публикации');
insert into publication_type VALUES (3, 'Другие публикации');
insert into publication_type VALUES (4, 'Опубликованные книги');
insert into publication_type VALUES (5, 'Опубликованные методические и иные пособия');

INSERT into scientific_type VALUES (1, 'Публикации');
INSERT into scientific_type VALUES (2, 'Научная деятельность, интересы и направления');
INSERT into scientific_type VALUES (3, 'Научное руководство и кураторство');

create sequence S_EMPLOYEE_SCIENTIFIC
minvalue 0
start with 1
no cycle;

create sequence s_previous_experience
minvalue 0
start with 1
no cycle;

INSERT into employee_type VALUES (1, 'Администрированный сотрудник');
INSERT into employee_type VALUES (2, 'Преподаватель');

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

create sequence s_employee_dept
minvalue 0
start with 1
no cycle;