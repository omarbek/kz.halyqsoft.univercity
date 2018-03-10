package kz.halyqsoft.univercity.entity.beans.univercity.view;

import org.r3a.common.entity.AbstractEntity;
import org.r3a.common.entity.EFieldType;
import org.r3a.common.entity.FieldInfo;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * @author Rakymzhan A. Kenzhegul
 * @created Jun 30, 2016 10:15:53 AM
 */
@Entity
public class V_EXAM_SCHEDULE_SUM extends AbstractEntity {
	
	private static final long serialVersionUID = 8483058656460636025L;

	@FieldInfo(type = EFieldType.TEXT, order = 10)
	@Column(name = "EXAMINER_LAST_NAME")
	private String examinerLastName;

	@FieldInfo(type = EFieldType.TEXT, order = 11)
	@Column(name = "EXAMINER_FIRST_NAME")
	private String examinerFirstName;
	
	@FieldInfo(type = EFieldType.TEXT, order = 13)
	@Column(name = "EXAMINER_MIDDLE_NAME")
	private String examinerMiddleName;
	
	@FieldInfo(type = EFieldType.TEXT, order = 15)
	@Column(name = "PROCTOR_LAST_NAME")
	private String proctorLastName;
	
	@FieldInfo(type = EFieldType.TEXT, order = 16)
	@Column(name = "PROCTOR_FIRST_NAME")
	private String proctorFirstName;
	
	@FieldInfo(type = EFieldType.TEXT, order = 17)
	@Column(name = "PROCTOR_MIDDLE_NAME")
	private String proctorMiddleName;
	
	@FieldInfo(type = EFieldType.TEXT, order = 19)
	@Column(name = "ROOM_NO")
	private String roomNo;
	
	@FieldInfo(type = EFieldType.DATE, order = 20)
	@Column(name = "EXAM_DATE")
    @Temporal(TemporalType.DATE)
    private Date examDate;
	
	@FieldInfo(type = EFieldType.MASK, fieldMask = "##:##", max = 5, min = 5, order = 21)
	@Column(name = "BEGIN_TIME", nullable = false)
	private String beginTime;
	
	@FieldInfo(type = EFieldType.MASK, fieldMask = "##:##", max = 5, min = 5, order = 22)
	@Column(name = "END_TIME", nullable = false)
	private String endTime;
	
	@FieldInfo(type = EFieldType.INTEGER, order = 26)
	@Column(name = "STUDENT_COUNT", nullable = false)
	private Integer studentCount;
	
	public V_EXAM_SCHEDULE_SUM() {
	}

	public String getExaminerLastName() {
		return examinerLastName;
	}

	public void setExaminerLastName(String examinerLastName) {
		this.examinerLastName = examinerLastName;
	}

	public String getExaminerFirstName() {
		return examinerFirstName;
	}

	public void setExaminerFirstName(String examinerFirstName) {
		this.examinerFirstName = examinerFirstName;
	}

	public String getExaminerMiddleName() {
		return examinerMiddleName;
	}

	public void setExaminerMiddleName(String examinerMiddleName) {
		this.examinerMiddleName = examinerMiddleName;
	}

	public String getProctorLastName() {
		return proctorLastName;
	}

	public void setProctorLastName(String proctorLastName) {
		this.proctorLastName = proctorLastName;
	}

	public String getProctorFirstName() {
		return proctorFirstName;
	}

	public void setProctorFirstName(String proctorFirstName) {
		this.proctorFirstName = proctorFirstName;
	}

	public String getProctorMiddleName() {
		return proctorMiddleName;
	}

	public void setProctorMiddleName(String proctorMiddleName) {
		this.proctorMiddleName = proctorMiddleName;
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

	public Integer getStudentCount() {
		return studentCount;
	}

	public void setStudentCount(Integer studentCount) {
		this.studentCount = studentCount;
	}
}
