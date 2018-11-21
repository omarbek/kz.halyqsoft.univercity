package kz.halyqsoft.univercity.modules.schedule;

import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import kz.halyqsoft.univercity.entity.beans.univercity.SCHEDULE_DETAIL;
import kz.halyqsoft.univercity.entity.beans.univercity.STREAM_GROUP;
import kz.halyqsoft.univercity.utils.CommonUtils;
import org.r3a.common.dblink.facade.CommonEntityFacadeBean;
import org.r3a.common.dblink.utils.SessionFacadeFactory;
import org.r3a.common.entity.Entity;
import org.r3a.common.entity.ID;
import org.r3a.common.entity.event.EntityEvent;
import org.r3a.common.entity.event.EntityListener;
import org.r3a.common.entity.query.QueryModel;
import org.r3a.common.vaadin.AbstractWebUI;
import org.r3a.common.vaadin.widget.dialog.AbstractDialog;
import org.r3a.common.vaadin.widget.dialog.Message;
import org.r3a.common.vaadin.widget.form.CommonFormWidget;
import org.r3a.common.vaadin.widget.form.FormModel;
import org.r3a.common.vaadin.widget.grid.GridWidget;

import javax.persistence.NoResultException;
import java.util.List;

public class ScheduleDetailEdit extends AbstractDialog {

    private ScheduleManual scheduleManual;
    private  SCHEDULE_DETAIL scheduleDetail;
    private CommonFormWidget scheduleDetailCFW;
    private GridWidget scheduleDetailGW;
    private final boolean isNew;
    private ID scheduleDeatilID;

