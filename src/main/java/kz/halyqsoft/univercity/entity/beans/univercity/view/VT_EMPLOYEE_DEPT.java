package kz.halyqsoft.univercity.entity.beans.univercity.view;

import kz.halyqsoft.univercity.entity.beans.univercity.EMPLOYEE;
import kz.halyqsoft.univercity.entity.beans.univercity.EMPLOYEE_DEPT;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.DEPARTMENT;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.EMPLOYEE_TYPE;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.POST;
import org.r3a.common.entity.AbstractEntity;
import org.r3a.common.entity.EFieldType;
import org.r3a.common.entity.FieldInfo;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * @@author Omarbek
 * @created Jan 2, 2016 12:24:49 PM
 */
@Entity
public class VT_EMPLOYEE_DEPT extends AbstractEntity {

	private static final long serialVersionUID = -1646900881175952040L;

	@FieldInfo(type = EFieldType.FK_COMBO, order = 1, inEdit = false, inGrid = false, inView = false)
    @ManyToOne
    @JoinColumns({
        @JoinColumn(name = "EMPLOYEE_ID", referencedColumnName = "ID")})
    private EMPLOYEE employee;
	
	@FieldInfo(type = EFieldType.FK_COMBO, order = 2, inGrid = false)
    @ManyToOne
    @JoinColumns({
        @JoinColumn(name = "EMPLOYEE_TYPE_ID", referencedColumnName = "ID")})
    private EMPLOYEE_TYPE employeeType;
	
	@FieldInfo(type = EFieldType.TEXT, order = 3, inEdit = false, inView = false, inGrid = false)
	@Column(name = "EMPLOYEE_TYPE_NAME", nullable = false)
	private String employeeTypeName;
	
	@FieldInfo(type = EFieldType.FK_DIALOG, order = 4, inGrid = false)
    @ManyToOne
    @JoinColumns({
        @JoinColumn(name = "DEPT_ID", referencedColumnName = "ID")})
    private DEPARTMENT department;
	
	@FieldInfo(type = EFieldType.TEXT, order = 5, inEdit = false, inView = false)
	@Column(name = "DEPT_NAME", nullable = false)
	private String deptName;
	
	@FieldInfo(type = EFieldType.TEXT, order = 6, inEdit = false, inView = false, inGrid = false)
	@Column(name = "DEPT_SHORT_NAME", nullable = false)
	private String deptShortName;
	
	@FieldInfo(type = EFieldType.FK_COMBO, order = 7, inGrid = false)
    @ManyToOne
    @JoinColumns({
        @JoinColumn(name = "POST_ID", referencedColumnName = "ID")})
    private POST post;
	
	@FieldInfo(type = EFieldType.TEXT, order = 8, inEdit = false, inView = false, columnWidth = 250)
	@Column(name = "POST_NAME", nullable = false)
	private String postName;
	
	@FieldInfo(type = EFieldType.INTEGER, order = 9, columnWidth = 130)
	@Column(name = "LIVE_LOAD", nullable = false)
    private Integer liveLoad;
	
	@FieldInfo(type = EFieldType.DOUBLE, order = 10, columnWidth = 130)
	@Column(name = "WAGE_RATE", nullable = false)
    private Double wageRate;
	
	@FieldInfo(type = EFieldType.DOUBLE, order = 11, columnWidth = 130, readOnlyFixed = true)
	@Column(name = "RATE_LOAD", nullable = false)
    private Double rateLoad;
	
	@FieldInfo(type = EFieldType.DOUBLE, order = 12, columnWidth = 130)
	@Column(name = "HOUR_COUNT", nullable = false)
    private Double hourCount;
	
	@FieldInfo(type = EFieldType.DATE, order = 13, inGrid = false)
	@Column(name = "HIRE_DATE")
    @Temporal(TemporalType.DATE)
    private Date hireDate;
	
	@FieldInfo(type = EFieldType.DATE, order = 14, required = false, inGrid = false)
	@Column(name = "DISMISS_DATE")
    @Temporal(TemporalType.DATE)
    private Date dismissDate;
	
	@FieldInfo(type = EFieldType.BOOLEAN, order = 15, required = false, inGrid = false)
	@Column(name = "ADVISER", nullable = false)
    private boolean adviser;
	
	@FieldInfo(type = EFieldType.FK_COMBO, order = 16, required = false, inEdit = false, inGrid = false, inView = false)
    @ManyToOne
    @JoinColumns({
        @JoinColumn(name = "PARENT_ID", referencedColumnName = "ID")})
    private EMPLOYEE_DEPT parent;
	
	public VT_EMPLOYEE_DEPT() {
	}

	public EMPLOYEE getEmployee() {
		return employee;
	}

	public void setEmployee(EMPLOYEE employee) {
		this.employee = employee;
	}

	public EMPLOYEE_TYPE getEmployeeType() {
		return employeeType;
	}

	public void setEmployeeType(EMPLOYEE_TYPE employeeType) {
		this.employeeType = employeeType;
	}

	public String getEmployeeTypeName() {
		return employeeTypeName;
	}

	public void setEmployeeTypeName(String employeeTypeName) {
		this.employeeTypeName = employeeTypeName;
	}

	public DEPARTMENT getDepartment() {
		return department;
	}

	public void setDepartment(DEPARTMENT department) {
		this.department = department;
	}

	public String getDeptName() {
		return deptName;
	}

	public void setDeptName(String deptName) {
		this.deptName = deptName;
	}

	public String getDeptShortName() {
		return deptShortName;
	}

	public void setDeptShortName(String deptShortName) {
		this.deptShortName = deptShortName;
	}

	public POST getPost() {
		return post;
	}

	public void setPost(POST post) {
		this.post = post;
	}

	public String getPostName() {
		return postName;
	}

	public void setPostName(String postName) {
		this.postName = postName;
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
}
