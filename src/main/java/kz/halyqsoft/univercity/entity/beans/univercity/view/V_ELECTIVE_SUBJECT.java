package kz.halyqsoft.univercity.entity.beans.univercity.view;

import kz.halyqsoft.univercity.entity.beans.univercity.CURRICULUM;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.*;
import org.r3a.common.entity.AbstractEntity;
import org.r3a.common.entity.EFieldType;
import org.r3a.common.entity.FieldInfo;

import javax.persistence.*;

/**
 * @author Omarbek
 * @created 13 ���� 2016 �. 15:11:29
 */
@Entity
public class V_ELECTIVE_SUBJECT extends AbstractEntity {

    private static final long serialVersionUID = -1722551975804650975L;

    @FieldInfo(type = EFieldType.FK_COMBO, order = 1, inGrid = false, inEdit = false, inView = false)
    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "CURRICULUM_ID", referencedColumnName = "ID")})
    private CURRICULUM curriculum;

    @FieldInfo(type = EFieldType.FK_COMBO, order = 2, inGrid = false, inEdit = true, inView = false)
    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "SEMESTER_ID", referencedColumnName = "ID")})
    private SEMESTER semester;

    @FieldInfo(type = EFieldType.TEXT, order = 3, inGrid = false, inEdit = false, inView = true)
    @Column(name = "SEMESTER_NAME")
    private String semesterName;

    @FieldInfo(type = EFieldType.FK_COMBO, order = 4, inGrid = false, inEdit = true, inView = false)
    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "SUBJECT_ID", referencedColumnName = "ID")})
    private V_SUBJECT_SELECT subject;

    @FieldInfo(type = EFieldType.TEXT, order = 5, inEdit = false, inView = true, columnWidth = 120)
    @Column(name = "SUBJECT_CODE")
    private String subjectCode;

    @FieldInfo(type = EFieldType.TEXT, order = 6, inEdit = false, inView = true)
    @Column(name = "SUBJECT_NAME")
    private String subjectName;

    @FieldInfo(type = EFieldType.FK_COMBO, order = 7, inGrid = false, inEdit = false, inView = false)
    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "SUBJECT_CYCLE_ID", referencedColumnName = "ID")})
    private SUBJECT_CYCLE subjectCycle;

    @FieldInfo(type = EFieldType.TEXT, order = 8, inEdit = false, inView = true, columnWidth = 90)
    @Column(name = "CYCLE_SHORT_NAME")
    private String cycleShortName;

    @FieldInfo(type = EFieldType.FK_COMBO, order = 9, inGrid = false, inEdit = false, inView = false)
    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "CREDITABILITY_ID", referencedColumnName = "ID")})
    private CREDITABILITY creditability;

    @FieldInfo(type = EFieldType.INTEGER, order = 10, inEdit = false, inView = true, columnWidth = 80)
    @Column(name = "CREDIT")
    private Integer credit;

    @FieldInfo(type = EFieldType.FK_COMBO, order = 11, inGrid = false, readOnlyFixed = true, required = false)
    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "ACADEMIC_FORMULA_ID", referencedColumnName = "ID")})
    private ACADEMIC_FORMULA academicFormula;

    @FieldInfo(type = EFieldType.TEXT, min = 5, max = 5, order = 12, inEdit = false, inView = false, columnWidth = 100)
    @Column(name = "FORMULA")
    private String formula;

    @FieldInfo(type = EFieldType.FK_COMBO, order = 13, inGrid = false, inEdit = false, inView = false)
    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "CONTROL_TYPE_ID", referencedColumnName = "ID")})
    private CONTROL_TYPE controlType;

    @FieldInfo(type = EFieldType.TEXT, order = 14, inGrid = false, inEdit = false, inView = true)
    @Column(name = "CONTROL_TYPE_NAME")
    private String controlTypeName;

    @FieldInfo(type = EFieldType.BOOLEAN_EDIT, order = 15, inEdit = false, inView = false, columnWidth = 170)
    @Column(name = "CONSIDER_CREDIT", nullable = false)
    private boolean considerCredit;

    @FieldInfo(type = EFieldType.BOOLEAN, order = 16, required = false, inEdit = false, inGrid = false, inView = false)
    @Column(name = "DELETED")
    private boolean deleted;

    public V_ELECTIVE_SUBJECT() {
    }

    public CURRICULUM getCurriculum() {
        return curriculum;
    }

    public void setCurriculum(CURRICULUM curriculum) {
        this.curriculum = curriculum;
    }

    public SEMESTER getSemester() {
        return semester;
    }

    public void setSemester(SEMESTER semester) {
        this.semester = semester;
    }

    public String getSemesterName() {
        return semesterName;
    }

    public void setSemesterName(String semesterName) {
        this.semesterName = semesterName;
    }

    public V_SUBJECT_SELECT getSubject() {
        return subject;
    }

    public void setSubject(V_SUBJECT_SELECT subject) {
        this.subject = subject;
    }

    public String getSubjectCode() {
        return subjectCode;
    }

    public void setSubjectCode(String subjectCode) {
        this.subjectCode = subjectCode;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }

    public SUBJECT_CYCLE getSubjectCycle() {
        return subjectCycle;
    }

    public void setSubjectCycle(SUBJECT_CYCLE subjectCycle) {
        this.subjectCycle = subjectCycle;
    }

    public String getCycleShortName() {
        return cycleShortName;
    }

    public void setCycleShortName(String cycleShortName) {
        this.cycleShortName = cycleShortName;
    }

    public CREDITABILITY getCreditability() {
        return creditability;
    }

    public void setCreditability(CREDITABILITY creditability) {
        this.creditability = creditability;
    }

    public Integer getCredit() {
        return credit;
    }

    public void setCredit(Integer credit) {
        this.credit = credit;
    }

    public ACADEMIC_FORMULA getAcademicFormula() {
        return academicFormula;
    }

    public void setAcademicFormula(ACADEMIC_FORMULA academicFormula) {
        this.academicFormula = academicFormula;
    }

    public String getFormula() {
        return formula;
    }

    public void setFormula(String formula) {
        this.formula = formula;
    }

    public CONTROL_TYPE getControlType() {
        return controlType;
    }

    public void setControlType(CONTROL_TYPE controlType) {
        this.controlType = controlType;
    }

    public String getControlTypeName() {
        return controlTypeName;
    }

    public void setControlTypeName(String controlTypeName) {
        this.controlTypeName = controlTypeName;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public boolean isConsiderCredit() {
        return considerCredit;
    }

    public void setConsiderCredit(boolean considerCredit) {
        this.considerCredit = considerCredit;
    }
}
