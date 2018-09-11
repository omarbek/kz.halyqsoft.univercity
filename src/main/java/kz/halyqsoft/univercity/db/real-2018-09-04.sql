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
    gr.id                          GROUPS_ID,
    gr.name                        GROUP_NAME,
    usr.DELETED,
    usr.CREATED,
    usr.UPDATED,
    advisor.fio                    advisor,
    coordinator.fio                coordinator,
    diplom.id                      diploma_type_id,
    diplom.type_name               diploma_type_name,
    k.language_id,
    stu.advisor_id,
    stu.coordinator_id
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
    INNER JOIN student_diploma_type diplom ON stu.diploma_type_id = diplom.id
    LEFT JOIN v_advisor advisor ON advisor.id = stu.advisor_id
    LEFT JOIN v_coordinator coordinator ON coordinator.id = stu.coordinator_id
    LEFT JOIN CARD card ON card.id = usr.card_id
    LEFT JOIN groups gr ON gr.id = k.groups_id
  WHERE usr.deleted = FALSE AND usr.locked = FALSE;

ALTER TABLE employee_dept
  ADD COLUMN coordinator BOOLEAN NOT NULL DEFAULT FALSE;

UPDATE employee_dept
SET coordinator = TRUE
WHERE employee_id IN (SELECT coordinator_id
                      FROM student
                      WHERE coordinator_id IS NOT NULL);

UPDATE employee_dept
SET adviser = TRUE
WHERE employee_id IN (SELECT advisor_id
                      FROM student
                      WHERE advisor_id IS NOT NULL);

CREATE OR REPLACE VIEW v_coordinator AS
  SELECT
    empl.id,
    trim(usr.LAST_NAME || ' ' || usr.FIRST_NAME || ' ' || coalesce(usr.MIDDLE_NAME, '')) FIO,
    usr.code,
    empl_dept.dept_id,
    dep.dept_name,
    empl_dept.post_id,
    post.post_name
  FROM employee empl
    INNER JOIN users usr ON empl.id = usr.id
    INNER JOIN employee_dept empl_dept ON empl.id = empl_dept.employee_id
    INNER JOIN department dep ON empl_dept.dept_id = dep.id
    LEFT JOIN post post ON empl_dept.post_id = post.id
  WHERE empl.employee_status_id = 1 AND usr.locked = FALSE AND LENGTH(usr.last_name) > 0
        AND empl_dept.dismiss_date IS NULL AND dep.deleted = FALSE
        AND empl_dept.coordinator = TRUE
  ORDER BY post.priority
  LIMIT 1;

ALTER TABLE lost_and_found
  DROP COLUMN photo;

CREATE TABLE question (
  id          BIGINT    NOT NULL,
  created_by  BIGINT    NOT NULL,
  description VARCHAR(255),
  created     TIMESTAMP NOT NULL,
  answer      VARCHAR(255),
  answered    BOOLEAN   NOT NULL
);

CREATE SEQUENCE s_question
MINVALUE 1
START WITH 1
NO CYCLE;

ALTER TABLE ONLY question
  ADD CONSTRAINT pk_question PRIMARY KEY (id);
ALTER TABLE ONLY question
  ADD CONSTRAINT fk_question FOREIGN KEY (created_by) REFERENCES users (id) ON
UPDATE RESTRICT ON DELETE RESTRICT;

INSERT INTO non_admission_cause VALUES (1, 'Успеваемость ниже 50%');
INSERT INTO non_admission_cause VALUES (2, 'Посещаемость ниже 80%');
INSERT INTO non_admission_cause VALUES (3, 'За неоплату');

ALTER TABLE non_admission_exam
  DROP COLUMN semester_data_id;

ALTER TABLE non_admission_exam
  DROP COLUMN student_id;

ALTER TABLE non_admission_exam
  ADD COLUMN student_education_id BIGINT NOT NULL;

ALTER TABLE ONLY non_admission_exam
  ADD CONSTRAINT fk_non_admission_exam_student_education
FOREIGN KEY (student_education_id)
REFERENCES student_education (id)
ON UPDATE RESTRICT ON DELETE RESTRICT;