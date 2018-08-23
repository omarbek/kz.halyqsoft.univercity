--set language in real

CREATE TABLE load_to_chair AS
  SELECT *
  FROM v_load_to_chair;

drop view v_load_to_chair_count_all;

CREATE OR REPLACE VIEW v_load_to_chair_count AS
  SELECT
    study_year,
    curriculum_id,
    subj.chair_id,
    sum(load.lc_count)           lc_count,
    sum(load.pr_count)           pr_count,
    sum(load.lb_count)           lb_count,
    sum(load.with_teacher_count) with_teacher_count,
    sum(rating_count)            rating_count,
    sum(exam_count)              exam_count,
    sum(control_count)           control_count,
    sum(course_work_count)       course_work_count,
    sum(diploma_count)           diploma_count,
    sum(practice_count)          practice_count,
    sum(mek)                     mek,
    sum(protect_diploma_count)   protect_diploma_count,
    sum(load.total_count)        total_count
  FROM load_to_chair load INNER JOIN subject subj ON subj.id = load.subject_id
  GROUP BY study_year, curriculum_id,subj.chair_id;

CREATE OR REPLACE VIEW v_load_to_chair_count_all AS
  SELECT
    curriculum_id,
    chair_id,
    sum(lc_count)              lc_count,
    sum(pr_count)              pr_count,
    sum(lb_count)              lb_count,
    sum(with_teacher_count)    with_teacher_count,
    sum(rating_count)          rating_count,
    sum(exam_count)            exam_count,
    sum(control_count)         control_count,
    sum(course_work_count)     course_work_count,
    sum(diploma_count)         diploma_count,
    sum(practice_count)        practice_count,
    sum(mek)                   mek,
    sum(protect_diploma_count) protect_diploma_count,
    sum(total_count)           total_count
  FROM v_load_to_chair_count
  GROUP BY curriculum_id, chair_id;

create sequence s_stream
minvalue 0
start with 1
no cycle;

create sequence s_stream_group
minvalue 0
start with 1
no cycle;

ALTER TABLE load_to_chair
  DROP COLUMN subject_name;