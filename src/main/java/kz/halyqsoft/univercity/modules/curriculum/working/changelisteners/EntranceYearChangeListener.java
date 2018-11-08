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
public class EntranceYearChangeListener implements Property.ValueChangeListener {

    private CurriculumView curriculumView;

    public EntranceYearChangeListener(CurriculumView curriculumView) {
        this.curriculumView=curriculumView;
    }

    @Override
    public void valueChange(Property.ValueChangeEvent ev) {
        ENTRANCE_YEAR entranceYear = (ENTRANCE_YEAR) ev.getProperty().getValue();
        if (entranceYear != null) {
            SPECIALITY speciality = (SPECIALITY) curriculumView.specialityCB.getValue();
            STUDENT_DIPLOMA_TYPE studentDiplomaType = (STUDENT_DIPLOMA_TYPE) curriculumView.diplomaTypeCB.getValue();
            if (speciality != null && studentDiplomaType != null) {
                curriculumView.findCurriculum(speciality, entranceYear, studentDiplomaType);
            }
        }
    }
}