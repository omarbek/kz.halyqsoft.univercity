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