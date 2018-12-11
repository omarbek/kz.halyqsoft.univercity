create table privileges (
  id         bigint       not null,
  type_name  varchar(255) not null,
  can_access boolean      not null
);

ALTER TABLE ONLY privileges
  ADD CONSTRAINT pk_privileges PRIMARY KEY (id);

insert into privileges values (1,'ИУПС',true);

update curriculum set status_id=1;