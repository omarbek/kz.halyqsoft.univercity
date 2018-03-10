package kz.halyqsoft.univercity.entity.beans.univercity.view;

import kz.halyqsoft.univercity.entity.beans.univercity.catalog.*;
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
 * @author Rakymzhan A. Kenzhegul
 * @created Apr 4, 2016 1:31:17 PM
 */
@Entity
public class V_STUDENT extends AbstractEntity {

	private static final long serialVersionUID = -3396080148467585560L;
	
	@FieldInfo(type = EFieldType.TEXT, max = 12, order = 1, required = false, columnWidth = 120)
	@Column(name = "USER_CODE")
	private String userCode;

	@FieldInfo(type = EFieldType.TEXT, max = 32, order = 3, required = false)
	@Column(name = "FIRST_NAME", nullable = false)
	private String firstName;
	
	@FieldInfo(type = EFieldType.TEXT, max = 32, order = 2, required = false)
	@Column(name = "LAST_NAME", nullable = false)
	private String lastName;
	
	@FieldInfo(type = EFieldType.TEXT, max = 32, order = 4, required = false, inGrid = false, inEdit = false, inView = false)
	@Column(name = "MIDDLE_NAME", nullable = true)
	private String middleName;
	
	@FieldInfo(type = EFieldType.TEXT_LATIN, max = 32, order = 6, inGrid = false, inEdit = false, inView = false)
	@Column(name = "FIRST_NAME_EN", nullable = false)
	private String firstNameEN;
	
	@FieldInfo(type = EFieldType.TEXT_LATIN, max = 32, order = 5, inGrid = false, inEdit = false, inView = false)
	@Column(name = "LAST_NAME_EN", nullable = false)
	private String lastNameEN;
	
	@FieldInfo(type = EFieldType.TEXT_LATIN, max = 32, order = 7, required = false, inGrid = false, inEdit = false, inView = false)
	@Column(name = "MIDDLE_NAME_EN", nullable = true)
	private String middleNameEN;
	
	@FieldInfo(type = EFieldType.DATE, order = 8, inGrid = false, inEdit = false, inView = false)
	@Column(name = "BIRTH_DATE")
    @Temporal(TemporalType.DATE)
    private Date birthDate;
	
	@FieldInfo(type = EFieldType.FK_COMBO, order = 9, inGrid = false, inEdit = false, inView = false)
    @ManyToOne
    @JoinColumns({
        @JoinColumn(name = "SEX_ID", referencedColumnName = "ID")})
    private SEX sex;
	
	@FieldInfo(type = EFieldType.TEXT, order = 10, inGrid = false, inEdit = false, inView = false)
	@Column(name = "SEX_NAME", nullable = false)
	private String sexName;
	
	@FieldInfo(type = EFieldType.FK_COMBO, order = 11, inGrid = false, inEdit = false, inView = false)
    @ManyToOne
    @JoinColumns({
        @JoinColumn(name = "MARITAL_STATUS_ID", referencedColumnName = "ID")})
    private MARITAL_STATUS maritalStatus;
	
	@FieldInfo(type = EFieldType.TEXT, order = 12, inGrid = false, inEdit = false, inView = false)
	@Column(name = "MARITAL_STATUS_NAME", nullable = false)
	private String maritalStatusName;
	
	@FieldInfo(type = EFieldType.FK_COMBO, order = 13, inGrid = false, inEdit = false, inView = false)
    @ManyToOne
    @JoinColumns({
        @JoinColumn(name = "NATIONALITY_ID", referencedColumnName = "ID")})
    private NATIONALITY nationality;
	
	@FieldInfo(type = EFieldType.TEXT, order = 14, inGrid = false, inEdit = false, inView = false)
	@Column(name = "NATION_NAME", nullable = false)
	private String nationalityName;
	
	@FieldInfo(type = EFieldType.FK_COMBO, order = 15, inGrid = false, inEdit = false, inView = false)
    @ManyToOne
    @JoinColumns({
        @JoinColumn(name = "CITIZENSHIP_ID", referencedColumnName = "ID")})
    private COUNTRY citizenship;
	
	@FieldInfo(type = EFieldType.TEXT, order = 16, inGrid = false, inEdit = false, inView = false)
	@Column(name = "CITIZENSHIP_NAME", nullable = false)
	private String citizenshipName;
	
	@FieldInfo(type = EFieldType.TEXT, max = 32, order = 17, required = false, readOnlyFixed = true, inGrid = false, inEdit = false, inView = false)
	@Column(name = "LOGIN")
	private String login;
	
