package kz.halyqsoft.univercity.modules.workflow.views.dialogs;

import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import kz.halyqsoft.univercity.entity.beans.USERS;
import kz.halyqsoft.univercity.entity.beans.univercity.DOCUMENT_SIGNER;
import kz.halyqsoft.univercity.entity.beans.univercity.EMPLOYEE;
import kz.halyqsoft.univercity.entity.beans.univercity.EMPLOYEE_DEPT;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.POST;
import kz.halyqsoft.univercity.utils.CommonUtils;
import org.r3a.common.dblink.facade.CommonEntityFacadeBean;
import org.r3a.common.dblink.utils.SessionFacadeFactory;
import org.r3a.common.entity.Entity;
import org.r3a.common.entity.query.QueryModel;
import org.r3a.common.entity.query.from.EJoin;
import org.r3a.common.entity.query.from.FromItem;
import org.r3a.common.entity.query.where.ECriteria;
import org.r3a.common.vaadin.AbstractWebUI;
import org.r3a.common.vaadin.widget.dialog.AbstractDialog;
import org.r3a.common.vaadin.widget.dialog.Message;
import org.r3a.common.vaadin.widget.grid.GridWidget;

import java.util.ArrayList;
import java.util.List;

public class AddEmployeeDialog extends AbstractDialog{

    private String title;
    public AddEmployeeDialog(CreateViewDialog createViewDialog, String title, Entity entity) throws Exception{
        setModal(true);
        setWidth(30, Unit.PERCENTAGE);

        this.title = title;
        DOCUMENT_SIGNER documentSigner = (DOCUMENT_SIGNER) entity;

        QueryModel<USERS> usersQM = new QueryModel<>(USERS.class);
        FromItem fromItem = usersQM.addJoin(EJoin.INNER_JOIN , "id" , EMPLOYEE.class , "id");
        FromItem fi = fromItem.addJoin(EJoin.INNER_JOIN, "id" , EMPLOYEE_DEPT.class , "employee");
        usersQM.addWhere(fi , "post" , ECriteria.EQUAL , documentSigner.getPost().getId());

        BeanItemContainer<USERS> usersBIC= new BeanItemContainer<>(USERS.class,SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(usersQM));

        ComboBox usersCB = new ComboBox();
        usersCB.setCaption(getUILocaleUtil().getCaption("employeesPanel"));
        usersCB.setSizeFull();
        usersCB.setContainerDataSource(usersBIC);
        getContent().addComponent(usersCB);
        Button saveButton = CommonUtils.createSaveButton();
        saveButton.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {
                if(usersCB.getValue()!=null){
                    documentSigner.setEmployee((EMPLOYEE) usersCB.getValue());
                    try{
                        SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).merge(documentSigner);
                        CommonUtils.showSavedNotification();
                        createViewDialog.getDocumentSignerGW().refresh();
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }else{
                    Message.showError(getUILocaleUtil().getCaption("chooseARecord"));
                }
            }
        });
        getContent().addComponent(saveButton);
        AbstractWebUI.getInstance().addWindow(this);

    }

    @Override
    protected String createTitle() {
        return title;
    }
}
