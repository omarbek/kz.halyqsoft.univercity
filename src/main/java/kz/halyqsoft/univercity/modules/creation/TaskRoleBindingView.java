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

public class TaskRoleBindingView extends AbstractTaskView {
    private HorizontalLayout horizontalLayout1;

    private VerticalLayout verticalLayout;

    private ComboBox tasksComboBox;


    private ComboBox rolesComboBox1;


    private Button bindBtn1;



    public TaskRoleBindingView(AbstractTask task) throws Exception {
        super(task);
    }

    @Override
    public void initView(boolean b) throws Exception {
        QueryModel<TASKS> tasksQueryModel= new QueryModel<TASKS>(TASKS.class);
        List<TASKS> tasks = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(tasksQueryModel);

        QueryModel<ROLES> rolesQueryModel = new QueryModel<ROLES>(ROLES.class);
        List<ROLES> roles = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(rolesQueryModel);

        bindBtn1 = new Button("Bind/connect1");

        verticalLayout = new VerticalLayout();

        horizontalLayout1 = new HorizontalLayout();


        tasksComboBox = new ComboBox("select task");
        tasksComboBox.setInvalidAllowed(false);
        tasksComboBox.addItems(tasks);


        rolesComboBox1 = new ComboBox("select role");
        rolesComboBox1.setInvalidAllowed(false);
        rolesComboBox1.addItems(roles);

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


        horizontalLayout1.addComponent(tasksComboBox);

        horizontalLayout1.addComponent(rolesComboBox1);

        horizontalLayout1.addComponent(bindBtn1);


        verticalLayout.addComponent(horizontalLayout1);
        verticalLayout.setComponentAlignment(horizontalLayout1, Alignment.MIDDLE_CENTER);

        getContent().addComponent(verticalLayout);
        getContent().setComponentAlignment(verticalLayout, Alignment.MIDDLE_CENTER);

    }

}
