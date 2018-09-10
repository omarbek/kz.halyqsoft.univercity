package kz.halyqsoft.univercity.entity.beans.univercity.view;

import kz.halyqsoft.univercity.entity.beans.USERS;
import org.r3a.common.entity.AbstractEntity;
import org.r3a.common.entity.EFieldType;
import org.r3a.common.entity.FieldInfo;

public class VLatecomers extends AbstractEntity {

    @FieldInfo(type= EFieldType.TEXT, order = 1)
    private String groupName;

    @FieldInfo(type = EFieldType.TEXT, order = 2)
    private String time;

    @FieldInfo(type = EFieldType.INTEGER, order = 4)
    private Long allStudents;

    @FieldInfo(type = EFieldType.INTEGER, order = 5)
    private Long isLate;

    @FieldInfo(type = EFieldType.INTEGER, order = 6, inGrid = false, inView = false, inEdit = false)
    private Long groupID;

    @FieldInfo(type = EFieldType.DOUBLE, order = 7)
    private double percantage;

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public Long getAllStudents() {
        return allStudents;
    }

    public void setAllStudents(Long allStudents) {
        this.allStudents = allStudents;
    }

    public Long getIsLate() {
        return isLate;
    }

    public void setIsLate(Long isLate) {
        this.isLate = isLate;
    }

    public Long getGroupID() {
        return groupID;
    }

    public void setGroupID(Long groupID) {
        this.groupID = groupID;
    }

    public double getPercantage() {
        return percantage;
    }

    public void setPercantage(double percantage) {
        this.percantage = percantage;
    }
}
