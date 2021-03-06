package kz.halyqsoft.univercity.entity.beans.univercity;

import kz.halyqsoft.univercity.entity.beans.univercity.catalog.CURRICULUM_STATUS;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.ENTRANCE_YEAR;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.SPECIALITY;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.STUDENT_DIPLOMA_TYPE;
import org.r3a.common.entity.AbstractEntity;
import org.r3a.common.entity.EFieldType;
import org.r3a.common.entity.FieldInfo;

import javax.persistence.*;
import java.util.Date;

/**
 * @author Omarbek
 * @created Feb 17, 2016 4:01:02 PM
 */
@Entity
public class CURRICULUM extends AbstractEntity {

    private static final long serialVersionUID = -6482182030687925440L;

    @FieldInfo(type = EFieldType.FK_COMBO, order = 1)
    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "SPECIALITY_ID", referencedColumnName = "ID")})
    private SPECIALITY speciality;

    @FieldInfo(type = EFieldType.FK_COMBO, order = 2)
    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "ENTRANCE_YEAR_ID", referencedColumnName = "ID")})
    private ENTRANCE_YEAR entranceYear;

    @FieldInfo(type = EFieldType.FK_COMBO, order = 3)
    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "STATUS_ID", referencedColumnName = "ID")})
    private CURRICULUM_STATUS curriculumStatus;

    @FieldInfo(type = EFieldType.FK_COMBO, order = 4)
    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "STUDENT_DIPLOMA_TYPE_ID", referencedColumnName = "ID")})
    private STUDENT_DIPLOMA_TYPE diplomaType;

    @FieldInfo(type = EFieldType.DATETIME, order = 5, required = false, readOnlyFixed = true, inGrid = false, inEdit = false, inView = false)
    @Column(name = "CREATED")
    @Temporal(TemporalType.TIMESTAMP)
    private Date created;

    @FieldInfo(type = EFieldType.DATETIME, order = 6, required = false, readOnlyFixed = true, inGrid = false, inEdit = false, inView = false)
    @Column(name = "UPDATED")
    @Temporal(TemporalType.TIMESTAMP)
    private Date updated;

    @FieldInfo(type = EFieldType.BOOLEAN, order = 7, required = false, inEdit = false, inGrid = false, inView = false)
    @Column(name = "DELETED", nullable = false)
    private boolean deleted;

    public CURRICULUM() {
    }

    public SPECIALITY getSpeciality() {
        return speciality;
    }

    public void setSpeciality(SPECIALITY speciality) {
        this.speciality = speciality;
    }

    public ENTRANCE_YEAR getEntranceYear() {
        return entranceYear;
    }

    public void setEntranceYear(ENTRANCE_YEAR entranceYear) {
        this.entranceYear = entranceYear;
    }

    public CURRICULUM_STATUS getCurriculumStatus() {
        return curriculumStatus;
    }

    public void setCurriculumStatus(CURRICULUM_STATUS curriculumStatus) {
        this.curriculumStatus = curriculumStatus;
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

    public STUDENT_DIPLOMA_TYPE getDiplomaType() {
        return diplomaType;
    }

    public void setDiplomaType(STUDENT_DIPLOMA_TYPE diplomaType) {
        this.diplomaType = diplomaType;
    }

    @Override
    public String toString() {
        return speciality + " "+
               entranceYear + " "+
               curriculumStatus + " "+
               diplomaType + " "+ created ;
    }
}
