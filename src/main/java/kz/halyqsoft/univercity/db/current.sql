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

INSERT INTO academic_calendar_faculty VALUES (1, 'Все');

CREATE SEQUENCE s_academic_calendar
MINVALUE 0
START WITH 1
NO CYCLE;

INSERT INTO ACADEMIC_CALENDAR_ITEM VALUES
  (1, 1, NULL, 'Диагностическое тестирование по языкам для студентов 1 года обучения всех специальностей', 'a', FALSE);
INSERT INTO ACADEMIC_CALENDAR_ITEM VALUES (2, 1, NULL, 'Регистрация для студентов 1 года обучения', 'b', FALSE);
INSERT INTO ACADEMIC_CALENDAR_ITEM
VALUES (3, 1, NULL, 'Формирование расписания учебных заведений на осенний семестр %1$s учебного года', 'c', FALSE);
INSERT INTO ACADEMIC_CALENDAR_ITEM
VALUES (4, 1, NULL, 'Регистрация на дисциплины весенного семестра для студентов 1 го:.', 'd', FALSE);
INSERT INTO ACADEMIC_CALENDAR_ITEM
VALUES (5, 1, NULL, 'Формирование рассмотрения учебных занятий на весенний семестр %1$s учебного года', 'e', FALSE);
INSERT INTO ACADEMIC_CALENDAR_ITEM
VALUES (6, 1, NULL, 'Регистрация на дисциплины на весеннего и осеннего семестров %1$s учебного года', 'f', FALSE);
INSERT INTO ACADEMIC_CALENDAR_ITEM VALUES (7, 1, NULL,
                                           'Ориентационный период и библиотечно-библиографические и информационные занятия для студентов 1 года обучения',
                                           'h', FALSE);
INSERT INTO ACADEMIC_CALENDAR_ITEM
VALUES (8, 1, NULL, 'Осенний семестр 1-7 год обучения (все специальности) (15 недель)', 'i', FALSE);
INSERT INTO ACADEMIC_CALENDAR_ITEM
VALUES (9, 1, NULL, 'Весенний семестр 1-7 год обучения (все специальности) (15 недель)', 'j', FALSE);
INSERT INTO ACADEMIC_CALENDAR_ITEM VALUES (10, 1, NULL, '-Осеннего семестра', 'k', FALSE);
INSERT INTO ACADEMIC_CALENDAR_ITEM VALUES (11, 1, NULL, '-Весеннего семестра', 'l', FALSE);
INSERT INTO ACADEMIC_CALENDAR_ITEM VALUES (12, 1, NULL, 'Зимние: (15 дней)', 'm', FALSE);
INSERT INTO ACADEMIC_CALENDAR_ITEM VALUES (13, 1, NULL, '1 год обучения (11 недель)', 'n', FALSE);
INSERT INTO ACADEMIC_CALENDAR_ITEM VALUES (14, 1, NULL, '2 год обучения (11 недель)', 'o', FALSE);
INSERT INTO ACADEMIC_CALENDAR_ITEM VALUES (15, 1, NULL, '3 год обучения (10 недель)', 'p', FALSE);
INSERT INTO ACADEMIC_CALENDAR_ITEM VALUES (16, 1, NULL, 'ВОУД для студентов выпускных годов обучения', 'q', FALSE);
INSERT INTO ACADEMIC_CALENDAR_ITEM VALUES (17, 1, NULL, 'Пробное тестирование ВОУД', 'r', FALSE);
INSERT INTO ACADEMIC_CALENDAR_ITEM VALUES (18, 1, NULL, '- 4 год обучения (в течение 4-х недель)', 's', FALSE);
INSERT INTO ACADEMIC_CALENDAR_ITEM VALUES (19, 1, NULL, '- 5, 6, 7 годы обучения (4 недели)', 't', FALSE);
INSERT INTO ACADEMIC_CALENDAR_ITEM VALUES (20, 1, NULL, '- 4 год обучения', 'u', FALSE);
INSERT INTO ACADEMIC_CALENDAR_ITEM VALUES (21, 1, NULL, '– 5, 6, 7 годы обучения (1 неделя)', 'v', FALSE);
INSERT INTO ACADEMIC_CALENDAR_ITEM VALUES (22, 1, NULL, '– 4 год обучения ', 'w', FALSE);
INSERT INTO ACADEMIC_CALENDAR_ITEM VALUES (23, 1, NULL, '– 5, 6, 7 год обучения', 'x', FALSE);
INSERT INTO ACADEMIC_CALENDAR_ITEM VALUES (24, 1, NULL, '– 4 год обучения', 'y', FALSE);
INSERT INTO ACADEMIC_CALENDAR_ITEM VALUES (25, 1, NULL, '– 5, 6, 7 годы обучения', 'z', FALSE);
INSERT INTO ACADEMIC_CALENDAR_ITEM VALUES (26, 1, NULL, '«Ярмарка вакансий – %1$s»', '0', FALSE);
INSERT INTO ACADEMIC_CALENDAR_ITEM VALUES (27, 1, NULL,
                                           'Учебно-ознакомительная практика студентов 1 года обучения для всех специальностей (в течение 2-х недель)',
                                           '1', FALSE);
