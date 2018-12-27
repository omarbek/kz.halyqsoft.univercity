package kz.halyqsoft.univercity.modules.workflow.views;

import com.vaadin.server.FileDownloader;
import com.vaadin.server.Sizeable;
import com.vaadin.server.StreamResource;
import com.vaadin.server.ThemeResource;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Embedded;
import kz.halyqsoft.univercity.entity.beans.USERS;
import kz.halyqsoft.univercity.entity.beans.univercity.DOCUMENT;
import kz.halyqsoft.univercity.entity.beans.univercity.DOCUMENT_SIGNER;
import kz.halyqsoft.univercity.entity.beans.univercity.DOCUMENT_STATUS;
import kz.halyqsoft.univercity.entity.beans.univercity.DOCUMENT_USER_REAL_INPUT;
import kz.halyqsoft.univercity.utils.EmployeePdfCreator;
import kz.halyqsoft.univercity.utils.WorkflowCommonUtils;
import org.r3a.common.dblink.facade.CommonEntityFacadeBean;
import org.r3a.common.dblink.utils.SessionFacadeFactory;
import org.r3a.common.entity.Entity;
import org.r3a.common.entity.ID;
import org.r3a.common.entity.event.EntityEvent;
import org.r3a.common.entity.event.EntityListener;
import org.r3a.common.entity.query.QueryModel;
import org.r3a.common.entity.query.where.ECriteria;
import org.r3a.common.vaadin.AbstractWebUI;
import org.r3a.common.vaadin.widget.dialog.AbstractDialog;
import org.r3a.common.vaadin.widget.dialog.Message;
import org.r3a.common.vaadin.widget.grid.GridWidget;
import org.r3a.common.vaadin.widget.grid.model.DBGridModel;
import org.r3a.common.vaadin.widget.table.TableWidget;
import org.r3a.common.vaadin.widget.table.model.DBTableModel;
import org.r3a.common.vaadin.widget.toolbar.IconToolbar;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class OutOnAgreeView extends BaseView implements EntityListener{

    private USERS currentUser;
    private TableWidget outMyDocsTW;
    private Button linkedTables;

    public OutOnAgreeView(String title){
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

                        dbGridModel.getQueryModel().addWhere("document" , ECriteria.EQUAL , outMyDocsTW.getSelectedEntity().getId());

                        getContent().addComponent(outMyDocsSignerGW);

                    }

                    @Override
                    protected String createTitle() {
                        init();
                        return getViewName();
                    }
                };
                if(outMyDocsTW.getSelectedEntity()!=null){
                    abstractDialog.open();
                }else{
                    Message.showError(getUILocaleUtil().getCaption("chooseARecord"));
                }
            }
        });

        currentUser = WorkflowCommonUtils.getCurrentUser();
        outMyDocsTW = new TableWidget(DOCUMENT.class);
        outMyDocsTW.getToolbarPanel().addComponent(linkedTables);
        outMyDocsTW.getToolbarPanel().setSizeUndefined();
        outMyDocsTW.setSizeFull();
        outMyDocsTW.setImmediate(true);
        outMyDocsTW.setResponsive(true);
        outMyDocsTW.setButtonVisible(IconToolbar.ADD_BUTTON , false);
        outMyDocsTW.setButtonVisible(IconToolbar.EDIT_BUTTON, false);
        outMyDocsTW.addEntityListener(this);


        DBTableModel dbTableModel = (DBTableModel) outMyDocsTW.getWidgetModel();
        dbTableModel.setCollapsed("creatorEmployee" , true);

        dbTableModel.setDeferredDelete(true);
        dbTableModel.getQueryModel().addWhere("creatorEmployee" , ECriteria.EQUAL , currentUser.getId());
        dbTableModel.getQueryModel().addWhereAnd("documentStatus" ,  ECriteria.EQUAL, WorkflowCommonUtils.getDocumentStatusByName(DOCUMENT_STATUS.CREATED).getId());
        getContent().addComponent(outMyDocsTW);
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
        DownloadDialog downloadDialog = new DownloadDialog(document);
        downloadDialog.setImmediate(true);
        downloadDialog.setWidth(60, Unit.PERCENTAGE);
        downloadDialog.setHeight(90, Unit.PERCENTAGE);
        return false;
    }

    private class DownloadDialog extends AbstractDialog{

        private DOCUMENT document;

        public DownloadDialog(DOCUMENT document){
            this.document = document;

            init();

            AbstractWebUI.getInstance().addWindow(this);
        }
        @Override
        protected String createTitle() {

            return getViewName();
        }

        private void init(){
            getContent().setDefaultComponentAlignment(Alignment.MIDDLE_CENTER);
            getContent().setWidth(100, Unit.PERCENTAGE);
            Button downloadBtn = new Button(getUILocaleUtil().getCaption("download"));
            document.getFileByte();
            StreamResource sr = EmployeePdfCreator.getStreamResourceFromByte(document.getFileByte(), document.getPdfDocument().getFileName());
            FileDownloader fileDownloader = new FileDownloader(sr);
            sr.setMIMEType("application/pdf");
            sr.setCacheTime(0);
            fileDownloader.extend(downloadBtn);

            Embedded embedded = new Embedded(getUILocaleUtil().getCaption("download") );
            embedded.setSource(EmployeePdfCreator.getStreamResourceFromByte(document.getFileByte(), "document.pdf"));
            embedded.setImmediate(true);
            embedded.setMimeType("application/pdf");
            embedded.setType(2);
            embedded.setSizeFull();
            embedded.setWidth(600, Sizeable.Unit.PIXELS);
            embedded.setHeight(700, Sizeable.Unit.PIXELS);

            getContent().addComponent(embedded);

            getContent().addComponent(downloadBtn);


        }

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

            List<DOCUMENT_USER_REAL_INPUT> documentUserRealInputList = new ArrayList<>();
            QueryModel<DOCUMENT_USER_REAL_INPUT> documentUserRealInputQM = new QueryModel<>(DOCUMENT_USER_REAL_INPUT.class);
            documentUserRealInputQM.addWhere("document", ECriteria.EQUAL , document.getId());

            try{
                documentSigners.addAll(SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(documentSignerPostQueryModel));
                documentUserRealInputList.addAll(SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(documentUserRealInputQM));

                SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).delete(documentUserRealInputList);
                SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).delete(documentSigners);

                try{
                    EmployeePdfCreator.deleteRelatedDoc((DOCUMENT) document);
                }catch (Exception e){
                    e.printStackTrace();
                }

                SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).delete(document);
                outMyDocsTW.refresh();
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
