alter table subject_requisite
    DROP COLUMN requisite_id;

ALTER TABLE subject_requisite
    add COLUMN pair_subject_id BIGINT NOT NULL;

ALTER TABLE subject_requisite
  ADD CONSTRAINT fk_subject_requisite_pair_subject FOREIGN KEY (pair_subject_id)
REFERENCES pair_subject (id)
ON DELETE RESTRICT ON UPDATE RESTRICT;

INSERT INTO subject_requisite
  SELECT
    nextval('s_subject_requisite'),
    prerequisite_id,
    TRUE,
    id
  FROM pair_subject
  WHERE prerequisite_id IS NOT NULL;

ALTER TABLE pair_subject
    DROP COLUMN prerequisite_id;

INSERT INTO subject_requisite
  SELECT
    nextval('s_subject_requisite'),
    postrequisite_id,
    FALSE,
    id
  FROM pair_subject
  WHERE pair_subject.postrequisite_id IS NOT NULL;

ALTER TABLE pair_subject
  DROP COLUMN postrequisite_id;