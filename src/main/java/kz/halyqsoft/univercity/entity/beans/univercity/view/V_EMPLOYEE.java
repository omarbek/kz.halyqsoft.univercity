package kz.halyqsoft.univercity.entity.beans.univercity.view;

import kz.halyqsoft.univercity.entity.beans.univercity.catalog.COUNTRY;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.DEPARTMENT;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.EMPLOYEE_STATUS;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.EMPLOYEE_TYPE;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.MARITAL_STATUS;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.NATIONALITY;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.POST;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.SEX;
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
 * @created Dec 30, 2015 5:07:51 PM
 */
@Entity
public class V_EMPLOYEE extends AbstractEntity {

	private static final long serialVersionUID = 4184439753927689601L;

	@FieldInfo(type = EFieldType.TEXT, max = 9, order = 1, columnWidth = 120)
	@Column(name = "CODE")
	private String code;
	
	@FieldInfo(type = EFieldType.TEXT, max = 64, order = 3)
	@Column(name = "FIRST_NAME", nullable = false)
	private String firstName;
	
	@FieldInfo(type = EFieldType.TEXT, max = 64, order = 2)
	@Column(name = "LAST_NAME", nullable = false)
	private String lastName;
	
	@FieldInfo(type = EFieldType.TEXT, max = 64, order = 4, required = false)
	@Column(name = "MIDDLE_NAME", nullable = true)
	private String middleName;
	
	@FieldInfo(type = EFieldType.TEXT_LATIN, max = 64, order = 6, inGrid = false)
	@Column(name = "FIRST_NAME_EN", nullable = false)
	private String firstNameEN;
	
	@FieldInfo(type = EFieldType.TEXT_LATIN, max = 64, order = 5, inGrid = false)
	@Column(name = "LAST_NAME_EN", nullable = false)
	private String lastNameEN;
	
	@FieldInfo(type = EFieldType.TEXT_LATIN, max = 64, order = 7, required = false, inGrid = false)
	@Column(name = "MIDDLE_NAME_EN", nullable = true)
	private String middleNameEN;
	
	@FieldInfo(type = EFieldType.DATE, order = 8, inGrid = false)
	@Column(name = "BIRTH_DATE")
    @Temporal(TemporalType.DATE)
    private Date birthDate;
	
	@FieldInfo(type = EFieldType.FK_COMBO, order = 9, inGrid = false)
    @ManyToOne
    @JoinColumns({
        @JoinColumn(name = "SEX_ID", referencedColumnName = "ID")})
    private SEX sex;
	
	@FieldInfo(type = EFieldType.TEXT, max = 64, order = 10, inGrid = false, inEdit = false, inView = false)
	@Column(name = "SEX_NAME", nullable = false)
	private String sexName;
	
	@FieldInfo(type = EFieldType.FK_COMBO, order = 11, inGrid = false)
    @ManyToOne
    @JoinColumns({
        @JoinColumn(name = "MARITAL_STATUS_ID", referencedColumnName = "ID")})
    private MARITAL_STATUS maritalStatus;
	
	@FieldInfo(type = EFieldType.TEXT, max = 64, order = 12, inGrid = false, inEdit = false, inView = false)
	@Column(name = "MARITAL_STATUS_NAME", nullable = false)
	private String maritalStatusName;
	
	@FieldInfo(type = EFieldType.FK_COMBO, order = 13, inGrid = false)
    @ManyToOne
    @JoinColumns({
        @JoinColumn(name = "NATIONALITY_ID", referencedColumnName = "ID")})
    private NATIONALITY nationality;
	
	@FieldInfo(type = EFieldType.TEXT, max = 64, order = 14, inGrid = false, inEdit = false, inView = false)
	@Column(name = "NATION_NAME", nullable = false)
	private String nationalityName;
	
