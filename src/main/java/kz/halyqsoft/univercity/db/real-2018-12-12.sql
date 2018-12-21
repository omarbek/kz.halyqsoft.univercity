alter table elective_subject
  add column updated_by varchar(100);
alter table curriculum_detail
  add column updated_by varchar(100);
alter table curriculum_add_program
  add column updated_by varchar(100);
alter table curriculum_after_semester
  add column updated_by varchar(100);