package kz.halyqsoft.univercity.modules.schedule;

import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.shared.ui.combobox.FilteringMode;
import com.vaadin.ui.*;
import kz.halyqsoft.univercity.entity.beans.univercity.AbstractWorkHourEntity;
import kz.halyqsoft.univercity.entity.beans.univercity.ROOM_WORK_HOUR;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.*;
import kz.halyqsoft.univercity.entity.beans.univercity.view.V_SUBJECT_SELECT;
import kz.halyqsoft.univercity.filter.FRoomFilter;
import kz.halyqsoft.univercity.modules.employee.workhour.WorkHourWidget;
import kz.halyqsoft.univercity.modules.room.RoomEdit;
import kz.halyqsoft.univercity.utils.CommonUtils;
import org.r3a.common.dblink.facade.CommonEntityFacadeBean;
import org.r3a.common.dblink.utils.SessionFacadeFactory;
import org.r3a.common.entity.Entity;
import org.r3a.common.entity.ID;
import org.r3a.common.entity.event.EntityEvent;
import org.r3a.common.entity.event.EntityListener;
import org.r3a.common.entity.query.QueryModel;
import org.r3a.common.entity.query.select.EAggregate;
import org.r3a.common.entity.query.where.ECriteria;
import org.r3a.common.vaadin.AbstractWebUI;
import org.r3a.common.vaadin.widget.DBSelectModel;
import org.r3a.common.vaadin.widget.dialog.AbstractDialog;
import org.r3a.common.vaadin.widget.dialog.Message;
import org.r3a.common.vaadin.widget.dialog.select.custom.grid.CustomGridSelectDialog;
import org.r3a.common.vaadin.widget.form.AbstractFormWidget;
import org.r3a.common.vaadin.widget.form.CommonFormWidget;
import org.r3a.common.vaadin.widget.form.FormModel;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static kz.halyqsoft.univercity.utils.CommonUtils.createButtonPanel;

/**
 * @author Assylkhan
 * on 25.01.2019
 * @project kz.halyqsoft.univercity
 */
public class ScheduleRoomTimeLimitDialog  extends AbstractDialog implements EntityListener{

    private AbstractFormWidget baseDataFW;
    private FRoomFilter filter;
    private WorkHourWidget whw;

    public ScheduleRoomTimeLimitDialog(FormModel baseDataFM, FRoomFilter filter) {
        baseDataFW = new CommonFormWidget(baseDataFM);
        baseDataFW.addEntityListener(this);
        this.filter = filter;

        setWidth("70%");
        setHeight("70%");
        getContent().setSizeFull();
        try{
            createTimeLimitTab(false);
        }catch (Exception e){
            e.printStackTrace();
        }

        setClosable(true);
        AbstractWebUI.getInstance().addWindow(this);
    }

    @Override
    protected String createTitle() {
        return getUILocaleUtil().getCaption("time.limit");
    }

