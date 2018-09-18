ALTER TABLE pair_subject
  ADD COLUMN prerequisite_id BIGINT NULL;
ALTER TABLE pair_subject
  ADD COLUMN postrequisite_id BIGINT NULL;
ALTER TABLE pair_subject
  ADD COLUMN aim BIGINT NULL;
ALTER TABLE pair_subject
  ADD COLUMN competence BIGINT NULL;

ALTER TABLE pair_subject ADD COLUMN code BIGINT NULL;

ALTER TABLE ONLY pair_subject
  ADD CONSTRAINT fk_pair_subject_postrequisite FOREIGN KEY (postrequisite_id)
REFERENCES subject (id) ON UPDATE RESTRICT ON DELETE RESTRICT;

ALTER TABLE ONLY pair_subject
  ADD CONSTRAINT fk_pair_subject_prerequisite FOREIGN KEY (prerequisite_id)
REFERENCES subject (id) ON UPDATE RESTRICT ON DELETE RESTRICT;