ALTER TABLE subject
  ADD COLUMN class_room NUMERIC(3);

ALTER TABLE subject
  ALTER COLUMN academic_formula_id DROP NOT NULL;
ALTER TABLE subject
  ALTER COLUMN lc_count DROP NOT NULL;
ALTER TABLE subject
  ALTER COLUMN lb_count DROP NOT NULL;
ALTER TABLE subject
  ALTER COLUMN pr_count DROP NOT NULL;
ALTER TABLE subject
  ALTER COLUMN with_teacher_count DROP NOT NULL;

CREATE TABLE practice_breakdown (
  id           BIGINT               NOT NULL,
  formula      CHARACTER VARYING(4) NOT NULL,
  first_digit  NUMERIC(2)           NOT NULL,
  second_digit NUMERIC(2)           NOT NULL
);

ALTER TABLE practice_breakdown
  ADD CONSTRAINT pk_practice_breakdown PRIMARY KEY (id);

CREATE UNIQUE INDEX idx_practice_breakdown
  ON practice_breakdown (
    formula ASC
  );

create sequence s_practice_breakdown
minvalue 0
start with 1
no cycle;

INSERT INTO practice_breakdown VALUES (nextval('s_practice_breakdown'), '1:1', 1, 1);
INSERT INTO practice_breakdown VALUES (nextval('s_practice_breakdown'), '1:5', 1, 5);
INSERT INTO practice_breakdown VALUES (nextval('s_practice_breakdown'), '1:0', 1, 0);

ALTER TABLE subject
  ADD COLUMN PRACTICE_BREAKDOWN_ID BIGINT;
ALTER TABLE subject
  ADD CONSTRAINT fk_subject_practice_breakdown FOREIGN KEY (PRACTICE_BREAKDOWN_ID)
REFERENCES PRACTICE_BREAKDOWN (id)
ON DELETE RESTRICT ON UPDATE RESTRICT;

--domik