package kz.halyqsoft.univercity.modules.workflow.views.dialogs;

import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.server.Page;
import com.vaadin.server.ThemeResource;
import com.vaadin.ui.*;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import kz.halyqsoft.univercity.entity.beans.univercity.*;
import kz.halyqsoft.univercity.modules.workflow.views.fields.CustomComponentHL;
import kz.halyqsoft.univercity.utils.CommonUtils;
import kz.halyqsoft.univercity.utils.EmployeePdfCreator;
import kz.halyqsoft.univercity.utils.FieldValidator;
import kz.halyqsoft.univercity.utils.OpenPdfDialogUtil;
import org.apache.commons.io.IOUtils;
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
import org.r3a.common.vaadin.widget.form.field.filelist.FileListFieldModel;
import org.r3a.common.vaadin.widget.grid.GridWidget;
import org.r3a.common.vaadin.widget.grid.model.DBGridModel;
import org.r3a.common.vaadin.widget.toolbar.IconToolbar;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

public class CreateViewDialog extends AbstractDialog implements EntityListener {
    private final String title;
    private AbstractCommonView prevView;
    private GridWidget documentSignerGW;
    private DOCUMENT document;
    private ComboBox importanceCB;
    private TextArea messageTA;
    private Upload uploadRelatedFile;
    private File relatedFile;
    private String relatedFilePath;
    private boolean customDocumentFlag = false;
    private List<CustomComponentHL> customComponentHLS = new ArrayList<>();
    private Button emptyRelatedUpload = new Button();
    private Button openPdf;

