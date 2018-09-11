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

ALTER TABLE practice_information ADD COLUMN entrance_year_id BIGINT NOT NULL ;
ALTER TABLE practice_information
  ADD CONSTRAINT fk_practice_information_entrance_year
FOREIGN KEY (entrance_year_id) REFERENCES entrance_year(id);

ALTER TABLE practice_information ADD CONSTRAINT  unique_groups_entrance_year UNIQUE (groups_id,entrance_year_id);

INSERT INTO nationality
VALUES
  (nextval('s_nationality') , 'Карачаевец');
INSERT INTO nationality
VALUES
  (nextval('s_nationality') , 'Ингуш');
INSERT INTO nationality
VALUES
  (nextval('s_nationality') , 'Араб');

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