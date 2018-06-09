package kz.halyqsoft.univercity.modules.room;

import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.shared.ui.combobox.FilteringMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.TextField;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.*;
import kz.halyqsoft.univercity.entity.beans.univercity.view.VRoom;
import kz.halyqsoft.univercity.entity.beans.univercity.view.V_ROOM_DEPARTMENT;
import kz.halyqsoft.univercity.entity.beans.univercity.view.V_ROOM_EQUIPMENT;
import kz.halyqsoft.univercity.filter.FRoomFilter;
import kz.halyqsoft.univercity.filter.panel.RoomFilterPanel;
import kz.halyqsoft.univercity.utils.CommonUtils;
import kz.halyqsoft.univercity.utils.EntityUtils;
import org.r3a.common.dblink.facade.CommonEntityFacadeBean;
import org.r3a.common.dblink.utils.SessionFacadeFactory;
import org.r3a.common.entity.Entity;
import org.r3a.common.entity.ID;
import org.r3a.common.entity.beans.AbstractTask;
import org.r3a.common.entity.event.EntityEvent;
import org.r3a.common.entity.query.QueryModel;
import org.r3a.common.entity.query.where.ECriteria;
import org.r3a.common.vaadin.view.AbstractTaskView;
import org.r3a.common.vaadin.widget.ERefreshType;
import org.r3a.common.vaadin.widget.dialog.select.ESelectType;
import org.r3a.common.vaadin.widget.dialog.select.custom.grid.CustomGridSelectDialog;
import org.r3a.common.vaadin.widget.filter2.AbstractFilterBean;
import org.r3a.common.vaadin.widget.filter2.FilterPanelListener;
import org.r3a.common.vaadin.widget.form.FormModel;
import org.r3a.common.vaadin.widget.form.field.fk.FKFieldModel;
import org.r3a.common.vaadin.widget.grid.GridWidget;
import org.r3a.common.vaadin.widget.grid.model.DBGridModel;
import org.r3a.common.vaadin.widget.table.TableWidget;
import org.r3a.common.vaadin.widget.table.model.DBTableModel;
import org.r3a.common.vaadin.widget.toolbar.AbstractToolbar;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Dinassil Omarbek
 * @created Jan 6, 2016 10:51:41 AM
 */
@SuppressWarnings({"serial", "unchecked"})
public class RoomView extends AbstractTaskView implements FilterPanelListener {

    private final RoomFilterPanel filterPanel;
    private GridWidget roomGW;
    private TableWidget equipmentTW;
    private TableWidget departmentTW;

    public RoomView(AbstractTask task) throws Exception {
        super(task);
        filterPanel = new RoomFilterPanel(new FRoomFilter());
    }

