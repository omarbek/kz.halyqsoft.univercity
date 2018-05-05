package kz.halyqsoft.univercity.modules.creation;

import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.shared.ui.combobox.FilteringMode;
import com.vaadin.ui.*;
import kz.halyqsoft.univercity.entity.beans.ROLES;
import kz.halyqsoft.univercity.entity.beans.USERS;
import kz.halyqsoft.univercity.entity.beans.USER_ROLES;
import kz.halyqsoft.univercity.entity.beans.univercity.view.VUserRoles;
import kz.halyqsoft.univercity.filter.FUserRolesFilter;
import kz.halyqsoft.univercity.filter.panel.UserRolesFilterPanel;
import kz.halyqsoft.univercity.utils.CommonUtils;
import org.r3a.common.dblink.facade.CommonEntityFacadeBean;
import org.r3a.common.dblink.utils.SessionFacadeFactory;
import org.r3a.common.entity.Entity;
import org.r3a.common.entity.ID;
import org.r3a.common.entity.beans.AbstractTask;
import org.r3a.common.entity.event.EntityListener;
import org.r3a.common.entity.query.QueryModel;
import org.r3a.common.vaadin.view.AbstractTaskView;
import org.r3a.common.vaadin.widget.ERefreshType;
import org.r3a.common.vaadin.widget.dialog.AbstractYesButtonListener;
import org.r3a.common.vaadin.widget.dialog.Message;
import org.r3a.common.vaadin.widget.filter2.AbstractFilterBean;
import org.r3a.common.vaadin.widget.filter2.FilterPanelListener;
import org.r3a.common.vaadin.widget.grid.GridWidget;
import org.r3a.common.vaadin.widget.grid.model.DBGridModel;
import org.r3a.common.vaadin.widget.toolbar.IconToolbar;

import java.util.*;

public class UserRoleBindingView extends AbstractTaskView implements EntityListener, FilterPanelListener {

    private UserRolesFilterPanel filterPanel;

    private GridWidget userRoleGW;

    private ComboBox rolesCB;
    private ComboBox usersCB;

    public UserRoleBindingView(AbstractTask task) throws Exception {
        super(task);
        filterPanel = new UserRolesFilterPanel(new FUserRolesFilter());
    }

