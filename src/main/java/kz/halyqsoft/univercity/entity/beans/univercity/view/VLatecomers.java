package kz.halyqsoft.univercity.entity.beans.univercity.view;

import org.r3a.common.entity.AbstractEntity;
import org.r3a.common.entity.EFieldType;
import org.r3a.common.entity.FieldInfo;

import java.math.BigDecimal;

public class VLatecomers extends AbstractEntity {

    @FieldInfo(type= EFieldType.TEXT, order = 1)
    private String groupName;

    @FieldInfo(type = EFieldType.TEXT, order = 2)
    private String  fio;

    @FieldInfo(type = EFieldType.INTEGER, order = 5)
    private BigDecimal time;


    @FieldInfo(type = EFieldType.INTEGER, order = 6, inGrid = false, inView = false, inEdit = false)
    private Long groupID;

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getFio() {
        return fio;
    }

    public void setFio(String fio) {
        this.fio = fio;
    }


    public BigDecimal getTime() {
        return time;
    }

    public void setTime(BigDecimal time) {
        this.time = time;
    }

    public Long getGroupID() {
        return groupID;
    }

    public void setGroupID(Long groupID) {
        this.groupID = groupID;
    }
}
