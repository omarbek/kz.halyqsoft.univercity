package kz.halyqsoft.univercity.modules.student.tabs;

import com.vaadin.ui.Alignment;
import com.vaadin.ui.VerticalLayout;
import kz.halyqsoft.univercity.entity.beans.univercity.USER_AWARD;
import kz.halyqsoft.univercity.entity.beans.univercity.view.V_USER_AWARD;
import kz.halyqsoft.univercity.modules.student.StudentEdit;
import kz.halyqsoft.univercity.utils.ErrorUtils;
import org.r3a.common.dblink.facade.CommonEntityFacadeBean;
import org.r3a.common.dblink.utils.SessionFacadeFactory;
import org.r3a.common.entity.Entity;
import org.r3a.common.entity.event.EntityEvent;
import org.r3a.common.entity.event.EntityListener;
import org.r3a.common.entity.query.QueryModel;
import org.r3a.common.entity.query.where.ECriteria;
import org.r3a.common.vaadin.widget.dialog.Message;
import org.r3a.common.vaadin.widget.form.FormModel;
import org.r3a.common.vaadin.widget.form.field.fk.FKFieldModel;
import org.r3a.common.vaadin.widget.table.TableWidget;
import org.r3a.common.vaadin.widget.table.model.DBTableModel;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Omarbek
 * @created 17.05.2017.
 */
@SuppressWarnings("serial")
public class AwardsTab extends VerticalLayout {

    private final StudentEdit.StudentEditHelper studentEditHelper;

    private TableWidget awardsTW;

    public AwardsTab(StudentEdit.StudentEditHelper studentEditHelper, boolean readOnly) {
        this.studentEditHelper = studentEditHelper;

        setSpacing(true);
        setSizeFull();

        awardsTW = new TableWidget(V_USER_AWARD.class);
        awardsTW.addEntityListener(new AwardsListener());
        DBTableModel awardsTM = (DBTableModel) awardsTW.getWidgetModel();
        awardsTM.setReadOnly(readOnly);
        QueryModel awardsQM = awardsTM.getQueryModel();
        awardsQM.addWhere("user", ECriteria.EQUAL, studentEditHelper.getStudent().getId());

        FormModel awardsFM = awardsTM.getFormModel();
        FKFieldModel awardFM = (FKFieldModel) awardsFM.getFieldModel("award");
        QueryModel awardQM = awardFM.getQueryModel();
        awardQM.addOrder("awardName");

        addComponent(awardsTW);
        setComponentAlignment(awardsTW, Alignment.MIDDLE_CENTER);
    }

    private class AwardsListener implements EntityListener {

        @Override
        public void handleEntityEvent(EntityEvent entityEvent) {
        }

        @Override
        public boolean preCreate(Object o, int i) {
            if (studentEditHelper.isStudentNew()) {
                Message.showInfo(studentEditHelper.getUiLocaleUtil().getMessage("info.save.base.data.first"));
                return false;
            }

            return true;
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
            V_USER_AWARD vtUserAward = (V_USER_AWARD) entity;
            USER_AWARD userAward;
            if (isNew) {
                userAward = new USER_AWARD();
                try {
                    userAward.setUser(studentEditHelper.getStudent());
                    userAward.setAward(vtUserAward.getAward());
                    SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).create(userAward);

                    QueryModel awardsQM = ((DBTableModel) awardsTW.getWidgetModel()).getQueryModel();
                    awardsQM.addWhere("user", ECriteria.EQUAL, studentEditHelper.getStudent().getId());

                    awardsTW.refresh();
                    studentEditHelper.showSavedNotification();
                } catch (Exception ex) {
                    ErrorUtils.LOG.error("Unable to createCertificate an award: ", ex);
                }
            } else {
                try {
                    userAward = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(USER_AWARD.class, vtUserAward.getId());
                    userAward.setAward(vtUserAward.getAward());
                    SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).merge(userAward);
                    awardsTW.refresh();
                    studentEditHelper.showSavedNotification();
                } catch (Exception ex) {
                    ErrorUtils.LOG.error("Unable to merge an award: ", ex);
                }
            }

            return false;
        }

        @Override
        public boolean preDelete(Object o, List<Entity> entities, int i) {
            List<USER_AWARD> delList = new ArrayList<>();
            for (Entity e : entities) {
                try {
                    delList.add(SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(USER_AWARD.class, e.getId()));
                } catch (Exception ex) {
                    ErrorUtils.LOG.error("Unable to delete user awards: ", ex);
                }
            }

            try {
                SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).delete(delList);
                awardsTW.refresh();
            } catch (Exception ex) {
                ErrorUtils.LOG.error("Unable to delete user awards: ", ex);
                Message.showError(studentEditHelper.getUiLocaleUtil().getMessage("error.cannotdelentity"));
            }

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
