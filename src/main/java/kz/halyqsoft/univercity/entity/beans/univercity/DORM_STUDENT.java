package kz.halyqsoft.univercity.entity.beans.univercity;

import kz.halyqsoft.univercity.entity.beans.univercity.catalog.DORM_ROOM;
import org.r3a.common.entity.AbstractEntity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * @author Rakymzhan A. Kenzhegul
 * @created Apr 11, 2017 5:16:07 PM
 */
@Entity
public class DORM_STUDENT extends AbstractEntity {

	private static final long serialVersionUID = -14530491549311796L;

	@ManyToOne
    @JoinColumns({
        @JoinColumn(name = "ROOM_ID", referencedColumnName = "ID", nullable = false)})
    private DORM_ROOM room;
	
	@ManyToOne
    @JoinColumns({
        @JoinColumn(name = "STUDENT_ID", referencedColumnName = "ID", nullable = false)})
    private STUDENT_EDUCATION student;
	
	@Column(name = "COST", nullable = false)
	private Double cost;
	
	@Column(name = "CHECK_IN_DATE", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date checkInDate;
	
	@Column(name = "CHECK_OUT_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    private Date checkOutDate;
	
	@Column(name = "DELETED", nullable = false)
    private boolean deleted;
	
	@Column(name = "CREATED", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date created;
	
	public DORM_STUDENT() {
	}

	public DORM_ROOM getRoom() {
		return room;
	}

	public void setRoom(DORM_ROOM room) {
		this.room = room;
	}

	public STUDENT_EDUCATION getStudent() {
		return student;
	}

	public void setStudent(STUDENT_EDUCATION student) {
		this.student = student;
	}

	public Double getCost() {
		return cost;
	}

	public void setCost(Double cost) {
		this.cost = cost;
	}

	public Date getCheckInDate() {
		return checkInDate;
	}

	public void setCheckInDate(Date checkInDate) {
		this.checkInDate = checkInDate;
	}

	public Date getCheckOutDate() {
		return checkOutDate;
	}

	public void setCheckOutDate(Date checkOutDate) {
		this.checkOutDate = checkOutDate;
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
}
