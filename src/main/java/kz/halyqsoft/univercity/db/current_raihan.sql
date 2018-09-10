CREATE TABLE practice_type(
  id        BIGINT       NOT NULL,
  type_name VARCHAR(128) NOT NULL
);

ALTER TABLE practice_type
  ADD CONSTRAINT pk_practice_type PRIMARY KEY (id);

INSERT INTO practice_type VALUES(1,'Производственная');
INSERT INTO practice_type VALUES(2,'Учебная');
INSERT INTO practice_type VALUES(3,'Педагогическая');

ALTER TABLE subject ADD practice_type_id int null;

ALTER TABLE ONLY subject
  ADD CONSTRAINT fk_practice_type FOREIGN KEY (practice_type_id)
REFERENCES practice_type (id)
ON UPDATE RESTRICT ON DELETE RESTRICT;