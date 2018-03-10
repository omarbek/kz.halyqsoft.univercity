package kz.halyqsoft.univercity.entity.beans.univercity;

import kz.halyqsoft.univercity.entity.beans.univercity.catalog.EQUIPMENT;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.ROOM;
import org.r3a.common.entity.AbstractEntity;
import org.r3a.common.entity.EFieldType;
import org.r3a.common.entity.FieldInfo;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;

/**
 * @author Rakymzhan A. Kenzhegul
 * @created Dec 24, 2015 7:51:23 PM
 */
@Entity
public class ROOM_EQUIPMENT extends AbstractEntity {

	private static final long serialVersionUID = -2283504619269589343L;

	@FieldInfo(type = EFieldType.FK_DIALOG, order = 1)
    @ManyToOne
    @JoinColumns({
        @JoinColumn(name = "ROOM_ID", referencedColumnName = "ID")})
    private ROOM room;
	
	@FieldInfo(type = EFieldType.FK_DIALOG, order = 2)
    @ManyToOne
    @JoinColumns({
        @JoinColumn(name = "EQUIPMENT_ID", referencedColumnName = "ID")})
    private EQUIPMENT equipment;
	
	public ROOM_EQUIPMENT() {
	}

	public ROOM getRoom() {
		return room;
	}

	public void setRoom(ROOM room) {
		this.room = room;
	}

	public EQUIPMENT getEquipment() {
		return equipment;
	}

	public void setEquipment(EQUIPMENT equipment) {
		this.equipment = equipment;
	}
}
