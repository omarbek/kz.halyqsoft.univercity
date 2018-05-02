ALTER SEQUENCE s_tasks RESTART WITH 20;
ALTER SEQUENCE s_role_tasks RESTART WITH 21;
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
INSERT INTO role_tasks VALUES (nextval('s_role_tasks'), 3, 22, TRUE);
INSERT INTO role_tasks VALUES (nextval('s_role_tasks'), 3, 23, TRUE);
