package kz.halyqsoft.univercity.entity.beans.univercity;

import kz.halyqsoft.univercity.entity.beans.USERS;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.ORGANIZATION;
import org.r3a.common.entity.AbstractEntity;
import org.r3a.common.entity.EFieldType;
import org.r3a.common.entity.FieldInfo;

import javax.persistence.*;
import java.util.Date;

@Entity
public class PRACTICE_STUDENT extends AbstractEntity {

    @FieldInfo(type = EFieldType.FK_COMBO, order = 1)
    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "student_id", referencedColumnName = "ID", nullable = false)})
    USERS student;

    @FieldInfo(type = EFieldType.FK_COMBO, order = 2)
    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "organization_id", referencedColumnName = "ID", nullable = false)})
    ORGANIZATION organization;

    @FieldInfo(type = EFieldType.DATETIME, order = 3)
    @Column(name = "come_in_date", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    Date comeInDate;

    @FieldInfo(type = EFieldType.DATETIME, order = 4,required = false)
    @Column(name = "come_out_date", nullable = true)
    @Temporal(TemporalType.TIMESTAMP)
    Date comeOutDate;

    @FieldInfo(type = EFieldType.FK_COMBO, order = 5,required = false)
    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "employee_id", referencedColumnName = "ID", nullable = false)})
    USERS employee;

    public PRACTICE_STUDENT() {
    }

    public USERS getStudent() {
        return student;
    }

    public void setStudent(USERS student) {
        this.student = student;
    }

    public ORGANIZATION getOrganization() {
        return organization;
    }

    public void setOrganization(ORGANIZATION organization) {
        this.organization = organization;
    }

    public Date getComeInDate() {
        return comeInDate;
    }

    public void setComeInDate(Date comeInDate) {
        this.comeInDate = comeInDate;
    }

    public Date getComeOutDate() {
        return comeOutDate;
    }

    public void setComeOutDate(Date comeOutDate) {
        this.comeOutDate = comeOutDate;
    }

    public USERS getEmployee() {
        return employee;
    }

    public void setEmployee(USERS employee) {
        this.employee = employee;
    }
}