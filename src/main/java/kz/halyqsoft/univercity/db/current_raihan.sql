CREATE TABLE graduate_employment(
  id               BIGINT NOT NULL,
  student_id       BIGINT NOT NULL,
  employed         BOOLEAN,
  by_speciality    BOOLEAN,
  master           BOOLEAN,
  decree           BOOLEAN,
  army             BOOLEAN
);

ALTER TABLE graduate_employment
  ADD CONSTRAINT pk_graduate_employment PRIMARY KEY (id);

ALTER TABLE ONLY graduate_employment
  ADD CONSTRAINT fk_graduate_employment FOREIGN KEY (student_id)
REFERENCES student (id)
ON UPDATE RESTRICT ON DELETE RESTRICT;

CREATE SEQUENCE s_graduate_employment
  minvalue 0
  start with 1
  no cycle;

CREATE UNIQUE index idx_graduate_employment
  ON graduate_employment (student_id);

--//01.08.18
  CREATE TABLE request_type_status(
  id BIGINT NOT NULL,
  status_name VARCHAR(64) NOT NULL
);

ALTER TABLE request_type_status
  ADD CONSTRAINT pk_request_type_status PRIMARY KEY (id);

INSERT INTO request_type_status VALUES(0,'Не рассмотрен');
INSERT INTO request_type_status VALUES(1,'Принято');
INSERT INTO request_type_status VALUES(2,'Отказано');

CREATE SEQUENCE s_request_type_status
  minvalue 0
  start with 1
  no cycle;

  ALTER TABLE dorm_student
    ADD COLUMN request_status_id BIGINT DEFAULT 0;

ALTER TABLE ONLY dorm_student
  ADD CONSTRAINT fk_dorm_student FOREIGN KEY (request_status_id)
REFERENCES request_type_status (id)
ON UPDATE RESTRICT ON DELETE RESTRICT;

ALTER TABLE dorm_student ALTER COLUMN check_in_date DROP NOT NULL ;

