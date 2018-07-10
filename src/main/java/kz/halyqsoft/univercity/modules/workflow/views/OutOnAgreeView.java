package kz.halyqsoft.univercity.modules.workflow.views;

import com.vaadin.server.ThemeResource;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import kz.halyqsoft.univercity.entity.beans.USERS;
import kz.halyqsoft.univercity.entity.beans.univercity.DOCUMENT;
import kz.halyqsoft.univercity.entity.beans.univercity.DOCUMENT_SIGNER;
import kz.halyqsoft.univercity.entity.beans.univercity.DOCUMENT_STATUS;
import kz.halyqsoft.univercity.modules.workflow.WorkflowCommonUtils;
import org.r3a.common.entity.ID;
import org.r3a.common.entity.query.QueryModel;
import org.r3a.common.entity.query.where.ECriteria;
import org.r3a.common.vaadin.widget.dialog.AbstractDialog;
import org.r3a.common.vaadin.widget.dialog.Message;
import org.r3a.common.vaadin.widget.grid.GridWidget;
import org.r3a.common.vaadin.widget.grid.model.DBGridModel;
import org.r3a.common.vaadin.widget.toolbar.IconToolbar;

import java.util.ArrayList;
import java.util.List;

public class OutOnAgreeView extends BaseView{

    private USERS currentUser;
    private GridWidget myDocsGW;
    private Button linkedTables;

    public OutOnAgreeView(String title){
        super(title);
    }

    @Override
    public void initView(boolean b) throws Exception {
        super.initView(b);

        linkedTables = new Button(getUILocaleUtil().getCaption("employeesPanel"));
        linkedTables.setIcon(new ThemeResource("img/button/preview.png"));
        linkedTables.setData(12);
        linkedTables.setStyleName("preview");

        linkedTables.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {
                AbstractDialog abstractDialog = new AbstractDialog() {

                    public void init(){
                        setWidth(50, Unit.PERCENTAGE);
                        GridWidget myDocsSignerGW = new GridWidget(DOCUMENT_SIGNER.class);

                        myDocsSignerGW.setSizeFull();
                        myDocsSignerGW.setImmediate(true);

                        DBGridModel dbGridModel = (DBGridModel) myDocsSignerGW.getWidgetModel();
                        dbGridModel.getFormModel().getFieldModel("documentSignerStatus").setInView(true);
                        dbGridModel.getFormModel().getFieldModel("documentSignerStatus").setInEdit(true);

                        dbGridModel.getQueryModel().addWhere("document" , ECriteria.EQUAL , myDocsGW.getSelectedEntity().getId());


                        myDocsSignerGW.setResponsive(true);
                        myDocsSignerGW.setButtonVisible(IconToolbar.ADD_BUTTON , false);
                        myDocsSignerGW.setButtonVisible(IconToolbar.PREVIEW_BUTTON, false);
                        myDocsSignerGW.setButtonVisible(IconToolbar.EDIT_BUTTON, false);


                        getContent().addComponent(myDocsSignerGW);
                        getContent().setComponentAlignment(myDocsSignerGW, Alignment.MIDDLE_CENTER);


                    }

                    @Override
                    protected String createTitle() {
                        init();
                        return getViewName();
                    }
                };
                if(myDocsGW.getSelectedEntity()!=null){
                    abstractDialog.open();
                }else{
                    Message.showError(getUILocaleUtil().getCaption("chooseARecord"));
                }
            }
        });

        currentUser = WorkflowCommonUtils.getCurrentUser();
        myDocsGW = new GridWidget(DOCUMENT.class);
        myDocsGW.setSizeFull();
        myDocsGW.setImmediate(true);
        myDocsGW.setResponsive(true);
        myDocsGW.setButtonVisible(IconToolbar.ADD_BUTTON , false);
        myDocsGW.setButtonVisible(IconToolbar.EDIT_BUTTON, false);

        List<ID> ids = new ArrayList<>();
        ids.add(WorkflowCommonUtils.getDocumentStatusByName(DOCUMENT_STATUS.IN_PROCESS).getId());
        ids.add(WorkflowCommonUtils.getDocumentStatusByName(DOCUMENT_STATUS.CREATED).getId());
        DBGridModel dbGridModel = (DBGridModel) myDocsGW.getWidgetModel();
        QueryModel myDocsQM = dbGridModel.getQueryModel();
        myDocsQM.addWhere("creatorEmployee" , ECriteria.EQUAL , currentUser.getId());
        myDocsQM.addWhereInAnd("documentStatus" ,  ids);

        getContent().addComponent(linkedTables);
        getContent().setComponentAlignment(linkedTables, Alignment.MIDDLE_CENTER);

        getContent().addComponent(myDocsGW);
    }
}
