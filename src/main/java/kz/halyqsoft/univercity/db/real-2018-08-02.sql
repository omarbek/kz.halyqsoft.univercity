alter table student_fin_debt drop COLUMN report_date;
alter table student_payment drop COLUMN payment_date;
alter table lesson_detail drop COLUMN grade;

CREATE TABLE request_type_status (
  id          BIGINT      NOT NULL,
  status_name VARCHAR(64) NOT NULL
);

ALTER TABLE request_type_status
  ADD CONSTRAINT pk_request_type_status PRIMARY KEY (id);

INSERT INTO request_type_status VALUES (0, 'Не рассмотрен');
INSERT INTO request_type_status VALUES (1, 'Принято');
INSERT INTO request_type_status VALUES (2, 'Отказано');

CREATE SEQUENCE s_request_type_status
MINVALUE 0
START WITH 1
NO CYCLE;

ALTER TABLE dorm_student
  ADD COLUMN request_status_id BIGINT DEFAULT 0;

ALTER TABLE ONLY dorm_student
  ADD CONSTRAINT fk_dorm_student FOREIGN KEY (request_status_id)
REFERENCES request_type_status (id)
ON UPDATE RESTRICT ON DELETE RESTRICT;

ALTER TABLE dorm_student
  ALTER COLUMN check_in_date DROP NOT NULL;