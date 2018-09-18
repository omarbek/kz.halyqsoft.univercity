ALTER TABLE pair_subject
  ADD COLUMN prerequisite_id BIGINT NULL;
ALTER TABLE pair_subject
  ADD COLUMN postrequisite_id BIGINT NULL;
ALTER TABLE pair_subject
  ADD COLUMN aim BIGINT NULL;
ALTER TABLE pair_subject
  ADD COLUMN competence BIGINT NULL;

ALTER TABLE pair_subject
  ALTER COLUMN aim TYPE VARCHAR(255);
ALTER TABLE pair_subject
  ALTER COLUMN competence TYPE VARCHAR(255);