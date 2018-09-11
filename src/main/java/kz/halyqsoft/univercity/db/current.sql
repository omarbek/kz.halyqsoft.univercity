DROP VIEW v_student CASCADE;

CREATE OR REPLACE VIEW V_STUDENT AS
  SELECT
    stu.ID,
    usr.FIRST_NAME,
    usr.LAST_NAME,
    usr.MIDDLE_NAME,
    usr.FIRST_NAME_EN,
    usr.LAST_NAME_EN,
    usr.MIDDLE_NAME_EN,
    usr.BIRTH_DATE,
    usr.SEX_ID,
    c.SEX_NAME,
    usr.MARITAL_STATUS_ID,
    d.STATUS_NAME                  MARITAL_STATUS_NAME,
    usr.NATIONALITY_ID,
    e.NATION_NAME,
    usr.CITIZENSHIP_ID,
    f.COUNTRY_NAME                 CITIZENSHIP_NAME,
    usr.CODE                       USER_CODE,
    usr.LOGIN,
    usr.EMAIL,
    usr.PHONE_MOBILE,
    stu.LEVEL_ID,
    g.LEVEL_NAME,
    stu.CATEGORY_ID,
    h.CATEGORY_NAME,
    stu.ACADEMIC_STATUS_ID,
    i.STATUS_NAME                  ACADEMIC_STATUS_NAME,
    stu.NEED_DORM,
    stu.ENTRANCE_YEAR_ID,
    j.ENTRANCE_YEAR,
    j.BEGIN_YEAR                   ENTRANCE_BEGIN_YEAR,
    j.END_YEAR                     ENTRANCE_END_YEAR,
    k.FACULTY_ID,
    l.DEPT_NAME                    FACULTY_NAME,
    l.DEPT_SHORT_NAME              FACULTY_SHORT_NAME,
    l.CODE                         FACULTY_CODE,
    k.CHAIR_ID,
    m.DEPT_NAME                    CHAIR_NAME,
    m.DEPT_SHORT_NAME              CHAIR_SHORT_NAME,
    m.CODE                         CHAIR_CODE,
    k.SPECIALITY_ID,
    n.CODE || ' - ' || n.SPEC_NAME SPECIALITY_NAME,
    n.CODE                         SPECIALITY_CODE,
    k.STUDY_YEAR_ID,
    k.EDUCATION_TYPE_ID,
    o.TYPE_NAME                    EDUCATION_TYPE_NAME,
    k.ENTRY_DATE,
    k.END_DATE,
    k.STUDENT_STATUS_ID,
    p.STATUS_NAME                  STUDENT_STATUS_NAME,
    card.id                        card_id,
    card.card_name,
    gr.id                          GROUPS_ID,
    gr.name                        GROUP_NAME,
    usr.DELETED,
    usr.CREATED,
    usr.UPDATED,
    advisor.fio                    advisor_name,
    coordinator.fio                coordinator_name,
    diplom.id                      diploma_type_id,
    diplom.type_name               diploma_type_name,
    k.language_id,
    stu.advisor_id,
    stu.coordinator_id
  FROM STUDENT stu INNER JOIN USERS usr ON stu.ID = usr.ID
    INNER JOIN SEX c ON usr.SEX_ID = c.ID
    INNER JOIN MARITAL_STATUS d ON usr.MARITAL_STATUS_ID = d.ID
    INNER JOIN NATIONALITY e ON usr.NATIONALITY_ID = e.ID
    INNER JOIN COUNTRY f ON usr.CITIZENSHIP_ID = f.ID
    INNER JOIN LEVEL g ON stu.LEVEL_ID = g.ID
    INNER JOIN STUDENT_CATEGORY h ON stu.CATEGORY_ID = h.ID
    LEFT JOIN ACADEMIC_STATUS i ON stu.ACADEMIC_STATUS_ID = i.ID
    INNER JOIN ENTRANCE_YEAR j ON stu.ENTRANCE_YEAR_ID = j.ID
    INNER JOIN STUDENT_EDUCATION k ON k.STUDENT_ID = stu.ID AND k.CHILD_ID IS NULL
    INNER JOIN DEPARTMENT l ON k.FACULTY_ID = l.ID AND l.parent_id IS NULL AND l.deleted = FALSE
    INNER JOIN DEPARTMENT m ON k.CHAIR_ID = m.ID AND m.parent_id IS NOT NULL AND m.deleted = FALSE
    INNER JOIN SPECIALITY n ON k.SPECIALITY_ID = n.ID AND n.deleted = FALSE
    INNER JOIN STUDENT_EDUCATION_TYPE o ON k.EDUCATION_TYPE_ID = o.ID
    INNER JOIN STUDENT_STATUS p ON k.STUDENT_STATUS_ID = p.ID
    INNER JOIN student_diploma_type diplom ON stu.diploma_type_id = diplom.id
    LEFT JOIN v_advisor advisor ON advisor.id = stu.advisor_id
    LEFT JOIN v_coordinator coordinator ON coordinator.id = stu.coordinator_id
    LEFT JOIN CARD card ON card.id = usr.card_id
    LEFT JOIN groups gr ON gr.id = k.groups_id
  WHERE usr.deleted = FALSE AND usr.locked = FALSE;

