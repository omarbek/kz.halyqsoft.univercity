package kz.halyqsoft.univercity.modules.student.tabs;

import com.vaadin.ui.Alignment;
import com.vaadin.ui.HorizontalLayout;
import kz.halyqsoft.univercity.entity.beans.univercity.GRADUATION_PROJECT;
import kz.halyqsoft.univercity.modules.student.StudentEdit;
import kz.halyqsoft.univercity.utils.ErrorUtils;
import org.r3a.common.dblink.facade.CommonEntityFacadeBean;
import org.r3a.common.dblink.utils.SessionFacadeFactory;
import org.r3a.common.entity.Entity;
import org.r3a.common.entity.event.EntityEvent;
import org.r3a.common.entity.event.EntityListener;
import org.r3a.common.entity.query.QueryModel;
import org.r3a.common.vaadin.widget.form.CommonFormWidget;
import org.r3a.common.vaadin.widget.form.FormModel;

import java.util.List;

/**
 * @author Omarbek
 * @created 15.05.2017.
 */
@SuppressWarnings("serial")
public class DiplomaTab extends HorizontalLayout {

    private final StudentEdit.StudentEditHelper studentEditHelper;

    public DiplomaTab(StudentEdit.StudentEditHelper studentEditHelper, boolean readOnly) {
        this.studentEditHelper = studentEditHelper;

        setSizeFull();

        CommonFormWidget diplomaFormWidget = new CommonFormWidget(GRADUATION_PROJECT.class);
        diplomaFormWidget.addEntityListener(new DiplomaEntityListener());
        addComponent(diplomaFormWidget);
        setComponentAlignment(diplomaFormWidget, Alignment.TOP_CENTER);

        FormModel diplomaFormModel = diplomaFormWidget.getWidgetModel();
        diplomaFormModel.setTitleResource("graduation.project");
        diplomaFormModel.setReadOnly(readOnly);
        try {
            if (studentEditHelper.getStudent().getGraduationProject() == null) {
                diplomaFormModel.createNew();
            } else {
                diplomaFormModel.loadEntity(studentEditHelper.getStudent().getGraduationProject().getId());
            }
        } catch (Exception e) {
            ErrorUtils.LOG.error("Ошибка дипломного проекта", e);
        }
    }

    private class DiplomaEntityListener implements EntityListener {

        @Override
        public void handleEntityEvent(EntityEvent entityEvent) {
        }

        @Override
        public boolean preCreate(Object o, int i) {
            return false;
        }

        @Override
        public void onCreate(Object o, Entity entity, int i) {
        }

        @Override
        public boolean onEdit(Object o, Entity entity, int i) {
            return false;
        }

        @Override
        public boolean onPreview(Object o, Entity entity, int i) {
            return false;
        }

        @Override
        public void beforeRefresh(Object o, int i) {
        }

        @Override
        public void onRefresh(Object o, List<Entity> list) {
        }

        @Override
        public void onFilter(Object o, QueryModel queryModel, int i) {
        }

        @Override
        public void onAccept(Object o, List<Entity> list, int i) {
        }

        @Override
        public boolean preSave(Object o, Entity entity, boolean isNew, int i) throws Exception {
            GRADUATION_PROJECT graduationProject = (GRADUATION_PROJECT) entity;
            if (isNew) {
                graduationProject.setId(studentEditHelper.getStudent().getId());
                SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).createNoID(graduationProject);
                studentEditHelper.showSavedNotification();
            } else {
                SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).merge(graduationProject);
                studentEditHelper.showSavedNotification();
            }
            return false;
        }

        @Override
        public boolean preDelete(Object o, List<Entity> list, int i) {
            return false;
        }

        @Override
        public void onDelete(Object o, List<Entity> list, int i) {
        }

        @Override
        public void deferredCreate(Object o, Entity entity) {
        }

        @Override
        public void deferredDelete(Object o, List<Entity> list) {
        }

        @Override
        public void onException(Object o, Throwable throwable) {
        }
    }
}
