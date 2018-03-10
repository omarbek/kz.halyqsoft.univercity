package kz.halyqsoft.univercity.entity.beans.univercity.view;

import kz.halyqsoft.univercity.entity.beans.univercity.EMPLOYEE;
import kz.halyqsoft.univercity.entity.beans.univercity.SCHEDULE;
import kz.halyqsoft.univercity.entity.beans.univercity.SCHEDULE_DETAIL;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.DEPARTMENT;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.ENTRANCE_YEAR;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.LANGUAGE;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.LESSON_TYPE;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.ROOM;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.SEMESTER_PERIOD;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.SUBJECT;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.WEEK_DAY;
import org.r3a.common.entity.AbstractEntity;
import org.r3a.common.entity.EFieldType;
import org.r3a.common.entity.FieldInfo;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;

/**
 * @author Kairat A. Gatiyatov
 * @created 11 ��� 2016 �. 15:11:29
 */

@SuppressWarnings("serial")
@Entity

public class V_STUDENT_SCHEDULE extends AbstractEntity {
	
	@FieldInfo(type = EFieldType.TEXT, order = 2, required = false, inEdit = false, inView = false)
	@Column(name = "TYPE_NAME", insertable = false, updatable = false)
	private String typeName;
	
	@FieldInfo(type = EFieldType.TEXT, order = 3, required = false, inEdit = false, inView = false)
	@Column(name = "DAY_NAME", insertable = false, updatable = false)
	private String dayName;
	
	@FieldInfo(type = EFieldType.TEXT, order = 4, required = false, inEdit = false, inView = false)
	@Column(name = "START_TIME_NAME", insertable = false, updatable = false)
	private String startTimeName;
	
	@FieldInfo(type = EFieldType.TEXT, order = 5, required = false, inEdit = false, inView = false)
	@Column(name = "ROOM_NO", insertable = false, updatable = false)
	private String roomNo;
	
	@FieldInfo(type = EFieldType.TEXT, order = 6, required = false, inEdit = false, inView = false)
	@Column(name = "FIRST_NAME", insertable = false, updatable = false)
	private String firstName;
	
	@FieldInfo(type = EFieldType.TEXT, order = 7, required = false, inEdit = false, inView = false)
	@Column(name = "LAST_NAME", insertable = false, updatable = false)
	private String lastName;
	
	@FieldInfo(type = EFieldType.FK_COMBO, inGrid = false, order = 8, required = false, inEdit = false, inView = false)
    @ManyToOne
    @JoinColumns({
        @JoinColumn(name = "YEAR_ID", referencedColumnName = "ID", insertable = false, updatable = false)})
    private ENTRANCE_YEAR yearId;
	
	@FieldInfo(type = EFieldType.FK_COMBO, inGrid = false, order = 9, required = false)
    @ManyToOne
    @JoinColumns({
        @JoinColumn(name = "SEMESTER_PERIOD_ID", referencedColumnName = "ID", insertable = false, updatable = false)})
    private SEMESTER_PERIOD semesterPeriodId;
	
	@FieldInfo(type = EFieldType.FK_COMBO, inGrid = false, order = 10, required = false, inEdit = false, inView = false)
    @ManyToOne
    @JoinColumns({
        @JoinColumn(name = "CHAIR_ID", referencedColumnName = "ID", insertable = false, updatable = false)})
    private DEPARTMENT chairId;
	
	@FieldInfo(type = EFieldType.FK_COMBO, inGrid = false, order = 11, required = false)
    @ManyToOne
    @JoinColumns({
        @JoinColumn(name = "SUBJECT_ID", referencedColumnName = "ID", insertable = false, updatable = false)})
    private SUBJECT subjectId;
	
	@FieldInfo(type = EFieldType.FK_COMBO, inGrid = false, order = 12, required = false, inEdit = false, inView = false)
    @ManyToOne
    @JoinColumns({
        @JoinColumn(name = "LESSON_TYPE_ID", referencedColumnName = "ID", insertable = false, updatable = false)})
    private LESSON_TYPE lessonTypeId;
	
	@FieldInfo(type = EFieldType.FK_COMBO, inGrid = false, order = 13, required = false, inEdit = false, inView = false)
    @ManyToOne
    @JoinColumns({
        @JoinColumn(name = "TEACHER_ID", referencedColumnName = "ID", insertable = false, updatable = false)})
    private EMPLOYEE teacherId;
	
	@FieldInfo(type = EFieldType.FK_COMBO, inGrid = false, order = 14, required = false, inEdit = false, inView = false)
    @ManyToOne
    @JoinColumns({
        @JoinColumn(name = "WEEK_DAY_ID", referencedColumnName = "ID", insertable = false, updatable = false)})
    private WEEK_DAY weekDayId;
	
