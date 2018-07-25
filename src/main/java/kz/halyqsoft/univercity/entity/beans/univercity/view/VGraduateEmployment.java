package kz.halyqsoft.univercity.entity.beans.univercity.view;

import org.r3a.common.entity.AbstractEntity;
import org.r3a.common.entity.EFieldType;
import org.r3a.common.entity.FieldInfo;

public class VGraduateEmployment extends AbstractEntity {

    @FieldInfo(type=EFieldType.TEXT, order = 1)
    private String specName;

    @FieldInfo(type = EFieldType.INTEGER, order = 2)
    private long graduatedSum;

    @FieldInfo(type = EFieldType.INTEGER, order = 3)
    private long employedSum;

    @FieldInfo(type = EFieldType.INTEGER, order = 4)
    private long bySpecSum;

    @FieldInfo(type = EFieldType.INTEGER, order = 5)
    private long masterSum;

    @FieldInfo(type = EFieldType.INTEGER, order = 6)
    private long decreeSum;

    @FieldInfo(type = EFieldType.INTEGER, order = 7)
    private long armySum;

    public VGraduateEmployment() {
    }

    public String getSpecName() {
        return specName;
    }

    public void setSpecName(String specName) {
        this.specName = specName;
    }

    public long getGraduatedSum() {
        return graduatedSum;
    }

    public void setGraduatedSum(long graduatedSum) {
        this.graduatedSum = graduatedSum;
    }

    public long getEmployedSum() {
        return employedSum;
    }

    public void setEmployedSum(long employedSum) {
        this.employedSum = employedSum;
    }

    public long getBySpecSum() {
        return bySpecSum;
    }

    public void setBySpecSum(long bySpecSum) {
        this.bySpecSum = bySpecSum;
    }

    public long getMasterSum() {
        return masterSum;
    }

    public void setMasterSum(long masterSum) {
        this.masterSum = masterSum;
    }

    public long getDecreeSum() {
        return decreeSum;
    }

    public void setDecreeSum(long decreeSum) {
        this.decreeSum = decreeSum;
    }

    public long getArmySum() {
        return armySum;
    }

    public void setArmySum(long armySum) {
        this.armySum = armySum;
    }
}
