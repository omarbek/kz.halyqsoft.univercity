package kz.halyqsoft.univercity.entity.beans.univercity.view;

import org.r3a.common.entity.AbstractEntity;
import org.r3a.common.entity.EFieldType;
import org.r3a.common.entity.FieldInfo;

public class VFaculty extends AbstractEntity {

    @FieldInfo(type=EFieldType.TEXT, order = 1)
    private String facultyName;

    @FieldInfo(type = EFieldType.INTEGER, order = 2)
    private Long count;

    @FieldInfo(type = EFieldType.INTEGER, order = 3)
    private Long isPresent;

    @FieldInfo(type = EFieldType.INTEGER, order = 4)
    private Long absent;

    @FieldInfo(type = EFieldType.DOUBLE, order = 5)
    private double percantage;

    @FieldInfo(type = EFieldType.INTEGER, order = 6, inGrid = false, inView = false, inEdit = false)
    private Long facultyID;

    @FieldInfo(type = EFieldType.INTEGER, order = 8, inGrid = false, inView = false, inEdit = false )
    private String time;

    public String getFacultyName() {
        return facultyName;
    }

    public void setFacultyName(String facultyName) {
        this.facultyName = facultyName;
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

    public Long getFacultyID() {
        return facultyID;
    }

    public void setFacultyID(Long facultyID) {
        this.facultyID = facultyID;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