INSERT INTO ACADEMIC_CALENDAR_ITEM
VALUES (28, 1, NULL, 'Защита отчетов учебно-ознакомительной практики студентов 1 года обучения', '2', FALSE);
INSERT INTO ACADEMIC_CALENDAR_ITEM VALUES
  (29, 1, NULL, 'Учебно-производственная практика студентов 2 года обучения для всех специальностей (2 недели)', '3',
   FALSE);
INSERT INTO ACADEMIC_CALENDAR_ITEM
VALUES (30, 1, NULL, 'Защита отчетов учебно-производственной практики студентов 2 года обучения', '4', FALSE);
INSERT INTO ACADEMIC_CALENDAR_ITEM VALUES
  (31, 1, NULL, 'Производственная практика студентов 3 года обучения для всех специальностей (в течение 4-х недель)',
   '5', FALSE);
INSERT INTO ACADEMIC_CALENDAR_ITEM VALUES
  (32, 1, NULL, 'Защита отчетов производственной практики студентов 3 года обучения (в течение 2-х недель)', '6',
   FALSE);
INSERT INTO ACADEMIC_CALENDAR_ITEM
VALUES (33, 1, NULL, 'Регистрация на дисциплины Летней школы (4 недели)', '7', FALSE);
INSERT INTO ACADEMIC_CALENDAR_ITEM VALUES (34, 1, NULL, '1 модуль (не более 2-х дисциплин) (4 недели)', '8', FALSE);
INSERT INTO ACADEMIC_CALENDAR_ITEM VALUES (35, 1, NULL, '2 модуль (не более 2-х дисциплин) (4 недели)', '9', FALSE);
INSERT INTO ACADEMIC_CALENDAR_ITEM
VALUES (36, 1, NULL, '1 модуль (не более 3-х дисциплин профильного цикла) (6 недель)', '10', FALSE);
INSERT INTO ACADEMIC_CALENDAR_ITEM VALUES
  (37, 1, 1, 'Диагностическое тестирование по языкам для студентов 1 года обучения всех специальностей', '11', FALSE);
INSERT INTO ACADEMIC_CALENDAR_ITEM
VALUES (38, 1, 1, 'Регистрация на дисциплины для студентов 1 года обучения', '12', FALSE);
INSERT INTO ACADEMIC_CALENDAR_ITEM
VALUES (39, 1, 1, 'Формирование расписания учебных занятий на осенний семестр %1$s учебного года', '13', FALSE);
INSERT INTO ACADEMIC_CALENDAR_ITEM VALUES
  (40, 1, 1, 'Преддипломная практика студентов всех специальностей: 5, 6, 7 годы обучения (4 недели)', '14', FALSE);
INSERT INTO ACADEMIC_CALENDAR_ITEM VALUES (41, 1, 1,
                                           'Ориентационный период и библиотечно-библиографические и информационные занятия для студентов 1 года обучения',
                                           '15', FALSE);