	@FieldInfo(type = EFieldType.TEXT, max = 32, order = 18, required = false, readOnlyFixed = true, inGrid = false, inEdit = false, inView = false)
	@Column(name = "EMAIL")
	private String email;
	
	@FieldInfo(type = EFieldType.MASK, fieldMask = "(###)-###-####", order = 19, required = false, inGrid = false, inEdit = false, inView = false)
	@Column(name = "PHONE_MOBILE")
	private String phoneMobile;
	
	@FieldInfo(type = EFieldType.FK_COMBO, order = 20, inGrid = false, inEdit = false, inView = false)
    @ManyToOne
    @JoinColumns({
        @JoinColumn(name = "LEVEL_ID", referencedColumnName = "ID")})
    private LEVEL level;
	
	@FieldInfo(type = EFieldType.TEXT, order = 21, inGrid = false, inEdit = false, inView = false)
	@Column(name = "LEVEL_NAME", nullable = false)
	private String levelName;
	
	@FieldInfo(type = EFieldType.FK_COMBO, order = 22, inGrid = false, inEdit = false, inView = false)
    @ManyToOne
    @JoinColumns({
        @JoinColumn(name = "CATEGORY_ID", referencedColumnName = "ID")})
    private STUDENT_CATEGORY category;
	
	@FieldInfo(type = EFieldType.TEXT, order = 23, inGrid = false, inEdit = false, inView = false)
	@Column(name = "CATEGORY_NAME", nullable = false)
	private String categoryName;
	
	@FieldInfo(type = EFieldType.FK_COMBO, order = 24, inGrid = false, inEdit = false, inView = false, required = false)
    @ManyToOne
    @JoinColumns({
        @JoinColumn(name = "ACADEMIC_STATUS_ID", referencedColumnName = "ID")})
    private ACADEMIC_STATUS academicStatus;
	
	@FieldInfo(type = EFieldType.TEXT, order = 25, inGrid = false, inEdit = false, inView = false)
	@Column(name = "ACADEMIC_STATUS_NAME", nullable = false)
	private String academicStatusName;
	
	@FieldInfo(type = EFieldType.BOOLEAN, order = 26, required = false, inGrid = false, inEdit = false, inView = false)
	@Column(name = "NEED_DORM", nullable = false)
    private boolean needDorm;
	
	@FieldInfo(type = EFieldType.FK_COMBO, order = 27, inGrid = false, inEdit = false, inView = false)
    @ManyToOne
    @JoinColumns({
        @JoinColumn(name = "ENTRANCE_YEAR_ID", referencedColumnName = "ID")})
    private ENTRANCE_YEAR entranceYear;
	
	@FieldInfo(type = EFieldType.TEXT, order = 28, inGrid = false, inEdit = false, inView = false)
	@Column(name = "ENTRANCE_YEAR", nullable = false)
	private String entranceYearStr;
	
	@FieldInfo(type = EFieldType.INTEGER, order = 29, inGrid = false, inEdit = false, inView = false)
	@Column(name = "ENTRANCE_BEGIN_YEAR", nullable = false)
	private Integer entranceBeginYear;
	
	@FieldInfo(type = EFieldType.INTEGER, order = 30, inGrid = false, inEdit = false, inView = false)
	@Column(name = "ENTRANCE_END_YEAR", nullable = false)
	private Integer entranceEndYear;
	
	@FieldInfo(type = EFieldType.FK_COMBO, order = 31, inGrid = false, required = false)
    @ManyToOne
    @JoinColumns({
        @JoinColumn(name = "FACULTY_ID", referencedColumnName = "ID")})
    private DEPARTMENT faculty;
	
	@FieldInfo(type = EFieldType.TEXT, order = 32, inGrid = false, inEdit = false, inView = false)
	@Column(name = "FACULTY_NAME", nullable = false)
	private String facultyName;
	
	@FieldInfo(type = EFieldType.TEXT, order = 33, inEdit = false, inView = false)
	@Column(name = "FACULTY_SHORT_NAME", nullable = false)
	private String facultyShortName;
	
	@FieldInfo(type = EFieldType.TEXT, order = 34, inGrid = false, inEdit = false, inView = false)
	@Column(name = "FACULTY_CODE", nullable = false)
	private String facultyCode;
	
