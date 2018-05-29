package kz.halyqsoft.univercity.entity.beans.univercity.view;

import org.r3a.common.entity.AbstractEntity;
import org.r3a.common.entity.EFieldType;
import org.r3a.common.entity.FieldInfo;

import javax.persistence.Entity;

/**
 * @author Omarbek
 * @created on 23.04.2018
 */
public class VCreativeExam extends AbstractEntity {

    @FieldInfo(type = EFieldType.TEXT, order = 2)
    private String code;

    @FieldInfo(type = EFieldType.TEXT, order = 3)
    private String fio;

    @FieldInfo(type = EFieldType.TEXT, order = 4)
    private String firstSubject;

    @FieldInfo(type = EFieldType.TEXT, order = 5)
    private String secondSubject;

    @FieldInfo(type = EFieldType.TEXT, order = 6)
    private Integer total;

    @FieldInfo(type = EFieldType.TEXT, order = 7)
    private String place;

    public String getFio() {
        return fio;
    }

    public void setFio(String fio) {
        this.fio = fio;
    }

    public String getFirstSubject() {
        return firstSubject;
    }

    public void setFirstSubject(String firstSubject) {
        this.firstSubject = firstSubject;
    }

    public String getSecondSubject() {
        return secondSubject;
    }

    public void setSecondSubject(String secondSubject) {
        this.secondSubject = secondSubject;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
