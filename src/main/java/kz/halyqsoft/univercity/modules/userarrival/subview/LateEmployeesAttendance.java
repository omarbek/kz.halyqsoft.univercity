package kz.halyqsoft.univercity.modules.userarrival.subview;

import com.vaadin.data.Property;
import com.vaadin.ui.*;
import kz.halyqsoft.univercity.entity.beans.univercity.USER_ARRIVAL;
import kz.halyqsoft.univercity.entity.beans.univercity.view.VGroup;
import kz.halyqsoft.univercity.entity.beans.univercity.view.VStudentInfo;
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
import org.r3a.common.vaadin.widget.grid.model.GridColumnModel;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * @author Omarbek
 * @created on 16.10.2018
 */
public class LateEmployeesAttendance implements EntityListener {

    private VerticalLayout mainVL;
    private DateField dateField;
    private GridWidget usersGW;
    private DBGridModel usersGM;
    public LateEmployeesAttendance(){


        GridWidget usersGW = new GridWidget(USER_ARRIVAL.class);
        usersGW.showToolbar(false);

        usersGM = (DBGridModel) usersGW.getWidgetModel();
        usersGM.setRefreshType(ERefreshType.MANUAL);

        Button printBtn = new Button(CommonUtils.getUILocaleUtil().getCaption("export"));
        printBtn.setImmediate(true);
        printBtn.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {

                List<String> tableHeader = new ArrayList<>();
                List<List<String>> tableBody= new ArrayList<>();

                String fileName = "document";

                tableHeader.add(CommonUtils.getUILocaleUtil().getCaption("user"));
                tableHeader.add(CommonUtils.getUILocaleUtil().getCaption("time"));

                for(int i = 0 ; i < usersGW.getAllEntities().size(); i++){
                    USER_ARRIVAL userArrival = (USER_ARRIVAL) usersGW.getAllEntities().get(i);
                    if(usersGW.getCaption()!=null){
                        fileName = usersGW.getCaption();
                    }
                    List<String> list = new ArrayList<>();
                    list.add(userArrival.getUser().toString());
                    list.add(CommonUtils.getFormattedDate(userArrival.getCreated()));
                    tableBody.add(list);
                }


                PrintDialog printDialog = new PrintDialog(tableHeader, tableBody , CommonUtils.getUILocaleUtil().getCaption("print"),fileName);
            }
        });
        HorizontalLayout buttonPanel = CommonUtils.createButtonPanel();

        dateField = new DateField();
        dateField.setValue(new Date());
        dateField.addValueChangeListener(new Property.ValueChangeListener() {
            @Override
            public void valueChange(Property.ValueChangeEvent valueChangeEvent) {
                refreshGridWidget(usersGW);
            }
        });

        buttonPanel.addComponent(dateField);
        buttonPanel.setComponentAlignment(dateField, Alignment.MIDDLE_CENTER);

        buttonPanel.addComponent(printBtn);
        buttonPanel.setComponentAlignment(printBtn, Alignment.MIDDLE_CENTER);



        mainVL = new VerticalLayout();
        mainVL.setSpacing(true);
        mainVL.setSizeFull();


        mainVL.addComponent(buttonPanel);
        mainVL.setComponentAlignment(buttonPanel, Alignment.TOP_RIGHT);
        mainVL.addComponent(usersGW);
    }

    private void refreshGridWidget(GridWidget usersGW) {
        try {
            String sql = "SELECT arriv.* " +
                    "FROM user_arrival arriv INNER JOIN v_employee empl ON empl.id = arriv.user_id " +
                    "WHERE date_trunc('day', arriv.created) = date_trunc('day', TIMESTAMP '"+ CommonUtils.getFormattedDate(dateField.getValue())+"') " +
                    "      AND arriv.created = (SELECT min(max_arriv.created) " +
                    "                           FROM user_arrival max_arriv " +
                    "                           WHERE max_arriv.user_id = arriv.user_id " +
                    "                                 AND date_trunc('day', max_arriv.created) = date_trunc('day', TIMESTAMP '"+ CommonUtils.getFormattedDate(dateField.getValue())+"') " +
                    "                                 AND come_in = TRUE) " +
                    "      AND come_in = TRUE AND arriv.created :: TIME > '08:40:00' " +
                    "ORDER BY created DESC;";
            List<USER_ARRIVAL> userArrivals = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(sql,
                    new HashMap<>(), USER_ARRIVAL.class);
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