    private void createTimeLimitTab(boolean readOnly) throws Exception {
        VerticalLayout content = getContent();
        content.setSpacing(true);
        content.setSizeFull();

        /* Time limit */
        ID roomId = ID.valueOf(-1);
        if (!baseDataFW.getWidgetModel().isCreateNew()) {
            roomId = baseDataFW.getWidgetModel().getEntity().getId();
        }
        QueryModel<ROOM_WORK_HOUR> rwhQM = new QueryModel<ROOM_WORK_HOUR>(ROOM_WORK_HOUR.class);
        rwhQM.addWhere("room", ECriteria.EQUAL, roomId);
        rwhQM.addWhere("dayHour", ECriteria.LESS_EQUAL, ID.valueOf(14));
        List<ROOM_WORK_HOUR> tempList = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).
                lookup(rwhQM);
        List<ROOM_WORK_HOUR> rwhList = new ArrayList<ROOM_WORK_HOUR>();
        for (ROOM_WORK_HOUR rwh : tempList) {
            ROOM_WORK_HOUR rwh1 = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).
                    lookup(ROOM_WORK_HOUR.class, rwh.getId());
            rwhList.add(rwh1);
        }
        whw = new WorkHourWidget(rwhList, readOnly, baseDataFW.getWidgetModel());
        whw.setCaption(getUILocaleUtil().getCaption("time.limit.setting"));
        whw.setLegend1Resource("available");
        whw.setLegend2Resource("not.available");
        content.addComponent(whw);
        content.setComponentAlignment(whw, Alignment.MIDDLE_CENTER);

        if (!readOnly) {
            HorizontalLayout buttonPanel = createButtonPanel();
            Button save = CommonUtils.createSaveButton();
            save.addClickListener(new Button.ClickListener() {

                @Override
                public void buttonClick(Button.ClickEvent ev) {
                    List<? extends AbstractWorkHourEntity> awheList = whw.getWorkHourList();
                    boolean saved = false;
                    for (AbstractWorkHourEntity awhe : awheList) {
                        if (awhe.isChanged()) {
                            try {
                                SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).merge(awhe);
                                saved = true;
                                awhe.setChanged(false);
                            } catch (Exception ex) {
                                CommonUtils.showMessageAndWriteLog("Unable to change time limit", ex);
                                break;
                            }
                        }
                    }

                    if (saved) {
                        CommonUtils.showSavedNotification();
                    }
                }
            });
            buttonPanel.addComponent(save);
            buttonPanel.setComponentAlignment(save, Alignment.MIDDLE_CENTER);

            Button cancel = CommonUtils.createCancelButton();
            cancel.addClickListener(new Button.ClickListener() {

                @Override
                public void buttonClick(Button.ClickEvent ev) {
                    whw.cancel();
                }
            });
            buttonPanel.addComponent(cancel);
            buttonPanel.setComponentAlignment(cancel, Alignment.MIDDLE_CENTER);
            content.addComponent(buttonPanel);
            content.setComponentAlignment(buttonPanel, Alignment.BOTTOM_CENTER);
        }
    }


    @Override
    public void handleEntityEvent(EntityEvent entityEvent) {

    }

    @Override
    public boolean preCreate(Object o, int i) {
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
    public boolean preSave(Object source, Entity entity, boolean isNew, int buttonId) throws Exception {
        if (source.equals(baseDataFW)) {
            return preSaveBaseData(source, entity, isNew, buttonId);
        }
        return false;
    }

    private boolean preSaveBaseData(Object source, Entity e, boolean isNew, int buttonId) {
        ROOM r = (ROOM) e;
        if (isNew) {
            r.setCreated(new Date());
            try {
                SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).create(r);
                baseDataFW.getWidgetModel().loadEntity(r.getId());
                baseDataFW.refresh();

                saveRoomWorkHour(r);
                CommonUtils.showSavedNotification();
            } catch (Exception ex) {
                CommonUtils.showMessageAndWriteLog("Unable to create a room", ex);
            }
        } else {
            try {
                r.setUpdated(new Date());
                SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).merge(r);
                saveRoomWorkHour(r);
                CommonUtils.showSavedNotification();
            } catch (Exception ex) {
                CommonUtils.showMessageAndWriteLog("Unable to merge a room", ex);
            }
        }

        return false;
    }

    private void saveRoomWorkHour(ROOM room) throws Exception {
        QueryModel<ROOM_WORK_HOUR> rwhQM = new QueryModel<ROOM_WORK_HOUR>(ROOM_WORK_HOUR.class);
        rwhQM.addSelect("room", EAggregate.COUNT);
        rwhQM.addWhere("room", ECriteria.EQUAL, room.getId());
        rwhQM.addWhere("dayHour", ECriteria.LESS_EQUAL, ID.valueOf(14));

        long count = (long) SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookupItems(rwhQM);
        if (count <= 0) {
            QueryModel<WEEK_DAY> wdQM = new QueryModel<WEEK_DAY>(WEEK_DAY.class);
            List<WEEK_DAY> wdList = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(wdQM);

            QueryModel<DAY_HOUR> dhQM = new QueryModel<DAY_HOUR>(DAY_HOUR.class);
            dhQM.addWhere("id", ECriteria.LESS_EQUAL, ID.valueOf(14));

            List<DAY_HOUR> dhList = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(dhQM);

            WORK_HOUR_STATUS whs = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(WORK_HOUR_STATUS.class, ID.valueOf(1));

            List<ROOM_WORK_HOUR> rwhList = new ArrayList<ROOM_WORK_HOUR>();
            for (WEEK_DAY wd : wdList) {
                for (DAY_HOUR dh : dhList) {
                    ROOM_WORK_HOUR rwh = new ROOM_WORK_HOUR();
                    rwh.setRoom(room);
                    rwh.setWeekDay(wd);
                    rwh.setDayHour(dh);
                    rwh.setWorkHourStatus(whs);
                    SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).create(rwh);
                    rwhList.add(rwh);
                }
            }

            whw.setWorkHourList(rwhList);
            whw.refresh();
        }
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
