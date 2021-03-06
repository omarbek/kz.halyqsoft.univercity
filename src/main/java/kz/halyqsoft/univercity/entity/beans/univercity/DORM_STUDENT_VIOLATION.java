package kz.halyqsoft.univercity.entity.beans.univercity;

import kz.halyqsoft.univercity.entity.beans.univercity.catalog.DORM_RULE_VIOLATION_TYPE;
import org.r3a.common.entity.AbstractEntity;

import javax.persistence.*;
import java.util.Date;

/**
 * @author Omarbek
 * @created Apr 12, 2018 10:22:36 AM
 */
@Entity
public class DORM_STUDENT_VIOLATION extends AbstractEntity {

	private static final long serialVersionUID = -5564198435066269152L;

	@ManyToOne
	@JoinColumns({@JoinColumn(name = "DORM_STUDENT_ID", referencedColumnName = "ID", nullable = false)})
	private DORM_STUDENT dormStudent;

	@ManyToOne
	@JoinColumns({@JoinColumn(name = "VIOLATION_TYPE_ID", referencedColumnName = "ID", nullable = false)})
	private DORM_RULE_VIOLATION_TYPE violationType;

	@Column(name = "VIOLATION_DATE", nullable = false)
	@Temporal(TemporalType.TIMESTAMP)
	private Date violationDate;

	@Column(name = "DESCR")
	private String descr;

	@Column(name = "CREATED", nullable = false)
	@Temporal(TemporalType.TIMESTAMP)
	private Date created;

	@Column(name = "EVICTED", nullable = false)
	private boolean evicted;

	public DORM_STUDENT_VIOLATION() {
	}

	public DORM_STUDENT getDormStudent() {
		return dormStudent;
	}

	public void setDormStudent(DORM_STUDENT dormStudent) {
		this.dormStudent = dormStudent;
	}

	public DORM_RULE_VIOLATION_TYPE getViolationType() {
		return violationType;
	}

	public void setViolationType(DORM_RULE_VIOLATION_TYPE violationType) {
		this.violationType = violationType;
	}

	public Date getViolationDate() {
		return violationDate;
	}

	public void setViolationDate(Date violationDate) {
		this.violationDate = violationDate;
	}

	public String getDescr() {
		return descr;
	}

	public void setDescr(String descr) {
		this.descr = descr;
	}

	public Date getCreated() {
		return created;
	}

	public void setCreated(Date created) {
		this.created = created;
	}

	public boolean isEvicted() {
		return evicted;
	}

	public void setEvicted(boolean evicted) {
		this.evicted = evicted;
	}

}
