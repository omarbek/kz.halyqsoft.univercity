package kz.halyqsoft.univercity.modules.individualeduplan.student;

import com.vaadin.ui.Alignment;
import com.vaadin.ui.TabSheet;
import kz.halyqsoft.univercity.entity.beans.univercity.STUDENT_EDUCATION;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.SEMESTER;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.STUDY_YEAR;
import kz.halyqsoft.univercity.utils.CommonUtils;
import org.r3a.common.dblink.facade.CommonEntityFacadeBean;
import org.r3a.common.dblink.utils.SessionFacadeFactory;
import org.r3a.common.entity.beans.AbstractTask;
import org.r3a.common.entity.query.QueryModel;
import org.r3a.common.entity.query.where.ECriteria;
import org.r3a.common.vaadin.view.AbstractTaskView;

import java.util.List;

/**
 * @author Omarbek Dinassil
 * @created Oct 31, 2016 6:15:08 PM
 */
@SuppressWarnings({"serial"})
public class RegistrationView extends AbstractTaskView {

    private SubjectPanel subjectPanel;
    private STUDY_YEAR studyYear;
    private STUDENT_EDUCATION studentEducation;

    public RegistrationView(AbstractTask task) throws Exception {
        super(task);
    }

    @Override
    public void initView(boolean readOnly) throws Exception {
        TabSheet ts = new TabSheet();

        QueryModel<STUDENT_EDUCATION> studentEducationQM = new QueryModel<>(STUDENT_EDUCATION.class);
        studentEducationQM.addWhere("student", ECriteria.EQUAL, CommonUtils.getCurrentUser().getId());
        studentEducationQM.addWhereNull("child");
        studentEducation = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookupSingle(studentEducationQM);


        QueryModel<SEMESTER> semesterQM = new QueryModel<SEMESTER>(SEMESTER.class);
           semesterQM.addWhere("studyYear",ECriteria.EQUAL,studentEducation.getStudyYear().getId());
            List<SEMESTER> semesterList = SessionFacadeFactory.
                    getSessionFacade(CommonEntityFacadeBean.class).lookup(semesterQM);
            for (SEMESTER s : semesterList) {
                subjectPanel = new SubjectPanel(this,s);
                subjectPanel.initPanel();

                ts.addTab(subjectPanel, getUILocaleUtil().getCaption("semester." + s.getId()));
            }

            ts.addSelectedTabChangeListener(new SemesterChangeListener());

            getContent().addComponent(ts);
            getContent().setComponentAlignment(ts, Alignment.MIDDLE_CENTER);
            getContent().setExpandRatio(ts, 1);
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