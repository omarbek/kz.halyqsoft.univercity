package kz.halyqsoft.univercity.entity.beans.univercity;

import kz.halyqsoft.univercity.entity.beans.univercity.catalog.NON_ADMISSION_CAUSE;
import org.r3a.common.entity.AbstractEntity;
import org.r3a.common.entity.EFieldType;
import org.r3a.common.entity.FieldInfo;

import javax.persistence.*;
import java.util.Date;

@Entity
public class NON_ADMISSION_EXAM extends AbstractEntity {

    @FieldInfo(type = EFieldType.FK_COMBO)
    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "STUDENT_EDUCATION_ID", referencedColumnName = "ID", nullable = false)})
    private STUDENT_EDUCATION studentEducation;

    @FieldInfo(type = EFieldType.FK_COMBO, order = 2)
    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "NON_ADMISSION_CAUSE_ID", referencedColumnName = "ID", nullable = false)})
    private NON_ADMISSION_CAUSE nonAdmissionCause;

    @FieldInfo(type = EFieldType.DATETIME, required = false, readOnlyFixed = true, inGrid = false, inEdit = false, inView = false, order = 3)
    @Column(name = "created")
    @Temporal(TemporalType.TIMESTAMP)
    private Date created = new Date();

    public NON_ADMISSION_CAUSE getNonAdmissionCause() {
        return nonAdmissionCause;
    }

    public void setNonAdmissionCause(NON_ADMISSION_CAUSE nonAdmissionCause) {
        this.nonAdmissionCause = nonAdmissionCause;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public STUDENT_EDUCATION getStudentEducation() {
        return studentEducation;
    }

    public void setStudentEducation(STUDENT_EDUCATION studentEducation) {
        this.studentEducation = studentEducation;
    }
}
