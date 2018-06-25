package kz.halyqsoft.univercity.filter;

import kz.halyqsoft.univercity.entity.beans.univercity.catalog.ENTRANCE_YEAR;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.SPECIALITY;
import org.r3a.common.vaadin.widget.filter2.AbstractFilterBean;

public class FElectiveFilter extends AbstractFilterBean {

    private SPECIALITY speciality;
    private ENTRANCE_YEAR entranceYear;

    public SPECIALITY getSpeciality() {
        return speciality;
    }

    public void setSpeciality(SPECIALITY speciality) {
        this.speciality = speciality;
    }

    public ENTRANCE_YEAR getEntranceYear() {
        return entranceYear;
    }

    public void setEntranceYear(ENTRANCE_YEAR entranceYear) {
        this.entranceYear = entranceYear;
    }

    @Override
    public boolean hasFilter() {
        return (!(speciality == null && entranceYear == null));
    }
}
