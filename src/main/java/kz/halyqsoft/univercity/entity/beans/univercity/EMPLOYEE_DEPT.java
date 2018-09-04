package kz.halyqsoft.univercity.entity.beans.univercity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.DEPARTMENT;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.EMPLOYEE_TYPE;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.POST;
import org.r3a.common.entity.AbstractEntity;
import org.r3a.common.entity.EFieldType;
import org.r3a.common.entity.FieldInfo;

import javax.persistence.*;
import java.util.Date;

/**
 * @author Omarbek
 * @created Dec 21, 2015 9:45:42 AM
 */
@Entity
public class EMPLOYEE_DEPT extends AbstractEntity {

	private static final long serialVersionUID = -7690878148670305216L;

	@FieldInfo(type = EFieldType.FK_COMBO, order = 1)
    @ManyToOne
    @JoinColumns({
        @JoinColumn(name = "EMPLOYEE_TYPE_ID", referencedColumnName = "ID")})
    private EMPLOYEE_TYPE employeeType;
	
    @ManyToOne
    @JoinColumns({
        @JoinColumn(name = "EMPLOYEE_ID", referencedColumnName = "ID")})
    private EMPLOYEE employee;
	
	@FieldInfo(type = EFieldType.FK_COMBO, order = 3)
    @ManyToOne
    @JoinColumns({
        @JoinColumn(name = "DEPT_ID", referencedColumnName = "ID")})
    private DEPARTMENT department;
	
	@FieldInfo(type = EFieldType.FK_COMBO, order = 4)
    @ManyToOne
    @JoinColumns({
        @JoinColumn(name = "POST_ID", referencedColumnName = "ID")})
    private POST post;

	
	@FieldInfo(type = EFieldType.INTEGER, order = 5)
	@Column(name = "LIVE_LOAD", nullable = false)
    private Integer liveLoad;//Норма нагрузки
	
	@FieldInfo(type = EFieldType.DOUBLE, order = 6)
	@Column(name = "WAGE_RATE", nullable = false)
    private Double wageRate;//Ставка 1 || 0.5
	
	@FieldInfo(type = EFieldType.DOUBLE, order = 7, readOnlyFixed = true)
	@Column(name = "RATE_LOAD", nullable = false)
    private Double rateLoad;//LIVE_LOAD / WAGE_RATE
	
	@FieldInfo(type = EFieldType.DOUBLE, order = 8)
	@Column(name = "HOUR_COUNT", nullable = false)
    private Double hourCount;
	
	@FieldInfo(type = EFieldType.DATE, order = 9)
	@Column(name = "HIRE_DATE")
    @Temporal(TemporalType.DATE)
    private Date hireDate;
	
	@FieldInfo(type = EFieldType.DATE, order = 10, required = false)
	@Column(name = "DISMISS_DATE")
    @Temporal(TemporalType.DATE)
    private Date dismissDate;
	
	@FieldInfo(type = EFieldType.BOOLEAN, order = 11, required = false)
	@Column(name = "ADVISER", nullable = false)
    private boolean adviser;

	@FieldInfo(type = EFieldType.BOOLEAN, order = 12, required = false)
	@Column(name = "LECTURER", nullable = false)
	private boolean lecturer;

	@FieldInfo(type = EFieldType.FK_COMBO, order = 13, required = false, inEdit = false, inGrid = false, inView = false)
    @ManyToOne
    @JoinColumns({
        @JoinColumn(name = "PARENT_ID", referencedColumnName = "ID")})
    private EMPLOYEE_DEPT parent;
	
	@FieldInfo(type = EFieldType.TEXT, max = 1000, order = 14, required = false, inGrid = false)
	@Column(name = "DESCR", nullable = false)
	private String descr;

	@FieldInfo(type = EFieldType.BOOLEAN, max = 1000, order = 15,required = false)
	@Column(name = "PRIORITY", nullable = false)
	private boolean priority;


	public EMPLOYEE_DEPT() {
	}

	public EMPLOYEE_TYPE getEmployeeType() {
		return employeeType;
	}

	public void setEmployeeType(EMPLOYEE_TYPE employeeType) {
		this.employeeType = employeeType;
	}

	public EMPLOYEE getEmployee() {
		return employee;
	}

	public void setEmployee(EMPLOYEE employee) {
		this.employee = employee;
	}

	public DEPARTMENT getDepartment() {
		return department;
	}

	public void setDepartment(DEPARTMENT department) {
		this.department = department;
	}

	public POST getPost() {
		return post;
	}

	public void setPost(POST post) {
		this.post = post;
	}

	public Integer getLiveLoad() {
		return liveLoad;
	}

	public void setLiveLoad(Integer liveLoad) {
		this.liveLoad = liveLoad;
	}
	
	public Double getWageRate() {
		return wageRate;
	}

	public void setWageRate(Double wageRate) {
		this.wageRate = wageRate;
	}

	public Double getRateLoad() {
		return rateLoad;
	}

	public void setRateLoad(Double rateLoad) {
		this.rateLoad = rateLoad;
	}
	
	public Double getHourCount() {
		return hourCount;
	}

	public void setHourCount(Double hourCount) {
		this.hourCount = hourCount;
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

	public boolean isAdviser() {
		return adviser;
	}

	public void setAdviser(boolean adviser) {
		this.adviser = adviser;
	}

	public EMPLOYEE_DEPT getParent() {
		return parent;
	}

	public void setParent(EMPLOYEE_DEPT parent) {
		this.parent = parent;
	}

	public String getDescr() {
		return descr;
	}

	public void setDescr(String descr) {
		this.descr = descr;
	}

	public boolean isLecturer() {
		return lecturer;
	}

	public void setLecturer(boolean lecturer) {
		this.lecturer = lecturer;
	}

	public static long getSerialVersionUID() {
		return serialVersionUID;
	}

	public boolean isPriority() {
		return priority;
	}

	public void setPriority(boolean priority) {
		this.priority = priority;
	}
}
