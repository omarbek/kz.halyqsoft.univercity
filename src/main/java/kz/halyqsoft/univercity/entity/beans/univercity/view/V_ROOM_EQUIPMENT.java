package kz.halyqsoft.univercity.entity.beans.univercity.view;

import kz.halyqsoft.univercity.entity.beans.univercity.catalog.EQUIPMENT;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.ROOM;
import org.r3a.common.entity.AbstractEntity;
import org.r3a.common.entity.EFieldType;
import org.r3a.common.entity.FieldInfo;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;

/**
 * @author Rakymzhan A. Kenzhegul
 * @created Jan 6, 2016 10:16:21 AM
 */
@Entity
public class V_ROOM_EQUIPMENT extends AbstractEntity {

	private static final long serialVersionUID = -6410374273883204067L;

	@FieldInfo(type = EFieldType.FK_DIALOG, order = 1, inGrid = false, inEdit = false, inView = false)
    @ManyToOne
    @JoinColumns({
        @JoinColumn(name = "ROOM_ID", referencedColumnName = "ID")})
    private ROOM room;
	
	@FieldInfo(type = EFieldType.FK_COMBO, order = 2, inGrid = false)
    @ManyToOne
    @JoinColumns({
        @JoinColumn(name = "EQUIPMENT_ID", referencedColumnName = "ID")})
    private EQUIPMENT equipment;
	
	@FieldInfo(type = EFieldType.TEXT, max = 32, order = 3, inEdit = false, inView = false)
	@Column(name = "EQUIPMENT_NAME", nullable = false)
	private String equipmentName;
	
	public V_ROOM_EQUIPMENT() {
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

	public String getEquipmentName() {
		return equipmentName;
	}

	public void setEquipmentName(String equipmentName) {
		this.equipmentName = equipmentName;
	}
}
