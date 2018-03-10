package kz.halyqsoft.univercity.entity.beans.univercity.view;

import org.r3a.common.entity.AbstractEntity;
import org.r3a.common.entity.EFieldType;
import org.r3a.common.entity.FieldInfo;

/**
 * @author Rakymzhan A. Kenzhegul
 * @created Apr 20, 2017 3:41:10 PM
 */
public class VCurriculumDetail extends AbstractEntity {

	private static final long serialVersionUID = -2605105139940857499L;

	@FieldInfo(type = EFieldType.TEXT, order = 2, columnWidth = 120)
	private String code;
	
	@FieldInfo(type = EFieldType.TEXT, order = 3)
	private String subjectName;
	
	@FieldInfo(type = EFieldType.TEXT, order = 4, columnWidth = 90)
	private String cycleShortName;
	
	@FieldInfo(type = EFieldType.INTEGER, order = 5, columnWidth = 80)
	private int credit;
	
	@FieldInfo(type = EFieldType.TEXT, order = 6, columnWidth = 90)
	private String formula;
	
	public VCurriculumDetail() {
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getSubjectName() {
		return subjectName;
	}

	public void setSubjectName(String subjectName) {
		this.subjectName = subjectName;
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

	public String getFormula() {
		return formula;
	}

	public void setFormula(String formula) {
		this.formula = formula;
	}
}
