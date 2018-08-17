package kz.halyqsoft.univercity.entity.beans.univercity.view;

import org.r3a.common.entity.AbstractEntity;

import javax.persistence.Column;
import javax.persistence.Entity;

/**
 * @author Omarbek
 * @created Nov 3, 2016 5:01:59 PM
 */
@Entity
public class V_SEMESTER_SUBJECT extends AbstractEntity {

	private static final long serialVersionUID = -8978926179888396447L;

	@Column(name = "SUBJECT_NAME", nullable = false)
	private String subjectName;
	
	@Column(name = "CHAIR_NAME", nullable = false)
	private String chairName;
	
	@Column(name = "LEVEL_NAME", nullable = false)
	private String levelName;
	
	@Column(name = "CYCLE_SHORT_NAME", nullable = false)
	private String cycleShortName;

	@Column(name = "CREDIT", nullable = false)
	private Integer credit;

	@Column(name = "PAIR_NUMBER", nullable = false)
	private Integer pairNumber;

	@Column(name = "FORMULA", nullable = false)
	private String formula;
	
	@Column(name = "CONTROL_TYPE_NAME", nullable = false)
	private String controlTypeName;
	
	public V_SEMESTER_SUBJECT() {
	}

	public String getSubjectName() {
		return subjectName;
	}

	public void setSubjectName(String subjectName) {
		this.subjectName = subjectName;
	}
	
	public String getChairName() {
		return chairName;
	}

	public void setChairName(String chairName) {
		this.chairName = chairName;
	}

	public String getLevelName() {
		return levelName;
	}

	public void setLevelName(String levelName) {
		this.levelName = levelName;
	}

	public String getCycleShortName() {
		return cycleShortName;
	}

	public void setCycleShortName(String cycleShortName) {
		this.cycleShortName = cycleShortName;
	}

	public Integer getCredit() {
		return credit;
	}

	public void setCredit(Integer credit) {
		this.credit = credit;
	}

	public String getFormula() {
		return formula;
	}

	public void setFormula(String formula) {
		this.formula = formula;
	}

	public String getControlTypeName() {
		return controlTypeName;
	}

	public void setControlTypeName(String controlTypeName) {
		this.controlTypeName = controlTypeName;
	}

	public Integer getPairNumber() {
		return pairNumber;
	}

	public void setPairNumber(Integer pairNumber) {
		this.pairNumber = pairNumber;
	}
}