    public CreateViewDialog(AbstractCommonView prevView, String title, final DOCUMENT document, List<PDF_DOCUMENT_SIGNER_POST> pdfDocumentSignerPosts) {
        getContent().setDefaultComponentAlignment(Alignment.MIDDLE_CENTER);
        this.title = title;
        this.prevView = prevView;
        this.document = document;
        this.setWidth(80.0F, Unit.PERCENTAGE);
        this.setImmediate(true);
        this.setResponsive(true);
        this.setClosable(false);

        HorizontalLayout mainHL = new HorizontalLayout();
        mainHL.setSizeFull();
        mainHL.setSpacing(true);
        mainHL.setImmediate(true);
        mainHL.setDefaultComponentAlignment(Alignment.MIDDLE_CENTER);

        QueryModel<DOCUMENT_IMPORTANCE> importanceQM = new QueryModel(DOCUMENT_IMPORTANCE.class);
        BeanItemContainer importanceBIC = null;

        try {
            importanceBIC = new BeanItemContainer(DOCUMENT_IMPORTANCE.class, SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(importanceQM));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        HorizontalLayout sectionHL = new HorizontalLayout();
        sectionHL.setSizeFull();
        sectionHL.setDefaultComponentAlignment(Alignment.MIDDLE_CENTER);

        VerticalLayout sectionVL = new VerticalLayout();
        sectionVL.setSizeFull();
        this.importanceCB = new ComboBox();
        this.importanceCB.setCaption(this.getUILocaleUtil().getEntityLabel(DOCUMENT_IMPORTANCE.class));
        this.importanceCB.setSizeFull();
        this.importanceCB.setContainerDataSource(importanceBIC);
        sectionVL.addComponent(this.importanceCB);

        this.messageTA = new TextArea(this.getUILocaleUtil().getCaption("message"));
        this.messageTA.setWidth(100, Unit.PERCENTAGE);
        sectionVL.addComponent(this.messageTA);
        sectionHL.addComponent(sectionVL);

        getContent().addComponent(sectionHL);

        Button clearUpload = new Button();
        clearUpload.setCaption(getUILocaleUtil().getCaption("clear"));
        clearUpload.setVisible(false);
        clearUpload.setImmediate(true);
        clearUpload.addClickListener(new ClickListener() {
            @Override
            public void buttonClick(ClickEvent clickEvent) {
                customDocumentFlag = false;
                clearUpload.setVisible(false);
                setInabilityOfCustomComponents(true);
            }
        });

        emptyRelatedUpload.setCaption(getUILocaleUtil().getCaption("empty"));
        emptyRelatedUpload.setVisible(false);
        emptyRelatedUpload.setImmediate(true);
        emptyRelatedUpload.addClickListener(new ClickListener() {
            @Override
            public void buttonClick(ClickEvent clickEvent) {
                emptyRelatedUpload.setVisible(false);
                deleteRelatedFile();
            }
        });

        FileUploader receiver = new FileUploader();

        Upload upload = new Upload(getUILocaleUtil().getMessage("upload.custom"), receiver);

        upload.addSucceededListener(new Upload.SucceededListener() {
            @Override
            public void uploadSucceeded(Upload.SucceededEvent succeededEvent) {
                try{
                    document.setFileByte( IOUtils.toByteArray(new FileInputStream(receiver.getFile())));
                    SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).merge(document);
                    CommonUtils.showSavedNotification();
                    customDocumentFlag = true;
                    clearUpload.setVisible(true);
                    setInabilityOfCustomComponents(false);
                }catch (Exception e){
                    Message.showError(e.getMessage());
                    e.printStackTrace();
                }
            }
        });

        upload.addStartedListener(new Upload.StartedListener() {
            @Override
            public void uploadStarted(Upload.StartedEvent startedEvent) {
                if (!CommonUtils.PDF_MIME_TYPE.equalsIgnoreCase(startedEvent.getMIMEType())) {
                    upload.interruptUpload();
                    String message = getUILocaleUtil().getMessage("error.permittedfiletype");
                    Message.showError(String.format(message, CommonUtils.PDF_MIME_TYPE));
                }
            }
        });

        upload.setImmediate(true);
        upload.setButtonCaption(getUILocaleUtil().getMessage("start.to.upload"));
        upload.addSucceededListener(receiver);


        FileReceiver fr = new FileReceiver();
        uploadRelatedFile = new Upload(getUILocaleUtil().getCaption("attach.related.file") , fr);
        uploadRelatedFile.setButtonCaption(getUILocaleUtil().getMessage("start.to.upload"));
        uploadRelatedFile.setImmediate(true);
        uploadRelatedFile.addSucceededListener(fr);
        uploadRelatedFile.addStartedListener(fr);
        uploadRelatedFile.addFailedListener(fr);


        mainHL.addComponent(uploadRelatedFile);
        mainHL.addComponent(emptyRelatedUpload);

        mainHL.addComponent(upload);
        mainHL.addComponent(clearUpload);

        getContent().addComponent(mainHL);

        QueryModel<DOCUMENT_USER_INPUT> documentUserInputQM = new QueryModel<>(DOCUMENT_USER_INPUT.class);
        documentUserInputQM.addWhere("pdfDocument" ,ECriteria.EQUAL , document.getPdfDocument().getId());
        documentUserInputQM.addOrder("value");

        List<DOCUMENT_USER_INPUT> documentUserInputList = new ArrayList<>();

        try{
                documentUserInputList.addAll(SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(documentUserInputQM));
        }catch (Exception e){
            e.printStackTrace();
        }

        for(DOCUMENT_USER_INPUT documentUserInput : documentUserInputList){
            CustomComponentHL component = new CustomComponentHL(documentUserInput);
            getContent().addComponent(component);
            customComponentHLS.add(component);
        }

        if (pdfDocumentSignerPosts != null) {
            this.documentSignerGW = new GridWidget(DOCUMENT_SIGNER.class);
            this.documentSignerGW.setImmediate(true);
            this.documentSignerGW.setButtonVisible(1, false);
            this.documentSignerGW.setButtonVisible(3, false);
            this.documentSignerGW.setButtonVisible(IconToolbar.PREVIEW_BUTTON, false);
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

                for(CustomComponentHL componentt : customComponentHLS){
                    if(!FieldValidator.isNotEmpty(componentt.getValueTA().getValue())){
                        Message.showError(CreateViewDialog.this.getUILocaleUtil().getMessage("pdf.field.empty"));
                        CreateViewDialog.this.open();
                        break;
                    }
                }

                List<DOCUMENT_USER_REAL_INPUT> documentUserRealInputList = new ArrayList<>();
                for(CustomComponentHL componentt : customComponentHLS){
                    DOCUMENT_USER_REAL_INPUT documentUserRealInput = new DOCUMENT_USER_REAL_INPUT();
                    documentUserRealInput.setCreated(new Date());
                    documentUserRealInput.setDocument(document);
                    documentUserRealInput.setValue(componentt.getValueTA().getValue());
                    documentUserRealInput.setDocumentUserInput(componentt.getDocumentUserInput());
                    documentUserRealInputList.add(documentUserRealInput);
                }

                try{
                    SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).create(documentUserRealInputList);

                }catch (Exception e){
                    e.printStackTrace();
                }

                document.setMessage(CreateViewDialog.this.messageTA.getValue());
                document.setDocumentImportance((DOCUMENT_IMPORTANCE)CreateViewDialog.this.importanceCB.getValue());

                if(!customDocumentFlag){
                    EmployeePdfCreator.createResourceWithReloadingResource(document).getStreamSource().getStream();
                }

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
                        deleteRelatedFile();

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

        openPdf = new Button(getUILocaleUtil().getCaption("open"));
        openPdf.setIcon(new ThemeResource("img/book_open.png"));
        openPdf.addClickListener(new Button.ClickListener(){
            @Override
            public void buttonClick(ClickEvent event) {

                if(!isComponentsValid()){
                    return;
                }

                deleteDocumentUserRealInputs(document);

                saveAll();

                OpenPdfDialogUtil openPdfDialog = new OpenPdfDialogUtil(document, 80,80);
            }
        });
        getContent().addComponent(openPdf);
        getContent().setComponentAlignment(openPdf, Alignment.MIDDLE_LEFT);
    }

