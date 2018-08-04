package kz.halyqsoft.univercity.entity.beans.univercity;

import kz.halyqsoft.univercity.entity.beans.univercity.catalog.DORM_ROOM;
import org.r3a.common.entity.AbstractEntity;
import org.r3a.common.entity.EFieldType;
import org.r3a.common.entity.FieldInfo;

import javax.persistence.*;
import java.util.Date;

/**
 * @author Omarbek
 * @created Apr 11, 2017 5:16:07 PM
 */
@Entity
public class DORM_STUDENT extends AbstractEntity {

	private static final long serialVersionUID = -14530491549311796L;

	@FieldInfo(type = EFieldType.FK_COMBO, order = 1)
	@ManyToOne
    @JoinColumns({
        @JoinColumn(name = "ROOM_ID", referencedColumnName = "ID", nullable = false)})
    private DORM_ROOM room;

	@FieldInfo(type = EFieldType.FK_COMBO, order = 2)
	@ManyToOne
    @JoinColumns({
        @JoinColumn(name = "STUDENT_ID", referencedColumnName = "ID", nullable = false)})
    private STUDENT_EDUCATION student;

	@FieldInfo(type = EFieldType.DOUBLE, order = 3)
	@Column(name = "COST", nullable = false)
	private Double cost;

	@FieldInfo(order = 4,inGrid = false)
	@Column(name = "CHECK_IN_DATE", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date checkInDate;

	@FieldInfo(order = 5,inGrid = false)
	@Column(name = "CHECK_OUT_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    private Date checkOutDate;

	@FieldInfo(order = 6,inGrid = false)
	@Column(name = "DELETED", nullable = false)
    private boolean deleted;

	@FieldInfo(order = 7,inGrid = false)
	@Column(name = "CREATED", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date created;

	@FieldInfo(order = 8,inGrid = false)
	@Column(name = "REQUEST_STATUS_ID", nullable = false)
	private int requestStatus;
	
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

	public int getRequestStatus() {
		return requestStatus;
	}

	public void setRequestStatus(int requestStatus) {
		this.requestStatus = requestStatus;
	}
}
