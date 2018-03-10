package kz.halyqsoft.univercity.entity.beans.univercity.catalog;

import org.r3a.common.entity.AbstractEntity;
import org.r3a.common.entity.EFieldType;
import org.r3a.common.entity.FieldInfo;

import javax.persistence.Column;
import javax.persistence.Entity;

/**
 * Author Rakymzhan A. Kenzhegul
 * Created Mar 1, 2016 10:00:13 AM
 */
@Entity
public class WEEK extends AbstractEntity {

	private static final long serialVersionUID = -7567547976010791831L;
	
	@FieldInfo(type = EFieldType.TEXT, order = 1)
	@Column(name = "WEEK_NAME", nullable = false)
	private String weekName;
	
	@FieldInfo(type = EFieldType.INTEGER, order = 2)
	@Column(name = "WEEK_CODE", nullable = false)
	private Integer weekCode;
	
	public WEEK() {
	}

	public String getWeekName() {
		return weekName;
	}

	public void setWeekName(String weekName) {
		this.weekName = weekName;
	}
	
	public Integer getWeekCode() {
		return weekCode;
	}

	public void setWeekCode(Integer weekCode) {
		this.weekCode = weekCode;
	}

	@Override
	public String toString() {
		return weekName;
	}
}
