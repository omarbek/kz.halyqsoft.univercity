ALTER TABLE pair_subject
  ADD COLUMN description varchar(8000) DEFAULT '';

ALTER TABLE subject DROP COLUMN descr ;

ALTER TABLE pair_subject
  ALTER COLUMN description SET NOT NULL;