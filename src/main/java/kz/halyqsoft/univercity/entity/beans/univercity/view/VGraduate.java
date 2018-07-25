package kz.halyqsoft.univercity.entity.beans.univercity.view;

import org.r3a.common.entity.AbstractEntity;
import org.r3a.common.entity.EFieldType;
import org.r3a.common.entity.FieldInfo;

public class VGraduate extends AbstractEntity {

    @FieldInfo(type=EFieldType.TEXT, order = 1)
    private String FIO;

    @FieldInfo(type = EFieldType.BOOLEAN, order = 2)
    private boolean employed;

    @FieldInfo(type = EFieldType.BOOLEAN, order = 3)
    private boolean bySpeciality;

    @FieldInfo(type = EFieldType.BOOLEAN, order = 4)
    private boolean master;

    @FieldInfo(type = EFieldType.BOOLEAN, order = 5)
    private boolean decree;

    @FieldInfo(type = EFieldType.BOOLEAN, order = 6)
    private boolean army;

    public VGraduate() {
    }

    public String getFIO() {
        return FIO;
    }

    public void setFIO(String FIO) {
        this.FIO = FIO;
    }

    public boolean isEmployed() {
        return employed;
    }

    public void setEmployed(boolean employed) {
        this.employed = employed;
    }

    public boolean isBySpeciality() {
        return bySpeciality;
    }

    public void setBySpeciality(boolean bySpeciality) {
        this.bySpeciality = bySpeciality;
    }

    public boolean isMaster() {
        return master;
    }

    public void setMaster(boolean master) {
        this.master = master;
    }

    public boolean isDecree() {
        return decree;
    }

    public void setDecree(boolean decree) {
        this.decree = decree;
    }

    public boolean isArmy() {
        return army;
    }

    public void setArmy(boolean army) {
        this.army = army;
    }
}
