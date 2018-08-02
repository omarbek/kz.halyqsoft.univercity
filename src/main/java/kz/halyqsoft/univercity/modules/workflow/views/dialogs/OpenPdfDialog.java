package kz.halyqsoft.univercity.modules.workflow.views.dialogs;

import com.vaadin.ui.Embedded;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.VerticalLayout;
import kz.halyqsoft.univercity.entity.beans.univercity.DOCUMENT;
import kz.halyqsoft.univercity.entity.beans.univercity.DOCUMENT_STATUS;
import kz.halyqsoft.univercity.utils.EmployeePdfCreator;
import kz.halyqsoft.univercity.utils.WindowUtils;
import kz.halyqsoft.univercity.utils.WorkflowCommonUtils;
import org.r3a.common.dblink.facade.CommonEntityFacadeBean;
import org.r3a.common.dblink.utils.SessionFacadeFactory;

public class OpenPdfDialog extends WindowUtils{

    private VerticalLayout mainVL;

    public OpenPdfDialog(DOCUMENT document, Integer width, Integer height){
        super();
        document.setDocumentStatus(WorkflowCommonUtils.getDocumentStatusByName(DOCUMENT_STATUS.IN_PROCESS));
        try{
            SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).merge(document);
        }catch (Exception e){
            e.printStackTrace();
        }
        mainVL = new VerticalLayout();
        mainVL.setSizeFull();
        mainVL.setImmediate(true);
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
