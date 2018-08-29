package kz.halyqsoft.univercity.entity.beans.univercity;

import org.r3a.common.entity.AbstractEntity;
import org.r3a.common.entity.EFieldType;
import org.r3a.common.entity.FieldInfo;

import javax.persistence.*;
import java.util.Date;

/**
 * @author Omarbek
 * @created Dec 22, 2015 3:33:08 PM
 */
@Entity
public class PREVIOUS_EXPERIENCE extends AbstractEntity {
	
	private static final long serialVersionUID = 2876044651499481973L;

	@FieldInfo(type = EFieldType.FK_COMBO, order = 1, inEdit = false, inGrid = false, inView = false)
    @ManyToOne
    @JoinColumns({
        @JoinColumn(name = "EMPLOYEE_ID", referencedColumnName = "ID")})
    private EMPLOYEE employee;
	
	@FieldInfo(type = EFieldType.TEXT, max = 64, order = 2)
	@Column(name = "ORGANIZATION_NAME", nullable = false)
	private String organizationName;
	
	@FieldInfo(type = EFieldType.TEXT, max = 64, order = 3, columnWidth = 220)
	@Column(name = "POST_NAME", nullable = false)
	private String postName;
	
	@FieldInfo(type = EFieldType.DATE, order = 4, columnWidth = 100)
	@Column(name = "HIRE_DATE")
    @Temporal(TemporalType.DATE)
    private Date hireDate;
	
	@FieldInfo(type = EFieldType.DATE, order = 5, columnWidth = 130, required = false)
	@Column(name = "DISMISS_DATE")
    @Temporal(TemporalType.DATE)
    private Date dismissDate;
	
	@FieldInfo(type = EFieldType.TEXT, isMemo = true, order = 6, inGrid = false, required = false)
	@Column(name = "DUTY", nullable = false)
	@Lob
    @Basic(fetch = FetchType.EAGER)
	private String duty;

	public PREVIOUS_EXPERIENCE() {
	}

	public EMPLOYEE getEmployee() {
		return employee;
	}

	public void setEmployee(EMPLOYEE employee) {
		this.employee = employee;
	}

	public String getOrganizationName() {
		return organizationName;
	}

	public void setOrganizationName(String organizationName) {
		this.organizationName = organizationName;
	}

	public String getPostName() {
		return postName;
	}

	public void setPostName(String postName) {
		this.postName = postName;
	}

	public Date getHireDate() {
		return hireDate;
	}

	public void setHireDate(Date hireDate) {
		this.hireDate = hireDate;
	}

	public Date getDismissDate() {
		return dismissDate;
	}

	public void setDismissDate(Date dismissDate) {
		this.dismissDate = dismissDate;
	}

	public String getDuty() {
		return duty;
	}

	public void setDuty(String duty) {
		this.duty = duty;
	}
}
