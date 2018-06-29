package kz.halyqsoft.univercity.modules.individualeduplan.student;

import com.vaadin.ui.Alignment;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.TabSheet;
import kz.halyqsoft.univercity.entity.beans.ROLES;
import kz.halyqsoft.univercity.entity.beans.univercity.CURRICULUM;
import kz.halyqsoft.univercity.entity.beans.univercity.STUDENT_EDUCATION;
import kz.halyqsoft.univercity.entity.beans.univercity.STUDENT_SUBJECT;
import kz.halyqsoft.univercity.modules.curriculum.working.semester.AddProgramPanel;
import kz.halyqsoft.univercity.modules.individualeduplan.student.SubjectPanel;
import org.r3a.common.entity.beans.AbstractTask;
import org.r3a.common.vaadin.view.AbstractTaskView;

import java.util.List;

/**
 * @author Omarbek Dinassil
 * @created Oct 31, 2016 6:15:08 PM
 */
@SuppressWarnings({"serial"})
public class RegistrationView extends AbstractTaskView {

    private SubjectPanel subjectPanel;
    private SemesterPanel semesterPanel;

    private STUDENT_SUBJECT studentSubject;
    private Label statusLabel;
    private CURRICULUM curriculum;
    private AddProgramPanel addProgramPanel;

    public RegistrationView(AbstractTask task) throws Exception {
        super(task);
    }

    @Override
    public void initView(boolean readOnly) throws Exception {
        TabSheet ts = new TabSheet();
        HorizontalLayout hl = new HorizontalLayout();


        semesterPanel = new SemesterPanel(this,1);
        semesterPanel.initPanel();
        ts.addTab(semesterPanel, getUILocaleUtil().getCaption("semester.1"));

        STUDENT_EDUCATION studentEducation = null;
        semesterPanel = new SemesterPanel(this,2);
        semesterPanel.initPanel();
        ts.addTab(semesterPanel, getUILocaleUtil().getCaption("semester.2"));

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