package kz.halyqsoft.univercity.filter;

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
    private boolean student;
    private Date date;

    @Override
    public boolean hasFilter() {
        return (!(code == null && firstname == null && lastname == null && date == null))
                || student;
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

    public boolean isStudent() {
        return student;
    }

    public void setStudent(boolean student) {
        this.student = student;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
