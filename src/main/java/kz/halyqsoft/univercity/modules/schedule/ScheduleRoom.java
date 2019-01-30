package kz.halyqsoft.univercity.modules.schedule;

import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.shared.ui.combobox.FilteringMode;
import com.vaadin.ui.*;
import kz.halyqsoft.univercity.entity.beans.USERS;
import kz.halyqsoft.univercity.entity.beans.univercity.EMPLOYEE;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.*;
import kz.halyqsoft.univercity.entity.beans.univercity.view.VRoom;
import kz.halyqsoft.univercity.entity.beans.univercity.view.V_EMPLOYEE;
import kz.halyqsoft.univercity.filter.FEmployeeFilter;
import kz.halyqsoft.univercity.filter.FRoomFilter;
import kz.halyqsoft.univercity.filter.panel.EmployeeFilterPanel;
import kz.halyqsoft.univercity.filter.panel.RoomFilterPanel;
import kz.halyqsoft.univercity.modules.room.RoomDialog;
import kz.halyqsoft.univercity.modules.room.RoomEdit;
import kz.halyqsoft.univercity.modules.room.RoomView;
import kz.halyqsoft.univercity.modules.workflowforemp.GridWidgetDialog;
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
import org.r3a.common.vaadin.view.AbstractCommonView;
import org.r3a.common.vaadin.widget.ERefreshType;
import org.r3a.common.vaadin.widget.dialog.select.ESelectType;
import org.r3a.common.vaadin.widget.dialog.select.custom.grid.CustomGridSelectDialog;
import org.r3a.common.vaadin.widget.filter2.AbstractFilterBean;
import org.r3a.common.vaadin.widget.filter2.FilterPanelListener;
import org.r3a.common.vaadin.widget.form.FormModel;
import org.r3a.common.vaadin.widget.form.field.fk.FKFieldModel;
import org.r3a.common.vaadin.widget.grid.GridWidget;
import org.r3a.common.vaadin.widget.grid.model.DBGridModel;
import org.r3a.common.vaadin.widget.toolbar.AbstractToolbar;

import java.math.BigDecimal;
import java.util.*;


public class ScheduleRoom extends AbstractCommonView implements EntityListener, FilterPanelListener {

    private RoomFilterPanel filterPanel;
    private GridWidget roomGW;
    private String title;
    private VerticalLayout mainVL;
    public ScheduleRoom(String title) {
        filterPanel = new RoomFilterPanel(new FRoomFilter());
        this.title = title;
        mainVL = new VerticalLayout();
        mainVL.setSpacing(true);
        mainVL.setSizeFull();
        //initView(true);
    }

    @Override
    public String getViewName() {
        return title;
    }

    @Override
    protected String getViewTitle(Locale locale) {
        return title;
    }


    @Override
    public void initView(boolean b) throws Exception {
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


        roomGW = new GridWidget(VRoom.class);
        roomGW.addEntityListener(this);
        roomGW.setButtonVisible(AbstractToolbar.PREVIEW_BUTTON, false);
        roomGW.setButtonVisible(AbstractToolbar.REFRESH_BUTTON, false);
        DBGridModel roomGM = (DBGridModel) roomGW.getWidgetModel();
        roomGM.setCrudEntityClass(ROOM.class);
        roomGM.setReadOnly(true);
        roomGM.setTitleVisible(false);
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




        mainVL.addComponent(filterPanel);
        mainVL.addComponent(roomGW);
    }

    @Override
    public void handleEntityEvent(EntityEvent ev) {
        if (ev.getSource().equals(roomGW)) {
            if (ev.getAction() == EntityEvent.SELECTED) {
                if(ev.getEntities().size()>0){
                    FormModel fm = ((DBGridModel) roomGW.getWidgetModel()).getFormModel();
                    fm.setReadOnly(false);
                    fm.setTitleVisible(false);
                    try{
                        fm.loadEntity(ev.getEntities().get(0).getId());
                        ScheduleRoomTimeLimitDialog scheduleRoomTimeLimitDialog = new ScheduleRoomTimeLimitDialog(fm,(FRoomFilter)filterPanel.getFilterBean());
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    @Override
    public void doFilter(AbstractFilterBean abstractFilterBean) {
        FRoomFilter rf = (FRoomFilter) abstractFilterBean;
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

    public RoomFilterPanel getFilterPanel() {
        return filterPanel;
    }

    @Override
    public void clearFilter() {
        doFilter(filterPanel.getFilterBean());
    }

    @Override
    public boolean preCreate(Object o, int i) {
        return false;
    }

    @Override
    public void onCreate(Object o, Entity entity, int i) {

    }

    @Override
    public boolean onEdit(Object source, Entity e, int buttonId) {
        return true;
    }
    @Override
    public boolean onPreview(Object source, Entity e, int buttonId) {
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
    public boolean preSave(Object o, Entity entity, boolean b, int i) throws Exception {
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

    public VerticalLayout getMainVL() {
        return mainVL;
    }
}
