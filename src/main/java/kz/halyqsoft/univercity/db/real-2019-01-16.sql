alter table load_to_chair
add column created_year_id bigint null;

update load_to_chair set created_year_id=2;

alter table load_to_chair
alter column created_year_id set not null;