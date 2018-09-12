DROP INDEX idx_t_student_education;

CREATE UNIQUE INDEX idx_t_student_education
  ON student_education (
    student_id ASC,
    faculty_id ASC,
    chair_id ASC,
    speciality_id ASC,
    study_year_id ASC,
    groups_id ASC
  );