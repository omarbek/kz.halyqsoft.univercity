package kz.halyqsoft.univercity.entity.beans.univercity.view;

import kz.halyqsoft.univercity.entity.beans.univercity.EMPLOYEE;
import org.r3a.common.entity.AbstractEntity;
import org.r3a.common.entity.EFieldType;
import org.r3a.common.entity.FieldInfo;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;

/**
 * @author Omarbek
 * @created Nov 14, 2016 2:53:06 PM
 */
@Entity
public class V_TEACHER_ROOM extends AbstractEntity {

	private static final long serialVersionUID = -8223421659756130613L;

    @ManyToOne
    @JoinColumns({
        @JoinColumn(name = "TEACHER_ID", referencedColumnName = "ID")})
    private EMPLOYEE teacher;
	
	@FieldInfo(type = EFieldType.TEXT, order = 2, inEdit = false, inView = false, columnWidth = 100)
	@Column(name = "CORPUS_NAME", nullable = false)
	private String corpusName;
	
	@FieldInfo(type = EFieldType.TEXT, max = 32, order = 3, columnWidth = 110)
	@Column(name = "ROOM_NO", nullable = false)
	private String roomNo;
	
	@FieldInfo(type = EFieldType.TEXT, order = 4, inEdit = false, inView = false)
	@Column(name = "ROOM_TYPE_NAME", nullable = false)
	private String roomTypeName;
	
	@FieldInfo(type = EFieldType.INTEGER, min = 1, max = 999, order = 5, columnWidth = 120)
	@Column(name = "CAPACITY", nullable = false)
	private Integer capacity;
	
	@FieldInfo(type = EFieldType.TEXT, max = 256, order = 6, required = false, inEdit = false, inView = false)
	@Column(name = "EQUIPMENT")
	private String equipment;
	
	@FieldInfo(type = EFieldType.TEXT, isMemo = true, max = 128, order = 14, required = false)
	@Column(name = "DESCR")
	private String descr;
	
	public V_TEACHER_ROOM() {
	}

	public EMPLOYEE getTeacher() {
		return teacher;
	}

	public void setTeacher(EMPLOYEE teacher) {
		this.teacher = teacher;
	}

	public String getCorpusName() {
		return corpusName;
	}

	public void setCorpusName(String corpusName) {
		this.corpusName = corpusName;
	}

	public String getRoomNo() {
		return roomNo;
	}

	public void setRoomNo(String roomNo) {
		this.roomNo = roomNo;
	}

	public String getRoomTypeName() {
		return roomTypeName;
	}

	public void setRoomTypeName(String roomTypeName) {
		this.roomTypeName = roomTypeName;
	}

	public Integer getCapacity() {
		return capacity;
	}

	public void setCapacity(Integer capacity) {
		this.capacity = capacity;
	}

	public String getEquipment() {
		return equipment;
	}

	public void setEquipment(String equipment) {
		this.equipment = equipment;
	}

	public String getDescr() {
		return descr;
	}

	public void setDescr(String descr) {
		this.descr = descr;
	}
}
