package kz.halyqsoft.univercity.entity.beans.univercity.view;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.r3a.common.entity.EFieldType;
import org.r3a.common.entity.FieldInfo;

import javax.persistence.Column;

public class V_PRIORITY extends V_EMPLOYEE_DEPT {

    private static final long serialVersionUID = -1646900881175952040L;


    @JsonIgnore
    @FieldInfo(type = EFieldType.BOOLEAN, order = 18, inGrid = false)
    private boolean priority;

    @FieldInfo(type = EFieldType.BOOLEAN, order = 19, required = false)
    private boolean lecturer;

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    @Override
    public boolean isPriority() {
        return priority;
    }

    @Override
    public void setPriority(boolean priority) {
        this.priority = priority;
    }

    @Override
    public boolean isLecturer() {
        return lecturer;
    }

    @Override
    public void setLecturer(boolean lecturer) {
        this.lecturer = lecturer;
    }
}
