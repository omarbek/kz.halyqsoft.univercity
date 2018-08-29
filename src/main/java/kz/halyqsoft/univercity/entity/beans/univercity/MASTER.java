package kz.halyqsoft.univercity.entity.beans.univercity;

import kz.halyqsoft.univercity.entity.beans.USERS;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.QUALIFICATION;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.SPECIALITY;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.UNIVERSITY;
import org.r3a.common.entity.AbstractEntity;
import org.r3a.common.entity.EFieldType;
import org.r3a.common.entity.FieldInfo;

import javax.persistence.*;
import java.util.Date;

@Entity
public class MASTER extends AbstractEntity {

    @FieldInfo(type = EFieldType.TEXT, order = 1)
    @Column(name = "ENTRANCE_YEAR", nullable = false)
    private String entranceYear;

    @FieldInfo(type = EFieldType.TEXT, order = 2)
    @Column(name = "GRADUATION_YEAR", nullable = false)
    private String graduationYear;

    @FieldInfo(type = EFieldType.TEXT, order = 3)
    @Column(name = "UNIVERSITY_NAME")
    private String university;

    @FieldInfo(type = EFieldType.TEXT, order = 4)
    @Column(name = "SPECIALITY_NAME")
    private String speciality;

    @FieldInfo(type = EFieldType.INTEGER, order = 6)
    @Column(name = "DIPLOMA_NUMBER", nullable = false)
    private int diplomaNumber;

    @FieldInfo(type = EFieldType.FK_COMBO, inGrid = false, inEdit = false, inView = false,order = 7)
    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "EMPLOYEE_ID", referencedColumnName = "ID")
    })
    private USERS employee;

    @FieldInfo(type = EFieldType.BOOLEAN, order = 8, required = false, inEdit = false, inGrid = false, inView = false )
    @Column(name = "DELETED", nullable = false)
    private boolean deleted;

    public MASTER() {
    }

    public String getEntranceYear() {
        return entranceYear;
    }

    public void setEntranceYear(String entranceYear) {
        this.entranceYear = entranceYear;
    }

    public String getGraduationYear() {
        return graduationYear;
    }

    public void setGraduationYear(String graduationYear) {
        this.graduationYear = graduationYear;
    }

    public String getUniversity() {
        return university;
    }

    public void setUniversity(String university) {
        this.university = university;
    }

    public String getSpeciality() {
        return speciality;
    }

    public void setSpeciality(String speciality) {
        this.speciality = speciality;
    }

    public int getDiplomaNumber() {
        return diplomaNumber;
    }

    public void setDiplomaNumber(int diplomaNumber) {
        this.diplomaNumber = diplomaNumber;
    }

    public USERS getEmployee() {
        return employee;
    }

    public void setEmployee(USERS employee) {
        this.employee = employee;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }
}