ALTER TABLE pair_subject ADD column teacher_id BIGINT NULL;

ALTER TABLE schedule_detail ADD COLUMN stream_id int;

ALTER TABLE ONLY schedule_detail
  ADD CONSTRAINT fk_t_schedule_detail_stream FOREIGN KEY (stream_id)
REFERENCES stream (id)
ON DELETE RESTRICT ON UPDATE RESTRICT;
