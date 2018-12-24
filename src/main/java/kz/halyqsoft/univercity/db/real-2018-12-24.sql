-- auto-generated definition
CREATE TABLE pdf_document_access_post
(
  id              BIGINT NOT NULL PRIMARY KEY,
  pdf_document_id BIGINT NOT NULL
    CONSTRAINT fk_pdf_document_signer_post_pdf_document
    REFERENCES pdf_document
    ON UPDATE RESTRICT ON DELETE RESTRICT,
  post_id         BIGINT NOT NULL
    CONSTRAINT fk_pdf_document_signer_post_post
    REFERENCES post
    ON UPDATE RESTRICT ON DELETE RESTRICT
);

CREATE SEQUENCE s_pdf_document_access_post START WITH 1 MINVALUE 0 NO CYCLE ;