CREATE TABLE pdf_property (
  id    BIGINT       NOT NULL,
  text  VARCHAR(255) NOT NULL,
  x     FLOAT        NOT NULL,
  y     FLOAT        NOT NULL,
  font  VARCHAR(100) NOT NULL,
  style VARCHAR(100) NOT NULL,
  size  NUMERIC(3)   NOT NULL
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
  pdf_property_id BIGINT       NOT NULL,
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

ALTER TABLE pdf_document
  ADD CONSTRAINT fk_pdf_document_users FOREIGN KEY (user_id)
REFERENCES users (id)
ON DELETE RESTRICT ON UPDATE RESTRICT;

ALTER TABLE pdf_document
  ADD CONSTRAINT fk_pdf_document_pdf_property FOREIGN KEY (pdf_property_id)
REFERENCES pdf_property (id)
ON DELETE RESTRICT ON UPDATE RESTRICT;

INSERT INTO country VALUES (nextval('S_COUNTRY'),'Казахстан', null);--for assil
INSERT into marital_status VALUES (nextval('S_MARITAL_STATUS'), 'Замужем/женат');