package kz.halyqsoft.univercity.modules.schedule;

import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.server.ThemeResource;
import com.vaadin.shared.ui.combobox.FilteringMode;
import com.vaadin.ui.*;
import kz.halyqsoft.univercity.entity.beans.USERS;
import kz.halyqsoft.univercity.entity.beans.univercity.GROUPS;
import kz.halyqsoft.univercity.entity.beans.univercity.SCHEDULE_DETAIL;
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
import java.util.ArrayList;
import java.util.List;

public class ScheduleDetailEdit extends AbstractDialog implements EntityListener {

    private ScheduleManual scheduleManual;
    private  SCHEDULE_DETAIL scheduleDetail;
    private CommonFormWidget scheduleDetailCFW;
    private GridWidget scheduleDetailGW;
    private final boolean isNew;
    private ID scheduleDeatilID;
    private  LESSON_TYPE lessonTypes;
    private ComboBox groupsCB;
    private VerticalLayout mainVL;
    private static final int INFORMATIONAL_STUDY_DIRECT = 9;
    private static final String COMPUTER = "Компьютер";
    private ArrayList<ComboBox> groupsCBs = new ArrayList<>();
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

        HorizontalLayout HL = new HorizontalLayout();
        HL.setSpacing(true);
        scheduleDetailCFW = getScheduleDetailCFW();

        Label label = new Label(getUILocaleUtil().getEntityLabel(GROUPS.class));

        mainVL = new VerticalLayout();
        mainVL.setSpacing(true);

        groupsCB = new ComboBox();
        groupsCB.setNullSelectionAllowed(true);
        groupsCB.setTextInputAllowed(true);
        groupsCB.setWidth("300");
        groupsCB.setFilteringMode(FilteringMode.STARTSWITH);
        QueryModel<GROUPS> groupsQueryModel = new QueryModel<>(GROUPS.class);
        groupsQueryModel.addWhere("deleted",ECriteria.EQUAL,false);
        BeanItemContainer<GROUPS> groupsBIC = new BeanItemContainer<>(GROUPS.class,
                SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(groupsQueryModel));
        groupsCB.setContainerDataSource(groupsBIC);

