package kz.halyqsoft.univercity.entity.beans.univercity.view;

import kz.halyqsoft.univercity.entity.beans.USERS;
import kz.halyqsoft.univercity.entity.beans.univercity.STUDENT;
import org.r3a.common.entity.AbstractEntity;
import org.r3a.common.entity.EFieldType;
import org.r3a.common.entity.FieldInfo;

import java.util.Date;

public class VStudentInfo extends AbstractEntity {

    @FieldInfo(type=EFieldType.TEXT, order = 1)
    private USERS student;

    @FieldInfo(type = EFieldType.TEXT, order = 2)
    private String code;

    @FieldInfo(type = EFieldType.DATETIME, order = 3)
    private Date comeIN;

    @FieldInfo(type = EFieldType.DATETIME, order = 4)
    private Date comeOUT;

    public USERS getStudent() {
        return student;
    }

    public void setStudent(USERS student) {
        this.student = student;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Date getComeIN() {
        return comeIN;
    }

    public void setComeIN(Date comeIN) {
        this.comeIN = comeIN;
    }

    public Date getComeOUT() {
        return comeOUT;
    }

    public void setComeOUT(Date comeOUT) {
        this.comeOUT = comeOUT;
    }
}