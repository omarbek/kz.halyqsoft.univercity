package kz.halyqsoft.univercity.filter;

import kz.halyqsoft.univercity.entity.beans.univercity.catalog.SPECIALITY;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.STUDY_YEAR;
import kz.halyqsoft.univercity.entity.beans.univercity.view.V_GROUP;
import org.r3a.common.vaadin.widget.filter2.AbstractFilterBean;

/**
 * @author Omarbek
 * @created Mar 27, 2018 4:54:32 PM
 */
public final class FChangeToNextYearGroupFilter extends AbstractFilterBean {


    private V_GROUP group;
    private SPECIALITY speciality;
    private STUDY_YEAR studyYear;

    public V_GROUP getGroup() {
        return group;
    }

    public void setGroup(V_GROUP group) {
        this.group = group;
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

    @Override
    public boolean hasFilter() {
        return !(group == null && speciality == null && group == null );
    }
}