INSERT INTO ACADEMIC_CALENDAR_ITEM
VALUES (42, 1, 1, 'Начало теоретического курса по дисциплинам осеннего семестра ', '16', FALSE);
INSERT INTO ACADEMIC_CALENDAR_ITEM
VALUES (43, 1, 1, 'Период перерегистрации (Add/Drop) (кроме студентов 1 года обучения)', '17', FALSE);
INSERT INTO ACADEMIC_CALENDAR_ITEM VALUES (44, 1, 1, 'Последняя регистрация на дисциплины (Audit)', '18', FALSE);
INSERT INTO ACADEMIC_CALENDAR_ITEM VALUES (45, 1, 1, 'День Конституции Республики Казахстан', '19', FALSE);
INSERT INTO ACADEMIC_CALENDAR_ITEM VALUES (46, 1, 1, 'Поздняя регистрация (платная)', '20', FALSE);
INSERT INTO ACADEMIC_CALENDAR_ITEM VALUES (47, 1, 1, 'Withdrawal (снятие/отказ от дисциплины)', '21', FALSE);
INSERT INTO ACADEMIC_CALENDAR_ITEM VALUES (48, 1, 1, 'Подписание индивидуальных учебных планов студентов', '22', FALSE);
INSERT INTO ACADEMIC_CALENDAR_ITEM
VALUES (49, 1, 1, 'Защита отчетов по преддипломной практике студентов: 5, 6, 7 годы обучения (1 неделя)', '23', FALSE);
INSERT INTO ACADEMIC_CALENDAR_ITEM
VALUES (50, 1, 1, 'Государственные экзамены по специальности: 5, 6, 7 годы обучения', '24', FALSE);
INSERT INTO ACADEMIC_CALENDAR_ITEM
VALUES (51, 1, 1, 'Подготовка и защита дипломных проектов: 5, 6, 7 годы обучения', '25', FALSE);
INSERT INTO ACADEMIC_CALENDAR_ITEM VALUES (52, 1, 1, 'ВОУД для студентов выпускных годов обучения', '26', FALSE);
INSERT INTO ACADEMIC_CALENDAR_ITEM VALUES (53, 1, 1, '1-ая Аттестационная неделя (8-ая неделя семестра)', '27', FALSE);
INSERT INTO ACADEMIC_CALENDAR_ITEM VALUES (54, 1, 1, 'Срок выставления результатов 1-ой аттестации', '28', FALSE);
INSERT INTO ACADEMIC_CALENDAR_ITEM VALUES
  (55, 1, 1, 'Регистрация на дисциплины весеннего семестра %1$s учебного года для студентов 1 годов обучения', '29',
   FALSE);
INSERT INTO ACADEMIC_CALENDAR_ITEM VALUES (56, 1, 1, '2-ая Аттестационная неделя (15-ая неделя семестра)', '30', FALSE);
INSERT INTO ACADEMIC_CALENDAR_ITEM VALUES (57, 1, 1, 'Срок выставления результатов 2-ой аттестации', '31', FALSE);
INSERT INTO ACADEMIC_CALENDAR_ITEM VALUES (58, 1, 1, 'День первого президента Республики Казахстан', '32', FALSE);
INSERT INTO ACADEMIC_CALENDAR_ITEM VALUES (59, 1, 1, 'Окончание теоретического курса осеннего семестра', '33', FALSE);
INSERT INTO ACADEMIC_CALENDAR_ITEM
VALUES (60, 1, 1, 'Экзаменационная сессия осеннего семестра (3 недели)', '34', FALSE);
INSERT INTO ACADEMIC_CALENDAR_ITEM VALUES (61, 1, 1, 'Срок выставления результатов экзаменов', '35', FALSE);
INSERT INTO ACADEMIC_CALENDAR_ITEM VALUES (62, 1, 1, 'День Независимости Республики Казахстан', '36', FALSE);
INSERT INTO ACADEMIC_CALENDAR_ITEM
VALUES (63, 1, 1, 'Формирование расписания учебных занятий на весенний семестр %1$s уч.года', '37', FALSE);
INSERT INTO ACADEMIC_CALENDAR_ITEM VALUES (64, 1, 1, 'Зимние каникулы (15 дней)', '38', FALSE);
INSERT INTO ACADEMIC_CALENDAR_ITEM
VALUES (65, 1, 2, 'Начало теоретического курса по дисциплинам весеннего семестра', '39', FALSE);
INSERT INTO ACADEMIC_CALENDAR_ITEM
VALUES (66, 1, 2, 'Период аппеляции результатов экзаменов осеннего семестра %1$s учебного года', '40', FALSE);
INSERT INTO ACADEMIC_CALENDAR_ITEM VALUES (67, 1, 2, 'Период перерегистрации (Add/Drop)', '41', FALSE);
INSERT INTO ACADEMIC_CALENDAR_ITEM VALUES (68, 1, 2, 'Последняя регистрация на дисциплины (Audit)', '42', FALSE);
INSERT INTO ACADEMIC_CALENDAR_ITEM VALUES (69, 1, 2, 'Поздняя регистрация (платная)', '43', FALSE);
INSERT INTO ACADEMIC_CALENDAR_ITEM VALUES (70, 1, 2, 'Withdrawal (снятие/отказ от дисциплины)', '44', FALSE);
INSERT INTO ACADEMIC_CALENDAR_ITEM VALUES (71, 1, 2, 'Подписание индивидуальных учебных планов студентов', '45', FALSE);
INSERT INTO ACADEMIC_CALENDAR_ITEM VALUES
  (72, 1, 2, 'Преддипломная практика студентов всех специальностей: 4 год обучения (в течение 4-х недель)', '46',
   FALSE);
