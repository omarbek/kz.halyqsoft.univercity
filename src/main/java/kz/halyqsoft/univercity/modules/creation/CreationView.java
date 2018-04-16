package kz.halyqsoft.univercity.modules.creation;

import com.vaadin.ui.*;
import javafx.scene.control.Alert;
import kz.halyqsoft.univercity.entity.beans.*;
import org.r3a.common.dblink.facade.CommonEntityFacadeBean;
import org.r3a.common.dblink.utils.SessionFacadeFactory;
import org.r3a.common.entity.beans.AbstractTask;
import org.r3a.common.entity.query.QueryModel;
import org.r3a.common.vaadin.view.AbstractTaskView;
import org.r3a.common.vaadin.widget.dialog.AbstractYesButtonListener;
import org.r3a.common.vaadin.widget.dialog.Message;

import java.math.BigInteger;
import java.util.List;

public class CreationView extends AbstractTaskView {
    private HorizontalLayout horizontalLayout1;

    private HorizontalLayout horizontalLayout2;

    private VerticalLayout verticalLayout;

    private ComboBox tasksComboBox;

    private ComboBox usersComboBox;

    private ComboBox rolesComboBox1;

    private ComboBox rolesComboBox2;

    private Button bindBtn1;

    private Button bindBtn2;


    public CreationView(AbstractTask task) throws Exception {
        super(task);
    }

    @Override
    public void initView(boolean b) throws Exception {
        QueryModel<TASKS> tasksQueryModel= new QueryModel<TASKS>(TASKS.class);
        List<TASKS> tasks = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(tasksQueryModel);

        QueryModel<USERS> usersQueryModel = new QueryModel<USERS>(USERS.class);
        List<USERS> users = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(usersQueryModel);

        QueryModel<ROLES> rolesQueryModel = new QueryModel<ROLES>(ROLES.class);
        List<ROLES> roles = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(rolesQueryModel);

        bindBtn1 = new Button("Bind/connect1");
        bindBtn2 = new Button("Bind/connect2");

        verticalLayout = new VerticalLayout();

        horizontalLayout1 = new HorizontalLayout();

        horizontalLayout2 = new HorizontalLayout();

        tasksComboBox = new ComboBox("select task");
        tasksComboBox.setInvalidAllowed(false);
        tasksComboBox.addItems(tasks);

        usersComboBox = new ComboBox("select user");
        usersComboBox.setInvalidAllowed(false);
        usersComboBox.addItems(users);

        rolesComboBox1 = new ComboBox("select role");
        rolesComboBox1.setInvalidAllowed(false);
        rolesComboBox1.addItems(roles);

        rolesComboBox2 = new ComboBox("select role");
        rolesComboBox2.setInvalidAllowed(false);
        rolesComboBox2.addItems(roles);

        horizontalLayout1.addComponent(tasksComboBox);

        horizontalLayout1.addComponent(rolesComboBox1);

        horizontalLayout1.addComponent(bindBtn1);

        horizontalLayout2.addComponent(usersComboBox);

        horizontalLayout2.addComponent(rolesComboBox2);

        horizontalLayout2.addComponent(bindBtn2);


        bindBtn1.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {
                TASKS task = (TASKS)tasksComboBox.getValue();
                ROLES role = (ROLES)rolesComboBox1.getValue();

                if(role==null || task == null)
                {
                    Message.showError("Select item please!");
                    return;
                }

                ROLE_TASKS role_tasks = new ROLE_TASKS();
                role_tasks.setRole(role);
                role_tasks.setTask(task);
                role_tasks.setAccessType(true);
                Message.showConfirm("Insert?", new AbstractYesButtonListener() {
                    @Override
                    public void buttonClick(Button.ClickEvent clickEvent) {
                        try{
                            SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).create(role_tasks);
                        }catch (Exception e){
                            e.printStackTrace();
                            Message.showError(e.getMessage());
                        }

                    }
                });
            }
        });

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


        verticalLayout.addComponent(horizontalLayout1);
        verticalLayout.setComponentAlignment(horizontalLayout1, Alignment.MIDDLE_CENTER);

        verticalLayout.addComponent(horizontalLayout2);
        verticalLayout.setComponentAlignment(horizontalLayout2, Alignment.MIDDLE_CENTER);

        getContent().addComponent(verticalLayout);
        getContent().setComponentAlignment(verticalLayout, Alignment.MIDDLE_CENTER);

    }

}
