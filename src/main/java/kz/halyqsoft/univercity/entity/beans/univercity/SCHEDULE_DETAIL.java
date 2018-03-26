package kz.halyqsoft.univercity.entity.beans.univercity;

import kz.halyqsoft.univercity.entity.beans.univercity.catalog.LESSON_TYPE;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.ROOM;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.TIME;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.WEEK_DAY;
import org.r3a.common.entity.AbstractEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;

/**
 * @author Omarbek
 * @created Apr 27, 2016 3:33:05 PM
 */
@Entity
public class SCHEDULE_DETAIL extends AbstractEntity {

	private static final long serialVersionUID = 1602572739711557773L;

	@ManyToOne
    @JoinColumns({
        @JoinColumn(name = "SCHEDULE_ID", referencedColumnName = "ID")})
    private SCHEDULE schedule;
	
	@ManyToOne
    @JoinColumns({
        @JoinColumn(name = "SUBJECT_ID", referencedColumnName = "ID")})
    private SEMESTER_SUBJECT subject;
	
	@ManyToOne
    @JoinColumns({
        @JoinColumn(name = "LESSON_TYPE_ID", referencedColumnName = "ID")})
    private LESSON_TYPE lessonType;
	
	@Column(name = "LESSON_NAME")
	private String lessonName;
	
	@ManyToOne
    @JoinColumns({
        @JoinColumn(name = "TEACHER_ID", referencedColumnName = "ID")})
    private EMPLOYEE teacher;
	
	@ManyToOne
    @JoinColumns({
        @JoinColumn(name = "WEEK_DAY_ID", referencedColumnName = "ID")})
    private WEEK_DAY weekDay;
	
	@ManyToOne
    @JoinColumns({
        @JoinColumn(name = "BEGIN_TIME_ID", referencedColumnName = "ID")})
    private TIME beginTime;
	
	@ManyToOne
    @JoinColumns({
        @JoinColumn(name = "END_TIME_ID", referencedColumnName = "ID")})
    private TIME endTime;
	
	@ManyToOne
    @JoinColumns({
        @JoinColumn(name = "ROOM_ID", referencedColumnName = "ID")})
    private ROOM room;
	
	@Column(name = "OLD_ID")
	private String oldId;
	
	public SCHEDULE_DETAIL() {
	}

	public SCHEDULE getSchedule() {
		return schedule;
	}

	public void setSchedule(SCHEDULE schedule) {
		this.schedule = schedule;
	}

	public SEMESTER_SUBJECT getSubject() {
		return subject;
	}

	public void setSubject(SEMESTER_SUBJECT subject) {
		this.subject = subject;
	}

	public LESSON_TYPE getLessonType() {
		return lessonType;
	}

	public void setLessonType(LESSON_TYPE lessonType) {
		this.lessonType = lessonType;
	}

	public String getLessonName() {
		return lessonName;
	}

	public void setLessonName(String lessonName) {
		this.lessonName = lessonName;
	}

	public EMPLOYEE getTeacher() {
		return teacher;
	}

	public void setTeacher(EMPLOYEE teacher) {
		this.teacher = teacher;
	}

	public WEEK_DAY getWeekDay() {
		return weekDay;
	}

	public void setWeekDay(WEEK_DAY weekDay) {
		this.weekDay = weekDay;
	}

	public TIME getBeginTime() {
		return beginTime;
	}

	public void setBeginTime(TIME beginTime) {
		this.beginTime = beginTime;
	}

	public TIME getEndTime() {
		return endTime;
	}

	public void setEndTime(TIME endTime) {
		this.endTime = endTime;
	}

	public ROOM getRoom() {
		return room;
	}

	public void setRoom(ROOM room) {
		this.room = room;
	}

	public String getOldId() {
		return oldId;
	}

	public void setOldId(String oldId) {
		this.oldId = oldId;
	}
}
