package kz.halyqsoft.univercity.modules.student;

import kz.halyqsoft.univercity.entity.beans.univercity.STUDENT;
import kz.halyqsoft.univercity.filter.FStudentFilter;
import org.r3a.common.dblink.facade.CommonEntityFacadeBean;
import org.r3a.common.dblink.utils.SessionFacadeFactory;
import org.r3a.common.entity.Entity;
import org.r3a.common.entity.beans.AbstractTask;
import org.r3a.common.entity.event.EntityEvent;
import org.r3a.common.vaadin.view.AbstractTaskView;
import org.r3a.common.vaadin.widget.dialog.Message;
import org.r3a.common.vaadin.widget.form.FormModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * @author Omarbek
 * @created on 02.04.2018
 */
public class StudentView extends AbstractTaskView {

    private AbstractStudentList abstractStudentList;

    public StudentView(AbstractTask task) throws Exception {
        super(task);
    }

    @Override
    public void initView(boolean b) throws Exception {
        abstractStudentList = new AbstractStudentList(new FStudentFilter(), false, false,getUILocaleUtil());
        getContent().addComponent(abstractStudentList);
    }

    @Override
    public void handleEntityEvent(EntityEvent ev) {
        if (ev.getSource().equals(abstractStudentList.studentGW)) {
            if (ev.getAction() == EntityEvent.SELECTED) {
                List<Entity> selectedList = ev.getEntities();
                if (!selectedList.isEmpty()) {
                    onEdit(ev.getSource(), selectedList.get(0), 2);
                }
            }
        }
    }

    @Override
    public boolean onEdit(Object source, Entity e, int buttonId) {
        if (source.equals(abstractStudentList.studentGW)) {
            FormModel fm = new FormModel(STUDENT.class);
            fm.setReadOnly(false);
            fm.setTitleVisible(false);
            try {
                fm.loadEntity(e.getId());
                StudentEdit se = new StudentEdit(fm, (FStudentFilter) abstractStudentList.filterPanel.getFilterBean());
//                StudentUI.getInstance().openCommonView(se);//TODO

                return false;
            } catch (Exception ex) {
                LOG.error("Unable to edit the student: ", ex);
                Message.showError("Unable to edit the student");
            }
        }

        return true;
    }

    @Override
    public boolean onPreview(Object source, Entity e, int buttonId) {
        if (source.equals(abstractStudentList.studentGW)) {
            FormModel fm = new FormModel(STUDENT.class);
            fm.setReadOnly(true);
            fm.setTitleVisible(false);
            try {
                fm.loadEntity(e.getId());
                StudentEdit se = new StudentEdit(fm, (FStudentFilter) abstractStudentList.filterPanel.getFilterBean());
//                StudentUI.getInstance().openCommonView(se);//TODO

                return false;
            } catch (Exception ex) {
                LOG.error("Unable to preview the student: ", ex);
                Message.showError("Unable to preview the student");
            }
        }

        return true;
    }

    @Override
    public boolean preDelete(Object source, List<Entity> entities, int buttonId) {
        if (source.equals(abstractStudentList.studentGW)) {
            List<STUDENT> mergeList = new ArrayList<>();
            for (Entity e : entities) {
                try {
                    STUDENT s = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).
                            lookup(STUDENT.class, e.getId());
                    s.setDeleted(true);
                    mergeList.add(s);
                } catch (Exception ex) {
                    LOG.error("Unable to delete the student: ", ex);
                }
            }

            try {
                SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).merge(mergeList);
                abstractStudentList.studentGW.refresh();
            } catch (Exception ex) {
                LOG.error("Unable to delete the students: ", ex);
            }
            return false;
        }

        return true;
    }
}
