package kz.halyqsoft.univercity.entity.beans.univercity;

import kz.halyqsoft.univercity.entity.beans.univercity.catalog.AWARD;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.SKILL;
import org.r3a.common.entity.AbstractEntity;
import org.r3a.common.entity.EFieldType;
import org.r3a.common.entity.FieldInfo;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;


@Entity
public class EMPLOYEE_AWARD extends AbstractEntity {

	private static final long serialVersionUID = -7690878148670305216L;

	@FieldInfo(type = EFieldType.FK_COMBO, order = 1, inView = false ,inEdit = false,inGrid = false)
    @ManyToOne
    @JoinColumns({
        @JoinColumn(name = "EMPLOYEE_ID", referencedColumnName = "ID")})
    private EMPLOYEE employee;

	@FieldInfo(type = EFieldType.FK_COMBO, order = 2)
	@ManyToOne
	@JoinColumns({
			@JoinColumn(name = "AWARD_ID", referencedColumnName = "ID")})
	private AWARD award;

	public EMPLOYEE getEmployee() {
		return employee;
	}

	public void setEmployee(EMPLOYEE employee) {
		this.employee = employee;
	}

	public AWARD getAward() {
		return award;
	}

	public void setAward(AWARD award) {
		this.award = award;
	}

	public EMPLOYEE_AWARD() {
	}
}
