package kz.halyqsoft.univercity.entity.beans.univercity;

import kz.halyqsoft.univercity.entity.beans.univercity.catalog.ROOM;
import org.r3a.common.entity.AbstractEntity;
import org.r3a.common.entity.EFieldType;
import org.r3a.common.entity.FieldInfo;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;

/**
 * @author Omarbek
 * @created Nov 14, 2016 12:25:21 PM
 */
@Entity
public class TEACHER_ROOM extends AbstractEntity {

	private static final long serialVersionUID = 2065537212647420624L;

	@FieldInfo(type = EFieldType.FK_DIALOG, order = 1)
    @ManyToOne
    @JoinColumns({
        @JoinColumn(name = "TEACHER_ID", referencedColumnName = "ID")})
    private EMPLOYEE teacher;
	
	@FieldInfo(type = EFieldType.FK_DIALOG, order = 2)
    @ManyToOne
    @JoinColumns({
        @JoinColumn(name = "ROOM_ID", referencedColumnName = "ID")})
    private ROOM room;
	
	public TEACHER_ROOM() {
	}

	public EMPLOYEE getTeacher() {
		return teacher;
	}

	public void setTeacher(EMPLOYEE teacher) {
		this.teacher = teacher;
	}

	public ROOM getRoom() {
		return room;
	}

	public void setRoom(ROOM room) {
		this.room = room;
	}
}
