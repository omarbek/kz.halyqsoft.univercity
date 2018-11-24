package kz.halyqsoft.univercity.entity.beans.univercity;

import kz.halyqsoft.univercity.entity.beans.univercity.catalog.*;
import org.r3a.common.entity.AbstractEntity;
import org.r3a.common.entity.EFieldType;
import org.r3a.common.entity.FieldInfo;

import javax.persistence.*;

/**
 * @author Omarbek
 * @created Apr 27, 2016 3:33:05 PM
 */
@Entity
public class SCHEDULE_DETAIL extends AbstractEntity {

    private static final long serialVersionUID = 1602572739711557773L;

    @FieldInfo(type = EFieldType.FK_COMBO,order = 1)
    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "SUBJECT_ID", referencedColumnName = "ID")})
    private SUBJECT subject;

    @FieldInfo(type = EFieldType.FK_COMBO,order = 2)
    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "LESSON_TYPE_ID", referencedColumnName = "ID")})
    private LESSON_TYPE lessonType;

    @FieldInfo(type = EFieldType.FK_COMBO,order = 3)
    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "TEACHER_ID", referencedColumnName = "ID")})
    private EMPLOYEE teacher;


    @FieldInfo(type = EFieldType.FK_COMBO,order = 4 , inView = false, inEdit = false, inGrid = false)
    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "WEEK_DAY_ID", referencedColumnName = "ID")})
    private WEEK_DAY weekDay;

    @FieldInfo(type = EFieldType.FK_COMBO,order = 5)
    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "LESSON_TIME_ID", referencedColumnName = "ID")})
    private LESSON_TIME lessonTime;

    @FieldInfo(type = EFieldType.FK_COMBO,order = 6)
    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "ROOM_ID", referencedColumnName = "ID")})
    private ROOM room;


    @FieldInfo(type = EFieldType.FK_COMBO,order = 7, inGrid = false, inView = false,inEdit = false)
    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "SEMESTER_DATA_ID", referencedColumnName = "ID")})
    private SEMESTER_DATA semesterData;

    @FieldInfo(type = EFieldType.FK_COMBO,order = 8, required = false)
    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "GROUP_ID",  referencedColumnName = "ID")})
    private GROUPS group;

    @FieldInfo(type = EFieldType.FK_DIALOG,order = 9,required = false)
    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "STREAM_ID",  referencedColumnName = "ID")})
    private STREAM stream;

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

    public LESSON_TIME getLessonTime() {
        return lessonTime;
    }

    public void setLessonTime(LESSON_TIME lessonTime) {
        this.lessonTime = lessonTime;
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

    public GROUPS getGroup() {
        return group;
    }

    public void setGroup(GROUPS group) {
        this.group = group;
    }

    public STREAM getStream() {
        return stream;
    }

    public void setStream(STREAM stream) {
        this.stream = stream;
    }
}
