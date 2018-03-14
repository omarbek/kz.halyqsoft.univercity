package kz.halyqsoft.univercity.entity.beans.univercity.catalog;

import org.r3a.common.entity.AbstractEntity;
import org.r3a.common.entity.EFieldType;
import org.r3a.common.entity.FieldInfo;

import javax.persistence.Column;
import javax.persistence.Entity;

/**
 * @author Omarbek
 * Created Apr 26, 2016 10:25:15 AM
 */
@Entity
public class SEMESTER_PERIOD extends AbstractEntity {

	private static final long serialVersionUID = 8109353218081571660L;
	
	@FieldInfo(type = EFieldType.TEXT, order = 2)
	@Column(name = "PERIOD_NAME", nullable = false)
	private String periodName;
	
	public SEMESTER_PERIOD() {
	}

	public String getPeriodName() {
		return periodName;
	}

	public void setPeriodName(String periodName) {
		this.periodName = periodName;
	}

	@Override
	public String toString() {
		return periodName;
	}
}
