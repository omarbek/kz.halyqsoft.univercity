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

  ALTER TABLE pair_subject
ADD COLUMN description varchar(8000) DEFAULT '';

ALTER TABLE subject DROP COLUMN descr ;

ALTER TABLE pair_subject
    ALTER COLUMN description SET NOT NULL ;
