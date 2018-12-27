package kz.halyqsoft.univercity.modules.workflow.views;

import com.vaadin.server.FontAwesome;
import com.vaadin.server.ThemeResource;
import com.vaadin.ui.*;
import kz.halyqsoft.univercity.entity.beans.USERS;
import kz.halyqsoft.univercity.entity.beans.univercity.DOCUMENT;
import kz.halyqsoft.univercity.entity.beans.univercity.DOCUMENT_SIGNER;
import kz.halyqsoft.univercity.entity.beans.univercity.DOCUMENT_SIGNER_STATUS;
import kz.halyqsoft.univercity.entity.beans.univercity.DOCUMENT_STATUS;
import kz.halyqsoft.univercity.utils.WorkflowCommonUtils;
import kz.halyqsoft.univercity.modules.workflow.views.dialogs.OpenPdfDialog;
import kz.halyqsoft.univercity.modules.workflow.views.dialogs.SignDocumentViewDialog;
import kz.halyqsoft.univercity.utils.CommonUtils;
import org.r3a.common.entity.ID;
import org.r3a.common.entity.query.QueryModel;
import org.r3a.common.entity.query.from.EJoin;
import org.r3a.common.entity.query.from.FromItem;
import org.r3a.common.entity.query.where.ECriteria;
import org.r3a.common.vaadin.widget.dialog.AbstractDialog;
import org.r3a.common.vaadin.widget.dialog.Message;
import org.r3a.common.vaadin.widget.grid.GridWidget;
import org.r3a.common.vaadin.widget.grid.model.DBGridModel;
import org.r3a.common.vaadin.widget.toolbar.IconToolbar;

import java.util.ArrayList;
import java.util.List;

public class InOnSignView extends BaseView {

    private USERS currentUser;
    private GridWidget inOnSignDocsGW;
    private Button linkedTables;

    public InOnSignView(String title){
        super(title);
    }

