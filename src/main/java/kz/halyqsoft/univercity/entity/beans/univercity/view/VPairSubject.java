package kz.halyqsoft.univercity.entity.beans.univercity.view;

import org.r3a.common.entity.AbstractEntity;
import org.r3a.common.entity.EFieldType;
import org.r3a.common.entity.FieldInfo;

import java.util.List;

/**
 * @author Omarbek
 * @created 18.07.2018
 */
public final class VPairSubject extends AbstractEntity {

	@FieldInfo(type = EFieldType.TEXT,order = 1)
	private String cycleShortName;

	@FieldInfo(type = EFieldType.TEXT,order = 2)
	private String subjectName;

	@FieldInfo(type = EFieldType.TEXT,order = 3)
	private String trajectoryName;

	@FieldInfo(type = EFieldType.TEXT, order = 4)
	private Integer credit;

	@FieldInfo(type = EFieldType.TEXT, order = 5)
	private Integer ects;

	@FieldInfo(type = EFieldType.TEXT, order = 6)
	private String semesterName;

	@FieldInfo(type = EFieldType.TEXT, order = 7)
	private Long pairNumber;

	@FieldInfo(type = EFieldType.TEXT, order = 8)
	private List<String> postrequisites;

	@FieldInfo(type = EFieldType.TEXT, order = 9)
	private List<String> prerequisites;

	@FieldInfo(type = EFieldType.TEXT, order = 10)
	private String aim;

	@FieldInfo(type = EFieldType.TEXT, order = 11)
	private String description;

	@FieldInfo(type = EFieldType.TEXT, order = 12)
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

	public String getCycleShortName() {
		return cycleShortName;
	}

	public void setCycleShortName(String cycleShortName) {
		this.cycleShortName = cycleShortName;
	}

	public String getAim() {
		return aim;
	}

	public void setAim(String aim) {
		this.aim = aim;
	}

	public List<String> getPostrequisites() {
		return postrequisites;
	}

	public void setPostrequisites(List<String> postrequisites) {
		this.postrequisites = postrequisites;
	}

	public List<String> getPrerequisites() {
		return prerequisites;
	}

	public void setPrerequisites(List<String> prerequisites) {
		this.prerequisites = prerequisites;
	}

	public String getCompetence() {
		return competence;
	}

	public void setCompetence(String competence) {
		this.competence = competence;
	}

	public String getTrajectoryName() {
		return trajectoryName;
	}

	public void setTrajectoryName(String trajectoryName) {
		this.trajectoryName = trajectoryName;
	}
}
