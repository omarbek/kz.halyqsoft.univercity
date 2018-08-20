ALTER TABLE document ALTER COLUMN file_byte DROP NOT NULL ;

ALTER TABLE semester_subject
  ADD CONSTRAINT uc_semester_subject
UNIQUE (semester_data_id ,subject_id);