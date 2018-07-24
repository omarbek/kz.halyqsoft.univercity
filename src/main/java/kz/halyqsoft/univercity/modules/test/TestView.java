package kz.halyqsoft.univercity.modules.test;

import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.ui.*;
import kz.halyqsoft.univercity.entity.beans.USERS;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.USER_TYPE;
import org.r3a.common.dblink.facade.CommonEntityFacadeBean;
import org.r3a.common.dblink.utils.SessionFacadeFactory;
import org.r3a.common.entity.beans.AbstractTask;
import org.r3a.common.entity.query.QueryModel;
import org.r3a.common.vaadin.view.AbstractTaskView;
import org.r3a.common.vaadin.widget.dialog.Message;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class TestView extends AbstractTaskView {

    private TextField firstNameTF;
    private TextField surnameTF;
    private ComboBox rolesCB;

    public TestView(AbstractTask task) throws Exception {
        super(task);
    }

    public static void main(String[] args) {
        List<Integer> repeatedIds=new ArrayList<>();
        repeatedIds.add(1);
        repeatedIds.add(2);
        repeatedIds.add(1);
        Set<Integer> ids = new HashSet<>();
        for (Integer repeatedId : repeatedIds) {
            Integer id = getId(ids, repeatedId);
            System.out.println(id);
        }
    }
    private static Integer getId(Set<Integer> ids, Integer id) {
        if (ids.contains(id)) {
            id = getId(ids, ++id);
        } else {
            ids.add(id);
        }
        return id;
    }

    @Override
    public void initView(boolean b) throws Exception {
        firstNameTF = new TextField(getUILocaleUtil().getCaption("firstNameTF"));
        firstNameTF.setImmediate(true);
        firstNameTF.setRequired(true);

        surnameTF = new TextField(getUILocaleUtil().getCaption("surnameTF"));
        surnameTF.setImmediate(true);
        surnameTF.setRequired(true);

        HorizontalLayout mainHL = new HorizontalLayout();
        mainHL.addComponent(firstNameTF);
        mainHL.addComponent(surnameTF);
        getContent().addComponent(mainHL);
        getContent().setComponentAlignment(mainHL, Alignment.MIDDLE_CENTER);

        rolesCB = new ComboBox(getUILocaleUtil().getCaption("rolesCB"));
        rolesCB.setRequired(true);
        QueryModel<USER_TYPE> typeQM = new QueryModel<>(USER_TYPE.class);
        BeanItemContainer<USER_TYPE> typeB = new BeanItemContainer<>(USER_TYPE.class,
                SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(typeQM));
        rolesCB.setContainerDataSource(typeB);

        HorizontalLayout saveHL = new HorizontalLayout();
        saveHL.addComponent(rolesCB);
        getContent().addComponent(saveHL);
        getContent().setComponentAlignment(saveHL, Alignment.MIDDLE_CENTER);

        Button saveBtn = new Button(getUILocaleUtil().getCaption("saveBtn"));

        saveBtn.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {
                String firstName = firstNameTF.getValue();
                String lastName = surnameTF.getValue();
                USER_TYPE userType = (USER_TYPE) rolesCB.getValue();


                if (firstName == null || firstName.isEmpty() || lastName == null || lastName.isEmpty()
                        || userType == null) {
                    Message.showError(getUILocaleUtil().getMessage("error.required.fields"));
                } else {
                    Message.showInfo(getUILocaleUtil().getCaption("firstNameTF") + ":" + firstName
                            + "\n" + getUILocaleUtil().getCaption("surnameTF") + ":" + lastName
                            + "\n" + getUILocaleUtil().getCaption("rolesCB") + ":" + userType);
                }
            }

        });

        Button clearBtn = new Button(getUILocaleUtil().getCaption("clearBtn"));

        clearBtn.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {
                firstNameTF.clear();
                surnameTF.clear();
                rolesCB.clear();
            }

        });


        HorizontalLayout componentHL = new HorizontalLayout();
        componentHL.addComponent(saveBtn);
        componentHL.addComponent(clearBtn);
        getContent().addComponent(componentHL);
        getContent().setComponentAlignment(componentHL, Alignment.MIDDLE_CENTER);

        Button usersToLower=new Button("to Lower");
        usersToLower.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                QueryModel<USERS> usersQM=new QueryModel<>(USERS.class);
//                usersQM.addWhere();
            }
        });
        getContent().addComponent(usersToLower);
    }
}
