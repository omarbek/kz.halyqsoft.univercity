package kz.halyqsoft.univercity.entity.beans.univercity;

import kz.halyqsoft.univercity.entity.beans.univercity.catalog.SEMESTER;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.SUBJECT;
import org.r3a.common.entity.AbstractEntity;
import org.r3a.common.entity.EFieldType;
import org.r3a.common.entity.FieldInfo;

import javax.persistence.*;
import java.util.Date;

/**
 * @author Omarbek
 * @created 19.06.2018
 */
@Entity
public class ELECTIVE_BINDED_SUBJECT extends AbstractEntity {

    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "CATALOG_ELECTIVE_SUBJECTS_ID", referencedColumnName = "ID")})
    private CATALOG_ELECTIVE_SUBJECTS catalogElectiveSubjects;

    @FieldInfo(type = EFieldType.FK_COMBO, order = 2)
    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "FIRST_SUBJECT_ID", referencedColumnName = "ID")})
    private SUBJECT firstSubject;

    @FieldInfo(type = EFieldType.FK_COMBO, order = 3)
    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "SECOND_SUBJECT_ID", referencedColumnName = "ID")})
    private SUBJECT secondSubject;

    @FieldInfo(type = EFieldType.FK_COMBO, order = 4)
    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "SEMESTER_ID", referencedColumnName = "ID")})
    private SEMESTER semester;

    public SUBJECT getFirstSubject() {
        return firstSubject;
    }

    public void setFirstSubject(SUBJECT firstSubject) {
        this.firstSubject = firstSubject;
    }

    public SUBJECT getSecondSubject() {
        return secondSubject;
    }

    public void setSecondSubject(SUBJECT secondSubject) {
        this.secondSubject = secondSubject;
    }

    public SEMESTER getSemester() {
        return semester;
    }

    public void setSemester(SEMESTER semester) {
        this.semester = semester;
    }

    public CATALOG_ELECTIVE_SUBJECTS getCatalogElectiveSubjects() {
        return catalogElectiveSubjects;
    }

    public void setCatalogElectiveSubjects(CATALOG_ELECTIVE_SUBJECTS catalogElectiveSubjects) {
        this.catalogElectiveSubjects = catalogElectiveSubjects;
    }
}
