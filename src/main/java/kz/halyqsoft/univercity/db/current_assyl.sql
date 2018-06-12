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

ALTER TABLE subject ALTER COLUMN deleted DROP DEFAULT;
ALTER TABLE subject ALTER deleted TYPE bool USING CASE WHEN deleted=0 THEN FALSE ELSE TRUE END;
ALTER TABLE subject ALTER COLUMN deleted SET DEFAULT FALSE;

ALTER TABLE subject ALTER COLUMN lang_en DROP DEFAULT;
ALTER TABLE subject ALTER lang_en TYPE bool USING CASE WHEN lang_en=0 THEN FALSE ELSE TRUE END;
ALTER TABLE subject ALTER COLUMN lang_en SET DEFAULT FALSE;

ALTER TABLE subject ALTER COLUMN lang_kz DROP DEFAULT;
ALTER TABLE subject ALTER lang_kz TYPE bool USING CASE WHEN lang_kz=0 THEN FALSE ELSE TRUE END;
ALTER TABLE subject ALTER COLUMN lang_kz SET DEFAULT FALSE;

ALTER TABLE subject ALTER COLUMN lang_ru DROP DEFAULT;
ALTER TABLE subject ALTER lang_ru TYPE bool USING CASE WHEN lang_ru=0 THEN FALSE ELSE TRUE END;
ALTER TABLE subject ALTER COLUMN lang_ru SET DEFAULT FALSE;

ALTER TABLE subject ALTER COLUMN mandatory DROP DEFAULT;
ALTER TABLE subject ALTER mandatory TYPE bool USING CASE WHEN mandatory=0 THEN FALSE ELSE TRUE END;
ALTER TABLE subject ALTER COLUMN mandatory SET DEFAULT FALSE;

-- SUBJECT


-- ELECTIVE_SUBJECT

ALTER TABLE subject ALTER COLUMN deleted DROP DEFAULT;
ALTER TABLE subject ALTER deleted TYPE bool USING CASE WHEN deleted=0 THEN FALSE ELSE TRUE END;
ALTER TABLE subject ALTER COLUMN deleted SET DEFAULT FALSE;

ALTER TABLE elective_subject ALTER COLUMN curriculum_id DROP NOT NULL;
ALTER TABLE elective_subject DROP COLUMN subject_id;
ALTER TABLE v_elective_subject DROP COLUMN subject_id;

ALTER TABLE elective_subject ADD column name_kz VARCHAR(255) not null;

ALTER  TABLE elective_subject INHERIT subject;

-- ELECTUVE_SUBJECT