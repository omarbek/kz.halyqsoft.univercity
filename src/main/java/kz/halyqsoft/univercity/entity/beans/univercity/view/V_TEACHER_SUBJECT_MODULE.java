package kz.halyqsoft.univercity.entity.beans.univercity.view;

import kz.halyqsoft.univercity.entity.beans.univercity.EMPLOYEE;
import org.r3a.common.entity.AbstractEntity;
import org.r3a.common.entity.EFieldType;
import org.r3a.common.entity.FieldInfo;

import javax.persistence.*;

@Entity
public class V_TEACHER_SUBJECT_MODULE extends AbstractEntity {

    private static final long serialVersionUID = 3570162232421096031L;

	@ManyToOne
    @JoinColumns({
        @JoinColumn(name = "EMPLOYEE_ID", referencedColumnName = "ID")})
    private EMPLOYEE employee;

	@FieldInfo(type = EFieldType.TEXT, max = 64, order = 3, inEdit = false, inGrid = true)
	@Column(name = "SUBJECT_NAME", nullable = false)
	private String subjectName;

	@FieldInfo(type = EFieldType.TEXT, max = 64, order = 4, inEdit = false, inGrid = true)
	@Column(name = "CREDIT", nullable = false)
	private String credit;

	@FieldInfo(type = EFieldType.TEXT, max = 64, order = 5, inEdit = false, inGrid = true)
	@Column(name = "MODULE_NAME", nullable = false)
	private String moduleName;

	@FieldInfo(type = EFieldType.BOOLEAN, order = 11, columnWidth = 100)
	@Column(name = "FALL", nullable = false)
    private boolean fall;

	@FieldInfo(type = EFieldType.BOOLEAN, order = 12, columnWidth = 112)
	@Column(name = "SPRING", nullable = false)
    private boolean spring;

	@FieldInfo(type = EFieldType.BOOLEAN, order = 13, columnWidth = 100)
	@Column(name = "SUMMER", nullable = false)
    private boolean summer;

	@Column(name = "LOAD_PER_HOURS", nullable = false)
    private boolean loadPerHours;

	public V_TEACHER_SUBJECT_MODULE() {
	}

	public EMPLOYEE getEmployee() {
		return employee;
	}

	public void setEmployee(EMPLOYEE employee) {
		this.employee = employee;
	}

	public String getSubjectName() {
		return subjectName;
	}

	public void setSubjectName(String subjectName) {
		this.subjectName = subjectName;
	}

	public static long getSerialVersionUID() {
		return serialVersionUID;
	}

	public String getCredit() {
		return credit;
	}

	public void setCredit(String credit) {
		this.credit = credit;
	}

	public String getModuleName() {
		return moduleName;
	}

	public void setModuleName(String moduleName) {
		this.moduleName = moduleName;
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
}
