package kz.halyqsoft.univercity.modules.room;

import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.shared.ui.combobox.FilteringMode;
import com.vaadin.ui.*;
import com.vaadin.ui.Button.ClickEvent;
import kz.halyqsoft.univercity.entity.beans.univercity.*;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.*;
import kz.halyqsoft.univercity.entity.beans.univercity.view.V_ROOM_DEPARTMENT;
import kz.halyqsoft.univercity.entity.beans.univercity.view.V_ROOM_EQUIPMENT;
import kz.halyqsoft.univercity.entity.beans.univercity.view.V_ROOM_SUBJECT;
import kz.halyqsoft.univercity.entity.beans.univercity.view.V_SUBJECT_SELECT;
import kz.halyqsoft.univercity.filter.FRoomFilter;
import kz.halyqsoft.univercity.modules.employee.workhour.WorkHourWidget;
import kz.halyqsoft.univercity.utils.CommonUtils;
import org.r3a.common.dblink.facade.CommonEntityFacadeBean;
import org.r3a.common.dblink.utils.SessionFacadeFactory;
import org.r3a.common.entity.Entity;
import org.r3a.common.entity.ID;
import org.r3a.common.entity.query.QueryModel;
import org.r3a.common.entity.query.select.EAggregate;
import org.r3a.common.entity.query.where.ECriteria;
import org.r3a.common.vaadin.view.AbstractCommonView;
import org.r3a.common.vaadin.widget.DBSelectModel;
import org.r3a.common.vaadin.widget.dialog.AbstractYesButtonListener;
import org.r3a.common.vaadin.widget.dialog.Message;
import org.r3a.common.vaadin.widget.dialog.select.custom.grid.CustomGridSelectDialog;
import org.r3a.common.vaadin.widget.form.AbstractFormWidget;
import org.r3a.common.vaadin.widget.form.AbstractFormWidgetView;
import org.r3a.common.vaadin.widget.form.CommonFormWidget;
import org.r3a.common.vaadin.widget.form.FormModel;
import org.r3a.common.vaadin.widget.form.field.fk.FKFieldModel;
import org.r3a.common.vaadin.widget.table.TableWidget;
import org.r3a.common.vaadin.widget.table.model.DBTableModel;
import org.r3a.common.vaadin.widget.toolbar.AbstractToolbar;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * @author Dinassil Omarbek
 * @created Jan 6, 2016 11:26:17 AM
 */
@SuppressWarnings({"serial"})
public final class RoomEdit extends AbstractFormWidgetView {

    private final AbstractFormWidget baseDataFW;
    private final FRoomFilter filter;
    private TableWidget equipmentTW;
    private TableWidget subjectTW;
    private TableWidget departmentTW;
    private WorkHourWidget whw;
    private CustomGridSelectDialog subjectSelectDlg;

