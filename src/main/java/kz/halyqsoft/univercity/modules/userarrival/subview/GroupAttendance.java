package kz.halyqsoft.univercity.modules.userarrival.subview;

import com.vaadin.server.Sizeable;
import com.vaadin.ui.*;
import kz.halyqsoft.univercity.entity.beans.univercity.EMPLOYEE;
import kz.halyqsoft.univercity.entity.beans.univercity.view.VDepartmentInfo;
import kz.halyqsoft.univercity.entity.beans.univercity.view.VGroup;
import kz.halyqsoft.univercity.modules.userarrival.UserArrivalView;
import org.r3a.common.dblink.facade.CommonEntityFacadeBean;
import org.r3a.common.dblink.utils.SessionFacadeFactory;
import org.r3a.common.entity.Entity;
import org.r3a.common.entity.ID;
import org.r3a.common.entity.event.EntityEvent;
import org.r3a.common.entity.event.EntityListener;
import org.r3a.common.entity.query.QueryModel;
import org.r3a.common.vaadin.widget.ERefreshType;
import org.r3a.common.vaadin.widget.grid.GridWidget;
import org.r3a.common.vaadin.widget.grid.model.DBGridModel;
import org.r3a.common.vaadin.widget.toolbar.AbstractToolbar;

import java.util.*;

import kz.halyqsoft.univercity.utils.CommonUtils;

public class GroupAttendance implements EntityListener{
    private VerticalLayout mainVL;
    private HorizontalLayout topHL;
    private GridWidget vGroupGW;
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

        vGroupGW = new GridWidget(VGroup.class);
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

    public List<VGroup> getList(String date){
        List<VGroup> groupList = new ArrayList<>();

        Map<Integer,Object> params = new HashMap<>();
        String sql = "SELECT\n" +
                "  g.name as group_name,\n" +
                "  g.id as group_id,\n" +
                "  g.curator_id as curator_id,\n" +
                "  COUNT(DISTINCT se2.student_id) AS count_students_in_the_group,\n" +
                "  count(DISTINCT ua.user_id) AS come_in_students,\n" +
                "  (COUNT(DISTINCT se2.student_id)-count(DISTINCT ua.user_id)) as do_not_come_students,\n" +
                "  CAST ((  (count(DISTINCT ua.user_id)*100) )as FLOAT) /CAST ((   (COUNT (DISTINCT se2.student_id))  ) as FLOAT)  as percentage_of_come_in_students\n" +
                "FROM groups g\n" +
                "  INNER JOIN student_education se\n" +
                "    ON g.id = se.groups_id\n" +
                "  INNER JOIN user_arrival ua\n" +
                "    ON ua.user_id = se.student_id\n" +
                "  INNER JOIN student_education se2\n" +
                "    ON g.id = se2.groups_id\n" +
                "WHERE\n" +
                "  date_trunc('day', ua.created)= date_trunc('day' , TIMESTAMP '"+date+"')\n" +
                "  AND\n" +
                "  ua.come_in = TRUE\n" +
                "  AND\n" +
                "  ua.created = (select max(ua2.created) from user_arrival ua2 " +
                "WHERE date_trunc('day', ua2.created)= date_trunc('day' , TIMESTAMP '"+date+"') and ua2.user_id = ua.user_id)\n" +
                "GROUP BY g.name, curator_id, g.id";

        try {
            List<Object> tmpList = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookupItemsList(sql, params);
            if (!tmpList.isEmpty()) {
                for (Object o : tmpList) {
                    Object[] oo = (Object[]) o;
                    VGroup vg = new VGroup();
                    vg.setGroupName((String)oo[0]);
                    vg.setGroupID((Long) oo[1]);
                    EMPLOYEE employee = null;
                    try{
                        employee = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(EMPLOYEE.class, (ID) oo[2] );
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    vg.setCurator(employee);
                    vg.setCount((Long)oo[3]);
                    vg.setIsPresent((Long)oo[4]);
                    vg.setAbsent((Long)oo[5]);
                    vg.setPercantage((Double) oo[6]);
                    groupList.add(vg);
                }
            }
        } catch (Exception ex) {
            CommonUtils.showMessageAndWriteLog("Unable to load teacher list", ex);
        }
        refreshList(groupList);
        return groupList;
    }

    private void refreshList(List<VGroup> list) {
        ((DBGridModel) vGroupGW.getWidgetModel()).setEntities(list);
        try {
            vGroupGW.refresh();
        } catch (Exception ex) {
            CommonUtils.showMessageAndWriteLog("Unable to refresh department list", ex);
        }
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
