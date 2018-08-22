package kz.halyqsoft.univercity.entity.beans.univercity.view;

import org.r3a.common.entity.AbstractEntity;
import org.r3a.common.entity.EFieldType;
import org.r3a.common.entity.FieldInfo;

public class VChild extends AbstractEntity {

    @FieldInfo(type=EFieldType.TEXT, order = 1)
    private String sex;

    @FieldInfo(type = EFieldType.DOUBLE, order = 2)
    private double childAge;

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public double getChildAge() {
        return childAge;
    }

    public void setChildAge(double childAge) {
        this.childAge = childAge;
    }
}