package kz.halyqsoft.univercity.entity.beans.univercity.view;

import kz.halyqsoft.univercity.entity.beans.univercity.CURRICULUM;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.*;
import org.r3a.common.entity.AbstractEntity;
import org.r3a.common.entity.EFieldType;
import org.r3a.common.entity.FieldInfo;

import javax.persistence.*;

/**
 * @author Omarbek
 * @created 06.11.2018
 */
@Entity
public class V_CURRICULUM_AFTER_SEMESTER extends AbstractEntity {

    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "CURRICULUM_ID", referencedColumnName = "ID")})
    private CURRICULUM curriculum;

    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "SUBJECT_ID", referencedColumnName = "ID")})
    private SUBJECT subject;

    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "CREDITABILITY_ID", referencedColumnName = "ID")})
    private CREDITABILITY creditability;

    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "ECTS_ID", referencedColumnName = "ID")})
    private ECTS ects;

    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "CONTROL_TYPE_ID", referencedColumnName = "ID")})
    private CONTROL_TYPE controlType;

    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "EDUCATION_MODULE_TYPE_ID", referencedColumnName = "ID")})
    private EDUCATION_MODULE_TYPE educationModuleType;

    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "MODULE_ID", referencedColumnName = "ID")})
    private SUBJECT_MODULE subjectModule;

    @FieldInfo(type = EFieldType.TEXT)
    @Column(name = "MODULE_SHORT_NAME")
    private String moduleShortName;

    @FieldInfo(type = EFieldType.TEXT, order = 2)
    @Column(name = "EDUCATION_MODULE_TYPE_NAME")
    private String educationModuleTypeName;

    @FieldInfo(type = EFieldType.TEXT, order = 3)
    @Column(name = "SUBJECT_CODE")
    private String subjectCode;

    @FieldInfo(type = EFieldType.TEXT, order = 4)
    @Column(name = "SUBJECT_NAME_KZ")
    private String subjectNameKz;

    @FieldInfo(type = EFieldType.TEXT, order = 5)
    @Column(name = "SUBJECT_NAME_RU")
    private String subjectNameRu;

    @FieldInfo(type = EFieldType.INTEGER, order = 6)
    @Column(name = "CREDIT")
    private Integer credit;

    @FieldInfo(type = EFieldType.INTEGER, order = 7)
    @Column(name = "ECTS_COUNT")
    private Integer ectsCount;

    @FieldInfo(type = EFieldType.INTEGER, order = 8)
    @Column(name = "LC_COUNT")
    private Integer lcCount;

    @FieldInfo(type = EFieldType.INTEGER, order = 9)
    @Column(name = "WEEK_NUMBER")
    private Integer weekNumber;

    @FieldInfo(type = EFieldType.INTEGER, order = 10)
    @Column(name = "LB_COUNT")
    private Integer lbCount;

    @FieldInfo(type = EFieldType.INTEGER, order = 11)
    @Column(name = "WITH_TEACHER_COUNT")
    private Integer withTeacherCount;

    @FieldInfo(type = EFieldType.INTEGER, order = 12)
    @Column(name = "OWN_COUNT")
    private Integer ownCount;

    @FieldInfo(type = EFieldType.INTEGER, order = 13)
    @Column(name = "TOTAL_COUNT")
    private Integer totalCount;

    @FieldInfo(type = EFieldType.TEXT, order = 14)
    @Column(name = "CONTROL_TYPE_NAME")
    private String controlTypeName;

    public CURRICULUM getCurriculum() {
        return curriculum;
    }

    public void setCurriculum(CURRICULUM curriculum) {
        this.curriculum = curriculum;
    }

    public SUBJECT getSubject() {
        return subject;
    }

    public void setSubject(SUBJECT subject) {
        this.subject = subject;
    }

    public CREDITABILITY getCreditability() {
        return creditability;
    }

    public void setCreditability(CREDITABILITY creditability) {
        this.creditability = creditability;
    }

    public ECTS getEcts() {
        return ects;
    }

    public void setEcts(ECTS ects) {
        this.ects = ects;
    }

    public CONTROL_TYPE getControlType() {
        return controlType;
    }

    public void setControlType(CONTROL_TYPE controlType) {
        this.controlType = controlType;
    }

    public EDUCATION_MODULE_TYPE getEducationModuleType() {
        return educationModuleType;
    }

    public void setEducationModuleType(EDUCATION_MODULE_TYPE educationModuleType) {
        this.educationModuleType = educationModuleType;
    }

    public SUBJECT_MODULE getSubjectModule() {
        return subjectModule;
    }

    public void setSubjectModule(SUBJECT_MODULE subjectModule) {
        this.subjectModule = subjectModule;
    }

    public String getModuleShortName() {
        return moduleShortName;
    }

    public void setModuleShortName(String moduleShortName) {
        this.moduleShortName = moduleShortName;
    }

    public String getEducationModuleTypeName() {
        return educationModuleTypeName;
    }

    public void setEducationModuleTypeName(String educationModuleTypeName) {
        this.educationModuleTypeName = educationModuleTypeName;
    }

    public String getSubjectCode() {
        return subjectCode;
    }

    public void setSubjectCode(String subjectCode) {
        this.subjectCode = subjectCode;
    }

    public String getSubjectNameKz() {
        return subjectNameKz;
    }

    public void setSubjectNameKz(String subjectNameKz) {
        this.subjectNameKz = subjectNameKz;
    }

    public String getSubjectNameRu() {
        return subjectNameRu;
    }

    public void setSubjectNameRu(String subjectNameRu) {
        this.subjectNameRu = subjectNameRu;
    }

    public Integer getCredit() {
        return credit;
    }

    public void setCredit(Integer credit) {
        this.credit = credit;
    }

    public Integer getEctsCount() {
        return ectsCount;
    }

    public void setEctsCount(Integer ectsCount) {
        this.ectsCount = ectsCount;
    }

    public Integer getLcCount() {
        return lcCount;
    }

    public void setLcCount(Integer lcCount) {
        this.lcCount = lcCount;
    }

    public Integer getLbCount() {
        return lbCount;
    }

    public void setLbCount(Integer lbCount) {
        this.lbCount = lbCount;
    }

    public Integer getWithTeacherCount() {
        return withTeacherCount;
    }

    public void setWithTeacherCount(Integer withTeacherCount) {
        this.withTeacherCount = withTeacherCount;
    }

    public Integer getOwnCount() {
        return ownCount;
    }

    public void setOwnCount(Integer ownCount) {
        this.ownCount = ownCount;
    }

    public Integer getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(Integer totalCount) {
        this.totalCount = totalCount;
    }

    public String getControlTypeName() {
        return controlTypeName;
    }

    public void setControlTypeName(String controlTypeName) {
        this.controlTypeName = controlTypeName;
    }

    public Integer getWeekNumber() {
        return weekNumber;
    }

    public void setWeekNumber(Integer weekNumber) {
        this.weekNumber = weekNumber;
    }
}