CREATE OR REPLACE VIEW V_GROUP AS
  SELECT
    gr.id,
    gr.speciality_id,
    gr.name  group_name,
    gr.language_id,
    gr.study_year_id,
    COUNT(1) student_count
  FROM groups gr
    INNER JOIN v_student stu ON stu.groups_id = gr.id
  WHERE stu.category_id = 3 and gr.deleted=FALSE
  GROUP BY gr.id;

CREATE OR REPLACE VIEW v_stream AS
  SELECT
    str.id,
    str.name                stream_name,
    str.semester_data_id,
    str.semester_id,
    sum(gr.student_count)   student_count,
    count(gr.student_count) group_count
  FROM stream str
    INNER JOIN stream_group str_gr ON str_gr.stream_id = str.id
    INNER JOIN v_group gr ON gr.id = str_gr.group_id
  WHERE str.name IS NOT NULL
  GROUP BY str.id;

CREATE OR REPLACE VIEW V_LOAD_TO_CHAIR AS
  SELECT DISTINCT
    floor(random() * (1000) + 1) :: BIGINT id,
    subj.id                                subject_id,
    curr.id                                curriculum_id,
    sem.study_year_id,
    str.id                                 stream_id,
    0                                      group_id,
    sem.id                                 semester_id,
    str.student_count                      student_number,
    curr_subj.credit,
    subj.lc_count                          lc_count,
    subj.pr_count * str.group_count        pr_count,
    subj.lb_count * str.group_count        lb_count,
    credit * 5                             with_teacher_count,
    str.student_count / 4                  rating_count,
    str.group_count * 2                    exam_count,
    CASE WHEN curr.student_diploma_type_id = 1
      THEN 0
    ELSE str.student_count / 5 END         control_count,
    CASE WHEN subj.course_work = TRUE
      THEN str.student_count / 4
    ELSE 0 END                             course_work_count,
    CASE WHEN curr_subj.semester_id IN (7, 8)
      THEN 12 * str.student_count
    ELSE 0 END                             diploma_count,
    CASE WHEN subj.week_number IS NOT NULL
      THEN subj.week_number * str.student_count
    ELSE 0 END                             practice_count,
    CASE WHEN curr_subj.semester_id = 8
      THEN str.student_count / 2
    ELSE 0 END                             mek,
    CASE WHEN curr_subj.semester_id = 8
      THEN str.student_count * 0.6
    ELSE 0 END                             protect_diploma_count,
    lc_count + pr_count + lb_count + credit * 5
    + str.student_count / 4 + str.group_count * 2
    + CASE WHEN curr.student_diploma_type_id = 1
      THEN 0
      ELSE str.student_count / 5 END
    + CASE WHEN subj.course_work = TRUE
      THEN str.student_count / 4
      ELSE 0 END
    + CASE WHEN curr_subj.semester_id IN (7, 8)
      THEN 12 * str.student_count
      ELSE 0 END
    + CASE WHEN subj.week_number IS NOT NULL
      THEN subj.week_number * str.student_count
      ELSE 0 END
    + CASE WHEN curr_subj.semester_id = 8
      THEN str.student_count / 2
      ELSE 0 END
    + CASE WHEN curr_subj.semester_id = 8
      THEN str.student_count * 0.6
      ELSE 0 END                           total_count
  FROM v_curriculum_subject curr_subj
    INNER JOIN subject subj ON subj.id = curr_subj.subject_id
    INNER JOIN curriculum curr ON curr_subj.curriculum_id = curr.id
    INNER JOIN semester sem ON sem.id = curr_subj.semester_id
    INNER JOIN (SELECT
                  DISTINCT
                  str.id,
                  str.group_count,
                  str.student_count,
                  gr.speciality_id,
                  gr.study_year_id
                FROM v_stream str
                  INNER JOIN stream_group str_gr ON str.id = str_gr.stream_id
                  INNER JOIN v_group gr ON str_gr.group_id = gr.id
               ) str ON str.speciality_id = curr.speciality_id
  WHERE curr_subj.deleted = FALSE AND subj.deleted = FALSE
        AND curr.deleted = FALSE AND sem.study_year_id = str.study_year_id
  UNION ALL
  SELECT DISTINCT
    floor(random() * (1000) + 1) :: BIGINT id,
    subj.id                                subject_id,
    curr.id                                curriculum_id,
    sem.study_year_id,
    str.id                                 stream_id,
    0                                      group_id,
    sem.id                                 semester_id,
    str.student_count                      student_number,
    curr_add_pr.credit,
    subj.lc_count                          lc_count,
    subj.pr_count * str.group_count        pr_count,
    subj.lb_count * str.group_count        lb_count,
    credit * 5                             with_teacher_count,
    str.student_count / 4                  rating_count,
    str.group_count * 2                    exam_count,
    CASE WHEN curr.student_diploma_type_id = 1
      THEN 0
    ELSE str.student_count / 5 END         control_count,
    CASE WHEN subj.course_work = TRUE
      THEN str.student_count / 4
    ELSE 0 END                             course_work_count,
    CASE WHEN curr_add_pr.semester_id IN (7, 8)
      THEN 12 * str.student_count
    ELSE 0 END                             diploma_count,
    CASE WHEN subj.week_number IS NOT NULL
      THEN subj.week_number * str.student_count
    ELSE 0 END                             practice_count,
    CASE WHEN curr_add_pr.semester_id = 8
      THEN str.student_count / 2
    ELSE 0 END                             mek,
    CASE WHEN curr_add_pr.semester_id = 8
      THEN str.student_count * 0.6
    ELSE 0 END                             protect_diploma_count,
    lc_count + pr_count + lb_count + credit * 5
    + str.student_count / 4 + str.group_count * 2
    + CASE WHEN curr.student_diploma_type_id = 1
      THEN 0
      ELSE str.student_count / 5 END
    + CASE WHEN subj.course_work = TRUE
      THEN str.student_count / 4
      ELSE 0 END
    + CASE WHEN curr_add_pr.semester_id IN (7, 8)
      THEN 12 * str.student_count
      ELSE 0 END
    + CASE WHEN subj.week_number IS NOT NULL
      THEN subj.week_number * str.student_count
      ELSE 0 END
    + CASE WHEN curr_add_pr.semester_id = 8
      THEN str.student_count / 2
      ELSE 0 END
    + CASE WHEN curr_add_pr.semester_id = 8
      THEN str.student_count * 0.6
      ELSE 0 END                           total_count
  FROM v_curriculum_add_program curr_add_pr
    INNER JOIN subject subj ON subj.id = curr_add_pr.subject_id
    INNER JOIN curriculum curr ON curr_add_pr.curriculum_id = curr.id
    INNER JOIN semester sem ON sem.id = curr_add_pr.semester_id
    INNER JOIN study_year year ON year.id = sem.study_year_id
    INNER JOIN (SELECT
                  DISTINCT
                  str.id,
                  str.group_count,
                  str.student_count,
                  gr.speciality_id,
                  gr.study_year_id
                FROM v_stream str
                  INNER JOIN stream_group str_gr ON str.id = str_gr.stream_id
                  INNER JOIN v_group gr ON str_gr.group_id = gr.id
               ) str ON str.speciality_id = curr.speciality_id
  WHERE curr_add_pr.deleted = FALSE AND subj.deleted = FALSE
        AND curr.deleted = FALSE AND sem.study_year_id = str.study_year_id
  UNION ALL
  SELECT DISTINCT
    floor(random() * (1000) + 1) :: BIGINT id,
    subj.id                                subject_id,
    curr.id                                curriculum_id,
    sem.study_year_id,
    0                                      stream_id,
    gr.id                                  group_id,
    sem.id                                 semester_id,
    gr.student_count                       student_number,
    curr_after_sem.credit,
    subj.lc_count                          lc_count,
    subj.pr_count * 1                      pr_count,
    subj.lb_count * 1                      lb_count,
    credit * 5                             with_teacher_count,
    gr.student_count / 4                   rating_count,
    1 * 2                                  exam_count,
    CASE WHEN curr.student_diploma_type_id = 1
      THEN 0
    ELSE gr.student_count / 5 END          control_count,
    CASE WHEN subj.course_work = TRUE
      THEN gr.student_count / 4
    ELSE 0 END                             course_work_count,
    CASE WHEN curr_after_sem.semester_id IN (7, 8)
      THEN 12 * gr.student_count
    ELSE 0 END                             diploma_count,
    CASE WHEN subj.week_number IS NOT NULL
      THEN subj.week_number * gr.student_count
    ELSE 0 END                             practice_count,
    CASE WHEN curr_after_sem.semester_id = 8
      THEN gr.student_count / 2
    ELSE 0 END                             mek,
    CASE WHEN curr_after_sem.semester_id = 8
      THEN gr.student_count * 0.6
    ELSE 0 END                             protect_diploma_count,
    lc_count + pr_count + lb_count + credit * 5
    + gr.student_count / 4 + 1 * 2
    + CASE WHEN curr.student_diploma_type_id = 1
      THEN 0
      ELSE gr.student_count / 5 END
    + CASE WHEN subj.course_work = TRUE
      THEN gr.student_count / 4
      ELSE 0 END
    + CASE WHEN curr_after_sem.semester_id IN (7, 8)
      THEN 12 * gr.student_count
      ELSE 0 END
    + CASE WHEN subj.week_number IS NOT NULL
      THEN subj.week_number * gr.student_count
      ELSE 0 END
    + CASE WHEN curr_after_sem.semester_id = 8
      THEN gr.student_count / 2
      ELSE 0 END
    + CASE WHEN curr_after_sem.semester_id = 8
      THEN gr.student_count * 0.6
      ELSE 0 END                           total_count
  FROM v_curriculum_after_semester curr_after_sem
    INNER JOIN subject subj ON subj.id = curr_after_sem.subject_id
    INNER JOIN curriculum curr ON curr_after_sem.curriculum_id = curr.id
    INNER JOIN semester sem ON sem.id = curr_after_sem.semester_id
    INNER JOIN study_year year ON year.id = sem.study_year_id
    INNER JOIN v_group gr ON gr.speciality_id = curr.speciality_id
  WHERE curr_after_sem.deleted = FALSE AND subj.deleted = FALSE
        AND curr.deleted = FALSE AND sem.study_year_id = gr.study_year_id;

