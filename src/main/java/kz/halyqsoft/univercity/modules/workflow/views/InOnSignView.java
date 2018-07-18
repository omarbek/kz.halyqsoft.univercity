package kz.halyqsoft.univercity.modules.workflow.views;

import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfSignatureAppearance;
import com.itextpdf.text.pdf.PdfStamper;
import com.itextpdf.text.pdf.security.*;
import com.vaadin.server.ThemeResource;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import kz.halyqsoft.univercity.entity.beans.USERS;
import kz.halyqsoft.univercity.entity.beans.univercity.DOCUMENT;
import kz.halyqsoft.univercity.entity.beans.univercity.DOCUMENT_SIGNER;
import kz.halyqsoft.univercity.entity.beans.univercity.DOCUMENT_STATUS;
import kz.halyqsoft.univercity.modules.workflow.WorkflowCommonUtils;
import org.r3a.common.entity.Entity;
import org.r3a.common.entity.ID;
import org.r3a.common.entity.event.EntityEvent;
import org.r3a.common.entity.event.EntityListener;
import org.r3a.common.entity.query.QueryModel;
import org.r3a.common.entity.query.from.EJoin;
import org.r3a.common.entity.query.from.FromItem;
import org.r3a.common.entity.query.where.ECriteria;
import org.r3a.common.vaadin.widget.dialog.AbstractDialog;
import org.r3a.common.vaadin.widget.dialog.Message;
import org.r3a.common.vaadin.widget.grid.GridWidget;
import org.r3a.common.vaadin.widget.grid.model.DBGridModel;
import org.r3a.common.vaadin.widget.toolbar.IconToolbar;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.util.ArrayList;
import java.util.List;

public class InOnSignView extends BaseView implements EntityListener{

    private USERS currentUser;
    private GridWidget myDocsGW;

    private static String FORM = "";
    private static String CERTIFICATE = "";
    private static char[] PASSWORD ;
    private static String DEST = "signed_by_%s.pdf";

    public InOnSignView(String title){
        super(title);
    }

    @Override
    public void initView(boolean b) throws Exception {
        super.initView(b);

        currentUser = WorkflowCommonUtils.getCurrentUser();
        myDocsGW = new GridWidget(DOCUMENT.class);
        myDocsGW.setSizeFull();
        myDocsGW.setImmediate(true);
        myDocsGW.setResponsive(true);
        myDocsGW.setButtonVisible(IconToolbar.ADD_BUTTON , false);
        myDocsGW.setButtonVisible(IconToolbar.EDIT_BUTTON, false);
        myDocsGW.setButtonVisible(IconToolbar.DELETE_BUTTON, false);
        myDocsGW.setButtonVisible(IconToolbar.PREVIEW_BUTTON, false);

        List<ID> ids = new ArrayList<>();
        ids.add(WorkflowCommonUtils.getDocumentStatusByName(DOCUMENT_STATUS.IN_PROCESS).getId());
        ids.add(WorkflowCommonUtils.getDocumentStatusByName(DOCUMENT_STATUS.CREATED).getId());
        DBGridModel dbGridModel = (DBGridModel) myDocsGW.getWidgetModel();
        QueryModel myDocsQM = dbGridModel.getQueryModel();
        myDocsQM.addJoin(EJoin.INNER_JOIN, "id", DOCUMENT_SIGNER.class , "document");
        getContent().addComponent(myDocsGW);

    }


    public void sign() throws IOException , DocumentException , GeneralSecurityException{
        KeyStore keyStore = KeyStore.getInstance("PKCS12");
        keyStore.load(new FileInputStream(CERTIFICATE) , PASSWORD);
        String alias = keyStore.aliases().nextElement();
        PrivateKey privateKey = (PrivateKey) keyStore.getKey(alias, PASSWORD);
        Certificate[] certificateChain = keyStore.getCertificateChain(alias);

        PdfReader reader = new PdfReader(FORM);
        FileOutputStream os = new FileOutputStream(String.format(DEST,"kuanysh"));
        PdfStamper stamper = PdfStamper.createSignature(reader,os,'\0');

        PdfSignatureAppearance appearance = stamper.getSignatureAppearance();
        appearance.setCertificate(certificateChain[0]);

        ExternalSignature externalSignature = new PrivateKeySignature(privateKey, "SHA-256", null);
        ExternalDigest externalDigest = new BouncyCastleDigest();
        MakeSignature.signDetached(appearance, externalDigest , externalSignature, certificateChain , null ,null, null, 0 , MakeSignature.CryptoStandard.CMS);
    }

    @Override
    public void handleEntityEvent(EntityEvent entityEvent) {
        if(entityEvent.getSource().equals(myDocsGW)){
            if(entityEvent.getAction() == EntityEvent.SELECTED){
                Message.showError("asdasdasdasd");
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
