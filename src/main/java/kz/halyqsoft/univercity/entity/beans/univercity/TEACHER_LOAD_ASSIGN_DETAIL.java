package kz.halyqsoft.univercity.entity.beans.univercity;

import kz.halyqsoft.univercity.entity.beans.univercity.catalog.*;
import org.r3a.common.entity.AbstractEntity;
import org.r3a.common.entity.EFieldType;
import org.r3a.common.entity.FieldInfo;

import javax.persistence.*;

/**
 * @author Omarbek
 * @created Mar 24, 2016 10:59:00 AM
 */
@Entity
public class TEACHER_LOAD_ASSIGN_DETAIL extends AbstractEntity {

    @FieldInfo(type = EFieldType.FK_COMBO, inGrid = false, inEdit = false)
    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "TEACHER_LOAD_ASSIGN_ID", referencedColumnName = "ID")})
    private TEACHER_LOAD_ASSIGN teacherLoadAssign;

    @FieldInfo(type = EFieldType.FK_COMBO, order = 2)
    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "TEACHER_ID", referencedColumnName = "ID")})
    private EMPLOYEE teacher;

    @FieldInfo(type = EFieldType.FK_DIALOG, order = 3)
    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "SUBJECT_ID", referencedColumnName = "ID")})
    private SUBJECT subject;

    @FieldInfo(type = EFieldType.FK_COMBO, order = 4)
    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "SEMESTER_PERIOD_ID", referencedColumnName = "ID")})
    private SEMESTER_PERIOD semesterPeriod;

    @FieldInfo(type = EFieldType.FK_DIALOG, order = 5, required = false)
    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "STREAM_ID", referencedColumnName = "ID")})
    private STREAM stream;

    @FieldInfo(type = EFieldType.FK_DIALOG, order = 6, required = false)
    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "GROUP_ID", referencedColumnName = "ID")})
    private GROUPS group;

    @FieldInfo(type = EFieldType.FK_DIALOG, order = 7, readOnlyFixed = true)
    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "STUDY_YEAR_ID", referencedColumnName = "ID")})
    private STUDY_YEAR studyYear;

    @FieldInfo(type = EFieldType.FK_COMBO, order = 8)
    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "SEMESTER_ID", referencedColumnName = "ID")})
    private SEMESTER semester;

    @FieldInfo(type = EFieldType.FK_COMBO, order = 9)
    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "STUDENT_DIPLOMA_TYPE_ID", referencedColumnName = "ID")})
    private STUDENT_DIPLOMA_TYPE studentDiplomaType;

    @FieldInfo(type = EFieldType.INTEGER, order = 10, inEdit = false)
    @Column(name = "STUDENT_COUNT", nullable = false)
    private Integer studentCount;

    @FieldInfo(type = EFieldType.FK_COMBO, order = 11, inEdit = false)
    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "CREDITABILITY_ID", referencedColumnName = "ID")})
    private CREDITABILITY credit;

    @FieldInfo(type = EFieldType.DOUBLE, order = 12)
    @Column(name = "LC_HOUR")
    private Double lcHour;

    @FieldInfo(type = EFieldType.DOUBLE, order = 13)
    @Column(name = "PR_HOUR")
    private Double prHour;

    @FieldInfo(type = EFieldType.DOUBLE, order = 14)
    @Column(name = "LB_HOUR")
    private Double lbHour;

    @FieldInfo(type = EFieldType.DOUBLE, order = 15)
    @Column(name = "WITH_TEACHER_HOUR")
    private Double withTeacherHour;

    @FieldInfo(type = EFieldType.DOUBLE, order = 16)
    @Column(name = "RATING_HOUR")
    private Double ratingHour;

    @FieldInfo(type = EFieldType.DOUBLE, order = 17)
    @Column(name = "EXAM_HOUR")
    private Double examHour;

    @FieldInfo(type = EFieldType.DOUBLE, order = 18)
    @Column(name = "CONTROL_HOUR")
    private Double controlHour;

    @FieldInfo(type = EFieldType.DOUBLE, order = 19)
    @Column(name = "COURSE_WORK_HOUR")
    private Double courseWorkHour;

    @FieldInfo(type = EFieldType.DOUBLE, order = 20)
    @Column(name = "DIPLOMA_HOUR")
    private Double diplomaHour;

    @FieldInfo(type = EFieldType.DOUBLE, order = 21)
    @Column(name = "PRACTICE_HOUR")
    private Double practiceHour;

    @FieldInfo(type = EFieldType.DOUBLE, order = 22)
    @Column(name = "MEK")
    private Double mek;

    @FieldInfo(type = EFieldType.DOUBLE, order = 23)
    @Column(name = "PROTECT_DIPLOMA_HOUR")
    private Double protectDiplomaHour;

    @FieldInfo(type = EFieldType.DOUBLE, order = 24, inEdit = false)
    @Column(name = "TOTAL_HOUR", nullable = false)
    private Double totalHour;

    public TEACHER_LOAD_ASSIGN getTeacherLoadAssign() {
        return teacherLoadAssign;
    }

    public void setTeacherLoadAssign(TEACHER_LOAD_ASSIGN teacherLoadAssign) {
        this.teacherLoadAssign = teacherLoadAssign;
    }

    public EMPLOYEE getTeacher() {
        return teacher;
    }

    public void setTeacher(EMPLOYEE teacher) {
        this.teacher = teacher;
    }

    public SUBJECT getSubject() {
        return subject;
    }

    public void setSubject(SUBJECT subject) {
        this.subject = subject;
    }

    public SEMESTER_PERIOD getSemesterPeriod() {
        return semesterPeriod;
    }

    public void setSemesterPeriod(SEMESTER_PERIOD semesterPeriod) {
        this.semesterPeriod = semesterPeriod;
    }

    public STREAM getStream() {
        return stream;
    }

    public void setStream(STREAM stream) {
        this.stream = stream;
    }

    public GROUPS getGroup() {
        return group;
    }

    public void setGroup(GROUPS group) {
        this.group = group;
    }

    public STUDY_YEAR getStudyYear() {
        return studyYear;
    }

    public void setStudyYear(STUDY_YEAR studyYear) {
        this.studyYear = studyYear;
    }

    public SEMESTER getSemester() {
        return semester;
    }

    public void setSemester(SEMESTER semester) {
        this.semester = semester;
    }

    public STUDENT_DIPLOMA_TYPE getStudentDiplomaType() {
        return studentDiplomaType;
    }

    public void setStudentDiplomaType(STUDENT_DIPLOMA_TYPE studentDiplomaType) {
        this.studentDiplomaType = studentDiplomaType;
    }

    public Integer getStudentCount() {
        return studentCount;
    }

    public void setStudentCount(Integer studentCount) {
        this.studentCount = studentCount;
    }

    public CREDITABILITY getCredit() {
        return credit;
    }

    public void setCredit(CREDITABILITY credit) {
        this.credit = credit;
    }

    public Double getLcHour() {
        return lcHour;
    }

    public void setLcHour(Double lcHour) {
        this.lcHour = lcHour;
    }

    public Double getPrHour() {
        return prHour;
    }

    public void setPrHour(Double prHour) {
        this.prHour = prHour;
    }

    public Double getLbHour() {
        return lbHour;
    }

    public void setLbHour(Double lbHour) {
        this.lbHour = lbHour;
    }

    public Double getWithTeacherHour() {
        return withTeacherHour;
    }

    public void setWithTeacherHour(Double withTeacherHour) {
        this.withTeacherHour = withTeacherHour;
    }

    public Double getRatingHour() {
        return ratingHour;
    }

    public void setRatingHour(Double ratingHour) {
        this.ratingHour = ratingHour;
    }

    public Double getExamHour() {
        return examHour;
    }

    public void setExamHour(Double examHour) {
        this.examHour = examHour;
    }

    public Double getControlHour() {
        return controlHour;
    }

    public void setControlHour(Double controlHour) {
        this.controlHour = controlHour;
    }

    public Double getCourseWorkHour() {
        return courseWorkHour;
    }

    public void setCourseWorkHour(Double courseWorkHour) {
        this.courseWorkHour = courseWorkHour;
    }

    public Double getDiplomaHour() {
        return diplomaHour;
    }

    public void setDiplomaHour(Double diplomaHour) {
        this.diplomaHour = diplomaHour;
    }

    public Double getPracticeHour() {
        return practiceHour;
    }

    public void setPracticeHour(Double practiceHour) {
        this.practiceHour = practiceHour;
    }

    public Double getMek() {
        return mek;
    }

    public void setMek(Double mek) {
        this.mek = mek;
    }

    public Double getProtectDiplomaHour() {
        return protectDiplomaHour;
    }

    public void setProtectDiplomaHour(Double protectDiplomaHour) {
        this.protectDiplomaHour = protectDiplomaHour;
    }

    public Double getTotalHour() {
        return totalHour;
    }

    public void setTotalHour(Double totalHour) {
        this.totalHour = totalHour;
    }
}
