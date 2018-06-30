ALTER TABLE STUDENT_SCHEDULE
  ALTER COLUMN DELETED TYPE BOOLEAN
  USING CASE WHEN DELETED = 0
  THEN FALSE
        WHEN DELETED = 1
          THEN TRUE
        ELSE NULL
        END;

INSERT INTO TASKS (CLASS_PATH, DESCR, ICON_NAME, NAME, TASK_ORDER, TASK_TYPE, TITLE, VISIBLE, ID,
                   PARENT_ID)
VALUES ('kz.halyqsoft.univercity.modules.registration.student.RegistrationView',
        'KK=ИУПС;RU=ИУПС;EN=Individual educational plan of student;', null,
        'KK=ИУПС;RU=ИУПС;EN=Individual educational plan of student;', 506, false,
        'KK=ИУПС;RU=ИУПС;EN=Individual educational plan of student;', true, nextval('s_tasks'), 29);

ALTER TABLE STUDENT_SUBJECT
  ALTER COLUMN DELETED TYPE BOOLEAN
  USING CASE WHEN DELETED = 0
  THEN FALSE
        WHEN DELETED = 1
          THEN TRUE
        ELSE NULL
        END;

delete from elective_binded_subject;
alter table elective_binded_subject add COLUMN semester_id BIGINT NOT NULL;
alter table  elective_binded_subject
  add constraint fk_elective_binded_subject_semester foreign key (semester_id)
references  semester (id)
on delete restrict on update restrict;

CREATE TABLE catalog_elective_subjects (
  id               BIGINT    NOT NULL,
  speciality_id    BIGINT    NOT NULL,
  entrance_year_id BIGINT    NOT NULL,
  created          TIMESTAMP NOT NULL,
  updated          TIMESTAMP,
  deleted          BOOLEAN   NOT NULL
);

ALTER TABLE catalog_elective_subjects
  ADD CONSTRAINT pk_catalog_elective_subjects PRIMARY KEY (id);

ALTER TABLE catalog_elective_subjects
  ADD CONSTRAINT fk_catalog_elective_subjects_speciality FOREIGN KEY (speciality_id)
REFERENCES speciality (id)
ON DELETE RESTRICT ON UPDATE RESTRICT;

ALTER TABLE catalog_elective_subjects
  ADD CONSTRAINT fk_catalog_elective_subjects_entrance_year FOREIGN KEY (entrance_year_id)
REFERENCES entrance_year (id)
ON DELETE RESTRICT ON UPDATE RESTRICT;

CREATE SEQUENCE s_catalog_elective_subjects
MINVALUE 0
START WITH 1
NO CYCLE;

ALTER table elective_binded_subject drop COLUMN created;
delete from elective_binded_subject;
ALTER table elective_binded_subject add COLUMN catalog_elective_subjects_id BIGINT not null;

ALTER TABLE elective_binded_subject
  ADD CONSTRAINT fk_elective_binded_subject_catalog_elective_subjects FOREIGN KEY (catalog_elective_subjects_id)
REFERENCES catalog_elective_subjects (id)
ON DELETE RESTRICT ON UPDATE RESTRICT;

INSERT INTO TASKS (CLASS_PATH, DESCR, ICON_NAME, NAME, TASK_ORDER, TASK_TYPE, TITLE, VISIBLE, ID, PARENT_ID)
VALUES ('kz.halyqsoft.univercity.modules.group.GroupsView', 'KK=Группы;RU=Группы;EN=Groups;', null,
        'KK=Группы;RU=Группы;EN=Groups;', 507, false, 'KK=Группы;RU=Группы;EN=Groups;', true, nextval('s_tasks'), 29);

CREATE TABLE groups (
  id  BIGINT  NOT NULL,
  speciality_id BIGINT NOT NULL,
  name CHARACTER VARYING(150) NOT NULL,
  orders BIGINT
);

ALTER TABLE groups
  ADD CONSTRAINT pk_group PRIMARY KEY (id);

ALTER TABLE ONLY groups
  ADD CONSTRAINT fk_group_speciality FOREIGN KEY (speciality_id)
REFERENCES speciality (id)
ON UPDATE RESTRICT ON DELETE RESTRICT;

create sequence S_GROUPS
minvalue 0
start with 1
no cycle;

ALTER TABLE groups ADD COLUMN deleted BOOLEAN NOT NULL ;

ALTER TABLE groups ADD COLUMN created TIMESTAMP DEFAULT now();

ALTER TABLE student_education ADD COLUMN groups_id BIGINT;

ALTER TABLE ONLY student_education
  ADD CONSTRAINT fk_student_education_groups FOREIGN KEY (groups_id)
REFERENCES groups (id)
ON UPDATE RESTRICT ON DELETE RESTRICT;

CREATE TABLE speciality_corpus (
  id            BIGINT NOT NULL,
  speciality_id BIGINT NOT NULL,
  corpus_id     BIGINT NOT NULL
);

ALTER TABLE ONLY speciality_corpus
  ADD CONSTRAINT pk_speciality_corpus PRIMARY KEY (id);

CREATE SEQUENCE s_speciality_corpus
MINVALUE 0
START WITH 1
NO CYCLE;

ALTER TABLE speciality_corpus
  ADD CONSTRAINT fk_speciality_corpus_speciality FOREIGN KEY (speciality_id)
REFERENCES speciality (id)
ON DELETE RESTRICT ON UPDATE RESTRICT;

ALTER TABLE speciality_corpus
  ADD CONSTRAINT fk_speciality_corpus_corpus FOREIGN KEY (corpus_id)
REFERENCES corpus (id)
ON DELETE RESTRICT ON UPDATE RESTRICT;

CREATE UNIQUE INDEX idx_speciality_corpus
  ON speciality_corpus (
    speciality_id ASC,
    corpus_id ASC
  );

INSERT INTO TASKS (CLASS_PATH, DESCR, ICON_NAME, NAME, TASK_ORDER, TASK_TYPE, TITLE, VISIBLE, ID, PARENT_ID)
VALUES ('kz.halyqsoft.univercity.modules.bindingspecialitytocorpus.BindingSpecialityToCorpusView',
        'KK=Мамандықты ғимаратқа қосу;RU=Привязка специальности корпусам;EN=Binding speciality to corpus;',
        NULL, 'KK=Мамандықты ғимаратқа қосу;RU=Привязка специальности корпусам;EN=Binding speciality to corpus;',
        220, FALSE, 'KK=Мамандықты ғимаратқа қосу;RU=Привязка специальности корпусам;EN=Binding speciality to corpus;',
        TRUE, nextval('s_tasks'), 3);


