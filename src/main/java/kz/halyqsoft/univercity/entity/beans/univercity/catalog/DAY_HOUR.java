package kz.halyqsoft.univercity.entity.beans.univercity.catalog;

import org.r3a.common.entity.AbstractEntity;
import org.r3a.common.entity.EFieldType;
import org.r3a.common.entity.FieldInfo;

import javax.persistence.Column;
import javax.persistence.Entity;

/**
 * @author Omarbek
 * Created Dec 22, 2015 4:02:37 PM
 */
@Entity
public class DAY_HOUR extends AbstractEntity {

	private static final long serialVersionUID = -8161086049501550103L;

	@FieldInfo(type = EFieldType.DOUBLE, order = 1, min = 8, max = 21)
	@Column(name = "FROM_HOUR", nullable = false)
    private double fromHour;
	
	@FieldInfo(type = EFieldType.DOUBLE, order = 2, min = 9, max = 22)
	@Column(name = "TO_HOUR", nullable = false)
    private double toHour;
	
	@FieldInfo(type = EFieldType.TEXT, max = 5, order = 3)
	@Column(name = "TIME_PERIOD", nullable = false)
	private String timePeriod;
	
	public DAY_HOUR() {
	}

	public double getFromHour() {
		return fromHour;
	}

	public void setFromHour(double fromHour) {
		this.fromHour = fromHour;
	}

	public double getToHour() {
		return toHour;
	}

	public void setToHour(double toHour) {
		this.toHour = toHour;
	}

	public String getTimePeriod() {
		return timePeriod;
	}

	public void setTimePeriod(String timePeriod) {
		this.timePeriod = timePeriod;
	}

	@Override
	public String toString() {
		return timePeriod;
	}
}
