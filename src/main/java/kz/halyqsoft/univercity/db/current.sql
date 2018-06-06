INSERT INTO study_direct VALUES (1, 'Математическое', 'Мат');
INSERT INTO study_direct VALUES (2, 'Художественное', 'Худ');
INSERT INTO study_direct VALUES (3, 'Литературный', 'Лит');

INSERT INTO subject_cycle VALUES (1, 'Цикл общеобразовательных дисциплин', 'ООД');
INSERT INTO subject_cycle VALUES (2, 'Цикл базовых дисциплин', 'БД');
INSERT INTO subject_cycle VALUES (3, 'Цикл профилирующих дисциплин', 'ПД');
INSERT INTO subject_cycle VALUES (4, 'Дополнительные виды обучения', 'ДВО');

INSERT INTO creditability VALUES (1, 1);
INSERT INTO creditability VALUES (2, 2);
INSERT INTO creditability VALUES (3, 3);
INSERT INTO creditability VALUES (4, 4);
INSERT INTO creditability VALUES (5, 5);

INSERT INTO academic_formula VALUES (1, 3, '1/1/1', 1, 1, 1);

INSERT INTO control_type VALUES (1, 'Экзамен');
INSERT INTO control_type VALUES (2, 'Зачет');
INSERT INTO control_type VALUES (3, 'Тест');

CREATE SEQUENCE S_SUBJECT
MINVALUE 0
START WITH 1
NO CYCLE;

ALTER TABLE subject
  ALTER COLUMN deleted TYPE BOOLEAN
  USING CASE WHEN deleted = 0
  THEN FALSE
        WHEN deleted = 1
          THEN TRUE
        ELSE NULL
        END;

ALTER TABLE subject
  ALTER COLUMN lang_en TYPE BOOLEAN
  USING CASE WHEN lang_en = 0
  THEN FALSE
        WHEN lang_en = 1
          THEN TRUE
        ELSE NULL
        END;

ALTER TABLE subject
  ALTER COLUMN lang_ru TYPE BOOLEAN
  USING CASE WHEN lang_ru = 0
  THEN FALSE
        WHEN lang_ru = 1
          THEN TRUE
        ELSE NULL
        END;

ALTER TABLE subject
  ALTER COLUMN lang_kz TYPE BOOLEAN
  USING CASE WHEN lang_kz = 0
  THEN FALSE
        WHEN lang_kz = 1
          THEN TRUE
        ELSE NULL
        END;

ALTER TABLE subject
  ALTER COLUMN mandatory TYPE BOOLEAN
  USING CASE WHEN mandatory = 0
  THEN FALSE
        WHEN mandatory = 1
          THEN TRUE
        ELSE NULL
        END;

ALTER TABLE curriculum_detail
  ALTER COLUMN deleted TYPE BOOLEAN
  USING CASE WHEN deleted = 0
  THEN FALSE
        WHEN deleted = 1
          THEN TRUE
        ELSE NULL
        END;

ALTER TABLE CURRICULUM_ADD_PROGRAM
  ALTER COLUMN deleted TYPE BOOLEAN
  USING CASE WHEN deleted = 0
  THEN FALSE
        WHEN deleted = 1
          THEN TRUE
        ELSE NULL
        END;

ALTER TABLE ELECTIVE_SUBJECT
  ALTER COLUMN deleted TYPE BOOLEAN
  USING CASE WHEN deleted = 0
  THEN FALSE
        WHEN deleted = 1
          THEN TRUE
        ELSE NULL
        END;

ALTER TABLE CURRICULUM_SUMMER
  ALTER COLUMN deleted TYPE BOOLEAN
  USING CASE WHEN deleted = 0
  THEN FALSE
        WHEN deleted = 1
          THEN TRUE
        ELSE NULL
        END;

ALTER TABLE CURRICULUM_SUMMER_DETAIL
  ALTER COLUMN deleted TYPE BOOLEAN
  USING CASE WHEN deleted = 0
  THEN FALSE
        WHEN deleted = 1
          THEN TRUE
        ELSE NULL
        END;