    private void setInabilityOfCustomComponents(boolean flag){
        for(CustomComponentHL cc: customComponentHLS){
            cc.getValueTA().setValue("default");
            cc.getValueTA().setEnabled(flag);
        }
    }

    private void saveAll(){
        List<DOCUMENT_USER_REAL_INPUT> documentUserRealInputList = new ArrayList<>();
        DOCUMENT_USER_REAL_INPUT documentUserRealInput = new DOCUMENT_USER_REAL_INPUT();
        for(CustomComponentHL componentt : customComponentHLS){
            documentUserRealInput.setCreated(new Date());
            documentUserRealInput.setDocument(document);
            documentUserRealInput.setValue(componentt.getValueTA().getValue());
            documentUserRealInput.setDocumentUserInput(componentt.getDocumentUserInput());
            documentUserRealInputList.add(documentUserRealInput);

        }

        try{
                SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).create(documentUserRealInputList);

        }catch (Exception e){
            e.printStackTrace();
        }

        document.setMessage(CreateViewDialog.this.messageTA.getValue());
        document.setDocumentImportance((DOCUMENT_IMPORTANCE)CreateViewDialog.this.importanceCB.getValue());
        if(!customDocumentFlag){
            EmployeePdfCreator.createResourceWithReloadingResource(document).getStreamSource().getStream();
        }


