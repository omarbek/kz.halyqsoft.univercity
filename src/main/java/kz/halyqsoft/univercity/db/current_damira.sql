CREATE TABLE subject_module
(
  id         BIGINT      NOT NULL,
  module_name            character varying(64)            not null,
  module_short_name      char(10)                not null
);

ALTER TABLE subject_module
  ADD CONSTRAINT pk_subject_module PRIMARY KEY (id);


insert into subject_module (id, module_name,module_short_name) values (1, 'Общие модули','ОМ');
insert into subject_module (id, module_name,module_short_name) values (2, 'Модули специальности','МС');
insert into subject_module (id, module_name,module_short_name) values (3, 'Дополнительные модули ','ДП');

delete FROM subject;

ALTER TABLE subject
  ADD module_id BIGINT NOT NULL ;

ALTER TABLE ONLY subject
  ADD CONSTRAINT fk_subject_subject_module FOREIGN KEY (module_id)
REFERENCES subject_module (id)
ON UPDATE RESTRICT ON DELETE RESTRICT;

ALTER TABLE subject ALTER COLUMN code DROP NOT NULL;
ALTER TABLE subject ALTER COLUMN subject_cycle_id DROP NOT NULL;