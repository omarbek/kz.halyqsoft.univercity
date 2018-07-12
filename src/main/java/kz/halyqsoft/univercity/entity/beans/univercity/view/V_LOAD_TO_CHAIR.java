package kz.halyqsoft.univercity.entity.beans.univercity.view;

import kz.halyqsoft.univercity.entity.beans.univercity.CURRICULUM;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.SUBJECT;
import org.r3a.common.entity.AbstractEntity;
import org.r3a.common.entity.EFieldType;
import org.r3a.common.entity.FieldInfo;

import javax.persistence.*;

/**
 * @author Omarbek
 * @created on 29.06.2018
 */
@Entity
public class V_LOAD_TO_CHAIR extends AbstractEntity {

    @FieldInfo(type = EFieldType.FK_DIALOG, order = 2, inGrid = false, columnWidth = 80)
    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "SUBJECT_ID", referencedColumnName = "ID")})
    private SUBJECT subject;

    @FieldInfo(type = EFieldType.FK_DIALOG, order = 3, inGrid = false, columnWidth = 80)
    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "CURRICULUM_ID", referencedColumnName = "ID")})
    private CURRICULUM curriculum;

    @FieldInfo(type = EFieldType.TEXT, order = 4)
    @Column(name = "SUBJECT_NAME", nullable = false)
    private String subjectName;

    @FieldInfo(type = EFieldType.INTEGER, order = 5)
    @Column(name = "STUDY_YEAR")
    private Integer studyYear;

    @FieldInfo(type = EFieldType.TEXT, order = 6)
    @Column(name = "STREAM")
    private String stream;

    @FieldInfo(type = EFieldType.TEXT, order = 7)
    @Column(name = "SEMESTER_NAME")
    private String semesterName;

    @FieldInfo(type = EFieldType.INTEGER, order = 8)
    @Column(name = "STUDENT_NUMBER")
    private Integer studentNumber;

    @FieldInfo(type = EFieldType.DOUBLE, order = 9)
    @Column(name = "CREDIT")
    private Double credit;

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

    public SUBJECT getSubject() {
        return subject;
    }

    public void setSubject(SUBJECT subject) {
        this.subject = subject;
    }

    public CURRICULUM getCurriculum() {
        return curriculum;
    }

    public void setCurriculum(CURRICULUM curriculum) {
        this.curriculum = curriculum;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }

    public String getStream() {
        return stream;
    }

    public void setStream(String stream) {
        this.stream = stream;
    }

    public String getSemesterName() {
        return semesterName;
    }

    public void setSemesterName(String semesterName) {
        this.semesterName = semesterName;
    }

    public Integer getStudyYear() {
        return studyYear;
    }

    public void setStudyYear(Integer studyYear) {
        this.studyYear = studyYear;
    }

    public Integer getStudentNumber() {
        return studentNumber;
    }

    public void setStudentNumber(Integer studentNumber) {
        this.studentNumber = studentNumber;
    }

    public Double getCredit() {
        return credit;
    }

    public void setCredit(Double credit) {
        this.credit = credit;
    }

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
}
