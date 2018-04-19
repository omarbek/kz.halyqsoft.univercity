DROP SEQUENCE s_vuser;

INSERT INTO tasks VALUES (18, 'KK=Отчеттар;RU=Отчеты;EN=Reports;', 'KK=Отчеттар;RU=Отчеты;EN=Reports;', FALSE, 212,
                          'kz.halyqsoft.univercity.modules.reports.ReportView', 3, NULL,
                          'KK=Отчеттар;RU=Отчеты;EN=Reports;', TRUE);

INSERT INTO role_tasks VALUES (19, 3, 18, TRUE);

UPDATE student set category_id=3;