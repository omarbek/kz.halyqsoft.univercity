package kz.halyqsoft.univercity.entity.beans.univercity;

import kz.halyqsoft.univercity.entity.beans.univercity.catalog.SEMESTER;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.SEMESTER_PERIOD;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.STUDENT_DIPLOMA_TYPE;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.SUBJECT;
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

    private static final long serialVersionUID = -1179682577968099408L;

    @FieldInfo(type = EFieldType.FK_COMBO)
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

    @FieldInfo(type = EFieldType.FK_DIALOG, order = 5)
    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "STREAM_ID", referencedColumnName = "ID")})
    private STREAM stream;

    @FieldInfo(type = EFieldType.FK_DIALOG, order = 6)
    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "GROUP_ID", referencedColumnName = "ID")})
    private GROUPS group;

    @FieldInfo(type = EFieldType.DOUBLE, order = 7)
    @Column(name = "LC_HOUR")
    private Double lcHour;

    @FieldInfo(type = EFieldType.DOUBLE, order = 8)
    @Column(name = "LB_HOUR")
    private Double lbHour;

    @FieldInfo(type = EFieldType.DOUBLE, order = 9)
    @Column(name = "PR_HOUR")
    private Double prHour;

    @FieldInfo(type = EFieldType.FK_COMBO, order = 10)
    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "SEMESTER_ID", referencedColumnName = "ID")})
    private SEMESTER semester;

    @FieldInfo(type = EFieldType.FK_COMBO, order = 11)
    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "STUDENT_DIPLOMA_TYPE_ID", referencedColumnName = "ID")})
    private STUDENT_DIPLOMA_TYPE studentDiplomaType;

    public TEACHER_LOAD_ASSIGN_DETAIL() {
    }

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

    public Double getLcHour() {
        return lcHour;
    }

    public void setLcHour(Double lcHour) {
        this.lcHour = lcHour;
    }

    public Double getLbHour() {
        return lbHour;
    }

    public void setLbHour(Double lbHour) {
        this.lbHour = lbHour;
    }

    public Double getPrHour() {
        return prHour;
    }

    public void setPrHour(Double prHour) {
        this.prHour = prHour;
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

    public GROUPS getGroup() {
        return group;
    }

    public void setGroup(GROUPS group) {
        this.group = group;
    }
}
