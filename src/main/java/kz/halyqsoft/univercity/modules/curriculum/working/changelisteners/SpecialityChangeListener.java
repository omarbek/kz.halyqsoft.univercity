package kz.halyqsoft.univercity.modules.curriculum.working.changelisteners;

import com.vaadin.data.Property;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.ACADEMIC_DEGREE;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.ENTRANCE_YEAR;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.SPECIALITY;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.STUDENT_DIPLOMA_TYPE;
import kz.halyqsoft.univercity.modules.curriculum.working.main.CurriculumView;
import kz.halyqsoft.univercity.utils.CommonUtils;
import org.r3a.common.dblink.facade.CommonEntityFacadeBean;
import org.r3a.common.dblink.utils.SessionFacadeFactory;
import org.r3a.common.entity.query.QueryModel;
import org.r3a.common.entity.query.where.ECriteria;

/**
 * @author Omarbek
 * @created on 31.10.2018
 */
public class SpecialityChangeListener implements Property.ValueChangeListener {

    private CurriculumView curriculumView;

    public SpecialityChangeListener(CurriculumView curriculumView) {
        this.curriculumView=curriculumView;
    }

    @Override
    public void valueChange(Property.ValueChangeEvent valueChangeEvent) {
        SPECIALITY speciality = (SPECIALITY) valueChangeEvent.getProperty().getValue();
        if (speciality != null) {
            ENTRANCE_YEAR entranceYear = (ENTRANCE_YEAR) curriculumView.entranceYearCB.getValue();
            STUDENT_DIPLOMA_TYPE studentDiplomaType = (STUDENT_DIPLOMA_TYPE) curriculumView.diplomaTypeCB.getValue();
            if (entranceYear != null && studentDiplomaType != null) {
                curriculumView.findCurriculum(speciality, entranceYear, studentDiplomaType);
            }

            QueryModel<ACADEMIC_DEGREE> academicDegreeQM = new QueryModel<>(ACADEMIC_DEGREE.class);
            academicDegreeQM.addWhere("speciality", ECriteria.EQUAL, speciality.getId());
            try {
                ACADEMIC_DEGREE academicDegree = SessionFacadeFactory.getSessionFacade(
                        CommonEntityFacadeBean.class).lookupSingle(academicDegreeQM);

                String academicDegreeText = String.format(CommonUtils.getUILocaleUtil().
                        getCaption("academic.degree"), academicDegree.getDegreeName());
                String studyPeriodText = String.format(CommonUtils.getUILocaleUtil().
                        getCaption("study.period"), academicDegree.getStudyPeriod());

                curriculumView.academicDegreeLabel.setValue(academicDegreeText);
                curriculumView.studyPeriodLabel.setValue(studyPeriodText);
            } catch (Exception ex) {
                CommonUtils.LOG.error("Unable to locate academic degree: ", ex);
            }
        }
    }

}