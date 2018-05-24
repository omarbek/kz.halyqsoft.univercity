CREATE TABLE contract_payment_type(
  id BIGINT NOT NULL,
  type_name VARCHAR(255) NOT NULL
);


ALTER TABLE contract_payment_type
  ADD CONSTRAINT pk_contract_payment_type PRIMARY KEY (id);
insert into contract_payment_type values (1, 'Оплата за общагу');
insert into contract_payment_type values (2, 'Оплата за учебу');

CREATE TABLE accountant_price(
  id BIGINT NOT NULL,
  student_diploma_type_id BIGINT NOT NULL,
  level_id BIGINT NOT NULL,
  contract_payment_type_id BIGINT NOT NULL,
  price NUMERIC(10,2) NOT NULL,
  price_in_letters VARCHAR(255) NOT NULL,
  deleted BOOLEAN NOT NULL
);

CREATE SEQUENCE s_accountant_price
  MINVALUE 0
  START WITH 1
  NO CYCLE;


ALTER SEQUENCE s_pdf_property
MINVALUE 0
START WITH 1
NO CYCLE;

ALTER TABLE accountant_price
  ADD CONSTRAINT pk_accountant_price PRIMARY KEY (id);

ALTER TABLE ONLY accountant_price
  ADD CONSTRAINT fk_accountant_price_level FOREIGN KEY (level_id)
REFERENCES level (id)
ON UPDATE RESTRICT ON DELETE RESTRICT;

ALTER TABLE accountant_price
  ADD CONSTRAINT fk_accountant_price_student_diploma_type FOREIGN KEY (student_diploma_type_id)
REFERENCES student_diploma_type (id)
ON DELETE RESTRICT ON UPDATE RESTRICT;

ALTER TABLE accountant_price
  ADD CONSTRAINT fk_accountant_price_contract_payment_type FOREIGN KEY (contract_payment_type_id)
REFERENCES contract_payment_type (id)
ON DELETE RESTRICT ON UPDATE RESTRICT;

CREATE UNIQUE INDEX idx_accountant_price ON accountant_price USING btree
(student_diploma_type_id,level_id,contract_payment_type_id) where deleted = false;
