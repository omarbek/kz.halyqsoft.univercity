
CREATE VIEW v_student_debts as
select s2.id,x.user_code, trim(x.LAST_NAME||' '||x.FIRST_NAME||' '||coalesce(x.MIDDLE_NAME, '')) fio,
  CASE WHEN x.id=payment.student_id then (a.price-(sum(payment.payment_sum)))
  ELSE a.price end DEBT_SUM
                from v_student x
        INNER JOIN student s2 ON x.id = s2.id
                        INNER JOIN student_diploma_type s3 ON s2.diploma_type_id = s3.id
                        INNER JOIN accountant_price a ON s3.id = a.student_diploma_type_id
                        LEFT JOIN student_payment as payment ON payment.student_id=s2.id
                where  a.contract_payment_type_id=2 AND
                a.level_id=x.level_id and x.deleted=false  and a.deleted=FALSE
GROUP BY payment.student_id,s2.id,x.user_code,x.last_name,x.first_name,x.middle_name,x.id,a.price;



________________________________________________________________________________


ALTER TABLE EMPLOYEE_DEPT
  ADD priority BOOLEAN;


DROP VIEW V_EMPLOYEE_DEPT;

CREATE OR REPLACE VIEW V_EMPLOYEE_DEPT AS
  SELECT
    empl_dept.ID,
    empl_dept.EMPLOYEE_ID,
    empl_dept.EMPLOYEE_TYPE_ID,
    empl_type.TYPE_NAME EMPLOYEE_TYPE_NAME,
    empl_dept.DEPT_ID,
    dep.DEPT_NAME,
    dep.DEPT_SHORT_NAME,
    empl_dept.POST_ID,
    post.POST_NAME,
    empl_dept.LIVE_LOAD,
    empl_dept.WAGE_RATE,
    empl_dept.RATE_LOAD,
    empl_dept.HOUR_COUNT,
    empl_dept.HIRE_DATE,
    empl_dept.DISMISS_DATE,
    empl_dept.ADVISER,
    empl_dept.PARENT_ID,
    empl_dept.lecturer,
    empl_dept.priority
  FROM EMPLOYEE_DEPT empl_dept INNER JOIN EMPLOYEE_TYPE empl_type ON empl_dept.EMPLOYEE_TYPE_ID = empl_type.ID
    INNER JOIN DEPARTMENT dep ON empl_dept.DEPT_ID = dep.ID
    LEFT JOIN POST post ON empl_dept.POST_ID = post.ID
    INNER JOIN EMPLOYEE empl ON empl_dept.EMPLOYEE_ID = empl.ID;