INSERT INTO ACADEMIC_CALENDAR_ITEM
VALUES (73, 1, 2, 'Защита отчетов по преддипломной практике студентов: 4 год обучения', '47', FALSE);
INSERT INTO ACADEMIC_CALENDAR_ITEM
VALUES (74, 1, 2, 'Регистрация на дисциплины осеннего и весеннего семестров %1$s учебного года', '48', FALSE);
INSERT INTO ACADEMIC_CALENDAR_ITEM VALUES (75, 1, 2, '1-ая Аттестационная неделя (8-я неделя семестра)', '49', FALSE);
INSERT INTO ACADEMIC_CALENDAR_ITEM VALUES (76, 1, 2, 'Срок выставления результатов 1-ой аттестации', '50', FALSE);
INSERT INTO ACADEMIC_CALENDAR_ITEM VALUES (77, 1, 2, 'Международный женский день ', '51', FALSE);
INSERT INTO ACADEMIC_CALENDAR_ITEM
VALUES (78, 1, 2, 'Государственные экзамены по специальности:  4 год обучения', '52', FALSE);
INSERT INTO ACADEMIC_CALENDAR_ITEM VALUES (79, 1, 2, 'Наурыз мейрамы', '53', FALSE);
INSERT INTO ACADEMIC_CALENDAR_ITEM
VALUES (80, 1, 2, 'Подготовка и защита дипломных проектов: 4 год обучения (8 недель)', '54', FALSE);
INSERT INTO ACADEMIC_CALENDAR_ITEM VALUES (81, 1, 2, '«Ярмарка вакансий – %1$s»', '55', FALSE);
INSERT INTO ACADEMIC_CALENDAR_ITEM VALUES (82, 1, 2, '2-ая Аттестационная неделя (15-я неделя семестра)', '56', FALSE);
INSERT INTO ACADEMIC_CALENDAR_ITEM VALUES (83, 1, 2, 'Срок выставления результатов 2-ой аттестации', '57', FALSE);
INSERT INTO ACADEMIC_CALENDAR_ITEM VALUES (84, 1, 2, 'Окончание теоретического курса весеннего семестра', '58', FALSE);
INSERT INTO ACADEMIC_CALENDAR_ITEM VALUES (85, 1, 2, 'Экзаменационная сессия весеннего семестра', '59', FALSE);
INSERT INTO ACADEMIC_CALENDAR_ITEM VALUES (86, 1, 2, 'Праздник единства народа Казахстана', '60', FALSE);
INSERT INTO ACADEMIC_CALENDAR_ITEM VALUES (87, 1, 2, 'День защитника Отечества в Казахстане', '61', FALSE);
INSERT INTO ACADEMIC_CALENDAR_ITEM VALUES (88, 1, 2, 'День Победы', '62', FALSE);
INSERT INTO ACADEMIC_CALENDAR_ITEM VALUES (89, 1, 2, 'Срок выставления результатов экзаменов', '63', FALSE);
INSERT INTO ACADEMIC_CALENDAR_ITEM
VALUES (90, 1, 2, 'Период аппеляции результатов экзаменов весеннего семестра %1$s учебного года', '64', FALSE);
INSERT INTO ACADEMIC_CALENDAR_ITEM VALUES
  (91, 1, 2, 'Учебно-ознакомительная практика студентов 1 года обучения для всех специальностей (в течение 2-х недель)',
   '65', FALSE);
INSERT INTO ACADEMIC_CALENDAR_ITEM
VALUES (92, 1, 2, 'Защита отчетов учебно-ознакомительной практики студентов 1 года обучения', '66', FALSE);
INSERT INTO ACADEMIC_CALENDAR_ITEM VALUES
  (93, 1, 2, 'Учебно-производственная практика студентов 2 года обучения для всех специальностей (2 недели)', '67',
   FALSE);
INSERT INTO ACADEMIC_CALENDAR_ITEM
VALUES (94, 1, 2, 'Защита отчетов учебно-производственной практики студентов 2 года обучения', '68', FALSE);
INSERT INTO ACADEMIC_CALENDAR_ITEM VALUES
  (95, 1, 2, 'Производственная практика студентов 3 года обучения для всех специальностей (в течение 4-х недель)', '69',
   FALSE);
INSERT INTO ACADEMIC_CALENDAR_ITEM VALUES
  (96, 1, 2, 'Защита отчетов производственной практики студентов 3 года обучения (в течение 2-х недель)', '70', FALSE);
