package kz.halyqsoft.univercity.entity.beans.univercity.view;

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
 * @created Jan 6, 2016 10:31:39 AM
 */
@Entity
public class V_ROOM_SUBJECT extends AbstractEntity {

	private static final long serialVersionUID = -3914313718252829631L;

	@FieldInfo(type = EFieldType.FK_DIALOG, order = 1, inGrid = false, inEdit = false, inView = false)
    @ManyToOne
    @JoinColumns({
        @JoinColumn(name = "ROOM_ID", referencedColumnName = "ID")})
    private ROOM room;
	
	@FieldInfo(type = EFieldType.FK_DIALOG, order = 2, inGrid = false)
    @ManyToOne
    @JoinColumns({
        @JoinColumn(name = "SUBJECT_ID", referencedColumnName = "ID")})
    private V_SUBJECT_SELECT subject;
	
	@FieldInfo(type = EFieldType.TEXT, max = 64, order = 3, inEdit = false, inView = false)
	@Column(name = "SUBJECT_NAME_RU", nullable = false)
	private String subjectNameRU;
	
	@FieldInfo(type = EFieldType.TEXT, max = 8, order = 4, inEdit = false, inView = false)
	@Column(name = "SUBJECT_CODE", nullable = false)
	private String subjectCode;
	
	@FieldInfo(type = EFieldType.TEXT, max = 8, order = 5, inEdit = false, inView = false)
	@Column(name = "CHAIR_NAME", nullable = false)
	private String chairName;
	
	@FieldInfo(type = EFieldType.TEXT, max = 8, order = 6, inEdit = false, inView = false)
	@Column(name = "LEVEL_NAME", nullable = false)
	private String levelName;
	
	@FieldInfo(type = EFieldType.INTEGER, max = 8, order = 7, inEdit = false, inView = false)
	@Column(name = "CREDIT", nullable = false)
	private Integer credit;
	
	@FieldInfo(type = EFieldType.TEXT, max = 8, order = 8, inEdit = false, inView = false)
	@Column(name = "CONTROL_TYPE_NAME", nullable = false)
	private String controlTypeName;
	
	public V_ROOM_SUBJECT() {
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

	public String getSubjectNameRU() {
		return subjectNameRU;
	}

	public void setSubjectNameRU(String subjectNameRU) {
		this.subjectNameRU = subjectNameRU;
	}

	public String getSubjectCode() {
		return subjectCode;
	}

	public void setSubjectCode(String subjectCode) {
		this.subjectCode = subjectCode;
	}

	public String getChairName() {
		return chairName;
	}

	public void setChairName(String chairName) {
		this.chairName = chairName;
	}

	public String getLevelName() {
		return levelName;
	}

	public void setLevelName(String levelName) {
		this.levelName = levelName;
	}

	public Integer getCredit() {
		return credit;
	}

	public void setCredit(Integer credit) {
		this.credit = credit;
	}

	public String getControlTypeName() {
		return controlTypeName;
	}

	public void setControlTypeName(String controlTypeName) {
		this.controlTypeName = controlTypeName;
	}
}
