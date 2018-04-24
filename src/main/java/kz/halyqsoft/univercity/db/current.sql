INSERT INTO tasks VALUES (19, 'KK=Творческий экзамен;RU=Творческий экзамен;EN=Creative exams;',
                          'KK=Творческий экзамен;RU=Творческий экзамен;EN=Creative exams;', FALSE, 213,
                          'kz.halyqsoft.univercity.modules.creativeexams.CreativeExamView', 3, NULL,
                          'KK=Творческий экзамен;RU=Творческий экзамен;EN=Creative exams;', TRUE);

INSERT INTO role_tasks VALUES (20, 3, 19, TRUE);

CREATE TABLE student_creative_exam
(
  id         BIGINT                 NOT NULL,
  student_id BIGINT                 NOT NULL,
  place      CHARACTER VARYING(150) NOT NULL,
  rate       NUMERIC(3)
);

ALTER TABLE student_creative_exam
  ADD CONSTRAINT pk_student_creative_exam PRIMARY KEY (id);

ALTER TABLE student_creative_exam
  ADD CONSTRAINT fk_student_creative_exam_student FOREIGN KEY (student_id)
REFERENCES student (id)
ON DELETE RESTRICT ON UPDATE RESTRICT;

CREATE TABLE creative_exam_subject
(
  id           BIGINT                NOT NULL,
  subject_name CHARACTER VARYING(32) NOT NULL
);

ALTER TABLE creative_exam_subject
  ADD CONSTRAINT pk_creative_exam_subject PRIMARY KEY (id);

CREATE TABLE student_creative_exam_subject
(
  id                       BIGINT     NOT NULL,
  student_creative_exam_id BIGINT     NOT NULL,
  creative_exam_subject_id BIGINT     NOT NULL,
  rate                     NUMERIC(2) NOT NULL
);

ALTER TABLE student_creative_exam_subject
  ADD CONSTRAINT pk_student_creative_exam_subject PRIMARY KEY (id);

CREATE UNIQUE INDEX idx_student_creative_exam_subject
  ON student_creative_exam_subject (
    student_creative_exam_id ASC,
    creative_exam_subject_id ASC
  );

ALTER TABLE student_creative_exam_subject
  ADD CONSTRAINT fk_student_creative_exam_subject_student_creative_exam FOREIGN KEY (student_creative_exam_id)
REFERENCES student_creative_exam (id)
ON DELETE RESTRICT ON UPDATE RESTRICT;

ALTER TABLE student_creative_exam_subject
  ADD CONSTRAINT fk_student_creative_exam_subject_creative_exam_subject FOREIGN KEY (creative_exam_subject_id)
REFERENCES creative_exam_subject (id)
ON DELETE RESTRICT ON UPDATE RESTRICT;

create sequence S_STUDENT_CREATIVE_EXAM_SUBJECT
minvalue 0
start with 1
no cycle;

create sequence S_STUDENT_CREATIVE_EXAM
minvalue 0
start with 1
no cycle;