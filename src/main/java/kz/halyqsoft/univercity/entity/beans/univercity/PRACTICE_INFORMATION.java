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
public class PRACTICE_INFORMATION extends AbstractEntity {

    @FieldInfo(type = EFieldType.FK_COMBO, order = 1)
    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "groups_id", referencedColumnName = "ID", nullable = false)})
    GROUPS groups;

    @FieldInfo(type = EFieldType.FK_COMBO, order = 2)
    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "teacher_id", referencedColumnName = "ID", nullable = false)})
    USERS employee;

    @FieldInfo(type = EFieldType.DATETIME, order = 3)
    @Column(name = "created")
    @Temporal(TemporalType.TIMESTAMP)
    Date created;

    public PRACTICE_INFORMATION() {
    }

    public GROUPS getGroups() {
        return groups;
    }

    public void setGroups(GROUPS groups) {
        this.groups = groups;
    }

    public USERS getEmployee() {
        return employee;
    }

    public void setEmployee(USERS employee) {
        this.employee = employee;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }
}