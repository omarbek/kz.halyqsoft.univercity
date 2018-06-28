ALTER TABLE subject
  ADD COLUMN lc_count NUMERIC(3) NOT NULL DEFAULT 0;
ALTER TABLE subject
  ADD COLUMN pr_count NUMERIC(3) NOT NULL DEFAULT 0;
ALTER TABLE subject
  ADD COLUMN lb_count NUMERIC(3) NOT NULL DEFAULT 0;
ALTER TABLE subject
  ADD COLUMN with_teacher_count NUMERIC(3) NOT NULL DEFAULT 0;
ALTER TABLE subject
  ADD COLUMN own_count NUMERIC(3) NOT NULL DEFAULT 0;
ALTER TABLE subject
  ADD COLUMN total_count NUMERIC(3) NOT NULL DEFAULT 0;

UPDATE subject
SET with_teacher_count = (
  SELECT cred.credit * 15
  FROM subject subj
    INNER JOIN creditability cred ON cred.id = subj.creditability_id
  WHERE subj.id = subject.id
),
  own_count            = (
    SELECT cred.credit * 15
    FROM subject subj
      INNER JOIN creditability cred ON cred.id = subj.creditability_id
    WHERE subj.id = subject.id
  ),
  total_count          = (
    SELECT cred.credit * 15 * 3
    FROM subject subj
      INNER JOIN creditability cred ON cred.id = subj.creditability_id
    WHERE subj.id = subject.id
  );

UPDATE subject
SET lc_count = (
  SELECT form.lc_count * 15
  FROM subject subj
    INNER JOIN academic_formula form ON form.id = subj.academic_formula_id
  WHERE subj.id = subject.id
),lb_count = (
  SELECT form.lb_count * 15
  FROM subject subj
    INNER JOIN academic_formula form ON form.id = subj.academic_formula_id
  WHERE subj.id = subject.id
),pr_count = (
  SELECT form.pr_count * 15
  FROM subject subj
    INNER JOIN academic_formula form ON form.id = subj.academic_formula_id
  WHERE subj.id = subject.id
);