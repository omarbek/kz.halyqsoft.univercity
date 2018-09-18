package kz.halyqsoft.univercity.entity.beans.univercity.view;

import org.r3a.common.entity.AbstractEntity;
import org.r3a.common.entity.EFieldType;
import org.r3a.common.entity.FieldInfo;

/**
 * @author Omarbek
 * @created 18.07.2018
 */
public final class VPairSubject extends AbstractEntity {

	@FieldInfo(type = EFieldType.INTEGER,order = 1)
	private long code;

	@FieldInfo(type = EFieldType.TEXT,order = 2)
	private String subjectName;

	@FieldInfo(type = EFieldType.TEXT, order = 3)
	private Integer credit;

	@FieldInfo(type = EFieldType.TEXT, order = 4)
	private Integer ects;

	@FieldInfo(type = EFieldType.TEXT, order = 5)
	private String semesterName;

	@FieldInfo(type = EFieldType.TEXT, order = 6)
	private Long pairNumber;

	@FieldInfo(type = EFieldType.TEXT, order = 7)
	private String postrequisite;

	@FieldInfo(type = EFieldType.TEXT, order = 8)
	private String prerequisite;

	@FieldInfo(type = EFieldType.TEXT, order = 9)
	private String aim;

	@FieldInfo(type = EFieldType.TEXT, order = 10)
	private String description;

	@FieldInfo(type = EFieldType.TEXT, order = 11)
	private String competence;

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

	public long getCode() {
		return code;
	}

	public void setCode(long code) {
		this.code = code;
	}

	public String getAim() {
		return aim;
	}

	public void setAim(String aim) {
		this.aim = aim;
	}

	public String getPostrequisite() {
		return postrequisite;
	}

	public void setPostrequisite(String postrequisite) {
		this.postrequisite = postrequisite;
	}

	public String getPrerequisite() {
		return prerequisite;
	}

	public void setPrerequisite(String prerequisite) {
		this.prerequisite = prerequisite;
	}

	public String getCompetence() {
		return competence;
	}

	public void setCompetence(String competence) {
		this.competence = competence;
	}
}
