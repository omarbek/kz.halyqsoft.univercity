package kz.halyqsoft.univercity.entity.beans.univercity;

import kz.halyqsoft.univercity.entity.beans.univercity.catalog.AWARD;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.QUALIFICATION;
import org.r3a.common.entity.AbstractEntity;
import org.r3a.common.entity.EFieldType;
import org.r3a.common.entity.FieldInfo;

import javax.persistence.*;
import java.util.Date;


@Entity
public class EMPLOYEE_QUALIFICATION extends AbstractEntity {

	private static final long serialVersionUID = -7690878148670305216L;

	@FieldInfo(type = EFieldType.FK_COMBO, order = 1, inView = false ,inEdit = false,inGrid = false)
    @ManyToOne
    @JoinColumns({
        @JoinColumn(name = "EMPLOYEE_ID", referencedColumnName = "ID")})
    private EMPLOYEE employee;

	@FieldInfo(type = EFieldType.FK_COMBO, order = 2)
	@ManyToOne
	@JoinColumns({
			@JoinColumn(name = "QUALIFICATION_ID", referencedColumnName = "ID")})
	private QUALIFICATION qualification;

	@FieldInfo(type = EFieldType.DATETIME, readOnlyFixed = true, inGrid = false, inEdit = false, order = 3)
	@Column(name = "created")
	@Temporal(TemporalType.TIMESTAMP)
	private Date created;

	@FieldInfo(type = EFieldType.DATETIME, required = false, readOnlyFixed = true, inEdit = false, order = 4)
	@Column(name = "updated")
	@Temporal(TemporalType.TIMESTAMP)
	private Date updated;

	public EMPLOYEE_QUALIFICATION() {
	}

	public EMPLOYEE getEmployee() {
		return employee;
	}

	public void setEmployee(EMPLOYEE employee) {
		this.employee = employee;
	}

	public QUALIFICATION getQualification() {
		return qualification;
	}

	public void setQualification(QUALIFICATION qualification) {
		this.qualification = qualification;
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
}
