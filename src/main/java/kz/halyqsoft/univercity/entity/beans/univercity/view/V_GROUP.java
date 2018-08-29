package kz.halyqsoft.univercity.entity.beans.univercity.view;

import kz.halyqsoft.univercity.entity.beans.univercity.catalog.LANGUAGE;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.SPECIALITY;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.STUDY_YEAR;
import org.r3a.common.entity.AbstractEntity;
import org.r3a.common.entity.EFieldType;
import org.r3a.common.entity.FieldInfo;

import javax.persistence.*;

/**
 * @author Omarbek
 * @created 30.06.2018
 */
@Entity
public class V_GROUP extends AbstractEntity {

    @FieldInfo(type = EFieldType.FK_COMBO , order = 2)
    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "STUDY_YEAR_ID", referencedColumnName = "ID", nullable = false)})
    private STUDY_YEAR studyYear;

    @FieldInfo(type = EFieldType.FK_COMBO , order = 3)
    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "LANGUAGE_ID", referencedColumnName = "ID", nullable = false)})
    private LANGUAGE language;

    @FieldInfo(type = EFieldType.FK_COMBO , order = 4)
    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "SPECIALITY_ID", referencedColumnName = "ID", nullable = false)})
    private SPECIALITY speciality;

    @FieldInfo(type = EFieldType.TEXT, order = 5)
    @Column(name = "GROUP_NAME", nullable = false)
    private String name;

    @FieldInfo(type = EFieldType.INTEGER, order = 6, readOnlyFixed = true)
    @Column(name = "STUDENT_COUNT")
    private Integer studentCount;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getStudentCount() {
        return studentCount;
    }

    public void setStudentCount(Integer studentCount) {
        this.studentCount = studentCount;
    }

    public STUDY_YEAR getStudyYear() {
        return studyYear;
    }

    public void setStudyYear(STUDY_YEAR studyYear) {
        this.studyYear = studyYear;
    }

    public LANGUAGE getLanguage() {
        return language;
    }

    public void setLanguage(LANGUAGE language) {
        this.language = language;
    }

    public SPECIALITY getSpeciality() {
        return speciality;
    }

    public void setSpeciality(SPECIALITY speciality) {
        this.speciality = speciality;
    }

    @Override
    public String toString() {
        return name;
    }
}
