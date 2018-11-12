CREATE TABLE stream_type (
  id        BIGINT                NOT NULL,
  TYPE_NAME CHARACTER VARYING(64) NOT NULL
);

ALTER TABLE stream_type
    add CONSTRAINT pk_stream_type PRIMARY KEY (id);

INSERT INTO stream_type VALUES (1,'Общие');
INSERT INTO stream_type VALUES (2,'Специализированный');
INSERT INTO stream_type VALUES (3,'Просто поток');

DELETE FROM stream_group;
DELETE FROM teacher_load_assign_detail;
DELETE FROM stream;

ALTER TABLE stream
  ADD COLUMN stream_type_id BIGINT NOT NULL;

ALTER TABLE stream
  ADD CONSTRAINT fk_stream_stream_type FOREIGN KEY (stream_type_id)
REFERENCES stream_type (id)
ON DELETE RESTRICT ON UPDATE RESTRICT;

ALTER TABLE groups
    DROP COLUMN orders;

DROP VIEW v_stream;
ALTER TABLE stream
  DROP COLUMN semester_data_id;
ALTER TABLE stream
  DROP COLUMN updated;

ALTER TABLE stream
  ADD COLUMN language_id BIGINT NOT NULL;
ALTER TABLE stream
  ADD CONSTRAINT fk_stream_language FOREIGN KEY (language_id)
REFERENCES language (id)
ON DELETE RESTRICT ON UPDATE RESTRICT;

ALTER TABLE stream
  ADD COLUMN deleted BOOLEAN NOT NULL;

ALTER TABLE stream
  ALTER COLUMN name SET NOT NULL;