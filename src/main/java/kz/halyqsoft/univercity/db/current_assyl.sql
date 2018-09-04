-- CHAT

CREATE TABLE chat (
  id        BIGINT                NOT NULL,
  first_user_id BIGINT NOT NULL,
  second_user_id BIGINT NOT NULL,
  accepted BOOLEAN
);

ALTER TABLE chat
  ADD CONSTRAINT pk_chat PRIMARY KEY (id);

ALTER TABLE ONLY chat
  ADD CONSTRAINT fk_chat_users_first FOREIGN KEY (first_user_id)
REFERENCES users (id)
ON UPDATE RESTRICT ON DELETE RESTRICT;


ALTER TABLE ONLY chat
  ADD CONSTRAINT fk_chat_users_second FOREIGN KEY (second_user_id)
REFERENCES users (id)
ON UPDATE RESTRICT ON DELETE RESTRICT;

create sequence S_CHAT
  minvalue 0
  start with 1
  no cycle;

-- MESSAGE

CREATE TABLE message (
  id        BIGINT                NOT NULL,
  chat_id BIGINT NOT NULL,
  content TEXT,
  created TIMESTAMP NOT NULL,
  from_first BOOLEAN NOT NULL
);

DROP TABLE message;

ALTER TABLE message
  ADD CONSTRAINT pk_message PRIMARY KEY (id);

ALTER TABLE ONLY message
  ADD CONSTRAINT fk_message_chat FOREIGN KEY (chat_id)
REFERENCES chat (id)
ON UPDATE RESTRICT ON DELETE RESTRICT;

create sequence S_MESSAGE
  minvalue 0
  start with 1
  no cycle;

-- NON ADMISSION

CREATE TABLE non_admission_cause (
  id BIGINT NOT NULL ,
  name VARCHAR(255) NOT NULL
);

ALTER TABLE non_admission_cause ADD CONSTRAINT pk_non_admission_cause PRIMARY KEY (id);

CREATE SEQUENCE s_non_admission_cause
  MINVALUE 0
  start WITH 1
  no CYCLE ;

CREATE TABLE non_admission_exam (
  id BIGINT NOT NULL ,
  student_id BIGINT NOT NULL,
  non_admission_cause_id BIGINT NOT NULL,
  semester_data_id BIGINT NOT NULL,
  created TIMESTAMP NOT NULL DEFAULT now()
);

ALTER TABLE non_admission_exam ADD CONSTRAINT pk_non_admission_exam PRIMARY KEY (id);

CREATE SEQUENCE s_non_admission_exam
  MINVALUE 0
  START WITH 1
  NO CYCLE ;

ALTER TABLE non_admission_exam ADD CONSTRAINT fk_non_admission_exam_student
FOREIGN KEY (student_id) REFERENCES student(id)
ON DELETE RESTRICT
ON UPDATE RESTRICT;

ALTER TABLE non_admission_exam ADD CONSTRAINT fk_non_admission_exam_semester_data
  FOREIGN KEY (semester_data_id) REFERENCES semester_data(id)
ON DELETE RESTRICT
ON UPDATE RESTRICT;

ALTER TABLE non_admission_exam ADD CONSTRAINT fk_non_admission_exam_non_admission_cause
  FOREIGN KEY (non_admission_cause_id) REFERENCES non_admission_cause(id)
ON DELETE RESTRICT
ON UPDATE RESTRICT ;


CREATE TABLE non_admission_subject (
  id BIGINT NOT NULL ,
  non_admission_exam_id BIGINT NOT NULL,
  subject_id BIGINT NOT NULL
);

ALTER TABLE non_admission_subject ADD CONSTRAINT pk_non_admission_subject
FOREIGN KEY (non_admission_exam_id) REFERENCES non_admission_exam(id);

CREATE SEQUENCE s_non_admission_subject
  MINVALUE 0
  START WITH 1
  NO CYCLE ;

ALTER TABLE non_admission_subject ADD CONSTRAINT fk_non_admission_subject_non_admission_exam
  FOREIGN KEY (non_admission_exam_id) REFERENCES non_admission_exam(id)
ON UPDATE RESTRICT ON DELETE RESTRICT ;

ALTER TABLE non_admission_subject ADD CONSTRAINT fk_non_admission_subject_subject
  FOREIGN KEY (subject_id) REFERENCES subject(id)
ON DELETE RESTRICT ON UPDATE RESTRICT;

CREATE SEQUENCE s_stream MINVALUE 0 START WITH 1 no CYCLE ;

DROP VIEW v_groups_creation_needed;

CREATE VIEW v_groups_creation_needed
  AS (select
       grs.id,
       s2.id speciality_id,
       grs.language_id ,
       vs.entrance_year_id,
       grs.study_year_id,
       c2.corpus_id
     FROM groups grs
       INNER JOIN speciality s2
         ON grs.speciality_id = s2.id
       INNER JOIN speciality_corpus c2
         ON s2.id = c2.speciality_id
       INNER JOIN v_student vs
         ON vs.groups_id = grs.id
     WHERE
       grs.deleted = FALSE
       and s2.deleted = FALSE);

ALTER TABLE groups ALTER COLUMN study_year_id DROP NOT NULL ;

UPDATE tasks SET name = 'KK=Жатақхана;RU=Общежитие;EN=Dorm;', title = 'KK=Жатақхана;RU=Общежитие;EN=Dorm;', task_type = false, task_order = 221, class_path = 'kz.halyqsoft.univercity.modules.dorm.DormView', parent_id = 3, icon_name = null, descr = 'KK=Жатақхана;RU=Общежитие;EN=Dorm;', visible = true WHERE id = 44;

-- INSERT INTO student_status VALUES (nextval('s_student_status' ), 'Оставлен на перекурс');

ALTER TABLE user_arrival ADD COLUMN manually_signed BOOLEAN NULL DEFAULT FALSE;

ALTER TABLE practice_information ADD COLUMN entrance_year_id BIGINT NOT NULL ;
ALTER TABLE practice_information
  ADD CONSTRAINT fk_practice_information_entrance_year
FOREIGN KEY (entrance_year_id) REFERENCES entrance_year(id);

ALTER TABLE practice_information ADD CONSTRAINT  unique_groups_entrance_year UNIQUE (groups_id,entrance_year_id);