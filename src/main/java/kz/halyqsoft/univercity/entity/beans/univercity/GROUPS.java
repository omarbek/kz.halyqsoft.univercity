package kz.halyqsoft.univercity.entity.beans.univercity;

import kz.halyqsoft.univercity.entity.beans.univercity.catalog.LANGUAGE;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.SPECIALITY;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.STUDY_YEAR;
import org.r3a.common.entity.AbstractEntity;
import org.r3a.common.entity.EFieldType;
import org.r3a.common.entity.FieldInfo;

import javax.persistence.*;
import java.util.Date;

@Entity
public class GROUPS extends AbstractEntity {

    @FieldInfo(type = EFieldType.FK_COMBO)
    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "SPECIALITY_ID", referencedColumnName = "ID", nullable = false)})
    private SPECIALITY speciality;

    @FieldInfo(type = EFieldType.FK_COMBO , order = 2)
    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "LANGUAGE_ID", referencedColumnName = "ID", nullable = false)})
    private LANGUAGE language;

    @FieldInfo(type = EFieldType.FK_COMBO , order = 3)
    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "STUDY_YEAR_ID", referencedColumnName = "ID", nullable = false)})
    private STUDY_YEAR studyYear;

    @FieldInfo(type = EFieldType.TEXT, order = 4)
    @Column(name = "name" , nullable = false)
    private String name;

    @FieldInfo(type = EFieldType.INTEGER, order = 5)
    @Column(name = "orders", nullable = false)
    private Long orders;

    @FieldInfo(type = EFieldType.BOOLEAN, inGrid = false, inEdit = false, inView = false )
    @Column(name = "deleted", nullable = false)
    private boolean deleted;

    @FieldInfo(type = EFieldType.DATETIME, required = false, readOnlyFixed = true, inGrid = false, inEdit = false, inView = false)
    @Column(name = "created")
    @Temporal(TemporalType.TIMESTAMP)
    private Date created;

    public GROUPS() {
    }

    public SPECIALITY getSpeciality() {
        return speciality;
    }

    public void setSpeciality(SPECIALITY speciality) {
        this.speciality = speciality;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getOrders() {
        return orders;
    }

    public void setOrders(Long orders) {
        this.orders = orders;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public LANGUAGE getLanguage() {
        return language;
    }

    public void setLanguage(LANGUAGE language) {
        this.language = language;
    }

    public STUDY_YEAR getStudyYear() {
        return studyYear;
    }

    public void setStudyYear(STUDY_YEAR studyYear) {
        this.studyYear = studyYear;
    }

    @Override
    public String toString() {
        return name;
    }
}