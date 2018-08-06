ALTER TABLE users
  ADD COLUMN push_id VARCHAR(255);

-- DOCUMENT ADDITION
CREATE TABLE document_user_input(
  id BIGINT NOT NULL,
  pdf_document_id BIGINT NOT NULL,
  value TEXT,
  description TEXT,
  created TIMESTAMP NOT NULL
);


ALTER TABLE document_user_input
  ADD CONSTRAINT pk_document_user_input
PRIMARY KEY (id);

create sequence s_document_user_input
  minvalue 0
  start with 1
  no cycle;

ALTER TABLE ONLY document_user_input
  ADD CONSTRAINT fk_document_user_input_pdf_document FOREIGN KEY (pdf_document_id)
REFERENCES  pdf_document(id)
ON UPDATE RESTRICT ON DELETE RESTRICT;
-- ANOTHER

ALTER TABLE pdf_document ADD COLUMN created TIMESTAMP NOT NULL DEFAULT now();

ALTER TABLE pdf_property ADD COLUMN rights BOOLEAN NOT NULL  DEFAULT FALSE;
ALTER TABLE pdf_property ADD COLUMN custom BOOLEAN NOT NULL  DEFAULT FALSE;

CREATE TABLE document_user_real_input(
  id BIGINT NOT NULL,
  document_user_input_id BIGINT NOT NULL,
  document_id BIGINT NOT NULL,
  value TEXT,
  created TIMESTAMP NOT NULL
);

ALTER TABLE document_user_real_input
  ADD CONSTRAINT pk_document_user_real_input
  PRIMARY KEY (id);

create sequence s_document_user_real_input
  minvalue 0
  start with 1
  no cycle;

ALTER TABLE ONLY document_user_real_input
  ADD CONSTRAINT fk_document_user_real_input_document
FOREIGN KEY (document_id)
REFERENCES  document(id)
ON UPDATE RESTRICT ON DELETE RESTRICT;

ALTER TABLE ONLY document_user_real_input
  ADD CONSTRAINT fk_document_user_real_input_document_user_input
FOREIGN KEY (document_user_input_id)
REFERENCES  document_user_input(id)
ON UPDATE RESTRICT ON DELETE RESTRICT;

-- DOCUMENT
ALTER TABLE document ADD COLUMN related_document_file_path  text;