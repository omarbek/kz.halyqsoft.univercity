ALTER SEQUENCE s_tasks RESTART WITH 20;
INSERT INTO tasks VALUES (nextval('s_tasks'),
                          'KK=Добавить модуль к роли;RU=Добавить модуль к роли;EN=Add task to role;',
                          'KK=Добавить модуль к роли;RU=Добавить модуль к роли;EN=Add task to role;', FALSE, 214,
                          'kz.halyqsoft.univercity.modules.creation.TaskRoleBindingView', 3, NULL,
                          'KK=Добавить модуль к роли;RU=Добавить модуль к роли;EN=Add task to role;', TRUE);
INSERT INTO tasks VALUES (nextval('s_tasks'),
                          'KK=Дать роль пользователю;RU=KK=Дать роль пользователю;EN=Add role to user;',
                          'KK=Дать роль пользователю;RU=KK=Дать роль пользователю;EN=Add role to user;', FALSE, 215,
                          'kz.halyqsoft.univercity.modules.creation.UserRoleBindingView', 3, NULL,
                          'KK=Дать роль пользователю;RU=KK=Дать роль пользователю;EN=Add role to user;', TRUE);

ALTER SEQUENCE s_role_tasks RESTART WITH 21;
INSERT INTO role_tasks VALUES (nextval('s_role_tasks'), 3, 20, TRUE);
INSERT INTO role_tasks VALUES (nextval('s_role_tasks'), 3, 21, TRUE);

CREATE TABLE student_diploma_type (
  id        BIGINT                NOT NULL,
  type_name CHARACTER VARYING(64) NOT NULL
);

ALTER TABLE student_diploma_type
  ADD CONSTRAINT pk_student_diploma_type PRIMARY KEY (id);

INSERT INTO student_diploma_type VALUES (1, 'Очный');
INSERT INTO student_diploma_type VALUES (2, 'Заочный');
INSERT INTO student_diploma_type VALUES (3, 'Вечерний');

ALTER TABLE student
  ADD COLUMN diploma_type_id BIGINT NOT NULL DEFAULT 1;

ALTER TABLE ONLY student
  ADD CONSTRAINT fk_student_student_diploma_type FOREIGN KEY (diploma_type_id)
REFERENCES student_diploma_type (id)
ON DELETE RESTRICT ON UPDATE RESTRICT;