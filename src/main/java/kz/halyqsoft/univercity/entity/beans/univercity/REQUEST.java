package kz.halyqsoft.univercity.entity.beans.univercity;

import kz.halyqsoft.univercity.entity.beans.univercity.catalog.REQUEST_STATUS;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.REQUEST_TYPE;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.ROOM;
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
 * @created Apr 19, 2017 9:44:57 AM
 */
@Entity
public class REQUEST extends AbstractEntity {

	private static final long serialVersionUID = 7345984856461290564L;

	@ManyToOne
    @JoinColumns({
        @JoinColumn(name = "REQUEST_TYPE_ID", referencedColumnName = "ID", nullable = false)})
    private REQUEST_TYPE requestType;
	
	@ManyToOne
    @JoinColumns({
        @JoinColumn(name = "EMPLOYEE_ID", referencedColumnName = "ID", nullable = false)})
    private EMPLOYEE employee;
	
	@Column(name = "REQUEST_DATE", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date requestDate;
	
	@ManyToOne
    @JoinColumns({
        @JoinColumn(name = "ROOM_ID", referencedColumnName = "ID", nullable = false)})
    private ROOM room;
	
	@ManyToOne
    @JoinColumns({
        @JoinColumn(name = "REQUEST_STATUS_ID", referencedColumnName = "ID", nullable = false)})
    private REQUEST_STATUS status;
	
	@ManyToOne
    @JoinColumns({
        @JoinColumn(name = "RESP_EMPL_ID", referencedColumnName = "ID")})
    private EMPLOYEE responsibleEmployee;
	
	@Column(name = "DESCR", nullable = true)
	private String descr;
	
	public REQUEST() {
	}

	public REQUEST_TYPE getRequestType() {
		return requestType;
	}

	public void setRequestType(REQUEST_TYPE requestType) {
		this.requestType = requestType;
	}

	public EMPLOYEE getEmployee() {
		return employee;
	}

	public void setEmployee(EMPLOYEE employee) {
		this.employee = employee;
	}

	public Date getRequestDate() {
		return requestDate;
	}

	public void setRequestDate(Date requestDate) {
		this.requestDate = requestDate;
	}

	public ROOM getRoom() {
		return room;
	}

	public void setRoom(ROOM room) {
		this.room = room;
	}

	public REQUEST_STATUS getStatus() {
		return status;
	}

	public void setStatus(REQUEST_STATUS status) {
		this.status = status;
	}

	public EMPLOYEE getResponsibleEmployee() {
		return responsibleEmployee;
	}

	public void setResponsibleEmployee(EMPLOYEE responsibleEmployee) {
		this.responsibleEmployee = responsibleEmployee;
	}

	public String getDescr() {
		return descr;
	}

	public void setDescr(String descr) {
		this.descr = descr;
	}
}
