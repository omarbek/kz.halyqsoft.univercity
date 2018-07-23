package kz.halyqsoft.univercity.modules.workflow.views;

import com.vaadin.server.FileDownloader;
import com.vaadin.server.StreamResource;
import com.vaadin.server.ThemeResource;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import kz.halyqsoft.univercity.entity.beans.USERS;
import kz.halyqsoft.univercity.entity.beans.univercity.DOCUMENT;
import kz.halyqsoft.univercity.entity.beans.univercity.DOCUMENT_SIGNER;
import kz.halyqsoft.univercity.entity.beans.univercity.EMPLOYEE;
import kz.halyqsoft.univercity.entity.beans.univercity.PDF_DOCUMENT_SIGNER_POST;
import kz.halyqsoft.univercity.modules.regapplicants.ApplicantsForm;
import kz.halyqsoft.univercity.modules.workflow.WorkflowCommonUtils;
import kz.halyqsoft.univercity.modules.workflow.views.utils.EmployeePdfCreator;
import org.r3a.common.dblink.facade.CommonEntityFacadeBean;
import org.r3a.common.dblink.utils.SessionFacadeFactory;
import org.r3a.common.entity.Entity;
import org.r3a.common.entity.event.EntityEvent;
import org.r3a.common.entity.event.EntityListener;
import org.r3a.common.entity.query.QueryModel;
import org.r3a.common.entity.query.where.ECriteria;
import org.r3a.common.vaadin.AbstractWebUI;
import org.r3a.common.vaadin.widget.dialog.AbstractDialog;
import org.r3a.common.vaadin.widget.dialog.Message;
import org.r3a.common.vaadin.widget.grid.GridWidget;
import org.r3a.common.vaadin.widget.grid.model.DBGridModel;
import org.r3a.common.vaadin.widget.toolbar.IconToolbar;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class MyDocumentsView extends BaseView implements EntityListener{
    private USERS currentUser;
    private GridWidget myDocsGW;
    private Button linkedTables;
    public MyDocumentsView(String title){
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

                        myDocsSignerGW.setResponsive(true);
                        myDocsSignerGW.setButtonVisible(IconToolbar.ADD_BUTTON , false);
                        myDocsSignerGW.setButtonVisible(IconToolbar.PREVIEW_BUTTON, false);
                        myDocsSignerGW.setButtonVisible(IconToolbar.EDIT_BUTTON, false);
                        myDocsSignerGW.setButtonVisible(IconToolbar.DELETE_BUTTON, false);

                        DBGridModel dbGridModel = (DBGridModel) myDocsSignerGW.getWidgetModel();
                        dbGridModel.getFormModel().getFieldModel("documentSignerStatus").setInView(true);

                        dbGridModel.getQueryModel().addWhere("document" , ECriteria.EQUAL , myDocsGW.getSelectedEntity().getId());

                        getContent().addComponent(myDocsSignerGW);


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
        myDocsGW.addEntityListener(this);


        DBGridModel dbGridModel = (DBGridModel) myDocsGW.getWidgetModel();
        dbGridModel.setDeferredDelete(true);
        dbGridModel.getQueryModel().addWhere("creatorEmployee" , ECriteria.EQUAL , currentUser.getId());

        getContent().addComponent(linkedTables);
        getContent().setComponentAlignment(linkedTables, Alignment.MIDDLE_CENTER);
        getContent().addComponent(myDocsGW);
    }

    @Override
    public void handleEntityEvent(EntityEvent entityEvent) {

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
        DOCUMENT document = (DOCUMENT)entity;

        AbstractDialog abstractDialog = new AbstractDialog() {
            @Override
            protected String createTitle() {
                init();
                return getViewName();
            }

            private void init(){

                Button downloadBtn = new Button(getUILocaleUtil().getCaption("download"));
                document.getFileByte();
                StreamResource sr = EmployeePdfCreator.getStreamResourceFromByte(document.getFileByte(), document.getPdfDocument().getFileName());
                FileDownloader fileDownloader = new FileDownloader(sr);
                sr.setMIMEType("application/pdf");
                sr.setCacheTime(0);
                fileDownloader.extend(downloadBtn);
                getContent().addComponent(downloadBtn);
            }
        };

        abstractDialog.open();
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
        return true;
    }

    @Override
    public void onDelete(Object o, List<Entity> list, int i) {
        for(Entity document : list){
            List<DOCUMENT_SIGNER> documentSigners = new ArrayList<>();
            QueryModel<DOCUMENT_SIGNER> documentSignerPostQueryModel = new QueryModel<>(DOCUMENT_SIGNER.class);
            documentSignerPostQueryModel.addWhere("document", ECriteria.EQUAL , document.getId());
            try{
                documentSigners.addAll(SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(documentSignerPostQueryModel));
                SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).delete(documentSigners);
                SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).delete(document);
            }catch (Exception e){
                Message.showError(e.getMessage());
                e.printStackTrace();
            }
        }
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
