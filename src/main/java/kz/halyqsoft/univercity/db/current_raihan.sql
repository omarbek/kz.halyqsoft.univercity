ALTER TABLE pair_subject ADD column teacher_id BIGINT NULL;

CREATE TABLE trajectory(
  id        bigint       not null,
  type_name varchar(128) not null
);

ALTER TABLE trajectory
  ADD CONSTRAINT pk_trajectory PRIMARY KEY (id);

CREATE SEQUENCE S_TRAJECTORY
  MINVALUE 0
  START WITH 1
  NO CYCLE;

ALTER TABLE schedule_detail DROP COLUMN stream_id;
