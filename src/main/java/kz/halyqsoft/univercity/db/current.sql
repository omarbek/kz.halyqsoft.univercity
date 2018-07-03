INSERT INTO TASKS (CLASS_PATH, DESCR, ICON_NAME, NAME, TASK_ORDER, TASK_TYPE, TITLE, VISIBLE, ID, PARENT_ID)
VALUES ('kz.halyqsoft.univercity.modules.checkstudents.CheckStudentsView',
        'KK=Удалить неполных абитуриентов;RU=Удалить неполных абитуриентов;EN=Delete lacking applicants;',
        NULL, 'KK=Удалить неполных абитуриентов;RU=Удалить неполных абитуриентов;EN=Delete lacking applicants;', 304,
        FALSE, 'KK=Удалить неполных абитуриентов;RU=Удалить неполных абитуриентов;EN=Delete lacking applicants;', TRUE,
        nextval('s_tasks'), 7);

INSERT INTO TASKS (CLASS_PATH, DESCR, ICON_NAME, NAME, TASK_ORDER, TASK_TYPE, TITLE, VISIBLE, ID, PARENT_ID)
VALUES ('kz.halyqsoft.univercity.modules.returnstudent.ReturnStudentView',
        'KK=Сделать абитуриентом;RU=Сделать абитуриентом;EN=Make the applicant;',
        NULL, 'KK=Сделать абитуриентом;RU=Сделать абитуриентом;EN=Make the applicant;', 404,
        FALSE, 'KK=Сделать абитуриентом;RU=Сделать абитуриентом;EN=Make the applicant;', TRUE,
        nextval('s_tasks'), 26);

INSERT INTO TASKS (CLASS_PATH, DESCR, ICON_NAME, NAME, TASK_ORDER, TASK_TYPE, TITLE, VISIBLE, ID, PARENT_ID)
VALUES ('kz.halyqsoft.univercity.modules.search.SearchByCardView',
        'KK=Поиск по карте;RU=Поиск по карте;EN=Search by card;',
        NULL, 'KK=Поиск по карте;RU=Поиск по карте;EN=Search by card;', 405,
        FALSE, 'KK=Поиск по карте;RU=Поиск по карте;EN=Search by card;', TRUE,
        nextval('s_tasks'), 26);

