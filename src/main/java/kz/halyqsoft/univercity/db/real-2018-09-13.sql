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

CREATE OR REPLACE VIEW V_GROUPS_CREATION_NEEDED AS
  SELECT DISTINCT
    grs.id,
    spec.id speciality_id,
    grs.language_id,
    stu.entrance_year_id,
    grs.study_year_id,
    spec_corp.corpus_id
  FROM groups grs
    INNER JOIN speciality spec ON grs.speciality_id = spec.id
    INNER JOIN speciality_corpus spec_corp ON spec.id = spec_corp.speciality_id
    INNER JOIN v_student stu ON stu.groups_id = grs.id
  WHERE grs.deleted = FALSE AND spec.deleted = FALSE;