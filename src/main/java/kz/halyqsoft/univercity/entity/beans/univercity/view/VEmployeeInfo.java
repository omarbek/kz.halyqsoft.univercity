package kz.halyqsoft.univercity.entity.beans.univercity.view;

import org.r3a.common.entity.AbstractEntity;
import org.r3a.common.entity.EFieldType;
import org.r3a.common.entity.FieldInfo;

public class VEmployeeInfo extends AbstractEntity {

    @FieldInfo(type=EFieldType.TEXT, order = 1)
    private String FIO;

    @FieldInfo(type = EFieldType.TEXT, order = 2)
    private String code;

    @FieldInfo(type = EFieldType.TEXT, order = 3)
    private String comeIN;

    @FieldInfo(type = EFieldType.TEXT, order = 4)
    private String comeOUT;

    public String getFIO() {
        return FIO;
    }

    public void setFIO(String FIO) {
        this.FIO = FIO;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getComeIN() {
        return comeIN;
    }

    public void setComeIN(String comeIN) {
        this.comeIN = comeIN;
    }

    public String getComeOUT() {
        return comeOUT;
    }

    public void setComeOUT(String comeOUT) {
        this.comeOUT = comeOUT;
    }
}