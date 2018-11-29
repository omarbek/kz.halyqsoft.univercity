CREATE OR REPLACE VIEW v_elective_subject AS
  SELECT elect_subj.ID,
         elect_subj.CURRICULUM_ID,
         elect_subj.SEMESTER_ID,
         elect_subj.semester_data_id,
         elect_subj.SUBJECT_ID,
         subj.CREDITABILITY_ID,
         subj.ects_id,
         subj.CONTROL_TYPE_ID,
         elect_subj.education_module_type_id,
         subj.module_id,
         subj.subject_cycle_id,
      --end of ids
         sem.SEMESTER_NAME,
         subj_mod.module_short_name,
         subj_cycle.cycle_short_name,
         CASE
           WHEN elect_subj.education_module_type_id IS NULL
                   THEN '-'
           ELSE edu_mod_type.type_name END               education_module_type_name,
         elect_subj.CODE                                 SUBJECT_CODE,
         subj.NAME_kz                                    SUBJECT_NAME_KZ,
         subj.name_ru                                    subject_name_ru,
         case
           when consider_credit then cred.CREDIT
           else cast(0 as numeric(2, 0)) end             credit,
         case
           when consider_credit then ects.ects
           else cast(0 as numeric(2, 0)) end             ects_count,
         case
           when consider_credit then subj.lc_count
           else cast(0 as numeric(3, 0)) end             lc_count,
         case
           when consider_credit then subj.pr_count
           else cast(0 as numeric(3, 0)) end             pr_count,
         case
           when consider_credit then subj.lb_count
           else cast(0 as numeric(3, 0)) end             lb_count,
         case
           when consider_credit then subj.with_teacher_count
           else cast(0 as numeric(3, 0)) end             with_teacher_count,
         case
           when consider_credit then subj.own_count
           else cast(0 as numeric(3, 0)) end             own_count,
         case
           when consider_credit then subj.total_count
           else cast(0 as numeric(3, 0)) end             total_count,
         case
           when consider_credit then contr_type.TYPE_NAME
           else cast(null as character varying(128)) end CONTROL_TYPE_NAME,
         elect_subj.consider_credit
  FROM elective_subject elect_subj
         INNER JOIN SEMESTER sem ON elect_subj.SEMESTER_ID = sem.ID
         INNER JOIN SUBJECT subj ON elect_subj.SUBJECT_ID = subj.ID
         INNER JOIN subject_module subj_mod ON subj.module_id = subj_mod.id
         INNER JOIN SUBJECT_CYCLE subj_cycle ON subj.SUBJECT_CYCLE_ID = subj_cycle.ID
         INNER JOIN CREDITABILITY cred ON subj.CREDITABILITY_ID = cred.ID
         INNER JOIN ACADEMIC_FORMULA form ON subj.ACADEMIC_FORMULA_ID = form.ID
         INNER JOIN CONTROL_TYPE contr_type ON subj.CONTROL_TYPE_ID = contr_type.ID
         INNER JOIN ects ON subj.ects_id = ects.id
         LEFT JOIN education_module_type edu_mod_type ON edu_mod_type.id = elect_subj.education_module_type_id
  WHERE elect_subj.deleted = FALSE
    AND subj.deleted = FALSE;