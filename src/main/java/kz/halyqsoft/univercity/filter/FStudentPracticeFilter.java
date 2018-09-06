package kz.halyqsoft.univercity.filter;

import kz.halyqsoft.univercity.entity.beans.USERS;
import kz.halyqsoft.univercity.entity.beans.univercity.GROUPS;
import kz.halyqsoft.univercity.entity.beans.univercity.STUDENT;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.ENTRANCE_YEAR;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.ORGANIZATION;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.STUDY_YEAR;
import org.r3a.common.vaadin.widget.filter2.AbstractFilterBean;

import java.util.Date;

public final class FStudentPracticeFilter extends AbstractFilterBean {

    private STUDENT student;
    private ORGANIZATION organization;
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

    @Override
    public boolean hasFilter() {
        return !( comeInDate == null && comeOutDate == null && organization == null && student ==null );
    }
}