        try {
            SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).merge(document);
        } catch (Exception var4) {
            var4.printStackTrace();
        }
    }

    boolean isComponentsValid(){
        for(CustomComponentHL componentt : customComponentHLS){
            if(!FieldValidator.isNotEmpty(componentt.getValueTA().getValue())){
                Message.showError(CreateViewDialog.this.getUILocaleUtil().getMessage("pdf.field.empty"));
                return false;
            }
        }

        if(importanceCB.getValue()==null){
            Message.showError(CreateViewDialog.this.getUILocaleUtil().getMessage("fill.all.fields"));
            return false;
        }

        return true;
    }


    public String getTitle() {
        return this.title;
    }

    public GridWidget getDocumentSignerGW() {
        return this.documentSignerGW;
    }

    class FileUploader implements Upload.Receiver, Upload.SucceededListener {
        private File file;
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


    private class FileReceiver implements Upload.Receiver, Upload.SucceededListener, Upload.FailedListener, Upload.StartedListener {


        @Override
        public void uploadFailed(Upload.FailedEvent ev) {
            relatedFilePath = null;
            CommonUtils.LOG.error("Order files: Unable to upload the file: " + ev.getFilename());
            CommonUtils.LOG.error(String.valueOf(ev.getReason()));
        }

        @Override
        public void uploadSucceeded(Upload.SucceededEvent ev) {
            FileInputStream fis = null;
            try {
                fis = new FileInputStream(relatedFile);
                byte[] bytes = new byte[(int) relatedFile.length()];
                fis.read(bytes);
            } catch (Exception ex) {
                CommonUtils.showMessageAndWriteLog("FileListWidget: Unable to read temp file", ex);
            } finally {
                if (fis != null) {
                    try {
                        fis.close();
                        document.setRelatedDocumentFilePath(relatedFilePath);
                        SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).merge(document);

                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            }

            emptyRelatedUpload.setVisible(true);
            Message.showInfo(getUILocaleUtil().getMessage("file.successfully"));
        }

        @Override
        public OutputStream receiveUpload(String filename, String mimeType) {
            FileOutputStream fos = null;
            try {
                relatedFilePath = "tmp/files/relateddocument/"  + document.getId().getId().longValue()+ "-";
                relatedFilePath+=filename;
                relatedFile = new File(relatedFilePath);

                if(document.getRelatedDocumentFilePath()!=null){
                    File file = new File(document.getRelatedDocumentFilePath());
                    if(file.exists()){
                        file.delete();
                    }
                }

                if (relatedFile.exists()) {
                    relatedFile.delete();
                }
                fos = new FileOutputStream(relatedFile);

            } catch (Exception ex) {
                CommonUtils.showMessageAndWriteLog("Cannot upload file: " + relatedFilePath, ex);
            }

            return fos;
        }

        @Override
        public void uploadStarted(Upload.StartedEvent ev) {
            long size = ev.getContentLength();
            long maxSize = FileListFieldModel.MAX_FILE_SIZE;
            if (size > maxSize) {
                CommonUtils.LOG.error("Trying to upload a big file: Filename = " + ev.getFilename() + ", size = " + size);
                uploadRelatedFile.interruptUpload();
                String message = getUILocaleUtil().getMessage("error.filetoobig");
                Message.showError(String.format(message, maxSize / 1024));
            }
        }
    }

    private void deleteRelatedFile(){
        if(document.getRelatedDocumentFilePath()!=null){
            if(!document.getRelatedDocumentFilePath().equalsIgnoreCase("")){
                File file = new File(document.getRelatedDocumentFilePath());
                if(file.exists()){
                    file.delete();
                }
                document.setRelatedDocumentFilePath(null);
            }
        }
    }

    @Override
    protected String createTitle() {
        return this.title;
    }

    @Override
    public void handleEntityEvent(EntityEvent entityEvent) {
    }

    public void deleteDocumentUserRealInputs(DOCUMENT document){
        QueryModel<DOCUMENT_USER_REAL_INPUT> documentUserRealInputQM = new QueryModel<>(DOCUMENT_USER_REAL_INPUT.
                class);
        documentUserRealInputQM.addWhere("document" , ECriteria.EQUAL , document.getId());
        try{
            CommonUtils.getQuery().delete(CommonUtils.getQuery().lookup(documentUserRealInputQM));
        }catch (Exception e){
            e.printStackTrace();
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
