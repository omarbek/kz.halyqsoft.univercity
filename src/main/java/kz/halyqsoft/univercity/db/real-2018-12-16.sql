delete from semester_period where id>3;
update semester_period set period_name='Летний' where id=3;

create sequence s_curriculum_summer
  minvalue 0
  start with 1
  no cycle;