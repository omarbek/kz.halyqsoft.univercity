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
  WHERE usr.deleted = FALSE;

CREATE TABLE contract_payment_type(
  id BIGINT NOT NULL,
  type_name VARCHAR(255) NOT NULL
);


ALTER TABLE contract_payment_type
  ADD CONSTRAINT pk_contract_payment_type PRIMARY KEY (id);
insert into contract_payment_type values (1, 'Оплата за общагу');
insert into contract_payment_type values (2, 'Оплата за учебу');

CREATE TABLE accountant_price(
  id BIGINT NOT NULL,
  student_diploma_type_id BIGINT NOT NULL,
  level_id BIGINT NOT NULL,
  contract_payment_type_id BIGINT NOT NULL,
  price NUMERIC(10,2) NOT NULL,
  price_in_letters VARCHAR(255) NOT NULL,
  deleted BOOLEAN NOT NULL
);

CREATE SEQUENCE s_accountant_price
  MINVALUE 0
  START WITH 1
  NO CYCLE;


ALTER SEQUENCE s_pdf_property
MINVALUE 0
START WITH 1
NO CYCLE;

ALTER TABLE accountant_price
  ADD CONSTRAINT pk_accountant_price PRIMARY KEY (id);

ALTER TABLE ONLY accountant_price
  ADD CONSTRAINT fk_accountant_price_level FOREIGN KEY (level_id)
REFERENCES level (id)
ON UPDATE RESTRICT ON DELETE RESTRICT;

ALTER TABLE accountant_price
  ADD CONSTRAINT fk_accountant_price_student_diploma_type FOREIGN KEY (student_diploma_type_id)
REFERENCES student_diploma_type (id)
ON DELETE RESTRICT ON UPDATE RESTRICT;

ALTER TABLE accountant_price
  ADD CONSTRAINT fk_accountant_price_contract_payment_type FOREIGN KEY (contract_payment_type_id)
REFERENCES contract_payment_type (id)
ON DELETE RESTRICT ON UPDATE RESTRICT;

CREATE UNIQUE INDEX idx_accountant_price ON accountant_price USING btree
(student_diploma_type_id,level_id,contract_payment_type_id) where deleted = false;
