package kz.halyqsoft.univercity.entity.beans.univercity.catalog;

import org.r3a.common.entity.AbstractEntity;
import org.r3a.common.entity.EFieldType;
import org.r3a.common.entity.FieldInfo;

import javax.persistence.*;

/**
 * @author Omarbek
 * @created on 14.07.2018
 */
@Entity
public class LESSON_TIME extends AbstractEntity {

    @FieldInfo(type = EFieldType.INTEGER, min = 1, max = 2)
    @Column(name = "LESSON_NUMBER", nullable = false)
    private Integer lessonNumber;

    @FieldInfo(type = EFieldType.FK_COMBO, order = 2)
    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "BEGIN_TIME_ID", referencedColumnName = "ID")})
    private TIME beginTime;

    @FieldInfo(type = EFieldType.FK_COMBO, order = 3)
    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "END_TIME_ID", referencedColumnName = "ID")})
    private TIME endTime;

    @FieldInfo(type = EFieldType.FK_COMBO, order = 4)
    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "SHIFT_ID", referencedColumnName = "ID")})
    private SHIFT shift;

    public Integer getLessonNumber() {
        return lessonNumber;
    }

    public void setLessonNumber(Integer lessonNumber) {
        this.lessonNumber = lessonNumber;
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

    public SHIFT getShift() {
        return shift;
    }

    public void setShift(SHIFT shift) {
        this.shift = shift;
    }

    @Override
    public String toString() {
        return beginTime.toString() + "-" + endTime.toString();
    }
}
