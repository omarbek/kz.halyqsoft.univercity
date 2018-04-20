package kz.halyqsoft.univercity.modules.creation;

import com.vaadin.ui.*;
import kz.halyqsoft.univercity.entity.beans.ROLES;
import kz.halyqsoft.univercity.entity.beans.USERS;
import kz.halyqsoft.univercity.entity.beans.USER_ROLES;
import kz.halyqsoft.univercity.entity.beans.univercity.view.VEmployee;
import org.r3a.common.dblink.facade.CommonEntityFacadeBean;
import org.r3a.common.dblink.utils.SessionFacadeFactory;
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
import org.r3a.common.vaadin.widget.toolbar.AbstractToolbar;

import java.util.List;

public class UserRoleBindingView extends AbstractTaskView implements EntityListener, FilterPanelListener {

    @Override
    public void doFilter(AbstractFilterBean abstractFilterBean) {

    }

    @Override
    public void clearFilter() {

    }

    private HorizontalLayout horizontalLayout2;

    private VerticalLayout verticalLayout;

    private GridWidget userRoleGW;

    private ComboBox usersComboBox;

    private ComboBox rolesComboBox2;

    private Button bindBtn2;

    public UserRoleBindingView(AbstractTask task) throws Exception {
        super(task);
    }

    @Override
    public void initView(boolean b) throws Exception {

        QueryModel<USERS> usersQueryModel = new QueryModel<USERS>(USERS.class);
        List<USERS> users = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(usersQueryModel);

        QueryModel<ROLES> rolesQueryModel = new QueryModel<ROLES>(ROLES.class);
        List<ROLES> roles = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(rolesQueryModel);

        bindBtn2 = new Button("Bind/connect2");

        verticalLayout = new VerticalLayout();

        horizontalLayout2 = new HorizontalLayout();

        usersComboBox = new ComboBox("select user");
        usersComboBox.setInvalidAllowed(false);
        usersComboBox.addItems(users);

        rolesComboBox2 = new ComboBox("select role");
        rolesComboBox2.setInvalidAllowed(false);
        rolesComboBox2.addItems(roles);

        bindBtn2.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {
                USERS user = (USERS)usersComboBox.getValue();
                ROLES role = (ROLES)rolesComboBox2.getValue();

                System.out.println(user);
                System.out.println(role);

                if(role==null || user == null)
                {
                    Message.showError("Select item please!");
                    return;
                }

                USERS newUser = new USERS();
                newUser.setId(user.getId());

                USER_ROLES user_roles = new USER_ROLES();
                user_roles.setRole(role);
                user_roles.setUser(newUser);

                Message.showConfirm("Insert?", new AbstractYesButtonListener() {
                    @Override
                    public void buttonClick(Button.ClickEvent clickEvent) {
                        try{
                            SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).create(user_roles);
                        }catch (Exception e){
                            e.printStackTrace();
                            Message.showError(e.getMessage());
                        }

                    }
                });
            }
        });


        userRoleGW = new GridWidget(VEmployee.class);
        userRoleGW.addEntityListener(this);
        userRoleGW.setButtonVisible(AbstractToolbar.REFRESH_BUTTON, false);
        userRoleGW.setButtonVisible(AbstractToolbar.PREVIEW_BUTTON, false);
        DBGridModel userRoleGM = (DBGridModel) userRoleGW.getWidgetModel();

        userRoleGM.setTitleVisible(false);
        userRoleGM.setMultiSelect(true);
        userRoleGM.setRefreshType(ERefreshType.MANUAL);



        horizontalLayout2.addComponent(usersComboBox);
        horizontalLayout2.addComponent(rolesComboBox2);
        horizontalLayout2.addComponent(bindBtn2);


        verticalLayout.addComponent(horizontalLayout2);
        verticalLayout.setComponentAlignment(horizontalLayout2, Alignment.MIDDLE_CENTER);

        verticalLayout.addComponent(userRoleGW);
        verticalLayout.setComponentAlignment(userRoleGW , Alignment.MIDDLE_CENTER);

        getContent().addComponent(verticalLayout);
        getContent().setComponentAlignment(verticalLayout, Alignment.MIDDLE_CENTER);

    }
}
