package kz.halyqsoft.univercity.entity.beans.univercity;

import kz.halyqsoft.univercity.entity.beans.univercity.catalog.*;
import org.r3a.common.entity.AbstractEntity;

import javax.persistence.*;

/**
 * @author Omarbek
 * @created Apr 27, 2016 3:33:05 PM
 */
@Entity
public class SCHEDULE_DETAIL extends AbstractEntity {

	private static final long serialVersionUID = 1602572739711557773L;

	@ManyToOne
    @JoinColumns({
        @JoinColumn(name = "SUBJECT_ID", referencedColumnName = "ID")})
    private SUBJECT subject;
	
	@ManyToOne
    @JoinColumns({
        @JoinColumn(name = "LESSON_TYPE_ID", referencedColumnName = "ID")})
    private LESSON_TYPE lessonType;
	
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
			@JoinColumn(name = "LESSON_TIME_ID", referencedColumnName = "ID")})
	private LESSON_TIME lessonTime;

	@ManyToOne
    @JoinColumns({
        @JoinColumn(name = "ROOM_ID", referencedColumnName = "ID")})
    private ROOM room;

	@ManyToOne
	@JoinColumns({
			@JoinColumn(name = "SEMESTER_DATA_ID", referencedColumnName = "ID")})
	private SEMESTER_DATA semesterData;

	@ManyToOne
	@JoinColumns({
			@JoinColumn(name = "GROUP_ID",  referencedColumnName = "ID")})
	private GROUPS group;
	
	public SCHEDULE_DETAIL() {
	}

	public SUBJECT getSubject() {
		return subject;
	}

	public void setSubject(SUBJECT subject) {
		this.subject = subject;
	}

	public LESSON_TYPE getLessonType() {
		return lessonType;
	}

	public void setLessonType(LESSON_TYPE lessonType) {
		this.lessonType = lessonType;
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

	public ROOM getRoom() {
		return room;
	}

	public void setRoom(ROOM room) {
		this.room = room;
	}

	public SEMESTER_DATA getSemesterData() {
		return semesterData;
	}

	public void setSemesterData(SEMESTER_DATA semesterData) {
		this.semesterData = semesterData;
	}

	public LESSON_TIME getLessonTime() {
		return lessonTime;
	}

	public void setLessonTime(LESSON_TIME lessonTime) {
		this.lessonTime = lessonTime;
	}

	public GROUPS getGroup() {
		return group;
	}

	public void setGroup(GROUPS group) {
		this.group = group;
	}
}
