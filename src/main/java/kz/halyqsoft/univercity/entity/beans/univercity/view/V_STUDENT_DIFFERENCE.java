package kz.halyqsoft.univercity.entity.beans.univercity.view;

import kz.halyqsoft.univercity.entity.beans.univercity.STUDENT_EDUCATION;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.SUBJECT;
import org.r3a.common.entity.AbstractEntity;
import org.r3a.common.entity.EFieldType;
import org.r3a.common.entity.FieldInfo;

import javax.persistence.*;

@Entity
public class V_STUDENT_DIFFERENCE extends AbstractEntity {

    private static final long serialVersionUID = 5389115473346404914L;

    @FieldInfo(type = EFieldType.FK_COMBO, order = 1, inEdit = false, inGrid = false, inView = false)
    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "student_education_id", referencedColumnName = "ID")})
    private STUDENT_EDUCATION studentEducation;

    @FieldInfo(type = EFieldType.FK_COMBO, order = 2)
    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "subject_id", referencedColumnName = "ID")})
    private SUBJECT subject;

    public V_STUDENT_DIFFERENCE() {
    }

    public V_STUDENT_DIFFERENCE(STUDENT_EDUCATION studentEducation, SUBJECT subject) {
        this.studentEducation = studentEducation;
        this.subject = subject;
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public STUDENT_EDUCATION getStudentEducation() {
        return studentEducation;
    }

    public void setStudentEducation(STUDENT_EDUCATION studentEducation) {
        this.studentEducation = studentEducation;
    }

    public SUBJECT getSubject() {
        return subject;
    }

    public void setSubject(SUBJECT subject) {
        this.subject = subject;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(studentEducation);
        sb.append(" ");
        sb.append(subject);


        return sb.toString();
    }
}
