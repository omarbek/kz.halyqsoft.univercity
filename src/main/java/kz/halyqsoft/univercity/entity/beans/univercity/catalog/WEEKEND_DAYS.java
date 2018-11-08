package kz.halyqsoft.univercity.entity.beans.univercity.catalog;

import org.r3a.common.entity.AbstractEntity;
import org.r3a.common.entity.EFieldType;
import org.r3a.common.entity.FieldInfo;

import javax.persistence.*;

@Entity
public class WEEKEND_DAYS extends AbstractEntity {


	@FieldInfo(type = EFieldType.TEXT, order = 1 )
	@Column(name = "WEEKEND_NAME", nullable = false)
	private String weekendName;

	@FieldInfo(type = EFieldType.FK_COMBO, order = 2)
	@ManyToOne
	@JoinColumns({
			@JoinColumn(name = "MONTH_ID", referencedColumnName = "ID")})
	private MONTH month;

	@FieldInfo(type = EFieldType.INTEGER, order = 3, max = 31)
	@Column(name = "WEEKEND_DAY")
	private int weekendDay;

	public WEEKEND_DAYS() {
	}

	@Override
	public String toString() {
		return weekendName + ": " + month + " " + weekendDay;
	}

	public String getWeekendName() {
		return weekendName;
	}

	public void setWeekendName(String weekendName) {
		this.weekendName = weekendName;
	}

	public MONTH getMonth() {
		return month;
	}

	public void setMonth(MONTH month) {
		this.month = month;
	}

	public int getWeekendDay() {
		return weekendDay;
	}

	public void setWeekendDay(int weekendDay) {
		this.weekendDay = weekendDay;
	}


}
