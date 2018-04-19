DROP SEQUENCE s_vuser;

INSERT INTO tasks VALUES (18, 'KK=Отчеттар;RU=Отчеты;EN=Reports;', 'KK=Отчеттар;RU=Отчеты;EN=Reports;', FALSE, 212,
                          'kz.halyqsoft.univercity.modules.reports.ReportView', 3, NULL,
                          'KK=Отчеттар;RU=Отчеты;EN=Reports;', TRUE);

INSERT INTO role_tasks VALUES (19, 3, 18, TRUE);

UPDATE student set category_id=3;

CREATE OR REPLACE VIEW v_coordinator AS
  SELECT
    empl.id,
    trim(usr.LAST_NAME || ' ' || usr.FIRST_NAME || ' ' || coalesce(usr.MIDDLE_NAME, '')) FIO,
    usr.code,
    empl_dept.dept_id,
    dep.dept_name,
    empl_dept.post_id,
    post.post_name
  FROM employee empl
    INNER JOIN users usr ON empl.id = usr.id
    INNER JOIN employee_dept empl_dept ON empl.id = empl_dept.employee_id
    INNER JOIN department dep ON empl_dept.dept_id = dep.id
    LEFT JOIN post post ON empl_dept.post_id = post.id
  WHERE empl.employee_status_id = 1 AND usr.locked = FALSE AND LENGTH(usr.last_name) > 0
        AND empl_dept.dismiss_date IS NULL AND dep.deleted = FALSE;

ALTER TABLE student
  ADD COLUMN coordinator_id BIGINT;
ALTER TABLE ONLY student
  ADD CONSTRAINT fk_t_student_coordinator FOREIGN KEY (coordinator_id)
REFERENCES employee (id)
ON DELETE RESTRICT ON UPDATE RESTRICT;