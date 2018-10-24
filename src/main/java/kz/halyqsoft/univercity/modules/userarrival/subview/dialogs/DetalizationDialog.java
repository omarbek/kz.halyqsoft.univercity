package kz.halyqsoft.univercity.modules.userarrival.subview.dialogs;

import com.vaadin.ui.*;
import kz.halyqsoft.univercity.entity.beans.USERS;
import kz.halyqsoft.univercity.entity.beans.univercity.USER_ARRIVAL;
import kz.halyqsoft.univercity.entity.beans.univercity.USER_PHOTO;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.TURNSTILE_TYPE;
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
import org.r3a.common.vaadin.widget.form.FormModel;
import org.r3a.common.vaadin.widget.form.GridFormWidget;
import org.r3a.common.vaadin.widget.photo.PhotoWidget;
import org.r3a.common.vaadin.widget.table.TableWidget;
import org.r3a.common.vaadin.widget.table.model.DBTableModel;

import javax.persistence.NoResultException;
import java.text.SimpleDateFormat;
import java.util.*;

public class DetalizationDialog extends AbstractDialog implements EntityListener{

    private String title;
    private VerticalLayout userArrivalVL;
    private VerticalLayout userDataVL;
    private HorizontalSplitPanel mainHSP;

    public DetalizationDialog(String title, USERS user , Date date) {

        this.title = title;

        setImmediate(true);
        setWidth(95, Unit.PERCENTAGE);
        setHeight(70, Unit.PERCENTAGE);

        mainHSP = new HorizontalSplitPanel();
        mainHSP.setSizeFull();
        mainHSP.setImmediate(true);
        mainHSP.setLocked(true);
        mainHSP.setSplitPosition(45);

        userArrivalVL = new VerticalLayout();
        userArrivalVL.setSizeFull();
        userArrivalVL.setImmediate(true);
        userArrivalVL.setDefaultComponentAlignment(Alignment.MIDDLE_CENTER);

        userDataVL = new VerticalLayout();
        userDataVL.setSizeFull();
        userDataVL.setImmediate(true);
        userDataVL.setDefaultComponentAlignment(Alignment.MIDDLE_CENTER);

        VerticalLayout userDataVL = new VerticalLayout();
        userDataVL.setDefaultComponentAlignment(Alignment.MIDDLE_CENTER);

        userDataVL.setImmediate(true);
        userDataVL.setSizeFull();
        userDataVL.setResponsive(true);

        USER_PHOTO userPhoto = null;
        QueryModel<USER_PHOTO> userPhotoQM = new QueryModel<>(USER_PHOTO.class);
        userPhotoQM.addWhere("user" , ECriteria.EQUAL , user.getId());
        try{
            userPhoto = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookupSingle(userPhotoQM);
        }catch (NoResultException nre){

        }catch (Exception e){
            e.printStackTrace();
        }
        if(userPhoto!=null){
            PhotoWidget photoWidget = new PhotoWidget(userPhoto.getPhoto());
            photoWidget.setReadOnly(true);
            photoWidget.setSaveButtonVisible(false);
            photoWidget.setEnabled(false);
            photoWidget.setResponsive(true);

            for(int i = 0 ; i < photoWidget.getContent().getComponentCount() ;i ++){
                if(photoWidget.getContent().getComponent(i) instanceof Upload || photoWidget.getContent().getComponent(i) instanceof Button){
                    photoWidget.getContent().getComponent(i).setVisible(false);
                }
            }
            userArrivalVL.addComponent(photoWidget);
        }
        FormModel userModel = new FormModel(USERS.class);
        GridFormWidget gridFormWidget = null;
        try{
            userModel.loadEntity(user.getId());
            userModel.setReadOnly(true);
            gridFormWidget = new GridFormWidget(userModel);
            gridFormWidget.setResponsive(true);
            gridFormWidget.setEnabled(false);

        }catch (Exception e){
            e.printStackTrace();
        }
        if(gridFormWidget!=null) {
            userDataVL.addComponent(gridFormWidget);
        }
        TableWidget userArrivalTW = new TableWidget(USER_ARRIVAL.class);
        userArrivalTW.setCaption(getUILocaleUtil().getCaption("userArrivalTW"));
        userArrivalTW.showToolbar(false);
        userArrivalTW.setImmediate(true);
        userArrivalTW.setSizeFull();
        userArrivalTW.addEntityListener(this);

        DBTableModel dbTableModel = (DBTableModel)userArrivalTW.getWidgetModel();
        dbTableModel.setEntities(getList(user,date));
        dbTableModel.setRefreshType(ERefreshType.MANUAL);

        TableWidget userArrivalBySemTW = new TableWidget(USER_ARRIVAL.class);
        userArrivalBySemTW.setCaption(" ");
        userArrivalBySemTW.setCaption(getUILocaleUtil().getCaption("userArrivalBySemTW"));
        userArrivalBySemTW.showToolbar(false);
        userArrivalBySemTW.setImmediate(true);
        userArrivalBySemTW.setSizeFull();
        userArrivalBySemTW.addEntityListener(this);

        DBTableModel userArrivalBySemTM = (DBTableModel)userArrivalBySemTW.getWidgetModel();
        userArrivalBySemTM.setEntities(getListBySem(user,date));
        userArrivalBySemTM.setRefreshType(ERefreshType.MANUAL);

        Button closeBtn = new Button(getUILocaleUtil().getCaption("close"));
        closeBtn.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {
                close();
            }
        });

        userArrivalVL.addComponent(userArrivalTW);
        userArrivalVL.addComponent(userArrivalBySemTW);
        this.userDataVL.addComponent(userDataVL);
        mainHSP.setFirstComponent(userArrivalVL);
        mainHSP.setSecondComponent(this.userDataVL);

        getContent().addComponent(mainHSP);
        getContent().addComponent(closeBtn);
        getContent().setComponentAlignment(closeBtn,Alignment.MIDDLE_CENTER);

        AbstractWebUI.getInstance().addWindow(this);
    }


    public List<USER_ARRIVAL> getList(USERS user, Date date){
        List<USER_ARRIVAL> userArrivals = new ArrayList<>();

        Map<Integer,Object> params = new HashMap<>();
        String formattedDate = new SimpleDateFormat("yyyy-MM-dd' 'HH:mm:ss.SSS").format(date);

        String sql = "select * from user_arrival ua " +
                "\nwhere ua.user_id = " + user.getId().getId().longValue()+" " +
                " and date_trunc('day',ua.created) = date_trunc('day', timestamp'"+formattedDate+"')"+
                "\n ORDER BY ua.created ";

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

    public List<USER_ARRIVAL> getListBySem(USERS user, Date date){
        List<USER_ARRIVAL> userArrivalList = new ArrayList<>();

        Map<Integer,Object> params = new HashMap<>();
        String formattedDate = new SimpleDateFormat("yyyy-MM-dd' 'HH:mm:ss.SSS").format(date);

        String sql = "select * from user_arrival ua " +
                "\nwhere ua.user_id = " + user.getId().getId().longValue()+" " +
                "\n ORDER BY ua.created ";

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
                        userArrivalList.add(ua);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }
        } catch (Exception ex) {
            CommonUtils.showMessageAndWriteLog("Unable to load user_arrival list", ex);
        }
        return userArrivalList;
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
