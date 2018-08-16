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

    @FieldInfo(type = EFieldType.DATE, order = 1)
    @Column(name = "ENTRANCE_YEAR", nullable = false)
    @Temporal(TemporalType.DATE)
    private Date entranceYear;

    @FieldInfo(type = EFieldType.DATE, order = 2)
    @Column(name = "GRADUATION_YEAR", nullable = false)
    @Temporal(TemporalType.DATE)
    private Date graduationYear;

    @FieldInfo(type = EFieldType.FK_COMBO, order = 3)
    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "UNIVERSITY_ID", referencedColumnName = "ID")
    })
    private UNIVERSITY university;

    @FieldInfo(type = EFieldType.FK_COMBO, order = 4)
    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "SPECIALITY_ID", referencedColumnName = "ID")
    })
    private SPECIALITY speciality;

    @FieldInfo(type = EFieldType.FK_COMBO, order = 5)
    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "QULIFICATION_ID", referencedColumnName = "ID")
    })
    private QUALIFICATION qualification;

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

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }
}