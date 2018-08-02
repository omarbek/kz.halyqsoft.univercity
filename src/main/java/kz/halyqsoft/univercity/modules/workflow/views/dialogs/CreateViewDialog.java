package kz.halyqsoft.univercity.modules.workflow.views.dialogs;

import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.server.Page;
import com.vaadin.ui.*;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;

import java.io.*;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import kz.halyqsoft.univercity.entity.beans.univercity.*;
import kz.halyqsoft.univercity.utils.CommonUtils;
import kz.halyqsoft.univercity.utils.WorkflowCommonUtils;
import org.apache.commons.io.IOUtils;
import org.bouncycastle.openssl.PasswordException;
import org.r3a.common.dblink.facade.CommonEntityFacadeBean;
import org.r3a.common.dblink.utils.SessionFacadeFactory;
import org.r3a.common.entity.Entity;
import org.r3a.common.entity.event.EntityEvent;
import org.r3a.common.entity.event.EntityListener;
import org.r3a.common.entity.query.QueryModel;
import org.r3a.common.entity.query.where.ECriteria;
import org.r3a.common.vaadin.view.AbstractCommonView;
import org.r3a.common.vaadin.widget.dialog.AbstractDialog;
import org.r3a.common.vaadin.widget.dialog.AbstractYesButtonListener;
import org.r3a.common.vaadin.widget.dialog.Message;
import org.r3a.common.vaadin.widget.grid.GridWidget;
import org.r3a.common.vaadin.widget.grid.model.DBGridModel;

public class CreateViewDialog extends AbstractDialog implements EntityListener {
    private final String title;
    private AbstractCommonView prevView;
    private GridWidget documentSignerGW;
    private DOCUMENT document;
    private ComboBox importanceCB;
    private TextArea messageTA;
    private File customFile;

    public CreateViewDialog(AbstractCommonView prevView, String title, final DOCUMENT document, List<PDF_DOCUMENT_SIGNER_POST> pdfDocumentSignerPosts) {
        this.title = title;
        this.prevView = prevView;
        this.document = document;
        this.setWidth(80.0F, Unit.PERCENTAGE);
        this.setImmediate(true);
        this.setResponsive(true);
        this.setClosable(false);
        QueryModel<DOCUMENT_IMPORTANCE> importanceQM = new QueryModel(DOCUMENT_IMPORTANCE.class);
        BeanItemContainer importanceBIC = null;

        try {
            importanceBIC = new BeanItemContainer(DOCUMENT_IMPORTANCE.class, SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(importanceQM));
        } catch (Exception ex) {
            ex.printStackTrace();
        }


        this.importanceCB = new ComboBox();
        this.importanceCB.setCaption(this.getUILocaleUtil().getEntityLabel(DOCUMENT_IMPORTANCE.class));
        this.importanceCB.setSizeFull();
        this.importanceCB.setContainerDataSource(importanceBIC);
        this.getContent().addComponent(this.importanceCB);

        this.messageTA = new TextArea(this.getUILocaleUtil().getCaption("message"));
        this.messageTA.setWidth(100, Unit.PERCENTAGE);
        this.getContent().addComponent(this.messageTA);

        FileUploader receiver = new FileUploader(customFile);
        Upload upload = new Upload(getUILocaleUtil().getMessage("upload.custom"), receiver);

        upload.addFinishedListener(new Upload.FinishedListener() {
            @Override
            public void uploadFinished(Upload.FinishedEvent finishedEvent) {
                try{
                    document.setFileByte( IOUtils.toByteArray(new FileInputStream(receiver.getFile())));
                    SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).merge(document);
                    CommonUtils.showSavedNotification();
                }catch (Exception e){
                    Message.showError(e.getMessage());
                    e.printStackTrace();
                }
            }
        });
        upload.setImmediate(true);
        upload.setButtonCaption(getUILocaleUtil().getMessage("start.to.upload"));

        upload.addSucceededListener(receiver);
        this.getContent().addComponent(upload);
        this.getContent().setComponentAlignment(upload, Alignment.MIDDLE_CENTER);



        if (pdfDocumentSignerPosts != null) {
            this.documentSignerGW = new GridWidget(DOCUMENT_SIGNER.class);
            this.documentSignerGW.setImmediate(true);
            this.documentSignerGW.setButtonVisible(1, false);
            this.documentSignerGW.setButtonVisible(3, false);
            this.documentSignerGW.addEntityListener(this);
            DBGridModel dbGridModel = (DBGridModel)this.documentSignerGW.getWidgetModel();
            dbGridModel.getQueryModel().addWhere("document", ECriteria.EQUAL, document.getId());
            this.getContent().addComponent(this.documentSignerGW);
        }

        this.setYesListener(new AbstractYesButtonListener() {
            @Override
            public void buttonClick(ClickEvent clickEvent) {
                Iterator var2 = CreateViewDialog.this.documentSignerGW.getAllEntities().iterator();

                while(var2.hasNext()) {
                    Entity ds = (Entity)var2.next();
                    if (((DOCUMENT_SIGNER)ds).getEmployee() == null || CreateViewDialog.this.importanceCB.getValue() == null) {
                        Message.showError(CreateViewDialog.this.getUILocaleUtil().getMessage("pdf.field.empty"));
                        CreateViewDialog.this.open();
                        break;
                    }
                }

                document.setMessage(CreateViewDialog.this.messageTA.getValue());
                document.setDocumentImportance((DOCUMENT_IMPORTANCE)CreateViewDialog.this.importanceCB.getValue());

                try {
                    SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).merge(document);
                } catch (Exception var4) {
                    var4.printStackTrace();
                }

                CreateViewDialog.this.close();
            }
        });
        this.setNoListener(new ClickListener() {
            @Override
            public void buttonClick(ClickEvent clickEvent) {
                Message.showConfirm(CreateViewDialog.this.getUILocaleUtil().getMessage("all.values.deleted"), new AbstractYesButtonListener() {
                    @Override
                    public void buttonClick(ClickEvent clickEvent) {
                        try {
                            SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).delete(CreateViewDialog.this.documentSignerGW.getAllEntities());
                        } catch (Exception var4) {
                            var4.printStackTrace();
                        }

                        try {
                            SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).delete(document);
                        } catch (Exception var3) {
                            var3.printStackTrace();
                        }

                        CreateViewDialog.this.close();
                    }
                });
                CreateViewDialog.this.open();
            }
        });
    }

    public String getTitle() {
        return this.title;
    }

    public GridWidget getDocumentSignerGW() {
        return this.documentSignerGW;
    }

    class FileUploader implements Upload.Receiver, Upload.SucceededListener {
        private File file;

        public FileUploader(File file){
            this.file = file;
        }

        @Override
        public OutputStream receiveUpload(String filename, String mimeType) {
            FileOutputStream fos = null; // Stream to write to
            try {
                file = new File(filename);
                fos = new FileOutputStream(file);

            } catch (final java.io.FileNotFoundException e) {
                new Notification("Could not open file",
                        e.getMessage(),
                        Notification.Type.ERROR_MESSAGE)
                        .show(Page.getCurrent());
                return null;
            }
            return fos;
        }

        @Override
        public void uploadSucceeded(Upload.SucceededEvent event) {

        }

        public File getFile() {
            return file;
        }
    }


    @Override
    protected String createTitle() {
        return this.title;
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
        Object object = null;

        try {
            new AddEmployeeDialog(this, this.prevView.getViewName(), entity);
            this.documentSignerGW.refresh();
        } catch (Exception e) {
            e.printStackTrace();
        }

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
