package kz.halyqsoft.univercity.entity.beans.univercity;

import kz.halyqsoft.univercity.entity.beans.univercity.catalog.SEX;
import org.r3a.common.entity.AbstractEntity;
import org.r3a.common.entity.EFieldType;
import org.r3a.common.entity.FieldInfo;

import javax.persistence.*;
import java.util.Date;


@Entity
public class CHILD extends AbstractEntity {

	@FieldInfo(type = EFieldType.FK_COMBO, inGrid = false, inEdit = false, inView = false, order = 1)
	@ManyToOne
	@JoinColumns({
			@JoinColumn(name = "EMPLOYEE_ID", referencedColumnName = "ID", nullable = false)})
	private EMPLOYEE employee;


	@FieldInfo(type = EFieldType.DATE, order = 5, inGrid = false)
	@Column(name = "BIRTH_DATE")
	@Temporal(TemporalType.DATE)
	private Date birthDate;

	@FieldInfo(type = EFieldType.FK_COMBO, order = 6)
	@ManyToOne
	@JoinColumns({
			@JoinColumn(name = "SEX_ID", referencedColumnName = "ID", nullable = false)})
	private SEX sex;

	public CHILD() {
	}


	public EMPLOYEE getEmployee() {
		return employee;
	}

	public void setEmployee(EMPLOYEE employee) {
		this.employee = employee;
	}

	public Date getBirthDate() {
		return birthDate;
	}

	public void setBirthDate(Date birthDate) {
		this.birthDate = birthDate;
	}

	public SEX getSex() {
		return sex;
	}

	public void setSex(SEX sex) {
		this.sex = sex;
	}
}