	@FieldInfo(type = EFieldType.FK_COMBO, order = 15, inGrid = false)
    @ManyToOne
    @JoinColumns({
        @JoinColumn(name = "CITIZENSHIP_ID", referencedColumnName = "ID")})
    private COUNTRY citizenship;
	
	@FieldInfo(type = EFieldType.TEXT, max = 64, order = 16, inGrid = false, inEdit = false, inView = false)
	@Column(name = "CITIZENSHIP_NAME", nullable = false)
	private String citizenshipName;
	
	@FieldInfo(type = EFieldType.TEXT, max = 32, order = 18, required = false, readOnlyFixed = true, inGrid = false, inEdit = false, inView = false)
	@Column(name = "LOGIN")
	private String login;
	
	@FieldInfo(type = EFieldType.TEXT, max = 32, order = 19, required = false, readOnlyFixed = true, inGrid = false)
	@Column(name = "EMAIL")
	private String email;
	
	@FieldInfo(type = EFieldType.MASK, fieldMask = "(###)-###-####", order = 20, required = false, inGrid = false)
	@Column(name = "PHONE_MOBILE")
	private String phoneMobile;
	
	@FieldInfo(type = EFieldType.FK_COMBO, order = 21, inGrid = false)
    @ManyToOne
    @JoinColumns({
        @JoinColumn(name = "EMPLOYEE_TYPE_ID", referencedColumnName = "ID")})
    private EMPLOYEE_TYPE employeeType;
	
	@FieldInfo(type = EFieldType.TEXT, order = 22, inEdit = false, inView = false, inGrid = false)
	@Column(name = "EMPLOYEE_TYPE_NAME", nullable = false)
	private String employeeTypeName;
	
	@FieldInfo(type = EFieldType.FK_DIALOG, order = 23, inGrid = false)
    @ManyToOne
    @JoinColumns({
        @JoinColumn(name = "DEPT_ID", referencedColumnName = "ID")})
    private DEPARTMENT department;
	
	@FieldInfo(type = EFieldType.TEXT, order = 24, inEdit = false, inView = false)
	@Column(name = "DEPT_NAME", nullable = false)
	private String deptName;
	
	@FieldInfo(type = EFieldType.TEXT, order = 25, inEdit = false, inView = false, inGrid = false)
	@Column(name = "DEPT_SHORT_NAME", nullable = false)
	private String deptShortName;
	
	@FieldInfo(type = EFieldType.TEXT, order = 26, inEdit = false, inView = false, inGrid = false)
	@Column(name = "DEPT_CODE", nullable = false)
	private String deptCode;
	
	@FieldInfo(type = EFieldType.FK_COMBO, order = 27, inGrid = false)
    @ManyToOne
    @JoinColumns({
        @JoinColumn(name = "POST_ID", referencedColumnName = "ID")})
    private POST post;
	
	@FieldInfo(type = EFieldType.TEXT, order = 28, inEdit = false, inView = false)
	@Column(name = "POST_NAME", nullable = false)
	private String postName;
	
	@FieldInfo(type = EFieldType.INTEGER, order = 29, inGrid = false)
	@Column(name = "LIVE_LOAD", nullable = false)
    private Integer liveLoad;
	
	@FieldInfo(type = EFieldType.DATE, order = 30, inGrid = false)
	@Column(name = "HIRE_DATE")
    @Temporal(TemporalType.DATE)
    private Date hireDate;
	
	@FieldInfo(type = EFieldType.DATE, order = 31, required = false, inGrid = false)
	@Column(name = "DISMISS_DATE")
    @Temporal(TemporalType.DATE)
    private Date dismissDate;
	
	@FieldInfo(type = EFieldType.BOOLEAN, order = 32, required = false, inGrid = false)
	@Column(name = "ADVISER", nullable = false)
    private boolean adviser;
	
	@FieldInfo(type = EFieldType.FK_COMBO, order = 33, inGrid = false)
    @ManyToOne
    @JoinColumns({
        @JoinColumn(name = "EMPLOYEE_STATUS_ID", referencedColumnName = "ID")})
    private EMPLOYEE_STATUS status;
	
