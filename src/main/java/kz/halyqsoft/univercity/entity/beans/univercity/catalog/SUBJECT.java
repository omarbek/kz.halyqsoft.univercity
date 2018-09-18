package kz.halyqsoft.univercity.entity.beans.univercity.catalog;

import kz.halyqsoft.univercity.entity.beans.univercity.PRACTICE_TYPE;
import org.r3a.common.entity.AbstractEntity;
import org.r3a.common.entity.EFieldType;
import org.r3a.common.entity.FieldInfo;

import javax.persistence.*;

/**
 * @author Omarbek
 * Created Dec 24, 2015 2:05:09 PM
 */
@Entity
public class SUBJECT extends AbstractEntity {

    private static final long serialVersionUID = -648721786979090913L;

    @FieldInfo(type = EFieldType.TEXT, max = 256, order = 1)
    @Column(name = "NAME_KZ", nullable = false)
    private String nameKZ;

    @FieldInfo(type = EFieldType.TEXT_LATIN, max = 256, order = 2)
    @Column(name = "NAME_EN", nullable = false)
    private String nameEN;

    @FieldInfo(type = EFieldType.TEXT, max = 256, order = 3)
    @Column(name = "NAME_RU", nullable = false)
    private String nameRU;

    @FieldInfo(type = EFieldType.FK_COMBO, order = 4, inGrid = false, required = false)
    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "PRACTICE_TYPE_ID", referencedColumnName = "ID")})
    private PRACTICE_TYPE practiceType;

    @FieldInfo(type = EFieldType.FK_DIALOG, order = 5, inGrid = false)
    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "STUDY_DIRECT_ID", referencedColumnName = "ID")})
    private STUDY_DIRECT studyDirect;

    @FieldInfo(type = EFieldType.FK_COMBO, order = 8)
    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "CHAIR_ID", referencedColumnName = "ID")})
    private DEPARTMENT chair;

    @FieldInfo(type = EFieldType.FK_COMBO, order = 9, inGrid = false)
    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "LEVEL_ID", referencedColumnName = "ID")})
    private LEVEL level;

    @FieldInfo(type = EFieldType.FK_COMBO, order = 10, inGrid = false)
    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "MODULE_ID", referencedColumnName = "ID")})
    private SUBJECT_MODULE subjectModule;

    @FieldInfo(type = EFieldType.FK_COMBO, order = 11, inGrid = false, required = false)
    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "SUBJECT_CYCLE_ID", referencedColumnName = "ID", nullable = true)})
    private SUBJECT_CYCLE subjectCycle;

    @FieldInfo(type = EFieldType.BOOLEAN, order = 12, required = false, columnWidth = 100)
    @Column(name = "MANDATORY", nullable = false)
    private boolean mandatory;

    @FieldInfo(type = EFieldType.FK_COMBO, order = 13, inGrid = false)
    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "CREDITABILITY_ID", referencedColumnName = "ID")})
    private CREDITABILITY creditability;

    @FieldInfo(type = EFieldType.FK_COMBO, order = 14, inGrid = false)
    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "ACADEMIC_FORMULA_ID", referencedColumnName = "ID")})
    private ACADEMIC_FORMULA academicFormula;

    @FieldInfo(type = EFieldType.FK_COMBO, order = 15)
    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "ECTS_ID", referencedColumnName = "ID")})
    private ECTS ects;

    @FieldInfo(type = EFieldType.BOOLEAN, order = 16, required = false, columnWidth = 100)
    @Column(name = "LANG_KZ", nullable = false)
    private boolean langKZ;

    @FieldInfo(type = EFieldType.BOOLEAN, order = 17, required = false, columnWidth = 100)
    @Column(name = "LANG_EN", nullable = false)
    private boolean langEN;

    @FieldInfo(type = EFieldType.BOOLEAN, order = 18, required = false, columnWidth = 100)
    @Column(name = "LANG_RU", nullable = false)
    private boolean langRU;

    @FieldInfo(type = EFieldType.INTEGER, order = 19)
    @Column(name = "LC_COUNT")
    private Integer lcCount;

    @FieldInfo(type = EFieldType.INTEGER, order = 20)
    @Column(name = "PR_COUNT")
    private Integer prCount;

    @FieldInfo(type = EFieldType.INTEGER, order = 21)
    @Column(name = "LB_COUNT")
    private Integer lbCount;

    @FieldInfo(type = EFieldType.INTEGER, order = 22, readOnlyFixed = true)
    @Column(name = "WITH_TEACHER_COUNT")
    private Integer withTeacherCount;

    @FieldInfo(type = EFieldType.INTEGER, order = 23, readOnlyFixed = true)
    @Column(name = "OWN_COUNT")
    private Integer ownCount;

    @FieldInfo(type = EFieldType.INTEGER, order = 24, readOnlyFixed = true)
    @Column(name = "TOTAL_COUNT")
    private Integer totalCount;

    @FieldInfo(type = EFieldType.BOOLEAN, order = 25, required = false, columnWidth = 100)
    @Column(name = "COURSE_WORK", nullable = false)
    private boolean courseWork;

    @FieldInfo(type = EFieldType.FK_COMBO, order = 26, inGrid = false)
    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "CONTROL_TYPE_ID", referencedColumnName = "ID")})
    private CONTROL_TYPE controlType;

    @FieldInfo(type = EFieldType.INTEGER, order = 27, required = false)
    @Column(name = "WEEK_NUMBER")
    private Integer weekNumber;

    @FieldInfo(type = EFieldType.BOOLEAN, order = 28, required = false, inEdit = false, inGrid = false, inView = false)
    @Column(name = "DELETED", nullable = false)
    private boolean deleted;

    public String getNameKZ() {
        return nameKZ;
    }

    public void setNameKZ(String nameKZ) {
        this.nameKZ = nameKZ;
    }

    public String getNameEN() {
        return nameEN;
    }

    public void setNameEN(String nameEN) {
        this.nameEN = nameEN;
    }

    public String getNameRU() {
        return nameRU;
    }

    public void setNameRU(String nameRU) {
        this.nameRU = nameRU;
    }

    public STUDY_DIRECT getStudyDirect() {
        return studyDirect;
    }

    public void setStudyDirect(STUDY_DIRECT studyDirect) {
        this.studyDirect = studyDirect;
    }

    public DEPARTMENT getChair() {
        return chair;
    }

    public void setChair(DEPARTMENT chair) {
        this.chair = chair;
    }

    public LEVEL getLevel() {
        return level;
    }

    public void setLevel(LEVEL level) {
        this.level = level;
    }

    public SUBJECT_MODULE getSubjectModule() {
        return subjectModule;
    }

    public void setSubjectModule(SUBJECT_MODULE subjectModule) {
        this.subjectModule = subjectModule;
    }

    public SUBJECT_CYCLE getSubjectCycle() {
        return subjectCycle;
    }

    public void setSubjectCycle(SUBJECT_CYCLE subjectCycle) {
        this.subjectCycle = subjectCycle;
    }

    public boolean isMandatory() {
        return mandatory;
    }

    public void setMandatory(boolean mandatory) {
        this.mandatory = mandatory;
    }

    public CREDITABILITY getCreditability() {
        return creditability;
    }

    public void setCreditability(CREDITABILITY creditability) {
        this.creditability = creditability;
    }

    public ACADEMIC_FORMULA getAcademicFormula() {
        return academicFormula;
    }

    public void setAcademicFormula(ACADEMIC_FORMULA academicFormula) {
        this.academicFormula = academicFormula;
    }

    public ECTS getEcts() {
        return ects;
    }

    public void setEcts(ECTS ects) {
        this.ects = ects;
    }

    public boolean isLangKZ() {
        return langKZ;
    }

    public void setLangKZ(boolean langKZ) {
        this.langKZ = langKZ;
    }

    public boolean isLangEN() {
        return langEN;
    }

    public void setLangEN(boolean langEN) {
        this.langEN = langEN;
    }

    public boolean isLangRU() {
        return langRU;
    }

    public void setLangRU(boolean langRU) {
        this.langRU = langRU;
    }


    public CONTROL_TYPE getControlType() {
        return controlType;
    }

    public void setControlType(CONTROL_TYPE controlType) {
        this.controlType = controlType;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public Integer getLcCount() {
        return lcCount;
    }

    public void setLcCount(Integer lcCount) {
        this.lcCount = lcCount;
    }

    public Integer getPrCount() {
        return prCount;
    }

    public void setPrCount(Integer prCount) {
        this.prCount = prCount;
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

    public String toString() {
        return nameRU + " - " + chair;
    }

    public boolean isCourseWork() {
        return courseWork;
    }

    public void setCourseWork(boolean courseWork) {
        this.courseWork = courseWork;
    }

    public Integer getWeekNumber() {
        return weekNumber;
    }

    public void setWeekNumber(Integer weekNumber) {
        this.weekNumber = weekNumber;
    }

    public PRACTICE_TYPE getPracticeType() {
        return practiceType;
    }

    public void setPracticeType(PRACTICE_TYPE practiceType) {
        this.practiceType = practiceType;
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }
}
