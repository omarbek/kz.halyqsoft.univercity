package kz.halyqsoft.univercity.entity.beans.univercity;

import kz.halyqsoft.univercity.entity.beans.univercity.catalog.SEMESTER;
import org.r3a.common.entity.AbstractEntity;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;

/**
 * @author Omarbek
 * @created 19.09.2018
 */
@Entity
public class STUDENT_TEACHER_SUBJECT extends AbstractEntity {

    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "STUDENT_EDUCATION_ID", referencedColumnName = "ID")})
    private STUDENT_EDUCATION studentEducation;

    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "TEACHER_SUBJECT_ID", referencedColumnName = "ID")})
    private TEACHER_SUBJECT teacherSubject;

    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "SEMESTER_ID", referencedColumnName = "ID")})
    private SEMESTER semester;

    public STUDENT_EDUCATION getStudentEducation() {
        return studentEducation;
    }

    public void setStudentEducation(STUDENT_EDUCATION studentEducation) {
        this.studentEducation = studentEducation;
    }

    public TEACHER_SUBJECT getTeacherSubject() {
        return teacherSubject;
    }

    public void setTeacherSubject(TEACHER_SUBJECT teacherSubject) {
        this.teacherSubject = teacherSubject;
    }

    public SEMESTER getSemester() {
        return semester;
    }

    public void setSemester(SEMESTER semester) {
        this.semester = semester;
    }
}
