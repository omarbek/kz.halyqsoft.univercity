package kz.halyqsoft.univercity.entity.beans.univercity.view;

import org.r3a.common.entity.AbstractEntity;

import javax.persistence.Column;
import javax.persistence.Entity;

/**
 * @author Omarbek
 * @created Nov 2, 2016 10:27:00 AM
 */
@Entity
public class V_STUDENT_SUBJECT extends AbstractEntity {

	private static final long serialVersionUID = -6630474683884352722L;
	
	@Column(name = "SUBJECT_CODE", nullable = false)
	private String subjectCode;

	@Column(name = "SUBJECT_NAME", nullable = false)
	private String subjectName;

	@Column(name = "CREDIT", nullable = false)
	private Integer credit;
	
	@Column(name = "FORMULA", nullable = false)
	private String formula;
	
	@Column(name = "CONTROL_TYPE_NAME", nullable = false)
	private String controlTypeName;
	
	public V_STUDENT_SUBJECT() {
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
