package kz.halyqsoft.univercity.modules.curriculum.working.changelisteners;

import com.vaadin.data.Property;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.ENTRANCE_YEAR;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.SPECIALITY;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.STUDENT_DIPLOMA_TYPE;
import kz.halyqsoft.univercity.modules.curriculum.working.main.CurriculumView;

/**
 * @author Omarbek
 * @created on 31.10.2018
 */
public class DiplomaTypeChangeListener implements Property.ValueChangeListener {

    private CurriculumView curriculumView;

    public DiplomaTypeChangeListener(CurriculumView curriculumView) {
        this.curriculumView=curriculumView;
    }

    @Override
    public void valueChange(Property.ValueChangeEvent ev) {
        STUDENT_DIPLOMA_TYPE type = (STUDENT_DIPLOMA_TYPE) ev.getProperty().getValue();
        if (type != null) {
            SPECIALITY speciality = (SPECIALITY) curriculumView.specialityCB.getValue();
            ENTRANCE_YEAR entranceYear = (ENTRANCE_YEAR) curriculumView.entranceYearCB.getValue();
            if (speciality != null && entranceYear != null) {
                curriculumView.findCurriculum(speciality, entranceYear, type);
            }
        }
    }
}
