package kz.halyqsoft.univercity.entity.beans.univercity.view;

import org.r3a.common.entity.AbstractEntity;

/**
 * @@author Omarbek
 * @created Feb 21, 2017 4:44:54 PM
 */
public final class VAttestationDetail extends AbstractEntity {

	private static final long serialVersionUID = -9199613824929924110L;
	
	private int recordNo;
	private String studentCode;
	private String studentFIO;
	private Long attestationDetailId1;
	private boolean attended1;
	private boolean late1;
	private boolean missed1;
	private double grade1;
	private String comments1;
	private Long attestationDetailId2;
	private boolean attended2;
	private boolean late2;
	private boolean missed2;
	private double grade2;
	private String comments2;
	
	public VAttestationDetail() {
	}

	public int getRecordNo() {
		return recordNo;
	}

	public void setRecordNo(int recordNo) {
		this.recordNo = recordNo;
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

	public Long getAttestationDetailId1() {
		return attestationDetailId1;
	}

	public void setAttestationDetailId1(Long attestationDetailId1) {
		this.attestationDetailId1 = attestationDetailId1;
	}

	public boolean isAttended1() {
		return attended1;
	}

	public void setAttended1(boolean attended1) {
		this.attended1 = attended1;
	}

	public boolean isLate1() {
		return late1;
	}

	public void setLate1(boolean late1) {
		this.late1 = late1;
	}

	public boolean isMissed1() {
		return missed1;
	}

	public void setMissed1(boolean missed1) {
		this.missed1 = missed1;
	}

	public double getGrade1() {
		return grade1;
	}

	public void setGrade1(double grade1) {
		this.grade1 = grade1;
	}

	public String getComments1() {
		return comments1;
	}

	public void setComments1(String comments1) {
		this.comments1 = comments1;
	}

	public Long getAttestationDetailId2() {
		return attestationDetailId2;
	}

	public void setAttestationDetailId2(Long attestationDetailId2) {
		this.attestationDetailId2 = attestationDetailId2;
	}

	public boolean isAttended2() {
		return attended2;
	}

	public void setAttended2(boolean attended2) {
		this.attended2 = attended2;
	}

	public boolean isLate2() {
		return late2;
	}

	public void setLate2(boolean late2) {
		this.late2 = late2;
	}

	public boolean isMissed2() {
		return missed2;
	}

	public void setMissed2(boolean missed2) {
		this.missed2 = missed2;
	}

	public double getGrade2() {
		return grade2;
	}

	public void setGrade2(double grade2) {
		this.grade2 = grade2;
	}

	public String getComments2() {
		return comments2;
	}

	public void setComments2(String comments2) {
		this.comments2 = comments2;
	}
}
