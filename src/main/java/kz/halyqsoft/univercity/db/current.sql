create table privileges (
  id         bigint       not null,
  type_name  varchar(255) not null,
  can_access boolean      not null
);

ALTER TABLE ONLY privileges
  ADD CONSTRAINT pk_privileges PRIMARY KEY (id);

insert into privileges values (1,'ИУПС',true);


ALTER TABLE pdf_document RENAME COLUMN for_students TO for_human_resource_department;