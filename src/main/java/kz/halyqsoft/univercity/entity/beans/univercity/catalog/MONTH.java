package kz.halyqsoft.univercity.entity.beans.univercity.catalog;

import org.r3a.common.entity.AbstractEntity;
import org.r3a.common.entity.EFieldType;
import org.r3a.common.entity.FieldInfo;

import java.util.Locale;

import javax.persistence.Column;
import javax.persistence.Entity;

/**
 * @author Omarbek
 * Created Mar 1, 2016 9:01:18 AM
 */
@Entity
public class MONTH extends AbstractEntity {




	private static final long serialVersionUID = 161308351644533733L;

	@FieldInfo(type = EFieldType.TEXT, order = 1)
	@Column(name = "MONTH_NAME_RU", nullable = false)
	private String monthNameRU;
	
	@FieldInfo(type = EFieldType.TEXT, order = 2)
	@Column(name = "MONTH_NAME_EN", nullable = false)
	private String monthNameEN;
	
	@FieldInfo(type = EFieldType.TEXT, order = 3)
	@Column(name = "MONTH_NAME_KZ", nullable = false)
	private String monthNameKZ;
	
	public MONTH() {
	}

	public String getMonthNameRU() {
		return monthNameRU;
	}

	public void setMonthNameRU(String monthNameRU) {
		this.monthNameRU = monthNameRU;
	}

	public String getMonthNameEN() {
		return monthNameEN;
	}

	public void setMonthNameEN(String monthNameEN) {
		this.monthNameEN = monthNameEN;
	}

	public String getMonthNameKZ() {
		return monthNameKZ;
	}

	public void setMonthNameKZ(String monthNameKZ) {
		this.monthNameKZ = monthNameKZ;
	}

	@Override
	public String toString() {
		return monthNameRU;
	}
	
	public String toString(Locale locale) {
		if (locale.getLanguage().equals("kk")) {
			return monthNameKZ;
		} else if (locale.getLanguage().equals("en")) {
			return monthNameEN;
		} else {
			return monthNameRU;
		}
	}
}
