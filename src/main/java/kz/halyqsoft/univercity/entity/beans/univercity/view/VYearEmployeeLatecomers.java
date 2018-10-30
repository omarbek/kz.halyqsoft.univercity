package kz.halyqsoft.univercity.entity.beans.univercity.view;

import org.r3a.common.entity.AbstractEntity;
import org.r3a.common.entity.EFieldType;
import org.r3a.common.entity.FieldInfo;

public class VYearEmployeeLatecomers extends AbstractEntity {

    @FieldInfo(type=EFieldType.TEXT, order = 1)
    private String FIO;

    @FieldInfo(type = EFieldType.TEXT, order = 2)
    private String faculty;

    @FieldInfo(type = EFieldType.TEXT, order = 3)
    private String postName;

    @FieldInfo(type = EFieldType.INTEGER, order = 4)
    private long lateSum;

    @FieldInfo(type = EFieldType.INTEGER, order = 5, inGrid = false, inView = false, inEdit = false )
    private String time;

    public String getFIO() {
        return FIO;
    }

    public void setFIO(String FIO) {
        this.FIO = FIO;
    }

    public String getPostName() {
        return postName;
    }

    public void setPostName(String postName) {
        this.postName = postName;
    }

    public long getLateSum() {
        return lateSum;
    }

    public void setLateSum(long lateSum) {
        this.lateSum = lateSum;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getFaculty() {
        return faculty;
    }

    public void setFaculty(String faculty) {
        this.faculty = faculty;
    }
}
