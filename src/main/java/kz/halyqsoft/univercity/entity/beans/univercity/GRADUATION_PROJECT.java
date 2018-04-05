package kz.halyqsoft.univercity.entity.beans.univercity;

import org.r3a.common.entity.AbstractEntity;
import org.r3a.common.entity.EFieldType;
import org.r3a.common.entity.FieldInfo;

import java.util.Date;

import javax.persistence.*;

/**
 * @author Omarbek
 * @created 04 ���� 2016 �. 15:11:29
 */
@Entity
public class GRADUATION_PROJECT extends AbstractEntity {

	private static final long serialVersionUID = -8295572563460334192L;

	@OneToOne
	@PrimaryKeyJoinColumn(name = "ID", referencedColumnName = "ID")
	private STUDENT student;

	@FieldInfo(type = EFieldType.TEXT, order = 1, inEdit = true, inView = true, inGrid = true)
	@Column(name = "PROJECT_THEME_KZ", nullable = false, length = 512)
	private String projectThemeKz;

	@FieldInfo(type = EFieldType.TEXT, order = 2, inEdit = true, inView = true, inGrid = true)
	@Column(name = "PROJECT_THEME_EN", nullable = false, length = 512)
	private String projectThemeEn;

	@FieldInfo(type = EFieldType.TEXT, order = 3, inEdit = true, inView = true, inGrid = true)
	@Column(name = "PROJECT_THEME_RU", nullable = false, length = 512)
	private String projectThemeRu;

	@FieldInfo(type = EFieldType.TEXT, order = 4, inEdit = true, inView = true, inGrid = true, required = false)
	@Column(name = "PROTOCOL_NO", length = 10)
	private String protocolNo;

	@FieldInfo(type = EFieldType.DATE, order = 5, inEdit = true, inView = true, inGrid = true, required = false)
	@Column(name = "PROTOCOL_DATE")
	@Temporal(TemporalType.DATE)
	private Date protocolDate;

	@FieldInfo(type = EFieldType.TEXT, order = 6, inEdit = true, inView = true, inGrid = true, required = false)
	@Column(name = "ACADEMIC_DEGREE_KZ", length = 128)
	private String academicDegreeKz;

	@FieldInfo(type = EFieldType.TEXT, order = 7, inEdit = true, inView = true, inGrid = true, required = false)
	@Column(name = "ACADEMIC_DEGREE_EN", length = 128)
	private String academicDegreeEn;

	@FieldInfo(type = EFieldType.TEXT, order = 8, inEdit = true, inView = true, inGrid = true)
	@Column(name = "ACADEMIC_DEGREE_RU", nullable = false, length = 128)
	private String academicDegreeRu;

	@FieldInfo(type = EFieldType.TEXT, order = 9, inEdit = true, inView = true, inGrid = true, required = false)
	@Column(name = "EDUCATIONAL_PROGRAM", length = 128)
	private String educationalProgram;

	@FieldInfo(type = EFieldType.TEXT, order = 10, inEdit = true, inView = true, inGrid = true, required = false)
	@Column(name = "QUALIFICATION", length = 64)
	private String qualification;

	@FieldInfo(type = EFieldType.TEXT, order = 11, inEdit = true, inView = true, inGrid = true)
	@Column(name = "DIPLOMA_NO", nullable = false, length = 10)
	private String diplomaNo;

	@FieldInfo(type = EFieldType.DATE, order = 12, inEdit = true, inView = true, inGrid = true)
	@Column(name = "DIPLOMA_DATE", nullable = false)
	@Temporal(TemporalType.DATE)
	private Date diplomaDate;

	@FieldInfo(type = EFieldType.TEXT, order = 13, inEdit = true, inView = true, inGrid = true)
	@Column(name = "REGISTRATION_NO", nullable = false, length = 10)
	private String registrationNo;

	public STUDENT getStudent() {
		return student;
	}

	public void setStudent(STUDENT student) {
		this.student = student;
	}

	public String getProjectThemeKz() {
		return projectThemeKz;
	}

	public void setProjectThemeKz(String projectThemeKz) {
		this.projectThemeKz = projectThemeKz;
	}

	public String getProjectThemeEn() {
		return projectThemeEn;
	}

	public void setProjectThemeEn(String projectThemeEn) {
		this.projectThemeEn = projectThemeEn;
	}

	public String getProjectThemeRu() {
		return projectThemeRu;
	}

	public void setProjectThemeRu(String projectThemeRu) {
		this.projectThemeRu = projectThemeRu;
	}

	public String getProtocolNo() {
		return protocolNo;
	}

	public void setProtocolNo(String protocolNo) {
		this.protocolNo = protocolNo;
	}

	public Date getProtocolDate() {
		return protocolDate;
	}

	public void setProtocolDate(Date protocolDate) {
		this.protocolDate = protocolDate;
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

	public String getAcademicDegreeRu() {
		return academicDegreeRu;
	}

	public void setAcademicDegreeRu(String academicDegreeRu) {
		this.academicDegreeRu = academicDegreeRu;
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

	public String getDiplomaNo() {
		return diplomaNo;
	}

	public void setDiplomaNo(String diplomaNo) {
		this.diplomaNo = diplomaNo;
	}

	public Date getDiplomaDate() {
		return diplomaDate;
	}

	public void setDiplomaDate(Date diplomaDate) {
		this.diplomaDate = diplomaDate;
	}

	public String getRegistrationNo() {
		return registrationNo;
	}

	public void setRegistrationNo(String registrationNo) {
		this.registrationNo = registrationNo;
	}
}
