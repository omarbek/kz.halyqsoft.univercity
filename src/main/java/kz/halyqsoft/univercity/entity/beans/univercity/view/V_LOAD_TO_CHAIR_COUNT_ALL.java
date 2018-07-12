package kz.halyqsoft.univercity.entity.beans.univercity.view;

import kz.halyqsoft.univercity.entity.beans.univercity.CURRICULUM;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.DEPARTMENT;
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

    @FieldInfo(type = EFieldType.DOUBLE, order = 10)
    @Column(name = "LC_COUNT")
    private Double lcCount;

    @FieldInfo(type = EFieldType.DOUBLE, order = 11)
    @Column(name = "PR_COUNT")
    private Double prCount;

    @FieldInfo(type = EFieldType.DOUBLE, order = 12)
    @Column(name = "LB_COUNT")
    private Double lbCount;

    @FieldInfo(type = EFieldType.DOUBLE, order = 13)
    @Column(name = "WITH_TEACHER_COUNT")
    private Double withTeacherCount;

    @FieldInfo(type = EFieldType.DOUBLE, order = 14)
    @Column(name = "RATING_COUNT")
    private Double ratingCount;

    @FieldInfo(type = EFieldType.DOUBLE, order = 15)
    @Column(name = "EXAM_COUNT")
    private Double examCount;

    @FieldInfo(type = EFieldType.DOUBLE, order = 16)
    @Column(name = "CONTROL_COUNT")
    private Double controlCount;

    @FieldInfo(type = EFieldType.DOUBLE, order = 17)
    @Column(name = "COURSE_WORK_COUNT")
    private Double courseWorkCount;

    @FieldInfo(type = EFieldType.DOUBLE, order = 18)
    @Column(name = "DIPLOMA_COUNT")
    private Double diplomaCount;

    @FieldInfo(type = EFieldType.DOUBLE, order = 19)
    @Column(name = "PRACTICE_COUNT")
    private Double practiceCount;

    @FieldInfo(type = EFieldType.DOUBLE, order = 20)
    @Column(name = "MEK")
    private Double mek;

    @FieldInfo(type = EFieldType.DOUBLE, order = 21)
    @Column(name = "PROTECT_DIPLOMA_COUNT")
    private Double protectDiplomaCount;

    @FieldInfo(type = EFieldType.DOUBLE, order = 22)
    @Column(name = "TOTAL_COUNT")
    private Double totalCount;

    public Double getLcCount() {
        return lcCount;
    }

    public void setLcCount(Double lcCount) {
        this.lcCount = lcCount;
    }

    public Double getPrCount() {
        return prCount;
    }

    public void setPrCount(Double prCount) {
        this.prCount = prCount;
    }

    public Double getLbCount() {
        return lbCount;
    }

    public void setLbCount(Double lbCount) {
        this.lbCount = lbCount;
    }

    public Double getWithTeacherCount() {
        return withTeacherCount;
    }

    public void setWithTeacherCount(Double withTeacherCount) {
        this.withTeacherCount = withTeacherCount;
    }

    public Double getRatingCount() {
        return ratingCount;
    }

    public void setRatingCount(Double ratingCount) {
        this.ratingCount = ratingCount;
    }

    public Double getExamCount() {
        return examCount;
    }

    public void setExamCount(Double examCount) {
        this.examCount = examCount;
    }

    public Double getControlCount() {
        return controlCount;
    }

    public void setControlCount(Double controlCount) {
        this.controlCount = controlCount;
    }

    public Double getCourseWorkCount() {
        return courseWorkCount;
    }

    public void setCourseWorkCount(Double courseWorkCount) {
        this.courseWorkCount = courseWorkCount;
    }

    public Double getDiplomaCount() {
        return diplomaCount;
    }

    public void setDiplomaCount(Double diplomaCount) {
        this.diplomaCount = diplomaCount;
    }

    public Double getPracticeCount() {
        return practiceCount;
    }

    public void setPracticeCount(Double practiceCount) {
        this.practiceCount = practiceCount;
    }

    public Double getMek() {
        return mek;
    }

    public void setMek(Double mek) {
        this.mek = mek;
    }

    public Double getProtectDiplomaCount() {
        return protectDiplomaCount;
    }

    public void setProtectDiplomaCount(Double protectDiplomaCount) {
        this.protectDiplomaCount = protectDiplomaCount;
    }

    public Double getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(Double totalCount) {
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
}