CREATE OR REPLACE VIEW V_TEACHER_LOAD_ASSIGN_DETAIL AS
  SELECT
    load_assign_det.ID,
    subj.id                     subject_id,
    subj.NAME_RU                SUBJECT_NAME,
    load_assign_det.stream_id,
    str.name                    stream_name,
    load_assign_det.semester_id,
    sem.semester_name,
    load_assign_det.SEMESTER_PERIOD_ID,
    sem_period.PERIOD_NAME      SEMESTER_PERIOD_NAME,
    sum(gr.student_count)       STUDENT_COUNT,
    cred.CREDIT,
    academ_form.FORMULA,
    load_assign_det.LC_HOUR,
    load_assign_det.LB_HOUR,
    load_assign_det.PR_HOUR,
    CASE WHEN pr_hour != 0
      THEN CASE WHEN load_assign_det.student_diploma_type_id = 1
        THEN credit * 5
           WHEN load_assign_det.student_diploma_type_id IN (6, 7)
             THEN (lc_hour + lb_hour + pr_hour) / 2
           ELSE 0 END
    ELSE 0 END                  with_teacher_hour,
    sum(gr.student_count) / 4   rating_hour,
    COUNT(gr.student_count) * 2 exam_hour,
    CASE WHEN load_assign_det.student_diploma_type_id IN (6, 7)
      THEN sum(gr.student_count) / 5
    ELSE 0 END                  control_hour,
    CASE WHEN subj.course_work = TRUE
      THEN sum(gr.student_count) / 4
    ELSE 0 END                  course_work_hour,
    CASE WHEN load_assign_det.semester_id IN (7, 8)
      THEN 12 * sum(gr.student_count)
    ELSE 0 END                  diploma_hour,
    CASE WHEN subj.week_number IS NOT NULL
      THEN subj.week_number * sum(gr.student_count)
    ELSE 0 END                  practice_hour,
    CASE WHEN load_assign_det.semester_id = 8
      THEN sum(gr.student_count) / 2
    ELSE 0 END                  mek,
    CASE WHEN load_assign_det.semester_id = 8
      THEN sum(gr.student_count) * 0.6
    ELSE 0 END                  protect_diploma_hour,
    LC_HOUR + PR_HOUR + LB_HOUR
    + CASE WHEN pr_hour != 0
      THEN CASE WHEN load_assign_det.student_diploma_type_id = 1
        THEN credit * 5
           WHEN load_assign_det.student_diploma_type_id IN (6, 7)
             THEN (lc_hour + lb_hour + pr_hour) / 2
           ELSE 0 END
      ELSE 0 END
    + sum(gr.student_count) / 4 + COUNT(gr.student_count) * 2
    + CASE WHEN load_assign_det.student_diploma_type_id = 1
      THEN 0
      ELSE sum(gr.student_count) / 5 END
    + CASE WHEN subj.course_work = TRUE
      THEN sum(gr.student_count) / 4
      ELSE 0 END
    + CASE WHEN load_assign_det.semester_id IN (7, 8)
      THEN 12 * sum(gr.student_count)
      ELSE 0 END
    + CASE WHEN subj.week_number IS NOT NULL
      THEN subj.week_number * sum(gr.student_count)
      ELSE 0 END
    + CASE WHEN load_assign_det.semester_id = 8
      THEN sum(gr.student_count) / 2
      ELSE 0 END
    + CASE WHEN load_assign_det.semester_id = 8
      THEN sum(gr.student_count) * 0.6
      ELSE 0 END                total_hour,
    load_assign_det.teacher_load_assign_id,
    load_assign_det.teacher_id,
    load_assign_det.student_diploma_type_id,
    load_assign_det.group_id
  FROM TEACHER_LOAD_ASSIGN_DETAIL load_assign_det
    INNER JOIN SUBJECT subj ON load_assign_det.SUBJECT_ID = subj.ID
    INNER JOIN CREDITABILITY cred ON subj.CREDITABILITY_ID = cred.ID
    INNER JOIN SEMESTER_PERIOD sem_period ON load_assign_det.SEMESTER_PERIOD_ID = sem_period.ID
    INNER JOIN ACADEMIC_FORMULA academ_form ON subj.ACADEMIC_FORMULA_ID = academ_form.ID
    INNER JOIN stream str ON str.id = load_assign_det.stream_id
    INNER JOIN semester sem ON sem.id = load_assign_det.semester_id
    INNER JOIN stream_group str_gr ON str_gr.stream_id = str.id
    LEFT JOIN v_group gr ON gr.id = str_gr.group_id
  GROUP BY load_assign_det.ID, subj.id, subj.NAME_RU, load_assign_det.stream_id, str.name,
    load_assign_det.semester_id, sem.semester_name, cred.CREDIT, academ_form.FORMULA,
    load_assign_det.SEMESTER_PERIOD_ID, sem_period.PERIOD_NAME, load_assign_det.LC_HOUR,
    load_assign_det.LB_HOUR, load_assign_det.PR_HOUR;

CREATE OR REPLACE VIEW v_student_debts AS
  SELECT
    s2.id,
    x.user_code,
    trim(x.LAST_NAME || ' ' || x.FIRST_NAME || ' ' || coalesce(x.MIDDLE_NAME, '')) fio,
    CASE WHEN x.id = payment.student_id
      THEN (a.price - (sum(payment.payment_sum)))
    ELSE a.price END                                                               DEBT_SUM
  FROM v_student x
    INNER JOIN student s2 ON x.id = s2.id
    INNER JOIN student_diploma_type s3 ON s2.diploma_type_id = s3.id
    INNER JOIN accountant_price a ON s3.id = a.student_diploma_type_id
    LEFT JOIN student_payment AS payment ON payment.student_id = s2.id
  WHERE a.contract_payment_type_id = 2 AND
        a.level_id = x.level_id AND x.deleted = FALSE AND a.deleted = FALSE
  GROUP BY payment.student_id, s2.id, x.user_code, x.last_name, x.first_name, x.middle_name, x.id, a.price;