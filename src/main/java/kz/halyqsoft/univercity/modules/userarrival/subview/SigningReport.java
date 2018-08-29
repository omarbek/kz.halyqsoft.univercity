package kz.halyqsoft.univercity.modules.userarrival.subview;

import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.server.Sizeable;
import com.vaadin.shared.ui.combobox.FilteringMode;
import com.vaadin.shared.ui.grid.HeightMode;
import com.vaadin.ui.*;
import kz.halyqsoft.univercity.entity.beans.USERS;
import kz.halyqsoft.univercity.entity.beans.univercity.USER_ARRIVAL;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.TURNSTILE_TYPE;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.USER_TYPE;
import kz.halyqsoft.univercity.filter.FUserFilter;
import kz.halyqsoft.univercity.filter.panel.UserFilterPanel;
import kz.halyqsoft.univercity.modules.userarrival.subview.dialogs.SignDialog;
import kz.halyqsoft.univercity.utils.CommonUtils;
import kz.halyqsoft.univercity.utils.SampleEntityListener;
import org.r3a.common.dblink.facade.CommonEntityFacadeBean;
import org.r3a.common.dblink.utils.SessionFacadeFactory;
import org.r3a.common.entity.ID;
import org.r3a.common.entity.event.EntityEvent;
import org.r3a.common.entity.query.QueryModel;
import org.r3a.common.vaadin.widget.ERefreshType;
import org.r3a.common.vaadin.widget.filter2.AbstractFilterBean;
import org.r3a.common.vaadin.widget.filter2.FilterPanelListener;
import org.r3a.common.vaadin.widget.grid.GridWidget;
import org.r3a.common.vaadin.widget.grid.model.DBGridModel;
import org.r3a.common.vaadin.widget.grid.model.GridColumnModel;

import java.util.*;

public class SigningReport extends SampleEntityListener implements FilterPanelListener{

    private UserFilterPanel filterPanel;


    private VerticalLayout mainVL;
    private GridWidget usersGW;
    private DBGridModel usersGM;
    List <ID> restrictedIDs = new ArrayList();
    public SigningReport(){
        restrictedIDs.add(ID.valueOf(1));
        restrictedIDs.add(ID.valueOf(2));

        filterPanel = new UserFilterPanel(new FUserFilter());
        filterPanel.addFilterPanelListener(this);

        mainVL = new VerticalLayout();
        mainVL.setImmediate(true);
        mainVL.setSizeFull();
        mainVL.setHeight(100, Sizeable.Unit.PERCENTAGE);

        usersGW = new GridWidget(USER_ARRIVAL.class);
        usersGW.setImmediate(true);
        usersGW.addEntityListener(this);
        usersGW.showToolbar(false);
        usersGW.setHeight(100, Sizeable.Unit.PERCENTAGE);



        usersGM = (DBGridModel) usersGW.getWidgetModel();
        usersGM.setRowNumberVisible(true);
        usersGM.setRowNumberWidth(80);
        usersGW.setHeight(100, Sizeable.Unit.PERCENTAGE);
        usersGM.setRefreshType(ERefreshType.MANUAL);
        usersGM.setHeightMode(HeightMode.CSS);


        usersGM.getColumnModel("manuallySigned").setInGrid(true);
        usersGM.getColumnModel("user").setInGrid(true);
        usersGM.getColumnModel("comeIn").setInGrid(true);

        for(GridColumnModel gcm : usersGM.getColumnModels()){
            gcm.setInGrid(true);
        }

        usersGM.getFormModel().getFieldModel("manuallySigned").setInView(true);
        usersGM.getFormModel().getFieldModel("manuallySigned").setInEdit(true);
        usersGM.getFormModel().getFieldModel("user").setInView(true);
        usersGM.getFormModel().getFieldModel("user").setInEdit(true);
        usersGM.getFormModel().getFieldModel("comeIn").setInView(true);
        usersGM.getFormModel().getFieldModel("comeIn").setInEdit(true);

        FUserFilter fUserFilter = (FUserFilter) filterPanel.getFilterBean();
        if (fUserFilter.hasFilter()) {
            doFilter(fUserFilter);
        }
        try{
            initFilter();
        }catch (Exception e){
            e.printStackTrace();
        }
        mainVL.addComponent(filterPanel);
        mainVL.setComponentAlignment(filterPanel, Alignment.MIDDLE_CENTER);

        mainVL.addComponent(usersGW);
        doFilter(filterPanel.getFilterBean()
        );
    }

    @Override
    public void handleEntityEvent(EntityEvent entityEvent) {

    }

