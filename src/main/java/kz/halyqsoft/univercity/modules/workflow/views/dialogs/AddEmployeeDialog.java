package kz.halyqsoft.univercity.modules.workflow.views.dialogs;

import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import kz.halyqsoft.univercity.entity.beans.USERS;
import kz.halyqsoft.univercity.entity.beans.univercity.*;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.POST;
import kz.halyqsoft.univercity.utils.CommonUtils;
import kz.halyqsoft.univercity.utils.WorkflowCommonUtils;
import org.r3a.common.dblink.facade.CommonEntityFacadeBean;
import org.r3a.common.dblink.utils.SessionFacadeFactory;
import org.r3a.common.entity.Entity;
import org.r3a.common.entity.ID;
import org.r3a.common.entity.query.QueryModel;
import org.r3a.common.entity.query.from.EJoin;
import org.r3a.common.entity.query.from.FromItem;
import org.r3a.common.entity.query.where.ECriteria;
import org.r3a.common.vaadin.AbstractWebUI;
import org.r3a.common.vaadin.widget.dialog.AbstractDialog;
import org.r3a.common.vaadin.widget.dialog.Message;
import org.r3a.common.vaadin.widget.grid.GridWidget;

import java.util.*;

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
                    EMPLOYEE employee = (EMPLOYEE) usersCB.getValue();
                    documentSigner.setEmployee(employee);
                    try{
                        documentSigner.setDocumentSignerStatus(WorkflowCommonUtils.getDocumentSignerStatusByName(DOCUMENT_SIGNER_STATUS.CREATED));

                        List<SUBSTITUTION> substitutions = getList(employee);
                        if(substitutions.size()>0){
                            documentSigner.setEmployee((EMPLOYEE) substitutions.get(0).getSubstitutor());
                            Message.showInfo(getUILocaleUtil().getMessage("unavailable.employee"));

                        }
                        documentSigner.setUpdated(new Date());
                        SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).merge(documentSigner);
                        CommonUtils.showSavedNotification();
                        createViewDialog.getDocumentSignerGW().refresh();
                        close();
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }else{
                    Message.showError(getUILocaleUtil().getCaption("chooseARecord"));
                }
            }
        });
        getContent().addComponent(saveButton);
        open();

    }

    @Override
    protected String createTitle() {
        return title;
    }


    public List<SUBSTITUTION> getList(EMPLOYEE employee) {

        List<SUBSTITUTION> list = new ArrayList<>();
        Map<Integer, Object> params = new HashMap<>();

        String sql = "SELECT id FROM substitution where employee_id = "+ employee.getId().getId().longValue() +" and until_date > now();" ;

        try {
            List<Object> tmpList = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookupItemsList(sql, params);
            if (!tmpList.isEmpty()) {
                for (Object o : tmpList) {
                    ID id = ID.valueOf((Long)o);
                    try {
                        SUBSTITUTION substitution = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(SUBSTITUTION.class, id);
                        list.add(substitution);
                    }catch(Exception e){
                        e.printStackTrace();
                    }
                }
            }
        } catch (Exception ex) {
            CommonUtils.showMessageAndWriteLog("Unable to load teacher list", ex);
        }

        return list;
    }
}
