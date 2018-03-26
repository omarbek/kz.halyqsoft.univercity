package kz.halyqsoft.univercity.entity.beans.univercity;

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
 * @author Omarbek
 * @created Apr 19, 2017 10:00:49 AM
 */
@Entity
public class REQUEST_REPORT extends AbstractEntity {

	private static final long serialVersionUID = 9010037471194167833L;

	@ManyToOne
    @JoinColumns({
        @JoinColumn(name = "EMPLOYEE_ID", referencedColumnName = "ID", nullable = false)})
    private EMPLOYEE employee;
	
	@Column(name = "REPORT_DATE", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date reportDate;
	
	@Column(name = "COMPUTER_NAME")
	private String computerName;
	
	@Column(name = "COMMENTS", nullable = true)
	private String comments;
	
	public REQUEST_REPORT() {
	}

	public EMPLOYEE getEmployee() {
		return employee;
	}

	public void setEmployee(EMPLOYEE employee) {
		this.employee = employee;
	}

	public Date getReportDate() {
		return reportDate;
	}

	public void setReportDate(Date reportDate) {
		this.reportDate = reportDate;
	}

	public String getComputerName() {
		return computerName;
	}

	public void setComputerName(String computerName) {
		this.computerName = computerName;
	}

	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}
}
