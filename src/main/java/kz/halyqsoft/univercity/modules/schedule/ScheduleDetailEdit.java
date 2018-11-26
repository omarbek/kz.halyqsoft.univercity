package kz.halyqsoft.univercity.modules.schedule;

import com.vaadin.data.Property;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import kz.halyqsoft.univercity.entity.beans.USERS;
import kz.halyqsoft.univercity.entity.beans.univercity.SCHEDULE_DETAIL;
import kz.halyqsoft.univercity.entity.beans.univercity.STREAM_GROUP;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.EMPLOYEE_TYPE;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.LESSON_TYPE;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.SEMESTER_DATA;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.WEEK_DAY;
import kz.halyqsoft.univercity.entity.beans.univercity.view.V_EMPLOYEE;
import kz.halyqsoft.univercity.utils.CommonUtils;
import org.r3a.common.dblink.facade.CommonEntityFacadeBean;
import org.r3a.common.dblink.utils.SessionFacadeFactory;
import org.r3a.common.entity.Entity;
import org.r3a.common.entity.ID;
import org.r3a.common.entity.event.EntityEvent;
import org.r3a.common.entity.event.EntityListener;
import org.r3a.common.entity.query.QueryModel;
import org.r3a.common.entity.query.from.EJoin;
import org.r3a.common.entity.query.from.FromItem;
import org.r3a.common.entity.query.where.ECriteria;
import org.r3a.common.vaadin.AbstractWebUI;
import org.r3a.common.vaadin.widget.dialog.AbstractDialog;
import org.r3a.common.vaadin.widget.dialog.Message;
import org.r3a.common.vaadin.widget.form.CommonFormWidget;
import org.r3a.common.vaadin.widget.form.FormModel;
import org.r3a.common.vaadin.widget.form.field.fk.FKFieldModel;
import org.r3a.common.vaadin.widget.grid.GridWidget;

import javax.persistence.NoResultException;
import java.util.List;

public class ScheduleDetailEdit extends AbstractDialog implements EntityListener {

    private ScheduleManual scheduleManual;
    private  SCHEDULE_DETAIL scheduleDetail;
    private CommonFormWidget scheduleDetailCFW;
    private GridWidget scheduleDetailGW;
    private final boolean isNew;
    private ID scheduleDeatilID;
    private  LESSON_TYPE lessonTypes;
    private FormModel scheduleFM;

    private static final int INFORMATIONAL_STUDY_DIRECT = 9;
    private static final String COMPUTER = "Компьютер";

    private static final int LECTURE = 1;
    private static final int PRACTICE = 3;

    private static final int LECTURE_COUNT = 2;
    private static final int PRACTICE_COUNT = 2;
    private static SEMESTER_DATA currentSemesterData;


