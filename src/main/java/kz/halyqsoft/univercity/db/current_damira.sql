ALTER TABLE practice_student
  ADD COLUMN employee_id BIGINT NULL;

ALTER TABLE practice_student
  ADD CONSTRAINT fk_t_practice_student_employee FOREIGN KEY (employee_id)
REFERENCES employee (id)
ON DELETE RESTRICT ON UPDATE RESTRICT;