package kz.halyqsoft.univercity.entity.beans.univercity;

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
 * @author Omarbek
 * @created 04 ���� 2016 �. 15:11:29
 */
@Entity
public class GRADUATION_PROJECT extends AbstractEntity {

	private static final long serialVersionUID = -8295572563460334192L;
	
	@FieldInfo(type = EFieldType.FK_COMBO, order = 1, inGrid = false, inEdit = false, inView = false)
    @ManyToOne
    @JoinColumns({
        @JoinColumn(name = "STUDENT_ID", referencedColumnName = "ID")})
    private STUDENT student;
	
	@FieldInfo(type = EFieldType.TEXT, order = 2, inEdit = true, inView = true, inGrid = true)
	@Column(name = "NAME_DIPLOMA")
	private String nameDiplomaRu;
	
	@FieldInfo(type = EFieldType.TEXT, order = 3, inEdit = true, inView = true, inGrid = true)
	@Column(name = "NAME_DIPLOMA_KZ")
	private String nameDiplomaKz;
	
	@FieldInfo(type = EFieldType.TEXT, order = 4, inEdit = true, inView = true, inGrid = true)
	@Column(name = "NAME_DIPLOMA__EN")
	private String nameDiplomaEn;
	
	@FieldInfo(type = EFieldType.TEXT, order = 5, inEdit = true, inView = true, inGrid = true)
	@Column(name = "DIPLOMA_NUMBER")
	private String diplomaNumber;
	
	@FieldInfo(type = EFieldType.DATE, order = 6, inEdit = true, inView = true, inGrid = true)
	@Column(name = "DIPLOMA_DATE")
    @Temporal(TemporalType.DATE)
    private Date diplomaDate;
	
	@FieldInfo(type = EFieldType.TEXT, order = 7, inEdit = true, inView = true, inGrid = true)
	@Column(name = "ACADEMIC_DEGREE_KZ")
	private String academicDegreeRu;
	
	@FieldInfo(type = EFieldType.TEXT, order = 8, inEdit = true, inView = true, inGrid = true)
	@Column(name = "ACADEMIC_DEGREE_EN")
	private String academicDegreeKz;
	
	@FieldInfo(type = EFieldType.TEXT, order = 9, inEdit = true, inView = true, inGrid = true)
	@Column(name = "ACADEMIC_DEGREE_RU")
	private String academicDegreeEn;
	
	@FieldInfo(type = EFieldType.TEXT, order = 10, inEdit = true, inView = true, inGrid = true)
	@Column(name = "EDUCATIONAL_PROGRAM")
	private String educationalProgram;
	
	@FieldInfo(type = EFieldType.TEXT, order = 11, inEdit = true, inView = true, inGrid = true)
	@Column(name = "QUALIFICATION")
	private String qualification;
	
	@FieldInfo(type = EFieldType.TEXT, order = 12, inEdit = true, inView = true, inGrid = true)
	@Column(name = "PROTOCOL_NUMBER")
	private String protocolNumber;
	
	@FieldInfo(type = EFieldType.DATE, order = 13, inEdit = true, inView = true, inGrid = true)
	@Column(name = "PROTOCOL_DATE")
    @Temporal(TemporalType.DATE)
    private Date protocolDate;
	
	@FieldInfo(type = EFieldType.TEXT, order = 14, inEdit = true, inView = true, inGrid = true)
	@Column(name = "REG_NUMBER")
	private String regNumber;
	
	@FieldInfo(type = EFieldType.TEXT, order = 15, inEdit = true, inView = true, inGrid = true)
	@Column(name = "FIRST_NAME_EN")
	private String firstNameEn;
	
	@FieldInfo(type = EFieldType.TEXT, order = 16, inEdit = true, inView = true, inGrid = true)
	@Column(name = "SECOND_NAME_EN")
	private String secondNameEn;
	
	@FieldInfo(type = EFieldType.TEXT, order = 17, required = false, inEdit = true, inView = true, inGrid = true)
	@Column(name = "MIDDLE_NAME_EN")
	private String middleNameEn;

	public STUDENT getStudent() {
		return student;
	}

	public void setStudent(STUDENT student) {
		this.student = student;
	}

	public String getNameDiplomaRu() {
		return nameDiplomaRu;
	}

	public void setNameDiplomaRu(String nameDiplomaRu) {
		this.nameDiplomaRu = nameDiplomaRu;
	}

	public String getNameDiplomaKz() {
		return nameDiplomaKz;
	}

	public void setNameDiplomaKz(String nameDiplomaKz) {
		this.nameDiplomaKz = nameDiplomaKz;
	}

	public String getNameDiplomaEn() {
		return nameDiplomaEn;
	}

	public void setNameDiplomaEn(String nameDiplomaEn) {
		this.nameDiplomaEn = nameDiplomaEn;
	}

	public String getDiplomaNumber() {
		return diplomaNumber;
	}

	public void setDiplomaNumber(String diplomaNumber) {
		this.diplomaNumber = diplomaNumber;
	}

	public Date getDiplomaDate() {
		return diplomaDate;
	}

	public void setDiplomaDate(Date diplomaDate) {
		this.diplomaDate = diplomaDate;
	}

	public String getAcademicDegreeRu() {
		return academicDegreeRu;
	}

	public void setAcademicDegreeRu(String academicDegreeRu) {
		this.academicDegreeRu = academicDegreeRu;
	}

	public String getAcademicDegreeKz() {
		return academicDegreeKz;
	}

	public void setAcademicDegreeKz(String academicDegreeKz) {
		this.academicDegreeKz = academicDegreeKz;
	}

	public String getAcademicDegreeEn() {
		return academicDegreeEn;
	}

	public void setAcademicDegreeEn(String academicDegreeEn) {
		this.academicDegreeEn = academicDegreeEn;
	}

	public String getEducationalProgram() {
		return educationalProgram;
	}

	public void setEducationalProgram(String educationalProgram) {
		this.educationalProgram = educationalProgram;
	}

	public String getQualification() {
		return qualification;
	}

	public void setQualification(String qualification) {
		this.qualification = qualification;
	}

	public String getProtocolNumber() {
		return protocolNumber;
	}

	public void setProtocolNumber(String protocolNumber) {
		this.protocolNumber = protocolNumber;
	}

	public Date getProtocolDate() {
		return protocolDate;
	}

	public void setProtocolDate(Date protocolDate) {
		this.protocolDate = protocolDate;
	}

	public String getRegNumber() {
		return regNumber;
	}

	public void setRegNumber(String regNumber) {
		this.regNumber = regNumber;
	}

	public String getFirstNameEn() {
		return firstNameEn;
	}

	public void setFirstNameEn(String firstNameEn) {
		this.firstNameEn = firstNameEn;
	}

	public String getSecondNameEn() {
		return secondNameEn;
	}

	public void setSecondNameEn(String secondNameEn) {
		this.secondNameEn = secondNameEn;
	}

	public String getMiddleNameEn() {
		return middleNameEn;
	}

	public void setMiddleNameEn(String middleNameEn) {
		this.middleNameEn = middleNameEn;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	
}
