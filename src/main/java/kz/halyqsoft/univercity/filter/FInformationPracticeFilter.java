package kz.halyqsoft.univercity.filter;

import kz.halyqsoft.univercity.entity.beans.USERS;
import kz.halyqsoft.univercity.entity.beans.univercity.GROUPS;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.ENTRANCE_YEAR;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.SPECIALITY;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.STUDY_YEAR;
import org.r3a.common.vaadin.widget.filter2.AbstractFilterBean;

import java.util.Date;

public final class FInformationPracticeFilter extends AbstractFilterBean {

    private String code;
    private GROUPS groups;
    private USERS employee;
    private Date created;
    private STUDY_YEAR studyYear;
    private ENTRANCE_YEAR entranceYear;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public GROUPS getGroups() {
        return groups;
    }

    public void setGroups(GROUPS groups) {
        this.groups = groups;
    }

    public USERS getEmployee() {
        return employee;
    }

    public void setEmployee(USERS employee) {
        this.employee = employee;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public STUDY_YEAR getStudyYear() {
        return studyYear;
    }

    public void setStudyYear(STUDY_YEAR studyYear) {
        this.studyYear = studyYear;
    }

    public ENTRANCE_YEAR getEntranceYear() {
        return entranceYear;
    }

    public void setEntranceYear(ENTRANCE_YEAR entranceYear) {
        this.entranceYear = entranceYear;
    }

    public FInformationPracticeFilter() {
    }

    @Override
    public boolean hasFilter() {
        return !(code == null && groups == null && employee== null && created ==null && entranceYear==null );
    }
}
