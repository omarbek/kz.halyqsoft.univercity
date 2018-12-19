package kz.halyqsoft.univercity.modules.dorm;

import com.vaadin.server.ThemeResource;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.DORM;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.DORM_ROOM;
import kz.halyqsoft.univercity.modules.dorm.mappedclasses.DormRoom;
import org.r3a.common.dblink.facade.CommonEntityFacadeBean;
import org.r3a.common.dblink.utils.SessionFacadeFactory;
import org.r3a.common.entity.Entity;
import org.r3a.common.entity.ID;
import org.r3a.common.entity.event.EntityEvent;
import org.r3a.common.vaadin.view.AbstractCommonView;
import org.r3a.common.vaadin.widget.ERefreshType;
import org.r3a.common.vaadin.widget.form.AbstractFormWidgetView;
import org.r3a.common.vaadin.widget.grid.GridWidget;
import org.r3a.common.vaadin.widget.grid.model.DBGridModel;
import org.r3a.common.vaadin.widget.toolbar.AbstractToolbar;
import org.r3a.common.vaadin.widget.toolbar.IconToolbar;

import java.util.*;

/**
 * @author Dinassil Omarbek
 * @created 26.05.2017.
 */
public class DormRoomEdit extends AbstractFormWidgetView {

    private static final float GRID_WIDTH = 50f;

    private final GridWidget dormRoomGrid = new GridWidget(DormRoom.class);

    private final DORM dormBuilding;

    DormRoomEdit(DORM dormBuilding) {
        setBackButtonVisible(false);
        this.dormBuilding = dormBuilding;

        dormRoomGrid.addEntityListener(this);
        dormRoomGrid.setWidth(GRID_WIDTH, Unit.PERCENTAGE);

        AbstractToolbar toolbarPanel = (AbstractToolbar) dormRoomGrid.getToolbarPanel().getComponent(0);
        toolbarPanel.setButtonVisible(IconToolbar.REFRESH_BUTTON, false);
        int buttonIndex = toolbarPanel.addButtonLast(createRefreshButton());
        toolbarPanel.setButtonDescription(buttonIndex, "refresh");

        DBGridModel dbGridModel = ((DBGridModel) dormRoomGrid.getWidgetModel());
        dbGridModel.setCrudEntityClass(DORM_ROOM.class);
        dbGridModel.setTitleVisible(false);
        dbGridModel.setRefreshType(ERefreshType.MANUAL);
        dbGridModel.getColumnModel(DormRoom.ROOM_NO_COLUMN).setLabelResource("dorm.room.roomNo");
        dbGridModel.getColumnModel(DormRoom.BED_COUNT_COLUMN).setLabelResource("dorm.room.bedCount");
        dbGridModel.getColumnModel(DormRoom.BUSY_BED_COUNT_COLUMN).setLabelResource("dorm.room.busyBedCount");
        dbGridModel.getColumnModel(DormRoom.COST_COLUMN).setLabelResource("dorm.room.cost");
        fillDormRoomsGrid();

        getContent().addComponent(dormRoomGrid);
        getContent().setComponentAlignment(dormRoomGrid, Alignment.TOP_CENTER);
    }

    @Override
    public void initView(boolean readOnly) throws Exception {
        super.initView(readOnly);
    }

    private Button createRefreshButton() {
        Button button = new Button();
        button.setIcon(new ThemeResource("img/button/refresh.png"));
        button.addStyleName("refresh");
        button.addClickListener(new Button.ClickListener() {

            @Override
            public void buttonClick(Button.ClickEvent event) {
                fillDormRoomsGrid();
            }
        });
        return button;
    }

    private void fillDormRoomsGrid() {
        try {
            List<DormRoom> dormRooms = new ArrayList<>();

            Map<Integer, Object> params = new HashMap<>();
            params.put(1, dormBuilding.getId().getId());
            List tmpList = SessionFacadeFactory.getSessionFacade(
                    CommonEntityFacadeBean.class).lookupItemsList(DormRoom.SQL, params);
            if (!tmpList.isEmpty()) {
                for (Object o : tmpList) {
                    Object[] oo = (Object[]) o;
                    DormRoom dormRoom = new DormRoom();
                    dormRoom.setId(ID.valueOf((long) oo[0]));
                    dormRoom.setRoomNo((String) oo[1]);
                    dormRoom.setBedCount((Integer) oo[2]);
                    dormRoom.setBusyBedCount(((Long) oo[3]).intValue());
                    dormRoom.setCost((Integer) oo[4]);
                    dormRooms.add(dormRoom);
                }
            }

            ((DBGridModel) dormRoomGrid.getWidgetModel()).setEntities(dormRooms);
            dormRoomGrid.refresh();
        } catch (Exception e) {
            LOG.error("Ошибка при загрузке комнать в общежитии", e);
        }
    }

    @Override
    protected AbstractCommonView getParentView() {
        return new DormBuildingEdit();
    }

    @Override
    public String getViewName() {
        return "RoomsEdit";
    }

    @Override
    protected String getViewTitle(Locale locale) {
        return getUILocaleUtil().getCaption("dorm.rooms").concat(" (").concat(dormBuilding.getName()).concat(")");
    }

    @Override
    public boolean preSave(Object source, Entity entity, boolean isNew, int buttonId) throws Exception {
        DORM_ROOM room = (DORM_ROOM) entity;
        if (isNew) {
            room.setDorm(dormBuilding);
        }
        return true;
    }



    @Override
    public void handleEntityEvent(EntityEvent ev) {
        if (ev.getAction() == EntityEvent.CREATED || ev.getAction() == EntityEvent.MERGED ||
                ev.getAction() == EntityEvent.REMOVED) {
            showSavedNotification();
            fillDormRoomsGrid();
        }
    }
}
