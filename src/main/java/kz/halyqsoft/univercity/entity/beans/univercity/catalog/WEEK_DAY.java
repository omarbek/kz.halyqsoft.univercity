package kz.halyqsoft.univercity.entity.beans.univercity.catalog;

import org.r3a.common.entity.AbstractEntity;
import org.r3a.common.entity.EFieldType;
import org.r3a.common.entity.FieldInfo;

import javax.persistence.Column;
import javax.persistence.Entity;

/**
 * @author Omarbek
 * Vreated Dec 22, 2015 3:50:03 PM
 */
@Entity
public class WEEK_DAY extends AbstractEntity {
	
	private static final long serialVersionUID = -5764618741586068771L;

	@FieldInfo(type = EFieldType.TEXT, max = 11, order = 1)
	@Column(name = "DAY_NAME_RU", nullable = false)
	private String dayNameRU;
	
	@FieldInfo(type = EFieldType.TEXT, max = 2, order = 2, inGrid = false)
	@Column(name = "DAY_SHORT_NAME_RU", nullable = false)
	private String dayShortNameRU;
	
	@FieldInfo(type = EFieldType.TEXT, max = 11, order = 3)
	@Column(name = "DAY_NAME_KZ", nullable = false)
	private String dayNameKZ;
	
	@FieldInfo(type = EFieldType.TEXT, max = 2, order = 4, inGrid = false)
	@Column(name = "DAY_SHORT_NAME_KZ", nullable = false)
	private String dayShortNameKZ;
	
	@FieldInfo(type = EFieldType.TEXT, max = 11, order = 5)
	@Column(name = "DAY_NAME_EN", nullable = false)
	private String dayNameEN;
	
	@FieldInfo(type = EFieldType.TEXT, max = 2, order = 6, inGrid = false)
	@Column(name = "DAY_SHORT_NAME_EN", nullable = false)
	private String dayShortNameEN;

	public WEEK_DAY() {
	}

	public String getDayNameRU() {
		return dayNameRU;
	}

	public void setDayNameRU(String dayNameRU) {
		this.dayNameRU = dayNameRU;
	}

	public String getDayShortNameRU() {
		return dayShortNameRU;
	}

	public void setDayShortNameRU(String dayShortNameRU) {
		this.dayShortNameRU = dayShortNameRU;
	}

	public String getDayNameKZ() {
		return dayNameKZ;
	}

	public void setDayNameKZ(String dayNameKZ) {
		this.dayNameKZ = dayNameKZ;
	}

	public String getDayShortNameKZ() {
		return dayShortNameKZ;
	}

	public void setDayShortNameKZ(String dayShortNameKZ) {
		this.dayShortNameKZ = dayShortNameKZ;
	}

	public String getDayNameEN() {
		return dayNameEN;
	}

	public void setDayNameEN(String dayNameEN) {
		this.dayNameEN = dayNameEN;
	}

	public String getDayShortNameEN() {
		return dayShortNameEN;
	}

	public void setDayShortNameEN(String dayShortNameEN) {
		this.dayShortNameEN = dayShortNameEN;
	}

	@Override
	public String toString() {
		return dayShortNameRU;
	}
}