    @Override
    public boolean preCreate(Object o, int i) {
        return super.preCreate(o, i);
    }

    public VerticalLayout getMainVL() {
        return mainVL;
    }

    @Override
    public void doFilter(AbstractFilterBean abstractFilterBean) {
        FUserFilter fUserFilter = (FUserFilter) abstractFilterBean;
        Map<Integer, Object> params = new HashMap<>();
        StringBuilder userSB = new StringBuilder();
        if (fUserFilter.getCode() != null && fUserFilter.getCode().trim().length() >= 1) {
            userSB.append(" and usr.CODE ilike '");
            userSB.append(fUserFilter.getCode().trim());
            userSB.append("%'");
        }
        if (fUserFilter.getFirstname() != null && fUserFilter.getFirstname().trim().length() >= 1) {
            userSB.append(" and usr.FIRST_NAME ilike '");
            userSB.append(fUserFilter.getFirstname().trim());
            userSB.append("%'");
        }
        if (fUserFilter.getLastname() != null && fUserFilter.getLastname().trim().length() >= 1) {
            userSB.append(" and usr.LAST_NAME  ilike '");
            userSB.append(fUserFilter.getLastname().trim());
            userSB.append("%'");
        }

        if (fUserFilter.getUserType()!=null) {
            userSB.append(" and user_type_id = " + fUserFilter.getUserType().getId().getId().intValue()+" ");
        }
        if(fUserFilter.getDate()!=null){
            userSB.append(" and date_trunc('day' , ua.created) = date_trunc('day' , TIMESTAMP '"+CommonUtils.getFormattedDate(fUserFilter.getDate())+"')");
        }
        List<USER_ARRIVAL> list = new ArrayList<>();
        userSB.insert(0, "select ua.* from user_arrival ua inner join  users usr on ua.user_id = usr.id" +
                " where usr.deleted = FALSE and usr.id NOT IN (1,2) and manually_signed=true "
        );

        try {
            List tmpList = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).
                    lookupItemsList(userSB.toString(), params);
            if (!tmpList.isEmpty()) {
                long id = 1;
                for (Object o : tmpList) {
                    Object[] oo = (Object[]) o;
                    USER_ARRIVAL users = new USER_ARRIVAL();
                    users.setId(ID.valueOf((Long) oo[0]));
                    users.setUser(SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(USERS.class,ID.valueOf((Long) oo[1])));
                    users.setCreated((Date)oo[2]);
                    users.setComeIn((Boolean)oo[3]);
                    users.setTurnstileType(SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(TURNSTILE_TYPE.class,ID.valueOf((Long) oo[4])));
                    users.setManuallySigned((Boolean)oo[5]);
                    list.add(users);
                }
            }
        } catch (Exception ex) {
            CommonUtils.showMessageAndWriteLog("Unable to load users' list", ex);
        }

        refresh(list);
    }

    @Override
    public void clearFilter() {

    }

    private void initFilter() throws Exception{
        filterPanel.addFilterPanelListener(this);
        TextField textField = new TextField();
        textField.setNullRepresentation("");
        textField.setNullSettingAllowed(true);
        filterPanel.addFilterComponent("code", textField);

        textField = new TextField();
        textField.setNullRepresentation("");
        textField.setNullSettingAllowed(true);
        filterPanel.addFilterComponent("firstname", textField);

        textField = new TextField();
        textField.setNullRepresentation("");
        textField.setNullSettingAllowed(true);
        filterPanel.addFilterComponent("lastname", textField);

        ComboBox userTypeCB = new ComboBox();
        userTypeCB.setNullSelectionAllowed(false);
        userTypeCB.setTextInputAllowed(false);
        userTypeCB.setFilteringMode(FilteringMode.OFF);
        userTypeCB.setPageLength(0);
        QueryModel<USER_TYPE> typeQM = new QueryModel<>(USER_TYPE.class);
        BeanItemContainer<USER_TYPE> typeBIC = new BeanItemContainer<>(USER_TYPE.class,
                SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(typeQM));
        userTypeCB.setContainerDataSource(typeBIC);
        filterPanel.addFilterComponent("userType", userTypeCB);
        DateField dateField = new DateField();
        filterPanel.addFilterComponent("date",dateField);
    }

    private void refresh(List<USER_ARRIVAL> list) {
        usersGM.setEntities(list);
        try {
            usersGW.refresh();
        } catch (Exception ex) {
            CommonUtils.showMessageAndWriteLog("Unable to refresh users' grid", ex);
        }
    }

}
