package kz.halyqsoft.univercity.entity.beans.univercity;

import kz.halyqsoft.univercity.entity.beans.univercity.catalog.ROOM;
import org.r3a.common.entity.EFieldType;
import org.r3a.common.entity.FieldInfo;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;

/**
 * @author Omarbek
 * @created Dec 24, 2015 8:09:08 PM
 */
@Entity
public class ROOM_WORK_HOUR extends AbstractWorkHourEntity {

	private static final long serialVersionUID = -7769389996757072018L;

	@FieldInfo(type = EFieldType.FK_DIALOG, order = 1, inEdit = false, inGrid = false, inView = false)
    @ManyToOne
    @JoinColumns({
        @JoinColumn(name = "ROOM_ID", referencedColumnName = "ID")})
    private ROOM room;
	
	public ROOM_WORK_HOUR() {
	}

	public ROOM getRoom() {
		return room;
	}

	public void setRoom(ROOM room) {
		this.room = room;
	}
	
	@Override
	protected Object clone() throws CloneNotSupportedException {
		ROOM_WORK_HOUR rwh = new ROOM_WORK_HOUR();
		rwh.setId(getId());
		rwh.setRoom(getRoom());
		rwh.setWeekDay(getWeekDay());
		rwh.setDayHour(getDayHour());
		rwh.setWorkHourStatus(getWorkHourStatus());
		rwh.setChanged(isChanged());
		
		return rwh;
	}
}
