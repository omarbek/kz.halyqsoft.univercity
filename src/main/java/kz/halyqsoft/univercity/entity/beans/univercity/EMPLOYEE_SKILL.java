package kz.halyqsoft.univercity.entity.beans.univercity;

import kz.halyqsoft.univercity.entity.beans.univercity.catalog.DEPARTMENT;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.EMPLOYEE_TYPE;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.POST;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.SKILL;
import org.r3a.common.entity.AbstractEntity;
import org.r3a.common.entity.EFieldType;
import org.r3a.common.entity.FieldInfo;

import javax.persistence.*;
import java.util.Date;


@Entity
public class EMPLOYEE_SKILL extends AbstractEntity {

	private static final long serialVersionUID = -7690878148670305216L;

	@FieldInfo(type = EFieldType.FK_COMBO, order = 1, inView = false ,inEdit = false,inGrid = false)
    @ManyToOne
    @JoinColumns({
        @JoinColumn(name = "EMPLOYEE_ID", referencedColumnName = "ID")})
    private EMPLOYEE employee;

	@FieldInfo(type = EFieldType.FK_COMBO, order = 2)
	@ManyToOne
	@JoinColumns({
			@JoinColumn(name = "SKILL_ID", referencedColumnName = "ID")})
	private SKILL skill;

	public EMPLOYEE getEmployee() {
		return employee;
	}

	public void setEmployee(EMPLOYEE employee) {
		this.employee = employee;
	}

	public SKILL getSkill() {
		return skill;
	}

	public void setSkill(SKILL skill) {
		this.skill = skill;
	}

	public EMPLOYEE_SKILL() {
	}
}
