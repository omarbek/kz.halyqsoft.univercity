package kz.halyqsoft.univercity.entity.beans.univercity;

import kz.halyqsoft.univercity.entity.beans.univercity.catalog.DEPARTMENT;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.ROOM;
import org.r3a.common.entity.AbstractEntity;
import org.r3a.common.entity.EFieldType;
import org.r3a.common.entity.FieldInfo;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;

/**
 * @@author Omarbek
 * @created Jan 13, 2016 4:36:05 PM
 */
@Entity
public class ROOM_DEPARTMENT extends AbstractEntity {

	private static final long serialVersionUID = -6785960030828699828L;

	@FieldInfo(type = EFieldType.FK_DIALOG, order = 1)
    @ManyToOne
    @JoinColumns({
        @JoinColumn(name = "ROOM_ID", referencedColumnName = "ID")})
    private ROOM room;
	
	@FieldInfo(type = EFieldType.FK_DIALOG, order = 2)
    @ManyToOne
    @JoinColumns({
        @JoinColumn(name = "DEPARTMENT_ID", referencedColumnName = "ID")})
    private DEPARTMENT department;
	
	public ROOM_DEPARTMENT() {
	}

	public ROOM getRoom() {
		return room;
	}

	public void setRoom(ROOM room) {
		this.room = room;
	}

	public DEPARTMENT getDepartment() {
		return department;
	}

	public void setDepartment(DEPARTMENT department) {
		this.department = department;
	}
}
