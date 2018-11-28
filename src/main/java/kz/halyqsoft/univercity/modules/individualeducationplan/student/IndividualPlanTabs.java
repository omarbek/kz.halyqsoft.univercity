package kz.halyqsoft.univercity.modules.individualeducationplan.student;

import com.vaadin.ui.TabSheet;
import kz.halyqsoft.univercity.entity.beans.USERS;
import kz.halyqsoft.univercity.entity.beans.univercity.STUDENT_EDUCATION;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.SEMESTER;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.STUDY_YEAR;
import kz.halyqsoft.univercity.utils.CommonUtils;
import org.r3a.common.dblink.facade.CommonEntityFacadeBean;
import org.r3a.common.dblink.utils.SessionFacadeFactory;
import org.r3a.common.entity.query.QueryModel;
import org.r3a.common.entity.query.where.ECriteria;

import java.util.List;

class IndividualPlanTabs extends TabSheet {

    private STUDY_YEAR studyYear;

    IndividualPlanTabs(USERS user, boolean isAdmin) throws Exception {
        QueryModel<STUDENT_EDUCATION> studentEducationQM = new QueryModel<>(STUDENT_EDUCATION.class);
        studentEducationQM.addWhere("student", ECriteria.EQUAL, user.getId());
        studentEducationQM.addWhereNull("child");
        STUDENT_EDUCATION studentEducation = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookupSingle(studentEducationQM);

        QueryModel<SEMESTER> semesterQM = new QueryModel<>(SEMESTER.class);
        semesterQM.addWhere("studyYear", ECriteria.EQUAL, studentEducation.getStudyYear().getId());
        List<SEMESTER> semesterList = SessionFacadeFactory.
                getSessionFacade(CommonEntityFacadeBean.class).lookup(semesterQM);
        for (SEMESTER semester : semesterList) {
            SubjectsPanel subjectPanel = new SubjectsPanel(semester, studentEducation,isAdmin);
            subjectPanel.initPanel();

            addTab(subjectPanel, CommonUtils.getUILocaleUtil().getCaption("semester." + semester.getId()));
        }

        addSelectedTabChangeListener(new SemesterChangeListener());
    }

    private class SemesterChangeListener implements TabSheet.SelectedTabChangeListener {

        @Override
        public void selectedTabChange(TabSheet.SelectedTabChangeEvent ev) {
//            AbstractCurriculumPanel acp = (AbstractCurriculumPanel) ev.getTabSheet().getSelectedTab();
//            acp.setCurriculum(curriculum);
//            try {
//                acp.refresh();
//            } catch (Exception ex) {
//                LOG.error("Unable to refresh: ", ex);
//            }
        }
    }
}
