package kz.halyqsoft.univercity.entity.beans.univercity;

import kz.halyqsoft.univercity.entity.beans.univercity.catalog.SEMESTER;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.SEMESTER_DATA;
import org.r3a.common.entity.AbstractEntity;
import org.r3a.common.entity.EFieldType;
import org.r3a.common.entity.FieldInfo;

import javax.persistence.*;
import java.util.Date;

@Entity
public class STREAM extends AbstractEntity {

    @FieldInfo(type = EFieldType.TEXT, required = false)
    @Column(name = "NAME")
    private String name;

    @FieldInfo(type = EFieldType.FK_COMBO, order = 2)
    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "SEMESTER_DATA_ID", referencedColumnName = "ID")})
    private SEMESTER_DATA semesterData;

    @FieldInfo(type = EFieldType.FK_COMBO, order = 3, required = false)
    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "SEMESTER_ID", referencedColumnName = "ID")})
    private SEMESTER semester;

    @FieldInfo(type = EFieldType.DATETIME, required = false, readOnlyFixed = true, inGrid = false, inEdit = false, order = 4)
    @Column(name = "created")
    @Temporal(TemporalType.TIMESTAMP)
    private Date created;

    @FieldInfo(type = EFieldType.DATETIME, required = false, readOnlyFixed = true, inGrid = false, inEdit = false,
            inView = false, order = 5)
    @Column(name = "updated")
    @Temporal(TemporalType.TIMESTAMP)
    private Date updated;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public SEMESTER_DATA getSemesterData() {
        return semesterData;
    }

    public void setSemesterData(SEMESTER_DATA semesterData) {
        this.semesterData = semesterData;
    }

    public SEMESTER getSemester() {
        return semester;
    }

    public void setSemester(SEMESTER semester) {
        this.semester = semester;
    }

    @Override
    public String toString() {
        return name;
    }
}
