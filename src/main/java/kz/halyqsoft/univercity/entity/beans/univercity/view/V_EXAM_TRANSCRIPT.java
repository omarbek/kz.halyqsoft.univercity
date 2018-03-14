package kz.halyqsoft.univercity.entity.beans.univercity.view;

import org.r3a.common.entity.AbstractEntity;

import java.math.BigInteger;

import javax.persistence.Column;
import javax.persistence.Entity;

/**
 * @@author Omarbek
 * @created Dec 6, 2016 6:00:12 PM
 */
@Entity
public class V_EXAM_TRANSCRIPT extends AbstractEntity {

	private static final long serialVersionUID = 8129688497188162915L;

	@Column(name = "SEMESTER_DATA_ID")
	private BigInteger semesterDataId;
	
	@Column(name = "SUBJECT_CODE")
	private String subjectCode;
	
	@Column(name = "SUBJECT_NAME_KZ")
	private String subjectNameKZ;
	
	@Column(name = "SUBJECT_NAME_EN")
	private String subjectNameEN;
	
	@Column(name = "SUBJECT_NAME_RU")
	private String subjectNameRU;
	
	@Column(name = "CREDIT")
	private Integer credit;
	
	@Column(name = "TOTAL_GRADE")
	private Double totalGrade;
	
	@Column(name = "DIGIT_GRADE")
	private Double digitGrade;
	
	@Column(name = "ALPHA_GRADE")
	private String alphaGrade;
	
	public V_EXAM_TRANSCRIPT() {
	}

	public BigInteger getSemesterDataId() {
		return semesterDataId;
	}

	public void setSemesterDataId(BigInteger semesterDataId) {
		this.semesterDataId = semesterDataId;
	}

	public String getSubjectCode() {
		return subjectCode;
	}

	public void setSubjectCode(String subjectCode) {
		this.subjectCode = subjectCode;
	}

	public String getSubjectNameKZ() {
		return subjectNameKZ;
	}

	public void setSubjectNameKZ(String subjectNameKZ) {
		this.subjectNameKZ = subjectNameKZ;
	}

	public String getSubjectNameEN() {
		return subjectNameEN;
	}

	public void setSubjectNameEN(String subjectNameEN) {
		this.subjectNameEN = subjectNameEN;
	}

	public String getSubjectNameRU() {
		return subjectNameRU;
	}

	public void setSubjectNameRU(String subjectNameRU) {
		this.subjectNameRU = subjectNameRU;
	}

	public Integer getCredit() {
		return credit;
	}

	public void setCredit(Integer credit) {
		this.credit = credit;
	}

	public Double getTotalGrade() {
		return totalGrade;
	}

	public void setTotalGrade(Double totalGrade) {
		this.totalGrade = totalGrade;
	}

	public Double getDigitGrade() {
		return digitGrade;
	}

	public void setDigitGrade(Double digitGrade) {
		this.digitGrade = digitGrade;
	}

	public String getAlphaGrade() {
		return alphaGrade;
	}

	public void setAlphaGrade(String alphaGrade) {
		this.alphaGrade = alphaGrade;
	}
}