	@FieldInfo(type = EFieldType.FK_COMBO, inGrid = false, order = 16, required = false, inEdit = false, inView = false)
    @ManyToOne
    @JoinColumns({
        @JoinColumn(name = "ROOM_ID", referencedColumnName = "ID", insertable = false, updatable = false)})
    private ROOM roomId;
	
	@FieldInfo(type = EFieldType.FK_COMBO, inGrid = false, order = 17, required = false, inEdit = false, inView = false)
    @ManyToOne
    @JoinColumns({
        @JoinColumn(name = "LANGUAGE_ID", referencedColumnName = "ID", insertable = false, updatable = false)})
    private LANGUAGE languageId;
	
	@FieldInfo(type = EFieldType.FK_COMBO, inGrid = false, order = 18, required = false, inEdit = false, inView = false)
    @ManyToOne
    @JoinColumns({
        @JoinColumn(name = "SCHEDULE_ID", referencedColumnName = "ID", insertable = false, updatable = false)})
    private SCHEDULE schedule;
	
	@FieldInfo(type = EFieldType.FK_COMBO, inGrid = false, order = 19, required = false, inEdit = false, inView = false)
    @ManyToOne
    @JoinColumns({
        @JoinColumn(name = "ID", referencedColumnName = "ID", insertable = false, updatable = false)})
    private SCHEDULE_DETAIL scheduleDetail;
	
	@FieldInfo(type = EFieldType.TEXT, order = 1, required = false, inEdit = false, inView = false)
	@Column(name = "NAME_RU", insertable = false, updatable = false)
	private String nameSubject;
	
	@FieldInfo(type = EFieldType.TEXT, order = 20, required = false, inEdit = false, inView = false)
	@Column(name = "PERIOD_NAME", insertable = false, updatable = false)
	private String periodName;
	
	public V_STUDENT_SCHEDULE() {
	}

	public String getPeriodName() {
		return periodName;
	}

	public void setPeriodName(String periodName) {
		this.periodName = periodName;
	}
	
	public String getTypeName() {
		return typeName;
	}

	public void setTypeName(String typeName) {
		this.typeName = typeName;
	}

	public String getNameSubject() {
		return nameSubject;
	}

	public void setNameSubject(String nameSubject) {
		this.nameSubject = nameSubject;
	}
	
	public String getDayName() {
		return dayName;
	}

	public void setDayName(String dayName) {
		this.dayName = dayName;
	}

	public String getStartTimeName() {
		return startTimeName;
	}

	public void setStartTimeName(String startTimeName) {
		this.startTimeName = startTimeName;
	}

	public String getRoomNo() {
		return roomNo;
	}

	public void setRoomNo(String roomNo) {
		this.roomNo = roomNo;
	}
	
	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	
	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	
	public ENTRANCE_YEAR getYearId() {
		return yearId;
	}

	public void setYearId(ENTRANCE_YEAR yearId) {
		this.yearId = yearId;
	}
	
	public SEMESTER_PERIOD getSemesterPeriodId() {
		return semesterPeriodId;
	}

	public void setSemesterPeriodId(SEMESTER_PERIOD semesterPeriodId) {
		this.semesterPeriodId = semesterPeriodId;
	}

	public DEPARTMENT getChairId() {
		return chairId;
	}

	public void setChairId(DEPARTMENT chairId) {
		this.chairId = chairId;
	}
	
	public SUBJECT getSubjectId() {
		return subjectId;
	}

	public void setSubjectId(SUBJECT subjectId) {
		this.subjectId = subjectId;
	}
	
	public LESSON_TYPE getLessonTypeId() {
		return lessonTypeId;
	}

	public void setLessonTypeId(LESSON_TYPE lessonTypeId) {
		this.lessonTypeId = lessonTypeId;
	}
	
	public EMPLOYEE getTeacherId() {
		return teacherId;
	}

	public void setTeacherId(EMPLOYEE teacherId) {
		this.teacherId = teacherId;
	}
	
	public WEEK_DAY getWeekDayId() {
		return weekDayId;
	}

	public void setWeekDayId(WEEK_DAY weekDayId) {
		this.weekDayId = weekDayId;
	}

	public ROOM getRoomId() {
		return roomId;
	}

	public void setRoomId(ROOM roomId) {
		this.roomId = roomId;
	}
	
	public LANGUAGE getLanguageId() {
		return languageId;
	}

	public void setLanguageId(LANGUAGE languageId) {
		this.languageId = languageId;
	}
	
	public SCHEDULE getSchedule() {
		return schedule;
	}

	public void setSchedule(SCHEDULE schedule) {
		this.schedule = schedule;
	}
	
	public SCHEDULE_DETAIL getScheduleDetail() {
		return scheduleDetail;
	}

	public void setSchedule(SCHEDULE_DETAIL scheduleDetail) {
		this.scheduleDetail = scheduleDetail;
	}

}