    @Override
    public void initView(boolean readOnly) throws Exception {
        filterPanel.addFilterPanelListener(this);
        ComboBox cb = new ComboBox();
        cb.setNullSelectionAllowed(true);
        cb.setTextInputAllowed(false);
        cb.setFilteringMode(FilteringMode.OFF);
        QueryModel<CORPUS> corpusQM = new QueryModel<CORPUS>(CORPUS.class);
        corpusQM.addOrder("id");
        BeanItemContainer<CORPUS> corpusBIC = new BeanItemContainer<CORPUS>(CORPUS.class,
                SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(corpusQM));
        cb.setContainerDataSource(corpusBIC);
        filterPanel.addFilterComponent("corpus", cb);

        cb = new ComboBox();
        cb.setNullSelectionAllowed(true);
        cb.setTextInputAllowed(false);
        cb.setFilteringMode(FilteringMode.OFF);
        QueryModel<ROOM_TYPE> roomTypeQM = new QueryModel<ROOM_TYPE>(ROOM_TYPE.class);
        roomTypeQM.addOrder("id");
        BeanItemContainer<ROOM_TYPE> roomTypeBIC = new BeanItemContainer<ROOM_TYPE>(ROOM_TYPE.class,
                SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(roomTypeQM));
        cb.setContainerDataSource(roomTypeBIC);
        filterPanel.addFilterComponent("roomType", cb);

        TextField tf = new TextField();
        tf.setNullRepresentation("");
        tf.setNullSettingAllowed(true);
        filterPanel.addFilterComponent("roomNo", tf);

        getContent().addComponent(filterPanel);
        getContent().setComponentAlignment(filterPanel, Alignment.TOP_CENTER);

        GridLayout dataGL = new GridLayout();
        dataGL.setSizeFull();
        dataGL.setSpacing(true);
        dataGL.setColumns(3);
        dataGL.setRows(1);
        dataGL.setColumnExpandRatio(0, (float) 1.4);
        dataGL.setColumnExpandRatio(1, (float) .6);
        dataGL.setColumnExpandRatio(2, (float) 1);

        roomGW = new GridWidget(VRoom.class);
        roomGW.addEntityListener(new RoomEntity());
        roomGW.setButtonVisible(AbstractToolbar.PREVIEW_BUTTON, false);
        roomGW.setButtonVisible(AbstractToolbar.REFRESH_BUTTON, false);
        DBGridModel roomGM = (DBGridModel) roomGW.getWidgetModel();
        roomGM.setCrudEntityClass(ROOM.class);
        roomGM.setReadOnly(readOnly);
        roomGM.setTitleVisible(false);
        roomGM.setMultiSelect(true);
        roomGM.setRefreshType(ERefreshType.MANUAL);

        FormModel formModel = roomGM.getFormModel();

        FKFieldModel respEmployeeFM = (FKFieldModel) formModel.getFieldModel("respEmployee");
        respEmployeeFM.setSelectType(ESelectType.CUSTOM_GRID);
        respEmployeeFM.setDialogHeight(400);
        respEmployeeFM.setDialogWidth(600);
        QueryModel respEmployeeQM = respEmployeeFM.getQueryModel();
        try {
            TextField fioTF = new TextField();
            fioTF.setImmediate(true);
            fioTF.setWidth(400, Unit.PIXELS);

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
            chairCB.setPageLength(0);
            chairCB.setWidth(400, Unit.PIXELS);

            QueryModel<POST> postQM1 = new QueryModel<POST>(POST.class);
            postQM1.addOrder("postName");
            BeanItemContainer<POST> postBIC = new BeanItemContainer<POST>(POST.class, SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(postQM1));
            ComboBox postCB = new ComboBox();
            postCB.setContainerDataSource(postBIC);
            postCB.setImmediate(true);
            postCB.setNullSelectionAllowed(true);
            postCB.setTextInputAllowed(true);
            postCB.setFilteringMode(FilteringMode.STARTSWITH);
            postCB.setPageLength(0);
            postCB.setWidth(400, Unit.PIXELS);

            CustomGridSelectDialog cgsd = respEmployeeFM.getCustomGridSelectDialog();
            cgsd.getSelectModel().setMultiSelect(false);
            cgsd.getFilterModel().addFilter("department", chairCB);
            cgsd.getFilterModel().addFilter("post", postCB);
            cgsd.getFilterModel().addFilter("fio", fioTF);
            cgsd.initFilter();
        } catch (Exception ex) {
            CommonUtils.showMessageAndWriteLog("Unable to initialize custom grid dialog", ex);
        }

        doFilter(filterPanel.getFilterBean());

        dataGL.addComponent(roomGW);

		/* Equipment */
        equipmentTW = new TableWidget(V_ROOM_EQUIPMENT.class);
        equipmentTW.showToolbar(false);
        DBTableModel equipmentTM = (DBTableModel) equipmentTW.getWidgetModel();
        equipmentTM.setReadOnly(true);
        QueryModel equipmentQM = equipmentTM.getQueryModel();
        equipmentQM.addWhere("room", ECriteria.EQUAL, ID.valueOf(-1));

        FormModel equipmentFM = equipmentTM.getFormModel();
        FKFieldModel equipmentFM1 = (FKFieldModel) equipmentFM.getFieldModel("equipment");
        QueryModel equipmentQM1 = equipmentFM1.getQueryModel();
        equipmentQM1.addOrder("equipmentName");

        dataGL.addComponent(equipmentTW);

		/* Room department */
        departmentTW = new TableWidget(V_ROOM_DEPARTMENT.class);
        departmentTW.showToolbar(false);
        DBTableModel departmentTM = (DBTableModel) departmentTW.getWidgetModel();
        departmentTM.setReadOnly(true);
        QueryModel departmentQM = departmentTM.getQueryModel();
        departmentQM.addWhere("room", ECriteria.EQUAL, ID.valueOf(-1));

        FormModel departmentFM = departmentTM.getFormModel();
        FKFieldModel departmentFM1 = (FKFieldModel) departmentFM.getFieldModel("department");
        departmentFM1.setDialogHeight(400);
        departmentFM1.setDialogWidth(400);
        QueryModel departmentQM1 = departmentFM1.getQueryModel();

        dataGL.addComponent(departmentTW);

        getContent().addComponent(dataGL);
        getContent().setComponentAlignment(dataGL, Alignment.MIDDLE_CENTER);
    }

