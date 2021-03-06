package kz.halyqsoft.univercity.entity.beans.univercity;

import kz.halyqsoft.univercity.entity.beans.univercity.catalog.SUBJECT;
import org.r3a.common.entity.AbstractEntity;
import org.r3a.common.entity.EFieldType;
import org.r3a.common.entity.FieldInfo;

import javax.persistence.*;


@Entity
public class STUDENT_DIFFERENCE extends AbstractEntity {

    private static final long serialVersionUID = 2009779400229209500L;


    @FieldInfo(type = EFieldType.FK_COMBO, order = 1 )
    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "STUDENT_EDUCATION_ID", referencedColumnName = "ID")})
    private STUDENT_EDUCATION studentEducation;

    @FieldInfo(type = EFieldType.FK_COMBO, order = 2 )
    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "SUBJECT_ID", referencedColumnName = "ID")})
    private SUBJECT subject;

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
