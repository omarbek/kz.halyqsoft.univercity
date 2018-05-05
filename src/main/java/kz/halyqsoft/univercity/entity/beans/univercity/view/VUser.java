package kz.halyqsoft.univercity.entity.beans.univercity.view;

import org.r3a.common.entity.AbstractEntity;
import org.r3a.common.entity.EFieldType;
import org.r3a.common.entity.FieldInfo;

import java.util.Date;

/**
 * @author Omarbek
 * @created on 16.04.2018
 */
public class VUser extends AbstractEntity {

    @FieldInfo(type = EFieldType.TEXT, order = 2)
    private String code;

    @FieldInfo(type = EFieldType.TEXT, order = 3)
    private String fio;

    @FieldInfo(type = EFieldType.TEXT, order = 4)
    private String specialty;

    @FieldInfo(type = EFieldType.INTEGER, order = 5)
    private int studyYear;

    @FieldInfo(type = EFieldType.TEXT, order = 6)
    private String deptName;

    @FieldInfo(type = EFieldType.TEXT, order = 7)
    private String postName;

    @FieldInfo(type = EFieldType.DATETIME, order = 8)
    private Date created;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getFio() {
        return fio;
    }

    public void setFio(String fio) {
        this.fio = fio;
    }

    public String getSpecialty() {
        return specialty;
    }

    public void setSpecialty(String specialty) {
        this.specialty = specialty;
    }

    public int getStudyYear() {
        return studyYear;
    }

    public void setStudyYear(int studyYear) {
        this.studyYear = studyYear;
    }

    public String getDeptName() {
        return deptName;
    }

    public void setDeptName(String deptName) {
        this.deptName = deptName;
    }

    public String getPostName() {
        return postName;
    }

    public void setPostName(String postName) {
        this.postName = postName;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }
}
