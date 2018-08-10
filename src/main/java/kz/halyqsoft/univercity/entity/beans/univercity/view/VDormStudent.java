package kz.halyqsoft.univercity.entity.beans.univercity.view;

import org.r3a.common.entity.AbstractEntity;
import org.r3a.common.entity.EFieldType;
import org.r3a.common.entity.FieldInfo;

import java.math.BigDecimal;

public class VDormStudent extends AbstractEntity {

    @FieldInfo(type=EFieldType.INTEGER, order = 1)
    private long roomNo;

    @FieldInfo(type = EFieldType.TEXT, order = 2)
    private String fio;

    @FieldInfo(type = EFieldType.DOUBLE, order = 3)
    private BigDecimal cost;

    public long getRoomNo() {
        return roomNo;
    }

    public void setRoomNo(long roomNo) {
        this.roomNo = roomNo;
    }

    public String getFio() {
        return fio;
    }

    public void setFio(String fio) {
        this.fio = fio;
    }

    public BigDecimal getCost() {
        return cost;
    }

    public void setCost(BigDecimal cost) {
        this.cost = cost;
    }


}
