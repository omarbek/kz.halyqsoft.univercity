package kz.halyqsoft.univercity.entity.beans.univercity.view;

import kz.halyqsoft.univercity.entity.beans.USERS;
import kz.halyqsoft.univercity.entity.beans.univercity.STUDENT;
import org.r3a.common.entity.AbstractEntity;
import org.r3a.common.entity.EFieldType;
import org.r3a.common.entity.FieldInfo;

import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.util.Date;

public class VStudentInfo extends AbstractEntity {

    @FieldInfo(type=EFieldType.TEXT, order = 1)
    private USERS student;

    @FieldInfo(type = EFieldType.TEXT, order = 2)
    private String code;

    @FieldInfo(type = EFieldType.TEXT, order = 3)
    private String comeIN;

    @FieldInfo(type = EFieldType.TEXT, order = 4)
    private String comeOUT;

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

    public VStudentInfo(){

    }
}