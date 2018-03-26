package kz.halyqsoft.univercity.entity.beans.univercity.view;

import org.r3a.common.entity.AbstractEntity;

import java.util.Locale;

/**
 * @author Omarbek
 * @created Feb 27, 2017 11:33:54 AM
 */
@SuppressWarnings("unused")
public final class VSubjectRequisite extends AbstractEntity {

	private static final long serialVersionUID = 2253505837562684180L;
	
	private String nameKZ;
	private String nameEN;
	private String nameRU;
	private String code;
	private int credit;
	private String formula;
	private String controlTypeName;
	private String subjectName;

	private final Locale locale;
	
	public VSubjectRequisite(Locale locale) {
		this.locale = locale;
	}

	public String getNameKZ() {
		return nameKZ;
	}

	public void setNameKZ(String nameKZ) {
		this.nameKZ = nameKZ;
	}

	public String getNameEN() {
		return nameEN;
	}

	public void setNameEN(String nameEN) {
		this.nameEN = nameEN;
	}

	public String getNameRU() {
		return nameRU;
	}

	public void setNameRU(String nameRU) {
		this.nameRU = nameRU;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
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

	public String getControlTypeName() {
		return controlTypeName;
	}

	public void setControlTypeName(String controlTypeName) {
		this.controlTypeName = controlTypeName;
	}

	public String getSubjectName() {
		if (locale.getLanguage().equals("kk")) {
			return nameKZ;
		} else if (locale.getLanguage().equals("en")) {
			return nameEN;
		} else {
			return nameRU;
		}
	}

	public void setSubjectName(String subjectName) {
	}
}
