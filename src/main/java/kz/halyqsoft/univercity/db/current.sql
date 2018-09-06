ALTER TABLE news
  ALTER COLUMN global_news TYPE BOOLEAN
  USING CASE WHEN global_news = 0
  THEN FALSE
        WHEN global_news = 1
          THEN TRUE
        ELSE NULL
        END;

ALTER TABLE news
  ALTER COLUMN DELETED TYPE BOOLEAN
  USING CASE WHEN DELETED = 0
  THEN FALSE
        WHEN DELETED = 1
          THEN TRUE
        ELSE NULL
        END;

CREATE TABLE student_attendance_log (
  id      BIGINT    NOT NULL,
  user_id BIGINT    NOT NULL,
  room_id BIGINT    NOT NULL,
  created TIMESTAMP NOT NULL
);

CREATE SEQUENCE s_student_attendance_log
MINVALUE 0
START WITH 1
NO CYCLE;

ALTER TABLE student_attendance_log
  ADD CONSTRAINT pk_student_attendance_log PRIMARY KEY (id);

ALTER TABLE student_attendance_log
  ADD CONSTRAINT fk_student_attendance_log_users FOREIGN KEY (user_id)
REFERENCES users (id)
ON DELETE RESTRICT ON UPDATE RESTRICT;

ALTER TABLE student_attendance_log
  ADD CONSTRAINT fk_student_attendance_log_room FOREIGN KEY (room_id)
REFERENCES room (id)
ON DELETE RESTRICT ON UPDATE RESTRICT;

ALTER TABLE accountant_price
  ADD price_in_letters_kaz VARCHAR(255) NOT NULL DEFAULT 0;