package kz.halyqsoft.univercity.entity.beans.univercity;

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
 * @author Rakymzhan A. Kenzhegul
 * @created Apr 3, 2017 3:03:25 PM
 */
@Entity
public class STUDENT_FIN_DEBT extends AbstractEntity {

	private static final long serialVersionUID = -2348574322994827920L;

	@ManyToOne
	@JoinColumns({ @JoinColumn(name = "STUDENT_ID", referencedColumnName = "ID", nullable = false) })
	private STUDENT student;

	@FieldInfo(type = EFieldType.DATETIME, order = 2)
	@Column(name = "REPORT_DATE", nullable = false)
	@Temporal(TemporalType.TIMESTAMP)
	private Date reportDate;

	@FieldInfo(type = EFieldType.DOUBLE, order = 3)
	@Column(name = "DEBT_SUM", nullable = false)
	private Double debtSum;

	@FieldInfo(type = EFieldType.BOOLEAN, order = 4)
	@Column(name = "RETAKE", nullable = false)
	private boolean retake;

	@Column(name = "CREATED", nullable = false)
	@Temporal(TemporalType.TIMESTAMP)
	private Date created;

	@Column(name = "DELETED", nullable = false)
	private boolean deleted;

	public STUDENT_FIN_DEBT() {
	}

	public STUDENT getStudent() {
		return student;
	}

	public void setStudent(STUDENT student) {
		this.student = student;
	}

	public Date getReportDate() {
		return reportDate;
	}

	public void setReportDate(Date reportDate) {
		this.reportDate = reportDate;
	}

	public Double getDebtSum() {
		return debtSum;
	}

	public void setDebtSum(Double debtSum) {
		this.debtSum = debtSum;
	}

	public boolean isRetake() {
		return retake;
	}

	public void setRetake(boolean retake) {
		this.retake = retake;
	}

	public Date getCreated() {
		return created;
	}

	public void setCreated(Date created) {
		this.created = created;
	}

	public boolean isDeleted() {
		return deleted;
	}

	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}
}
