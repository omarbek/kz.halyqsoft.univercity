package kz.halyqsoft.univercity.entity.beans.univercity.view;

import kz.halyqsoft.univercity.entity.beans.univercity.CURRICULUM;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.CREDITABILITY;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.EDUCATION_MODULE_TYPE;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.SEMESTER;
import org.r3a.common.entity.AbstractEntity;
import org.r3a.common.entity.EFieldType;
import org.r3a.common.entity.FieldInfo;

import javax.persistence.*;

/**
 * @author Omarbek
 * @created Feb 25, 2016 5:22:43 PM
 */
@Entity
public class V_CURRICULUM_AFTER_SEMESTER extends AbstractEntity {

    private static final long serialVersionUID = 1630740319812777639L;

    @FieldInfo(type = EFieldType.FK_COMBO, order = 1, inGrid = false, inEdit = false, inView = false)
    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "CURRICULUM_ID", referencedColumnName = "ID")})
    private CURRICULUM curriculum;

    @FieldInfo(type = EFieldType.FK_DIALOG, order = 2, inGrid = false)
    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "SUBJECT_ID", referencedColumnName = "ID")})
    private V_SUBJECT_SELECT subject;

    @FieldInfo(type = EFieldType.TEXT, max = 13, order = 5, required = false, columnWidth = 120)
    @Column(name = "SUBJECT_CODE")
    private String subjectCode;

    @FieldInfo(type = EFieldType.TEXT, max = 64, order = 4, inEdit = false, inView = false)
    @Column(name = "SUBJECT_NAME_RU")
    private String subjectNameRU;

    @FieldInfo(type = EFieldType.FK_COMBO, order = 3, inGrid = false, readOnlyFixed = true, required = false)
    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "CREDITABILITY_ID", referencedColumnName = "ID")})
    private CREDITABILITY creditability;

    @FieldInfo(type = EFieldType.INTEGER, max = 6, order = 6, inEdit = false, inView = false, columnWidth = 80)
    @Column(name = "CREDIT")
    private Integer credit;

    @FieldInfo(type = EFieldType.FK_COMBO, order = 7, inGrid = false)
    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "SEMESTER_ID", referencedColumnName = "ID")})
    private SEMESTER semester;

    @FieldInfo(type = EFieldType.TEXT, max = 64, order = 8, inEdit = false, inView = false, columnWidth = 120)
    @Column(name = "SEMESTER_NAME")
    private String semesterName;

    @FieldInfo(type = EFieldType.FK_COMBO, order = 9)
    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "EDUCATION_MODULE_TYPE_ID", referencedColumnName = "ID")})
    private EDUCATION_MODULE_TYPE educationModuleType;

    @FieldInfo(type = EFieldType.TEXT, order = 10,inEdit = false,inGrid = false)
    @Column(name = "EDUCATION_MODULE_TYPE_NAME")
    private String educationModuleTypeName;

    @FieldInfo(type = EFieldType.BOOLEAN, order = 11, required = false, inEdit = false, inGrid = false, inView = false)
    @Column(name = "DELETED")
    private boolean deleted;

    public CURRICULUM getCurriculum() {
        return curriculum;
    }

    public void setCurriculum(CURRICULUM curriculum) {
        this.curriculum = curriculum;
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

    public String getSubjectNameRU() {
        return subjectNameRU;
    }

    public void setSubjectNameRU(String subjectNameRU) {
        this.subjectNameRU = subjectNameRU;
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

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public EDUCATION_MODULE_TYPE getEducationModuleType() {
        return educationModuleType;
    }

    public void setEducationModuleType(EDUCATION_MODULE_TYPE educationModuleType) {
        this.educationModuleType = educationModuleType;
    }

    public String getEducationModuleTypeName() {
        return educationModuleTypeName;
    }

    public void setEducationModuleTypeName(String educationModuleTypeName) {
        this.educationModuleTypeName = educationModuleTypeName;
    }
}
