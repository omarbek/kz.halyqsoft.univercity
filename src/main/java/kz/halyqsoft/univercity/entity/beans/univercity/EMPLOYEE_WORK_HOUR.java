package kz.halyqsoft.univercity.entity.beans.univercity;

import org.r3a.common.entity.EFieldType;
import org.r3a.common.entity.FieldInfo;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;

/**
 * @author Rakymzhan A. Kenzhegul
 * @created Dec 22, 2015 4:18:21 PM
 */
@Entity
public class EMPLOYEE_WORK_HOUR extends AbstractWorkHourEntity {

	private static final long serialVersionUID = -3130073886193674337L;

	@FieldInfo(type = EFieldType.FK_COMBO, order = 1, inEdit = false, inGrid = false, inView = false)
    @ManyToOne
    @JoinColumns({
        @JoinColumn(name = "EMPLOYEE_ID", referencedColumnName = "ID")})
    private EMPLOYEE employee;
	
	public EMPLOYEE_WORK_HOUR() {
	}

	public EMPLOYEE getEmployee() {
		return employee;
	}

	public void setEmployee(EMPLOYEE employee) {
		this.employee = employee;
	}

	@Override
	protected Object clone() throws CloneNotSupportedException {
		EMPLOYEE_WORK_HOUR ewh = new EMPLOYEE_WORK_HOUR();
		ewh.setId(getId());
		ewh.setEmployee(getEmployee());
		ewh.setWeekDay(getWeekDay());
		ewh.setDayHour(getDayHour());
		ewh.setWorkHourStatus(getWorkHourStatus());
		ewh.setChanged(isChanged());
		
		return ewh;
	}
}
