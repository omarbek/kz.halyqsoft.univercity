package kz.halyqsoft.univercity.entity.beans.univercity;

import kz.halyqsoft.univercity.entity.beans.univercity.catalog.ROOM;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.SEMESTER_DATA;
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
 * @author Rakymzhan A. Kenzhegul
 * @created Jun 28, 2016 4:20:32 PM
 */
@Entity
public class EXAM_SCHEDULE extends AbstractEntity {

	private static final long serialVersionUID = -6668695185309004002L;

	@FieldInfo(type = EFieldType.FK_COMBO, order = 1)
    @ManyToOne
    @JoinColumns({
        @JoinColumn(name = "SEMESTER_DATA_ID", referencedColumnName = "ID")})
    private SEMESTER_DATA semesterData;
	
	@FieldInfo(type = EFieldType.FK_DIALOG, order = 3, readOnlyFixed = true)
    @ManyToOne
    @JoinColumns({
        @JoinColumn(name = "SUBJECT_ID", referencedColumnName = "ID")})
    private SEMESTER_SUBJECT subject;
	
	@FieldInfo(type = EFieldType.FK_DIALOG, order = 4)
    @ManyToOne
    @JoinColumns({
        @JoinColumn(name = "EXAMINER_ID", referencedColumnName = "ID")})
    private EMPLOYEE examiner;
	
	@FieldInfo(type = EFieldType.FK_DIALOG, order = 5)
    @ManyToOne
    @JoinColumns({
        @JoinColumn(name = "PROCTOR_ID", referencedColumnName = "ID")})
    private EMPLOYEE proctor;
	
	@FieldInfo(type = EFieldType.FK_DIALOG, order = 6)
    @ManyToOne
    @JoinColumns({
        @JoinColumn(name = "ROOM_ID", referencedColumnName = "ID")})
    private ROOM room;
	
	@FieldInfo(type = EFieldType.DATE, order = 7)
	@Column(name = "EXAM_DATE")
    @Temporal(TemporalType.DATE)
    private Date examDate;
	
	@FieldInfo(type = EFieldType.MASK, fieldMask = "##:##", max = 5, min = 5, order = 8)
	@Column(name = "BEGIN_TIME")
	private String beginTime;
	
	@FieldInfo(type = EFieldType.MASK, fieldMask = "##:##", max = 5, min = 5, order = 9)
	@Column(name = "END_TIME")
	private String endTime;
	
	@FieldInfo(type = EFieldType.DATETIME, order = 10, required = false, readOnlyFixed = true, inGrid = false, inEdit = false, inView = false)
	@Column(name = "CREATED")
    @Temporal(TemporalType.TIMESTAMP)
    private Date created;
	
	@FieldInfo(type = EFieldType.DATETIME, order = 11, required = false, readOnlyFixed = true, inGrid = false, inEdit = false, inView = false)
	@Column(name = "UPDATED")
    @Temporal(TemporalType.TIMESTAMP)
    private Date updated;
	
	@FieldInfo(type = EFieldType.BOOLEAN, order = 12, required = false, inEdit = false, inGrid = false, inView = false)
	@Column(name = "DELETED", nullable = false)
    private boolean deleted;
	
	@Column(name = "OLD_ID")
	private String oldId;
	
	public EXAM_SCHEDULE() {
	}

	public SEMESTER_DATA getSemesterData() {
		return semesterData;
	}

	public void setSemesterData(SEMESTER_DATA semesterData) {
		this.semesterData = semesterData;
	}

	public SEMESTER_SUBJECT getSubject() {
		return subject;
	}

	public void setSubject(SEMESTER_SUBJECT subject) {
		this.subject = subject;
	}

	public EMPLOYEE getExaminer() {
		return examiner;
	}

	public void setExaminer(EMPLOYEE examiner) {
		this.examiner = examiner;
	}

	public EMPLOYEE getProctor() {
		return proctor;
	}

	public void setProctor(EMPLOYEE proctor) {
		this.proctor = proctor;
	}

	public ROOM getRoom() {
		return room;
	}

	public void setRoom(ROOM room) {
		this.room = room;
	}

	public Date getExamDate() {
		return examDate;
	}

	public void setExamDate(Date examDate) {
		this.examDate = examDate;
	}

	public String getBeginTime() {
		return beginTime;
	}

	public void setBeginTime(String beginTime) {
		this.beginTime = beginTime;
	}

	public String getEndTime() {
		return endTime;
	}

	public void setEndTime(String endTime) {
		this.endTime = endTime;
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

	public boolean isDeleted() {
		return deleted;
	}

	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}

	public String getOldId() {
		return oldId;
	}

	public void setOldId(String oldId) {
		this.oldId = oldId;
	}
}
