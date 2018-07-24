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
import kz.halyqsoft.univercity.modules.workflow.WorkflowCommonUtils;
import kz.halyqsoft.univercity.modules.workflow.views.InOnSignView;
import kz.halyqsoft.univercity.utils.WindowUtils;
import org.bouncycastle.openssl.PasswordException;
import org.r3a.common.dblink.facade.CommonEntityFacadeBean;
import org.r3a.common.dblink.utils.SessionFacadeFactory;
import org.r3a.common.entity.Entity;
import org.r3a.common.entity.event.EntityEvent;
import org.r3a.common.entity.event.EntityListener;
import org.r3a.common.entity.query.QueryModel;
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
import java.util.List;

public class SignDocumentViewDialog extends AbstractDialog implements EntityListener{
    private final String title;
    private InOnSignView prevView;
    private GridWidget documentSignerGW;
    private DOCUMENT document;

    private File keyFile;


    private static String FORM = "";
    private static String CERTIFICATE = "";
    private static String DEST = "signed_by_%s.pdf";


    public SignDocumentViewDialog(InOnSignView prevView , String title, DOCUMENT document){
        this.title = title;
        this.prevView = prevView;
        this.document = document;

        setWidth(80 ,Unit.PERCENTAGE);
        setImmediate(true);
        setResponsive(true);
        setClosable(false);


        QueryModel<DOCUMENT_SIGNER_STATUS> documentSignerStatusQM = new QueryModel<>(DOCUMENT_SIGNER_STATUS.class);
        List<DOCUMENT_SIGNER_STATUS> documentSignerStatusList = new ArrayList<>();
        try{
            documentSignerStatusList.addAll(SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(documentSignerStatusQM));
        }catch (Exception e){
            e.printStackTrace();
        }
        BeanItemContainer<DOCUMENT_SIGNER_STATUS> documentSignerStatusBIC = new BeanItemContainer<DOCUMENT_SIGNER_STATUS>(DOCUMENT_SIGNER_STATUS.class, documentSignerStatusList);

        ComboBox documentSignerStatusCB = new ComboBox();
        documentSignerStatusCB.setContainerDataSource(documentSignerStatusBIC);
        documentSignerStatusCB.addValueChangeListener(new Property.ValueChangeListener() {
            @Override
            public void valueChange(Property.ValueChangeEvent valueChangeEvent) {
                DOCUMENT_SIGNER_STATUS dss = (DOCUMENT_SIGNER_STATUS) valueChangeEvent.getProperty().getValue();

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
                    FileUploader receiver = new FileUploader(keyFile);

                    Upload upload = new Upload("Upload certificate here", receiver);
                    upload.addFinishedListener(new Upload.FinishedListener() {
                        @Override
                        public void uploadFinished(Upload.FinishedEvent finishedEvent) {
                            try{
                                document.setFileByte(sign(receiver.getFile(), passwordField.getValue(), document));
                                DOCUMENT_STATUS documentStatus = WorkflowCommonUtils.getDocumentStatusByName(DOCUMENT_STATUS.ACCEPTED);
                                document.setDocumentStatus(documentStatus);
                                SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).merge(document);
                                Message.showInfo(getUILocaleUtil().getCaption("signed"));
                                close();
                                prevView.getInOnSignDocsGW().refresh();
                            }catch (PasswordException pe){
                                Message.showError(pe.toString());
                                pe.printStackTrace();
                            }catch (Exception e){
                                Message.showError(e.getMessage());
                                e.printStackTrace();
                            }
                        }
                    });
                    upload.setImmediate(true);
                    upload.setButtonCaption("Start Upload");

                    getContent().addComponent(passwordField);
                    upload.addSucceededListener(receiver);
                    getContent().addComponent(upload);

                }
            }
        });
        getContent().addComponent(documentSignerStatusCB);

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
        appearance.setVisibleSignature(new Rectangle(250,50,10,10) , 1 , "Signature1");

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
