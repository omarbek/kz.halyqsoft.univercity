INSERT INTO TASKS (CLASS_PATH, DESCR, ICON_NAME, NAME, TASK_ORDER, TASK_TYPE, TITLE, VISIBLE, ID, PARENT_ID)
VALUES ('kz.halyqsoft.univercity.modules.dorm.DormView', 'KK=Общага;RU=Общага;EN=Dorm;', null, 'KK=Общага;RU=Общага;EN=Dorm;',
        221, false, 'KK=Общага;RU=Общага;EN=Dorm;', true, nextval('s_tasks'), 3);

CREATE TABLE COMPLAINT
(
  ID                BIGINT PRIMARY KEY NOT NULL,
  USER_ID           BIGINT             NOT NULL,
  SHORT_DESCRIPTION VARCHAR(50)        NOT NULL,
  DESCRIPTION       VARCHAR(2048)      NOT NULL,
  CREATE_DATE       DATE               NOT NULL,
  CONSTRAINT FK_T_COMPLAINT_USER
  FOREIGN KEY (USER_ID)
  REFERENCES USERS (ID)
  ON DELETE RESTRICT
  ON UPDATE RESTRICT
);

CREATE SEQUENCE S_COMPLAINT
MINVALUE 0
START WITH 1
NO CYCLE;

ALTER TABLE DORM
  DROP COLUMN ROOM_COUNT;
ALTER TABLE DORM
  DROP COLUMN BED_COUNT;

CREATE SEQUENCE S_DORM
MINVALUE 0
START WITH 1
NO CYCLE;

ALTER TABLE DORM_STUDENT_VIOLATION
  ADD COLUMN evicted NUMERIC(1);
UPDATE DORM_STUDENT_VIOLATION
SET evicted = 0;
ALTER TABLE DORM_STUDENT_VIOLATION
  ALTER COLUMN evicted SET NOT NULL;

ALTER TABLE dorm
  ALTER COLUMN DELETED TYPE BOOLEAN
  USING CASE WHEN DELETED = 0
  THEN FALSE
        WHEN DELETED = 1
          THEN TRUE
        ELSE NULL
        END;

ALTER TABLE dorm_student
  ALTER COLUMN DELETED TYPE BOOLEAN
  USING CASE WHEN DELETED = 0
  THEN FALSE
        WHEN DELETED = 1
          THEN TRUE
        ELSE NULL
        END;

ALTER TABLE dorm_room
  ALTER COLUMN DELETED TYPE BOOLEAN
  USING CASE WHEN DELETED = 0
  THEN FALSE
        WHEN DELETED = 1
          THEN TRUE
        ELSE NULL
        END;

ALTER TABLE pdf_document DROP COLUMN file_byte;
ALTER TABLE document ADD COLUMN file_byte bytea NOT NULL;

CREATE SEQUENCE S_DORM_RULE_VIOLATION_TYPE
MINVALUE 0
START WITH 1
NO CYCLE;

INSERT INTO DORM_RULE_VIOLATION_TYPE VALUES (nextval('s_dorm_rule_violation_type'), 'Несанкционированный ночлег');
INSERT INTO DORM_RULE_VIOLATION_TYPE VALUES (nextval('s_dorm_rule_violation_type'), 'Нарушение ночного режима');
INSERT INTO DORM_RULE_VIOLATION_TYPE VALUES (nextval('s_dorm_rule_violation_type'), 'Распитие спиртных напитков');
INSERT INTO DORM_RULE_VIOLATION_TYPE
VALUES (nextval('s_dorm_rule_violation_type'), 'Антисанитария в жилой комнате и техпомещениях');
INSERT INTO DORM_RULE_VIOLATION_TYPE VALUES (nextval('s_dorm_rule_violation_type'), 'Нанесение материального ущерба');
INSERT INTO DORM_RULE_VIOLATION_TYPE VALUES (nextval('s_dorm_rule_violation_type'), 'Антисанитария на кухне');
INSERT INTO DORM_RULE_VIOLATION_TYPE
VALUES (nextval('s_dorm_rule_violation_type'), 'Грубое отношение к работникам ДМиС');
INSERT INTO DORM_RULE_VIOLATION_TYPE VALUES (nextval('s_dorm_rule_violation_type'), 'Курение');
INSERT INTO DORM_RULE_VIOLATION_TYPE
VALUES (nextval('s_dorm_rule_violation_type'), 'Нарушение правил проживания в ДМиС');

CREATE SEQUENCE S_DORM_ROOM
MINVALUE 0
START WITH 1
NO CYCLE;

CREATE SEQUENCE S_DORM_STUDENT
MINVALUE 0
START WITH 1
NO CYCLE;

CREATE SEQUENCE S_DORM_STUDENT_VIOLATION
MINVALUE 0
START WITH 1
NO CYCLE;

ALTER TABLE DORM_STUDENT_VIOLATION
  ALTER COLUMN evicted TYPE BOOLEAN
  USING CASE WHEN evicted = 0
  THEN FALSE
        WHEN evicted = 1
          THEN TRUE
        ELSE NULL
        END;

INSERT INTO TASKS (CLASS_PATH, DESCR, ICON_NAME, NAME, TASK_ORDER, TASK_TYPE, TITLE, VISIBLE, ID, PARENT_ID)
VALUES ('kz.halyqsoft.univercity.modules.finance.FinanceView', 'KK=Биллинг;RU=Биллинг;EN=Billing;', NULL,
        'KK=Биллинг;RU=Биллинг;EN=Billing;', 222, FALSE, 'KK=Биллинг;RU=Биллинг;EN=Billing;', TRUE, nextval('s_tasks'),
        3);

ALTER TABLE student_fin_debt
  ALTER COLUMN retake TYPE BOOLEAN
  USING CASE WHEN retake = 0
  THEN FALSE
        WHEN retake = 1
          THEN TRUE
        ELSE NULL
        END;

CREATE SEQUENCE s_student_payment
MINVALUE 0
START WITH 1
NO CYCLE;

CREATE SEQUENCE s_student_fin_debt
MINVALUE 0
START WITH 1
NO CYCLE;

CREATE TABLE device (
  id      BIGINT                NOT NULL,
  name    CHARACTER VARYING(64) NOT NULL
);

ALTER TABLE device
  ADD CONSTRAINT pk_device PRIMARY KEY (id);

CREATE SEQUENCE S_DEVICE
MINVALUE 0
START WITH 1
NO CYCLE;

ALTER TABLE room
  ADD device_id BIGINT;

ALTER TABLE room
  ADD CONSTRAINT fk_room_device FOREIGN KEY (device_id)
REFERENCES device (id)
ON DELETE RESTRICT ON UPDATE RESTRICT;

CREATE UNIQUE INDEX idx_room_device
  ON room (
    device_id ASC
  );

CREATE SEQUENCE S_LESSON
MINVALUE 0
START WITH 1
NO CYCLE;

ALTER TABLE lesson
  ALTER COLUMN canceled TYPE BOOLEAN
  USING CASE WHEN canceled = 0
  THEN FALSE
        WHEN canceled = 1
          THEN TRUE
        ELSE NULL
        END;

CREATE SEQUENCE S_LESSON_DETAIL
MINVALUE 0
START WITH 1
NO CYCLE;