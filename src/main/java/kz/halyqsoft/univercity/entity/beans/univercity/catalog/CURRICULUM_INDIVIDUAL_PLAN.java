package kz.halyqsoft.univercity.entity.beans.univercity.catalog;

import org.r3a.common.entity.AbstractEntity;
import org.r3a.common.entity.EFieldType;
import org.r3a.common.entity.FieldInfo;

import javax.persistence.*;

/**
 * @author Omarbek
 * @created on 12.11.2018
 */
@Entity
public class CURRICULUM_INDIVIDUAL_PLAN extends AbstractEntity {

    @FieldInfo(type = EFieldType.FK_COMBO)
    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "SPECIALITY_ID", referencedColumnName = "ID")})
    private SPECIALITY speciality;

    @FieldInfo(type = EFieldType.FK_COMBO, order = 2)
    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "ENTRANCE_YEAR_ID", referencedColumnName = "ID")})
    private ENTRANCE_YEAR entranceYear;

    @FieldInfo(type = EFieldType.FK_COMBO, order = 3)
    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "DIPLOMA_TYPE_ID", referencedColumnName = "ID")})
    private STUDENT_DIPLOMA_TYPE studentDiplomaType;

    @FieldInfo(type = EFieldType.TEXT, order = 4, max = 20)
    @Column(name = "student_code", nullable = false)
    private String studentCode;

    public SPECIALITY getSpeciality() {
        return speciality;
    }

    public void setSpeciality(SPECIALITY speciality) {
        this.speciality = speciality;
    }

    public String getStudentCode() {
        return studentCode;
    }

    public void setStudentCode(String studentCode) {
        this.studentCode = studentCode;
    }

    public ENTRANCE_YEAR getEntranceYear() {
        return entranceYear;
    }

    public void setEntranceYear(ENTRANCE_YEAR entranceYear) {
        this.entranceYear = entranceYear;
    }

    public STUDENT_DIPLOMA_TYPE getStudentDiplomaType() {
        return studentDiplomaType;
    }

    public void setStudentDiplomaType(STUDENT_DIPLOMA_TYPE studentDiplomaType) {
        this.studentDiplomaType = studentDiplomaType;
    }
}
