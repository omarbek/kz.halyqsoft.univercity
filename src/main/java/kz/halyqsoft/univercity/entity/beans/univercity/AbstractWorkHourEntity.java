package kz.halyqsoft.univercity.entity.beans.univercity;

import kz.halyqsoft.univercity.entity.beans.univercity.catalog.DAY_HOUR;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.WEEK_DAY;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.WORK_HOUR_STATUS;
import org.r3a.common.entity.AbstractEntity;
import org.r3a.common.entity.EFieldType;
import org.r3a.common.entity.FieldInfo;

import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;

/**
 * @author Omarbek
 * @created Jan 2, 2016 3:30:54 PM
 */
@SuppressWarnings("serial")
@MappedSuperclass
public abstract class AbstractWorkHourEntity extends AbstractEntity implements Cloneable {
	
	@FieldInfo(type = EFieldType.FK_COMBO, order = 2)
    @ManyToOne
    @JoinColumns({
        @JoinColumn(name = "WEEK_DAY_ID", referencedColumnName = "ID")})
    private WEEK_DAY weekDay;
	
	@FieldInfo(type = EFieldType.FK_COMBO, order = 3)
    @ManyToOne
    @JoinColumns({
        @JoinColumn(name = "DAY_HOUR_ID", referencedColumnName = "ID")})
    private DAY_HOUR dayHour;
	
	@FieldInfo(type = EFieldType.FK_COMBO, order = 4)
    @ManyToOne
    @JoinColumns({
        @JoinColumn(name = "WORK_HOUR_STATUS_ID", referencedColumnName = "ID")})
    private WORK_HOUR_STATUS workHourStatus;
	
	@Transient
	private boolean changed = false;
	
	public WEEK_DAY getWeekDay() {
		return weekDay;
	}

	public void setWeekDay(WEEK_DAY weekDay) {
		this.weekDay = weekDay;
	}

	public DAY_HOUR getDayHour() {
		return dayHour;
	}

	public void setDayHour(DAY_HOUR dayHour) {
		this.dayHour = dayHour;
	}

	public WORK_HOUR_STATUS getWorkHourStatus() {
		return workHourStatus;
	}

	public void setWorkHourStatus(WORK_HOUR_STATUS workHourStatus) {
		this.workHourStatus = workHourStatus;
	}

	public boolean isChanged() {
		return changed;
	}

	public void setChanged(boolean changed) {
		this.changed = changed;
	}
	
	@Transient
	public final AbstractWorkHourEntity getClone() throws Exception {
		return (AbstractWorkHourEntity)clone();
	}
}