INSERT INTO ACADEMIC_CALENDAR_ITEM VALUES (97, 1, 2, 'Регистрация на дисциплины Летней школы (4 недели)', '71', FALSE);
INSERT INTO ACADEMIC_CALENDAR_ITEM VALUES (98, 1, 2, '1 модуль (не более 2-х дисциплин) (4 недели)', '72', FALSE);
INSERT INTO ACADEMIC_CALENDAR_ITEM VALUES (99, 1, 2, '2 модуль (не более 2-х дисциплин) (4 недели)', '73', FALSE);
INSERT INTO ACADEMIC_CALENDAR_ITEM VALUES (100, 1, 2, 'День Столицы', '74', FALSE);
INSERT INTO ACADEMIC_CALENDAR_ITEM VALUES (101, 1, 2, '1 год обучения  (11 недель)', '75', FALSE);
INSERT INTO ACADEMIC_CALENDAR_ITEM VALUES (102, 1, 2, '2 год обучения (11 недель)', '76', FALSE);
INSERT INTO ACADEMIC_CALENDAR_ITEM VALUES (103, 1, 2, '3 год обучения (10 недель)', '77', FALSE);

CREATE SEQUENCE s_academic_calendar_detail
MINVALUE 0
START WITH 1
NO CYCLE;

DROP INDEX idx_t_academic_calendar_detail;

CREATE UNIQUE INDEX idx_t_academic_calendar_detail
  ON academic_calendar_detail (
    ACADEMIC_CALENDAR_ID ASC,
    ITEM_ID ASC
  );

INSERT INTO employee VALUES (1, 1, FALSE, FALSE);
INSERT INTO employee VALUES (2, 1, FALSE, FALSE);

update tasks set parent_id=26 where id in (13,15,22);
update tasks set task_order=401 where id=13;
update tasks set task_order=402 where id=15;
update tasks set task_order=403 where id=22;

update tasks set parent_id=29 where id in (25,28);
update tasks set task_order=501 where id=25;
update tasks set task_order=502 where id=28;

insert into CURRICULUM_STATUS values (1, 'На создании');
insert into CURRICULUM_STATUS values (2, 'На согласовании');
insert into CURRICULUM_STATUS values (3, 'Утвержден');

ALTER TABLE curriculum_detail
  ALTER COLUMN consider_credit TYPE BOOLEAN
  USING CASE WHEN consider_credit = 0
  THEN FALSE
        WHEN consider_credit = 1
          THEN TRUE
        ELSE NULL
        END;

CREATE OR REPLACE VIEW V_CURRICULUM_DETAIL AS
  SELECT
    a.ID,
    a.CURRICULUM_ID,
    a.SEMESTER_ID,
    b.SEMESTER_NAME,
    a.SUBJECT_ID,
    c.CODE                      SUBJECT_CODE,
    c.NAME_RU                   SUBJECT_NAME,
    CASE WHEN c.SUBJECT_CYCLE_ID = 4
      THEN a.SUBJECT_CYCLE_ID
    ELSE c.SUBJECT_CYCLE_ID END SUBJECT_CYCLE_ID,
    CASE WHEN c.SUBJECT_CYCLE_ID = 4
      THEN i.CYCLE_SHORT_NAME
    ELSE d.CYCLE_SHORT_NAME END CYCLE_SHORT_NAME,
    c.CREDITABILITY_ID,
    e.CREDIT,
    c.ACADEMIC_FORMULA_ID,
    f.FORMULA,
    a.RECOMMENDED_SEMESTER,
    a.CONSIDER_CREDIT,
    c.CONTROL_TYPE_ID,
    h.TYPE_NAME                 CONTROL_TYPE_NAME,
    a.DELETED,
    FALSE                       ELECTIVE
  FROM CURRICULUM_DETAIL a INNER JOIN SEMESTER b ON a.SEMESTER_ID = b.ID
    INNER JOIN SUBJECT c ON a.SUBJECT_ID = c.ID
    INNER JOIN SUBJECT_CYCLE d ON c.SUBJECT_CYCLE_ID = d.ID
    INNER JOIN CREDITABILITY e ON c.CREDITABILITY_ID = e.ID
    INNER JOIN ACADEMIC_FORMULA f ON c.ACADEMIC_FORMULA_ID = f.ID
    INNER JOIN CONTROL_TYPE h ON c.CONTROL_TYPE_ID = h.ID
    LEFT JOIN SUBJECT_CYCLE i ON a.SUBJECT_CYCLE_ID = i.ID
  UNION ALL
  SELECT
    aa.ID,
    aa.CURRICULUM_ID,
    aa.SEMESTER_ID,
    bb.SEMESTER_NAME,
    aa.ELECTIVE_SUBJECT_ID       SUBJECT_ID,
    NULL                         SUBJECT_CODE,
    cc.NAME_RU                   SUBJECT_NAME,
    aa.ELECTIVE_SUBJECT_CYCLE_ID SUBJECT_CYCLE_ID,
    dd.CYCLE_SHORT_NAME,
    NULL                         CREDITABILITY_ID,
    aa.ELECTIVE_SUBJECT_CREDIT   CREDIT,
    NULL                         ACADEMIC_FORMULA_ID,
    NULL                         FORMULA,
    NULL                         RECOMMENDED_SEMESTER,
    aa.CONSIDER_CREDIT,
    NULL                         CONTROL_TYPE_ID,
    NULL                         CONTROL_TYPE_NAME,
    aa.DELETED,
    TRUE                         ELECTIVE
  FROM CURRICULUM_DETAIL aa INNER JOIN SEMESTER bb ON aa.SEMESTER_ID = bb.ID
    INNER JOIN ELECTIVE_SUBJECT_LABEL cc ON aa.ELECTIVE_SUBJECT_ID = cc.ID
    INNER JOIN SUBJECT_CYCLE dd ON aa.ELECTIVE_SUBJECT_CYCLE_ID = dd.ID;

