alter table load_to_chair
add column created_year_id bigint null;

update load_to_chair set created_year_id=2;

alter table load_to_chair
alter column created_year_id set not null;

create table constants
(
  id        bigint         not null,
  type_name varchar(255)   not null,
  value     numeric(10, 2) not null
);

create sequence s_constants
  minvalue 0
  start with 1
  no cycle;

alter table constants
  add constraint pk_constants primary key (id);