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

-- DOCUMENT_ACTIVITY

CREATE TABLE DOCUMENT_ACTIVITY (
  id        BIGINT                NOT NULL,
  activity_name VARCHAR(50)
);

ALTER TABLE DOCUMENT_ACTIVITY
  ADD CONSTRAINT pk_document_activity PRIMARY KEY (id);

create sequence S_DOCUMENT_ACTIVITY
  minvalue 0
  start with 1
  no cycle;

-- DOCUMENT_STATUS

CREATE TABLE DOCUMENT_STATUS (
  id        BIGINT                NOT NULL,
  status_name VARCHAR(50)
);

ALTER TABLE DOCUMENT_STATUS
  ADD CONSTRAINT pk_document_status PRIMARY KEY (id);

create sequence S_DOCUMENT_STATUS
  minvalue 0
  start with 1
  no cycle;

-- DOCUMENT_SIGNER_STATUS

CREATE TABLE DOCUMENT_SIGNER_STATUS (
  id        BIGINT                NOT NULL,
  status_name VARCHAR(50)
);

ALTER TABLE DOCUMENT_SIGNER_STATUS
  ADD CONSTRAINT pk_document_signer_status PRIMARY KEY (id);

create sequence S_DOCUMENT_SIGNER_STATUS
  minvalue 0
  start with 1
  no cycle;

-- DOCUMENT_IMPORTANCE


CREATE TABLE DOCUMENT_IMPORTANCE (
  id        BIGINT                NOT NULL,
  importance_name VARCHAR(50)
);

ALTER TABLE DOCUMENT_IMPORTANCE
ADD CONSTRAINT pk_document_importance PRIMARY KEY (id);

create sequence S_DOCUMENT_IMPORTANCE
  minvalue 0
  start with 1
  no cycle;

-- DOCUMENT

CREATE TABLE DOCUMENT(
  id        BIGINT                NOT NULL,
  creator_employee_id BIGINT NOT NULL,
  document_status_id BIGINT NOT NULL,
  document_importance_id BIGINT NOT NULL,
  pdf_document_id BIGINT NOT NULL,
  message TEXT,
  note TEXT,
  deadline_date TIMESTAMP NOT NULL,
  created TIMESTAMP NOT NULL,
  updated TIMESTAMP NOT NULL,
  deleted BOOLEAN
);


ALTER TABLE DOCUMENT
  ADD CONSTRAINT pk_document PRIMARY KEY (id);

create sequence S_DOCUMENT
  minvalue 0
  start with 1
  no cycle;

ALTER TABLE ONLY DOCUMENT
  ADD CONSTRAINT fk_document_creater_employee FOREIGN KEY (creator_employee_id)
REFERENCES users (id)
ON UPDATE RESTRICT ON DELETE RESTRICT;

ALTER TABLE ONLY DOCUMENT
  ADD CONSTRAINT fk_document_document_status FOREIGN KEY (document_status_id)
REFERENCES users (id)
ON UPDATE RESTRICT ON DELETE RESTRICT;