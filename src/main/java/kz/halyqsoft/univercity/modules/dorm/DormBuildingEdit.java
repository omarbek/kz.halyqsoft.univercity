package kz.halyqsoft.univercity.modules.dorm;

import com.vaadin.server.ThemeResource;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import kz.halyqsoft.univercity.entity.beans.univercity.DORM_STUDENT;
import kz.halyqsoft.univercity.entity.beans.univercity.DORM_STUDENT_VIOLATION;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.DORM;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.DORM_ROOM;
import kz.halyqsoft.univercity.modules.dorm.mappedclasses.DormBuilding;
import org.r3a.common.dblink.facade.CommonEntityFacadeBean;
import org.r3a.common.dblink.utils.SessionFacadeFactory;
import org.r3a.common.entity.Entity;
import org.r3a.common.entity.ID;
import org.r3a.common.entity.event.EntityEvent;
import org.r3a.common.entity.query.QueryModel;
import org.r3a.common.entity.query.from.EJoin;
import org.r3a.common.entity.query.from.FromItem;
import org.r3a.common.entity.query.where.ECriteria;
import org.r3a.common.vaadin.view.AbstractCommonView;
import org.r3a.common.vaadin.widget.ERefreshType;
import org.r3a.common.vaadin.widget.dialog.Message;
import org.r3a.common.vaadin.widget.form.AbstractFormWidgetView;
import org.r3a.common.vaadin.widget.grid.GridWidget;
import org.r3a.common.vaadin.widget.grid.model.DBGridModel;
import org.r3a.common.vaadin.widget.toolbar.AbstractToolbar;
import org.r3a.common.vaadin.widget.toolbar.IconToolbar;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

/**
 * @author Dinassil Omarbek
 * @created 25.05.2017.
 */
public class DormBuildingEdit extends AbstractFormWidgetView {

    private static final float GRID_WIDTH = 50f;

    private GridWidget dormBuildingsGrid = new GridWidget(DormBuilding.class);

