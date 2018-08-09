package kz.halyqsoft.univercity.modules.graduateemployment;

import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import kz.halyqsoft.univercity.entity.beans.univercity.GRADUATE_EMPLOYMENT;
import kz.halyqsoft.univercity.entity.beans.univercity.view.VGraduate;
import kz.halyqsoft.univercity.entity.beans.univercity.view.V_STUDENT;
import kz.halyqsoft.univercity.utils.CommonUtils;
import org.r3a.common.dblink.facade.CommonEntityFacadeBean;
import org.r3a.common.dblink.utils.SessionFacadeFactory;
import org.r3a.common.entity.Entity;
import org.r3a.common.entity.ID;
import org.r3a.common.entity.event.EntityEvent;
import org.r3a.common.entity.event.EntityListener;
import org.r3a.common.entity.query.QueryModel;
import org.r3a.common.entity.query.from.EJoin;
import org.r3a.common.entity.query.where.ECriteria;
import org.r3a.common.vaadin.AbstractWebUI;
import org.r3a.common.vaadin.widget.dialog.AbstractDialog;
import org.r3a.common.vaadin.widget.dialog.Message;
import org.r3a.common.vaadin.widget.form.CommonFormWidget;
import org.r3a.common.vaadin.widget.form.FormModel;
import org.r3a.common.vaadin.widget.form.field.fk.FKFieldModel;

import javax.persistence.NoResultException;
import java.util.ArrayList;
import java.util.List;

public class GraduateEmploymentEdit extends AbstractDialog {

    private CommonFormWidget graduateEmploymentCFW;
    private ID graduateEmploymentID;
    private final boolean isNew;
    private GraduateEmploymentView graduateEmploymentView;

    GraduateEmploymentEdit(VGraduate graduateEmployment, boolean isNew, GraduateEmploymentView graduateEmploymentView) throws Exception {
        this.isNew = isNew;
        this.graduateEmploymentView = graduateEmploymentView;

        if(graduateEmployment!=null){
            graduateEmploymentID = graduateEmployment.getId();
        }

        setWidth(500, Unit.PIXELS);
        setHeight(500, Unit.PIXELS);
        center();

        graduateEmploymentCFW = createGraduateEmployment();
        getContent().addComponent(graduateEmploymentCFW);
        getContent().setComponentAlignment(graduateEmploymentCFW, Alignment.TOP_CENTER);

        Button saveButton = CommonUtils.createSaveButton();
        saveButton.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent ev) {
                if (graduateEmploymentCFW.getWidgetModel().isModified()) {
                    graduateEmploymentCFW.save();
                    graduateEmploymentView.refresh();
                    close();
                }
            }
        });

        Button cancelButton = CommonUtils.createCancelButton();
        cancelButton.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent ev) {
                graduateEmploymentCFW.cancel();
            }
        });

        HorizontalLayout buttonsHL = CommonUtils.createButtonPanel();
        buttonsHL.addComponents(saveButton, cancelButton);
        buttonsHL.setComponentAlignment(saveButton, Alignment.MIDDLE_CENTER);
        buttonsHL.setComponentAlignment(cancelButton, Alignment.MIDDLE_CENTER);
        getContent().addComponent(buttonsHL);
        getContent().setComponentAlignment(buttonsHL, Alignment.BOTTOM_CENTER);

        AbstractWebUI.getInstance().addWindow(this);
    }

    private CommonFormWidget createGraduateEmployment() throws Exception {
        CommonFormWidget graduateEmploymentFW = new CommonFormWidget(GRADUATE_EMPLOYMENT.class);
        graduateEmploymentFW.addEntityListener(new GraduateEmployment());
        final FormModel graduateEmploymentFM = graduateEmploymentFW.getWidgetModel();
        graduateEmploymentFM.setReadOnly(false);
        String errorMessage = getUILocaleUtil().getCaption("title.error").concat(": ").
                concat("Graduate employment");//TODO resource
        graduateEmploymentFM.setErrorMessageTitle(errorMessage);
        graduateEmploymentFM.setButtonsVisible(false);

        if (!isNew) {
            graduateEmploymentFM.getFieldModel("student").setReadOnly(true);
        }
        QueryModel<V_STUDENT> studentQM = new QueryModel<>(V_STUDENT.class);
        studentQM.addJoin(EJoin.INNER_JOIN, "id", GRADUATE_EMPLOYMENT.class,
                "student");
        List<V_STUDENT> students = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).
                lookup(studentQM);
        List<ID> ids = new ArrayList<>();
        for (V_STUDENT student : students) {
            ids.add(student.getId());
        }

        QueryModel studentEmploymentQM = ((FKFieldModel) graduateEmploymentFM.getFieldModel("student")).getQueryModel();
        studentEmploymentQM.addWhere("studentStatus",ECriteria.EQUAL,ID.valueOf(2));

        if (isNew) {
            GRADUATE_EMPLOYMENT graduateEmployment = (GRADUATE_EMPLOYMENT) graduateEmploymentFM.createNew();
        } else {
            try {
                GRADUATE_EMPLOYMENT graduateEmployment = SessionFacadeFactory.getSessionFacade(
                        CommonEntityFacadeBean.class).lookup(GRADUATE_EMPLOYMENT.class,graduateEmploymentID);
                if (graduateEmployment != null) {
                    graduateEmploymentFM.loadEntity(graduateEmployment.getId());
                }
            } catch (NoResultException ex) {
                GRADUATE_EMPLOYMENT graduateEmployment = (GRADUATE_EMPLOYMENT) graduateEmploymentFM.
                        createNew();
            }
        }
        return graduateEmploymentFW;
    }

    private class GraduateEmployment implements EntityListener {

        @Override
        public boolean preSave(Object o, Entity entity, boolean isNew, int i) throws Exception {
            GRADUATE_EMPLOYMENT graduateEmployment = (GRADUATE_EMPLOYMENT) entity;
            if (isNew) {
                try {
                    SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).create(graduateEmployment);
                    graduateEmploymentCFW.getWidgetModel().loadEntity(graduateEmployment.getId());
                    graduateEmploymentCFW.refresh();
                    CommonUtils.showSavedNotification();

                } catch (Exception ex) {
                    CommonUtils.showMessageAndWriteLog("Unable to create binding elective subject", ex);
                }
            } else {
                try {
                    SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).merge(graduateEmployment);
                    CommonUtils.showSavedNotification();
                } catch (Exception ex) {
                    CommonUtils.showMessageAndWriteLog("Unable to merge binding elective subject", ex);
                }
            }
            return false;
        }

        @Override
        public void handleEntityEvent(EntityEvent entityEvent) {
        }

        @Override
        public boolean preCreate(Object o, int i) {
            if (graduateEmploymentCFW.getWidgetModel().isCreateNew()) {
                Message.showInfo(getUILocaleUtil().getMessage("info.save.base.data.first"));
                return false;
            }
            return  true;
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

    @Override
    protected String createTitle() {
        return "Graduate employment";
    }
}
