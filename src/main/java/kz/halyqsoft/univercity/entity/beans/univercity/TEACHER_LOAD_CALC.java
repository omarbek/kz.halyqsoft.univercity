package kz.halyqsoft.univercity.entity.beans.univercity;

import kz.halyqsoft.univercity.entity.beans.univercity.catalog.DEPARTMENT;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.ENTRANCE_YEAR;
import org.r3a.common.entity.AbstractEntity;
import org.r3a.common.entity.EFieldType;
import org.r3a.common.entity.FieldInfo;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * @@author Omarbek
 * @created Mar 15, 2016 11:12:28 AM
 */
@Entity
public class TEACHER_LOAD_CALC extends AbstractEntity {

	private static final long serialVersionUID = -7679995345278977470L;

	@FieldInfo(type = EFieldType.FK_COMBO, order = 1)
    @ManyToOne
    @JoinColumns({
        @JoinColumn(name = "YEAR_ID", referencedColumnName = "ID")})
    private ENTRANCE_YEAR year;
	
	@FieldInfo(type = EFieldType.FK_DIALOG, order = 2, inGrid = false)
    @ManyToOne
    @JoinColumns({
        @JoinColumn(name = "CHAIR_ID", referencedColumnName = "ID")})
    private DEPARTMENT chair;
	
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
	
	public TEACHER_LOAD_CALC() {
	}

	public ENTRANCE_YEAR getYear() {
		return year;
	}

	public void setYear(ENTRANCE_YEAR year) {
		this.year = year;
	}

	public DEPARTMENT getChair() {
		return chair;
	}

	public void setChair(DEPARTMENT chair) {
		this.chair = chair;
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
}