	@FieldInfo(type = EFieldType.TEXT, max = 64, order = 34, inGrid = false, inEdit = false, inView = false)
	@Column(name = "EMPLOYEE_STATUS_NAME", nullable = false)
	private String statusName;
	
	@FieldInfo(type = EFieldType.BOOLEAN, order = 35, required = false, inEdit = false, inGrid = false, inView = false)
	@Column(name = "DELETED", nullable = false)
    private boolean deleted;
	
	@FieldInfo(type = EFieldType.DATETIME, order = 36, required = false, readOnlyFixed = true, inGrid = false, inEdit = false, inView = false)
	@Column(name = "CREATED")
    @Temporal(TemporalType.TIMESTAMP)
    private Date created;
	
	@FieldInfo(type = EFieldType.DATETIME, order = 37, required = false, readOnlyFixed = true, inGrid = false, inEdit = false, inView = false)
	@Column(name = "UPDATED")
    @Temporal(TemporalType.TIMESTAMP)
    private Date updated;
	
	public V_EMPLOYEE() {
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getMiddleName() {
		return middleName;
	}

	public void setMiddleName(String middleName) {
		this.middleName = middleName;
	}

	public String getFirstNameEN() {
		return firstNameEN;
	}

	public void setFirstNameEN(String firstNameEN) {
		this.firstNameEN = firstNameEN;
	}

	public String getLastNameEN() {
		return lastNameEN;
	}

	public void setLastNameEN(String lastNameEN) {
		this.lastNameEN = lastNameEN;
	}

	public String getMiddleNameEN() {
		return middleNameEN;
	}

	public void setMiddleNameEN(String middleNameEN) {
		this.middleNameEN = middleNameEN;
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

	public String getSexName() {
		return sexName;
	}

	public void setSexName(String sexName) {
		this.sexName = sexName;
	}

	public MARITAL_STATUS getMaritalStatus() {
		return maritalStatus;
	}

	public void setMaritalStatus(MARITAL_STATUS maritalStatus) {
		this.maritalStatus = maritalStatus;
	}

	public String getMaritalStatusName() {
		return maritalStatusName;
	}

	public void setMaritalStatusName(String maritalStatusName) {
		this.maritalStatusName = maritalStatusName;
	}

	public NATIONALITY getNationality() {
		return nationality;
	}

	public void setNationality(NATIONALITY nationality) {
		this.nationality = nationality;
	}

	public String getNationalityName() {
		return nationalityName;
	}

	public void setNationalityName(String nationalityName) {
		this.nationalityName = nationalityName;
	}

	public COUNTRY getCitizenship() {
		return citizenship;
	}

	public void setCitizenship(COUNTRY citizenship) {
		this.citizenship = citizenship;
	}

	public String getCitizenshipName() {
		return citizenshipName;
	}

	public void setCitizenshipName(String citizenshipName) {
		this.citizenshipName = citizenshipName;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getLogin() {
		return login;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPhoneMobile() {
		return phoneMobile;
	}

	public void setPhoneMobile(String phoneMobile) {
		this.phoneMobile = phoneMobile;
	}
	
	public EMPLOYEE_STATUS getStatus() {
		return status;
	}

	public void setStatus(EMPLOYEE_STATUS status) {
		this.status = status;
	}

	public String getStatusName() {
		return statusName;
	}

	public void setStatusName(String statusName) {
		this.statusName = statusName;
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

	public String getDeptCode() {
		return deptCode;
	}

	public void setDeptCode(String deptCode) {
		this.deptCode = deptCode;
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

	public boolean isDeleted() {
		return deleted;
	}

	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
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

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(lastName);
		sb.append(" ");
		sb.append(firstName);
		if (middleName != null) {
			sb.append(" ");
			sb.append(middleName);
		}
		
		return sb.toString();
	}
}
