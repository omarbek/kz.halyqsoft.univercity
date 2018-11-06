UPDATE employee_dept
SET coordinator = FALSE
WHERE employee_id IN (SELECT
                        id
                      FROM v_coordinator
                      GROUP BY id
                      HAVING count(id) > 1);

INSERT INTO practice_type VALUES (4, 'Преддипломная');