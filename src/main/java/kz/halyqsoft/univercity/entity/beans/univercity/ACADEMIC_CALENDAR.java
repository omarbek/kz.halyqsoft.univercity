package kz.halyqsoft.univercity.entity.beans.univercity;

import kz.halyqsoft.univercity.entity.beans.univercity.catalog.ACADEMIC_CALENDAR_FACULTY;
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
 * @created Oct 22, 2016 1:53:55 PM
 */
@Entity
public class ACADEMIC_CALENDAR extends AbstractEntity {

	private static final long serialVersionUID = -5321922531931501550L;

	@FieldInfo(type = EFieldType.FK_COMBO, order = 1)
    @ManyToOne
    @JoinColumns({
        @JoinColumn(name = "YEAR_ID", referencedColumnName = "ID")})
    private ENTRANCE_YEAR year;
	
	@FieldInfo(type = EFieldType.FK_COMBO, order = 2)
    @ManyToOne
    @JoinColumns({
        @JoinColumn(name = "FACULTY_ID", referencedColumnName = "ID")})
    private ACADEMIC_CALENDAR_FACULTY faculty;
	
	@FieldInfo(type = EFieldType.DATETIME, order = 3, required = false, readOnlyFixed = true, inGrid = false, inEdit = false, inView = false)
	@Column(name = "CREATED")
    @Temporal(TemporalType.TIMESTAMP)
    private Date created;
	
	@Column(name = "STATUS")
	private Integer status;
	
	public ACADEMIC_CALENDAR() {
	}

	public ENTRANCE_YEAR getYear() {
		return year;
	}

	public void setYear(ENTRANCE_YEAR year) {
		this.year = year;
	}

	public ACADEMIC_CALENDAR_FACULTY getFaculty() {
		return faculty;
	}

	public void setFaculty(ACADEMIC_CALENDAR_FACULTY faculty) {
		this.faculty = faculty;
	}

	public Date getCreated() {
		return created;
	}

	public void setCreated(Date created) {
		this.created = created;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}
}
