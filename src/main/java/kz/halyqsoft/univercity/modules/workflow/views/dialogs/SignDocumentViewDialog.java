package kz.halyqsoft.univercity.modules.workflow.views.dialogs;

import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfSignatureAppearance;
import com.itextpdf.text.pdf.PdfStamper;
import com.itextpdf.text.pdf.security.*;
import com.vaadin.data.Property;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.server.Page;
import com.vaadin.ui.*;
import kz.halyqsoft.univercity.entity.beans.univercity.*;
import kz.halyqsoft.univercity.utils.FieldValidator;
import kz.halyqsoft.univercity.utils.WorkflowCommonUtils;
import kz.halyqsoft.univercity.modules.workflow.views.InOnSignView;
import kz.halyqsoft.univercity.utils.CommonUtils;
import org.bouncycastle.openssl.PasswordException;
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
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class SignDocumentViewDialog extends AbstractDialog implements EntityListener{
    private final String title;
    private GridWidget documentSignerGW;
    private DOCUMENT document;

    private File keyFile;
    private static String DEST = "signed_by_%s.pdf";


    public SignDocumentViewDialog( String title, DOCUMENT document){
        this.title = title;
        this.document = document;

        setWidth(80 ,Unit.PERCENTAGE);
        setImmediate(true);
        setResponsive(true);
        setClosable(true);

        VerticalLayout content = new VerticalLayout();
        content.setImmediate(true)
        ;
        QueryModel<DOCUMENT_SIGNER_STATUS> documentSignerStatusQM = new QueryModel<>(DOCUMENT_SIGNER_STATUS.class);
        List<DOCUMENT_SIGNER_STATUS> documentSignerStatusList = new ArrayList<>();
        try{
            documentSignerStatusList.addAll(SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(documentSignerStatusQM));
        }catch (Exception e){
            e.printStackTrace();
        }
        BeanItemContainer<DOCUMENT_SIGNER_STATUS> documentSignerStatusBIC = new BeanItemContainer<DOCUMENT_SIGNER_STATUS>(DOCUMENT_SIGNER_STATUS.class, documentSignerStatusList);

        ComboBox documentSignerStatusCB = new ComboBox();
        documentSignerStatusCB.setCaption(getUILocaleUtil().getEntityLabel(DOCUMENT_SIGNER_STATUS.class));
        documentSignerStatusCB.setContainerDataSource(documentSignerStatusBIC);
        documentSignerStatusCB.addValueChangeListener(new Property.ValueChangeListener() {
            @Override
            public void valueChange(Property.ValueChangeEvent valueChangeEvent) {
                DOCUMENT_SIGNER_STATUS dss = (DOCUMENT_SIGNER_STATUS) valueChangeEvent.getProperty().getValue();
                content.removeAllComponents();
                if(dss.getStatusName().equalsIgnoreCase(DOCUMENT_SIGNER_STATUS.SIGNED)){

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
                    PasswordField passwordField = new PasswordField(getUILocaleUtil().getCaption("enter.password"));
                    passwordField.setRequired(true);
                    passwordField.setWidth(100, Unit.PERCENTAGE);
                    FileUploader receiver = new FileUploader(keyFile);
                    Upload upload = new Upload(getUILocaleUtil().getCaption("upload.certificate"), receiver);

                    upload.addFinishedListener(new Upload.FinishedListener() {
                        @Override
                        public void uploadFinished(Upload.FinishedEvent finishedEvent) {
                            try{
                                document.setFileByte(sign(receiver.getFile(), passwordField.getValue(), document));

                                QueryModel<DOCUMENT_SIGNER> documentSignersQM = new QueryModel<>(DOCUMENT_SIGNER.class);
                                documentSignersQM.addWhere("document", ECriteria.EQUAL , document.getId());
                                List<DOCUMENT_SIGNER> documentSigners = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(documentSignersQM);
                                boolean flag = true;
                                DOCUMENT_STATUS documentStatus = WorkflowCommonUtils.getDocumentStatusByName(DOCUMENT_STATUS.ACCEPTED);
                                DOCUMENT_SIGNER_STATUS documentSignerStatus = WorkflowCommonUtils.getDocumentSignerStatusByName(DOCUMENT_SIGNER_STATUS.SIGNED);
                                if(documentSigners.size()>0){
                                    for(DOCUMENT_SIGNER documentSigner : documentSigners){
                                        if(documentSigner.getDocumentSignerStatus().getId()!=documentSignerStatus.getId()){
                                            flag = false;
                                        }
                                    }
                                }
                                if(flag){
                                    document.setDocumentStatus(documentStatus);
                                }

                                SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).merge(document);

                                QueryModel<DOCUMENT_SIGNER> documentSignerQM = new QueryModel<>(DOCUMENT_SIGNER.class);
                                documentSignerQM.addWhere("document", ECriteria.EQUAL, document.getId());
                                documentSignerQM.addWhereAnd("employee", ECriteria.EQUAL, CommonUtils.getCurrentUser().getId());
                                documentSignerQM.addWhereAnd("documentSignerStatus", ECriteria.NOT_EQUAL, WorkflowCommonUtils.getDocumentSignerStatusByName(DOCUMENT_SIGNER_STATUS.SIGNED).getId());

                                List<DOCUMENT_SIGNER> documentSignerss = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(documentSignerQM);
                                if(documentSignerss.size()>0){
                                    documentSignerss.get(0).setDocumentSignerStatus(WorkflowCommonUtils.getDocumentSignerStatusByName(DOCUMENT_SIGNER_STATUS.SIGNED));
                                    documentSignerss.get(0).setUpdated(new Date());
                                    SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).merge(documentSignerss.get(0));
                                }

                                Message.showInfo(getUILocaleUtil().getCaption("signed"));

                                close();

                            }catch (PasswordException pe){
                                Message.showError(getUILocaleUtil().getMessage("incorrect.password"));
                                pe.printStackTrace();
                            }catch (IOException e){
                                Message.showError(getUILocaleUtil().getMessage("incorrect.password"));
                                Message.showError(e.getMessage());
                                e.printStackTrace();
                            }catch (Exception e){
                                Message.showError(e.getMessage());
                                e.printStackTrace();
                            }
                        }
                    });
                    upload.setImmediate(true);
                    upload.setButtonCaption(getUILocaleUtil().getMessage("start.to.upload"));

                    content.addComponent(passwordField);
                    content.setComponentAlignment(passwordField, Alignment.MIDDLE_CENTER);

                    upload.addSucceededListener(receiver);
                    content.addComponent(upload);
                    content.setComponentAlignment(upload, Alignment.MIDDLE_CENTER);

                }else{
                    content.removeAllComponents();
                   TextArea messageTA = new TextArea();
                   messageTA.setCaption(getUILocaleUtil().getCaption("reason"));
                   messageTA.setRequired(true);
                   messageTA.setImmediate(true);
                   messageTA.setWidth(100, Unit.PERCENTAGE);
                   Button saveBtn = CommonUtils.createSaveButton();
                   saveBtn.addClickListener(new Button.ClickListener() {
                       @Override
                       public void buttonClick(Button.ClickEvent clickEvent) {
                           if(FieldValidator.isNotEmpty(messageTA.getValue())){
                               try{
                                   QueryModel<DOCUMENT_SIGNER> documentSignerQM = new QueryModel<>(DOCUMENT_SIGNER.class);
                                   documentSignerQM.addWhere("document", ECriteria.EQUAL, document.getId());
                                   documentSignerQM.addWhereAnd("employee", ECriteria.EQUAL, CommonUtils.getCurrentUser().getId());
                                   List<DOCUMENT_SIGNER> documentSigners = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(documentSignerQM);
                                   if(documentSigners.size()>0){
                                       documentSigners.get(0).setDocumentSignerStatus(dss);
                                       documentSigners.get(0).setMessage(messageTA.getValue());
                                       documentSigners.get(0).setUpdated(new Date());
                                       SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).merge(documentSigners.get(0));
                                       if(dss.getStatusName().equalsIgnoreCase(DOCUMENT_SIGNER_STATUS.FINALLY_REFUSED))
                                       {
                                           document.setDocumentStatus(WorkflowCommonUtils.getDocumentStatusByName(DOCUMENT_STATUS.FINALLY_REFUSED));

                                       }else {
                                           document.setDocumentStatus(WorkflowCommonUtils.getDocumentStatusByName(DOCUMENT_STATUS.REFUSED_FOR_CORRECTION));
                                       }
                                       document.setUpdated(new Date());
                                       SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).merge(document);
                                       CommonUtils.showSavedNotification();
                                       close();
                                   }
                               }catch (Exception e){
                                   e.printStackTrace();
                               }
                           }else{
                               Message.showError(getUILocaleUtil().getMessage("fill.all.fields"));
                           }
                       }
                   });

                   content.addComponent(messageTA);
                   content.setComponentAlignment(messageTA,Alignment.MIDDLE_CENTER);

                   content.addComponent(saveBtn);
                   content.setComponentAlignment(saveBtn,Alignment.MIDDLE_CENTER);
                }
            }
        });



        getContent().addComponent(documentSignerStatusCB);
        getContent().setComponentAlignment(documentSignerStatusCB, Alignment.MIDDLE_CENTER);

        getContent().addComponent(content);
        getContent().setComponentAlignment(content, Alignment.MIDDLE_CENTER);


        AbstractWebUI.getInstance().addWindow(this);
    }

    public byte[] sign(File keyFile, String password , DOCUMENT document) throws IOException, DocumentException, GeneralSecurityException {

        KeyStore keyStore = KeyStore.getInstance("PKCS12");
        keyStore.load(new FileInputStream(keyFile.getAbsolutePath()) , password.toCharArray());

        String alias = keyStore.aliases().nextElement();
        PrivateKey privateKey = (PrivateKey) keyStore.getKey(alias, password.toCharArray());
        Certificate[] certificateChain = keyStore.getCertificateChain(alias);

        PdfReader reader = new PdfReader(document.getFileByte());
        FileOutputStream os = new FileOutputStream(String.format(DEST,keyFile.getName()));
        PdfStamper stamper = PdfStamper.createSignature(reader,os,'\0');

        PdfSignatureAppearance appearance = stamper.getSignatureAppearance();
        appearance.setCertificate(certificateChain[0]);
        //appearance.setVisibleSignature(new Rectangle(250,50,10,10) , 1 , "Signature1");

        ExternalSignature externalSignature = new PrivateKeySignature(privateKey, "SHA-256", null);
        ExternalDigest externalDigest = new BouncyCastleDigest();
        MakeSignature.signDetached(appearance, externalDigest , externalSignature, certificateChain , null ,null, null, 0 , MakeSignature.CryptoStandard.CMS);

        File newFile = new File(String.format(DEST,keyFile.getName()));
        Path path = Paths.get(String.format(DEST,keyFile.getName()));
        byte[] data = Files.readAllBytes(path);

        if(newFile.exists()){
            newFile.delete();
        }

        return data;
    }

    public String getTitle() {
        return title;
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
