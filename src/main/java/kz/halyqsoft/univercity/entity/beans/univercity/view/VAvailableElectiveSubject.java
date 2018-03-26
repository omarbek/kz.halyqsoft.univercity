package kz.halyqsoft.univercity.entity.beans.univercity.view;

import org.r3a.common.entity.AbstractEntity;

/**
 * @author Omarbek
 * @created Feb 22, 2017 10:11:54 AM
 */
public final class VAvailableElectiveSubject extends AbstractEntity {

	private static final long serialVersionUID = 8934333134745925257L;
	
	private String subjectCode;
	private String subjectName;
	private Integer credit;
	private String formula;
	private String controlTypeName;
	
	public VAvailableElectiveSubject() {
	}

	public String getSubjectCode() {
		return subjectCode;
	}

	public void setSubjectCode(String subjectCode) {
		this.subjectCode = subjectCode;
	}

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
}
