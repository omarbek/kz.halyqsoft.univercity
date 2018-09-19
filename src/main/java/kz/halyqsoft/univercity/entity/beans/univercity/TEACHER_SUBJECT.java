package kz.halyqsoft.univercity.entity.beans.univercity;

import kz.halyqsoft.univercity.entity.beans.univercity.view.V_SUBJECT_SELECT;
import org.r3a.common.entity.AbstractEntity;
import org.r3a.common.entity.EFieldType;
import org.r3a.common.entity.FieldInfo;

import javax.persistence.*;

/**
 * @author Omarbek
 * @created Dec 24, 2015 4:13:13 PM
 */
@Entity
public class TEACHER_SUBJECT extends AbstractEntity {

	private static final long serialVersionUID = 656623766838019754L;

    @ManyToOne
    @JoinColumns({
        @JoinColumn(name = "EMPLOYEE_ID", referencedColumnName = "ID")})
    private EMPLOYEE employee;
	
	@FieldInfo(type = EFieldType.FK_DIALOG, order = 2, inGrid = false)
    @ManyToOne
    @JoinColumns({
        @JoinColumn(name = "SUBJECT_ID", referencedColumnName = "ID")})
    private V_SUBJECT_SELECT subject;
	
	@FieldInfo(type = EFieldType.BOOLEAN, order = 9, required = false)
	@Column(name = "FALL", nullable = false)
    private boolean fall;
	
	@FieldInfo(type = EFieldType.BOOLEAN, order = 10, required = false)
	@Column(name = "SPRING", nullable = false)
    private boolean spring;
	
	@FieldInfo(type = EFieldType.BOOLEAN, order = 11, required = false)
	@Column(name = "SUMMER", nullable = false)
    private boolean summer;
	
	@Column(name = "LOAD_PER_HOURS", nullable = false)
    private boolean loadPerHours;
	
	public EMPLOYEE getEmployee() {
		return employee;
	}

	public void setEmployee(EMPLOYEE employee) {
		this.employee = employee;
	}

	public V_SUBJECT_SELECT getSubject() {
		return subject;
	}

	public void setSubject(V_SUBJECT_SELECT subject) {
		this.subject = subject;
	}

	public boolean isFall() {
		return fall;
	}

	public void setFall(boolean fall) {
		this.fall = fall;
	}

	public boolean isSpring() {
		return spring;
	}

	public void setSpring(boolean spring) {
		this.spring = spring;
	}
	
	public boolean isSummer() {
		return summer;
	}

	public void setSummer(boolean summer) {
		this.summer = summer;
	}

	public boolean isLoadPerHours() {
		return loadPerHours;
	}

	public void setLoadPerHours(boolean loadPerHours) {
		this.loadPerHours = loadPerHours;
	}

	@Override
	public String toString() {
		return employee.toString();
	}
}