CREATE OR REPLACE VIEW V_CURRICULUM_ADD_PROGRAM AS
  SELECT
    curr_add_pr.ID,
    curr_add_pr.CURRICULUM_ID,
    curr_add_pr.SUBJECT_ID,
    subj.NAME_RU SUBJECT_NAME_RU,
    subj.CODE    SUBJECT_CODE,
    subj.CREDITABILITY_ID,
    cred.CREDIT,
    curr_add_pr.SEMESTER_ID,
    sem.SEMESTER_NAME,
    curr_add_pr.DELETED
  FROM CURRICULUM_ADD_PROGRAM curr_add_pr INNER JOIN SUBJECT subj ON curr_add_pr.SUBJECT_ID = subj.ID
    INNER JOIN CREDITABILITY cred ON subj.CREDITABILITY_ID = cred.ID
    INNER JOIN SEMESTER sem ON curr_add_pr.SEMESTER_ID = sem.ID;

INSERT INTO MONTH VALUES (1, 'Январь', 'January', 'Қаңтар');
INSERT INTO MONTH VALUES (2, 'Февраль', 'February', 'Ақпан');
INSERT INTO MONTH VALUES (3, 'Март', 'March', 'Наурыз');
INSERT INTO MONTH VALUES (4, 'Апрель', 'April', 'Сәуір');
INSERT INTO MONTH VALUES (5, 'Май', 'May', 'Мамыр');
INSERT INTO MONTH VALUES (6, 'Июнь', 'June', 'Маусым');
INSERT INTO MONTH VALUES (7, 'Июль', 'July', 'Шілде');
INSERT INTO MONTH VALUES (8, 'Август', 'August', 'Тамыз');
INSERT INTO MONTH VALUES (9, 'Сентябрь', 'September', 'Қыркүйек');
INSERT INTO MONTH VALUES (10, 'Октябрь', 'October', 'Қазан');
INSERT INTO MONTH VALUES (11, 'Ноябрь', 'November', 'Қараша');
INSERT INTO MONTH VALUES (12, 'Декабрь', 'December', 'Желтоксан');

insert into CURRICULUM_SCHEDULE_SYMBOL values (1, '+' ,'Теоретическое обучение с отрывом от производства');
insert into CURRICULUM_SCHEDULE_SYMBOL values (2, '::' ,'Экзаменационная сессия');
insert into CURRICULUM_SCHEDULE_SYMBOL values (3, 'о' ,'Вычислительная практика');
insert into CURRICULUM_SCHEDULE_SYMBOL values (4, 'х' ,'Производственная, преддипломная практики');
insert into CURRICULUM_SCHEDULE_SYMBOL values (5, 'П' ,'ПГК');
insert into CURRICULUM_SCHEDULE_SYMBOL values (6, 'Д' ,'Подготовка дипломного проекта');
insert into CURRICULUM_SCHEDULE_SYMBOL values (7, 'Г' ,'Государственные экзамены');
insert into CURRICULUM_SCHEDULE_SYMBOL values (8, '=' ,'Каникулы');
insert into CURRICULUM_SCHEDULE_SYMBOL values (9, 'З' ,'Защита дипломного проекта');