CREATE OR REPLACE VIEW V_SUBJECT_SELECT AS
  SELECT
    subj.ID,
    subj.NAME_RU,
    subj.CODE,
    subj.STUDY_DIRECT_ID,
    subj.CHAIR_ID,
    subj.LEVEL_ID,
    subj.SUBJECT_CYCLE_ID,
    subj.MANDATORY,
    subj.CREDITABILITY_ID,
    cred.CREDIT,
    contr_type.TYPE_NAME CONTROL_TYPE_NAME
  FROM SUBJECT subj INNER JOIN CREDITABILITY cred ON subj.CREDITABILITY_ID = cred.ID
    INNER JOIN CONTROL_TYPE contr_type ON subj.CONTROL_TYPE_ID = contr_type.ID
  WHERE subj.DELETED = FALSE
        AND subj.CHAIR_ID IS NOT NULL;

CREATE SEQUENCE S_SUBJECT_REQUISITE
MINVALUE 0
START WITH 1
NO CYCLE;

ALTER TABLE subject_requisite
  ALTER COLUMN pre_requisite TYPE BOOLEAN
  USING CASE WHEN pre_requisite = 0
  THEN FALSE
        WHEN pre_requisite = 1
          THEN TRUE
        ELSE NULL
        END;

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
      ON empl.ID = usr.ID AND empl.EMPLOYEE_STATUS_ID = 1 AND usr.LOCKED = FALSE
    INNER JOIN EMPLOYEE_DEPT empl_dept
      ON empl.ID = empl_dept.EMPLOYEE_ID AND empl_dept.EMPLOYEE_TYPE_ID = 2
         AND empl_dept.DISMISS_DATE IS NULL AND empl_dept.adviser = TRUE
    INNER JOIN DEPARTMENT dep ON empl_dept.DEPT_ID = dep.ID AND dep.DELETED = FALSE
    LEFT JOIN POST post ON empl_dept.POST_ID = post.ID;

ALTER TABLE teacher_subject
  ALTER COLUMN group_lab_count DROP NOT NULL;

ALTER TABLE teacher_subject
  ALTER COLUMN group_prac_count DROP NOT NULL;

ALTER TABLE teacher_subject
  ALTER COLUMN group_lec_count DROP NOT NULL;

DROP VIEW V_TEACHER_SUBJECT;

ALTER TABLE teacher_subject
  ALTER COLUMN fall TYPE BOOLEAN
  USING CASE WHEN fall = 0
  THEN FALSE
        WHEN fall = 1
          THEN TRUE
        ELSE NULL
        END;

ALTER TABLE teacher_subject
  ALTER COLUMN spring TYPE BOOLEAN
  USING CASE WHEN spring = 0
  THEN FALSE
        WHEN spring = 1
          THEN TRUE
        ELSE NULL
        END;

ALTER TABLE teacher_subject
  ALTER COLUMN summer TYPE BOOLEAN
  USING CASE WHEN summer = 0
  THEN FALSE
        WHEN summer = 1
          THEN TRUE
        ELSE NULL
        END;

CREATE OR REPLACE VIEW V_TEACHER_SUBJECT AS
  SELECT
    teacher_subject.ID,
    teacher_subject.EMPLOYEE_ID,
    subj.NAME_RU SUBJECT_NAME,
    subj.CODE    SUBJECT_CODE,
    teacher_subject.FALL,
    teacher_subject.SPRING,
    teacher_subject.SUMMER,
    teacher_subject.LOAD_PER_HOURS
  FROM TEACHER_SUBJECT teacher_subject INNER JOIN SUBJECT subj ON teacher_subject.SUBJECT_ID = subj.ID;

CREATE SEQUENCE S_TEACHER_SUBJECT
MINVALUE 0
START WITH 1
NO CYCLE;

CREATE SEQUENCE S_GRADUATE_STUDENT_LOAD
MINVALUE 0
START WITH 1
NO CYCLE;

