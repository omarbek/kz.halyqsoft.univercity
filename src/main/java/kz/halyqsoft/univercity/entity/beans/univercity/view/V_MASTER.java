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

    @FieldInfo(type = EFieldType.DATE, order = 1,max = 2099)
    @Column(name = "ENTRANCE_YEAR", nullable = false)
    private Date entranceYear;

    @FieldInfo(type = EFieldType.DATE, order = 2,max = 2099)
    @Column(name = "GRADUATION_YEAR", nullable = false)
    private Date graduationYear;

    @FieldInfo(type = EFieldType.FK_COMBO, order = 3, inGrid = false)
    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "UNIVERSITY_ID", referencedColumnName = "ID")
    })
    private UNIVERSITY university;

    @FieldInfo(type = EFieldType.FK_COMBO, order = 4, inGrid = false)
    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "SPECIALITY_ID", referencedColumnName = "ID")
    })
    private SPECIALITY speciality;

    @FieldInfo(type = EFieldType.FK_COMBO, order = 5, inGrid = false)
    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "QULIFICATION_ID", referencedColumnName = "ID")
    })
    private QUALIFICATION qualification;

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

    public Date getEntranceYear() {
        return entranceYear;
    }

    public void setEntranceYear(Date entranceYear) {
        this.entranceYear = entranceYear;
    }

    public Date getGraduationYear() {
        return graduationYear;
    }

    public void setGraduationYear(Date graduationYear) {
        this.graduationYear = graduationYear;
    }

    public UNIVERSITY getUniversity() {
        return university;
    }

    public void setUniversity(UNIVERSITY university) {
        this.university = university;
    }

    public SPECIALITY getSpeciality() {
        return speciality;
    }

    public void setSpeciality(SPECIALITY speciality) {
        this.speciality = speciality;
    }

    public QUALIFICATION getQualification() {
        return qualification;
    }

    public void setQualification(QUALIFICATION qualification) {
        this.qualification = qualification;
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
