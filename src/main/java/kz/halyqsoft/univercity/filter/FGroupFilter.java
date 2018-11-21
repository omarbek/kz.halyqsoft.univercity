package kz.halyqsoft.univercity.filter;

import kz.halyqsoft.univercity.entity.beans.univercity.GROUPS;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.SPECIALITY;
import org.r3a.common.vaadin.widget.filter2.AbstractFilterBean;

/**
 * @author Omarbek
 * @created Mar 27, 2018 4:54:32 PM
 */
public final class FGroupFilter extends AbstractFilterBean {

    private String code;
    private SPECIALITY speciality;
    private GROUPS group;
    private Long orders;

    public FGroupFilter() {
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public GROUPS getGroup() {
        return group;
    }

    public void setGroup(GROUPS group) {
        this.group = group;
    }

    public Long getOrders() {
        return orders;
    }

    public void setOrders(Long orders) {
        this.orders = orders;
    }

    public SPECIALITY getSpeciality() {
        return speciality;
    }

    public void setSpeciality(SPECIALITY speciality) {
        this.speciality = speciality;
    }

    @Override
    public boolean hasFilter() {
        return !(code == null && orders == null && group == null );
    }
}