CREATE SEQUENCE S_CURRICULUM
MINVALUE 0
START WITH 1
NO CYCLE;

insert into SEMESTER_PERIOD values (4, 'Летний 2');
insert into SEMESTER_PERIOD values (5, 'Летний 3');
update SEMESTER_PERIOD set PERIOD_NAME = 'Летний 1' where ID = 3;

insert into SEMESTER values (1, 1, 1, '1 семестр');
insert into SEMESTER values (2, 1, 2, '2 семестр');
insert into SEMESTER values (3, 2, 1, '3 семестр');
insert into SEMESTER values (4, 2, 2, '4 семестр');
insert into SEMESTER values (5, 3, 1, '5 семестр');
insert into SEMESTER values (6, 3, 2, '6 семестр');
insert into SEMESTER values (7, 4, 1, '7 семестр');
insert into SEMESTER values (8, 4, 2, '8 семестр');

CREATE OR REPLACE VIEW V_ELECTIVE_SUBJECT AS
  SELECT
    a.ID,
    a.CURRICULUM_ID,
    a.SEMESTER_ID,
    d.SEMESTER_NAME,
    a.SUBJECT_ID,
    c.CODE                      SUBJECT_CODE,
    c.NAME_RU                   SUBJECT_NAME,
    CASE WHEN c.subject_cycle_id = 4
      THEN a.subject_cycle_id
    ELSE c.subject_cycle_id END SUBJECT_CYCLE_ID,
    CASE WHEN c.subject_cycle_id = 4
      THEN i.CYCLE_SHORT_NAME
    ELSE g.CYCLE_SHORT_NAME END CYCLE_SHORT_NAME,
    c.CREDITABILITY_ID,
    f.CREDIT,
    c.ACADEMIC_FORMULA_ID,
    e.FORMULA,
    c.CONTROL_TYPE_ID,
    h.TYPE_NAME                 CONTROL_TYPE_NAME,
    a.DELETED
  FROM ELECTIVE_SUBJECT a INNER JOIN CURRICULUM b ON b.ID = a.CURRICULUM_ID
    INNER JOIN SUBJECT c ON c.ID = a.SUBJECT_ID
    INNER JOIN SEMESTER d ON d.ID = a.SEMESTER_ID
    INNER JOIN ACADEMIC_FORMULA e ON c.ACADEMIC_FORMULA_ID = e.ID
    INNER JOIN CREDITABILITY f ON c.CREDITABILITY_ID = f.ID
    INNER JOIN SUBJECT_CYCLE g ON c.SUBJECT_CYCLE_ID = g.ID
    INNER JOIN CONTROL_TYPE h ON c.CONTROL_TYPE_ID = h.ID
    LEFT JOIN SUBJECT_CYCLE i ON a.SUBJECT_CYCLE_ID = i.ID;

CREATE OR REPLACE VIEW V_ELECTIVE_SUBJECT_LABEL AS
  SELECT
    a.ID,
    a.NAME_KZ,
    a.NAME_EN,
    a.NAME_RU,
    a.CODE,
    a.STUDY_DIRECT_ID,
    a.DESCR,
    a.CHAIR_ID,
    a.LEVEL_ID,
    a.SUBJECT_CYCLE_ID,
    a.CREDITABILITY_ID,
    a.ACADEMIC_FORMULA_ID,
    a.GROUP_LEC_ID,
    a.GROUP_PRAC_ID,
    a.GROUP_LAB_ID,
    a.CONTROL_TYPE_ID,
    a.DELETED,
    FALSE ELECTIVE
  FROM SUBJECT a
  UNION ALL
  SELECT
    aa.ID,
    aa.NAME_KZ,
    aa.NAME_EN,
    aa.NAME_RU,
    NULL  CODE,
    NULL  STUDY_DIRECT_ID,
    NULL  DESCR,
    NULL  CHAIR_ID,
    NULL  LEVEL_ID,
    NULL  SUBJECT_CYCLE_ID,
    NULL  CREDITABILITY_ID,
    NULL  ACADEMIC_FORMULA_ID,
    NULL  GROUP_LEC_ID,
    NULL  GROUP_PRAC_ID,
    NULL  GROUP_LAB_ID,
    NULL  CONTROL_TYPE_ID,
    FALSE DELETED,
    TRUE  ELECTIVE
  FROM ELECTIVE_SUBJECT_LABEL aa;

CREATE SEQUENCE S_CURRICULUM_DETAIL
MINVALUE 0
START WITH 1
NO CYCLE;

DROP VIEW V_STUDENT;

