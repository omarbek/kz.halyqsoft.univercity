package kz.halyqsoft.univercity.entity.beans.univercity;

import kz.halyqsoft.univercity.entity.beans.univercity.catalog.ROOM;
import kz.halyqsoft.univercity.entity.beans.univercity.view.V_SUBJECT_SELECT;
import org.r3a.common.entity.AbstractEntity;
import org.r3a.common.entity.EFieldType;
import org.r3a.common.entity.FieldInfo;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;

/**
 * @@author Omarbek
 * @created Dec 24, 2015 7:40:37 PM
 */
@Entity
public class ROOM_SUBJECT extends AbstractEntity {

	private static final long serialVersionUID = -5959643349746662389L;

	@FieldInfo(type = EFieldType.FK_DIALOG, order = 1)
    @ManyToOne
    @JoinColumns({
        @JoinColumn(name = "ROOM_ID", referencedColumnName = "ID")})
    private ROOM room;
	
	@FieldInfo(type = EFieldType.FK_DIALOG, order = 2)
    @ManyToOne
    @JoinColumns({
        @JoinColumn(name = "SUBJECT_ID", referencedColumnName = "ID")})
    private V_SUBJECT_SELECT subject;
	
	public ROOM_SUBJECT() {
	}

	public ROOM getRoom() {
		return room;
	}

	public void setRoom(ROOM room) {
		this.room = room;
	}

	public V_SUBJECT_SELECT getSubject() {
		return subject;
	}

	public void setSubject(V_SUBJECT_SELECT subject) {
		this.subject = subject;
	}
}
