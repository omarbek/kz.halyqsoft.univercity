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