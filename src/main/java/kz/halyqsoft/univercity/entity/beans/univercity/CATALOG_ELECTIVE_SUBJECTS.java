package kz.halyqsoft.univercity.entity.beans.univercity;

import kz.halyqsoft.univercity.entity.beans.univercity.catalog.CURRICULUM_STATUS;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.ENTRANCE_YEAR;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.SPECIALITY;
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
public class CATALOG_ELECTIVE_SUBJECTS extends AbstractEntity {

	private static final long serialVersionUID = -6482182030687925440L;

	@FieldInfo(type = EFieldType.FK_COMBO, order = 2)
    @ManyToOne
    @JoinColumns({
        @JoinColumn(name = "SPECIALITY_ID", referencedColumnName = "ID")})
    private SPECIALITY speciality;

	@FieldInfo(type = EFieldType.FK_COMBO, order = 3)
    @ManyToOne
    @JoinColumns({
        @JoinColumn(name = "ENTRANCE_YEAR_ID", referencedColumnName = "ID")})
    private ENTRANCE_YEAR entranceYear;

	@FieldInfo(type = EFieldType.DATETIME, order = 4, required = false, readOnlyFixed = true, inGrid = false, inEdit = false, inView = false)
	@Column(name = "CREATED")
    @Temporal(TemporalType.TIMESTAMP)
    private Date created;

	@FieldInfo(type = EFieldType.DATETIME, order = 5, required = false, readOnlyFixed = true, inGrid = false, inEdit = false, inView = false)
	@Column(name = "UPDATED")
    @Temporal(TemporalType.TIMESTAMP)
    private Date updated;

	@FieldInfo(type = EFieldType.BOOLEAN, order = 6, required = false, inEdit = false, inGrid = false, inView = false)
	@Column(name = "DELETED", nullable = false)
    private boolean deleted;

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
}
