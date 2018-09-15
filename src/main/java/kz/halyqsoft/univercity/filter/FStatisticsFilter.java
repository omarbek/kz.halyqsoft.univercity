package kz.halyqsoft.univercity.filter;

import kz.halyqsoft.univercity.entity.beans.ROLES;
import kz.halyqsoft.univercity.entity.beans.USERS;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.DEPARTMENT;
import org.r3a.common.vaadin.widget.filter2.AbstractFilterBean;

import java.util.Date;

public class FStatisticsFilter extends AbstractFilterBean {

    private DEPARTMENT department;
    private Date startingDate;
    private Date endingDate;

    public DEPARTMENT getDepartment() {
        return department;
    }

    public void setDepartment(DEPARTMENT department) {
        this.department = department;
    }

    public Date getStartingDate() {
        return startingDate;
    }

    public void setStartingDate(Date startingDate) {
        this.startingDate = startingDate;
    }

    public Date getEndingDate() {
        return endingDate;
    }

    public void setEndingDate(Date endingDate) {
        this.endingDate = endingDate;
    }

    @Override
    public boolean hasFilter() {
        return (!(department == null && startingDate== null && endingDate == null));
    }
}