    public RoomEdit(FormModel baseDataFM, FRoomFilter filter) throws Exception {
        super();

        setBackButtonVisible(false);
        this.filter = filter;

        HorizontalLayout content = new HorizontalLayout();
        content.setSpacing(true);
        content.setSizeFull();

        baseDataFM.setButtonsVisible(false);

        baseDataFW = new CommonFormWidget(baseDataFM);
        baseDataFW.addEntityListener(this);

        VerticalLayout bdContent = new VerticalLayout();
        bdContent.addComponent(baseDataFW);

        bdContent.setComponentAlignment(baseDataFW, Alignment.MIDDLE_RIGHT);

        VerticalLayout equipContent = new VerticalLayout();

        equipmentTW = new TableWidget(V_ROOM_EQUIPMENT.class);
        equipmentTW.addEntityListener(this);
        equipmentTW.setButtonVisible(AbstractToolbar.PREVIEW_BUTTON, false);
        equipmentTW.setButtonVisible(AbstractToolbar.REFRESH_BUTTON, false);
        DBTableModel equipmentTM = (DBTableModel) equipmentTW.getWidgetModel();
        equipmentTM.setReadOnly(baseDataFM.isReadOnly());
        QueryModel equipmentQM = equipmentTM.getQueryModel();

        ID equipRoomId = ID.valueOf(-1);
        if (!baseDataFW.getWidgetModel().isCreateNew()) {
            equipRoomId = baseDataFW.getWidgetModel().getEntity().getId();
        }
        equipmentQM.addWhere("room", ECriteria.EQUAL, equipRoomId);

        FormModel equipmentFM = equipmentTM.getFormModel();
        FKFieldModel equipmentFM1 = (FKFieldModel) equipmentFM.getFieldModel("equipment");
        QueryModel equipmentQM1 = equipmentFM1.getQueryModel();
        equipmentQM1.addOrder("equipmentName");

        equipContent.addComponent(equipmentTW);
        equipContent.setComponentAlignment(equipmentTW, Alignment.MIDDLE_CENTER);

        VerticalLayout depContent = new VerticalLayout();

        departmentTW = new TableWidget(V_ROOM_DEPARTMENT.class);
        departmentTW.addEntityListener(this);
        departmentTW.setButtonVisible(AbstractToolbar.PREVIEW_BUTTON, false);
        departmentTW.setButtonVisible(AbstractToolbar.REFRESH_BUTTON, false);

        DBTableModel departmentTM = (DBTableModel) departmentTW.getWidgetModel();
        departmentTM.setReadOnly(baseDataFM.isReadOnly());
        QueryModel departmentQM = departmentTM.getQueryModel();

        ID depRoomId = ID.valueOf(-1);
        if (!baseDataFW.getWidgetModel().isCreateNew()) {
            depRoomId = baseDataFW.getWidgetModel().getEntity().getId();
        }
        departmentQM.addWhere("room", ECriteria.EQUAL, depRoomId);

        FormModel departmentFM = departmentTM.getFormModel();
        FKFieldModel departmentFM1 = (FKFieldModel) departmentFM.getFieldModel("department");
        departmentFM1.setDialogHeight(400);
        departmentFM1.setDialogWidth(400);
        QueryModel departmentQM1 = departmentFM1.getQueryModel();
        departmentQM1.addWhere("deleted", Boolean.FALSE);

        depContent.addComponent(departmentTW);
        depContent.setComponentAlignment(departmentTW, Alignment.MIDDLE_CENTER);

        if (!baseDataFM.isReadOnly()) {
            HorizontalLayout buttonPanel = createButtonPanel();
            Button save = createSaveButton();
            save.addClickListener(new Button.ClickListener() {

                @Override
                public void buttonClick(ClickEvent ev) {
                    baseDataFW.save();
                }
            });
            buttonPanel.addComponent(save);
            buttonPanel.setComponentAlignment(save, Alignment.MIDDLE_CENTER);

            Button cancel = createCancelButton();
            cancel.addClickListener(new Button.ClickListener() {

                @Override
                public void buttonClick(ClickEvent ev) {
                    baseDataFW.cancel();
                }
            });
            buttonPanel.addComponent(cancel);
            buttonPanel.setComponentAlignment(cancel, Alignment.MIDDLE_CENTER);
            bdContent.addComponent(buttonPanel);
            bdContent.setComponentAlignment(buttonPanel, Alignment.BOTTOM_RIGHT);
        }

        content.addComponent(bdContent);
        content.setComponentAlignment(bdContent, Alignment.TOP_LEFT);
        content.addComponent(equipmentTW);
        content.setComponentAlignment(equipmentTW, Alignment.TOP_CENTER);
        content.addComponent(departmentTW);
        content.setComponentAlignment(departmentTW, Alignment.TOP_RIGHT);

        getTabSheet().addTab(content, getMasterTabTitle());

        boolean readOnly = baseDataFW.getWidgetModel().isReadOnly();
        createSubjectsTab(readOnly);
        createTimeLimitTab(readOnly);
    }

    @Override
    protected AbstractCommonView getParentView() {
        return null;
    }

    @Override
    public String getViewName() {
        return "roomEdit";
    }

    @Override
    protected String getViewTitle(Locale locale) {
        try {
            FormModel roomFM = baseDataFW.getWidgetModel();
            ROOM room = (ROOM) roomFM.getEntity();
            if (room != null && !roomFM.isCreateNew()) {
                return getUILocaleUtil().getCaption("room.edit") + ": " + room.toString();
            } else {
                return getUILocaleUtil().getCaption("room.new");
            }
        } catch (Exception ex) {
            CommonUtils.showMessageAndWriteLog("Unable to create view title", ex);
        }
        return "";
    }

    @Override
    public void initView(boolean readOnly) throws Exception {
        super.initView(readOnly);
    }

