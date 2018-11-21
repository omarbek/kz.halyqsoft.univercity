ALTER TABLE week_day
  ADD COLUMN value NUMERIC(1) NOT NULL DEFAULT 0;

UPDATE week_day
SET value = 1
WHERE id = 1;
UPDATE week_day
SET value = 2
WHERE id = 2;
UPDATE week_day
SET value = 3
WHERE id = 3;
UPDATE week_day
SET value = 4
WHERE id = 4;
UPDATE week_day
SET value = 5
WHERE id = 5;
UPDATE week_day
SET value = 6
WHERE id = 6;