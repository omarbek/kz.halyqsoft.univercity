package kz.halyqsoft.univercity.modules.student.tabs;

import com.vaadin.ui.Alignment;
import com.vaadin.ui.VerticalLayout;
import kz.halyqsoft.univercity.entity.beans.univercity.USER_SOCIAL_CATEGORY;
import kz.halyqsoft.univercity.entity.beans.univercity.view.V_USER_SOCIAL_CATEGORY;
import kz.halyqsoft.univercity.modules.student.StudentEdit;
import kz.halyqsoft.univercity.utils.CommonUtils;
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
public class SocialCategoriesTab extends VerticalLayout {

    private final StudentEdit.StudentEditHelper studentEditHelper;

    private TableWidget socialCategoriesTW;

    public SocialCategoriesTab(StudentEdit.StudentEditHelper studentEditHelper, boolean readOnly) {
        this.studentEditHelper = studentEditHelper;

        setSpacing(true);
        setSizeFull();

        socialCategoriesTW = new TableWidget(V_USER_SOCIAL_CATEGORY.class);
        socialCategoriesTW.addEntityListener(new SocialCategoriesEntityListener());
        DBTableModel socialCategoriesTM = (DBTableModel) socialCategoriesTW.getWidgetModel();
        socialCategoriesTM.setReadOnly(readOnly);
        QueryModel socialCategoriesQM = socialCategoriesTM.getQueryModel();
        socialCategoriesQM.addWhere("user", ECriteria.EQUAL, studentEditHelper.getStudent().getId());

        FormModel socialCategoryFM = socialCategoriesTM.getFormModel();
        FKFieldModel socialCategoryFieldModel = (FKFieldModel) socialCategoryFM.getFieldModel("socialCategory");
        QueryModel socialCategoryQM = socialCategoryFieldModel.getQueryModel();
        socialCategoryQM.addOrder("categoryName");

        addComponent(socialCategoriesTW);
        setComponentAlignment(socialCategoriesTW, Alignment.MIDDLE_CENTER);
    }

    private class SocialCategoriesEntityListener implements EntityListener {

        @Override
        public boolean preCreate(Object o, int i) {
            if (studentEditHelper.isStudentNew()) {
                Message.showInfo(studentEditHelper.getUiLocaleUtil().getMessage("info.save.base.data.first"));
                return false;
            }
            return true;
        }

        @Override
        public boolean preSave(Object o, Entity entity, boolean isNew, int i) throws Exception {
            V_USER_SOCIAL_CATEGORY vtUserSocialCategory = (V_USER_SOCIAL_CATEGORY) entity;
            USER_SOCIAL_CATEGORY usc;
            if (isNew) {
                usc = new USER_SOCIAL_CATEGORY();
                try {
                    usc.setUser(studentEditHelper.getStudent());
                    usc.setSocialCategory(vtUserSocialCategory.getSocialCategory());
                    SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).create(usc);

                    QueryModel socialCategoriesQM = ((DBTableModel) socialCategoriesTW.getWidgetModel()).getQueryModel();
                    socialCategoriesQM.addWhere("user", ECriteria.EQUAL, studentEditHelper.getStudent().getId());

                    socialCategoriesTW.refresh();
                    studentEditHelper.showSavedNotification();
                } catch (Exception ex) {
                    CommonUtils.showMessageAndWriteLog("Unable to create a social category", ex);
                }
            } else {
                try {
                    usc = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(USER_SOCIAL_CATEGORY.class, vtUserSocialCategory.getId());
                    usc.setSocialCategory(vtUserSocialCategory.getSocialCategory());
                    SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).merge(usc);
                    socialCategoriesTW.refresh();
                    studentEditHelper.showSavedNotification();
                } catch (Exception ex) {
                    CommonUtils.showMessageAndWriteLog("Unable to merge a social category", ex);
                }
            }
            return false;
        }

        @Override
        public boolean preDelete(Object o, List<Entity> entities, int i) {
            List<USER_SOCIAL_CATEGORY> delList = new ArrayList<>();
            for (Entity e : entities) {
                try {
                    delList.add(SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(USER_SOCIAL_CATEGORY.class, e.getId()));
                } catch (Exception ex) {
                    CommonUtils.showMessageAndWriteLog("Unable to delete user social categories", ex);
                }
            }

            try {
                SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).delete(delList);
                socialCategoriesTW.refresh();
            } catch (Exception ex) {
                CommonUtils.LOG.error("Unable to delete user social categories: ", ex);
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

        @Override
        public void handleEntityEvent(EntityEvent entityEvent) {
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
    }
}
