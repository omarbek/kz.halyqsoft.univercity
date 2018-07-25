package kz.halyqsoft.univercity.entity.beans.univercity.view;

import org.r3a.common.entity.AbstractEntity;
import org.r3a.common.entity.EFieldType;
import org.r3a.common.entity.FieldInfo;

/**
 * @author Omarbek
 * @created 18.07.2018
 */
public final class VPairSubject extends AbstractEntity {

	@FieldInfo(type = EFieldType.TEXT)
	private String subjectName;

	@FieldInfo(type = EFieldType.TEXT, order = 2)
	private Integer credit;

	@FieldInfo(type = EFieldType.TEXT, order = 3)
	private Integer ects;

	@FieldInfo(type = EFieldType.TEXT, order = 4)
	private String semesterName;

	@FieldInfo(type = EFieldType.TEXT, order = 5)
	private String description;

	@FieldInfo(type = EFieldType.TEXT, order = 6)
	private Long pairNumber;

	public String getSubjectName() {
		return subjectName;
	}

	public void setSubjectName(String subjectName) {
		this.subjectName = subjectName;
	}

	public Integer getCredit() {
		return credit;
	}

	public void setCredit(Integer credit) {
		this.credit = credit;
	}

	public Integer getEcts() {
		return ects;
	}

	public void setEcts(Integer ects) {
		this.ects = ects;
	}

	public String getSemesterName() {
		return semesterName;
	}

	public void setSemesterName(String semesterName) {
		this.semesterName = semesterName;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Long getPairNumber() {
		return pairNumber;
	}

	public void setPairNumber(Long pairNumber) {
		this.pairNumber = pairNumber;
	}
}
