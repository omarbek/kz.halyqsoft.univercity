package kz.halyqsoft.univercity.entity.beans.univercity.catalog;

import org.r3a.common.entity.AbstractEntity;
import org.r3a.common.entity.EFieldType;
import org.r3a.common.entity.FieldInfo;

import javax.persistence.Column;
import javax.persistence.Entity;

/**
 * @author Omarbek
 * Created Feb 17, 2016 3:34:12 PM
 */
@Entity
public class ENTRANCE_YEAR extends AbstractEntity {

	private static final long serialVersionUID = -8974306473931994368L;

	@FieldInfo(type = EFieldType.TEXT, min = 9, max = 9, order = 1)
	@Column(name = "ENTRANCE_YEAR", nullable = false)
	private String entranceYear;
	
	@FieldInfo(type = EFieldType.INTEGER, max = 2099, order = 2)
	@Column(name = "BEGIN_YEAR", nullable = false)
	private Integer beginYear;
	
	@FieldInfo(type = EFieldType.INTEGER, max = 2100, order = 3)
	@Column(name = "END_YEAR", nullable = false)
	private Integer endYear;
	
	public ENTRANCE_YEAR() {
	}

	public String getEntranceYear() {
		return entranceYear;
	}

	public void setEntranceYear(String entranceYear) {
		this.entranceYear = entranceYear;
	}

	public Integer getBeginYear() {
		return beginYear;
	}

	public void setBeginYear(Integer beginYear) {
		this.beginYear = beginYear;
	}

	public Integer getEndYear() {
		return endYear;
	}

	public void setEndYear(Integer endYear) {
		this.endYear = endYear;
	}

	@Override
	public String toString() {
		return entranceYear;
	}
}
