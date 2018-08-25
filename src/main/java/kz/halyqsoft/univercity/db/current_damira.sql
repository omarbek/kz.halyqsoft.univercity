
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
