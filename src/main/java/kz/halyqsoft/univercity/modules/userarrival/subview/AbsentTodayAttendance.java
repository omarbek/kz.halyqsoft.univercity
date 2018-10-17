package kz.halyqsoft.univercity.modules.userarrival.subview;

import com.vaadin.ui.VerticalLayout;
import kz.halyqsoft.univercity.entity.beans.univercity.view.V_EMPLOYEE;
import org.r3a.common.dblink.facade.CommonEntityFacadeBean;
import org.r3a.common.dblink.utils.SessionFacadeFactory;
import org.r3a.common.entity.Entity;
import org.r3a.common.entity.event.EntityEvent;
import org.r3a.common.entity.event.EntityListener;
import org.r3a.common.entity.query.QueryModel;
import org.r3a.common.vaadin.widget.ERefreshType;
import org.r3a.common.vaadin.widget.grid.GridWidget;
import org.r3a.common.vaadin.widget.grid.model.DBGridModel;

import java.util.HashMap;
import java.util.List;

/**
 * @author Omarbek
 * @created on 16.10.2018
 */
public class AbsentTodayAttendance implements EntityListener {

    private VerticalLayout mainVL;

    public AbsentTodayAttendance() {
        mainVL = new VerticalLayout();
        mainVL.setSpacing(true);
        mainVL.setSizeFull();

        GridWidget usersGW = new GridWidget(V_EMPLOYEE.class);
        usersGW.showToolbar(false);

        DBGridModel usersGM = (DBGridModel) usersGW.getWidgetModel();
        usersGM.setRefreshType(ERefreshType.MANUAL);

        refreshGridWidget(usersGW);
        mainVL.addComponent(usersGW);
    }

    private void refreshGridWidget(GridWidget usersGW) {
        try {
            String sql = "SELECT * " +
                    "FROM v_employee " +
                    "WHERE id NOT IN (SELECT DISTINCT empl_in.id " +
                    "                 FROM user_arrival arriv " +
                    "                   INNER JOIN v_employee empl_in ON empl_in.id = arriv.user_id " +
                    "                 WHERE date_trunc('day', arriv.created) = date_trunc('day', now()) " +
                    "                       AND come_in = TRUE)" +
                    " order by dept_code";
            List<V_EMPLOYEE> userArrivals = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).
                    lookup(sql, new HashMap<>(), V_EMPLOYEE.class);
            ((DBGridModel) usersGW.getWidgetModel()).setEntities(userArrivals);
            usersGW.refresh();
        } catch (Exception e) {
            e.printStackTrace();//TODO catch
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

    public VerticalLayout getMainVL() {
        return mainVL;
    }
}
