package kz.halyqsoft.univercity.modules.workflow.views;

import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalSplitPanel;
import com.vaadin.ui.VerticalLayout;
import kz.halyqsoft.univercity.entity.beans.USERS;
import kz.halyqsoft.univercity.entity.beans.univercity.*;
import kz.halyqsoft.univercity.modules.workflow.WorkflowCommonUtils;
import kz.halyqsoft.univercity.modules.workflow.views.dialogs.CreateViewDialog;
import kz.halyqsoft.univercity.utils.CommonUtils;
import org.r3a.common.dblink.facade.CommonEntityFacadeBean;
import org.r3a.common.dblink.utils.SessionFacadeFactory;
import org.r3a.common.entity.Entity;
import org.r3a.common.entity.event.EntityEvent;
import org.r3a.common.entity.event.EntityListener;
import org.r3a.common.entity.query.QueryModel;
import org.r3a.common.entity.query.where.ECriteria;
import org.r3a.common.vaadin.AbstractSecureWebUI;
import org.r3a.common.vaadin.AbstractWebUI;
import org.r3a.common.vaadin.widget.dialog.Message;
import org.r3a.common.vaadin.widget.grid.GridWidget;
import org.r3a.common.vaadin.widget.grid.model.DBGridModel;
import org.r3a.common.vaadin.widget.toolbar.IconToolbar;

