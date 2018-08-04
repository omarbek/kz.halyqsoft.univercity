package kz.halyqsoft.univercity.modules.userarrival.subview.dialogs;

import com.vaadin.ui.Button;
import com.vaadin.ui.VerticalLayout;
import kz.halyqsoft.univercity.entity.beans.USERS;
import kz.halyqsoft.univercity.entity.beans.univercity.STUDENT;
import kz.halyqsoft.univercity.entity.beans.univercity.USER_ARRIVAL;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.TURNSTILE_TYPE;
import kz.halyqsoft.univercity.entity.beans.univercity.view.VGroup;
import kz.halyqsoft.univercity.entity.beans.univercity.view.VStudentInfo;
import kz.halyqsoft.univercity.utils.CommonUtils;
import org.r3a.common.dblink.facade.CommonEntityFacadeBean;
import org.r3a.common.dblink.utils.SessionFacadeFactory;
import org.r3a.common.entity.Entity;
import org.r3a.common.entity.ID;
import org.r3a.common.entity.event.EntityEvent;
import org.r3a.common.entity.event.EntityListener;
import org.r3a.common.entity.query.QueryModel;
import org.r3a.common.entity.query.where.ECriteria;
import org.r3a.common.vaadin.AbstractWebUI;
import org.r3a.common.vaadin.widget.ERefreshType;
import org.r3a.common.vaadin.widget.dialog.AbstractDialog;
import org.r3a.common.vaadin.widget.grid.GridWidget;
import org.r3a.common.vaadin.widget.grid.model.DBGridModel;
import org.r3a.common.vaadin.widget.table.TableWidget;
import org.r3a.common.vaadin.widget.table.model.DBTableModel;

import javax.swing.*;
import java.text.SimpleDateFormat;
import java.util.*;

public class DetalizationDialog extends AbstractDialog implements EntityListener{

    private String title;
    private VerticalLayout mainVL;
    public DetalizationDialog(String title, USERS user , Date date) {

        this.title = title;

        setImmediate(true);
        setWidth(60, Unit.PERCENTAGE);
        setHeight(60, Unit.PERCENTAGE);

        mainVL = new VerticalLayout();
        mainVL.setSizeFull();
        mainVL.setImmediate(true);

        TableWidget userArrivalTW = new TableWidget(USER_ARRIVAL.class);
        userArrivalTW.showToolbar(false);
        userArrivalTW.setImmediate(true);
        userArrivalTW.setSizeFull();
        userArrivalTW.addEntityListener(this);

        DBTableModel dbTableModel = (DBTableModel)userArrivalTW.getWidgetModel();
        dbTableModel.setEntities(getList(user,date));
        dbTableModel.setRefreshType(ERefreshType.MANUAL);



        Button closeBtn = new Button(getUILocaleUtil().getCaption("close"));
        closeBtn.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {
                close();
            }
        });

        mainVL.addComponent(userArrivalTW);

        getContent().addComponent(mainVL);
        getContent().addComponent(closeBtn);

        AbstractWebUI.getInstance().addWindow(this);
    }


    public List<USER_ARRIVAL> getList(USERS user, Date date){
        List<USER_ARRIVAL> userArrivals = new ArrayList<>();

        Map<Integer,Object> params = new HashMap<>();
        String formattedDate = new SimpleDateFormat("yyyy-MM-dd' 'HH:mm:ss.SSS").format(date);

        String sql = "select * from user_arrival ua " +
                "\nwhere ua.user_id = "+user.getId().getId().longValue()+" " +
                "\nand date_trunc('day' , ua.created) = date_trunc('day' , TIMESTAMP '"+formattedDate+"');";

        try {
            List<Object> tmpList = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookupItemsList(sql, params);
            if (!tmpList.isEmpty()) {
                for (Object o : tmpList) {
                    try{
                        Object[] oo = (Object[]) o;
                        USER_ARRIVAL ua = new USER_ARRIVAL();
                        ua.setId(ID.valueOf((Long)oo[0]));
                        ID userId = ID.valueOf((Long)oo[1]);
                        if(userId!=null){
                            ua.setUser(SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(USERS.class,userId));
                        }
                        ua.setCreated((Date)oo[2]);
                        ua.setComeIn((Boolean)oo[3]);

                        ID turnstileId = ID.valueOf((Long)oo[4]);
                        if(turnstileId!=null){
                            ua.setTurnstileType(SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(TURNSTILE_TYPE.class,turnstileId));
                        }
                        userArrivals.add(ua);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }
        } catch (Exception ex) {
            CommonUtils.showMessageAndWriteLog("Unable to load user_arrival list", ex);
        }
        return userArrivals;
    }


    @Override
    protected String createTitle() {
        return title;
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
