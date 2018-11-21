package kz.halyqsoft.univercity.modules.userarrival.subview;

import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.server.Sizeable;
import com.vaadin.shared.ui.combobox.FilteringMode;
import com.vaadin.shared.ui.grid.HeightMode;
import com.vaadin.ui.*;
import kz.halyqsoft.univercity.entity.beans.USERS;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.USER_TYPE;
import kz.halyqsoft.univercity.filter.FUserFilter;
import kz.halyqsoft.univercity.filter.panel.UserFilterPanel;
import kz.halyqsoft.univercity.modules.userarrival.subview.dialogs.PrintDialog;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SigningSection extends SampleEntityListener implements FilterPanelListener{

    private UserFilterPanel filterPanel;

    private VerticalLayout mainVL;
    private GridWidget usersGW;
    private DBGridModel usersGM;
    private Button printBtn;
    List <ID> restrictedIDs = new ArrayList();
    public SigningSection(){
        restrictedIDs.add(ID.valueOf(1));
        restrictedIDs.add(ID.valueOf(2));

        filterPanel = new UserFilterPanel(new FUserFilter());
        filterPanel.addFilterPanelListener(this);

        mainVL = new VerticalLayout();
        mainVL.setImmediate(true);
        mainVL.setSizeFull();
        mainVL.setHeight(100, Sizeable.Unit.PERCENTAGE);

        usersGW = new GridWidget(USERS.class);
        usersGW.setImmediate(true);
        usersGW.addEntityListener(this);
        usersGW.showToolbar(false);
        usersGW.setHeight(100, Sizeable.Unit.PERCENTAGE);

        usersGM = (DBGridModel) usersGW.getWidgetModel();
        usersGM.setRowNumberVisible(true);
        usersGM.setRowNumberWidth(80);
        usersGW.setHeight(100, Sizeable.Unit.PERCENTAGE);
        usersGM.setRefreshType(ERefreshType.MANUAL);
        usersGM.getFormModel().getFieldModel("card").setInView(false);
        usersGM.getColumnModel("card").setInGrid(false);
        usersGM.setHeightMode(HeightMode.CSS);

        FUserFilter fUserFilter = (FUserFilter) filterPanel.getFilterBean();
        if (fUserFilter.hasFilter()) {
            doFilter(fUserFilter);
        }
        try{
            initFilter();
        }catch (Exception e){
            e.printStackTrace();
        }

        printBtn = new Button(CommonUtils.getUILocaleUtil().getCaption("export"));
        printBtn.setImmediate(true);
        printBtn.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {
                List<String> tableHeader = new ArrayList<>();
                List<List<String>> tableBody = new ArrayList<>();

                String fileName = "document";

                if (mainVL.getComponentIndex(usersGW) != -1) {
                    for (GridColumnModel gcm : usersGM.getColumnModels()) {
                        tableHeader.add(gcm.getLabel());
                    }
                    for (int i = 0; i < usersGW.getAllEntities().size(); i++) {
                        USERS users = (USERS) usersGW.getAllEntities().get(i);
                        if (usersGW.getCaption() != null) {
                            fileName = usersGW.getCaption();
                        }
                        List<String> list = new ArrayList<>();
                        list.add(users.getLogin());
                        list.add(users.getLastName());
                        list.add(users.getFirstName());
                        if(users.getMiddleName()!=null) {
                            list.add(users.getMiddleName());
                        }else{
                            list.add("");
                        }
                        tableBody.add(list);
                    }
                    PrintDialog printDialog = new PrintDialog(tableHeader, tableBody, CommonUtils.getUILocaleUtil().getCaption("print"), fileName);
                }
            }
        });

        mainVL.addComponent(filterPanel);
        mainVL.setComponentAlignment(filterPanel, Alignment.MIDDLE_CENTER);

        mainVL.addComponent(printBtn);
        mainVL.setComponentAlignment(printBtn, Alignment.MIDDLE_RIGHT);

        mainVL.addComponent(usersGW);
        doFilter(filterPanel.getFilterBean()
        );
    }

    @Override
    public void handleEntityEvent(EntityEvent entityEvent) {
        if(entityEvent.getSource().equals(usersGW)){
            if(entityEvent.getAction()==EntityEvent.SELECTED){
                if(usersGW.getSelectedEntity()!=null){
                    SignDialog signDialog = new SignDialog(CommonUtils.getUILocaleUtil().getCaption("manuallySign"), (USERS)usersGW.getSelectedEntity());
                }
            }
        }
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

        List<USERS> list = new ArrayList<>();
        userSB.insert(0, "select * from users usr where usr.deleted = FALSE and usr.id NOT IN (1,2) ");

        try {
            List tmpList = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).
                    lookupItemsList(userSB.toString(), params);
            if (!tmpList.isEmpty()) {
                long id = 1;
                for (Object o : tmpList) {
                    Object[] oo = (Object[]) o;
                    USERS users = new USERS();
                    users.setId(ID.valueOf((Long) oo[0]));
                    users.setCode((String) oo[13]);
                    users.setLogin((String)oo[14]);
                    users.setFirstName((String)oo[2]);
                    users.setLastName((String)oo[3]);
                    users.setMiddleName((String)oo[4]);
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
    }

    private void refresh(List<USERS> list) {
        usersGM.setEntities(list);
        try {
            usersGW.refresh();
        } catch (Exception ex) {
            CommonUtils.showMessageAndWriteLog("Unable to refresh users' grid", ex);
        }
    }

}
