package kz.halyqsoft.univercity.modules.workflow.views.dialogs;

import com.vaadin.data.Property;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.server.FileDownloader;
import com.vaadin.server.StreamResource;
import com.vaadin.ui.*;
import kz.halyqsoft.univercity.entity.beans.univercity.DOCUMENT;
import kz.halyqsoft.univercity.entity.beans.univercity.DOCUMENT_SIGNER;
import kz.halyqsoft.univercity.entity.beans.univercity.DOCUMENT_SIGNER_STATUS;
import kz.halyqsoft.univercity.entity.beans.univercity.DOCUMENT_STATUS;
import kz.halyqsoft.univercity.modules.workflow.views.BaseView;
import kz.halyqsoft.univercity.modules.workflow.views.InOnAgreeView;
import kz.halyqsoft.univercity.utils.*;
import org.r3a.common.dblink.facade.CommonEntityFacadeBean;
import org.r3a.common.dblink.utils.SessionFacadeFactory;
import org.r3a.common.entity.query.QueryModel;
import org.r3a.common.entity.query.where.ECriteria;
import org.r3a.common.vaadin.widget.dialog.Message;
import org.r3a.common.vaadin.widget.grid.GridWidget;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class OpenPdfDialog extends WindowUtils{

    private VerticalLayout mainVL;
    private VerticalLayout refusedVL;

    public OpenPdfDialog(DOCUMENT document, BaseView baseView, Integer width, Integer height){
        super();

        HorizontalLayout mainHL = new HorizontalLayout();
        mainHL.setSizeFull();
        mainHL.setImmediate(true);
        mainHL.setDefaultComponentAlignment(Alignment.MIDDLE_CENTER);

        Button downloadRelatedDocs = new Button(getUILocaleUtil().getCaption("download"));

        Button sendToSign = new Button(getUILocaleUtil().getCaption("send.to.sign"));
        sendToSign.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {
                QueryModel<DOCUMENT_SIGNER> documentSignerQM = new QueryModel<>(DOCUMENT_SIGNER.class);
                documentSignerQM.addWhere("document" , ECriteria.EQUAL, document.getId());
                documentSignerQM.addWhereAnd("employee" , ECriteria.EQUAL, CommonUtils.getCurrentUser().getId());
                try{
                    DOCUMENT_SIGNER documentSigner = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookupSingle(documentSignerQM);
                    documentSigner.setDocumentSignerStatus(WorkflowCommonUtils.getDocumentSignerStatusByName(DOCUMENT_SIGNER_STATUS.IN_PROCESS));
                    documentSigner.setUpdated(new Date());
                    SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).merge(documentSigner);


                    if(baseView instanceof InOnAgreeView){
                        ((InOnAgreeView)baseView).getDbGridModel().setEntities(((InOnAgreeView) baseView).getList());
                    }

                    close();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
        document.setDocumentStatus(WorkflowCommonUtils.getDocumentStatusByName(DOCUMENT_STATUS.IN_PROCESS));
        try{
            SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).merge(document);
        }catch (Exception e){
            e.printStackTrace();
        }
        mainVL = new VerticalLayout();
        mainVL.setSizeFull();
        mainVL.setImmediate(true);
        mainVL.setDefaultComponentAlignment(Alignment.MIDDLE_CENTER);

        refusedVL = new VerticalLayout();
        refusedVL.setSizeFull();
        refusedVL.setImmediate(true);
        refusedVL.setDefaultComponentAlignment(Alignment.MIDDLE_CENTER);

        Embedded pdf = new Embedded(null, EmployeePdfCreator.createResourceStudent( document));

        pdf.setImmediate(true);
        pdf.setSizeFull();
        pdf.setMimeType("application/pdf");
        pdf.setType(2);
        pdf.setHeight(570,Unit.PIXELS);

        TextArea textArea = new TextArea( document.getCreatorEmployee().getFirstName() + " " + document.getCreatorEmployee().getLastName() + ": " + getUILocaleUtil().getCaption("message")  ) ;
        textArea.setValue(document.getMessage());
        textArea.setWordwrap(true);
        textArea.setImmediate(true);
        textArea.setReadOnly(true);
        textArea.setWidth(100, Unit.PERCENTAGE);
        mainVL.setHeight(100, Unit.PERCENTAGE);
        mainVL.addComponent(pdf);
        mainVL.addComponent(textArea);

        if(document.getRelatedDocumentFilePath()!=null){

            File file = new File(document.getRelatedDocumentFilePath());
            StreamResource sr = EmployeePdfCreator.getResource(document.getRelatedDocumentFilePath(),file);
            FileDownloader fileDownloader = new FileDownloader(sr);
            sr.setCacheTime(0);
            fileDownloader.extend(downloadRelatedDocs);


            mainHL.addComponent(downloadRelatedDocs);
        }

        QueryModel<DOCUMENT_SIGNER_STATUS> documentSignerStatusQM = new QueryModel<>(DOCUMENT_SIGNER_STATUS.class);
        documentSignerStatusQM.addWhereAnd("id", ECriteria.NOT_EQUAL , WorkflowCommonUtils.getDocumentSignerStatusByName(DOCUMENT_SIGNER_STATUS.IN_PROCESS).getId());
        documentSignerStatusQM.addWhereAnd("id", ECriteria.NOT_EQUAL , WorkflowCommonUtils.getDocumentSignerStatusByName(DOCUMENT_SIGNER_STATUS.CREATED).getId());
        documentSignerStatusQM.addWhereAnd("id", ECriteria.NOT_EQUAL , WorkflowCommonUtils.getDocumentSignerStatusByName(DOCUMENT_SIGNER_STATUS.SIGNED).getId());
        //FOR COMBOBOX

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
                refusedVL.removeAllComponents();

                if(dss!=null) {
                    sendToSign.setEnabled(false);
                    TextArea messageTA = new TextArea();
                    messageTA.setCaption(getUILocaleUtil().getCaption("reason"));
                    messageTA.setRequired(true);
                    messageTA.setImmediate(true);
                    messageTA.setWidth(100, Unit.PERCENTAGE);
                    Button saveBtn = CommonUtils.createSaveButton();
                    saveBtn.addClickListener(new Button.ClickListener() {
                        @Override
                        public void buttonClick(Button.ClickEvent clickEvent) {
                            if (FieldValidator.isNotEmpty(messageTA.getValue())) {
                                try {
                                    QueryModel<DOCUMENT_SIGNER> documentSignerQM = new QueryModel<>(DOCUMENT_SIGNER.class);
                                    documentSignerQM.addWhere("document", ECriteria.EQUAL, document.getId());
                                    documentSignerQM.addWhereAnd("employee", ECriteria.EQUAL, CommonUtils.getCurrentUser().getId());
                                    List<DOCUMENT_SIGNER> documentSigners = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(documentSignerQM);
                                    if (documentSigners.size() > 0) {
                                        documentSigners.get(0).setDocumentSignerStatus(dss);
                                        documentSigners.get(0).setMessage(messageTA.getValue());
                                        documentSigners.get(0).setUpdated(new Date());
                                        SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).merge(documentSigners.get(0));
                                        if (dss.getStatusName().equalsIgnoreCase(DOCUMENT_SIGNER_STATUS.FINALLY_REFUSED)) {
                                            document.setDocumentStatus(WorkflowCommonUtils.getDocumentStatusByName(DOCUMENT_STATUS.FINALLY_REFUSED));

                                        } else {
                                            document.setDocumentStatus(WorkflowCommonUtils.getDocumentStatusByName(DOCUMENT_STATUS.REFUSED_FOR_CORRECTION));
                                        }
                                        document.setUpdated(new Date());
                                        SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).merge(document);
                                        CommonUtils.showSavedNotification();
                                        close();
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            } else {
                                Message.showError(getUILocaleUtil().getMessage("fill.all.fields"));
                            }
                        }
                    });

                    refusedVL.addComponent(messageTA);
                    refusedVL.setComponentAlignment(messageTA, Alignment.MIDDLE_CENTER);

                    refusedVL.addComponent(saveBtn);
                    refusedVL.setComponentAlignment(saveBtn, Alignment.MIDDLE_CENTER);
                }else{
                    sendToSign.setEnabled(true);
                }
            }
        });

        mainHL.addComponentAsFirst(documentSignerStatusCB);
        mainHL.addComponent(sendToSign);

        mainVL.addComponent(mainHL);
        mainVL.addComponent(refusedVL);
        init(width,height);
    }

    @Override
    protected String createTitle() {
        return "PDF";
    }

    @Override
    protected void refresh() throws Exception {

    }

    @Override
    protected VerticalLayout getVerticalLayout() {
        return mainVL;
    }
}
