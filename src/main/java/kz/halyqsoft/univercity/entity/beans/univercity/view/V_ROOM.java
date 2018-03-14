package kz.halyqsoft.univercity.entity.beans.univercity.view;

import kz.halyqsoft.univercity.entity.beans.univercity.catalog.CORPUS;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.ROOM_TYPE;
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
 * @@author Omarbek
 * @created Jan 6, 2016 9:38:38 AM
 */
@Entity
public class V_ROOM extends AbstractEntity {

	private static final long serialVersionUID = 3965282724273774002L;

	@FieldInfo(type = EFieldType.FK_COMBO, order = 1, inGrid = false)
    @ManyToOne
    @JoinColumns({
        @JoinColumn(name = "CORPUS_ID", referencedColumnName = "ID")})
    private CORPUS corpus;
	
	@FieldInfo(type = EFieldType.TEXT, order = 2, inEdit = false, inView = false, columnWidth = 120)
	@Column(name = "CORPUS_NAME", nullable = false)
	private String corpusName;
	
	@FieldInfo(type = EFieldType.TEXT, max = 32, order = 3)
	@Column(name = "ROOM_NO", nullable = false)
	private String roomNo;
	
	@FieldInfo(type = EFieldType.FK_COMBO, order = 4, inGrid = false)
    @ManyToOne
    @JoinColumns({
        @JoinColumn(name = "ROOM_TYPE_ID", referencedColumnName = "ID")})
    private ROOM_TYPE roomType;
	
	@FieldInfo(type = EFieldType.TEXT, order = 5, inEdit = false, inView = false, columnWidth = 120)
	@Column(name = "ROOM_TYPE_NAME", nullable = false)
	private String roomTypeName;
	
	@FieldInfo(type = EFieldType.INTEGER, min = 1, max = 999, order = 6, columnWidth = 120)
	@Column(name = "CAPACITY", nullable = false)
	private Integer capacity;
	
	@FieldInfo(type = EFieldType.TEXT, max = 256, order = 7, required = false, inGrid = false, inEdit = false, inView = false)
	@Column(name = "EQUIPMENT")
	private String equipment;
	
	@FieldInfo(type = EFieldType.FK_DIALOG, order = 8, required = false, inGrid = false)
    @ManyToOne
    @JoinColumns({
        @JoinColumn(name = "RESP_EMP_ID", referencedColumnName = "ID")})
    private V_ADVISOR respEmployee;
	
	@FieldInfo(type = EFieldType.TEXT, max = 256, order = 9, required = false, inGrid = false, inEdit = false, inView = false)
	@Column(name = "RESP_EMP_FIO")
	private String respEmployeeFIO;
	
	@FieldInfo(type = EFieldType.TEXT, isMemo = true, max = 128, order = 14, required = false, inGrid = false)
	@Column(name = "DESCR")
	private String descr;
	
	@FieldInfo(type = EFieldType.BOOLEAN, order = 15, required = false, inEdit = false, inGrid = false, inView = false)
	@Column(name = "DELETED", nullable = false)
    private boolean deleted;
	
	@FieldInfo(type = EFieldType.DATETIME, order = 16, required = false, readOnlyFixed = true, inGrid = false, inEdit = false, inView = false)
	@Column(name = "CREATED")
    @Temporal(TemporalType.TIMESTAMP)
    private Date created;
	
	@FieldInfo(type = EFieldType.DATETIME, order = 17, required = false, readOnlyFixed = true, inGrid = false, inEdit = false, inView = false)
	@Column(name = "UPDATED")
    @Temporal(TemporalType.TIMESTAMP)
    private Date updated;
	
	public V_ROOM() {
	}

	public CORPUS getCorpus() {
		return corpus;
	}

	public void setCorpus(CORPUS corpus) {
		this.corpus = corpus;
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

	public ROOM_TYPE getRoomType() {
		return roomType;
	}

	public void setRoomType(ROOM_TYPE roomType) {
		this.roomType = roomType;
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

	public V_ADVISOR getRespEmployee() {
		return respEmployee;
	}

	public void setRespEmployee(V_ADVISOR respEmployee) {
		this.respEmployee = respEmployee;
	}

	public String getRespEmployeeFIO() {
		return respEmployeeFIO;
	}

	public void setRespEmployeeFIO(String respEmployeeFIO) {
		this.respEmployeeFIO = respEmployeeFIO;
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
		StringBuilder sb = new StringBuilder();
		sb.append(corpusName);
		sb.append(", ");
		sb.append(roomNo);
		sb.append(", ");
		sb.append(roomTypeName);
		
		return sb.toString();
	}
}
