delete from lesson_detail;
delete from lesson;

update shift_study_year set shift_id=1;
insert into shift_study_year values (nextval('s_shift_study_year'),2,2);
insert into shift_study_year values (nextval('s_shift_study_year'),3,2);
insert into shift_study_year values (nextval('s_shift_study_year'),4,1);

ALTER TABLE schedule_detail ADD COLUMN stream_id int;

ALTER TABLE ONLY schedule_detail
  ADD CONSTRAINT fk_t_schedule_detail_stream FOREIGN KEY (stream_id)
REFERENCES stream (id)
ON DELETE RESTRICT ON UPDATE RESTRICT;

CREATE OR REPLACE VIEW V_TEACHER_SUBJECT_MODULE AS
  SELECT
         teacher_subject.ID,
         teacher_subject.EMPLOYEE_ID,
         subj.NAME_RU SUBJECT_NAME,
         credit.credit,
         subjm.module_name,
         teacher_subject.FALL,
         teacher_subject.SPRING,
         teacher_subject.SUMMER,
         teacher_subject.LOAD_PER_HOURS
  FROM TEACHER_SUBJECT teacher_subject
         INNER JOIN SUBJECT subj ON teacher_subject.SUBJECT_ID = subj.ID
         INNER JOIN creditability credit ON subj.creditability_id = credit.id
         INNER JOIN subject_module subjm ON subj.module_id = subjm.id;