package kz.halyqsoft.univercity.entity.beans.univercity;

import kz.halyqsoft.univercity.entity.beans.USERS;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.LANGUAGE;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.SPECIALITY;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.STUDY_YEAR;
import org.r3a.common.entity.AbstractEntity;
import org.r3a.common.entity.EFieldType;
import org.r3a.common.entity.FieldInfo;

import javax.persistence.*;
import java.util.Date;

@Entity
public class SUBSTITUTION extends AbstractEntity {

    @FieldInfo(type = EFieldType.FK_COMBO, order = 1 )
    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "employee_id", referencedColumnName = "ID")})
    private USERS employee;

    @FieldInfo(type = EFieldType.FK_COMBO, order = 2)
    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "substitutor_id", referencedColumnName = "ID")})
    private USERS substitutor;

    @FieldInfo(type = EFieldType.DATETIME, order = 3)
    @Column(name = "until_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date untilDate;

    @FieldInfo(type = EFieldType.DATETIME, required = false, readOnlyFixed = true, inGrid = false, inEdit = false, inView = false, order = 4)
    @Column(name = "created")
    @Temporal(TemporalType.TIMESTAMP)
    private Date created;

    public SUBSTITUTION() {
    }

    public USERS getEmployee() {
        return employee;
    }

    public void setEmployee(USERS employee) {
        this.employee = employee;
    }

    public USERS getSubstitutor() {
        return substitutor;
    }

    public void setSubstitutor(USERS substitutor) {
        this.substitutor = substitutor;
    }

    public Date getUntilDate() {
        return untilDate;
    }

    public void setUntilDate(Date untilDate) {
        this.untilDate = untilDate;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }
}