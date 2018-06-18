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

-- GROUPS

CREATE TABLE groups (
  id  BIGINT  NOT NULL,
  speciality_id BIGINT NOT NULL,
  name CHARACTER VARYING(150) NOT NULL,
  orders BIGINT
);

ALTER TABLE groups
  ADD CONSTRAINT pk_group PRIMARY KEY (id);

ALTER TABLE ONLY groups
  ADD CONSTRAINT fk_group_speciality FOREIGN KEY (speciality_id)
REFERENCES speciality (id)
ON UPDATE RESTRICT ON DELETE RESTRICT;

create sequence S_GROUPS
  minvalue 0
  start with 1
  no cycle;

ALTER TABLE groups ADD COLUMN deleted BOOLEAN NOT NULL ;


ALTER TABLE student_education ADD COLUMN groups_id BIGINT;

ALTER TABLE ONLY student_education
  ADD CONSTRAINT fk_student_education_groups FOREIGN KEY (groups_id)
REFERENCES groups (id)
ON UPDATE RESTRICT ON DELETE RESTRICT;

-- SUBJECT

