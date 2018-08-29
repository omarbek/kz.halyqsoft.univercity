package kz.halyqsoft.univercity.entity.beans.univercity.view;

import kz.halyqsoft.univercity.entity.beans.USERS;
import kz.halyqsoft.univercity.entity.beans.univercity.EMPLOYEE;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.QUALIFICATION;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.SPECIALITY;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.UNIVERSITY;
import org.r3a.common.entity.AbstractEntity;
import org.r3a.common.entity.EFieldType;
import org.r3a.common.entity.FieldInfo;

import javax.persistence.*;
import java.util.Date;

@Entity
public class V_MASTER extends AbstractEntity {

    @FieldInfo(type = EFieldType.TEXT, order = 1,max = 2099)
    @Column(name = "ENTRANCE_YEAR", nullable = false)
    private String  entranceYear;


    @FieldInfo(type = EFieldType.TEXT, order = 2,max = 2099)
    @Column(name = "GRADUATION_YEAR", nullable = false)
    private String graduationYear;

    @FieldInfo(type = EFieldType.FK_COMBO, order = 3, inGrid = false)
@Column(name = "UNIVERSITY_NAME")
    private String university;

    @FieldInfo(type = EFieldType.FK_COMBO, order = 4, inGrid = false)
    @Column(name = "SPECIALITY_NAME")
    private String speciality;


    @FieldInfo(type = EFieldType.INTEGER, order = 6)
    @Column(name = "DIPLOMA_NUMBER", nullable = false)
    private int diplomaNumber;

    @FieldInfo(type = EFieldType.FK_COMBO, order = 7, inGrid = false)
    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "EMPLOYEE_ID", referencedColumnName = "ID")
    })
    private USERS employee;

    public V_MASTER() {
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
}
