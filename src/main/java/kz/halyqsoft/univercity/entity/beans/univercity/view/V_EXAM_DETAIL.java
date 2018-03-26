package kz.halyqsoft.univercity.entity.beans.univercity.view;

import org.r3a.common.entity.AbstractEntity;
import org.r3a.common.entity.EFieldType;
import org.r3a.common.entity.FieldInfo;

import javax.persistence.Column;
import javax.persistence.Entity;

/**
 * @author Omarbek
 * @created Jul 12, 2016 2:46:36 AM
 */
@Entity
public class V_EXAM_DETAIL extends AbstractEntity {

	private static final long serialVersionUID = 1798842133993661797L;

	@FieldInfo(type = EFieldType.TEXT, order = 1)
	@Column(name = "STUDENT_CODE", nullable = false)
	private String studentCode;
	
	@FieldInfo(type = EFieldType.TEXT, order = 2)
	@Column(name = "STUDENT_FIO", nullable = false)
	private String studentFIO;
	
	@FieldInfo(type = EFieldType.BOOLEAN, order = 3)
	@Column(name = "ATTENDED", nullable = false)
    private boolean attended;
	
	@FieldInfo(type = EFieldType.BOOLEAN, order = 4)
	@Column(name = "LATE", nullable = false)
    private boolean late;
	
	@FieldInfo(type = EFieldType.BOOLEAN, order = 5)
	@Column(name = "MISSED", nullable = false)
    private boolean missed;
	
	@FieldInfo(type = EFieldType.DOUBLE, order = 6)
	@Column(name = "ATTESTATION_1", nullable = false)
	private Double attestation1;
	
	@FieldInfo(type = EFieldType.DOUBLE, order = 7)
	@Column(name = "ATTESTATION_2", nullable = false)
	private Double attestation2;
	
	@FieldInfo(type = EFieldType.DOUBLE, order = 8)
	@Column(name = "GRADE", nullable = false)
	private Double grade;
	
	@FieldInfo(type = EFieldType.DOUBLE, order = 9)
	@Column(name = "TOTAL_GRADE", nullable = false)
	private Double totalGrade;
	
	@FieldInfo(type = EFieldType.DOUBLE, order = 10)
	@Column(name = "DIGIT_GRADE", nullable = false)
	private Double digitGrade;
	
	@FieldInfo(type = EFieldType.TEXT, order = 11)
	@Column(name = "ALPHA_GRADE")
	private String alphaGrade;
	
	@FieldInfo(type = EFieldType.TEXT, order = 12)
	@Column(name = "COMMENTS")
	private String comments;
	
	public V_EXAM_DETAIL() {
	}

	public String getStudentCode() {
		return studentCode;
	}

	public void setStudentCode(String studentCode) {
		this.studentCode = studentCode;
	}

	public String getStudentFIO() {
		return studentFIO;
	}

	public void setStudentFIO(String studentFIO) {
		this.studentFIO = studentFIO;
	}

	public boolean isAttended() {
		return attended;
	}

	public void setAttended(boolean attended) {
		this.attended = attended;
	}

	public boolean isLate() {
		return late;
	}

	public void setLate(boolean late) {
		this.late = late;
	}

	public boolean isMissed() {
		return missed;
	}

	public void setMissed(boolean missed) {
		this.missed = missed;
	}

	public Double getAttestation1() {
		return attestation1;
	}

	public void setAttestation1(Double attestation1) {
		this.attestation1 = attestation1;
	}

	public Double getAttestation2() {
		return attestation2;
	}

	public void setAttestation2(Double attestation2) {
		this.attestation2 = attestation2;
	}

	public Double getGrade() {
		return grade;
	}

	public void setGrade(Double grade) {
		this.grade = grade;
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

	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}
}
