package kz.halyqsoft.univercity.filter;

import kz.halyqsoft.univercity.entity.beans.univercity.catalog.*;
import org.r3a.common.vaadin.widget.filter2.AbstractFilterBean;

/**
 * @author Omarbek
 * @created Mar 27, 2017 4:54:32 PM
 */
public final class FGroupFilter extends AbstractFilterBean {

    private String code;
    private SPECIALITY speciality;
    private String name;
    private Long orders;

    public FGroupFilter() {
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
        return !(code == null && orders == null && name == null );
    }
}