CREATE OR REPLACE VIEW V_STUDENT AS
  SELECT
    stu.ID,
    usr.FIRST_NAME,
    usr.LAST_NAME,
    usr.MIDDLE_NAME,
    usr.FIRST_NAME_EN,
    usr.LAST_NAME_EN,
    usr.MIDDLE_NAME_EN,
    usr.BIRTH_DATE,
    usr.SEX_ID,
    c.SEX_NAME,
    usr.MARITAL_STATUS_ID,
    d.STATUS_NAME                  MARITAL_STATUS_NAME,
    usr.NATIONALITY_ID,
    e.NATION_NAME,
    usr.CITIZENSHIP_ID,
    f.COUNTRY_NAME                 CITIZENSHIP_NAME,
    usr.CODE                       USER_CODE,
    usr.LOGIN,
    usr.EMAIL,
    usr.PHONE_MOBILE,
    stu.LEVEL_ID,
    g.LEVEL_NAME,
    stu.CATEGORY_ID,
    h.CATEGORY_NAME,
    stu.ACADEMIC_STATUS_ID,
    i.STATUS_NAME                  ACADEMIC_STATUS_NAME,
    stu.NEED_DORM,
    stu.ENTRANCE_YEAR_ID,
    j.ENTRANCE_YEAR,
    j.BEGIN_YEAR                   ENTRANCE_BEGIN_YEAR,
    j.END_YEAR                     ENTRANCE_END_YEAR,
    k.FACULTY_ID,
    l.DEPT_NAME                    FACULTY_NAME,
    l.DEPT_SHORT_NAME              FACULTY_SHORT_NAME,
    l.CODE                         FACULTY_CODE,
    k.CHAIR_ID,
    m.DEPT_NAME                    CHAIR_NAME,
    m.DEPT_SHORT_NAME              CHAIR_SHORT_NAME,
    m.CODE                         CHAIR_CODE,
    k.SPECIALITY_ID,
    n.CODE || ' - ' || n.SPEC_NAME SPECIALITY_NAME,
    n.CODE                         SPECIALITY_CODE,
    k.STUDY_YEAR_ID,
    k.EDUCATION_TYPE_ID,
    o.TYPE_NAME                    EDUCATION_TYPE_NAME,
    k.ENTRY_DATE,
    k.END_DATE,
    k.STUDENT_STATUS_ID,
    p.STATUS_NAME                  STUDENT_STATUS_NAME,
    card.id                        card_id,
    card.card_name,
    usr.DELETED,
    usr.CREATED,
    usr.UPDATED,
    advisor.fio                    advisor,
    coordinator.fio                coordinator
  FROM STUDENT stu INNER JOIN USERS usr ON stu.ID = usr.ID
    INNER JOIN SEX c ON usr.SEX_ID = c.ID
    INNER JOIN MARITAL_STATUS d ON usr.MARITAL_STATUS_ID = d.ID
    INNER JOIN NATIONALITY e ON usr.NATIONALITY_ID = e.ID
    INNER JOIN COUNTRY f ON usr.CITIZENSHIP_ID = f.ID
    INNER JOIN LEVEL g ON stu.LEVEL_ID = g.ID
    INNER JOIN STUDENT_CATEGORY h ON stu.CATEGORY_ID = h.ID
    LEFT JOIN ACADEMIC_STATUS i ON stu.ACADEMIC_STATUS_ID = i.ID
    INNER JOIN ENTRANCE_YEAR j ON stu.ENTRANCE_YEAR_ID = j.ID
    INNER JOIN STUDENT_EDUCATION k ON k.STUDENT_ID = stu.ID AND k.CHILD_ID IS NULL
    INNER JOIN DEPARTMENT l ON k.FACULTY_ID = l.ID AND l.parent_id IS NULL AND l.deleted = FALSE
    INNER JOIN DEPARTMENT m ON k.CHAIR_ID = m.ID AND m.parent_id IS NOT NULL AND m.deleted = FALSE
    INNER JOIN SPECIALITY n ON k.SPECIALITY_ID = n.ID AND n.deleted = FALSE
    INNER JOIN STUDENT_EDUCATION_TYPE o ON k.EDUCATION_TYPE_ID = o.ID
    INNER JOIN STUDENT_STATUS p ON k.STUDENT_STATUS_ID = p.ID
    LEFT JOIN v_advisor advisor ON advisor.id = stu.advisor_id
    LEFT JOIN v_coordinator coordinator ON coordinator.id = stu.coordinator_id
    LEFT JOIN CARD card ON card.id = usr.card_id
  WHERE usr.deleted = FALSE AND usr.locked = FALSE;