    ScheduleDetailEdit(ScheduleManual scheduleManual, SCHEDULE_DETAIL scheduleDetail, boolean isNew)
            throws Exception{
        this.scheduleDetail = scheduleDetail;
        this.scheduleManual = scheduleManual;
        this.isNew = isNew;
        currentSemesterData = CommonUtils.getCurrentSemesterData();
        QueryModel<LESSON_TYPE> lessonTypeQuety = new QueryModel<>(LESSON_TYPE.class);
        lessonTypeQuety.addWhere("id",ECriteria.EQUAL,ID.valueOf(1));
        lessonTypes = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookupSingle(lessonTypeQuety);

        if(scheduleDetail!=null){
            scheduleDeatilID = scheduleDetail.getId();
        }

        setHeight("500");
        setWidth("700");
        center();

        scheduleDetailCFW = getScheduleDetailCFW();
        FKFieldModel lessonFM = (FKFieldModel) scheduleFM.getFieldModel("lessonType");
        if(scheduleFM.getFieldModel("group").getValue()!=null&&scheduleFM.getFieldModel("stream").getValue()!=null) {
            lessonFM.getListeners().add(new Property.ValueChangeListener() {
                @Override
                public void valueChange(Property.ValueChangeEvent valueChangeEvent) {
                    if (lessonFM.getValue() != null) {
                        if (lessonFM.getValue().equals(lessonTypes)) {
                            scheduleFM.getFieldModel("group").setReadOnly(true);
                            scheduleFM.getFieldModel("stream").getField().setEnabled(true);
                        } else {
                            scheduleFM.getFieldModel("group").setReadOnly(false);
                            scheduleFM.getFieldModel("stream").getField().setEnabled(false);
                        }
                    }
                }
            });
        }

        getContent().addComponent(scheduleDetailCFW);
        getContent().setComponentAlignment(scheduleDetailCFW,Alignment.MIDDLE_CENTER);

        Button saveButton = CommonUtils.createSaveButton();
        saveButton.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent ev) {

                if (scheduleDetailCFW.getWidgetModel().isModified()) {
                    scheduleDetailCFW.save();

                }
            }
        });

        Button closeButton = new Button(getUILocaleUtil().getCaption("close"));
        closeButton.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {
                close();
                try {
                } catch (Exception e) {
                    CommonUtils.showMessageAndWriteLog("Unable to refresh elective subject view", e);
                }
            }
        });

        HorizontalLayout buttonsHL = CommonUtils.createButtonPanel();
        buttonsHL.addComponents(saveButton, closeButton);
        buttonsHL.setComponentAlignment(saveButton, Alignment.MIDDLE_CENTER);
        buttonsHL.setComponentAlignment(closeButton, Alignment.MIDDLE_CENTER);
        getContent().addComponent(buttonsHL);
        getContent().setComponentAlignment(buttonsHL, Alignment.MIDDLE_CENTER);

        AbstractWebUI.getInstance().addWindow(this);
    }

    private CommonFormWidget getScheduleDetailCFW()throws Exception{

        CommonFormWidget scheduleDetailCFW = new CommonFormWidget(SCHEDULE_DETAIL.class);
        scheduleDetailCFW.addEntityListener(new ScheduleDetailListener());
        scheduleFM = scheduleDetailCFW.getWidgetModel();
        FKFieldModel fkfm = (FKFieldModel) scheduleFM.getFieldModel("stream");
        fkfm.setDialogHeight(400);
        fkfm.setDialogWidth(500);
        QueryModel streamQM = ((FKFieldModel)scheduleFM.getFieldModel("stream")).getQueryModel();
        streamQM.addJoin(EJoin.LEFT_JOIN,"id",STREAM_GROUP.class,"stream_id");
        QueryModel scheduleDetailTeacherQM = ((FKFieldModel)scheduleFM.getFieldModel("teacher")).getQueryModel();
        scheduleDetailTeacherQM.addJoin(EJoin.INNER_JOIN,"id" , USERS.class ,"id");
        FromItem vEmployeeFI = scheduleDetailTeacherQM.addJoin(EJoin.INNER_JOIN,"id", V_EMPLOYEE.class,"id");
        scheduleDetailTeacherQM.addWhere(vEmployeeFI , "employeeType" , ECriteria.EQUAL , EMPLOYEE_TYPE.TEACHER_ID);
        ((FKFieldModel)scheduleFM.getFieldModel("subject")).getQueryModel().addWhere("deleted", ECriteria.EQUAL , false);
        ((FKFieldModel)scheduleFM.getFieldModel("group")).getQueryModel().addWhere("deleted", ECriteria.EQUAL , false);

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


    @Override
    protected String createTitle() {
        return "Schedule Detail";
    }

    private class ScheduleDetailListener implements EntityListener{

        @Override
        public boolean preSave(Object source, Entity e, boolean isNew, int i) throws Exception {
            SCHEDULE_DETAIL scheduleDetail = (SCHEDULE_DETAIL) e;
            if (isNew) {

                if(scheduleFM.getFieldModel("group").getField()!=null&&scheduleFM.getFieldModel("stream").getField()!=null) {

                    scheduleDetail.setWeekDay((WEEK_DAY) scheduleManual.getWeekDayGW().getSelectedEntity());
                    scheduleDetail.setSemesterData((SEMESTER_DATA) scheduleManual.getSemesterDataCB().getValue());
                }
                try {
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
            if(scheduleManual.getSemesterDataCB().getValue()==null){
            Message.showError(getUILocaleUtil().getMessage("select.semester"));

        }
            if(scheduleManual.getWeekDayGW().getSelectedEntity()==null){
            Message.showError(getUILocaleUtil().getMessage("select.week.day"));
        }
        return true;
        }

        @Override
        public void onCreate(Object o, Entity entity, int i) {
            if(o.equals(scheduleFM)){
                scheduleFM.getFieldModel("group").getField().setEnabled(false);
                scheduleFM.getFieldModel("stream").getField().setEnabled(false);

            }
        }

        @Override
        public boolean onEdit(Object o, Entity entity, int i) {
            return true;
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
            return true;
        }

        @Override
        public void onDelete(Object o, List<Entity> list, int i) {
            try{
                SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).delete(list);
            }catch (Exception e){
                e.printStackTrace();
            }
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
    public void handleEntityEvent(EntityEvent ev) {

    }

    @Override
    public boolean preCreate(Object source, int buttonId) {
        if(scheduleManual.getSemesterDataCB().getValue()==null){
            Message.showError(getUILocaleUtil().getMessage("select.semester"));

        }
        if(scheduleManual.getWeekDayGW().getSelectedEntity()==null){
            Message.showError(getUILocaleUtil().getMessage("select.week.day"));
        }
        return true;
    }

    @Override
    public boolean preSave(Object source, Entity e, boolean isNew, int buttonId) {
        return false;
    }

        @Override
    public void onDelete(Object source, List<Entity> entities, int buttonId) {

    }

    @Override
    public void onCreate(Object o, Entity entity, int i) {

    }

    @Override
    public boolean onEdit(Object o, Entity entity, int i) {
        return true;
    }

    @Override
    public boolean onPreview(Object o, Entity entity, int i) {
        return true;
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
        return true;
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