    @Override
    public void doFilter(AbstractFilterBean filterBean) {
        FRoomFilter rf = (FRoomFilter) filterBean;
        int i = 1;
        Map<Integer, Object> params = new HashMap<Integer, Object>();
        StringBuilder sb = new StringBuilder();
        if (rf.getCorpus() != null) {
            params.put(i, rf.getCorpus().getId().getId());
            sb.append("room.CORPUS_ID = ?" + i++);
        }
        if (rf.getRoomType() != null) {
            if (sb.length() > 0) {
                sb.append(" and ");
            }
            params.put(i, rf.getRoomType().getId().getId());
            sb.append("room.ROOM_TYPE_ID = ?" + i++);
        }
        if (rf.getRoomNo() != null && rf.getRoomNo().trim().length() > 0) {
            if (sb.length() > 0) {
                sb.append(" and ");
            }
            sb.append("lower(room.ROOM_NO) like '");
            sb.append(rf.getRoomNo().trim().toLowerCase());
            sb.append("%'");
        }
        if (sb.length() > 0) {
            sb.append(" and ");
        }
        params.put(i, Boolean.FALSE);
        sb.append("room.DELETED = ?" + i++);

        List<VRoom> list = new ArrayList<VRoom>();
        if (sb.length() > 0) {
            sb.insert(0, " where ");
            String sql = "SELECT " +
                    "  room.ID, " +
                    "  corpus.CORPUS_NAME, " +
                    "  room.ROOM_NO, " +
                    "  room_type.TYPE_NAME ROOM_TYPE_NAME, " +
                    "  room.CAPACITY " +
                    "FROM ROOM room INNER JOIN CORPUS corpus ON room.CORPUS_ID = corpus.ID " +
                    "  INNER JOIN ROOM_TYPE room_type ON room.ROOM_TYPE_ID = room_type.ID" +
                    sb.toString() +
                    " order by room.ROOM_NO";//TODO
            try {
                List<Object> tmpList = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookupItemsList(sql, params);
                if (!tmpList.isEmpty()) {
                    for (Object o : tmpList) {
                        Object[] oo = (Object[]) o;
                        VRoom r = new VRoom();
                        r.setId(ID.valueOf((long) oo[0]));
                        r.setCorpusName((String) oo[1]);
                        r.setRoomNo((String) oo[2]);
                        r.setRoomTypeName((String) oo[3]);
                        r.setCapacity(((BigDecimal) oo[4]).intValue());
                        list.add(r);
                    }
                }
            } catch (Exception ex) {
                CommonUtils.showMessageAndWriteLog("Unable to load room list", ex);
            }
        }

        ((DBGridModel) roomGW.getWidgetModel()).setEntities(list);
        try {
            roomGW.refresh();
        } catch (Exception ex) {
            CommonUtils.showMessageAndWriteLog("Unable to refresh room grid", ex);
        }
    }

    @Override
    public void clearFilter() {
        doFilter(filterPanel.getFilterBean());
    }

    public void refresh() throws Exception {
        FRoomFilter roomFilter = (FRoomFilter) filterPanel.getFilterBean();
        doFilter(roomFilter);
    }

    private class RoomEntity extends EntityUtils {

        @Override
        public void handleEntityEvent(EntityEvent ev) {
            if (ev.getSource().equals(roomGW)) {
                if (ev.getAction() == EntityEvent.SELECTED) {
                    ID roomId = ID.valueOf(-1);
                    List<Entity> selectedList = ev.getEntities();
                    if (selectedList.size() == 1) {
                        roomId = selectedList.get(0).getId();
                    }
                    QueryModel equipmentQM = ((DBTableModel) equipmentTW.getWidgetModel()).getQueryModel();
                    equipmentQM.addWhere("room", ECriteria.EQUAL, roomId);

                    QueryModel departmentQM = ((DBTableModel) departmentTW.getWidgetModel()).getQueryModel();
                    departmentQM.addWhere("room", ECriteria.EQUAL, roomId);

                    try {
                        equipmentTW.refresh();
                        departmentTW.refresh();
                    } catch (Exception ex) {
                        CommonUtils.showMessageAndWriteLog("Unable to refresh equipments and departments", ex);
                    }
                }
            }
        }

        @Override
        public boolean preDelete(Object source, List<Entity> entities, int buttonId) {
            if (source.equals(roomGW)) {
                List<ROOM> delList = new ArrayList<ROOM>(entities.size());
                for (Entity e : entities) {
                    try {
                        ROOM r = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(ROOM.class,
                                e.getId());
                        r.setDeleted(true);
                        delList.add(r);
                    } catch (Exception ex) {
                        CommonUtils.showMessageAndWriteLog("Unable to delete the room", ex);
                        break;
                    }
                }

                try {
                    SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).merge(delList);
                    refresh();
                } catch (Exception ex) {
                    CommonUtils.showMessageAndWriteLog("Unable to delete the rooms", ex);
                }
                return false;
            }

            return true;
        }

        @Override
        protected void init(Object source, Entity e, boolean isNew) throws Exception {
            FormModel fm = ((DBGridModel) roomGW.getWidgetModel()).getFormModel();
            fm.setReadOnly(false);
            fm.setTitleVisible(false);
            if (e != null && !isNew) {
                fm.loadEntity(e.getId());
            } else {
                fm.createNew();
            }
            RoomEdit roomEdit = new RoomEdit(fm, (FRoomFilter) filterPanel.getFilterBean());
            new RoomDialog(roomEdit, RoomView.this);
        }

        @Override
        protected GridWidget getGridWidget() {
            return roomGW;
        }

        @Override
        protected String getModuleName() {
            return "rooms";
        }

        @Override
        protected Class<? extends Entity> getEntityClass() {
            return null;
        }

        @Override
        protected void removeChildrenEntity(List<Entity> delList) throws Exception {

        }

        @Override
        protected void refresh() throws Exception {
            RoomView.this.refresh();
        }
    }
}
