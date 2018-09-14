package kz.halyqsoft.univercity.entity.beans.univercity.view;

import org.r3a.common.entity.AbstractEntity;
import org.r3a.common.entity.EFieldType;
import org.r3a.common.entity.FieldInfo;

public class VTopUserArrival extends AbstractEntity {

    @FieldInfo(type = EFieldType.TEXT , order=1)
    private String fio;

    @FieldInfo(type = EFieldType.TEXT, order=2 )
    private String group;

    @FieldInfo(type = EFieldType.DOUBLE, order=3 )
    private Double percentage;

    public String getFio() {
        return fio;
    }

    public void setFio(String fio) {
        this.fio = fio;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public Double getPercentage() {
        return percentage;
    }

    public void setPercentage(Double percentage) {
        this.percentage = percentage;
    }
}
