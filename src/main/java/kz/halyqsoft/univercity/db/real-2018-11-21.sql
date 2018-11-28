alter table stream
    add column updated timestamp;

--assyl
CREATE EXTENSION tablefunc;


CREATE TABLE ABSENCE_CAUSE (
    id      BIGINT    NOT NULL,
    name VARCHAR(255) NOT NULL
);


ALTER TABLE ABSENCE_CAUSE
    ADD CONSTRAINT pk_absence_cause PRIMARY KEY (id);

CREATE SEQUENCE S_ABSENCE_CAUSE
    MINVALUE 0
    START WITH 1
    NO CYCLE;

CREATE TABLE EMPLOYEE_ABSENCE_CAUSE (
    id BIGINT NOT NULL ,
    employee_id BIGINT NOT NULL ,
    absence_cause_id BIGINT NOT NULL ,
    starting_date TIMESTAMP NOT NULL ,
    final_date TIMESTAMP NOT NULL,
    created TIMESTAMP NOT NULL
);


ALTER TABLE EMPLOYEE_ABSENCE_CAUSE
    ADD CONSTRAINT pk_employee_absence_cause PRIMARY KEY (id);

ALTER TABLE EMPLOYEE_ABSENCE_CAUSE
    ADD CONSTRAINT fk_employee_absence_cause_absence_cause FOREIGN KEY (absence_cause_id)
REFERENCES absence_cause (id) ON UPDATE RESTRICT ON DELETE RESTRICT ;

ALTER TABLE EMPLOYEE_ABSENCE_CAUSE
    ADD CONSTRAINT fk_employee_absence_cause_employee FOREIGN KEY (employee_id)
REFERENCES employee (id) ON UPDATE RESTRICT ON DELETE RESTRICT ;

CREATE SEQUENCE S_EMPLOYEE_ABSENCE_CAUSE
    MINVALUE 0
    START WITH 1
    NO CYCLE;
--end assyl

drop view V_SUBJECT_SELECT;
CREATE OR REPLACE VIEW V_SUBJECT_SELECT AS
  SELECT subj.ID,
         subj.name_kz,
         subj.NAME_RU,
         subj.module_id,
         subj.STUDY_DIRECT_ID,
         subj.CHAIR_ID,
         subj.LEVEL_ID,
         subj.SUBJECT_CYCLE_ID,
         subj.MANDATORY,
         subj.CREDITABILITY_ID,
         cred.CREDIT,
         contr_type.TYPE_NAME CONTROL_TYPE_NAME
  FROM SUBJECT subj
         INNER JOIN CREDITABILITY cred ON subj.CREDITABILITY_ID = cred.ID
         INNER JOIN CONTROL_TYPE contr_type ON subj.CONTROL_TYPE_ID = contr_type.ID
  WHERE subj.DELETED = FALSE
    AND subj.CHAIR_ID IS NOT NULL;