package kz.halyqsoft.univercity.modules.userarrival.subview;

import com.vaadin.server.Sizeable;
import com.vaadin.ui.*;
import kz.halyqsoft.univercity.entity.beans.univercity.view.VGroup;
import kz.halyqsoft.univercity.modules.userarrival.UserArrivalView;
import org.r3a.common.entity.Entity;
import org.r3a.common.entity.event.EntityEvent;
import org.r3a.common.entity.event.EntityListener;
import org.r3a.common.entity.query.QueryModel;
import org.r3a.common.vaadin.widget.ERefreshType;
import org.r3a.common.vaadin.widget.grid.GridWidget;
import org.r3a.common.vaadin.widget.grid.model.DBGridModel;
import org.r3a.common.vaadin.widget.toolbar.AbstractToolbar;

import java.util.Date;
import java.util.List;

import kz.halyqsoft.univercity.utils.CommonUtils;

public class GroupAttendance implements EntityListener{
    private VerticalLayout mainVL;
    private HorizontalLayout topHL;
    public GroupAttendance(){
        mainVL = new VerticalLayout();
        mainVL.setImmediate(true);

        topHL = new HorizontalLayout();
        topHL.setWidth(100, Sizeable.Unit.PERCENTAGE);
        topHL.setImmediate(true);

        init();
    }

    private void init(){
        Button backButton = new Button(CommonUtils.getUILocaleUtil().getCaption("backButton"));
        backButton.setVisible(false);

        topHL.addComponent(backButton);
        topHL.setComponentAlignment(backButton, Alignment.TOP_LEFT);

        DateField dateField = new DateField();
        dateField.setValue(new Date());

        topHL.addComponent(dateField);
        topHL.setComponentAlignment(dateField, Alignment.TOP_RIGHT);

        GridWidget vGroupGW = new GridWidget(VGroup.class);
        vGroupGW.setImmediate(true);
        vGroupGW.showToolbar(false);
        vGroupGW.setButtonVisible(AbstractToolbar.REFRESH_BUTTON, true);
        vGroupGW.addEntityListener(this);

        DBGridModel vGroupGM = (DBGridModel) vGroupGW.getWidgetModel();
        vGroupGM.setTitleVisible(false);
        vGroupGM.setMultiSelect(false);
        //vGroupGM.setEntities(getList(date.getValue().toString()));
        vGroupGM.setRefreshType(ERefreshType.MANUAL);


        mainVL.addComponent(topHL);
        mainVL.addComponent(vGroupGW);
    }

    public VerticalLayout getMainVL() {
        return mainVL;
    }



    @Override
    public void handleEntityEvent(EntityEvent entityEvent) {

    }

    @Override
    public boolean preCreate(Object o, int i) {
        return false;
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
}
