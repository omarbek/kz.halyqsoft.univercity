ALTER TABLE child DROP COLUMN last_name;
ALTER TABLE child DROP COLUMN first_name;
ALTER TABLE child DROP COLUMN middle_name;

ALTER TABLE lost_and_found
  ADD COLUMN file_name VARCHAR(128) NOT NULL;
ALTER TABLE lost_and_found
  ADD COLUMN updated TIMESTAMP NULL;