package kz.halyqsoft.univercity.modules.registration.student;

import com.vaadin.ui.Alignment;
import com.vaadin.ui.TabSheet;
import kz.halyqsoft.univercity.modules.curriculum.working.schedule.SchedulePanel;
import org.r3a.common.entity.beans.AbstractTask;
import org.r3a.common.vaadin.view.AbstractTaskView;

/**
 * @author Omarbek Dinassil
 * @created Oct 31, 2016 6:15:08 PM
 */
@SuppressWarnings({"serial"})
public class RegistrationView extends AbstractTaskView {

    private SubjectPanel subjectPanel;

    public RegistrationView(AbstractTask task) throws Exception {
        super(task);
    }

    @Override
    public void initView(boolean readOnly) throws Exception {
        TabSheet ts = new TabSheet();

        subjectPanel = new SubjectPanel(this);
        subjectPanel.initPanel();
        ts.addTab(subjectPanel, getUILocaleUtil().getCaption("curriculum.schedule"));

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
