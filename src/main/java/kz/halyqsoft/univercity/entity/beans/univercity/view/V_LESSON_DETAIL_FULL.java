package kz.halyqsoft.univercity.entity.beans.univercity.view;

import kz.halyqsoft.univercity.entity.beans.univercity.EMPLOYEE;
import kz.halyqsoft.univercity.entity.beans.univercity.LESSON;
import kz.halyqsoft.univercity.entity.beans.univercity.SCHEDULE_DETAIL;
import kz.halyqsoft.univercity.entity.beans.univercity.STUDENT;
import kz.halyqsoft.univercity.entity.beans.univercity.STUDENT_EDUCATION;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.LESSON_TYPE;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.ROOM;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.SUBJECT;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.WEEK_DAY;
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
 * @created Aug 10, 2016 10:54:02 AM
 */
@Entity
public class V_LESSON_DETAIL_FULL extends AbstractEntity {

	private static final long serialVersionUID = -6840584424310857845L;

	@FieldInfo(type = EFieldType.FK_COMBO, order = 1)
    @ManyToOne
    @JoinColumns({
        @JoinColumn(name = "LESSON_ID", referencedColumnName = "ID")})
    private LESSON lesson;
	
	@FieldInfo(type = EFieldType.FK_COMBO, order = 2)
    @ManyToOne
    @JoinColumns({
        @JoinColumn(name = "SCHEDULE_DETAIL_ID", referencedColumnName = "ID")})
    private SCHEDULE_DETAIL scheduleDetail;
	
	@FieldInfo(type = EFieldType.FK_DIALOG, order = 3)
    @ManyToOne
    @JoinColumns({
        @JoinColumn(name = "SUBJECT_ID", referencedColumnName = "ID")})
    private SUBJECT subject;
	
	@FieldInfo(type = EFieldType.TEXT, order = 4)
	@Column(name = "SUBJECT_NAME_KZ", nullable = false)
	private String subjectNameKZ;
	
	@FieldInfo(type = EFieldType.TEXT, order = 5)
	@Column(name = "SUBJECT_NAME_EN", nullable = false)
	private String subjectNameEN;
	
	@FieldInfo(type = EFieldType.TEXT, order = 6)
	@Column(name = "SUBJECT_NAME_RU", nullable = false)
	private String subjectNameRU;
	
	@FieldInfo(type = EFieldType.TEXT, order = 7)
	@Column(name = "SUBJECT_CODE", nullable = false)
	private String subjectCode;
	
	@FieldInfo(type = EFieldType.FK_COMBO, order = 8)
    @ManyToOne
    @JoinColumns({
        @JoinColumn(name = "LESSON_TYPE_ID", referencedColumnName = "ID")})
    private LESSON_TYPE lessonType;
	
	@FieldInfo(type = EFieldType.TEXT, order = 9)
	@Column(name = "LESSON_TYPE_NAME", nullable = false)
	private String lessonTypeName;
	
	@FieldInfo(type = EFieldType.TEXT, order = 10)
	@Column(name = "LESSON_TYPE_SHORT_NAME", nullable = false)
	private String lessonTypeShortName;
	
	@FieldInfo(type = EFieldType.FK_DIALOG, order = 11)
    @ManyToOne
    @JoinColumns({
        @JoinColumn(name = "TEACHER_ID", referencedColumnName = "ID")})
    private EMPLOYEE teacher;
	
	@FieldInfo(type = EFieldType.TEXT, order = 12)
	@Column(name = "TEACHER_FIO", nullable = false)
	private String teacherFIO;
	
	@FieldInfo(type = EFieldType.FK_DIALOG, order = 13)
    @ManyToOne
    @JoinColumns({
        @JoinColumn(name = "WEEK_DAY_ID", referencedColumnName = "ID")})
    private WEEK_DAY weekDay;
	
	@FieldInfo(type = EFieldType.TEXT, order = 14)
	@Column(name = "WEEK_DAY_NAME", nullable = false)
	private String weekDayName;
	
	@FieldInfo(type = EFieldType.TEXT, order = 15)
	@Column(name = "WEEK_DAY_SHORT_NAME", nullable = false)
	private String weekDayShortName;
	
	//DAY_HOUR_ID[bigint]
	//TIME_PERIOD[char(11 octets)]
	//
	
	
	
//	//netu
//	@FieldInfo(type = EFieldType.TEXT, order = 17)
//	@Column(name = "START_TIME_NAME", nullable = false)
//	private String startTimeName;
	
	@FieldInfo(type = EFieldType.FK_DIALOG, order = 18)
    @ManyToOne
    @JoinColumns({
        @JoinColumn(name = "ROOM_ID", referencedColumnName = "ID")})
    private ROOM room;
	
	@FieldInfo(type = EFieldType.TEXT, order = 19)
	@Column(name = "ROOM_NO", nullable = false)
	private String roomNo;
	
	@FieldInfo(type = EFieldType.DATE, order = 20)
	@Column(name = "LESSON_DATE")
    @Temporal(TemporalType.DATE)
    private Date lessonDate;
	
