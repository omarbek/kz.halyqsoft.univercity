package kz.halyqsoft.univercity.entity.beans.univercity.catalog;

import kz.halyqsoft.univercity.entity.beans.univercity.view.V_ADVISOR;
import org.r3a.common.entity.AbstractEntity;
import org.r3a.common.entity.EFieldType;
import org.r3a.common.entity.FieldInfo;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * @author Omarbek
 * Created Dec 24, 2015 4:56:28 PM
 */
@Entity
public class ROOM extends AbstractEntity {

	private static final long serialVersionUID = -3807935418223922884L;

	@FieldInfo(type = EFieldType.FK_COMBO, order = 1)
    @ManyToOne
    @JoinColumns({
        @JoinColumn(name = "CORPUS_ID", referencedColumnName = "ID")})
    private CORPUS corpus;
	
	@FieldInfo(type = EFieldType.TEXT, max = 32, order = 2)
	@Column(name = "ROOM_NO", nullable = false)
	private String roomNo;
	
	@FieldInfo(type = EFieldType.FK_COMBO, order = 3)
    @ManyToOne
    @JoinColumns({
        @JoinColumn(name = "ROOM_TYPE_ID", referencedColumnName = "ID")})
    private ROOM_TYPE roomType;
	
	@FieldInfo(type = EFieldType.INTEGER, min = 1, max = 999, order = 4)
	@Column(name = "CAPACITY", nullable = false)
	private Integer capacity;
	
	@FieldInfo(type = EFieldType.TEXT, max = 256, order = 5, required = false, inEdit = false, inView = false)
	@Column(name = "EQUIPMENT")
	private String equipment;
	
	@FieldInfo(type = EFieldType.FK_DIALOG, order = 6, required = false)
    @ManyToOne
    @JoinColumns({
        @JoinColumn(name = "RESP_EMP_ID", referencedColumnName = "ID")})
    private V_ADVISOR respEmployee;
	
	@FieldInfo(type = EFieldType.TEXT, isMemo = true, max = 128, order = 8, required = false)
	@Column(name = "DESCR")
	private String descr;
	
	@FieldInfo(type = EFieldType.BOOLEAN, order = 9, required = false, inEdit = false, inGrid = false, inView = false)
	@Column(name = "DELETED", nullable = false)
    private boolean deleted;
	
	@FieldInfo(type = EFieldType.DATETIME, order = 10, required = false, readOnlyFixed = true, inGrid = false, inEdit = false, inView = false)
	@Column(name = "CREATED")
    @Temporal(TemporalType.TIMESTAMP)
    private Date created;
	
	@FieldInfo(type = EFieldType.DATETIME, order = 11, required = false, readOnlyFixed = true, inGrid = false, inEdit = false, inView = false)
	@Column(name = "UPDATED")
    @Temporal(TemporalType.TIMESTAMP)
    private Date updated;
	
	public ROOM() {
	}

	public CORPUS getCorpus() {
		return corpus;
	}

	public void setCorpus(CORPUS corpus) {
		this.corpus = corpus;
	}

	public String getRoomNo() {
		return roomNo;
	}

	public void setRoomNo(String roomNo) {
		this.roomNo = roomNo;
	}

	public ROOM_TYPE getRoomType() {
		return roomType;
	}

	public void setRoomType(ROOM_TYPE roomType) {
		this.roomType = roomType;
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

	public V_ADVISOR getRespEmployee() {
		return respEmployee;
	}

	public void setRespEmployee(V_ADVISOR respEmployee) {
		this.respEmployee = respEmployee;
	}

	public String getDescr() {
		return descr;
	}

	public void setDescr(String descr) {
		this.descr = descr;
	}

	public boolean isDeleted() {
		return deleted;
	}

	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}

	public Date getCreated() {
		return created;
	}

	public void setCreated(Date created) {
		this.created = created;
	}

	public Date getUpdated() {
		return updated;
	}

	public void setUpdated(Date updated) {
		this.updated = updated;
	}

	@Override
	public String toString() {
		return roomNo;
	}
}
