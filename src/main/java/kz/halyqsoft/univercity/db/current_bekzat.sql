CREATE TABLE pdf_property (
  id    BIGINT       NOT NULL,
  text  VARCHAR(255) NOT NULL,
  x     FLOAT        NOT NULL,
  y     FLOAT        NOT NULL,
  font  VARCHAR(100) NOT NULL,
  style VARCHAR(100) NOT NULL,
  size  NUMERIC(3)   NOT NULL,
  pdf_document_id BIGINT NOT NULL
);

ALTER TABLE pdf_property
  ADD CONSTRAINT pk_pdf_property PRIMARY KEY (id);

CREATE SEQUENCE s_pdf_property
  MINVALUE 0
  START WITH 1
  NO CYCLE;

CREATE TABLE pdf_document (
  id              BIGINT       NOT NULL,
  user_id         BIGINT       NOT NULL,
  title           VARCHAR(255) NULL,
  file_name       VARCHAR(255) NOT NULL,
  file_byte       BYTEA        NOT NULL
);

CREATE SEQUENCE s_pdf_document
  MINVALUE 0
  START WITH 1
  NO CYCLE;

ALTER TABLE pdf_document
  ADD CONSTRAINT pk_pdf_document PRIMARY KEY (id);

ALTER TABLE ONLY pdf_document
  ADD CONSTRAINT fk_pdf_document_users FOREIGN KEY (user_id)
REFERENCES users (id)
ON UPDATE RESTRICT ON DELETE RESTRICT;

ALTER TABLE pdf_property
  ADD CONSTRAINT fk_pdf_property_pdf_document FOREIGN KEY (pdf_document_id)
REFERENCES pdf_document (id)
ON DELETE RESTRICT ON UPDATE RESTRICT;