	@FieldInfo(type = EFieldType.FK_COMBO, order = 35, inGrid = false, inEdit = false, inView = false)
    @ManyToOne
    @JoinColumns({
        @JoinColumn(name = "CHAIR_ID", referencedColumnName = "ID")})
    private DEPARTMENT chair;
	
	@FieldInfo(type = EFieldType.TEXT, order = 36, inGrid = false, inEdit = false, inView = false)
	@Column(name = "CHAIR_NAME", nullable = false)
	private String chairName;
	
	@FieldInfo(type = EFieldType.TEXT, order = 37, inGrid = false, inEdit = false, inView = false)
	@Column(name = "CHAIR_SHORT_NAME", nullable = false)
	private String chairShortName;
	
	@FieldInfo(type = EFieldType.TEXT, order = 38, inGrid = false, inEdit = false, inView = false)
	@Column(name = "CHAIR_CODE", nullable = false)
	private String chairCode;
	
	@FieldInfo(type = EFieldType.FK_COMBO, order = 39, inGrid = false, required = false)
    @ManyToOne
    @JoinColumns({
        @JoinColumn(name = "SPECIALITY_ID", referencedColumnName = "ID")})
    private SPECIALITY speciality;
	
	@FieldInfo(type = EFieldType.TEXT, order = 40, inEdit = false, inView = false)
	@Column(name = "SPECIALITY_NAME", nullable = false)
	private String specialityName;
	
	@FieldInfo(type = EFieldType.TEXT, order = 41, inGrid = false, inEdit = false, inView = false)
	@Column(name = "SPECIALITY_CODE", nullable = false)
	private String specialityCode;
	
	@FieldInfo(type = EFieldType.FK_COMBO, order = 42, inGrid = false, required = false)
    @ManyToOne
    @JoinColumns({
        @JoinColumn(name = "STUDY_YEAR_ID", referencedColumnName = "ID")})
    private STUDY_YEAR studyYear;
	
	@FieldInfo(type = EFieldType.FK_COMBO, order = 43, inGrid = false, required = false)
    @ManyToOne
    @JoinColumns({
        @JoinColumn(name = "EDUCATION_TYPE_ID", referencedColumnName = "ID")})
    private STUDENT_EDUCATION_TYPE educationType;
	
	@FieldInfo(type = EFieldType.TEXT, order = 44, inGrid = false, inEdit = false, inView = false)
	@Column(name = "EDUCATION_TYPE_NAME", nullable = false)
	private String educationTypeName;
	
