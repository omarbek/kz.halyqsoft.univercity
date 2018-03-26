package kz.halyqsoft.univercity.entity.beans.univercity.view;

import org.r3a.common.entity.AbstractEntity;
import org.r3a.common.entity.EFieldType;
import org.r3a.common.entity.FieldInfo;

import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * @author Omarbek
 * @created Nov 28, 2016 3:34:02 PM
 */
@Entity
public class V_EXAM_SCHEDULE_SELECT extends AbstractEntity {

	private static final long serialVersionUID = -3236696085274882883L;
	
	@Column(name = "SUBJECT_ID", nullable = false)
	private BigInteger subjectId;

	@FieldInfo(type = EFieldType.TEXT, order = 2, inGrid = false)
	@Column(name = "SUBJECT_NAME_KZ", nullable = false)
	private String subjectNameKZ;
	
	@FieldInfo(type = EFieldType.TEXT_LATIN, order = 3, inGrid = false)
	@Column(name = "SUBJECT_NAME_EN", nullable = false)
	private String subjectNameEN;
	
	@FieldInfo(type = EFieldType.TEXT, order = 4)
	@Column(name = "SUBJECT_NAME_RU", nullable = false)
	private String subjectNameRU;
	
	@FieldInfo(type = EFieldType.TEXT, order = 5)
	@Column(name = "ROOM_NO", nullable = false)
	private String roomNo;
	
	@FieldInfo(type = EFieldType.DATE, order = 6, inGrid = false)
	@Column(name = "EXAM_DATE")
    @Temporal(TemporalType.DATE)
    private Date examDate;
	
	@FieldInfo(type = EFieldType.TEXT, order = 7)
	@Column(name = "BEGIN_TIME", nullable = false)
	private String beginTime;
	
	@FieldInfo(type = EFieldType.TEXT, order = 8)
	@Column(name = "END_TIME", nullable = false)
	private String endTime;
	
	public V_EXAM_SCHEDULE_SELECT() {
	}
	
	public BigInteger getSubjectId() {
		return subjectId;
	}

	public void setSubjectId(BigInteger subjectId) {
		this.subjectId = subjectId;
	}

	public String getSubjectNameKZ() {
		return subjectNameKZ;
	}

	public void setSubjectNameKZ(String subjectNameKZ) {
		this.subjectNameKZ = subjectNameKZ;
	}

	public String getSubjectNameEN() {
		return subjectNameEN;
	}

	public void setSubjectNameEN(String subjectNameEN) {
		this.subjectNameEN = subjectNameEN;
	}

	public String getSubjectNameRU() {
		return subjectNameRU;
	}

	public void setSubjectNameRU(String subjectNameRU) {
		this.subjectNameRU = subjectNameRU;
	}

	public String getRoomNo() {
		return roomNo;
	}

	public void setRoomNo(String roomNo) {
		this.roomNo = roomNo;
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
	
	public String toString(Locale locale) {
		StringBuilder sb = new StringBuilder();
		if (locale.getLanguage().equals("kk")) {
			sb.append(subjectNameKZ);
		} else if (locale.getLanguage().equals("en")) {
			sb.append(subjectNameEN);
		} else {
			sb.append(subjectNameRU);
		}
		
		sb.append(", ");
		sb.append(roomNo);
		sb.append(", ");
		sb.append(new SimpleDateFormat("dd.MM.yyyy").format(examDate));
		sb.append(", ");
		sb.append(beginTime);
		
		return sb.toString();
	}
}