    ScheduleDetailEdit(ScheduleManual scheduleManual, SCHEDULE_DETAIL scheduleDetail, boolean isNew)
            throws Exception{
        this.scheduleDetail = scheduleDetail;
        this.scheduleManual = scheduleManual;
        this.isNew = isNew;

        if(scheduleDetail!=null){
            scheduleDeatilID = scheduleDetail.getId();
        }

        setHeight("400");
        setWidth("600");
        center();

        scheduleDetailCFW = getScheduleDetailCFW();
        getContent().addComponent(scheduleDetailCFW);
        getContent().setComponentAlignment(scheduleDetailCFW,Alignment.MIDDLE_CENTER);

        Button saveButton = CommonUtils.createSaveButton();
        saveButton.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent ev) {
                if (scheduleDetailCFW.getWidgetModel().isModified()) {
                    scheduleDetailCFW.save();
                    scheduleDetailGW.setVisible(true);
                }
            }
        });

        Button cancelButton = CommonUtils.createCancelButton();
        cancelButton.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent ev) {

            }
        });

        HorizontalLayout buttonsHL = CommonUtils.createButtonPanel();
        buttonsHL.addComponents(saveButton, cancelButton);
        buttonsHL.setComponentAlignment(saveButton, Alignment.MIDDLE_CENTER);
        buttonsHL.setComponentAlignment(cancelButton, Alignment.MIDDLE_CENTER);
        getContent().addComponent(buttonsHL);
        getContent().setComponentAlignment(buttonsHL, Alignment.BOTTOM_CENTER);

        scheduleDetailGW = new GridWidget(STREAM_GROUP.class);
        getContent().addComponent(scheduleDetailGW);
        getContent().setComponentAlignment(scheduleDetailGW,Alignment.MIDDLE_CENTER);

        Button closeButton = new Button(getUILocaleUtil().getCaption("close"));
        closeButton.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {
                close();
//                try {
//                    scheduleManual.refresh();
//                } catch (Exception e) {
//                    CommonUtils.showMessageAndWriteLog("Unable to refresh elective subject view", e);
//                }
            }
        });
        getContent().addComponent(closeButton);
        getContent().setComponentAlignment(closeButton, Alignment.MIDDLE_CENTER);

        AbstractWebUI.getInstance().addWindow(this);
    }

    private CommonFormWidget getScheduleDetailCFW()throws Exception{
        CommonFormWidget scheduleDetailCFW = new CommonFormWidget(SCHEDULE_DETAIL.class);
        scheduleDetailCFW.addEntityListener(new ScheduleDetailListener());
        FormModel scheduleFM = scheduleDetailCFW.getWidgetModel();
        scheduleFM.setReadOnly(false);
        String errorMessage = getUILocaleUtil().getCaption("title.error").concat(": ").
                concat("Binding elective subject");
        scheduleFM.setErrorMessageTitle(errorMessage);
        scheduleFM.setButtonsVisible(false);

        if (isNew) {
            SCHEDULE_DETAIL scheduleDetail = (SCHEDULE_DETAIL) scheduleFM.createNew();
        } else {
            try {
                SCHEDULE_DETAIL scheduleDetail = SessionFacadeFactory.getSessionFacade(
                        CommonEntityFacadeBean.class).lookup(SCHEDULE_DETAIL.class, scheduleDeatilID);
                if (scheduleDetail != null) {
                    scheduleFM.loadEntity(scheduleDetail.getId());
                }
            } catch (NoResultException ex) {
                SCHEDULE_DETAIL scheduleDetail = (SCHEDULE_DETAIL) scheduleFM.
                        createNew();
            }
        }

        return scheduleDetailCFW;
    }

    private GridWidget pairSubjectGridWidget() throws Exception {
        if (scheduleDeatilID == null && !scheduleDetailCFW.getWidgetModel().isCreateNew()) {
            SCHEDULE_DETAIL scheduleDetail = (SCHEDULE_DETAIL) scheduleDetailCFW.
                    getWidgetModel().getEntity();
            if (scheduleDetail != null) {
                scheduleDeatilID = scheduleDetail.getId();
            }
        }

        GridWidget scheduleGrid = new GridWidget(STREAM_GROUP.class);
        if (isNew) {
            scheduleGrid.setVisible(false);
            scheduleDeatilID = ID.valueOf(-1);
        }

        return scheduleGrid;
    }

    @Override
    protected String createTitle() {
        return "Schedule Detail";
    }

    private class ScheduleDetailListener implements EntityListener{

        @Override
        public boolean preSave(Object o, Entity entity, boolean isNew, int i) throws Exception {
            SCHEDULE_DETAIL scheduleDetail = (SCHEDULE_DETAIL) entity;
            if (isNew) {
                try {

//                    QueryModel<CATALOG_ELECTIVE_SUBJECTS> catQM = new QueryModel<>(CATALOG_ELECTIVE_SUBJECTS.class);
//                    SPECIALITY spec = (SPECIALITY) bindingElectiveSubjectView.getSpecCB().getValue();
//                    ENTRANCE_YEAR year = (ENTRANCE_YEAR) bindingElectiveSubjectView.getYearCB().getValue();
//                    catQM.addWhere("speciality", ECriteria.EQUAL, spec.getId());
//                    catQM.addWhere("entranceYear", ECriteria.EQUAL, year.getId());
//                    CATALOG_ELECTIVE_SUBJECTS cat = getElectiveSubjects(catQM, spec, year);
//                    scheduleDetail.setCatalogElectiveSubjects(cat);
                    SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).create(scheduleDetail);

                    scheduleDetailCFW.getWidgetModel().loadEntity(scheduleDetail.getId());
                    scheduleDetailCFW.refresh();

                    ScheduleDetailEdit.this.scheduleDetail = scheduleDetail;
                    CommonUtils.showSavedNotification();

                } catch (Exception ex) {
                    CommonUtils.showMessageAndWriteLog("Unable to create binding elective subject", ex);
                }
            } else {
                try {
                    SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).merge(scheduleDetail);
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
            if (scheduleDetailCFW.getWidgetModel().isCreateNew()) {
                Message.showInfo(getUILocaleUtil().getMessage("info.save.base.data.first"));
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
