-- CHAT

CREATE TABLE chat (
  id        BIGINT                NOT NULL,
  first_user_id BIGINT NOT NULL,
  second_user_id BIGINT NOT NULL,
  accepted BOOLEAN
);

ALTER TABLE chat
  ADD CONSTRAINT pk_chat PRIMARY KEY (id);

ALTER TABLE ONLY chat
  ADD CONSTRAINT fk_chat_users_first FOREIGN KEY (first_user_id)
REFERENCES users (id)
ON UPDATE RESTRICT ON DELETE RESTRICT;


ALTER TABLE ONLY chat
  ADD CONSTRAINT fk_chat_users_second FOREIGN KEY (second_user_id)
REFERENCES users (id)
ON UPDATE RESTRICT ON DELETE RESTRICT;

create sequence S_CHAT
  minvalue 0
  start with 1
  no cycle;

-- MESSAGE

CREATE TABLE message (
  id        BIGINT                NOT NULL,
  chat_id BIGINT NOT NULL,
  content TEXT,
  created TIMESTAMP NOT NULL,
  from_first BOOLEAN NOT NULL
);

DROP TABLE message;

ALTER TABLE message
  ADD CONSTRAINT pk_message PRIMARY KEY (id);

ALTER TABLE ONLY message
  ADD CONSTRAINT fk_message_chat FOREIGN KEY (chat_id)
REFERENCES chat (id)
ON UPDATE RESTRICT ON DELETE RESTRICT;

create sequence S_MESSAGE
  minvalue 0
  start with 1
  no cycle;

-- PRACTICE

CREATE TABLE practice_information (
  id BIGINT NOT NULL ,
  groups_id BIGINT NOT NULL,
  teacher_id BIGINT NOT NULL ,
  created TIMESTAMP NOT NULL
);

CREATE SEQUENCE s_practice_information
  MINVALUE 0
  START WITH 1
  NO CYCLE ;

ALTER TABLE practice_information
  ADD CONSTRAINT pk_practice_information PRIMARY KEY (id);

ALTER TABLE practice_information
    ADD CONSTRAINT fk_practice_information_groups
  FOREIGN KEY (groups_id)
  REFERENCES groups (id)
  ON UPDATE RESTRICT ON DELETE RESTRICT ;

ALTER TABLE practice_information
  ADD CONSTRAINT fk_practice_information_employee
  FOREIGN KEY  (teacher_id)
  REFERENCES employee(id)
  ON UPDATE RESTRICT ON DELETE RESTRICT ;

CREATE TABLE practice_student (
  id BIGINT NOT NULL ,
  student_id BIGINT NOT NULL ,
  organization_id BIGINT,
  come_in_date TIMESTAMP NOT NULL,
  come_out_date TIMESTAMP
);

CREATE SEQUENCE s_practice_student
  MINVALUE 0
  START WITH 1
  NO CYCLE ;

ALTER TABLE practice_student
  ADD CONSTRAINT pk_practice_student
  PRIMARY KEY (id);

ALTER TABLE practice_student ADD CONSTRAINT  fk_practice_student_organization
  FOREIGN KEY (organization_id) REFERENCES organization(id)
  ON UPDATE RESTRICT ON DELETE RESTRICT ;

ALTER TABLE practice_student ADD CONSTRAINT fk_practice_student_student
  FOREIGN KEY (student_id) REFERENCES student(id)
  ON UPDATE RESTRICT ON DELETE RESTRICT;

--SUBSTITUTION

CREATE TABLE substitution (
  id BIGINT NOT NULL ,
  employee_id BIGINT NOT NULL,
  substitutor_id BIGINT NOT NULL ,
  until_date TIMESTAMP NOT NULL,
  created TIMESTAMP NOT NULL DEFAULT now()
);

CREATE SEQUENCE s_substitution
  MINVALUE 0
  START WITH 1
  NO CYCLE ;

ALTER TABLE substitution
  ADD CONSTRAINT pk_substitution PRIMARY KEY (id);

ALTER TABLE substitution
    ADD CONSTRAINT fk_substitution_employee FOREIGN KEY (employee_id)
  REFERENCES employee(id)
  ON UPDATE RESTRICT ON DELETE RESTRICT ;

ALTER TABLE substitution
    ADD CONSTRAINT fk_substitution_substitutor FOREIGN KEY (substitutor_id)
  REFERENCES employee(id)
  ON UPDATE RESTRICT ON DELETE RESTRICT;


--SKILL

