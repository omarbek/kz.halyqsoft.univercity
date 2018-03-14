package kz.halyqsoft.univercity.entity.beans.univercity;

import kz.halyqsoft.univercity.entity.beans.univercity.catalog.SUBJECT;
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
 * @@author Omarbek
 * @created Mar 15, 2017 6:01:27 PM
 */
@Entity
public class QUESTIONING extends AbstractEntity {

	private static final long serialVersionUID = -575139584100869072L;

	@ManyToOne
    @JoinColumns({
        @JoinColumn(name = "USER_ID", referencedColumnName = "ID")})
    private USER user;
	
	@ManyToOne
    @JoinColumns({
        @JoinColumn(name = "SUBJECT_ID", referencedColumnName = "ID")})
    private SUBJECT subject;
	
	@Column(name = "BEGIN_DATE", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date beginDate;
	
	@Column(name = "END_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    private Date endDate;
	
	@Column(name = "STATUS", nullable = false)
    private boolean status;
	
	public QUESTIONING() {
	}

	public USER getUser() {
		return user;
	}

	public void setUser(USER user) {
		this.user = user;
	}

	public SUBJECT getSubject() {
		return subject;
	}

	public void setSubject(SUBJECT subject) {
		this.subject = subject;
	}

	public Date getBeginDate() {
		return beginDate;
	}

	public void setBeginDate(Date beginDate) {
		this.beginDate = beginDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public boolean isStatus() {
		return status;
	}

	public void setStatus(boolean status) {
		this.status = status;
	}
}
