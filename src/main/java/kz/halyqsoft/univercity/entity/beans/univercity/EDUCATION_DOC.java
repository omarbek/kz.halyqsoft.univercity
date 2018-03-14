package kz.halyqsoft.univercity.entity.beans.univercity;

import kz.halyqsoft.univercity.entity.beans.univercity.catalog.COUNTRY;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.EDUCATION_DOC_TYPE;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.EDUCATION_TYPE;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.LANGUAGE;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.SCHOOL_CERTIFICATE_TYPE;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.SCHOOL_TYPE;
import org.r3a.common.entity.EFieldType;
import org.r3a.common.entity.FieldInfo;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;

/**
 * @@author Omarbek
 * @created Nov 13, 2015 3:10:44 PM
 */
@Entity
@DiscriminatorValue(value = "3")
public class EDUCATION_DOC extends USER_DOCUMENT {

	private static final long serialVersionUID = 783542890067358199L;

	@FieldInfo(type = EFieldType.FK_COMBO, order = 5, inGrid = false)
    @ManyToOne
    @JoinColumns({
        @JoinColumn(name = "EDUCATION_TYPE_ID", referencedColumnName = "ID")})
    private EDUCATION_TYPE educationType;
	
	@FieldInfo(type = EFieldType.FK_COMBO, order = 6, inGrid = false)
    @ManyToOne
    @JoinColumns({
        @JoinColumn(name = "EDUCATION_DOC_TYPE_ID", referencedColumnName = "ID")})
    private EDUCATION_DOC_TYPE educationDocType;
	
	@FieldInfo(type = EFieldType.FK_COMBO, order = 7, inGrid = false)
    @ManyToOne
    @JoinColumns({
        @JoinColumn(name = "SCHOOL_TYPE_ID", referencedColumnName = "ID")})
    private SCHOOL_TYPE schoolType;
	
	@FieldInfo(type = EFieldType.TEXT, max = 256, order = 8)
	@Column(name = "SCHOOL_NAME")
	private String schoolName;
	
	@FieldInfo(type = EFieldType.FK_COMBO, order = 9, required = true, inGrid = false)
    @ManyToOne
    @JoinColumns({
        @JoinColumn(name = "SCHOOL_COUNTRY_ID", referencedColumnName = "ID")})
    private COUNTRY schoolCountry;
	
	@FieldInfo(type = EFieldType.FK_COMBO, order = 10, required = false, inGrid = false)
    @ManyToOne
    @JoinColumns({
        @JoinColumn(name = "SCHOOL_REGION_ID", referencedColumnName = "ID")})
    private COUNTRY schoolRegion;
	
	@FieldInfo(type = EFieldType.TEXT, max = 256, order = 11, required = false, inGrid = false)
	@Column(name = "SCHOOL_ADDRESS")
	private String schoolAddress;
	
	@FieldInfo(type = EFieldType.FK_COMBO, order = 12, required = true, inGrid = false)
    @ManyToOne
    @JoinColumns({
        @JoinColumn(name = "LANGUAGE_ID", referencedColumnName = "ID")})
    private LANGUAGE language;
	
	@FieldInfo(type = EFieldType.FK_COMBO, order = 13, required = false, inGrid = false)
    @ManyToOne
    @JoinColumns({
        @JoinColumn(name = "SCHOOL_CERTIFICATE_TYPE_ID", referencedColumnName = "ID")})
    private SCHOOL_CERTIFICATE_TYPE schoolCertificateType;
	
	@FieldInfo(type = EFieldType.BOOLEAN, order = 14, required = false, inGrid = false)
	@Column(name = "HIGH_GRADUATED", nullable = true)
    private boolean highGraduated;
	
	@FieldInfo(type = EFieldType.BOOLEAN, order = 15, required = false, inGrid = false)
	@Column(name = "GOLD_MARK", nullable = true)
    private boolean goldMark;
	
