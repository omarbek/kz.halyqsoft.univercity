ALTER TABLE pair_subject
  ADD COLUMN prerequisite_id BIGINT NULL;
ALTER TABLE pair_subject
  ADD COLUMN postrequisite_id BIGINT NULL;
ALTER TABLE pair_subject
  ADD COLUMN aim BIGINT NULL;
ALTER TABLE pair_subject
  ADD COLUMN competence BIGINT NULL;