        Button addComponentButton = new Button(new ThemeResource("img/button/add.png"));
        addComponentButton.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {

                HorizontalLayout HLayout = new HorizontalLayout();
                HLayout.setSpacing(true);

                Label label = new Label(getUILocaleUtil().getEntityLabel(GROUPS.class));

                groupsCB = new ComboBox();
                groupsCB.setNullSelectionAllowed(true);
                groupsCB.setTextInputAllowed(true);
                groupsCB.setWidth("300");
                groupsCB.setFilteringMode(FilteringMode.STARTSWITH);
                QueryModel<GROUPS> groupsQM = new QueryModel<>(GROUPS.class);
                groupsQM.addWhere("deleted",ECriteria.EQUAL,false);
                BeanItemContainer<GROUPS> groupsBIC = null;
                try {
                    groupsBIC = new BeanItemContainer<>(GROUPS.class,
                            SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(groupsQM));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                groupsCB.setContainerDataSource(groupsBIC);
                groupsCBs.add(groupsCB);
                HLayout.addComponent(label);
                HLayout.setComponentAlignment(label,Alignment.MIDDLE_CENTER);
                HLayout.addComponent(groupsCB);
                HLayout.setComponentAlignment(groupsCB,Alignment.MIDDLE_CENTER);
                HLayout.addComponent(addComponentButton);
                HLayout.setComponentAlignment(addComponentButton,Alignment.MIDDLE_CENTER);

                mainVL.addComponent(HLayout);
                mainVL.setComponentAlignment(HLayout,Alignment.MIDDLE_CENTER);

            }
        });

        HL.addComponent(label);
        HL.setComponentAlignment(label,Alignment.MIDDLE_CENTER);
        HL.addComponent(groupsCB);
        groupsCBs.add(groupsCB);
        HL.setComponentAlignment(groupsCB,Alignment.MIDDLE_LEFT);
        HL.addComponent(addComponentButton);
        HL.setComponentAlignment(addComponentButton,Alignment.MIDDLE_CENTER);

        getContent().addComponent(scheduleDetailCFW);
        getContent().setComponentAlignment(scheduleDetailCFW,Alignment.MIDDLE_CENTER);
        mainVL.addComponent(HL);
        mainVL.setComponentAlignment(HL,Alignment.MIDDLE_CENTER);
        getContent().addComponent(mainVL);
        getContent().setComponentAlignment(mainVL,Alignment.MIDDLE_CENTER);

        Button saveButton = CommonUtils.createSaveButton();
        saveButton.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent ev) {

                if (scheduleDetailCFW.getWidgetModel().isModified()) {
                    scheduleDetailCFW.save();

                }
            }
        });

        Button cancelButton = CommonUtils.createCancelButton();
        cancelButton.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent ev) {
                scheduleDetailCFW.cancel();
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
        buttonsHL.addComponents(saveButton, cancelButton);
        buttonsHL.setComponentAlignment(saveButton, Alignment.MIDDLE_CENTER);
        buttonsHL.setComponentAlignment(cancelButton, Alignment.MIDDLE_CENTER);
        getContent().addComponent(buttonsHL);
        getContent().setComponentAlignment(buttonsHL, Alignment.MIDDLE_CENTER);
        getContent().addComponent(closeButton);
        getContent().setComponentAlignment(closeButton,Alignment.MIDDLE_CENTER);

        AbstractWebUI.getInstance().addWindow(this);
    }

    private CommonFormWidget getScheduleDetailCFW()throws Exception{

        CommonFormWidget scheduleDetailCFW = new CommonFormWidget(SCHEDULE_DETAIL.class);
        scheduleDetailCFW.addEntityListener(new ScheduleDetailListener());
        FormModel scheduleFM = scheduleDetailCFW.getWidgetModel();
        scheduleFM.getFieldModel("group").setInView(false);
        scheduleFM.getFieldModel("group").setInEdit(false);
        QueryModel scheduleDetailTeacherQM = ((FKFieldModel)scheduleFM.getFieldModel("teacher")).getQueryModel();
        scheduleDetailTeacherQM.addJoin(EJoin.INNER_JOIN,"id" , USERS.class ,"id");
        FromItem vEmployeeFI = scheduleDetailTeacherQM.addJoin(EJoin.INNER_JOIN,"id", V_EMPLOYEE.class,"id");
        scheduleDetailTeacherQM.addWhere(vEmployeeFI , "employeeType" , ECriteria.EQUAL , EMPLOYEE_TYPE.TEACHER_ID);
        ((FKFieldModel)scheduleFM.getFieldModel("subject")).getQueryModel().addWhere("deleted", ECriteria.EQUAL , false);

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

                for(ComboBox cb : groupsCBs) {
                    if(cb.getValue()==null){
                        Message.showError("Fill all cbs");
                        return false;
                    }
                }

                for(ComboBox cb : groupsCBs) {
                    GROUPS groups = (GROUPS) cb.getValue();
                    scheduleDetail.setId(null);
                    scheduleDetail.setGroup(groups);
                    scheduleDetail.setWeekDay((WEEK_DAY) scheduleManual.getWeekDayGW().getSelectedEntity());
                    scheduleDetail.setSemesterData((SEMESTER_DATA) scheduleManual.getSemesterDataCB().getValue());
                    SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).create(scheduleDetail);
                }


                try {
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

        }

        @Override
        public boolean onEdit(Object o, Entity entity, int i) {
            SCHEDULE_DETAIL scheduleDetailEdit = (SCHEDULE_DETAIL)groupsCB.getValue();
            groupsCB.setValue(scheduleDetailEdit);

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

    public ComboBox getGroupsCB() {
        return groupsCB;
    }

    public void setGroupsCB(ComboBox groupsCB) {
        this.groupsCB = groupsCB;
    }
}