	@FieldInfo(type = EFieldType.DATE, order = 45, required = false,  inGrid = false, inEdit = false, inView = false)
	@Column(name = "ENTRY_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    private Date entryDate;
	
	@FieldInfo(type = EFieldType.DATE, order = 46, required = false, inGrid = false, inEdit = false, inView = false)
	@Column(name = "END_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    private Date endDate;
	
	@FieldInfo(type = EFieldType.FK_COMBO, order = 47, inGrid = false, required = false)
    @ManyToOne
    @JoinColumns({
        @JoinColumn(name = "STUDENT_STATUS_ID", referencedColumnName = "ID")})
    private STUDENT_STATUS studentStatus;
	
	@FieldInfo(type = EFieldType.TEXT, order = 48, inEdit = false, inView = false)
	@Column(name = "STUDENT_STATUS_NAME", nullable = false)
	private String studentStatusName;
	
	@FieldInfo(type = EFieldType.BOOLEAN, order = 49, required = false, inEdit = false, inGrid = false, inView = false)
	@Column(name = "DELETED", nullable = false)
    private boolean deleted;
	
	@FieldInfo(type = EFieldType.DATETIME, order = 50, required = false, readOnlyFixed = true, inGrid = false, inEdit = false, inView = false)
	@Column(name = "CREATED")
    @Temporal(TemporalType.TIMESTAMP)
    private Date created;
	
	@FieldInfo(type = EFieldType.DATETIME, order = 51, required = false, readOnlyFixed = true, inGrid = false, inEdit = false, inView = false)
	@Column(name = "UPDATED")
    @Temporal(TemporalType.TIMESTAMP)
    private Date updated;
	
	public V_STUDENT() {
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

	public String getUserCode() {
		return userCode;
	}

	public void setUserCode(String userCode) {
		this.userCode = userCode;
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

	public LEVEL getLevel() {
		return level;
	}

	public void setLevel(LEVEL level) {
		this.level = level;
	}

	public String getLevelName() {
		return levelName;
	}

	public void setLevelName(String levelName) {
		this.levelName = levelName;
	}

	public STUDENT_CATEGORY getCategory() {
		return category;
	}

	public void setCategory(STUDENT_CATEGORY category) {
		this.category = category;
	}

	public String getCategoryName() {
		return categoryName;
	}

	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
	}

	public ACADEMIC_STATUS getAcademicStatus() {
		return academicStatus;
	}

	public void setAcademicStatus(ACADEMIC_STATUS academicStatus) {
		this.academicStatus = academicStatus;
	}

	public String getAcademicStatusName() {
		return academicStatusName;
	}

	public void setAcademicStatusName(String academicStatusName) {
		this.academicStatusName = academicStatusName;
	}

	public boolean isNeedDorm() {
		return needDorm;
	}

	public void setNeedDorm(boolean needDorm) {
		this.needDorm = needDorm;
	}

	public ENTRANCE_YEAR getEntranceYear() {
		return entranceYear;
	}

	public void setEntranceYear(ENTRANCE_YEAR entranceYear) {
		this.entranceYear = entranceYear;
	}

	public String getEntranceYearStr() {
		return entranceYearStr;
	}

	public void setEntranceYearStr(String entranceYearStr) {
		this.entranceYearStr = entranceYearStr;
	}

	public Integer getEntranceBeginYear() {
		return entranceBeginYear;
	}

	public void setEntranceBeginYear(Integer entranceBeginYear) {
		this.entranceBeginYear = entranceBeginYear;
	}

	public Integer getEntranceEndYear() {
		return entranceEndYear;
	}

	public void setEntranceEndYear(Integer entranceEndYear) {
		this.entranceEndYear = entranceEndYear;
	}

	public DEPARTMENT getFaculty() {
		return faculty;
	}

	public void setFaculty(DEPARTMENT faculty) {
		this.faculty = faculty;
	}

	public String getFacultyName() {
		return facultyName;
	}

	public void setFacultyName(String facultyName) {
		this.facultyName = facultyName;
	}

	public String getFacultyShortName() {
		return facultyShortName;
	}

	public void setFacultyShortName(String facultyShortName) {
		this.facultyShortName = facultyShortName;
	}

	public String getFacultyCode() {
		return facultyCode;
	}

	public void setFacultyCode(String facultyCode) {
		this.facultyCode = facultyCode;
	}

	public DEPARTMENT getChair() {
		return chair;
	}

	public void setChair(DEPARTMENT chair) {
		this.chair = chair;
	}

	public String getChairName() {
		return chairName;
	}

	public void setChairName(String chairName) {
		this.chairName = chairName;
	}

	public String getChairShortName() {
		return chairShortName;
	}

	public void setChairShortName(String chairShortName) {
		this.chairShortName = chairShortName;
	}

	public String getChairCode() {
		return chairCode;
	}

	public void setChairCode(String chairCode) {
		this.chairCode = chairCode;
	}

	public SPECIALITY getSpeciality() {
		return speciality;
	}

	public void setSpeciality(SPECIALITY speciality) {
		this.speciality = speciality;
	}

	public String getSpecialityName() {
		return specialityName;
	}

	public void setSpecialityName(String specialityName) {
		this.specialityName = specialityName;
	}

	public String getSpecialityCode() {
		return specialityCode;
	}

	public void setSpecialityCode(String specialityCode) {
		this.specialityCode = specialityCode;
	}
	
	public STUDY_YEAR getStudyYear() {
		return studyYear;
	}

	public void setStudyYear(STUDY_YEAR studyYear) {
		this.studyYear = studyYear;
	}

	public STUDENT_EDUCATION_TYPE getEducationType() {
		return educationType;
	}

	public void setEducationType(STUDENT_EDUCATION_TYPE educationType) {
		this.educationType = educationType;
	}

	public String getEducationTypeName() {
		return educationTypeName;
	}

	public void setEducationTypeName(String educationTypeName) {
		this.educationTypeName = educationTypeName;
	}

	public Date getEntryDate() {
		return entryDate;
	}

	public void setEntryDate(Date entryDate) {
		this.entryDate = entryDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public STUDENT_STATUS getStudentStatus() {
		return studentStatus;
	}

	public void setStudentStatus(STUDENT_STATUS studentStatus) {
		this.studentStatus = studentStatus;
	}

	public String getStudentStatusName() {
		return studentStatusName;
	}

	public void setStudentStatusName(String studentStatusName) {
		this.studentStatusName = studentStatusName;
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
}
