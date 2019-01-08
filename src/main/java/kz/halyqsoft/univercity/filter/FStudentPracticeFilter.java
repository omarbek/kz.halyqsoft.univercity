package kz.halyqsoft.univercity.filter;

import kz.halyqsoft.univercity.entity.beans.univercity.GROUPS;
import kz.halyqsoft.univercity.entity.beans.univercity.STUDENT;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.DEPARTMENT;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.ORGANIZATION;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.SPECIALITY;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.STUDY_YEAR;
import org.r3a.common.vaadin.widget.filter2.AbstractFilterBean;

import java.util.Date;

public final class FStudentPracticeFilter extends AbstractFilterBean {

    private STUDENT student;
    private ORGANIZATION organization;
    private DEPARTMENT department;
    private SPECIALITY speciality;
    private STUDY_YEAR studyYear;
    private GROUPS groups;
    private Date comeInDate;
    private Date comeOutDate;

    public STUDENT getStudent() {
        return student;
    }

    public void setStudent(STUDENT student) {
        this.student = student;
    }

    public ORGANIZATION getOrganization() {
        return organization;
    }

    public void setOrganization(ORGANIZATION organization) {
        this.organization = organization;
    }

    public FStudentPracticeFilter() {
    }

    public Date getComeInDate() {
        return comeInDate;
    }

    public void setComeInDate(Date comeInDate) {
        this.comeInDate = comeInDate;
    }

    public Date getComeOutDate() {
        return comeOutDate;
    }

    public void setComeOutDate(Date comeOutDate) {
        this.comeOutDate = comeOutDate;
    }

    public DEPARTMENT getDepartment() {
        return department;
    }

    public void setDepartment(DEPARTMENT department) {
        this.department = department;
    }

    public SPECIALITY getSpeciality() {
        return speciality;
    }

    public void setSpeciality(SPECIALITY speciality) {
        this.speciality = speciality;
    }

    public STUDY_YEAR getStudyYear() {
        return studyYear;
    }

    public void setStudyYear(STUDY_YEAR studyYear) {
        this.studyYear = studyYear;
    }

    public GROUPS getGroups() {
        return groups;
    }

    public void setGroups(GROUPS groups) {
        this.groups = groups;
    }

    @Override
    public boolean hasFilter() {
        return !( comeInDate == null && comeOutDate == null && department == null &&
                speciality == null && studyYear == null && groups ==null && organization == null && student ==null );
    }
}
