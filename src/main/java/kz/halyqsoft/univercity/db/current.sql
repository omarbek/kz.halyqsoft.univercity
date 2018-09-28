DROP TABLE attestation_type;
DROP TABLE attestation_detail;
DROP TABLE attestation;

CREATE TABLE attestation
(
  id BIGINT NOT NULL
    CONSTRAINT pk_t_attestation
    PRIMARY KEY,
  begin_date       TIMESTAMP,
  finish_date      TIMESTAMP,
  semester_data_id BIGINT NOT NULL
    CONSTRAINT fk_attestation_semester_data
    REFERENCES semester_data
    ON UPDATE RESTRICT ON DELETE RESTRICT
);

CREATE SEQUENCE s_attestation
  MINVALUE 0
  START WITH 1
  NO CYCLE ;

CREATE TABLE attestation_detail
(
  id              BIGINT        NOT NULL
    CONSTRAINT pk_t_attestation_detail
    PRIMARY KEY,
  attestation_id  BIGINT        NOT NULL
    CONSTRAINT fk_attestation_detail_attestation
    REFERENCES attestation
    ON UPDATE RESTRICT ON DELETE RESTRICT,
  student_education_id      BIGINT        NOT NULL
    CONSTRAINT fk_attestation_detail_student_education
    REFERENCES student_education
    ON UPDATE RESTRICT ON DELETE RESTRICT,
  employee_id      BIGINT        NOT NULL
    CONSTRAINT fk_attestation_detail_employee
    REFERENCES employee (id)
    ON UPDATE RESTRICT ON DELETE RESTRICT,
  grade           DOUBLE PRECISION NOT NULL,
  created TIMESTAMP DEFAULT now(),
  updated         TIMESTAMP
);

CREATE UNIQUE INDEX idx_t_attestation_detail
  ON attestation_detail (attestation_id, student_education_id);

CREATE SEQUENCE s_attestation_detail
  MINVALUE 0
  START WITH 1
  NO CYCLE ;