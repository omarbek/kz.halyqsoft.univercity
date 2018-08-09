package kz.halyqsoft.univercity.entity.beans.univercity.view;

import org.r3a.common.entity.AbstractEntity;
import org.r3a.common.entity.EFieldType;
import org.r3a.common.entity.FieldInfo;

public class VAbsents extends AbstractEntity {

    @FieldInfo(type=EFieldType.TEXT, order = 1)
    private String FIO;

    @FieldInfo(type = EFieldType.TEXT, order = 2)
    private String faculty;

    @FieldInfo(type = EFieldType.TEXT, order = 3)
    private String speciality;

    @FieldInfo(type = EFieldType.TEXT, order = 4)
    private String group;

    @FieldInfo(type = EFieldType.DOUBLE, order = 5)
    private Double absentSum;

    public String getFIO() {
        return FIO;
    }

    public void setFIO(String FIO) {
        this.FIO = FIO;
    }

    public String getFaculty() {
        return faculty;
    }

    public void setFaculty(String faculty) {
        this.faculty = faculty;
    }

    public String getSpeciality() {
        return speciality;
    }

    public void setSpeciality(String speciality) {
        this.speciality = speciality;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public Double getAbsentSum() {
        return absentSum;
    }

    public void setAbsentSum(Double absentSum) {
        this.absentSum = absentSum;
    }
}