    private void createSubjectsTab(boolean readOnly) throws Exception {
        VerticalLayout content = new VerticalLayout();
        content.setSpacing(true);
        content.setSizeFull();

		/* Room subject */
        subjectTW = new TableWidget(V_ROOM_SUBJECT.class);
        subjectTW.setButtonVisible(AbstractToolbar.EDIT_BUTTON, false);
        subjectTW.setButtonVisible(AbstractToolbar.PREVIEW_BUTTON, false);
        subjectTW.setButtonVisible(AbstractToolbar.REFRESH_BUTTON, false);
        subjectTW.addEntityListener(this);
        DBTableModel subjectTM = (DBTableModel) subjectTW.getWidgetModel();
        subjectTM.setReadOnly(readOnly);
        QueryModel subjectQM = subjectTM.getQueryModel();

        ID roomId = ID.valueOf(-1);
        if (!baseDataFW.getWidgetModel().isCreateNew()) {
            roomId = baseDataFW.getWidgetModel().getEntity().getId();
        }
        subjectQM.addWhere("room", ECriteria.EQUAL, roomId);

        content.addComponent(subjectTW);
        content.setComponentAlignment(subjectTW, Alignment.MIDDLE_CENTER);

        getTabSheet().addTab(content, getUILocaleUtil().getCaption("room.subject"));
    }

    private void createTimeLimitTab(boolean readOnly) throws Exception {
        VerticalLayout content = new VerticalLayout();
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
        whw = new WorkHourWidget(rwhList, readOnly,baseDataFW.getWidgetModel());
        whw.setCaption(getUILocaleUtil().getCaption("time.limit.setting"));
        whw.setLegend1Resource("available");
        whw.setLegend2Resource("not.available");
        content.addComponent(whw);
        content.setComponentAlignment(whw, Alignment.MIDDLE_CENTER);

        if (!readOnly) {
            HorizontalLayout buttonPanel = createButtonPanel();
            Button save = createSaveButton();
            save.addClickListener(new Button.ClickListener() {

                @Override
                public void buttonClick(ClickEvent ev) {
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
                        showSavedNotification();
                    }
                }
            });
            buttonPanel.addComponent(save);
            buttonPanel.setComponentAlignment(save, Alignment.MIDDLE_CENTER);

            Button cancel = createCancelButton();
            cancel.addClickListener(new Button.ClickListener() {

                @Override
                public void buttonClick(ClickEvent ev) {
                    whw.cancel();
                }
            });
            buttonPanel.addComponent(cancel);
            buttonPanel.setComponentAlignment(cancel, Alignment.MIDDLE_CENTER);
            content.addComponent(buttonPanel);
            content.setComponentAlignment(buttonPanel, Alignment.BOTTOM_CENTER);
        }

