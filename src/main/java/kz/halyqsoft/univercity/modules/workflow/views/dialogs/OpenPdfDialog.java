package kz.halyqsoft.univercity.modules.workflow.views.dialogs;

import com.vaadin.server.FileDownloader;
import com.vaadin.server.StreamResource;
import com.vaadin.ui.*;
import kz.halyqsoft.univercity.entity.beans.univercity.DOCUMENT;
import kz.halyqsoft.univercity.entity.beans.univercity.DOCUMENT_SIGNER;
import kz.halyqsoft.univercity.entity.beans.univercity.DOCUMENT_SIGNER_STATUS;
import kz.halyqsoft.univercity.entity.beans.univercity.DOCUMENT_STATUS;
import kz.halyqsoft.univercity.modules.workflow.views.BaseView;
import kz.halyqsoft.univercity.modules.workflow.views.InOnAgreeView;
import kz.halyqsoft.univercity.utils.CommonUtils;
import kz.halyqsoft.univercity.utils.EmployeePdfCreator;
import kz.halyqsoft.univercity.utils.WindowUtils;
import kz.halyqsoft.univercity.utils.WorkflowCommonUtils;
import org.r3a.common.dblink.facade.CommonEntityFacadeBean;
import org.r3a.common.dblink.utils.SessionFacadeFactory;
import org.r3a.common.entity.query.QueryModel;
import org.r3a.common.entity.query.where.ECriteria;
import org.r3a.common.vaadin.widget.grid.GridWidget;

import java.io.File;
import java.util.Date;

public class OpenPdfDialog extends WindowUtils{

    private VerticalLayout mainVL;

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

        mainHL.addComponent(sendToSign);

        mainVL.addComponent(mainHL);

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
