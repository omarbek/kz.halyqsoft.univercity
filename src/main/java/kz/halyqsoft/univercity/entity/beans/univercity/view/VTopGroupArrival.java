package kz.halyqsoft.univercity.entity.beans.univercity.view;

import org.r3a.common.entity.AbstractEntity;
import org.r3a.common.entity.EFieldType;
import org.r3a.common.entity.FieldInfo;

public class VTopGroupArrival extends AbstractEntity {

    @FieldInfo(type = EFieldType.TEXT , order=1)
    private String group;

    @FieldInfo(type = EFieldType.TEXT, order=2 )
    private String teacher;

    @FieldInfo(type = EFieldType.INTEGER, order=3 )
    private long sumOfStudent;

    @FieldInfo(type = EFieldType.INTEGER, order=4 )
    private long attend;

    @FieldInfo(type = EFieldType.DOUBLE, order=5 )
    private double percent;

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getTeacher() {
        return teacher;
    }

    public void setTeacher(String teacher) {
        this.teacher = teacher;
    }

    public long getSumOfStudent() {
        return sumOfStudent;
    }

    public void setSumOfStudent(long sumOfStudent) {
        this.sumOfStudent = sumOfStudent;
    }

    public long getAttend() {
        return attend;
    }

    public void setAttend(long attend) {
        this.attend = attend;
    }

    public double getPercent() {
        return percent;
    }

    public void setPercent(double percent) {
        this.percent = percent;
    }
}