ALTER TABLE room
  ALTER COLUMN deleted TYPE BOOLEAN
  USING CASE WHEN deleted = 0
  THEN FALSE
        WHEN deleted = 1
          THEN TRUE
        ELSE NULL
        END;

CREATE OR REPLACE VIEW V_ROOM AS
  SELECT
    room.ID,
    room.CORPUS_ID,
    corpus.CORPUS_NAME,
    room.ROOM_NO,
    room.ROOM_TYPE_ID,
    room_type.TYPE_NAME                                                                  ROOM_TYPE_NAME,
    room.CAPACITY,
    room.EQUIPMENT,
    room.RESP_EMP_ID,
    trim(usr.LAST_NAME || ' ' || usr.FIRST_NAME || ' ' || coalesce(usr.MIDDLE_NAME, '')) RESP_EMP_FIO,
    room.DESCR,
    room.DELETED,
    room.CREATED,
    room.UPDATED
  FROM ROOM room INNER JOIN CORPUS corpus ON room.CORPUS_ID = corpus.ID
    INNER JOIN ROOM_TYPE room_type ON room.ROOM_TYPE_ID = room_type.ID
    LEFT JOIN USERS usr ON room.RESP_EMP_ID = usr.ID;

CREATE OR REPLACE VIEW V_ROOM_DEPARTMENT AS
  SELECT
    room_dept.ID,
    room_dept.ROOM_ID,
    room_dept.DEPARTMENT_ID,
    dept.DEPT_NAME
  FROM ROOM_DEPARTMENT room_dept INNER JOIN DEPARTMENT dept ON room_dept.DEPARTMENT_ID = dept.ID;

CREATE VIEW V_ROOM_EQUIPMENT AS
  SELECT
    room_equip.ID,
    room_equip.ROOM_ID,
    room_equip.EQUIPMENT_ID,
    equip.EQUIPMENT_NAME
  FROM ROOM_EQUIPMENT room_equip INNER JOIN EQUIPMENT equip ON room_equip.EQUIPMENT_ID = equip.ID;

INSERT INTO room_type VALUES (1, 'Лекционный');
INSERT INTO room_type VALUES (2, 'Лабораторный');
INSERT INTO room_type VALUES (3, 'Практичный');

CREATE SEQUENCE s_room
MINVALUE 0
START WITH 1
NO CYCLE;

CREATE SEQUENCE s_room_work_hour
MINVALUE 0
START WITH 1
NO CYCLE;

CREATE OR REPLACE VIEW V_ROOM_SUBJECT AS
  SELECT
    room_subj.ID,
    room_subj.ROOM_ID,
    room_subj.SUBJECT_ID,
    subj.NAME_RU           SUBJECT_NAME_RU,
    subj.CODE              SUBJECT_CODE,
    dept.DEPT_NAME         CHAIR_NAME,
    lvl.LEVEL_NAME,
    cred.CREDIT,
    control_type.TYPE_NAME CONTROL_TYPE_NAME
  FROM ROOM_SUBJECT room_subj INNER JOIN SUBJECT subj ON room_subj.SUBJECT_ID = subj.ID
    INNER JOIN DEPARTMENT dept ON subj.CHAIR_ID = dept.ID
    INNER JOIN LEVEL lvl ON subj.LEVEL_ID = lvl.ID
    INNER JOIN CREDITABILITY cred ON subj.CREDITABILITY_ID = cred.ID
    INNER JOIN CONTROL_TYPE control_type ON subj.CONTROL_TYPE_ID = control_type.ID;

CREATE SEQUENCE s_room_subject
MINVALUE 0
START WITH 1
NO CYCLE;

CREATE SEQUENCE S_ROOM_EQUIPMENT
MINVALUE 0
START WITH 1
NO CYCLE;

CREATE SEQUENCE S_ROOM_DEPARTMENT
MINVALUE 0
START WITH 1
NO CYCLE;

CREATE SEQUENCE S_TEACHER_ROOM
MINVALUE 0
START WITH 1
NO CYCLE;