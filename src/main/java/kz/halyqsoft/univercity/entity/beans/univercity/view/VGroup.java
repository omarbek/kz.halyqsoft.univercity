package kz.halyqsoft.univercity.entity.beans.univercity.view;

import kz.halyqsoft.univercity.entity.beans.univercity.EMPLOYEE;
import org.r3a.common.entity.AbstractEntity;
import org.r3a.common.entity.EFieldType;
import org.r3a.common.entity.FieldInfo;

import javax.persistence.Column;

public class VGroup extends AbstractEntity {

    @FieldInfo(type=EFieldType.TEXT, order = 1)
    private String groupName;

    @FieldInfo(type = EFieldType.TEXT, order = 2)
    private EMPLOYEE curator;

    @FieldInfo(type = EFieldType.INTEGER, order = 3)
    private Long count;

    @FieldInfo(type = EFieldType.INTEGER, order = 4)
    private Long isPresent;

    @FieldInfo(type = EFieldType.INTEGER, order = 5)
    private Long absent;

    @FieldInfo(type = EFieldType.DOUBLE, order = 6)
    private double percantage;

    @FieldInfo(type = EFieldType.INTEGER, order = 7, inGrid = false, inView = false, inEdit = false)
    private Long groupID;

    @FieldInfo(type = EFieldType.INTEGER, order = 8, inGrid = false, inView = false, inEdit = false )
    private String time;

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public EMPLOYEE getCurator() {
        return curator;
    }

    public void setCurator(EMPLOYEE curator) {
        this.curator = curator;
    }

    public Long getCount() {
        return count;
    }

    public void setCount(Long count) {
        this.count = count;
    }

    public Long getIsPresent() {
        return isPresent;
    }

    public void setIsPresent(Long isPresent) {
        this.isPresent = isPresent;
    }

    public Long getAbsent() {
        return absent;
    }

    public void setAbsent(Long absent) {
        this.absent = absent;
    }

    public double getPercantage() {
        return percantage;
    }

    public void setPercantage(double percantage) {
        this.percantage = percantage;
    }

    public Long getGroupID() {
        return groupID;
    }

    public void setGroupID(Long groupID) {
        this.groupID = groupID;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