        getTabSheet().addTab(content, getUILocaleUtil().getCaption("time.limit"));
    }

    @Override
    public boolean preCreate(Object source, int buttonId) {
        if (source.equals(equipmentTW) || source.equals(subjectTW) || source.equals(departmentTW)) {
            if (baseDataFW.getWidgetModel().isCreateNew()) {
                Message.showInfo(getUILocaleUtil().getMessage("info.save.base.data.first"));
                return false;
            } else if (source.equals(subjectTW)) {
                subjectSelectDlg = new CustomGridSelectDialog(new SubjectSelectYesListener(), V_SUBJECT_SELECT.class);
                subjectSelectDlg.setDialogHeight(400);
                subjectSelectDlg.setDialogWidth(700);
                QueryModel subjectQM1 = ((DBSelectModel) subjectSelectDlg.getSelectModel()).getQueryModel();
                subjectQM1.addWhere("chair", ECriteria.EQUAL, ID.valueOf(-1));
                try {
                    QueryModel<DEPARTMENT> chairQM1 = new QueryModel<DEPARTMENT>(DEPARTMENT.class);
                    chairQM1.addWhereNotNull("parent");
                    chairQM1.addWhereAnd("deleted", Boolean.FALSE);
                    chairQM1.addOrder("deptName");
                    BeanItemContainer<DEPARTMENT> chairBIC = new BeanItemContainer<DEPARTMENT>(DEPARTMENT.class, SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(chairQM1));
                    ComboBox chairCB = new ComboBox();
                    chairCB.setContainerDataSource(chairBIC);
                    chairCB.setImmediate(true);
                    chairCB.setNullSelectionAllowed(true);
                    chairCB.setTextInputAllowed(true);
                    chairCB.setFilteringMode(FilteringMode.CONTAINS);
                    chairCB.setWidth(400, Unit.PIXELS);
                    chairCB.setPageLength(0);

                    QueryModel<LEVEL> levelQM = new QueryModel<LEVEL>(LEVEL.class);
                    levelQM.addOrder("levelName");
                    BeanItemContainer<LEVEL> levelBIC = new BeanItemContainer<LEVEL>(LEVEL.class, SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(levelQM));
                    ComboBox levelCB = new ComboBox();
                    levelCB.setContainerDataSource(levelBIC);
                    levelCB.setImmediate(true);
                    levelCB.setNullSelectionAllowed(true);
                    levelCB.setTextInputAllowed(false);
                    levelCB.setFilteringMode(FilteringMode.OFF);

                    QueryModel<CREDITABILITY> creditabilityQM = new QueryModel<CREDITABILITY>(CREDITABILITY.class);
                    creditabilityQM.addOrder("credit");
                    BeanItemContainer<CREDITABILITY> creditabilityBIC = new BeanItemContainer<CREDITABILITY>(CREDITABILITY.class, SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(creditabilityQM));
                    ComboBox creditabilityCB = new ComboBox();
                    creditabilityCB.setContainerDataSource(creditabilityBIC);
                    creditabilityCB.setImmediate(true);
                    creditabilityCB.setNullSelectionAllowed(true);
                    creditabilityCB.setTextInputAllowed(false);
                    creditabilityCB.setFilteringMode(FilteringMode.OFF);
                    creditabilityCB.setPageLength(0);

                    subjectSelectDlg.getSelectModel().setMultiSelect(false);
                    subjectSelectDlg.getFilterModel().addFilter("chair", chairCB);
                    subjectSelectDlg.getFilterModel().addFilter("level", levelCB);
                    subjectSelectDlg.getFilterModel().addFilter("creditability", creditabilityCB);
                    subjectSelectDlg.setFilterRequired(true);
                    subjectSelectDlg.initFilter();
                    subjectSelectDlg.open();
                } catch (Exception ex) {
                    CommonUtils.showMessageAndWriteLog("Unable to initialize custom grid dialog", ex);
                }

                return false;
            }

            return true;
        }

        return super.preCreate(source, buttonId);
    }

    @Override
    public boolean preSave(Object source, Entity e, boolean isNew, int buttonId) throws Exception {
        if (source.equals(baseDataFW)) {
            return preSaveBaseData(source, e, isNew, buttonId);
        } else if (source.equals(equipmentTW)) {
            return preSaveEquipment(source, e, isNew, buttonId);
        } else if (source.equals(departmentTW)) {
            return preSaveDepartment(source, e, isNew, buttonId);
        }

        return super.preSave(source, e, isNew, buttonId);
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
                showSavedNotification();
            } catch (Exception ex) {
                CommonUtils.showMessageAndWriteLog("Unable to create a room", ex);
            }
        } else {
            try {
                r.setUpdated(new Date());
                SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).merge(r);
                saveRoomWorkHour(r);
                showSavedNotification();
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

    private boolean preSaveEquipment(Object source, Entity e, boolean isNew, int buttonId) throws Exception {
        V_ROOM_EQUIPMENT vre = (V_ROOM_EQUIPMENT) e;
        ROOM_EQUIPMENT re = null;
        ROOM r = (ROOM) baseDataFW.getWidgetModel().getEntity();
        if (isNew) {
            re = new ROOM_EQUIPMENT();
            try {
                re.setRoom(r);
                re.setEquipment(vre.getEquipment());
                SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).create(re);
                updateRoomEquipments(r);

                QueryModel equipmentQM = ((DBTableModel) equipmentTW.getWidgetModel()).getQueryModel();
                equipmentQM.addWhere("room", ECriteria.EQUAL, r.getId());

                equipmentTW.refresh();
                showSavedNotification();
            } catch (Exception ex) {
                CommonUtils.showMessageAndWriteLog("Unable to create a room equipment", ex);
            }
        } else {
            try {
                re = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(ROOM_EQUIPMENT.class, vre.getId());
                re.setEquipment(vre.getEquipment());
                SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).merge(re);
                updateRoomEquipments(re.getRoom());

                equipmentTW.refresh();
                showSavedNotification();
            } catch (Exception ex) {
                CommonUtils.showMessageAndWriteLog("Unable to merge a room equipment", ex);
            }
        }

        return false;
    }

    private void updateRoomEquipments(ROOM room) throws Exception {
        QueryModel<V_ROOM_EQUIPMENT> qm = new QueryModel<V_ROOM_EQUIPMENT>(V_ROOM_EQUIPMENT.class);
        qm.addWhere("room", ECriteria.EQUAL, room.getId());

        List<V_ROOM_EQUIPMENT> reList = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(qm);
        if (!reList.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            boolean first = true;
            for (V_ROOM_EQUIPMENT re : reList) {
                if (!first) {
                    sb.append(", ");
                }
                sb.append(re.getEquipmentName());
                first = false;
            }
            room.setEquipment(sb.toString());
        } else {
            room.setEquipment(null);
        }

        SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).merge(room);
    }

    private boolean preSaveDepartment(Object source, Entity e, boolean isNew, int buttonId) throws Exception {
        V_ROOM_DEPARTMENT vrd = (V_ROOM_DEPARTMENT) e;
        ROOM_DEPARTMENT rd = null;
        ROOM r = (ROOM) baseDataFW.getWidgetModel().getEntity();
        if (isNew) {
            rd = new ROOM_DEPARTMENT();
            try {
                rd.setRoom(r);
                rd.setDepartment(vrd.getDepartment());
                SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).create(rd);

                QueryModel departmentQM = ((DBTableModel) departmentTW.getWidgetModel()).getQueryModel();
                departmentQM.addWhere("room", ECriteria.EQUAL, r.getId());

                departmentTW.refresh();
                showSavedNotification();
            } catch (Exception ex) {
                CommonUtils.showMessageAndWriteLog("Unable to create a room department", ex);
            }
        } else {
            try {
                rd = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(ROOM_DEPARTMENT.class, vrd.getId());
                rd.setDepartment(vrd.getDepartment());
                SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).merge(rd);
                departmentTW.refresh();
                showSavedNotification();
            } catch (Exception ex) {
                CommonUtils.showMessageAndWriteLog("Unable to merge a room department", ex);
            }
        }

        return false;
    }

    @Override
    public boolean preDelete(Object source, List<Entity> entities, int buttonId) {
        if (source.equals(equipmentTW)) {
            List<ROOM_EQUIPMENT> delList = new ArrayList<ROOM_EQUIPMENT>();
            for (Entity e : entities) {
                try {
                    delList.add(SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(ROOM_EQUIPMENT.class, e.getId()));
                } catch (Exception ex) {
                    CommonUtils.showMessageAndWriteLog("Unable to delete room equipment", ex);
                }
            }

            try {
                SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).delete(delList);
                updateRoomEquipments(delList.get(0).getRoom());
                equipmentTW.refresh();
            } catch (Exception ex) {
                CommonUtils.showMessageAndWriteLog("Unable to delete room equipment", ex);
            }

            return false;
        } else if (source.equals(subjectTW)) {
            List<ROOM_SUBJECT> delList = new ArrayList<ROOM_SUBJECT>();
            for (Entity e : entities) {
                try {
                    delList.add(SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(ROOM_SUBJECT.class, e.getId()));
                } catch (Exception ex) {
                    CommonUtils.showMessageAndWriteLog("Unable to delete room subject", ex);
                }
            }

            try {
                SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).delete(delList);
                subjectTW.refresh();
            } catch (Exception ex) {
                LOG.error("Unable to delete room subject: ", ex);
                Message.showError(getUILocaleUtil().getMessage("error.cannotdelentity"));
            }

            return false;
        } else if (source.equals(departmentTW)) {
            List<ROOM_DEPARTMENT> delList = new ArrayList<ROOM_DEPARTMENT>();
            for (Entity e : entities) {
                try {
                    delList.add(SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(ROOM_DEPARTMENT.class, e.getId()));
                } catch (Exception ex) {
                    CommonUtils.showMessageAndWriteLog("Unable to delete room room department", ex);
                }
            }

            try {
                SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).delete(delList);
                departmentTW.refresh();
            } catch (Exception ex) {
                LOG.error("Unable to delete room department: ", ex);
                Message.showError(getUILocaleUtil().getMessage("error.cannotdelentity"));
            }

            return false;
        }

        return super.preDelete(source, entities, buttonId);
    }

    @Override
    public void deferredCreate(Object source, Entity e) {
    }

    @Override
    public void deferredDelete(Object source, List<Entity> entities) {
    }

    @Override
    public void onException(Object source, Throwable ex) {
    }

    private class SubjectSelectYesListener extends AbstractYesButtonListener {

        @Override
        public void buttonClick(ClickEvent event) {
            V_SUBJECT_SELECT vss = (V_SUBJECT_SELECT) subjectSelectDlg.getSelectedEntities().get(0);
            try {
                ROOM r = (ROOM) baseDataFW.getWidgetModel().getEntity();
                ROOM_SUBJECT rs = new ROOM_SUBJECT();
                rs.setRoom(r);
                rs.setSubject(vss);
                SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).create(rs);

                QueryModel subjectQM = ((DBTableModel) subjectTW.getWidgetModel()).getQueryModel();
                subjectQM.addWhere("room", ECriteria.EQUAL, r.getId());
                subjectTW.refresh();
                showSavedNotification();
            } catch (Exception ex) {
                CommonUtils.showMessageAndWriteLog("Unable to add subject to room", ex);
            }
        }

        @Override
        protected boolean canClose() {
            return canProcess();
        }

        @Override
        protected boolean canProcess() {
            return (!subjectSelectDlg.getSelectedEntities().isEmpty());
        }
    }
}