    DormBuildingEdit() {
        setBackButtonVisible(false);
        dormBuildingsGrid.addEntityListener(this);
        dormBuildingsGrid.setWidth(GRID_WIDTH, Unit.PERCENTAGE);
        AbstractToolbar toolbarPanel = (AbstractToolbar) dormBuildingsGrid.getToolbarPanel().getComponent(0);
        toolbarPanel.setButtonVisible(IconToolbar.REFRESH_BUTTON, false);
        toolbarPanel.addButtonClickListener(AbstractToolbar.DELETE_BUTTON, new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {
                try{
                    DormBuilding dormStudent = (DormBuilding) dormBuildingsGrid.getSelectedEntity();

                    QueryModel<DORM_STUDENT_VIOLATION> dsv = new QueryModel<>(DORM_STUDENT_VIOLATION.class);
                    FromItem fi = dsv.addJoin(EJoin.INNER_JOIN,"dormStudent",DORM_STUDENT.class,"id");
                    FromItem item = fi.addJoin(EJoin.INNER_JOIN,"room",DORM_ROOM.class,"id");
                    FromItem fromItem = item.addJoin(EJoin.INNER_JOIN,"dorm",DORM.class,"id");
                    dsv.addWhere(fromItem,"id",ECriteria.EQUAL,dormStudent.getId());
                    DORM_STUDENT_VIOLATION violation = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookupSingle(dsv);

                    SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).delete(violation);

                } catch (Exception e) {
                e.printStackTrace();
            }
            }
        });
        int buttonIndex = toolbarPanel.addButtonFirst(createEnterButton());
        toolbarPanel.setButtonDescription(buttonIndex, "enter");
        buttonIndex = toolbarPanel.addButtonLast(createRefreshButton());
        toolbarPanel.setButtonDescription(buttonIndex, "refresh");

        DBGridModel dbGridModel = ((DBGridModel) dormBuildingsGrid.getWidgetModel());
        dbGridModel.setCrudEntityClass(DORM.class);
        dbGridModel.setTitleVisible(false);
        dbGridModel.setRefreshType(ERefreshType.MANUAL);
        dbGridModel.getColumnModel(DormBuilding.NAME_COLUMN).setLabelResource("dorm.building.name");
        dbGridModel.getColumnModel(DormBuilding.ADDRESS_COLUMN).setLabelResource("dorm.building.address");
        dbGridModel.getColumnModel(DormBuilding.ROOM_COUNT_COLUMN).setLabelResource("dorm.building.roomCount");
        dbGridModel.getColumnModel(DormBuilding.BUSY_ROOM_COUNT_COLUMN).setLabelResource("dorm.building.busyRoomCount");
        dbGridModel.getColumnModel(DormBuilding.BED_COUNT_COLUMN).setLabelResource("dorm.building.bedCount");
        dbGridModel.getColumnModel(DormBuilding.BUSY_BED_COUNT_COLUMN).setLabelResource("dorm.building.busyBedCount");
        fillDormBuildingsGrid();

        getContent().addComponent(dormBuildingsGrid);
        getContent().setComponentAlignment(dormBuildingsGrid, Alignment.TOP_CENTER);
    }

    @Override
    public void initView(boolean readOnly) throws Exception {
        super.initView(readOnly);
    }

    private Button createEnterButton() {
        Button button = new Button(getUILocaleUtil().getCaption("enter"));
        button.setIcon(new ThemeResource("img/button/arrow_right.png"));
        button.addClickListener(new Button.ClickListener() {

            @Override
            public void buttonClick(Button.ClickEvent event) {
                Entity dormBuildingEntity = dormBuildingsGrid.getSelectedEntity();
                if (dormBuildingEntity == null) {
                    Message.showError(getUILocaleUtil().getMessage("null.record.enter"));
                } else {
                    try {
                        DORM dormBuilding = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class)
                                .lookup(DORM.class, dormBuildingEntity.getId());
                        DormRoomEdit roomsEdit = new DormRoomEdit(dormBuilding);
                        new DormDialog(roomsEdit);
                    } catch (Exception e) {
                        LOG.error("Ошибка загрузки здания общежития", e);
                    }
                }
            }
        });
        return button;
    }

    private Button createRefreshButton() {
        Button button = new Button();
        button.setIcon(new ThemeResource("img/button/refresh.png"));
        button.addStyleName("refresh");
        button.addClickListener(new Button.ClickListener() {

            @Override
            public void buttonClick(Button.ClickEvent event) {
                fillDormBuildingsGrid();
            }
        });
        return button;
    }

    private void fillDormBuildingsGrid() {
        try {
            List<DormBuilding> dormBuildings=new ArrayList<>();
            List tmpList = SessionFacadeFactory.getSessionFacade(
                    CommonEntityFacadeBean.class).lookupItemsList(DormBuilding.SQL, new HashMap<>());
            if (!tmpList.isEmpty()) {
                for (Object o : tmpList) {
                    Object[] oo = (Object[]) o;
                    DormBuilding dormBuilding = new DormBuilding();
                    dormBuilding.setId(ID.valueOf((long) oo[0]));
                    dormBuilding.setName((String) oo[1]);
                    dormBuilding.setAddress((String) oo[2]);
                    dormBuilding.setRoomCount(((Long) oo[3]).intValue());
                    dormBuilding.setBusyRoomCount(((Long) oo[4]).intValue());
                    dormBuilding.setBedCount((Integer) oo[5]);
                    dormBuilding.setBusyBedCount(((BigDecimal) oo[6]).intValue());
                    dormBuildings.add(dormBuilding);
                }
            }
            ((DBGridModel) dormBuildingsGrid.getWidgetModel()).setEntities(dormBuildings);
            dormBuildingsGrid.refresh();
        } catch (Exception e) {
            LOG.error("Ошибка при загрузке общежитий", e);
        }
    }

    @Override
    protected AbstractCommonView getParentView() {
        return null;
    }

    @Override
    public String getViewName() {
        return "DormsEdit";
    }

    @Override
    protected String getViewTitle(Locale locale) {
        return getUILocaleUtil().getCaption("dorm");
    }

    @Override
    public void handleEntityEvent(EntityEvent ev) {
        if (ev.getAction() == EntityEvent.CREATED || ev.getAction() == EntityEvent.MERGED ||
                ev.getAction() == EntityEvent.REMOVED) {
            showSavedNotification();
            fillDormBuildingsGrid();
        }
    }
}
