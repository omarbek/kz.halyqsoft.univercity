package kz.halyqsoft.univercity.entity.beans.univercity;

import kz.halyqsoft.univercity.entity.beans.univercity.catalog.DEPARTMENT;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.ENTRANCE_YEAR;
import org.r3a.common.entity.AbstractEntity;
import org.r3a.common.entity.EFieldType;
import org.r3a.common.entity.FieldInfo;

import javax.persistence.*;
import java.util.Date;

/**
 * @author Omarbek
 * @created Mar 23, 2016 9:34:06 AM
 */
@Entity
public class TEACHER_LOAD_ASSIGN extends AbstractEntity {

    private static final long serialVersionUID = -8088033612264741103L;

    @FieldInfo(type = EFieldType.FK_COMBO, order = 1)
    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "CHAIR_ID", referencedColumnName = "ID")})
    private DEPARTMENT department;

    @FieldInfo(type = EFieldType.FK_COMBO, order = 2)
    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "ENTRANCE_YEAR_ID", referencedColumnName = "ID")})
    private ENTRANCE_YEAR entranceYear;

    @FieldInfo(type = EFieldType.DATETIME, order = 3, required = false, readOnlyFixed = true, inGrid = false, inEdit = false, inView = false)
    @Column(name = "CREATED")
    @Temporal(TemporalType.TIMESTAMP)
    private Date created;

    @FieldInfo(type = EFieldType.DATETIME, order = 4, required = false, readOnlyFixed = true, inGrid = false, inEdit = false, inView = false)
    @Column(name = "UPDATED")
    @Temporal(TemporalType.TIMESTAMP)
    private Date updated;

    @FieldInfo(type = EFieldType.BOOLEAN, order = 5, required = false, inEdit = false, inGrid = false, inView = false)
    @Column(name = "DELETED", nullable = false)
    private boolean deleted;

    @FieldInfo(type = EFieldType.BOOLEAN, order = 6, required = false, inGrid = false, readOnlyFixed = true)
    @Column(name = "ACCEPTED", nullable = false)
    private boolean accepted;

    public TEACHER_LOAD_ASSIGN() {
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public Date getUpdated() {
        return updated;
    }

    public void setUpdated(Date updated) {
        this.updated = updated;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public boolean isAccepted() {
        return accepted;
    }

    public void setAccepted(boolean accepted) {
        this.accepted = accepted;
    }

    public DEPARTMENT getDepartment() {
        return department;
    }

    public void setDepartment(DEPARTMENT department) {
        this.department = department;
    }

    public ENTRANCE_YEAR getEntranceYear() {
        return entranceYear;
    }

    public void setEntranceYear(ENTRANCE_YEAR entranceYear) {
        this.entranceYear = entranceYear;
    }

    @Override
    public String toString() {
        return department + " " + entranceYear;
    }
}
