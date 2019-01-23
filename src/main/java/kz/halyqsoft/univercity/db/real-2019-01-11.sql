ALTER TABLE employee
  ADD retiree BOOLEAN  NOT NULL  DEFAULT FALSE ;
ALTER TABLE department
  ADD dep_language BOOLEAN  NOT NULL  DEFAULT TRUE ;

  UPDATE degree
SET degree_name='кандидат наук'
WHERE id=1;

UPDATE degree
SET degree_name='доктор наук'
WHERE id=2;

UPDATE degree
SET degree_name='доктор Ph.D'
WHERE id=3;

UPDATE degree
SET degree_name='ассоциированный профессор (доцент)'
WHERE id=4;

INSERT INTO DEGREE (DEGREE_NAME, ID) VALUES ('профессор', 7);
INSERT INTO DEGREE (DEGREE_NAME, ID) VALUES ('доцент', 8);
INSERT INTO DEGREE (DEGREE_NAME, ID) VALUES ('профессоров (по новой квалификации)', 9);


------domik