import javax.persistence.NoResultException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class CreateView extends BaseView implements EntityListener{

    private HorizontalSplitPanel mainHSP;

    private VerticalLayout firstVL;
    private VerticalLayout secondVL;

    private GridWidget pdfDocumentGW;
    private GridWidget pdfDocSignerPostGW;
    private Button btnCreate;

    public CreateView(String title){
        super(title);
    }

    @Override
    public void initView(boolean b) throws Exception {
        super.initView(b);
        mainHSP = new HorizontalSplitPanel();
        mainHSP.setSplitPosition(72);
        mainHSP.setSizeFull();
        mainHSP.setResponsive(true);
        mainHSP.setImmediate(true);

        firstVL = new VerticalLayout();
        firstVL.setImmediate(true);
        firstVL.setSizeFull();
        secondVL = new VerticalLayout();
        secondVL.setImmediate(true);
        secondVL.setSizeFull();

        pdfDocumentGW = new GridWidget(PDF_DOCUMENT.class);
        pdfDocumentGW.addEntityListener(this);
        pdfDocumentGW.setImmediate(true);
        pdfDocumentGW.setButtonVisible(IconToolbar.PREVIEW_BUTTON, false);
        pdfDocumentGW.setButtonVisible(IconToolbar.EDIT_BUTTON, false);
        pdfDocumentGW.setButtonVisible(IconToolbar.DELETE_BUTTON, false);
        pdfDocumentGW.setButtonVisible(IconToolbar.ADD_BUTTON, false);

        DBGridModel pdfDocumentGM = (DBGridModel) pdfDocumentGW.getWidgetModel();
        pdfDocumentGM.getQueryModel().addWhere("deleted" , ECriteria.EQUAL , false);
        btnCreate = new Button(getUILocaleUtil().getCaption("create"));
        btnCreate.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {
                PDF_DOCUMENT pdfDocument= (PDF_DOCUMENT) pdfDocumentGW.getSelectedEntity();
                if(pdfDocument!=null){
                    USERS user = WorkflowCommonUtils.getCurrentUser();
                    DOCUMENT_STATUS documentStatus = WorkflowCommonUtils.getDocumentStatusByName(DOCUMENT_STATUS.CREATED);
                    DOCUMENT_IMPORTANCE documentImportance = WorkflowCommonUtils.getDocumentImportanceByValue(DOCUMENT_IMPORTANCE.IMPORTANCE_1);
                    DOCUMENT document = new DOCUMENT();

                    Calendar deadlineDate = Calendar.getInstance();
                    deadlineDate.setTime(new Date());
                    deadlineDate.add(Calendar.DATE , pdfDocument.getPeriod());

                    document.setCreated(new Date());
                    document.setCreatorEmployee((EMPLOYEE) user);
                    document.setDocumentImportance(documentImportance);
                    document.setPdfDocument(pdfDocument);
                    document.setDocumentStatus(documentStatus);
                    document.setDeadlineDate(deadlineDate.getTime());
                    document.setDeleted(false);

                    try{
                        SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).create(document);
                    }catch (Exception e)
                    {
                        e.printStackTrace();
                        Message.showError(e.getMessage());
                    }
                    List<PDF_DOCUMENT_SIGNER_POST> pdfDocumentSignerPosts = null;

                    if(document.getId()!=null){

                        try{
                            QueryModel<PDF_DOCUMENT_SIGNER_POST> pdfDocumentSignerPostQM = new QueryModel<>(PDF_DOCUMENT_SIGNER_POST.class);
                            pdfDocumentSignerPostQM.addWhere("pdfDocument" ,ECriteria.EQUAL , document.getPdfDocument().getId());
                            pdfDocumentSignerPosts = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(pdfDocumentSignerPostQM);
                            if(pdfDocumentSignerPosts.size()>0){
                                for(PDF_DOCUMENT_SIGNER_POST pdfDocumentSignerPost : pdfDocumentSignerPosts){
                                    DOCUMENT_SIGNER documentSigner = new DOCUMENT_SIGNER();
                                    documentSigner.setCreated(new Date());
                                    documentSigner.setDeleted(false);
                                    documentSigner.setPost(pdfDocumentSignerPost.getPost());
                                    documentSigner.setDocument(document);
                                    DOCUMENT_SIGNER_STATUS documentSignerStatus = WorkflowCommonUtils.getDocumentSignerStatusByName(DOCUMENT_SIGNER_STATUS.CREATED);
                                    documentSigner.setDocumentSignerStatus(documentSignerStatus);
                                    SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).create(documentSigner);
                                }
                            }
                        }catch (Exception e){
                            e.printStackTrace();
                        }

                        CommonUtils.showSavedNotification();
                        CreateViewDialog createViewDialog = new CreateViewDialog(CreateView.this , getViewName(), document, pdfDocumentSignerPosts);
                        createViewDialog.open();
                    }

                }else{
                    Message.showError(getUILocaleUtil().getMessage("choose.document"));
                }
            }
        });
        firstVL.addComponent(btnCreate);
        firstVL.setComponentAlignment(btnCreate, Alignment.MIDDLE_CENTER);

        firstVL.addComponent(pdfDocumentGW);

        mainHSP.setFirstComponent(firstVL);
        mainHSP.setSecondComponent(secondVL);
        getContent().addComponent(mainHSP);
    }

    @Override
    public void handleEntityEvent(EntityEvent entityEvent) {
        if(entityEvent.getSource().equals(pdfDocumentGW)) {
            PDF_DOCUMENT pdfDocument = (PDF_DOCUMENT) pdfDocumentGW.getSelectedEntity();
            secondVL.removeAllComponents();
            if(pdfDocument!=null){

                pdfDocSignerPostGW = new GridWidget(PDF_DOCUMENT_SIGNER_POST.class);
                DBGridModel pdfDocSignerPostGM = (DBGridModel) pdfDocSignerPostGW.getWidgetModel();
                pdfDocSignerPostGM.getQueryModel().addWhere("pdfDocument", ECriteria.EQUAL , pdfDocument.getId());
                pdfDocSignerPostGW.setButtonVisible(IconToolbar.PREVIEW_BUTTON, false);
                pdfDocSignerPostGW.setButtonVisible(IconToolbar.EDIT_BUTTON, false);
                pdfDocSignerPostGW.setButtonVisible(IconToolbar.DELETE_BUTTON, false);
                pdfDocSignerPostGW.setButtonVisible(IconToolbar.ADD_BUTTON, false);

                secondVL.addComponent(pdfDocSignerPostGW);
            }
        }
    }

    @Override
    public boolean preCreate(Object o, int i) {
        return false;
    }

    @Override
    public void onCreate(Object o, Entity entity, int i) {

    }

    @Override
    public boolean onEdit(Object o, Entity entity, int i) {
        return false;
    }

    @Override
    public boolean onPreview(Object o, Entity entity, int i) {
        return false;
    }

    @Override
    public void beforeRefresh(Object o, int i) {

    }

    @Override
    public void onRefresh(Object o, List<Entity> list) {

    }

    @Override
    public void onFilter(Object o, QueryModel queryModel, int i) {

    }

    @Override
    public void onAccept(Object o, List<Entity> list, int i) {

    }

    @Override
    public boolean preSave(Object o, Entity entity, boolean b, int i) throws Exception {
        return false;
    }

    @Override
    public boolean preDelete(Object o, List<Entity> list, int i) {
        return false;
    }

    @Override
    public void onDelete(Object o, List<Entity> list, int i) {

    }

    @Override
    public void deferredCreate(Object o, Entity entity) {

    }

    @Override
    public void deferredDelete(Object o, List<Entity> list) {

    }

    @Override
    public void onException(Object o, Throwable throwable) {

    }
}
