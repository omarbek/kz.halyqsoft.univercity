package kz.halyqsoft.univercity.entity.beans.univercity.view;

import org.r3a.common.entity.AbstractEntity;

/**
 * @author Omarbek
 * @created Mar 30, 2018 5:01:44 PM
 */
public class VIndividualCurriculumPrint extends AbstractEntity {
	
	private static final long serialVersionUID = -8177021263255783072L;
	
	private int recordNo;
	private String subjectNameKZ;
	private String subjectNameEN;
	private String subjectNameRU;
	private char subjectStatus;
	private String cycleShortName;
	private int credit;
	private String teacherFIO;

	public VIndividualCurriculumPrint() {
	}

	public int getRecordNo() {
		return recordNo;
	}

	public void setRecordNo(int recordNo) {
		this.recordNo = recordNo;
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

	public char getSubjectStatus() {
		return subjectStatus;
	}

	public void setSubjectStatus(char subjectStatus) {
		this.subjectStatus = subjectStatus;
	}

	public String getCycleShortName() {
		return cycleShortName;
	}

	public void setCycleShortName(String cycleShortName) {
		this.cycleShortName = cycleShortName;
	}

	public int getCredit() {
		return credit;
	}

	public void setCredit(int credit) {
		this.credit = credit;
	}

	public String getTeacherFIO() {
		return teacherFIO;
	}

	public void setTeacherFIO(String teacherFIO) {
		this.teacherFIO = teacherFIO;
	}
}
