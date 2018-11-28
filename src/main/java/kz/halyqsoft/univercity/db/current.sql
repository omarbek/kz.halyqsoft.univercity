delete from lesson_detail;
delete from lesson;

update shift_study_year set shift_id=1;
insert into shift_study_year values (nextval('s_shift_study_year'),2,2);
insert into shift_study_year values (nextval('s_shift_study_year'),3,2);
insert into shift_study_year values (nextval('s_shift_study_year'),4,1);