CREATE TABLE skill (
  id BIGINT NOT NULL,
  skill_name VARCHAR(255)
);

ALTER TABLE skill
  ADD CONSTRAINT pk_skill
  PRIMARY KEY (id);

CREATE SEQUENCE s_skill
  MINVALUE 0
  START WITH 1
  NO CYCLE;

--EMPLOYEE_SKILL

CREATE TABLE employee_skill (
  id BIGINT NOT NULL,
  employee_id BIGINT NOT NULL,
  skill_id BIGINT NOT NULL
);

CREATE SEQUENCE s_employee_skill
  MINVALUE 0
  START WITH 1
  NO CYCLE;

ALTER TABLE employee_skill
  ADD CONSTRAINT pk_employee_skill
PRIMARY KEY (id);

ALTER TABLE employee_skill
  ADD CONSTRAINT fk_employee_skill_employee
FOREIGN KEY (employee_id)
  REFERENCES employee(id)
 ON UPDATE RESTRICT ON DELETE RESTRICT ;

ALTER TABLE employee_skill
  ADD CONSTRAINT fk_employee_skill_skill
FOREIGN KEY (skill_id)
REFERENCES skill(id)
ON UPDATE RESTRICT ON DELETE RESTRICT ;

--CHILD

CREATE TABLE child (
  id BIGINT NOT NULL ,
  employee_id BIGINT NOT NULL ,
  first_name VARCHAR(255) NOT NULL ,
  last_name VARCHAR(255) NOT NULL ,
  middle_name VARCHAR(255),
  birth_date TIMESTAMP NOT NULL,
  sex_id BIGINT NOT NULL
);

CREATE SEQUENCE s_child
  MINVALUE 0
  START WITH 1
  NO CYCLE;

ALTER TABLE child
  ADD CONSTRAINT pk_child
PRIMARY KEY (id);

ALTER TABLE child
  ADD CONSTRAINT fk_child_employee
FOREIGN KEY (employee_id)
REFERENCES employee(id)
ON UPDATE RESTRICT ON DELETE RESTRICT ;

ALTER TABLE child
  ADD CONSTRAINT fk_child_sex
FOREIGN KEY (sex_id)
REFERENCES sex(id)
ON UPDATE RESTRICT ON DELETE RESTRICT ;

--EMPLOYEE_AWARD

CREATE TABLE employee_award (
  id BIGINT NOT NULL,
  employee_id BIGINT NOT NULL,
  award_id BIGINT NOT NULL
);

CREATE SEQUENCE s_employee_award
  MINVALUE 0
  START WITH 1
  NO CYCLE;

ALTER TABLE employee_award
  ADD CONSTRAINT pk_employee_award
PRIMARY KEY (id);

ALTER TABLE employee_award
  ADD CONSTRAINT fk_employee_award_employee
FOREIGN KEY (employee_id)
REFERENCES employee(id)
ON UPDATE RESTRICT ON DELETE RESTRICT ;

ALTER TABLE employee_award
  ADD CONSTRAINT fk_employee_award_award
FOREIGN KEY (award_id)
REFERENCES award(id)
ON UPDATE RESTRICT ON DELETE RESTRICT ;

--QUALIFICATION

CREATE TABLE qualification (
  id BIGINT NOT NULL,
  qualification_name VARCHAR(255)
);

ALTER TABLE qualification
  ADD CONSTRAINT pk_qualification
PRIMARY KEY (id);

CREATE SEQUENCE s_qualification
  MINVALUE 0
  START WITH 1
  NO CYCLE;

--EMPLOYEE_QUALIFICATION

CREATE TABLE employee_qualification (
  id BIGINT NOT NULL,
  employee_id BIGINT NOT NULL,
  qualification_id BIGINT NOT NULL,
  created TIMESTAMP NOT NULL,
  updated TIMESTAMP
);

CREATE SEQUENCE s_employee_qualification
  MINVALUE 0
  START WITH 1
  NO CYCLE;

ALTER TABLE employee_qualification
  ADD CONSTRAINT pk_employee_qualification
PRIMARY KEY (id);

ALTER TABLE employee_qualification
  ADD CONSTRAINT fk_employee_qualification_employee
FOREIGN KEY (employee_id)
REFERENCES employee(id)
ON UPDATE RESTRICT ON DELETE RESTRICT ;

ALTER TABLE employee_qualification
  ADD CONSTRAINT fk_employee_qualification_qualification
FOREIGN KEY (qualification_id)
REFERENCES qualification(id)
ON UPDATE RESTRICT ON DELETE RESTRICT ;

