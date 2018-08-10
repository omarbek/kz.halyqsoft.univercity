package kz.halyqsoft.univercity.entity.beans.univercity.view;

import org.r3a.common.entity.AbstractEntity;
import org.r3a.common.entity.EFieldType;
import org.r3a.common.entity.FieldInfo;

public class VGraduate extends AbstractEntity {

    @FieldInfo(type=EFieldType.TEXT, order = 1)
    private String FIO;

    @FieldInfo(type = EFieldType.TEXT, order = 2)
    private String employed;

    @FieldInfo(type = EFieldType.TEXT, order = 3)
    private String bySpeciality;

    @FieldInfo(type = EFieldType.TEXT, order = 4)
    private String master;

    @FieldInfo(type = EFieldType.TEXT, order = 5)
    private String decree;

    @FieldInfo(type = EFieldType.TEXT, order = 6)
    private String army;

    public String getFIO() {
        return FIO;
    }

    public void setFIO(String FIO) {
        this.FIO = FIO;
    }

    public String getEmployed() {
        return employed;
    }

    public void setEmployed(String employed) {
        this.employed = employed;
    }

    public String getBySpeciality() {
        return bySpeciality;
    }

    public void setBySpeciality(String bySpeciality) {
        this.bySpeciality = bySpeciality;
    }

    public String getMaster() {
        return master;
    }

    public void setMaster(String master) {
        this.master = master;
    }

    public String getDecree() {
        return decree;
    }

    public void setDecree(String decree) {
        this.decree = decree;
    }

    public String getArmy() {
        return army;
    }

    public void setArmy(String army) {
        this.army = army;
    }
}
