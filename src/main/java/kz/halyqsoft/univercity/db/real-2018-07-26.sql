ALTER TABLE pair_subject
  ADD COLUMN description varchar(8000) DEFAULT '';

ALTER TABLE subject DROP COLUMN descr ;

ALTER TABLE pair_subject
  ALTER COLUMN description SET NOT NULL;

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
REFERENCES DOCUMENT_STATUS (id)
ON UPDATE RESTRICT ON DELETE RESTRICT;

--PDF DOCUMENT SIGNER LIST

CREATE TABLE PDF_DOCUMENT_SIGNER_POST (
  id BIGINT NOT NULL ,
  pdf_document_id BIGINT NOT NULL ,
  post_id BIGINT NOT NULL
);
CREATE SEQUENCE s_pdf_document_signer_post
  MINVALUE 0
  START WITH 1
  NO CYCLE;

ALTER TABLE ONLY PDF_DOCUMENT_SIGNER_POST
    ADD CONSTRAINT fk_pdf_document_signer_post_post FOREIGN KEY (post_id)
REFERENCES post (id)
ON UPDATE RESTRICT
ON DELETE RESTRICT;

ALTER TABLE ONLY PDF_DOCUMENT_SIGNER_POST
  ADD CONSTRAINT fk_pdf_document_signer_post_pdf_document FOREIGN KEY (pdf_document_id)
REFERENCES pdf_document (id)
ON UPDATE RESTRICT
ON DELETE RESTRICT;

--PDF DOCUMENT SIGNER LIST END

ALTER TABLE pdf_document ADD COLUMN period INTEGER NOT NULL DEFAULT 0;

ALTER TABLE DOCUMENT ALTER COLUMN updated DROP NOT NULL ;


--DOCUMENT SIGNERS

CREATE TABLE DOCUMENT_SIGNER(
  id        BIGINT                NOT NULL,
  employee_id BIGINT,
  document_id BIGINT NOT NULL,
  post_id BIGINT NOT NULL ,
  document_signer_status_id BIGINT ,
  message TEXT,
  created TIMESTAMP NOT NULL,
  updated TIMESTAMP,
  deleted BOOLEAN DEFAULT FALSE
);

CREATE SEQUENCE s_document_signer
  MINVALUE 0
  START WITH 1
  NO CYCLE ;

ALTER TABLE ONLY DOCUMENT_SIGNER
  ADD CONSTRAINT pk_document_signer PRIMARY KEY (id);

ALTER TABLE ONLY DOCUMENT_SIGNER
  ADD CONSTRAINT fk_document_signer_document
FOREIGN KEY (document_id) REFERENCES DOCUMENT(id);


ALTER TABLE ONLY DOCUMENT_SIGNER
  ADD CONSTRAINT fk_document_signer_post
FOREIGN KEY (post_id) REFERENCES post(id);

ALTER TABLE ONLY DOCUMENT_SIGNER
  ADD CONSTRAINT fk_document_signer_document_signer_status
FOREIGN KEY (document_signer_status_id) REFERENCES DOCUMENT_SIGNER_STATUS(id);

--DOCUMENT SIGNERS END

ALTER TABLE DOCUMENT DROP COLUMN note;

ALTER TABLE DOCUMENT_IMPORTANCE DROP COLUMN importance_name;
ALTER TABLE DOCUMENT_IMPORTANCE ADD COLUMN importance_value INTEGER NOT NULL;


ALTER TABLE ONLY DOCUMENT
  ADD CONSTRAINT fk_document_document_importance FOREIGN KEY (document_importance_id)
REFERENCES DOCUMENT_IMPORTANCE(id)
ON UPDATE RESTRICT ON DELETE RESTRICT;

ALTER TABLE ONLY DOCUMENT
  ADD CONSTRAINT fk_document_pdf_document FOREIGN KEY (pdf_document_id)
REFERENCES pdf_document(id)
ON UPDATE RESTRICT ON DELETE RESTRICT;


insert into document_status (id, status_name) values (nextval('s_document_status') , 'создано');
insert into document_status (id, status_name) values (nextval('s_document_status') , 'в процессе');
insert into document_status (id, status_name) values (nextval('s_document_status') , 'отказано на доработку');
insert into document_status (id, status_name) values (nextval('s_document_status') , 'отказано окончательно');
insert into document_status (id, status_name) values (nextval('s_document_status') , 'принято');

insert into document_importance (id, importance_value) values (nextval('s_document_importance') , 1);
insert into document_importance (id, importance_value) values (nextval('s_document_importance') , 2);
insert into document_importance (id, importance_value) values (nextval('s_document_importance') , 3);
insert into document_importance (id, importance_value) values (nextval('s_document_importance') , 4);
insert into document_importance (id, importance_value) values (nextval('s_document_importance') , 5);

insert into document_signer_status (id, status_name) values (nextval('s_document_signer_status') , 'создано');
insert into document_signer_status (id, status_name) values (nextval('s_document_signer_status') , 'подписано');
insert into document_signer_status (id, status_name) values (nextval('s_document_signer_status') , 'отказано на доработку');
insert into document_signer_status (id, status_name) values (nextval('s_document_signer_status') , 'отказано окончательно');
insert into document_signer_status (id, status_name) values (nextval('s_document_signer_status') , 'в процессе');
