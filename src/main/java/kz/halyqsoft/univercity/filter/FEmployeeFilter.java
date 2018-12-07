package kz.halyqsoft.univercity.filter;

import kz.halyqsoft.univercity.entity.beans.univercity.catalog.CARD;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.DEPARTMENT;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.POST;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.SUBJECT;
import org.r3a.common.vaadin.widget.filter2.AbstractFilterBean;

/**
 * @author Omarbek
 * @created Apr 10, 2018 5:18:24 PM
 */
public final class FEmployeeFilter extends AbstractFilterBean {

    private static final long serialVersionUID = 6675454138920254736L;

    private String code;
    private String firstname;
	private String lastname;
    private DEPARTMENT department;
    private POST post;
    private CARD card;
    private SUBJECT subject;
    private Integer childAge;
    private Integer childCount;

    public FEmployeeFilter() {
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

    public DEPARTMENT getDepartment() {
        return department;
    }

    public void setDepartment(DEPARTMENT department) {
        this.department = department;
    }

    public POST getPost() {
        return post;
    }

    public void setPost(POST post) {
        this.post = post;
    }

    public CARD getCard() {
        return card;
    }

    public void setCard(CARD card) {
        this.card = card;
    }

    public Integer getChildAge() {
        return childAge;
    }

    public void setChildAge(Integer childAge) {
        this.childAge = childAge;
    }

    public SUBJECT getSubject() {
        return subject;
    }

    public void setSubject(SUBJECT subject) {
        this.subject = subject;
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public Integer getChildCount() {
        return childCount;
    }

    public void setChildCount(Integer childCount) {
        this.childCount = childCount;
    }

    @Override
    public boolean hasFilter() {
        return (!(code == null && firstname == null && lastname == null
                && department == null && post == null && card == null && childAge == null && subject == null && childCount==null));
    }
}
