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