	@FieldInfo(type = EFieldType.DATE, order = 21)
	@Column(name = "LESSON_BEGIN_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    private Date lessonBeginDate;
	
	@FieldInfo(type = EFieldType.DATE, order = 22)
	@Column(name = "LESSON_FINISH_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    private Date lessonFinishDate;
	
	@FieldInfo(type = EFieldType.BOOLEAN, order = 23)
	@Column(name = "CANCELED", nullable = false)
    private boolean canceled;
	
	@FieldInfo(type = EFieldType.TEXT, isMemo = true, order = 24)
	@Column(name = "CANCEL_REASON")
	private String cancelReason;
	
	@FieldInfo(type = EFieldType.FK_COMBO, order = 25)
    @ManyToOne
    @JoinColumns({
        @JoinColumn(name = "STUDENT_EDUCATION_ID", referencedColumnName = "ID")})
    private STUDENT_EDUCATION studentEducaion;
	
	@FieldInfo(type = EFieldType.FK_COMBO, order = 26)
    @ManyToOne
    @JoinColumns({
        @JoinColumn(name = "STUDENT_ID", referencedColumnName = "ID")})
    private STUDENT student;
	
	@FieldInfo(type = EFieldType.TEXT, order = 27)
	@Column(name = "STUDENT_FIO", nullable = false)
	private String studentFIO;
	
	@FieldInfo(type = EFieldType.INTEGER, min = 1, max = 3, order = 28)
	@Column(name = "ATTENDANCE_MARK", nullable = false)
    private Integer attendanceMark;
	
	
//	//netu
//	@FieldInfo(type = EFieldType.TEXT, order = 29)
//	@Column(name = "VISIT_MARK_TYPE_NAME", nullable = false)
//	private String visitMarkTypeName;
	
	@FieldInfo(type = EFieldType.INTEGER, order = 30)
	@Column(name = "GRADE", nullable = false)
	private Integer grade;
	
	public V_LESSON_DETAIL_FULL() {
	}

	public LESSON getLesson() {
		return lesson;
	}

	public void setLesson(LESSON lesson) {
		this.lesson = lesson;
	}

	public SCHEDULE_DETAIL getScheduleDetail() {
		return scheduleDetail;
	}

	public void setScheduleDetail(SCHEDULE_DETAIL scheduleDetail) {
		this.scheduleDetail = scheduleDetail;
	}

	public SUBJECT getSubject() {
		return subject;
	}

	public void setSubject(SUBJECT subject) {
		this.subject = subject;
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

	public String getSubjectCode() {
		return subjectCode;
	}

	public void setSubjectCode(String subjectCode) {
		this.subjectCode = subjectCode;
	}

	public LESSON_TYPE getLessonType() {
		return lessonType;
	}

	public void setLessonType(LESSON_TYPE lessonType) {
		this.lessonType = lessonType;
	}

	public String getLessonTypeName() {
		return lessonTypeName;
	}

	public void setLessonTypeName(String lessonTypeName) {
		this.lessonTypeName = lessonTypeName;
	}

	public String getLessonTypeShortName() {
		return lessonTypeShortName;
	}

	public void setLessonTypeShortName(String lessonTypeShortName) {
		this.lessonTypeShortName = lessonTypeShortName;
	}

	public EMPLOYEE getTeacher() {
		return teacher;
	}

	public void setTeacher(EMPLOYEE teacher) {
		this.teacher = teacher;
	}

	public String getTeacherFIO() {
		return teacherFIO;
	}

	public void setTeacherFIO(String teacherFIO) {
		this.teacherFIO = teacherFIO;
	}

	public WEEK_DAY getWeekDay() {
		return weekDay;
	}

	public void setWeekDay(WEEK_DAY weekDay) {
		this.weekDay = weekDay;
	}

	public String getWeekDayName() {
		return weekDayName;
	}

	public void setWeekDayName(String weekDayName) {
		this.weekDayName = weekDayName;
	}

	public String getWeekDayShortName() {
		return weekDayShortName;
	}

	public void setWeekDayShortName(String weekDayShortName) {
		this.weekDayShortName = weekDayShortName;
	}

//	public String getStartTimeName() {
//		return startTimeName;
//	}
//
//	public void setStartTimeName(String startTimeName) {
//		this.startTimeName = startTimeName;
//	}

	public ROOM getRoom() {
		return room;
	}

	public void setRoom(ROOM room) {
		this.room = room;
	}

	public String getRoomNo() {
		return roomNo;
	}

	public void setRoomNo(String roomNo) {
		this.roomNo = roomNo;
	}

	public Date getLessonDate() {
		return lessonDate;
	}

	public void setLessonDate(Date lessonDate) {
		this.lessonDate = lessonDate;
	}

	public Date getLessonBeginDate() {
		return lessonBeginDate;
	}

	public void setLessonBeginDate(Date lessonBeginDate) {
		this.lessonBeginDate = lessonBeginDate;
	}

	public Date getLessonFinishDate() {
		return lessonFinishDate;
	}

	public void setLessonFinishDate(Date lessonFinishDate) {
		this.lessonFinishDate = lessonFinishDate;
	}

	public boolean isCanceled() {
		return canceled;
	}

	public void setCanceled(boolean canceled) {
		this.canceled = canceled;
	}

	public String getCancelReason() {
		return cancelReason;
	}

	public void setCancelReason(String cancelReason) {
		this.cancelReason = cancelReason;
	}

	public STUDENT_EDUCATION getStudentEducaion() {
		return studentEducaion;
	}

	public void setStudentEducaion(STUDENT_EDUCATION studentEducaion) {
		this.studentEducaion = studentEducaion;
	}

	public STUDENT getStudent() {
		return student;
	}

	public void setStudent(STUDENT student) {
		this.student = student;
	}

	public String getStudentFIO() {
		return studentFIO;
	}

	public void setStudentFIO(String studentFIO) {
		this.studentFIO = studentFIO;
	}

	public Integer getAttendanceMark() {
		return attendanceMark;
	}

	public void setAttendanceMark(Integer attendanceMark) {
		this.attendanceMark = attendanceMark;
	}

//	public String getVisitMarkTypeName() {
//		return visitMarkTypeName;
//	}
//
//	public void setVisitMarkTypeName(String visitMarkTypeName) {
//		this.visitMarkTypeName = visitMarkTypeName;
//	}

	public Integer getGrade() {
		return grade;
	}

	public void setGrade(Integer grade) {
		this.grade = grade;
	}
}
