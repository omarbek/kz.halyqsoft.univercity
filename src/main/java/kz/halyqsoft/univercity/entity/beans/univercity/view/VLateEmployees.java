package kz.halyqsoft.univercity.entity.beans.univercity.view;

import org.r3a.common.entity.AbstractEntity;
import org.r3a.common.entity.EFieldType;
import org.r3a.common.entity.FieldInfo;

import java.util.Date;

public class VLateEmployees extends AbstractEntity {

        @FieldInfo(type=EFieldType.TEXT, order = 1)
        private String FIO;

        @FieldInfo(type = EFieldType.TEXT, order = 2)
        private String faculty;

        @FieldInfo(type = EFieldType.DATE, order = 3)
        private Date date;

        @FieldInfo(type = EFieldType.INTEGER, order = 3,inView = false)
        private long userId;

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

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }
}
