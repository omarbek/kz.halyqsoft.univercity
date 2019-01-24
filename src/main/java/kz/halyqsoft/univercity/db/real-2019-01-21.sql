create table constants
(
  id        bigint         not null,
  type_name varchar(255)   not null,
  value     numeric(10, 2) not null
);

create sequence s_constants
  minvalue 0
  start with 1
  no cycle;

alter table constants
  add constraint pk_constants primary key (id);

insert into constants values (nextval('s_constants'),'СРСП',5);
insert into constants values (nextval('s_constants'),'Рейтинг',0.25);
insert into constants values (nextval('s_constants'),'Билет',0.25);
insert into constants values (nextval('s_constants'),'Экзамен',2);
insert into constants values (nextval('s_constants'),'Конрольная работа',0.2);
insert into constants values (nextval('s_constants'),'Курсовая работа',0.25);
insert into constants values (nextval('s_constants'),'Дипломная работа',12);
insert into constants values (nextval('s_constants'),'Гос экзамен',0.5);
insert into constants values (nextval('s_constants'),'Защита диплома',0.6);

CREATE OR REPLACE VIEW v_load_to_chair_with_groups AS
SELECT vltc.id,
       vltc.subject_id,
       vltc.curriculum_id,
       vltc.study_year_id,
       vltc.stream_id,
       vltc.group_id,
       CASE
         WHEN string_agg(g.name, ',') ISNULL
           THEN ''
         ELSE string_agg(g.name, ',') END AS groups,
       vltc.semester_id,
       vltc.student_number,
       vltc.credit,
       vltc.lc_count,
       vltc.pr_count,
       vltc.lb_count,
       vltc.with_teacher_count,
       vltc.rating_count,
       vltc.exam_count,
       vltc.control_count,
       vltc.course_work_count,
       vltc.diploma_count,
       vltc.practice_count,
       vltc.mek,
       vltc.protect_diploma_count,
       vltc.total_count,
       vltc.created_year_id
FROM load_to_chair vltc
       LEFT JOIN
     stream_group sg ON sg.stream_id = vltc.stream_id
       LEFT JOIN groups g ON sg.group_id = g.id
GROUP BY vltc.id,
         vltc.subject_id,
         vltc.curriculum_id,
         vltc.study_year_id,
         vltc.stream_id,
         vltc.group_id,
         vltc.semester_id,
         vltc.student_number,
         vltc.credit,
         vltc.lc_count,
         vltc.pr_count,
         vltc.lb_count,
         vltc.with_teacher_count,
         vltc.rating_count,
         vltc.exam_count,
         vltc.control_count,
         vltc.course_work_count,
         vltc.diploma_count,
         vltc.practice_count,
         vltc.mek,
         vltc.protect_diploma_count,
         vltc.total_count,
         vltc.created_year_id;

CREATE OR REPLACE VIEW v_load_to_chair_count AS
SELECT floor(random() * (1000) + 1) :: BIGINT id,
       curriculum_id,
       load.semester_id,
       study_year,
       sum(load.lc_count)                     lc_count,
       sum(load.pr_count)                     pr_count,
       sum(load.lb_count)                     lb_count,
       sum(load.with_teacher_count)           with_teacher_count,
       sum(rating_count)                      rating_count,
       sum(exam_count)                        exam_count,
       sum(control_count)                     control_count,
       sum(course_work_count)                 course_work_count,
       sum(diploma_count)                     diploma_count,
       sum(practice_count)                    practice_count,
       sum(mek)                               mek,
       sum(protect_diploma_count)             protect_diploma_count,
       sum(load.total_count)                  total_count,
       created_year_id
FROM load_to_chair load
       INNER JOIN subject subj ON subj.id = load.subject_id
       INNER JOIN study_year year on year.id = load.study_year_id
GROUP BY study_year, load.semester_id, curriculum_id,created_year_id;

CREATE OR REPLACE VIEW v_load_to_chair_count_all AS
SELECT floor(random() * (1000) + 1) :: BIGINT id,
       curriculum_id,
       semester_id,
       sum(lc_count)                          lc_count,
       sum(pr_count)                          pr_count,
       sum(lb_count)                          lb_count,
       sum(with_teacher_count)                with_teacher_count,
       sum(rating_count)                      rating_count,
       sum(exam_count)                        exam_count,
       sum(control_count)                     control_count,
       sum(course_work_count)                 course_work_count,
       sum(diploma_count)                     diploma_count,
       sum(practice_count)                    practice_count,
       sum(mek)                               mek,
       sum(protect_diploma_count)             protect_diploma_count,
       sum(total_count)                       total_count,
       created_year_id
FROM v_load_to_chair_count
GROUP BY semester_id, curriculum_id,created_year_id;