	@FieldInfo(type = EFieldType.DOUBLE, order = 16, inGrid = false)
	@Column(name = "GPA")
    private Double gpa;
	
	@FieldInfo(type = EFieldType.INTEGER, order = 17, columnWidth = 130, required = false, inGrid = false, inEdit = false, inView = false)
	@Column(name = "ENTRY_YEAR")
    private Integer entryYear;
	
	@FieldInfo(type = EFieldType.INTEGER, order = 18, columnWidth = 130, required = false, inGrid = false)
	@Column(name = "END_YEAR")
    private Integer endYear;
	
	@FieldInfo(type = EFieldType.TEXT, max = 64, order = 19, required = false, inGrid = false)
	@Column(name = "FACULTY_NAME", nullable = true)
	private String facultyName;
	
	@FieldInfo(type = EFieldType.TEXT, max = 64, order = 20, required = false, inGrid = false)
	@Column(name = "SPECIALITY_NAME", nullable = true)
	private String specialityName;

	public EDUCATION_DOC() {
	}

	public EDUCATION_TYPE getEducationType() {
		return educationType;
	}

	public void setEducationType(EDUCATION_TYPE educationType) {
		this.educationType = educationType;
	}

	public EDUCATION_DOC_TYPE getEducationDocType() {
		return educationDocType;
	}

	public void setEducationDocType(EDUCATION_DOC_TYPE educationDocType) {
		this.educationDocType = educationDocType;
	}

	public SCHOOL_TYPE getSchoolType() {
		return schoolType;
	}

	public void setSchoolType(SCHOOL_TYPE schoolType) {
		this.schoolType = schoolType;
	}

	public String getSchoolName() {
		return schoolName;
	}

	public void setSchoolName(String schoolName) {
		this.schoolName = schoolName;
	}

	public COUNTRY getSchoolCountry() {
		return schoolCountry;
	}

	public void setSchoolCountry(COUNTRY schoolCountry) {
		this.schoolCountry = schoolCountry;
	}

	public COUNTRY getSchoolRegion() {
		return schoolRegion;
	}

	public void setSchoolRegion(COUNTRY schoolRegion) {
		this.schoolRegion = schoolRegion;
	}

	public String getSchoolAddress() {
		return schoolAddress;
	}

	public void setSchoolAddress(String schoolAddress) {
		this.schoolAddress = schoolAddress;
	}

	public LANGUAGE getLanguage() {
		return language;
	}

	public void setLanguage(LANGUAGE language) {
		this.language = language;
	}

	public SCHOOL_CERTIFICATE_TYPE getSchoolCertificateType() {
		return schoolCertificateType;
	}

	public void setSchoolCertificateType(SCHOOL_CERTIFICATE_TYPE schoolCertificateType) {
		this.schoolCertificateType = schoolCertificateType;
	}

	public boolean isHighGraduated() {
		return highGraduated;
	}

	public void setHighGraduated(boolean highGraduated) {
		this.highGraduated = highGraduated;
	}
	
	public boolean isGoldMark() {
		return goldMark;
	}

	public void setGoldMark(boolean goldMark) {
		this.goldMark = goldMark;
	}

	public Double getGpa() {
		return gpa;
	}

	public void setGpa(Double gpa) {
		this.gpa = gpa;
	}

	public Integer getEntryYear() {
		return entryYear;
	}

	public void setEntryYear(Integer entryYear) {
		this.entryYear = entryYear;
	}

	public Integer getEndYear() {
		return endYear;
	}

	public void setEndYear(Integer endYear) {
		this.endYear = endYear;
	}

	public String getFacultyName() {
		return facultyName;
	}

	public void setFacultyName(String facultyName) {
		this.facultyName = facultyName;
	}

	public String getSpecialityName() {
		return specialityName;
	}

	public void setSpecialityName(String specialityName) {
		this.specialityName = specialityName;
	}
}