    @Override
    public void initView(boolean b) throws Exception {
        super.initView(b);
        linkedTables = new Button(getUILocaleUtil().getCaption("employeesPanel"));
        linkedTables.setIcon(new ThemeResource("img/button/users.png"));
        linkedTables.setData(12);
        linkedTables.setStyleName("preview");

        linkedTables.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {
                AbstractDialog abstractDialog = new AbstractDialog() {

                    public void init(){
                        setWidth(50, Unit.PERCENTAGE);
                        GridWidget outMyDocsSignerGW = new GridWidget(DOCUMENT_SIGNER.class);
                        outMyDocsSignerGW.setSizeFull();
                        outMyDocsSignerGW.setImmediate(true);

                        outMyDocsSignerGW.setResponsive(true);
                        outMyDocsSignerGW.setButtonVisible(IconToolbar.ADD_BUTTON , false);
                        outMyDocsSignerGW.setButtonVisible(IconToolbar.PREVIEW_BUTTON, false);
                        outMyDocsSignerGW.setButtonVisible(IconToolbar.EDIT_BUTTON, false);
                        outMyDocsSignerGW.setButtonVisible(IconToolbar.DELETE_BUTTON, false);

                        DBGridModel dbGridModel = (DBGridModel) outMyDocsSignerGW.getWidgetModel();
                        dbGridModel.getFormModel().getFieldModel("documentSignerStatus").setInView(true);

                        dbGridModel.getQueryModel().addWhere("document" , ECriteria.EQUAL , inOnSignDocsGW.getSelectedEntity().getId());

                        getContent().addComponent(outMyDocsSignerGW);

                    }

                    @Override
                    protected String createTitle() {
                        init();
                        return getViewName();
                    }
                };
                if(inOnSignDocsGW.getSelectedEntity()!=null){
                    abstractDialog.open();
                }else{
                    Message.showError(getUILocaleUtil().getCaption("chooseARecord"));
                }
            }
        });

        Button previewBtn = new Button(getUILocaleUtil().getCaption("preview"));
        previewBtn.setIcon(new ThemeResource("img/button/preview.png"));
        previewBtn.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {
                if(inOnSignDocsGW.getSelectedEntity()!=null){
                    OpenPdfDialog openPdfDialog = new OpenPdfDialog((DOCUMENT) inOnSignDocsGW.getSelectedEntity(), InOnSignView.this,700,700);
                    openPdfDialog.addCloseListener(new Window.CloseListener() {
                        @Override
                        public void windowClose(Window.CloseEvent closeEvent) {
                            try{
                                inOnSignDocsGW.refresh();
                            }catch (Exception e){
                                e.printStackTrace();
                            }
                        }
                    });
                }else{
                    Message.showError(getUILocaleUtil().getCaption("chooseARecord"));
                }
            }
        });
        Button signBtn = new Button(getUILocaleUtil().getCaption("signdocument"));
        signBtn.setIcon(FontAwesome.PENCIL);
        signBtn.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {
                if(inOnSignDocsGW.getSelectedEntity()!=null){
                    SignDocumentViewDialog signDocumentViewDialog = new SignDocumentViewDialog( "SIGN", (DOCUMENT) inOnSignDocsGW.getSelectedEntity());
                    signDocumentViewDialog.addCloseListener(new Window.CloseListener() {
                        @Override
                        public void windowClose(Window.CloseEvent closeEvent) {
                            try{
                                inOnSignDocsGW.refresh();
                            }catch (Exception e){
                                e.printStackTrace();
                            }
                        }
                    });
                }else{
                    Message.showError(getUILocaleUtil().getCaption("chooseARecord"));
                }
            }
        });

        currentUser = WorkflowCommonUtils.getCurrentUser();
        inOnSignDocsGW = new GridWidget(DOCUMENT.class);
        inOnSignDocsGW.setSizeFull();

        inOnSignDocsGW.getToolbarPanel().addComponent(signBtn);
        inOnSignDocsGW.getToolbarPanel().addComponent(previewBtn);
        inOnSignDocsGW.getToolbarPanel().addComponent(linkedTables);
        inOnSignDocsGW.getToolbarPanel().setSizeUndefined();
        inOnSignDocsGW.setImmediate(true);
        inOnSignDocsGW.setResponsive(true);
        inOnSignDocsGW.setButtonVisible(IconToolbar.ADD_BUTTON , false);
        inOnSignDocsGW.setButtonVisible(IconToolbar.EDIT_BUTTON, false);
        inOnSignDocsGW.setButtonVisible(IconToolbar.DELETE_BUTTON, false);
        inOnSignDocsGW.setButtonVisible(IconToolbar.PREVIEW_BUTTON, false);

        List<ID> ids = new ArrayList<>();
        ids.add(WorkflowCommonUtils.getDocumentStatusByName(DOCUMENT_STATUS.IN_PROCESS).getId());
        ids.add(WorkflowCommonUtils.getDocumentStatusByName(DOCUMENT_STATUS.CREATED).getId());

        DBGridModel dbGridModel = (DBGridModel) inOnSignDocsGW.getWidgetModel();
        QueryModel inOnSignDocsQM = dbGridModel.getQueryModel();
        FromItem fi = inOnSignDocsQM.addJoin(EJoin.INNER_JOIN, "id", DOCUMENT_SIGNER.class , "document");
        inOnSignDocsQM.addWhereIn("documentStatus" , ids);
        inOnSignDocsQM.addWhereAnd(fi , "employee", ECriteria.EQUAL , CommonUtils.getCurrentUser().getId());
        inOnSignDocsQM.addWhereAnd(fi , "documentSignerStatus", ECriteria.EQUAL, WorkflowCommonUtils.getDocumentSignerStatusByName(DOCUMENT_SIGNER_STATUS.IN_PROCESS).getId());

        getContent().addComponent(inOnSignDocsGW);

    }

    public GridWidget getInOnSignDocsGW() {
        return inOnSignDocsGW;
    }


}
