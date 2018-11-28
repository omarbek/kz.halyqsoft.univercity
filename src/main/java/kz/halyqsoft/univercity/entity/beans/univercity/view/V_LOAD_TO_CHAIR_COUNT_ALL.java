package kz.halyqsoft.univercity.entity.beans.univercity.view;

import kz.halyqsoft.univercity.entity.beans.univercity.CURRICULUM;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.DEPARTMENT;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.SEMESTER;
import org.r3a.common.entity.AbstractEntity;
import org.r3a.common.entity.EFieldType;
import org.r3a.common.entity.FieldInfo;

import javax.persistence.*;

/**
 * @author Omarbek
 * @created on 30.06.2018
 */
@Entity
public class V_LOAD_TO_CHAIR_COUNT_ALL extends AbstractEntity{

    @FieldInfo(type = EFieldType.FK_DIALOG, order = 3, inGrid = false, columnWidth = 80)
    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "CURRICULUM_ID", referencedColumnName = "ID")})
    private CURRICULUM curriculum;

    @FieldInfo(type = EFieldType.FK_COMBO, order = 4)
    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "CHAIR_ID", referencedColumnName = "ID")})
    private DEPARTMENT chair;

    @FieldInfo(type = EFieldType.FK_COMBO, order = 5)
    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "SEMESTER_ID", referencedColumnName = "ID")})
    private SEMESTER semester;

    @FieldInfo(type = EFieldType.DOUBLE, order = 10)
    @Column(name = "LC_COUNT")
    private double lcCount;

    @FieldInfo(type = EFieldType.DOUBLE, order = 11)
    @Column(name = "PR_COUNT")
    private double prCount;

    @FieldInfo(type = EFieldType.DOUBLE, order = 12)
    @Column(name = "LB_COUNT")
    private double lbCount;

    @FieldInfo(type = EFieldType.DOUBLE, order = 13)
    @Column(name = "WITH_TEACHER_COUNT")
    private double withTeacherCount;

    @FieldInfo(type = EFieldType.DOUBLE, order = 14)
    @Column(name = "RATING_COUNT")
    private double ratingCount;

    @FieldInfo(type = EFieldType.DOUBLE, order = 15)
    @Column(name = "EXAM_COUNT")
    private double examCount;

    @FieldInfo(type = EFieldType.DOUBLE, order = 16)
    @Column(name = "CONTROL_COUNT")
    private double controlCount;

    @FieldInfo(type = EFieldType.DOUBLE, order = 17)
    @Column(name = "COURSE_WORK_COUNT")
    private double courseWorkCount;

    @FieldInfo(type = EFieldType.DOUBLE, order = 18)
    @Column(name = "DIPLOMA_COUNT")
    private double diplomaCount;

    @FieldInfo(type = EFieldType.DOUBLE, order = 19)
    @Column(name = "PRACTICE_COUNT")
    private double practiceCount;

    @FieldInfo(type = EFieldType.DOUBLE, order = 20)
    @Column(name = "MEK")
    private double mek;

    @FieldInfo(type = EFieldType.DOUBLE, order = 21)
    @Column(name = "PROTECT_DIPLOMA_COUNT")
    private double protectDiplomaCount;

    @FieldInfo(type = EFieldType.DOUBLE, order = 22)
    @Column(name = "TOTAL_COUNT")
    private double totalCount;

    public double getLcCount() {
        return lcCount;
    }

    public void setLcCount(double lcCount) {
        this.lcCount = lcCount;
    }

    public double getPrCount() {
        return prCount;
    }

    public void setPrCount(double prCount) {
        this.prCount = prCount;
    }

    public double getLbCount() {
        return lbCount;
    }

    public void setLbCount(double lbCount) {
        this.lbCount = lbCount;
    }

    public double getWithTeacherCount() {
        return withTeacherCount;
    }

    public void setWithTeacherCount(double withTeacherCount) {
        this.withTeacherCount = withTeacherCount;
    }

    public double getRatingCount() {
        return ratingCount;
    }

    public void setRatingCount(double ratingCount) {
        this.ratingCount = ratingCount;
    }

    public double getExamCount() {
        return examCount;
    }

    public void setExamCount(double examCount) {
        this.examCount = examCount;
    }

    public double getControlCount() {
        return controlCount;
    }

    public void setControlCount(double controlCount) {
        this.controlCount = controlCount;
    }

    public double getCourseWorkCount() {
        return courseWorkCount;
    }

    public void setCourseWorkCount(double courseWorkCount) {
        this.courseWorkCount = courseWorkCount;
    }

    public double getDiplomaCount() {
        return diplomaCount;
    }

    public void setDiplomaCount(double diplomaCount) {
        this.diplomaCount = diplomaCount;
    }

    public double getPracticeCount() {
        return practiceCount;
    }

    public void setPracticeCount(double practiceCount) {
        this.practiceCount = practiceCount;
    }

    public double getMek() {
        return mek;
    }

    public void setMek(double mek) {
        this.mek = mek;
    }

    public double getProtectDiplomaCount() {
        return protectDiplomaCount;
    }

    public void setProtectDiplomaCount(double protectDiplomaCount) {
        this.protectDiplomaCount = protectDiplomaCount;
    }

    public double getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(double totalCount) {
        this.totalCount = totalCount;
    }

    public CURRICULUM getCurriculum() {
        return curriculum;
    }

    public void setCurriculum(CURRICULUM curriculum) {
        this.curriculum = curriculum;
    }

    public DEPARTMENT getChair() {
        return chair;
    }

    public void setChair(DEPARTMENT chair) {
        this.chair = chair;
    }

    public SEMESTER getSemester() {
        return semester;
    }

    public void setSemester(SEMESTER semester) {
        this.semester = semester;
    }
}