    @Override
    public void initView(boolean b) throws Exception {

        filterPanel.addFilterPanelListener(this);

        rolesCB = new ComboBox();
        rolesCB.setNullSelectionAllowed(true);
        rolesCB.setTextInputAllowed(false);
        rolesCB.setFilteringMode(FilteringMode.CONTAINS);
        rolesCB.setPageLength(0);
        rolesCB.setWidth(300, Unit.PIXELS);
        QueryModel<ROLES> roleQM = new QueryModel<>(ROLES.class);
        BeanItemContainer<ROLES> roleBIC = new BeanItemContainer<>(ROLES.class,
                SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(roleQM));
        rolesCB.setContainerDataSource(roleBIC);
        filterPanel.addFilterComponent("roleName", rolesCB);


        usersCB = new ComboBox();
//        usersCB.setCaption(getUILocaleUtil().getCaption(""));//TODO
        usersCB.setNullSelectionAllowed(true);
        usersCB.setTextInputAllowed(false);
        usersCB.setFilteringMode(FilteringMode.CONTAINS);
        usersCB.setPageLength(0);
        usersCB.setWidth(220, Unit.PIXELS);
        QueryModel<USERS> userQM = new QueryModel<>(USERS.class);
        BeanItemContainer<USERS> userBIC = new BeanItemContainer<>(USERS.class,
                SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(userQM));
        usersCB.setContainerDataSource(userBIC);

        filterPanel.addFilterComponent("userName", usersCB);
        Button bindButton = new Button(getUILocaleUtil().getCaption("creation.bind"));
        HorizontalLayout componentHL = new HorizontalLayout();

        bindButton.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {

                USERS user = (USERS) usersCB.getValue();
                ROLES role = (ROLES) rolesCB.getValue();

                if(role==null || user == null)
                {
                    Message.showError(getUILocaleUtil().getMessage("error.required.fields"));
                    return;
                }

                USERS newUser = new USERS();
                newUser.setId(user.getId());

                USER_ROLES user_roles = new USER_ROLES();
                user_roles.setRole(role);
                user_roles.setUser(newUser);

                Message.showConfirm(getUILocaleUtil().getMessage("confirmation.save"), new AbstractYesButtonListener() {
                    @Override
                    public void buttonClick(Button.ClickEvent clickEvent) {
                        try{
                            SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).create(user_roles);
                        }catch (Exception e){
                            e.printStackTrace();
                            Message.showError(e.getMessage());
                        }

                        refresh();
                    }
                });
            }
        });
        componentHL.addComponent(bindButton);
        componentHL.setComponentAlignment(bindButton,Alignment.MIDDLE_CENTER);


        getContent().addComponent(filterPanel);
        getContent().setComponentAlignment(filterPanel, Alignment.TOP_CENTER);

        getContent().addComponent(componentHL);
        getContent().setComponentAlignment(componentHL, Alignment.MIDDLE_CENTER);

        userRoleGW = new GridWidget(VUserRoles.class);
        userRoleGW.addEntityListener(this);
        userRoleGW.setButtonVisible(IconToolbar.EDIT_BUTTON , false);
        userRoleGW.setButtonVisible(IconToolbar.ADD_BUTTON , false);
        userRoleGW.setButtonVisible(IconToolbar.PREVIEW_BUTTON, false);
        userRoleGW.setButtonVisible(IconToolbar.REFRESH_BUTTON, false);

        DBGridModel userRoleGM = (DBGridModel) userRoleGW.getWidgetModel();
        userRoleGM.setRowNumberVisible(true);
        userRoleGM.setTitleVisible(false);
        userRoleGM.setMultiSelect(true);
        userRoleGM.setRefreshType(ERefreshType.MANUAL);

        FUserRolesFilter ef = (FUserRolesFilter) filterPanel.getFilterBean();
        if (ef.hasFilter()) {
            doFilter(ef);
        }
        getContent().addComponent(userRoleGW);
        getContent().setComponentAlignment(userRoleGW, Alignment.MIDDLE_CENTER);

    }


    @Override
    public void doFilter(AbstractFilterBean abstractFilterBean) {
        FUserRolesFilter urf = (FUserRolesFilter)abstractFilterBean;
        int i =  1;
        Map<Integer, Object> params = new HashMap<>();
        StringBuilder sb = new StringBuilder();
        if (urf.getRoleName() != null) {
            params.put(i, urf.getRoleName().getId().getId());
            if (sb.length() > 0) {
                sb.append(" and ");
            }
            sb.append("ur.role_id = ?");
            sb.append(i++);
        }
        if (urf.getUserName() != null) {
            params.put(i, urf.getUserName().getId().getId());
            if (sb.length() > 0) {
                sb.append(" and ");
            }
            sb.append("ur.user_id= ?");
            sb.append(i++);
        }

        List<VUserRoles> list = new ArrayList<>();
        if(sb.length()>0){


            String sql = "select ur.id , " +
                    "   trim(u.LAST_NAME || ' ' || u.FIRST_NAME || ' '" +
                                        "|| coalesce(u.MIDDLE_NAME, '')) userName , r.role_name roleName " +
                    "   from user_roles ur " +
                    "   INNER JOIN users u on u.id = ur.user_id " +
                    "   INNER JOIN roles r on r.id = ur.role_id where "
                    + sb.toString();
            try {
                List<Object> tmpList = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookupItemsList(sql , params);
                if (!tmpList.isEmpty()) {
                    for (Object o : tmpList) {
                        Object[] oo = (Object[]) o;
                        VUserRoles ve = new VUserRoles();
                        ve.setId(ID.valueOf((long) oo[0]));
                        ve.setUserName((String) oo[1]);
                        ve.setRoleName((String) oo[2]);
                        list.add(ve);
                    }
                }
            } catch (Exception ex) {
                CommonUtils.showMessageAndWriteLog("Unable to load user_roles list", ex);
            }
        }else {
            Message.showInfo(getUILocaleUtil().getMessage("select.1.search.condition"));
        }

        ((DBGridModel)userRoleGW.getWidgetModel()).setEntities(list);
        try {
            userRoleGW.refresh();
        } catch (Exception ex) {
            CommonUtils.showMessageAndWriteLog("Unable to refresh user_roles grid", ex);
        }
    }

    @Override
    public void clearFilter() {
        ((DBGridModel) userRoleGW.getWidgetModel()).setEntities(new ArrayList<>(1));
        try {
            userRoleGW.refresh();
        } catch (Exception ex) {
            CommonUtils.showMessageAndWriteLog("Unable to refresh user_role grid", ex);
        }
    }

    @Override
    public boolean preDelete(Object source, List<Entity> entities, int buttonId) {
        if(source.equals(userRoleGW)){
            for(Entity entity:entities){
                try{
                    USER_ROLES userRoles= SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(USER_ROLES.class , entity.getId());
                    SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).delete(userRoles);
                    userRoleGW.refresh();
                }catch (Exception e){
                    Message.showError(e.getMessage());
                }
            }
            refresh();
        }
        return false;
    }

    private void refresh(){
        try {
            userRoleGW.refresh();
            doFilter(filterPanel.getFilterBean());
        } catch (Exception e){
            Message.showError(e.getMessage());
        }
    }

}
