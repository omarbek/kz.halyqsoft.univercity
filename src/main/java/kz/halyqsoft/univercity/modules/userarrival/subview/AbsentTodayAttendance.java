package kz.halyqsoft.univercity.modules.userarrival.subview;

import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.VerticalLayout;
import kz.halyqsoft.univercity.entity.beans.univercity.USER_ARRIVAL;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.DEPARTMENT;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.POST;
import kz.halyqsoft.univercity.entity.beans.univercity.view.V_EMPLOYEE;
import kz.halyqsoft.univercity.modules.userarrival.subview.dialogs.PrintDialog;
import kz.halyqsoft.univercity.utils.CommonUtils;
import org.r3a.common.dblink.facade.CommonEntityFacadeBean;
import org.r3a.common.dblink.utils.SessionFacadeFactory;
import org.r3a.common.entity.Entity;
import org.r3a.common.entity.event.EntityEvent;
import org.r3a.common.entity.event.EntityListener;
import org.r3a.common.entity.query.QueryModel;
import org.r3a.common.vaadin.widget.ERefreshType;
import org.r3a.common.vaadin.widget.grid.GridWidget;
import org.r3a.common.vaadin.widget.grid.model.DBGridModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @author Omarbek
 * @created on 16.10.2018
 */
public class AbsentTodayAttendance implements EntityListener {

    private VerticalLayout mainVL;

    public AbsentTodayAttendance() {

        GridWidget usersGW = new GridWidget(V_EMPLOYEE.class);
        usersGW.showToolbar(false);

        DBGridModel usersGM = (DBGridModel) usersGW.getWidgetModel();
        usersGM.setRefreshType(ERefreshType.MANUAL);

        Button printBtn = new Button(CommonUtils.getUILocaleUtil().getCaption("export"));
        printBtn.setImmediate(true);
        printBtn.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {

                List<String> tableHeader = new ArrayList<>();
                List<List<String>> tableBody= new ArrayList<>();

                String fileName = "document";

                tableHeader.add(CommonUtils.getUILocaleUtil().getCaption("code"));
                tableHeader.add(CommonUtils.getUILocaleUtil().getCaption("user"));
                tableHeader.add(CommonUtils.getUILocaleUtil().getEntityLabel(DEPARTMENT.class));
                tableHeader.add(CommonUtils.getUILocaleUtil().getEntityLabel(POST.class));

                for(int i = 0 ; i < usersGW.getAllEntities().size(); i++){
                    V_EMPLOYEE user = (V_EMPLOYEE) usersGW.getAllEntities().get(i);
                    if(usersGW.getCaption()!=null){
                        fileName = usersGW.getCaption();
                    }
                    List<String> list = new ArrayList<>();
                    list.add(user.getCode());
                    list.add(user.getFirstName() + " " + user.getLastName() + " " +  (user.getMiddleName() != null ? user.getMiddleName() : ""));
                    list.add(user.getDepartment() != null ? user.getDepartment().getDeptName() : "");
                    list.add(user.getPostName() != null ? user.getPostName() : "");
                    tableBody.add(list);
                }


                PrintDialog printDialog = new PrintDialog(tableHeader, tableBody , CommonUtils.getUILocaleUtil().getCaption("print"),fileName);
            }
        });

        mainVL = new VerticalLayout();
        mainVL.setSpacing(true);
        mainVL.setSizeFull();


        refreshGridWidget(usersGW);
        mainVL.addComponent(printBtn);
        mainVL.setComponentAlignment(printBtn, Alignment.TOP_RIGHT);
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
