package kz.halyqsoft.univercity.filter;

import kz.halyqsoft.univercity.entity.beans.univercity.catalog.USER_TYPE;
import org.r3a.common.vaadin.widget.filter2.AbstractFilterBean;

import java.util.Date;

/**
 * @author Omarbek
 * @created on 16.04.2018
 */
public final class FUserFilter extends AbstractFilterBean {

    private String code;
    private String firstname;
    private String lastname;
    private USER_TYPE userType;
    private Date date;
    private String iin;

    @Override
    public boolean hasFilter() {
        return (!(code == null && firstname == null && lastname == null && date == null && userType == null && iin == null));
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public USER_TYPE getUserType() {
        return userType;
    }

    public void setUserType(USER_TYPE userType) {
        this.userType = userType;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getIin() {
        return iin;
    }

    public void setIin(String iin) {
        this.iin = iin;
    }
}
